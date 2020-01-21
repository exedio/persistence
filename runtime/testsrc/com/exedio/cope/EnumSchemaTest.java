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

import static com.exedio.cope.EnumFieldType.roundUpTo10;
import static com.exedio.cope.SchemaInfo.getColumnValue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class EnumSchemaTest
{
	@Test void testRoundUpTo10()
	{
		assertEquals(-10, roundUpTo10(-12));
		assertEquals(-10, roundUpTo10(-11));
		assertEquals(-10, roundUpTo10(-10));
		assertEquals(  0, roundUpTo10( -9));
		assertEquals(  0, roundUpTo10( -8));
		assertEquals(  0, roundUpTo10( -2));
		assertEquals(  0, roundUpTo10( -1));
		assertEquals(  0, roundUpTo10(  0));
		assertEquals( 10, roundUpTo10(  1));
		assertEquals( 10, roundUpTo10(  2));
		assertEquals( 10, roundUpTo10(  8));
		assertEquals( 10, roundUpTo10(  9));
		assertEquals( 10, roundUpTo10( 10));
		assertEquals( 20, roundUpTo10( 11));
		assertEquals( 20, roundUpTo10( 12));
	}

	@Test void testNormal()
	{
		assertColumnValues(Normal.class, 10, 20, 30);
	}
	@SuppressWarnings("unused")
	enum Normal
	{
		Eins, Zwei, Drei
	}


	@Test void testNormal2()
	{
		assertColumnValues(Normal2.class, 10, 20);

		try
		{
			getColumnValue(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	@SuppressWarnings("unused")
	enum Normal2
	{
		Eins, Zwei
	}


	@Test void testAnnotatedBefore()
	{
		assertColumnValues(AnnotatedBefore.class, 10, 11, 20);
	}
	@SuppressWarnings("unused")
	enum AnnotatedBefore
	{
		Eins,
		@CopeSchemaValue(11) Zwei,
		Drei
	}


	@Test void testAnnotatedAfter()
	{
		assertColumnValues(AnnotatedAfter.class, 10, 19, 20);
	}
	@SuppressWarnings("unused")
	enum AnnotatedAfter
	{
		Eins,
		@CopeSchemaValue(19) Zwei,
		Drei
	}


	@Test void testAnnotatedStart()
	{
		assertColumnValues(AnnotatedStart.class, 9, 10, 20);
	}
	@SuppressWarnings("unused")
	enum AnnotatedStart
	{
		@CopeSchemaValue(9) Eins,
		Zwei,
		Drei
	}


	@Test void testAnnotatedEnd()
	{
		assertColumnValues(AnnotatedEnd.class, 10, 20, 21);
	}
	@SuppressWarnings("unused")
	enum AnnotatedEnd
	{
		Eins,
		Zwei,
		@CopeSchemaValue(21) Drei
	}


	@Test void testCollisionBefore()
	{
		assertFails(
				CollisionBefore.class,
				"@CopeSchemaValue for Zwei must be greater than 10, but was 10.");
	}
	@SuppressWarnings("unused")
	enum CollisionBefore
	{
		Eins,
		@CopeSchemaValue(10) Zwei,
		Drei
	}


	@Test void testCollisionAfter()
	{
		assertColumnValues(CollisionAfter.class, 10, 20, 30);
	}
	@SuppressWarnings("unused")
	enum CollisionAfter
	{
		Eins,
		@CopeSchemaValue(20) Zwei,
		Drei
	}


	@Test void testCollisionStart()
	{
		assertColumnValues(CollisionStart.class, 10, 20, 30);
	}
	@SuppressWarnings("unused")
	enum CollisionStart
	{
		@CopeSchemaValue(10) Eins,
		Zwei,
		Drei
	}


	@Test void testCollisionEnd()
	{
		assertFails(
				CollisionEnd.class,
				"@CopeSchemaValue for Drei must be greater than 20, but was 20.");
	}
	@SuppressWarnings("unused")
	enum CollisionEnd
	{
		Eins,
		Zwei,
		@CopeSchemaValue(20) Drei
	}


	@Test void testOrderBefore()
	{
		assertFails(
				OrderBefore.class,
				"@CopeSchemaValue for Zwei must be greater than 10, but was 9.");
	}
	@SuppressWarnings("unused")
	enum OrderBefore
	{
		Eins,
		@CopeSchemaValue(9) Zwei,
		Drei
	}


	@Test void testOrderAfter()
	{
		assertColumnValues(OrderAfter.class, 10, 21, 30);
	}
	@SuppressWarnings("unused")
	enum OrderAfter
	{
		Eins,
		@CopeSchemaValue(21) Zwei,
		Drei
	}


	@Test void testOrderStart()
	{
		assertColumnValues(OrderStart.class, 11, 20, 30);
	}
	@SuppressWarnings("unused")
	enum OrderStart
	{
		@CopeSchemaValue(11) Eins,
		Zwei,
		Drei
	}


	@Test void testOrderEnd()
	{
		assertFails(
				OrderEnd.class,
				"@CopeSchemaValue for Drei must be greater than 20, but was 19.");
	}
	@SuppressWarnings("unused")
	enum OrderEnd
	{
		Eins,
		Zwei,
		@CopeSchemaValue(19) Drei
	}


	@Test void testSubclass()
	{
		assertColumnValues(Subclass.class, 1, 2);
	}
	@SuppressWarnings("unused")
	enum Subclass
	{
		@CopeSchemaValue(1) Eins {@Override int zack(){ return 1; } },
		@CopeSchemaValue(2) Zwei {@Override int zack(){ return 2; } };

		abstract int zack();
	}


	@Test void testZero()
	{
		assertColumnValues(Zero.class, 0, 1, 2);
	}
	@SuppressWarnings("unused")
	enum Zero
	{
		@CopeSchemaValue(0) Zero,
		@CopeSchemaValue(1) Eins,
		@CopeSchemaValue(2) Zwei
	}


	@Test void testNegativeFull()
	{
		assertColumnValues(NegativeFull.class, -40, -35, -5, 0, 10);
	}
	@SuppressWarnings("unused")
	enum NegativeFull
	{
		@CopeSchemaValue(-40) Minus40,
		@CopeSchemaValue(-35) Minus35,
		@CopeSchemaValue( -5) Minus5,
		Eins,
		Zwei
	}


	@Test void testNegativeFirst()
	{
		assertColumnValues(NegativeFirst.class, -15, -10, 0, 10);
	}
	@SuppressWarnings("unused")
	enum NegativeFirst
	{
		@CopeSchemaValue(-15) Minus15,
		Eins,
		Zwei,
		Drei
	}


	private static void assertColumnValues(
			final Class<? extends Enum<?>> actual,
			final int... expected)
	{
		final ArrayList<Integer> actualValues = new ArrayList<>(expected.length);
		for(final Enum<?> e : actual.getEnumConstants())
			actualValues.add(getColumnValue(e));

		final ArrayList<Integer> expectedValues = new ArrayList<>(expected.length);
		for(final int e : expected)
			expectedValues.add(e);

		assertEquals(expectedValues, actualValues, actual.getName());
	}

	private static <E extends Enum<E>> void assertFails(
			final Class<E> actual,
			final String message)
	{
		try
		{
			EnumField.create(actual);
			fail(actual.getName());
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(actual.getName() + ": " + message, e.getMessage());
		}
	}
}
