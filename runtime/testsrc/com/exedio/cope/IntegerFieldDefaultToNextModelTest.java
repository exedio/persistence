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
import static com.exedio.cope.IntegerFieldDefaultToNextItem.next;
import static com.exedio.cope.IntegerFieldDefaultToNextItem.none;
import static com.exedio.cope.SchemaInfo.getDefaultToNextSequenceName;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class IntegerFieldDefaultToNextModelTest
{
	public static final Model MODEL = Model.builder().
			name(IntegerFieldDefaultToNextModelTest.class).
			add(TYPE).
			build();

	@Test void testModel()
	{
		assertEquals(list(
				TYPE.getThis(),
				next, next.getDefaultNextSequence(), none
				), TYPE.getDeclaredFeatures());

		assertEquals(true,  next.hasDefault());
		assertEquals(false, none.hasDefault());

		assertEquals(null, next.getDefaultConstant());
		assertEquals(null, none.getDefaultConstant());

		assertEquals(true,  next.isDefaultNext());
		assertEquals(false, none.isDefaultNext());

		assertEquals(integer(10001), getDefaultNextStart(next));
		assertEquals(null, getDefaultNextStart(none));

		assertEquals(10001, next.getDefaultNextStartX());
		assertFails(none::getDefaultNextStartX, IllegalArgumentException.class, "is not defaultToNext: " + none);

		{
			final Sequence s = next.getDefaultNextSequence();
			assertNotNull(s);
			assertEquals("next-Seq",s.getName());
			assertEquals("IntegerFieldDefaultToNextItem.next-Seq", s.getID());
			assertEquals(TYPE, s.getType());
			assertEquals(null, s.getPattern());
			assertEquals(10001, s.getStart());
			assertEquals(Integer.MAX_VALUE, s.getEnd());

			assertSame(s, getDefaultNext(next));
		}
		assertEquals(null, getDefaultNext(none));
		assertFails(none::getDefaultNextSequence, IllegalArgumentException.class, "is not defaultToNext: " + none);
	}
	@Test void testNextToConstant()
	{
		final IntegerField feature = next.defaultTo(99);
		assertEquals(true, feature.hasDefault());
		assertEquals(integer(99), feature.getDefaultConstant());
		assertEquals(false, feature.isDefaultNext());
		assertFails(feature::getDefaultNextStartX, IllegalArgumentException.class, "is not defaultToNext: " + feature);
	}
	@Test void testConstantToNext()
	{
		final IntegerField origin = new IntegerField().defaultTo(55);
		final IntegerField feature = origin.defaultToNext(88);
		assertEquals(true, feature.hasDefault());
		assertEquals(null, feature.getDefaultConstant());
		assertEquals(true, feature.isDefaultNext());
		assertEquals(88, feature.getDefaultNextStartX());
	}
	@Test void testStartOutOfRange()
	{
		assertFails(
				() -> next.min(10002),
				IllegalArgumentException.class,
				"The start value 10001 for defaultToNext of the field does not comply to one of it's own constraints, " +
				"caused a IntegerRangeViolationException: " +
				"range violation, " +
				"10001 is too small, " +
				"must be at least 10002");
	}
	@SuppressWarnings("deprecation") // OK, testing deprecated API
	@Test void testSequenceBehindNone()
	{
		assertEquals(null, none.checkSequenceBehindDefaultToNext());
	}
	@Test void testSequenceBehindNoneX()
	{
		assertFails(
				none::checkSequenceBehindDefaultToNextX,
				IllegalArgumentException.class,
				"is not defaultToNext: " + none);
	}
	@Test void testSequenceNameNone()
	{
		assertFails(
				() -> getDefaultToNextSequenceName(none),
				IllegalArgumentException.class,
				"is not defaultToNext: " + none);
	}

	private static Integer integer(final int i)
	{
		return Integer.valueOf(i);
	}

	@SuppressWarnings("deprecation") // OK, wrapping deprecated API
	private static Integer getDefaultNextStart(final IntegerField f)
	{
		return f.getDefaultNextStart();
	}

	@SuppressWarnings("deprecation") // OK, wrapping deprecated API
	private static Sequence getDefaultNext(final IntegerField f)
	{
		return f.getDefaultNext();
	}
}
