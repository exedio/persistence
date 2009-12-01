/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;
import static com.exedio.cope.SchemaInfo.getColumnValue;

public class EnumSchemaTest extends CopeAssert
{
	public void testNormal()
	{
		
		assertEquals(10, getColumnValue(Normal.Eins));
		assertEquals(20, getColumnValue(Normal.Zwei));
		assertEquals(30, getColumnValue(Normal.Drei));
	}
	
	enum Normal
	{
		Eins, Zwei, Drei;
	}
	
	public void testNormal2()
	{
		
		assertEquals(10, getColumnValue(Normal2.Eins));
		assertEquals(20, getColumnValue(Normal2.Zwei));

		try
		{
			getColumnValue((Normal2)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	
	enum Normal2
	{
		Eins, Zwei;
	}
	
	@SuppressWarnings({"unchecked","cast"}) // OK: test bad api usage
	@Deprecated // OK: test deprecated api
	public void testUnchecked()
	{
		final EnumField<Normal2> normal = Item.newEnumField(Normal2.class);
		try
		{
			getColumnValue(((EnumField)normal), (Enum)Normal.Eins);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expected " + Normal2.class.getName() + ", but was a " + Normal.class.getName(), e.getMessage());
		}
	}
	
	public void testAnnotatedBefore()
	{
		
		assertEquals(10, getColumnValue(AnnotatedBefore.Eins));
		assertEquals(11, getColumnValue(AnnotatedBefore.Zwei));
		assertEquals(20, getColumnValue(AnnotatedBefore.Drei));
	}
	
	enum AnnotatedBefore
	{
		Eins,
		@CopeSchemaValue(11) Zwei,
		Drei;
	}
	
	public void testAnnotatedAfter()
	{
		
		assertEquals(10, getColumnValue(AnnotatedAfter.Eins));
		assertEquals(19, getColumnValue(AnnotatedAfter.Zwei));
		assertEquals(20, getColumnValue(AnnotatedAfter.Drei));
	}
	
	enum AnnotatedAfter
	{
		Eins,
		@CopeSchemaValue(19) Zwei,
		Drei;
	}
	
	public void testAnnotatedStart()
	{
		
		assertEquals( 9, getColumnValue(AnnotatedStart.Eins));
		assertEquals(10, getColumnValue(AnnotatedStart.Zwei));
		assertEquals(20, getColumnValue(AnnotatedStart.Drei));
	}
	
	enum AnnotatedStart
	{
		@CopeSchemaValue(9) Eins,
		Zwei,
		Drei;
	}
	
	public void testAnnotatedEnd()
	{
		
		assertEquals(10, getColumnValue(AnnotatedEnd.Eins));
		assertEquals(20, getColumnValue(AnnotatedEnd.Zwei));
		assertEquals(21, getColumnValue(AnnotatedEnd.Drei));
	}
	
	enum AnnotatedEnd
	{
		Eins,
		Zwei,
		@CopeSchemaValue(21) Drei;
	}
	
	public void testAnnotatedError()
	{
		try
		{
			Item.newEnumField(CollisionBefore.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(CollisionBefore.class.getName() + ": @CopeSchemaValue for Zwei must be greater than 10 and less than 20, but was 10.", e.getMessage());
		}
		try
		{
			Item.newEnumField(CollisionAfter.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(CollisionAfter.class.getName() + ": @CopeSchemaValue for Zwei must be greater than 10 and less than 20, but was 20.", e.getMessage());
		}
		try
		{
			Item.newEnumField(CollisionStart.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(CollisionStart.class.getName() + ": @CopeSchemaValue for Eins must be less than 10, but was 10.", e.getMessage());
		}
		try
		{
			Item.newEnumField(CollisionEnd.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(CollisionEnd.class.getName() + ": @CopeSchemaValue for Drei must be greater than 20, but was 20.", e.getMessage());
		}
		try
		{
			Item.newEnumField(OrderBefore.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(OrderBefore.class.getName() + ": @CopeSchemaValue for Zwei must be greater than 10 and less than 20, but was 9.", e.getMessage());
		}
		try
		{
			Item.newEnumField(OrderAfter.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(OrderAfter.class.getName() + ": @CopeSchemaValue for Zwei must be greater than 10 and less than 20, but was 21.", e.getMessage());
		}
		try
		{
			Item.newEnumField(OrderStart.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(OrderStart.class.getName() + ": @CopeSchemaValue for Eins must be less than 10, but was 11.", e.getMessage());
		}
		try
		{
			Item.newEnumField(OrderEnd.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(OrderEnd.class.getName() + ": @CopeSchemaValue for Drei must be greater than 20, but was 19.", e.getMessage());
		}
	}
	
	enum CollisionBefore
	{
		Eins,
		@CopeSchemaValue(10) Zwei,
		Drei;
	}
	
	enum CollisionAfter
	{
		Eins,
		@CopeSchemaValue(20) Zwei,
		Drei;
	}
	
	enum CollisionStart
	{
		@CopeSchemaValue(10) Eins,
		Zwei,
		Drei;
	}
	
	enum CollisionEnd
	{
		Eins,
		Zwei,
		@CopeSchemaValue(20) Drei;
	}
	
	enum OrderBefore
	{
		Eins,
		@CopeSchemaValue(9) Zwei,
		Drei;
	}
	
	enum OrderAfter
	{
		Eins,
		@CopeSchemaValue(21) Zwei,
		Drei;
	}
	
	enum OrderStart
	{
		@CopeSchemaValue(11) Eins,
		Zwei,
		Drei;
	}
	
	enum OrderEnd
	{
		Eins,
		Zwei,
		@CopeSchemaValue(19) Drei;
	}
	
	public void testSubclass()
	{
		assertEquals(1, getColumnValue(Subclass.Eins));
		assertEquals(2, getColumnValue(Subclass.Zwei));
	}
	
	enum Subclass
	{
		@CopeSchemaValue(1) Eins {@Override int zack(){ return 1; } },
		@CopeSchemaValue(2) Zwei {@Override int zack(){ return 2; } };
		abstract int zack();
	}
}
