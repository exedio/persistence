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

import static com.exedio.cope.CopyConstraint.RESOLVE_TEMPLATE;
import static com.exedio.cope.CopyConstraint.SELF_TEMPLATE;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class FunctionFieldTest
{
	@Test void testCopyFromBooleanField()
	{
		checkCopyFrom(new BooleanField());
	}

	@Test void testCopyFromDateField()
	{
		checkCopyFrom(new DateField());
	}

	@Test void testCopyFromDayField()
	{
		checkCopyFrom(new DayField());
	}

	@Test void testCopyFromDoubleField()
	{
		checkCopyFrom(new DoubleField());
	}

	@Test void testCopyFromEnumField()
	{
		checkCopyFrom(EnumField.create(SomeEnum.class));
	}

	@Test void testCopyFromIntegerField()
	{
		checkCopyFrom(new IntegerField());
	}

	@Test void testCopyFromItemField()
	{
		checkCopyFrom(ItemField.create(SomeItem.class));
	}

	@Test void testCopyFromLongField()
	{
		checkCopyFrom(new LongField());
	}

	@Test void testCopyFromStringField()
	{
		checkCopyFrom(new StringField());
	}

	static final <E> void checkCopyFrom(final FunctionField<E> fieldA)
	{
		final ItemField<?> firstItemField = ItemField.create(SomeItem.class);
		final ItemField<?> secondItemField = ItemField.create(SomeItem.class);

		{
			final Supplier<? extends FunctionField<E>> templateB = () -> { throw new AssertionFailedError(); };
			final Supplier<? extends FunctionField<E>> templateC = () -> { throw new AssertionFailedError(); };
			final FunctionField<E> fieldB = fieldA.copyFrom(firstItemField, templateB);
			final FunctionField<E> fieldC = fieldB.copyFrom(secondItemField, templateC);
			final FunctionField<E> fieldD = fieldC.noCopyFrom();

			assertEquals(List.of(firstItemField), stream(fieldB.copyFrom).map(c -> c.target).collect(toList()));
			assertEquals(List.of(firstItemField, secondItemField), stream(fieldC.copyFrom).map(c -> c.target).collect(toList()));
			assertEquals(List.of(templateB), stream(fieldB.copyFrom).map(c -> c.template).collect(toList()));
			assertEquals(List.of(templateB, templateC), stream(fieldC.copyFrom).map(c -> c.template).collect(toList()));
			assertArrayEquals(null, fieldD.copyFrom);
			assertNotSame(fieldA, fieldD);
			assertEquals(fieldA.getClass(), fieldB.getClass());
			assertEquals(fieldA.getClass(), fieldC.getClass());
			assertEquals(fieldA.getClass(), fieldD.getClass());
		}
		{
			final FunctionField<E> fieldB = fieldA.copyFromSelf(firstItemField);
			final FunctionField<E> fieldC = fieldB.copyFromSelf(secondItemField);
			final FunctionField<E> fieldD = fieldC.noCopyFrom();

			assertEquals(List.of(firstItemField), stream(fieldB.copyFrom).map(c -> c.target).collect(toList()));
			assertEquals(List.of(firstItemField, secondItemField), stream(fieldC.copyFrom).map(c -> c.target).collect(toList()));
			assertEquals(List.of(SELF_TEMPLATE), stream(fieldB.copyFrom).map(c -> c.template).collect(toList()));
			assertEquals(List.of(SELF_TEMPLATE, SELF_TEMPLATE), stream(fieldC.copyFrom).map(c -> c.template).collect(toList()));
			assertArrayEquals(null, fieldD.copyFrom);
			assertNotSame(fieldA, fieldD);
			assertEquals(fieldA.getClass(), fieldB.getClass());
			assertEquals(fieldA.getClass(), fieldC.getClass());
			assertEquals(fieldA.getClass(), fieldD.getClass());
		}
		final FunctionField<E> fieldB = fieldA.copyFrom(firstItemField);
		final FunctionField<E> fieldC = fieldB.copyFrom(secondItemField);
		final FunctionField<E> fieldD = fieldC.noCopyFrom();

		assertArrayEquals(null, fieldA.copyFrom);
		assertEquals(List.of(firstItemField), stream(fieldB.copyFrom).map(c -> c.target).collect(toList()));
		assertEquals(List.of(firstItemField, secondItemField), stream(fieldC.copyFrom).map(c -> c.target).collect(toList()));
		assertEquals(List.of(RESOLVE_TEMPLATE), stream(fieldB.copyFrom).map(c -> c.template).collect(toList()));
		assertEquals(List.of(RESOLVE_TEMPLATE, RESOLVE_TEMPLATE), stream(fieldC.copyFrom).map(c -> c.template).collect(toList()));
		assertArrayEquals(null, fieldD.copyFrom);
		assertNotSame(fieldA, fieldD);
		assertEquals(fieldA.getClass(), fieldB.getClass());
		assertEquals(fieldA.getClass(), fieldC.getClass());
		assertEquals(fieldA.getClass(), fieldD.getClass());
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class SomeItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
	enum SomeEnum { a, b }
}
