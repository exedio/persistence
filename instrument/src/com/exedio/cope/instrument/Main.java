/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.instrument;

import static com.exedio.cope.util.StrictFile.delete;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

import com.exedio.cope.util.Clock;
import com.exedio.cope.util.StrictFile;
import com.sun.tools.javac.api.JavacTool;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

final class Main
{
	final void run(final ArrayList<File> files, final Params params, final ArrayList<File> resourceFiles) throws HumanReadableException, ParserException, IOException
	{
		if(files.isEmpty())
			throw new HumanReadableException("nothing to do.");
		if ( noFilesModifiedAfter(files, params.timestampFile, params.verbose) && noFilesModifiedAfter(resourceFiles, params.timestampFile, params.verbose) )
		{
			System.out.println("No files or resources modified.");
			return;
		}

		if ( params.deinstrument )
		{
			final long start = System.currentTimeMillis();
			runJavac(files);
			System.out.println("runJavac "+(System.currentTimeMillis()-start)+" ms");
			return;
		}

		if(params.verify)
			System.out.println("Instrumenting in verify mode.");
		try
		{
			InstrumentContext.enter();

			final Charset charset = params.charset;
			final JavaRepository repository = new JavaRepository();
			final ArrayList<Parser> parsers = new ArrayList<>(files.size());

			this.verbose = params.verbose;
			instrumented = 0;
			skipped = 0;
			for(final File file : files)
			{
				if(!file.exists())
					throw new RuntimeException("error: input file " + file.getAbsolutePath() + " does not exist.");
				if(!file.isFile())
					throw new RuntimeException("error: input file " + file.getAbsolutePath() + " is not a regular file.");

				final JavaFile javaFile = new JavaFile(repository, file);
				final Parser parser = new Parser(new Lexer(file, charset, javaFile), new Instrumentor(), javaFile);
				parser.parseFile();
				parsers.add(parser);
			}

			repository.endBuildStage();

			for(final Parser parser : parsers)
			{
				final JavaFile javaFile = parser.javaFile;
				for(final JavaClass javaClass : javaFile.getClasses())
				{
					final CopeType type = CopeType.getCopeType(javaClass);
					if(type!=null)
					{
						if(!type.isInterface())
						{
							//System.out.println("onClassEnd("+jc.getName()+") writing");
							for(final CopeFeature feature : type.getFeatures())
								feature.getInstance();
						}
					}
				}
			}

			final Iterator<Parser> parsersIter = parsers.iterator();
			for(final File file : files)
			{
				final Parser parser = parsersIter.next();

				final StringBuilder baos = new StringBuilder((int)file.length() + 100);
				final Generator generator = new Generator(parser.javaFile, baos, params);
				generator.write();

				if(!parser.lexer.inputEqual(baos))
				{
					if(params.verify)
						throw new HumanReadableException(
								"Not yet instrumented " + file.getAbsolutePath() + lineSeparator() +
								"Instrumentor runs in verify mode, which is typically enabled while Continuous Integration." + lineSeparator() +
								"Probably you did commit a change causing another change in instrumented code," + lineSeparator() +
								"but you did not run the intrumentor.");
					logInstrumented(file);
					delete(file);
					final CharsetEncoder decoder = charset.newEncoder();
					final ByteBuffer out = decoder.encode(CharBuffer.wrap(baos));
					try(FileOutputStream o = new FileOutputStream(file))
					{
						o.getChannel().write(out);
					}
				}
				else
				{
					logSkipped(file);
				}
			}

			if ( params.timestampFile!=null )
			{
				if ( params.timestampFile.exists() )
				{
					StrictFile.setLastModified(params.timestampFile, Clock.currentTimeMillis());
				}
				else
				{
					StrictFile.createNewFile(params.timestampFile);
				}
			}
		}
		finally
		{
			InstrumentContext.leave();
		}

		if(verbose || instrumented>0)
			System.out.println("Instrumented " + instrumented + ' ' + (instrumented==1 ? "file" : "files") + ", skipped " + skipped + " in " + files.iterator().next().getParentFile().getAbsolutePath());
	}

	private static boolean noFilesModifiedAfter(final ArrayList<File> checkFiles, final File referenceFile, final boolean verbose)
	{
		if ( referenceFile==null || !referenceFile.exists() )
		{
			if ( verbose )
			{
				System.out.println("No timestamp file, instrumentation required.");
			}
			return false;
		}
		else
		{
			final long referenceLastModified = referenceFile.lastModified();
			for (final File file: checkFiles)
			{
				if ( file.lastModified()>=referenceLastModified )
				{
					if ( verbose )
					{
						System.out.println("File "+file+" changed after timestamp file, instrumentation required.");
					}
					return false;
				}
			}
			return true;
		}
	}

	private void runJavac(final ArrayList<File> files)
	{
		// "JavacTool.create()" is not part of the "exported" API
		// (not annotated with https://docs.oracle.com/javase/8/docs/jdk/api/javac/tree/jdk/Exported.html).
		// The more stable alternative would be calling "ToolProvider.getSystemJavaCompiler()", but that causes
		// class path issues with when run as an ant task.
		final JavaCompiler compiler=JavacTool.create();
		if ( compiler==null )
		{
			throw new NullPointerException("no system java compiler found - please make sure your \"java\" is from a JDK, not a JRE");
		}
		final StandardJavaFileManager fileManager=compiler.getStandardFileManager(null, null, null);
		final Iterable<? extends JavaFileObject> sources=fileManager.getJavaFileObjectsFromFiles(files);
		final List<String> optionList = new ArrayList<>();
		optionList.addAll(asList("-classpath", toClasspath(com.exedio.cope.Item.class.getClassLoader())));
		optionList.add("-proc:only");
		final JavaCompiler.CompilationTask task = compiler.getTask(new StringWriter(), null, null, optionList, null, sources);
		task.setProcessors(singleton(new InstrumentorProcessor()));
		task.call();
	}

	private String toClasspath(ClassLoader cl)
	{
		final Pattern pattern = Pattern.compile("AntClassLoader\\[(.*)\\]");
		final Matcher matcher = pattern.matcher(cl.toString());
		if ( !matcher.matches() ) throw new RuntimeException();
		return matcher.group(1);
	}

	boolean verbose;
	int skipped;
	int instrumented;

	private void logSkipped(final File file)
	{
		if(verbose)
			System.out.println("Skipped " + file);

		skipped++;
	}

	private void logInstrumented(final File file)
	{
		if(verbose)
			System.out.println("Instrumented " + file);

		instrumented++;
	}

}
