/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope;

import static com.exedio.cope.RuntimeTester.assertFieldsCovered;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class MatchConditionTest
{
	@Test void testFieldsCovered()
	{
		assertFieldsCovered(asList(FIELD1), new MatchCondition(FIELD1, "one"));
		assertFieldsCovered(asList(FIELD2), new MatchCondition(FIELD2, "two"));
	}
	@Test void testEquals()
	{
		assertEqualsAndHash(
				new MatchCondition(FIELD1, "one"),
				new MatchCondition(FIELD1, "one"));
		assertNotEqualsAndHash(
				new MatchCondition(FIELD1, "one"),
				new MatchCondition(FIELD2, "one"),
				new MatchCondition(FIELD1, "two"));
	}
	@Test void testToString()
	{
		assertEquals(FIELD1 + " matches 'one'", new MatchCondition(FIELD1, "one").toString());
		assertEquals(FIELD2 + " matches 'two'", new MatchCondition(FIELD2, "two").toString());
	}
	@Test void testCreateFunctionNull()
	{
		assertFails(
				() -> new MatchCondition(null, null),
				NullPointerException.class,
				"function");
	}
	@Test void testCreateValueNull()
	{
		assertFails(
				() -> new MatchCondition(FIELD1, null),
				NullPointerException.class,
				"value");
	}

	private static final StringField FIELD1 = new StringField();
	private static final StringField FIELD2 = new StringField();
}
