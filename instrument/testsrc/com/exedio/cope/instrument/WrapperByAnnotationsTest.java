package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NULLABLE;
import static com.exedio.cope.instrument.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class WrapperByAnnotationsTest
{
	@Test
	void nullabilityFromAnnotations() throws NoSuchMethodException
	{
		assertEquals(DEFAULT, WrapperByAnnotations.nullabilityFromAnnotations(AnnotationFactory.none()));
		assertEquals(NONNULL, WrapperByAnnotations.nullabilityFromAnnotations(AnnotationFactory.javaxNonnull()));
		assertEquals(NULLABLE, WrapperByAnnotations.nullabilityFromAnnotations(AnnotationFactory.javaxNullable()));
		final Method javaxNonnullNever = AnnotationFactory.javaxNonnullNever();
		assertFails(
				() -> WrapperByAnnotations.nullabilityFromAnnotations(javaxNonnullNever),
				RuntimeException.class,
				"non-default setting of Nonnull.when() not supported");
	}
}
