package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NULLABLE;
import static com.exedio.cope.instrument.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.meta.When;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

class JavaxNullabilityTest
{
	@SuppressWarnings("unused")
	private static Object none()
	{
		throw new AssertionFailedError();
	}

	@SuppressWarnings("unused")
	@Nonnull
	private static Object nonnull()
	{
		throw new AssertionFailedError();
	}

	@SuppressWarnings("unused")
	@Nullable
	private static Object nullable()
	{
		throw new AssertionFailedError();
	}

	@SuppressWarnings("unused")
	@Nonnull(when=When.NEVER)
	private static Object nonnullNever()
	{
		throw new AssertionFailedError();
	}

	@SuppressWarnings({"unused", "NullableProblems"})
	@Nullable
	@Nonnull
	private static Object nullableNonnull()
	{
		throw new AssertionFailedError();
	}

	@SuppressWarnings({"unused", "NullableProblems", "EmptyMethod"})
	private static void parameters(
			final Object none,
			@Nullable final Object nullable,
			@Nonnull final Object nonnull,
			@Nullable @Nonnull final Object nullableNonnull)
	{
		throw new AssertionFailedError();
	}

	@Test
	void className()
	{
		assertEquals(Nonnull.class.getName(), JavaxNullability.NONNULL_CLASS_NAME);
	}

	@Test
	void fromAnnotations() throws NoSuchMethodException
	{
		assertEquals(DEFAULT, nullability("none"));
		assertEquals(NONNULL, nullability("nonnull"));
		assertEquals(NULLABLE, nullability("nullable"));
		final Method javaxNonnullNever = method("nonnullNever");
		assertFails(
				() -> JavaxNullability.fromAnnotations(javaxNonnullNever),
				RuntimeException.class,
				"non-default setting of Nonnull.when() not supported");
		assertEquals(NONNULL, nullability("nullableNonnull")); // nonnull wins

		final Method parameters = method(
				"parameters",
				Object.class,
				Object.class,
				Object.class,
				Object.class);
		final Parameter[] parameterArray = parameters.getParameters();
		assertEquals(DEFAULT, nullability(parameterArray[0]));
		assertEquals(NULLABLE, nullability(parameterArray[1]));
		assertEquals(NONNULL, nullability(parameterArray[2]));
		assertEquals(NONNULL, nullability(parameterArray[3])); // nonnull wins
	}

	private static Method method(final String name, final Class<?>... parameterTypes)
			throws NoSuchMethodException
	{
		return JavaxNullabilityTest.class.getDeclaredMethod(name, parameterTypes);
	}

	private static Nullability nullability(final String name, final Class<?>... parameterTypes)
			throws NoSuchMethodException
	{
		return JavaxNullability.fromAnnotations(method(name, parameterTypes));
	}

	private static Nullability nullability(final Parameter parameter)
	{
		return JavaxNullability.fromAnnotations(parameter);
	}
}
