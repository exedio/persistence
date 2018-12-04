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

import static com.exedio.cope.IntegerFieldDefaultToNextItem.TYPE;
import static com.exedio.cope.IntegerFieldDefaultToNextItem.integerNext;
import static com.exedio.cope.IntegerFieldDefaultToNextItem.integerNone;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class IntegerFieldDefaultToNextModelTest
{
	public static final Model MODEL = new Model(TYPE);

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testModel()
	{
		assertEquals(list(
				TYPE.getThis(),
				integerNext, integerNext.getDefaultNext(), integerNone
				), TYPE.getDeclaredFeatures());

		assertEquals(true,  integerNext.hasDefault());
		assertEquals(false, integerNone.hasDefault());

		assertEquals(null, integerNext.getDefaultConstant());
		assertEquals(null, integerNone.getDefaultConstant());

		assertEquals(true,  integerNext.isDefaultNext());
		assertEquals(false, integerNone.isDefaultNext());

		assertEquals(integer(10001), integerNext.getDefaultNextStart());
		assertEquals(null, integerNone.getDefaultNextStart());

		{
			final Sequence s = integerNext.getDefaultNext();
			assertNotNull(s);
			assertEquals("integerNext-Seq",s.getName());
			assertEquals("IntegerFieldDefaultToNextItem.integerNext-Seq", s.getID());
			assertEquals(TYPE, s.getType());
			assertEquals(null, s.getPattern());
			assertEquals(10001, s.getStart());
			assertEquals(Integer.MAX_VALUE, s.getEnd());
		}
		assertEquals(null, integerNone.getDefaultNext());
	}
	@Test void testNextToConstant()
	{
		final IntegerField feature = integerNext.defaultTo(99);
		assertEquals(true, feature.hasDefault());
		assertEquals(integer(99), feature.getDefaultConstant());
		assertEquals(false, feature.isDefaultNext());
		assertEquals(null, feature.getDefaultNextStart());
	}
	@Test void testStartOutOfRange()
	{
		try
		{
			integerNext.min(10002);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"The start value for defaultToNext of the field does not comply to one of it's own constraints, " +
					"caused a IntegerRangeViolationException: " +
					"range violation, " +
					"10001 is too small, " +
					"must be at least 10002. Start value was '10001'.", e.getMessage());
		}
	}

	private static Integer integer(final int i)
	{
		return Integer.valueOf(i);
	}
}
