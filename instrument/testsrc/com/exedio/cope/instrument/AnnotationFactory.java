package com.exedio.cope.instrument;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.meta.When;

final class AnnotationFactory
{
	private static Object noneMethod()
	{
		return new Object();
	}
	static Method none() throws NoSuchMethodException
	{
		return AnnotationFactory.class.getDeclaredMethod("noneMethod");
	}

	@Nonnull
	private static Object javaxNonnullMethod()
	{
		return new Object();
	}
	static Method javaxNonnull() throws NoSuchMethodException
	{
		return AnnotationFactory.class.getDeclaredMethod("javaxNonnullMethod");
	}

	@Nullable
	private static Object javaxNullableMethod()
	{
		return null;
	}
	static Method javaxNullable() throws NoSuchMethodException
	{
		return AnnotationFactory.class.getDeclaredMethod("javaxNullableMethod");
	}

	@Nonnull(when = When.NEVER)
	private static Object javaxNonnullNeverMethod()
	{
		return null;
	}
	static Method javaxNonnullNever() throws NoSuchMethodException
	{
		return AnnotationFactory.class.getDeclaredMethod("javaxNonnullNeverMethod");
	}

	private AnnotationFactory()
	{
		// prevent instantiation
	}
}
