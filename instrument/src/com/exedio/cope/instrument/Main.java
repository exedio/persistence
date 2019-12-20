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
import static java.util.Collections.singletonList;

import com.exedio.cope.util.Clock;
import com.exedio.cope.util.StrictFile;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class Main
{
	static final String GENERATED_VALUE = "com.exedio.cope.instrument";

	static final int INITIAL_BUFFER_SIZE=16384;

	void run(final Params params) throws HumanReadableException, IOException
	{
		final List<File> files = params.getJavaSourceFilesExcludingIgnored();
		if(files.isEmpty())
			throw new HumanReadableException("nothing to do.");
		if ( noFilesModifiedAfter(files, params.getTimestampFile(), params.verbose)
			&& noFilesModifiedAfter(params.resources, params.getTimestampFile(), params.verbose)
			&& noFilesModifiedAfter(params.classpath, params.getTimestampFile(), params.verbose) )
		{
			System.out.println("No files or resources modified.");
			return;
		}

		if(params.verify)
			System.out.println("Instrumenting in verify mode.");

		final Charset charset = params.charset;
		final JavaRepository repository = new JavaRepository();

		this.verbose = params.verbose;
		instrumented = 0;
		skipped = 0;

		final ClassLoader interimClassLoader = runJavac(params, repository);

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

		final Set<Method> generateDeprecateds = findMethods(interimClassLoader, params.getGenerateDeprecateds(), "<generateDeprecated>", asList(Deprecated.class, Wrap.class));
		final Set<Method> disabledWraps = findMethods(interimClassLoader, params.getDisabledWraps(), "<disableWrap>", singletonList(Wrap.class));
		for(final JavaFile javaFile: repository.getFiles())
		{
			final StringBuilder buffer = new StringBuilder(INITIAL_BUFFER_SIZE);
			final Generator generator = new Generator(javaFile, buffer, params, generateDeprecateds, disabledWraps);
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

		if ( params.getTimestampFile().exists() )
		{
			StrictFile.setLastModified(params.getTimestampFile(), Clock.currentTimeMillis());
		}
		else
		{
			if (!params.getTimestampFile().getParentFile().isDirectory())
				StrictFile.mkdirs(params.getTimestampFile().getParentFile());
			StrictFile.createNewFile(params.getTimestampFile());
		}

		if(verbose || instrumented>0)
			System.out.println("Instrumented " + instrumented + ' ' + (instrumented==1 ? "file" : "files") + ", skipped " + skipped + " in " + files.iterator().next().getParentFile().getAbsolutePath());
	}

	@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // OK: checks isDirectory before calling listFiles
	private static boolean noFilesModifiedAfter(final Iterable<File> checkFiles, final File referenceFile, final boolean verbose)
	{
		if ( !referenceFile.exists() )
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
					//noinspection ConstantConditions OK: checks isDirectory before calling listFiles
					if ( !noFilesModifiedAfter(asList(file.listFiles()), referenceFile, verbose) )
					{
						return false;
					}
				}
			}
			return true;
		}
	}

	private static ClassLoader runJavac(final Params params, final JavaRepository repository) throws IOException, HumanReadableException
	{
		final InterimProcessor interimProcessor = new InterimProcessor(params);
		final FillRepositoryProcessor fillRepositoryProcessor = new FillRepositoryProcessor(repository, interimProcessor);
		new JavacRunner(interimProcessor, fillRepositoryProcessor).run(params);
		return interimProcessor.getInterimClassLoader();
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

	Set<Method> findMethods(final ClassLoader classLoader, final List<Params.Method> methodConfigurations, final String tagForErrors, final List<Class<? extends Annotation>> requiredAnnotations) throws HumanReadableException
	{
		final Set<Method> result = new HashSet<>();
		for (final Params.Method methodConfiguration: methodConfigurations)
		{
			try
			{
				final Class<?>[] methodParams = new Class<?>[methodConfiguration.parameterTypes.length];
				for (int i=0; i<methodParams.length; i++)
				{
					final String parameterType = methodConfiguration.parameterTypes[i];
					try
					{
						methodParams[i] = ClassHelper.isPrimitive(parameterType) ?
							ClassHelper.getClass(parameterType) :
							Class.forName(parameterType, false, classLoader);
					}
					catch (final ClassNotFoundException ignored)
					{
						throw new HumanReadableException("can't resolve parameter type '"+parameterType+"' for "+tagForErrors+": "+methodConfiguration);
					}
				}
				final Class<?> clazz;
				try
				{
					clazz = Class.forName(methodConfiguration.className, false, classLoader);
				}
				catch (final ClassNotFoundException ignored)
				{
					throw new HumanReadableException("class not found for "+tagForErrors+": "+methodConfiguration.className);
				}
				final Method method = clazz.getMethod(methodConfiguration.methodName, methodParams);
				for (final Class<? extends Annotation> requiredAnnotation : requiredAnnotations)
				{
					if (method.getAnnotation(requiredAnnotation)==null)
						throw new HumanReadableException("method listed in "+tagForErrors+" is not annotated as @"+requiredAnnotation.getSimpleName()+": "+methodConfiguration);
				}
				result.add(method);
			}
			catch (final NoSuchMethodException ignored)
			{
				throw new HumanReadableException("method not found for "+tagForErrors+": "+methodConfiguration);
			}
		}
		return result;
	}
}
