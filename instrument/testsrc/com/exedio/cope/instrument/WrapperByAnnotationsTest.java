package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NULLABLE;
import static com.exedio.cope.instrument.WrapperByAnnotations.nullabilityFromAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

class WrapperByAnnotationsTest
{
	@SuppressWarnings("unused")
	private static Object none()
	{
		return new Object();
	}

	@SuppressWarnings("unused")
	@javax.annotation.Nullable
	private static Object javaxNullable()
	{
		return null;
	}

	@SuppressWarnings("unused")
	private static @NonNull Object jspecifyNonnull()
	{
		return new Object();
	}

	@SuppressWarnings({"unused", "NullableProblems"})
	@javax.annotation.Nullable
	private static @NonNull Object javaxNullableJspecifyNonnull()
	{
		return null;
	}

	@Test
	void testNullabilityFromAnnotations() throws NoSuchMethodException
	{
		assertEquals(DEFAULT, nullabilityFromAnnotations(method("none"), method("none").getAnnotatedReturnType()));
		assertEquals(NULLABLE, nullabilityFromAnnotations(method("javaxNullable"), method("none").getAnnotatedReturnType()));
		final Method jspecifyNonnull = method("jspecifyNonnull");
		assertEquals(NONNULL, nullabilityFromAnnotations(jspecifyNonnull, jspecifyNonnull.getAnnotatedReturnType()));
		final Method javaxNullableJspecifyNonnull = method("javaxNullableJspecifyNonnull"); // jspecify wins
		assertEquals(
				NONNULL,
				nullabilityFromAnnotations(javaxNullableJspecifyNonnull, javaxNullableJspecifyNonnull.getAnnotatedReturnType()));
	}

	private static Method method(final String name, final Class<?>... parameterTypes) throws NoSuchMethodException
	{
		return WrapperByAnnotationsTest.class.getDeclaredMethod(name, parameterTypes);
	}
}
