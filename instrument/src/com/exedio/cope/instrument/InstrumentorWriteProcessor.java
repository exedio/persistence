package com.exedio.cope.instrument;

import static java.util.Arrays.asList;

import com.exedio.cope.util.Clock;
import com.exedio.cope.util.StrictFile;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.tools.Diagnostic;

final class InstrumentorWriteProcessor extends JavacProcessor
{
	static final int INITIAL_BUFFER_SIZE=16384;

	private final Params params;
	private final JavaRepository repository;
	private final InterimProcessor interimProcessor;

	int skipped = 0;
	int instrumented = 0;

	InstrumentorWriteProcessor(final Params params,
										final JavaRepository repository,
										final InterimProcessor interimProcessor)
	{
		this.params = params;
		this.repository = repository;
		this.interimProcessor = interimProcessor;
	}

	@Override
	public void processInternal(final RoundEnvironment roundEnv)
	{
		try
		{
			final ClassLoader interimClassLoader = interimProcessor.getInterimClassLoader();

			for(final JavaFile javaFile : repository.getFiles())
			{
				for(final JavaClass javaClass : javaFile.getClasses())
				{
					final LocalCopeType type = repository.getCopeType(javaClass);
					if(type != null)
					{
						if(! type.isInterface())
						{
							for(final LocalCopeFeature feature : type.getFeatures())
								feature.getInstance();
						}
					}
				}
			}

			final Set<Method> generateDeprecateds = findMethods(interimClassLoader, params.getGenerateDeprecateds(), "<generateDeprecated>", asList(Deprecated.class, Wrap.class));
			final Set<Method> disabledWraps = findMethods(interimClassLoader, params.getDisabledWraps(), "<disableWrap>", List.of(Wrap.class));
			boolean foundNotYetInstrumented = false;
			for(final JavaFile javaFile : repository.getFiles())
			{
				final StringBuilder buffer = new StringBuilder(INITIAL_BUFFER_SIZE);
				final Generator generator = new Generator(javaFile, buffer, params, generateDeprecateds, disabledWraps);
				generator.write();

				if(! javaFile.inputEqual(buffer, params.charset))
				{
					if(params.verify)
					{
						foundNotYetInstrumented = true;
						processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "not yet instrumented", javaFile.getRootClass().sourceLocation);
					}
					else
					{
						logInstrumented(javaFile);
						javaFile.overwrite(buffer, params.charset);
					}
				}
				else
				{
					logSkipped(javaFile);
				}
			}
			if (foundNotYetInstrumented)
			{
				System.out.println(
						"Instrumentor runs in verify mode (which is typically enabled while Continuous Integration), but found non-instrumented files. " +
						"Probably you committed a change causing an other change in instrumented code, but you did not run the instrumentor."
				);
				return;
			}

			for(final JavaFile javaFile : repository.getFiles())
			{
				for(final JavaClass clazz : javaFile.classes)
				{
					for(final JavaField field : clazz.getFields())
					{
						field.reportInvalidWrapperUsages(processingEnv.getMessager());
					}
				}
			}

			if(params.getTimestampFile().exists())
			{
				StrictFile.setLastModified(params.getTimestampFile(), Clock.currentTimeMillis());
			}
			else
			{
				if(! params.getTimestampFile().getParentFile().isDirectory())
					StrictFile.mkdirs(params.getTimestampFile().getParentFile());
				StrictFile.createNewFile(params.getTimestampFile());
			}

			if(params.verbose || instrumented>0)
				System.out.println("Instrumented " + instrumented + ' ' + (instrumented==1 ? "file" : "files") + ", skipped " + skipped + " in " + params.getJavaSourceFilesExcludingIgnored().iterator().next().getParentFile().getAbsolutePath());
		}
		catch (final HumanReadableException|IOException e)
		{
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
		}
	}

	static Set<Method> findMethods(final ClassLoader classLoader, final List<Params.Method> methodConfigurations, final String tagForErrors, final List<Class<? extends Annotation>> requiredAnnotations) throws HumanReadableException
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

	private void logSkipped(final JavaFile file)
	{
		if(params.verbose)
			System.out.println("Skipped " + file.getSourceFileName());

		skipped++;
	}

	private void logInstrumented(final JavaFile file)
	{
		if(params.verbose)
			System.out.println("Instrumented " + file.getSourceFileName());

		instrumented++;
	}
}
