package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.Nullability.DEFAULT;
import static com.exedio.cope.instrument.Nullability.NONNULL;
import static com.exedio.cope.instrument.Nullability.NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class GeneratorTest
{
	private static final String EOL = System.lineSeparator();
	private static final TypeWriter FQN = new TypeWriter(null, true);

	@Test
	void writeParameter_none()
	{
		assertEquals(
				"final java.lang.String value",
				none(g -> g.writeParameter(FQN, String.class, DEFAULT, false, "value")));
		assertEquals(
				"final java.lang.String value",
				none(g -> g.writeParameter(FQN, String.class, NONNULL, false, "value")));
		assertEquals(
				"final java.lang.String value",
				none(g -> g.writeParameter(FQN, String.class, NULLABLE, false, "value")));
	}

	@Test
	void writeParameter_primitive()
	{
		assertEquals(
				"final int value",
				javax(g -> g.writeParameter(FQN, int.class, NONNULL, false, "value")));
		assertEquals(
				"final int value",
				javax(g -> g.writeParameter(FQN, int.class, NULLABLE, false, "value"))); // TODO log or fail?
	}

	@Test
	void writeParameter_javax()
	{
		assertEquals(
				"final java.lang.String value",
				javax(g -> g.writeParameter(FQN, String.class, DEFAULT, false, "value")));
		assertEquals(
				"@javax.annotation.Nullable final java.lang.String value",
				javax(g -> g.writeParameter(FQN, String.class, NULLABLE, false, "value")));
		assertEquals(
				"@javax.annotation.Nonnull final java.lang.String value",
				javax(g -> g.writeParameter(FQN, String.class, NONNULL, false, "value")));

		assertEquals(
				"@javax.annotation.Nullable final java.lang.String[] values",
				javax(g -> g.writeParameter(FQN, String[].class, NULLABLE, false, "values")));
		assertEquals(
				"@javax.annotation.Nullable final java.lang.String... values",
				javax(g -> g.writeParameter(FQN, String[].class, NULLABLE, true, "values")));
	}

	@Test
	void writeMethodNullability_none()
	{
		assertEquals(
				"",
				none(g -> g.writeMethodNullability(DEFAULT, false)));
		assertEquals(
				"",
				none(g -> g.writeMethodNullability(NONNULL, false)));
		assertEquals(
				"",
				none(g -> g.writeMethodNullability(NULLABLE, false)));
	}

	@Test
	void writeMethodNullability_javax()
	{
		assertEquals(
				"",
				javax(g -> g.writeMethodNullability(DEFAULT, false)));
		assertEquals(
				"@javax.annotation.Nonnull" + EOL,
				javax(g -> g.writeMethodNullability(NONNULL, false)));
		assertEquals(
				"@javax.annotation.Nullable" + EOL,
				javax(g -> g.writeMethodNullability(NULLABLE, false)));
	}

	@Test
	void writeMethodNullability_javax_optional()
	{
		assertEquals(
				"@javax.annotation.Nonnull" + EOL,
				javax(g -> g.writeMethodNullability(DEFAULT, true)));
		assertEquals(
				"@javax.annotation.Nonnull" + EOL,
				javax(g -> g.writeMethodNullability(NONNULL, true)));
		assertEquals(
				"@javax.annotation.Nonnull" + EOL,
				javax(g -> g.writeMethodNullability(NULLABLE, true)));
	}

	private static String write(
			final boolean nullabilityAnnotations,
			final Consumer<Generator> write)
	{
		final StringBuilder output = new StringBuilder();
		final Params params = new Params();
		params.nullabilityAnnotations = nullabilityAnnotations;
		final Generator generator = new Generator(output, params, Set.of(), Set.of());
		generator.setTypeContext(new Generator.TypeContext(0, true));
		write.accept(generator);
		return output.toString();
	}

	private static String none(final Consumer<Generator> write)
	{
		return write(false, write);
	}

	private static String javax(final Consumer<Generator> write)
	{
		return write(true, write);
	}
}
