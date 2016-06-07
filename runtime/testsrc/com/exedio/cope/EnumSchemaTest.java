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

import static com.exedio.cope.SchemaInfo.getColumnValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import org.junit.Test;

public class EnumSchemaTest
{
	@Test public void testNormal()
	{
		assertColumnValues(Normal.class, 10, 20, 30);
	}
	enum Normal
	{
		Eins, Zwei, Drei;
	}


	@Test public void testNormal2()
	{
		assertColumnValues(Normal2.class, 10, 20);

		try
		{
			getColumnValue((Normal2)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	enum Normal2
	{
		Eins, Zwei;
	}


	@SuppressWarnings({"unchecked","cast", "rawtypes"}) // OK: test bad api usage
	@Deprecated // OK: test deprecated api
	@Test public void testUnchecked()
	{
		final EnumField<Normal2> normal = EnumField.create(Normal2.class);
		try
		{
			getColumnValue(((EnumField)normal), (Enum)Normal.Eins);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected " + Normal2.class.getName() + ", but was a " + Normal.class.getName(), e.getMessage());
		}
	}


	@Test public void testAnnotatedBefore()
	{
		assertColumnValues(AnnotatedBefore.class, 10, 11, 20);
	}
	enum AnnotatedBefore
	{
		Eins,
		@CopeSchemaValue(11) Zwei,
		Drei;
	}


	@Test public void testAnnotatedAfter()
	{
		assertColumnValues(AnnotatedAfter.class, 10, 19, 20);
	}
	enum AnnotatedAfter
	{
		Eins,
		@CopeSchemaValue(19) Zwei,
		Drei;
	}


	@Test public void testAnnotatedStart()
	{
		assertColumnValues(AnnotatedStart.class, 9, 10, 20);
	}
	enum AnnotatedStart
	{
		@CopeSchemaValue(9) Eins,
		Zwei,
		Drei;
	}


	@Test public void testAnnotatedEnd()
	{
		assertColumnValues(AnnotatedEnd.class, 10, 20, 21);
	}
	enum AnnotatedEnd
	{
		Eins,
		Zwei,
		@CopeSchemaValue(21) Drei;
	}


	@Test public void testCollisionBefore()
	{
		try
		{
			EnumField.create(CollisionBefore.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(CollisionBefore.class.getName() + ": @CopeSchemaValue for Zwei must be greater than 10 and less than 20, but was 10.", e.getMessage());
		}
	}
	enum CollisionBefore
	{
		Eins,
		@CopeSchemaValue(10) Zwei,
		Drei;
	}


	@Test public void testCollisionAfter()
	{
		try
		{
			EnumField.create(CollisionAfter.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(CollisionAfter.class.getName() + ": @CopeSchemaValue for Zwei must be greater than 10 and less than 20, but was 20.", e.getMessage());
		}
	}
	enum CollisionAfter
	{
		Eins,
		@CopeSchemaValue(20) Zwei,
		Drei;
	}


	@Test public void testCollisionStart()
	{
		try
		{
			EnumField.create(CollisionStart.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(CollisionStart.class.getName() + ": @CopeSchemaValue for Eins must be less than 10, but was 10.", e.getMessage());
		}
	}
	enum CollisionStart
	{
		@CopeSchemaValue(10) Eins,
		Zwei,
		Drei;
	}


	@Test public void testCollisionEnd()
	{
		try
		{
			EnumField.create(CollisionEnd.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(CollisionEnd.class.getName() + ": @CopeSchemaValue for Drei must be greater than 20, but was 20.", e.getMessage());
		}
	}
	enum CollisionEnd
	{
		Eins,
		Zwei,
		@CopeSchemaValue(20) Drei;
	}


	@Test public void testOrderBefore()
	{
		try
		{
			EnumField.create(OrderBefore.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(OrderBefore.class.getName() + ": @CopeSchemaValue for Zwei must be greater than 10 and less than 20, but was 9.", e.getMessage());
		}
	}
	enum OrderBefore
	{
		Eins,
		@CopeSchemaValue(9) Zwei,
		Drei;
	}


	@Test public void testOrderAfter()
	{
		try
		{
			EnumField.create(OrderAfter.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(OrderAfter.class.getName() + ": @CopeSchemaValue for Zwei must be greater than 10 and less than 20, but was 21.", e.getMessage());
		}
	}
	enum OrderAfter
	{
		Eins,
		@CopeSchemaValue(21) Zwei,
		Drei;
	}


	@Test public void testOrderStart()
	{
		try
		{
			EnumField.create(OrderStart.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(OrderStart.class.getName() + ": @CopeSchemaValue for Eins must be less than 10, but was 11.", e.getMessage());
		}
	}
	enum OrderStart
	{
		@CopeSchemaValue(11) Eins,
		Zwei,
		Drei;
	}


	@Test public void testOrderEnd()
	{
		try
		{
			EnumField.create(OrderEnd.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(OrderEnd.class.getName() + ": @CopeSchemaValue for Drei must be greater than 20, but was 19.", e.getMessage());
		}
	}
	enum OrderEnd
	{
		Eins,
		Zwei,
		@CopeSchemaValue(19) Drei;
	}


	@Test public void testSubclass()
	{
		assertColumnValues(Subclass.class, 1, 2);
	}
	enum Subclass
	{
		@CopeSchemaValue(1) Eins {@Override int zack(){ return 1; } },
		@CopeSchemaValue(2) Zwei {@Override int zack(){ return 2; } };
		abstract int zack();
	}


	private static final void assertColumnValues(
			final Class<? extends Enum<?>> actual,
			final int... expected)
	{
		final ArrayList<Integer> actualValues = new ArrayList<>(expected.length);
		for(final Enum<?> e : actual.getEnumConstants())
			actualValues.add(getColumnValue(e));

		final ArrayList<Integer> expectedValues = new ArrayList<>(expected.length);
		for(final int e : expected)
			expectedValues.add(e);

		assertEquals(actual.getName(), expectedValues, actualValues);
	}
}
