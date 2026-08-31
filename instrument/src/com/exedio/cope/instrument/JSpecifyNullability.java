package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NULLABLE;

import java.lang.reflect.AnnotatedType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
final class JSpecifyNullability
{
	static final String NULLABLE_CLASS_NAME = "org.jspecify.annotations.Nullable";
	static final String NONNULL_CLASS_NAME = "org.jspecify.annotations.NonNull";

	private static @Nullable Boolean classesAvailable = null;

	private static boolean classesAreAvailable()
	{
		if (classesAvailable==null)
		{
			try
			{
				Class.forName(NULLABLE_CLASS_NAME);
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

	static Nullability fromAnnotations(final AnnotatedType type)
	{
		if (!classesAreAvailable())
			return DEFAULT;
		if (type.isAnnotationPresent(NonNull.class))
			return NONNULL;
		if (type.isAnnotationPresent(Nullable.class))
			return NULLABLE;
		return DEFAULT;
	}

	private JSpecifyNullability()
	{
		// prevent instantiation
	}
}
