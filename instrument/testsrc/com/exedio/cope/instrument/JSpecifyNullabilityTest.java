package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

class JSpecifyNullabilityTest
{
	@SuppressWarnings("unused")
	private static Object none()
	{
		throw new AssertionFailedError();
	}

	@SuppressWarnings("unused")
	private static @NonNull Object nonnull()
	{
		throw new AssertionFailedError();
	}

	@SuppressWarnings("unused")
	private static @Nullable Object nullable()
	{
		throw new AssertionFailedError();
	}

	@SuppressWarnings({"unused", "NullableProblems"})
	private static @Nullable @NonNull Object nullableNonnull()
	{
		throw new AssertionFailedError();
	}

	@SuppressWarnings({"unused", "NullableProblems", "EmptyMethod"})
	private static void parameters(
			final Object none,
			final @Nullable Object nullable,
			final @NonNull Object nonnull,
			final @Nullable @NonNull Object nullableNonnull)
	{
		throw new AssertionFailedError();
	}

	@Test
	void className()
	{
		assertEquals("org.jspecify.annotations.Nullable", JSpecifyNullability.NULLABLE_CLASS_NAME);
		assertEquals("org.jspecify.annotations.NonNull", JSpecifyNullability.NONNULL_CLASS_NAME);
	}

	@Test
	void fromAnnotations() throws NoSuchMethodException
	{
		assertEquals(DEFAULT, nullability(JSpecifyNullabilityTest.class, "none"));
		assertEquals(NONNULL, nullability(JSpecifyNullabilityTest.class, "nonnull"));
		assertEquals(NULLABLE, nullability(JSpecifyNullabilityTest.class, "nullable"));
		assertEquals(NONNULL, nullability(JSpecifyNullabilityTest.class, "nullableNonnull")); // nonnull wins

		final Method parameters = JSpecifyNullabilityTest.class.getDeclaredMethod(
				"parameters",
				Object.class,
				Object.class,
				Object.class,
				Object.class);
		final java.lang.reflect.Parameter[] parameterArray = parameters.getParameters();
		assertEquals(DEFAULT, nullability(parameterArray[0]));
		assertEquals(NULLABLE, nullability(parameterArray[1]));
		assertEquals(NONNULL, nullability(parameterArray[2]));
		assertEquals(NONNULL, nullability(parameterArray[3])); // nonnull wins
	}

	private static Nullability nullability(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes)
			throws NoSuchMethodException
	{
		final Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
		return JSpecifyNullability.fromAnnotations(method.getAnnotatedReturnType());
	}

	private static Nullability nullability(final java.lang.reflect.Parameter parameter)
	{
		return JSpecifyNullability.fromAnnotations(parameter.getAnnotatedType());
	}
}
