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

import com.exedio.cope.util.Clock;
import com.exedio.cope.util.StrictFile;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.StandardJavaFileManager;

final class Main
{
	static final String GENERATED_VALUE = "com.exedio.cope.instrument";

	static final int INITIAL_BUFFER_SIZE=16384;

	void run(final Params params) throws HumanReadableException, IOException
	{
		final List<File> files = params.getJavaSourceFilesExcludingIgnored();
		if(files.isEmpty())
			throw new HumanReadableException("nothing to do.");
		if ( noFilesModifiedAfter(files, params.timestampFile, params.verbose)
			&& noFilesModifiedAfter(params.resources, params.timestampFile, params.verbose)
			&& noFilesModifiedAfter(params.classpath, params.timestampFile, params.verbose) )
		{
			System.out.println("No files or resources modified.");
			return;
		}

		if(params.verify)
			System.out.println("Instrumenting in verify mode.");
		if (params.configByTags==ConfigurationByJavadocTags.convertToAnnotations)
			throw new HumanReadableException("configByTags set to convertToAnnotations - stopping");

		try
		{
			InstrumentContext.enter();

			final Charset charset = params.charset;
			final JavaRepository repository = new JavaRepository( createClassLoader(params.classpath) );

			this.verbose = params.verbose;
			instrumented = 0;
			skipped = 0;

			runJavac(params, repository);

			repository.endBuildStage();

			for(final JavaFile javaFile: repository.getFiles())
			{
				for(final JavaClass javaClass : javaFile.getClasses())
				{
					final LocalCopeType type = LocalCopeType.getCopeType(javaClass);
					if(type!=null)
					{
						if(!type.isInterface())
						{
							for(final LocalCopeFeature feature : type.getFeatures())
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

			int invalidWraps=0;
			for(final JavaFile javaFile: repository.getFiles())
			{
				for (final JavaClass clazz: javaFile.classes)
				{
					for (final JavaField field: clazz.getFields())
					{
						if (field.hasInvalidWrapperUsages())
						{
							invalidWraps++;
						}
					}
				}
			}
			if (invalidWraps>0)
			{
				throw new HumanReadableException("fix invalid wraps at "+invalidWraps+" field(s)");
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

	private ClassLoader createClassLoader(final Iterable<File> classpathFiles)
	{
		final List<URL> urls=new ArrayList<>();
		for (final File classpathFile: classpathFiles)
		{
			try
			{
				urls.add(classpathFile.toURI().toURL());
			}
			catch (final MalformedURLException e)
			{
				throw new RuntimeException(e);
			}
		}
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
	}

	@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // OK: checks isDirectory before calling listFiles
	private static boolean noFilesModifiedAfter(final Iterable<File> checkFiles, final File referenceFile, final boolean verbose)
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
				if ( file.isDirectory() )
				{
					if ( !noFilesModifiedAfter(Arrays.asList(file.listFiles()), referenceFile, verbose) )
					{
						return false;
					}
				}
			}
			return true;
		}
	}

	private static void runJavac(final Params params, final JavaRepository repository) throws IOException, HumanReadableException
	{
		new JavacRunner<InstrumentorProcessor>()
		{
			@Override
			InstrumentorProcessor createProcessor(final StandardJavaFileManager fileManager)
			{
				return new InstrumentorProcessor(params, repository, fileManager.getJavaFileObjectsFromFiles(params.ignoreFiles));
			}

			@Override
			void validateProcessor(final InstrumentorProcessor instrumentorProcessor) throws HumanReadableException
			{
				if (!instrumentorProcessor.processHasBeenCalled)
				{
					// InstrumentorProcessor has not been invoked - this happens if parsing failed
					throw new HumanReadableException("fix compiler errors");
				}
				if (instrumentorProcessor.foundJavadocControlTags && params.configByTags==ConfigurationByJavadocTags.error)
				{
					throw new HumanReadableException("found javadoc instrumentor control tags");
				}
			}

		}.run(params);
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
