package com.exedio.cope;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;

import org.junit.jupiter.api.Test;

public class InConditionTest
{
	@Test void testEqualsAndHash()
	{
		final StringField field = new StringField().optional();
		assertEqualsAndHash(new InCondition<>(field, false, asList("AAA", "BBB")), new InCondition<>(field, false, asList("AAA", "BBB")));
		assertNotEqualsAndHash(new InCondition<>(field, false, asList("AAA", "BBB")), new InCondition<>(field, false, asList("BBB", "AAA")));
		assertNotEqualsAndHash(new InCondition<>(field, false, asList("AAA", "BBB")), new InCondition<>(field, false, asList("AAA", "CCC")));

		assertNotEqualsAndHash(new InCondition<>(field, false, asList("AAA", "BBB")), new InCondition<>(field, false, asList("AAA", "BBB")).not());
		assertEqualsAndHash(new InCondition<>(field, false, asList("AAA", "BBB")), new InCondition<>(field, false, asList("AAA", "BBB")).not().not());

		final StringField field2 = new StringField().optional();
		assertNotEqualsAndHash(new InCondition<>(field, false, asList("AAA", "BBB")), new InCondition<>(field2, false, asList("AAA", "BBB")));
	}

	@Test void testCreate()
	{
		assertFails(() -> new InCondition<>(null, false, asList("AAA", "BBB")), NullPointerException.class, "function");

		final StringField field = new StringField().optional();
		assertFails(() -> new InCondition<>(field, false, null), NullPointerException.class, "allowedValues");

		assertFails(() -> new InCondition<>(field, false, asList("AAA")), IllegalArgumentException.class, "allowedValues must contain more than one element");

	}
}
