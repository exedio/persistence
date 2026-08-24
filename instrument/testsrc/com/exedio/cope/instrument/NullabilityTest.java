package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NONNULL_CLASS_NAME;
import static com.exedio.cope.instrument.Nullability.NULLABLE;
import static com.exedio.cope.instrument.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.testmodel.NullabilityItem;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;

class NullabilityTest
{
	@Test
	void nullability() throws HumanReadableException, IOException
	{
		new JavaRepositoryBuilder().filter(NullabilityItem.class).buildAndRun(
				repository ->
				{
					final LocalCopeType nullabilityItem = repository.getCopeType(NullabilityItem.class.getName());
					assertEquals("NullabilityItem", nullabilityItem.getName());
					final LocalCopeFeature optional = nullabilityItem.getFeatures().get(0);
					assertEquals("optional", optional.getName());
					final WrapperX allCanReturnNull = optional.getWrappers().get(0);
					assertEquals("allCanReturnNull", allCanReturnNull.name);
					assertEquals(NULLABLE, allCanReturnNull.getMethodNullability());
				}
		);
	}

	@Test
	void nonnullClassName()
	{
		assertEquals(Nonnull.class.getName(), NONNULL_CLASS_NAME);
	}

	@Test
	void forOptional()
	{
		assertEquals(NULLABLE, Nullability.forOptional(true));
		assertEquals(NONNULL, Nullability.forOptional(false));
	}

	@Test
	void forMandatory()
	{
		assertEquals(NONNULL, Nullability.forMandatory(true));
		assertEquals(NULLABLE, Nullability.forMandatory(false));
	}

	@Test
	void fromAnnotations() throws NoSuchMethodException
	{
		assertEquals(DEFAULT, Nullability.fromAnnotations(AnnotationFactory.none()));
		assertEquals(NONNULL, Nullability.fromAnnotations(AnnotationFactory.javaxNonnull()));
		assertEquals(NULLABLE, Nullability.fromAnnotations(AnnotationFactory.javaxNullable()));
		final Method javaxNonnullNever = AnnotationFactory.javaxNonnullNever();
		assertFails(
				() -> Nullability.fromAnnotations(javaxNonnullNever),
				RuntimeException.class,
				"non-default setting of Nonnull.when() not supported");
	}
}
