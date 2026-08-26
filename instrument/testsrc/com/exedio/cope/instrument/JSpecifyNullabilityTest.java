package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
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
	@NullMarked
	private static class MarkedClass
	{
		private static Object classMarkedMethod(final Object parameter)
		{
			return parameter;
		}

		private static @Nullable Object explicitlyNullableMethod()
		{
			return null;
		}

		@SuppressWarnings("NullableProblems")
		private static @NonNull Object explicitlyNonnullMethod()
		{
			return new Object();
		}

		private static int primitiveMethod()
		{
			return 0;
		}

		@NullUnmarked
		private static Object classUnmarkedMethod()
		{
			return new Object();
		}
	}

	private static class UnmarkedClass
	{
		@SuppressWarnings("unused")
		@NullMarked
		private static Object methodMarked()
		{
			return new Object();
		}
	}

	@SuppressWarnings({"unused", "EmptyClass"})
	@NullMarked
	private static class MarkedOuterClass
	{
		private static class NestedClass
		{
			private static Object nestedClassMarked()
			{
				return new Object();
			}
		}

		@NullUnmarked
		private static class UnmarkedNestedClass
		{
			private static Object nestedClassUnmarked()
			{
				return new Object();
			}
		}
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
		assertEquals(NONNULL, nullability(MarkedClass.class, "classMarkedMethod", Object.class));
		final Method classMarkedMethod = MarkedClass.class.getDeclaredMethod("classMarkedMethod", Object.class);
		assertEquals(NONNULL, nullability(classMarkedMethod.getParameters()[0]));
		assertEquals(NULLABLE, nullability(MarkedClass.class, "explicitlyNullableMethod"));
		assertEquals(NONNULL, nullability(MarkedClass.class, "explicitlyNonnullMethod"));
		assertEquals(DEFAULT, nullability(MarkedClass.class, "primitiveMethod"));
		assertEquals(DEFAULT, nullability(MarkedClass.class, "classUnmarkedMethod"));
		assertEquals(NONNULL, nullability(UnmarkedClass.class, "methodMarked"));
		assertEquals(NONNULL, nullability(MarkedOuterClass.NestedClass.class, "nestedClassMarked"));
		assertEquals(DEFAULT, nullability(MarkedOuterClass.UnmarkedNestedClass.class, "nestedClassUnmarked"));

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
		return JSpecifyNullability.fromAnnotations(method, method.getAnnotatedReturnType());
	}

	private static Nullability nullability(final java.lang.reflect.Parameter parameter)
	{
		return JSpecifyNullability.fromAnnotations(parameter, parameter.getAnnotatedType());
	}
}
