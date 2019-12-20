package com.exedio.cope.instrument;

import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import com.exedio.cope.util.Clock;
import com.exedio.cope.util.StrictFile;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("*")
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
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv)
	{
		if (roundEnv.processingOver())
			return false;
		try
		{
			final ClassLoader interimClassLoader = interimProcessor.getInterimClassLoader();

			for(final JavaFile javaFile : repository.getFiles())
			{
				for(final JavaClass javaClass : javaFile.getClasses())
				{
					final LocalCopeType type = LocalCopeType.getCopeType(javaClass);
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
			final Set<Method> disabledWraps = findMethods(interimClassLoader, params.getDisabledWraps(), "<disableWrap>", singletonList(Wrap.class));
			for(final JavaFile javaFile : repository.getFiles())
			{
				final StringBuilder buffer = new StringBuilder(INITIAL_BUFFER_SIZE);
				final Generator generator = new Generator(javaFile, buffer, params, generateDeprecateds, disabledWraps);
				generator.write(params.charset);

				if(! javaFile.inputEqual(buffer, params.charset))
				{
					if(params.verify)
						throw new HumanReadableException(
								"Not yet instrumented " + javaFile.getSourceFileName() + lineSeparator() +
								"Instrumentor runs in verify mode, which is typically enabled while Continuous Integration." + lineSeparator() +
								"Probably you did commit a change causing another change in instrumented code," + lineSeparator() +
								"but you did not run the instrumentor.");
					logInstrumented(javaFile);
					javaFile.overwrite(buffer, params.charset);
				}
				else
				{
					logSkipped(javaFile);
				}
			}

			int invalidWraps = 0;
			for(final JavaFile javaFile : repository.getFiles())
			{
				for(final JavaClass clazz : javaFile.classes)
				{
					for(final JavaField field : clazz.getFields())
					{
						if(field.hasInvalidWrapperUsages())
						{
							invalidWraps++;
						}
					}
				}
			}
			if(invalidWraps > 0)
			{
				throw new HumanReadableException("fix invalid wraps at " + invalidWraps + " field(s)");
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
		return false;
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
