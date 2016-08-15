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

import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

import com.exedio.cope.util.Clock;
import com.exedio.cope.util.StrictFile;
import com.sun.tools.javac.api.JavacTool;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

final class Main
{
	static final String GENERATED_VALUE = "com.exedio.cope.instrument";

	static final int INITIAL_BUFFER_SIZE=16384;

	final void run(final Params params, final ArrayList<File> classpathFiles, final ArrayList<File> resourceFiles) throws HumanReadableException, IOException
	{
		final List<File> files = new ArrayList<>(params.sourceFiles);
		files.removeAll(params.ignoreFiles);
		if(files.isEmpty())
			throw new HumanReadableException("nothing to do.");
		if ( noFilesModifiedAfter(files, params.timestampFile, params.verbose) && noFilesModifiedAfter(resourceFiles, params.timestampFile, params.verbose) )
		{
			System.out.println("No files or resources modified.");
			return;
		}

		if(params.verify)
			System.out.println("Instrumenting in verify mode.");
		try
		{
			InstrumentContext.enter();

			final Charset charset = params.charset;
			final JavaRepository repository = new JavaRepository();

			this.verbose = params.verbose;
			instrumented = 0;
			skipped = 0;

			runJavac(params, classpathFiles, repository);

			repository.endBuildStage();

			for(final JavaFile javaFile: repository.getFiles())
			{
				for(final JavaClass javaClass : javaFile.getClasses())
				{
					final CopeType type = CopeType.getCopeType(javaClass);
					if(type!=null)
					{
						if(!type.isInterface())
						{
							for(final CopeFeature feature : type.getFeatures())
								feature.getInstance();
						}
					}
				}
			}

			for(final JavaFile javaFile: repository.getFiles())
			{
				final StringBuilder buffer = new StringBuilder(INITIAL_BUFFER_SIZE);
				final Generator generator = new Generator(javaFile, buffer, params);
				generator.write(charset);

				if(!javaFile.inputEqual(buffer, charset))
				{
					if(params.verify)
						throw new HumanReadableException(
								"Not yet instrumented " + javaFile.getSourceFileName() + lineSeparator() +
								"Instrumentor runs in verify mode, which is typically enabled while Continuous Integration." + lineSeparator() +
								"Probably you did commit a change causing another change in instrumented code," + lineSeparator() +
								"but you did not run the instrumentor.");
					logInstrumented(javaFile);
					javaFile.overwrite(buffer, charset);
				}
				else
				{
					logSkipped(javaFile);
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

	private static boolean noFilesModifiedAfter(final List<File> checkFiles, final File referenceFile, final boolean verbose)
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

	static void runJavac(final Params params, final List<File> classpathFiles, final JavaRepository repository) throws IOException, HumanReadableException
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
		try (final StandardJavaFileManager fileManager=compiler.getStandardFileManager(null, null, null))
		{
			final List<File> sortedSourceFiles=new ArrayList<>(params.sourceFiles);
			// We have to sort files to have a deterministic order - otherwise, resolving classes by
			// simple name is not deterministic.
			Collections.sort(sortedSourceFiles);
			final Iterable<? extends JavaFileObject> sources=fileManager.getJavaFileObjectsFromFiles(sortedSourceFiles);
			final List<String> optionList = new ArrayList<>();
			optionList.addAll(asList("-classpath", combineClasspath(getCurrentClasspath(), getConfiguredClasspath(classpathFiles))));
			optionList.add("-proc:only");
			optionList.add("-Xmaxwarns");
			optionList.add("10000");
			final JavaCompiler.CompilationTask task = compiler.getTask(null, null, null, optionList, null, sources);
			final InstrumentorProcessor instrumentorProcessor=new InstrumentorProcessor(repository, fileManager.getJavaFileObjectsFromFiles(params.ignoreFiles));
			task.setProcessors(singleton(instrumentorProcessor));
			task.call();
			if (!instrumentorProcessor.processHasBeenCalled)
			{
				// InstrumentorProcessor has not been invoked - this happens if parsing failed
				throw new HumanReadableException("fix compiler errors");
			}
		}
	}

	private static String combineClasspath(final String classpathA, final String classpathB)
	{
		if (classpathA.isEmpty())
		{
			return classpathB;
		}
		else if (classpathB.isEmpty())
		{
			return classpathA;
		}
		else
		{
			return classpathA+File.pathSeparatorChar+classpathB;
		}
	}

	private static String getConfiguredClasspath(final List<File> classpathFiles)
	{
		final StringBuilder result=new StringBuilder();
		boolean needSeparator=false;
		for (final File classpathFile: classpathFiles)
		{
			if (needSeparator)
			{
				result.append(File.pathSeparatorChar);
			}
			result.append(classpathFile.getAbsolutePath());
			needSeparator=true;
		}
		return result.toString();
	}

	private static String getCurrentClasspath()
	{
		// This is a hack:
		// We want to use the current classpath also in the javac task that's being started, so we
		// have to reconstruct a file-based classpath from a class loader.
		return toClasspath(com.exedio.cope.Item.class.getClassLoader());
	}

	private static String toClasspath(final ClassLoader cl)
	{
		if (cl instanceof URLClassLoader)
		{
			// this works for JUnit tests
			final URLClassLoader urlClassLoader=(URLClassLoader)cl;
			final StringBuilder result=new StringBuilder();
			for (int i=0; i < urlClassLoader.getURLs().length; i++)
			{
				if (i!=0)
				{
					result.append(File.pathSeparatorChar);
				}
				final URL url=urlClassLoader.getURLs()[i];
				result.append(url.toString());
			}
			return result.toString();
		}
		else
		{
			// this works for Ant
			final Pattern pattern = Pattern.compile("AntClassLoader\\[(.*)\\]");
			final String classLoaderString=cl.toString();
			final Matcher matcher = pattern.matcher(classLoaderString);
			if ( !matcher.matches() ) throw new RuntimeException("failed to construct file-based classpath from class loader; see Main.java getJavacClasspath(); class loader: "+classLoaderString);
			return matcher.group(1);
		}
	}

	boolean verbose;
	int skipped;
	int instrumented;

	private void logSkipped(final JavaFile file)
	{
		if(verbose)
			System.out.println("Skipped " + file.getSourceFileName());

		skipped++;
	}

	private void logInstrumented(final JavaFile file)
	{
		if(verbose)
			System.out.println("Instrumented " + file.getSourceFileName());

		instrumented++;
	}

}
