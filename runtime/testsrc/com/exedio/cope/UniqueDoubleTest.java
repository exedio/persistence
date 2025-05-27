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

import static com.exedio.cope.AbstractRuntimeTest.assertDelete;
import static com.exedio.cope.SchemaInfo.getConstraintName;
import static com.exedio.cope.UniqueDoubleItem.TYPE;
import static com.exedio.cope.UniqueDoubleItem.constraint;
import static com.exedio.cope.UniqueDoubleItem.forConstraint;
import static com.exedio.cope.UniqueDoubleItem.integer;
import static com.exedio.cope.UniqueDoubleItem.string;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class UniqueDoubleTest extends TestWithEnvironment
{
	public UniqueDoubleTest()
	{
		super(UniqueDoubleModelTest.MODEL);
	}

	@Test void test()
	{
		assertEquals(
				unq("UniqueDoubleItem_constraint"),
				getConstraintName(constraint));

		assertEquals(null, forConstraint("a", 1));

		final UniqueDoubleItem a1 = new UniqueDoubleItem("a", 1);
		assertEquals(a1, forConstraint("a", 1));

		final UniqueDoubleItem a2 = new UniqueDoubleItem("a", 2);
		assertEquals(a2, forConstraint("a", 2));

		final UniqueDoubleItem b1 = new UniqueDoubleItem("b", 1);
		assertEquals(b1, forConstraint("b", 1));

		final UniqueDoubleItem b2 = new UniqueDoubleItem("b", 2);
		assertEquals(b2, forConstraint("b", 2));

		assertEquals(b1, forConstraint("b", 1));
		try
		{
			new UniqueDoubleItem("b", 1);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(constraint, e.getFeature());
			assertEquals(constraint, e.getFeatureForDescription());
			assertEqualsUnmodifiable(asList(string, integer), e.getFields());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + constraint, e.getMessage());
			assertCause(e);
		}
		assertEquals(b1, forConstraint("b", 1));
		try
		{
			TYPE.newItem(SetValue.map(string, "b"), SetValue.map(integer, 1));
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(constraint, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + constraint, e.getMessage());
			assertCause(e);
		}
		assertEquals(b1, forConstraint("b", 1));

		try
		{
			b2.setInteger(1);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(constraint, e.getFeature());
			assertEquals(b2, e.getItem());
			assertEquals("unique violation on " + b2 + " for " + constraint, e.getMessage());
			assertCause(e);
		}
		assertEquals(2, b2.getInteger());

		try
		{
			b2.set(SetValue.map(integer, 1));
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(constraint, e.getFeature());
			assertEquals(b2, e.getItem());
			assertEquals("unique violation on " + b2 + " for " + constraint, e.getMessage());
			assertCause(e);
		}
		assertEquals(2, b2.getInteger());

		// test setting the value already set
		b2.setInteger(2);
		assertEquals(2, b2.getInteger());

		assertDelete(b2);
		assertDelete(b1);

		final UniqueDoubleItem b1X = new UniqueDoubleItem("b", 1);
		assertEquals(b1X, forConstraint("b", 1));

		assertDelete(a2);
		assertDelete(a1);
		assertDelete(b1X);
	}
}
