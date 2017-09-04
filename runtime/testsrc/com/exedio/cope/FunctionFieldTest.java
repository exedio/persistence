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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.Test;

public class FunctionFieldTest
{
	@Test public void testCopyFromBooleanField()
	{
		checkCopyFrom(new BooleanField());
	}

	@Test public void testCopyFromDateField()
	{
		checkCopyFrom(new DateField());
	}

	@Test public void testCopyFromDayField()
	{
		checkCopyFrom(new DayField());
	}

	@Test public void testCopyFromDoubleField()
	{
		checkCopyFrom(new DoubleField());
	}

	@Test public void testCopyFromEnumField()
	{
		checkCopyFrom(EnumField.create(SomeEnum.class));
	}

	@Test public void testCopyFromIntegerField()
	{
		checkCopyFrom(new IntegerField());
	}

	@Test public void testCopyFromItemField()
	{
		checkCopyFrom(ItemField.create(SomeItem.class));
	}

	@Test public void testCopyFromLongField()
	{
		checkCopyFrom(new LongField());
	}

	@Test public void testCopyFromStringField()
	{
		checkCopyFrom(new StringField());
	}

	static final <E> void checkCopyFrom(final FunctionField<E> fieldA)
	{
		final ItemField<?> firstItemField = ItemField.create(SomeItem.class);
		final ItemField<?> secondItemField = ItemField.create(SomeItem.class);
		final FunctionField<E> fieldB = fieldA.copyFrom(firstItemField);
		final FunctionField<E> fieldC = fieldB.copyFrom(secondItemField);
		final FunctionField<E> fieldD = fieldC.noCopyFrom();

		assertArrayEquals(null, fieldA.copyFrom);
		assertArrayEquals(new ItemField<?>[]{firstItemField}, fieldB.copyFrom);
		assertArrayEquals(new ItemField<?>[]{firstItemField, secondItemField}, fieldC.copyFrom);
		assertArrayEquals(null, fieldD.copyFrom);
		assertNotSame(fieldA, fieldD);
		assertEquals(fieldA.getClass(), fieldB.getClass());
		assertEquals(fieldA.getClass(), fieldC.getClass());
		assertEquals(fieldA.getClass(), fieldD.getClass());
	}

	@WrapperIgnore
	static class SomeItem extends Item
	{
		private static final long serialVersionUID = 1L;
	}

	enum SomeEnum { a, b }
}
