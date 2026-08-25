package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NONNULL_CLASS_NAME;
import static com.exedio.cope.instrument.Nullability.NULLABLE;
import static com.exedio.cope.instrument.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.testmodel.NullabilityItem;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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
					assertNullability(optional, NULLABLE, NULLABLE);

					final LocalCopeFeature mandatory = nullabilityItem.getFeatures().get(1);
					assertEquals("mandatory", mandatory.getName());
					assertNullability(mandatory, NONNULL, NONNULL);
				}
		);
	}

	private static void assertNullability(
			final LocalCopeFeature feature,
			final Nullability dynamicMethodNullability,
			final Nullability dynamicParameterNullability)
	{
		final List<WrapperX> wrappers = feature.getWrappers();
		assertEquals(7, wrappers.size());
		assertWrapper(wrappers, "allCanReturnNull", NULLABLE);
		assertWrapper(wrappers, "allCannotReturnNull", NONNULL);
		assertWrapper(wrappers, "onlyOptionalsCanReturnNull", dynamicMethodNullability);
		assertWrapper(wrappers, "allCanTakeNull", DEFAULT, NULLABLE);
		assertWrapper(wrappers, "allCannotTakeNull", DEFAULT, NONNULL);
		assertWrapper(wrappers, "onlyOptionalsCanTakeNull", DEFAULT, dynamicParameterNullability);
		assertWrapper(wrappers, "defaultNullability", DEFAULT, DEFAULT);
	}

	private static void assertWrapper(
			final List<WrapperX> wrappers,
			final String name,
			final Nullability methodNullability,
			final Nullability... parameterNullabilities)
	{
		final WrapperX wrapper = wrappers.stream().filter(candidate -> name.equals(candidate.name)).findFirst().orElseThrow();
		final List<Executable> assertions = new ArrayList<>();
		assertions.add(() -> assertEquals(methodNullability, wrapper.getMethodNullability(), "method"));
		assertions.add(() -> assertEquals(parameterNullabilities.length, wrapper.getParameters().size(), "parameter count"));
		for (int i=0; i<parameterNullabilities.length; i++)
		{
			final WrapperX.Parameter parameter = wrapper.getParameters().get(i);
			final Nullability actual = parameter.isNullable() ? NULLABLE : parameter.isNonnull() ? NONNULL : DEFAULT;
			final int parameterIndex = i;
			assertions.add(() -> assertEquals(parameterNullabilities[parameterIndex], actual, "parameter " + parameterIndex));
		}
		assertAll(name, assertions.stream());
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
