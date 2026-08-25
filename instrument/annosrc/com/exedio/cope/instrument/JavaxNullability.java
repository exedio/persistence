package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NULLABLE;

import java.lang.reflect.AnnotatedElement;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.meta.When;
import org.jspecify.annotations.NullMarked;

@NullMarked
final class JavaxNullability
{
	static final String NONNULL_CLASS_NAME = "javax.annotation.Nonnull";

	private static @org.jspecify.annotations.Nullable Boolean classesAvailable = null;

	static boolean classesAreAvailable()
	{
		if (classesAvailable==null)
		{
			try
			{
				Class.forName(NONNULL_CLASS_NAME);
				classesAvailable = true;
			}
			catch (final ClassNotFoundException e)
			{
				classesAvailable = false;
			}
		}
		return classesAvailable;
	}

	static Nullability fromAnnotations(final AnnotatedElement element)
	{
		if (!classesAreAvailable())
			return DEFAULT;

		final Nonnull nonnullAnnotation = element.getAnnotation(Nonnull.class);
		if (nonnullAnnotation!=null)
		{
			if (nonnullAnnotation.when()==When.ALWAYS)
				return NONNULL;
			throw new RuntimeException("non-default setting of Nonnull.when() not supported");
		}
		if (element.isAnnotationPresent(Nullable.class))
			return NULLABLE;
		return DEFAULT;
	}

	private JavaxNullability()
	{
		// prevent instantiation
	}
}
