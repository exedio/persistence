package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NULLABLE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.testmodel.NullabilityItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
		assertEquals(8, wrappers.size());
		assertWrapper(wrappers, "defaultReturnAndParameter", DEFAULT, DEFAULT);
		assertWrapper(wrappers, "conditionalNullableReturn", dynamicMethodNullability);
		assertWrapper(wrappers, "conditionalNullableParameter", DEFAULT, dynamicParameterNullability);
		assertWrapper(wrappers, "javaxNullableReturn", NULLABLE);
		assertWrapper(wrappers, "javaxNonnullReturn", NONNULL);
		assertWrapper(wrappers, "javaxNullableNonnullReturn", NONNULL); // nonnull wins
		assertWrapper(wrappers, "javaxNullableParameter", DEFAULT, NULLABLE);
		assertWrapper(wrappers, "javaxNonnullParameter", DEFAULT, NONNULL);
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
}
