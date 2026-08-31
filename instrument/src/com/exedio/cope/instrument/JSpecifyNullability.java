package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NULLABLE;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
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

	static Nullability fromAnnotations(final AnnotatedElement element, final AnnotatedType type)
	{
		if (!classesAreAvailable())
			return DEFAULT;
		if (isPrimitive(type))
			return DEFAULT;
		if (type.isAnnotationPresent(NonNull.class))
			return NONNULL;
		if (type.isAnnotationPresent(Nullable.class))
			return NULLABLE;
		if (isNullMarked(element))
			return NONNULL;
		return DEFAULT;
	}

	private static boolean isPrimitive(final AnnotatedType type)
	{
		return type.getType() instanceof final Class<?> c && c.isPrimitive();
	}

	private static boolean isNullMarked(final AnnotatedElement element)
	{
		if (element instanceof final Parameter parameter)
			return isNullMarked(parameter);
		if (element instanceof final Executable executable)
			return isNullMarked(executable);

		throw new RuntimeException(element.getClass().getName());
	}

	private static boolean isNullMarked(final Parameter parameter)
	{
		return isNullMarked(parameter.getDeclaringExecutable());
	}

	private static boolean isNullMarked(final Executable executable)
	{
		final Boolean marked = getNullMarked(executable);
		return marked != null ? marked : isNullMarked(executable.getDeclaringClass());
	}

	private static boolean isNullMarked(final Class<?> clazz)
	{
		final Boolean marked = getNullMarked(clazz);
		if (marked!=null)
			return marked;

		for (Class<?> enclosingClass = clazz.getEnclosingClass(); enclosingClass!=null; enclosingClass = enclosingClass.getEnclosingClass())
		{
			final Boolean enclosingMarked = getNullMarked(enclosingClass);
			if (enclosingMarked!=null)
				return enclosingMarked;
		}

		final Boolean packageMarked = getNullMarked(clazz.getPackage());
		return packageMarked!=null ? packageMarked : isNullMarked(clazz.getModule());
	}

	private static boolean isNullMarked(final Module module)
	{
		return Boolean.TRUE.equals(getNullMarked(module));
	}

	private static @Nullable Boolean getNullMarked(final AnnotatedElement element)
	{
		if (element.isAnnotationPresent(NullUnmarked.class))
			return false;
		if (element.isAnnotationPresent(NullMarked.class))
			return true;
		return null;
	}

	private JSpecifyNullability()
	{
		// prevent instantiation
	}
}
