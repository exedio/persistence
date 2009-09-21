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

package com.exedio.cope.pattern;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.BooleanField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;

public class TypeInheritancePatternTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(TypeInheritanceTestPatternItem.TYPE);
	
	public TypeInheritancePatternTest()
	{
		super(MODEL);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
	}
	
	public void testIt()
	{
		//abstract type
		final Type<?> abstractType = TypeInheritanceTestPatternItem.testPattern.getAbstractType();
		final StringField abstractTypeString = (StringField)abstractType.getFeature(TypeInheritanceTestPattern.ABSTRACTTYPE_STRING);
		final BooleanField abstractTypeBoolean = (BooleanField)abstractType.getFeature(TypeInheritanceTestPattern.ABSTRACTTYPE_BOOLEAN);

		//sub type
		final Type<?> subType = TypeInheritanceTestPatternItem.testPattern.getSubType();
		final IntegerField subTypeInteger = (IntegerField)subType.getFeature(TypeInheritanceTestPattern.SUBTYPE_INTEGER);
		
		assertEqualsUnmodifiable(
				list(
					abstractTypeString,
					abstractTypeBoolean
				),
				abstractType.getFields() );
		
		assertTrue(abstractType.isAbstract());
		assertFalse(subType.isAbstract());
		
		assertEqualsUnmodifiable(
				list(
					abstractTypeString,
					abstractTypeBoolean,
					subTypeInteger
				),
				subType.getFields() );
		
		//type hierarchy
		assertEquals(null, abstractType.getSupertype());
		assertEquals(abstractType, subType.getSupertype());
		
		assertEqualsUnmodifiable(
				list(
					subType
				),
				abstractType.getTypesOfInstances() );
		assertEqualsUnmodifiable(
				list(
					subType
				),
				subType.getTypesOfInstances() );
		
		
		//assignable
		assertTrue(abstractType.isAssignableFrom(subType));
		assertTrue(subType.isAssignableFrom(subType));
		assertTrue(abstractType.isAssignableFrom(abstractType));
		assertFalse(subType.isAssignableFrom(abstractType));
		
		final Type<?> abstractType2 = TypeInheritanceTestPatternItem.testPattern2.getAbstractType();
		final Type<?> subType2 = TypeInheritanceTestPatternItem.testPattern2.getSubType();
		assertFalse(abstractType.isAssignableFrom(subType2));
		assertFalse(abstractType2.isAssignableFrom(subType));
		assertFalse(abstractType.isAssignableFrom(abstractType2));
		assertFalse(subType.isAssignableFrom(subType2));
		
		//getID
		assertEquals("TypeInheritanceTestPatternItem.testPatternAbstractType", abstractType.getID());
		assertEquals("TypeInheritanceTestPatternItem.testPatternSubType", subType.getID());
		assertEquals("TypeInheritanceTestPatternItem.testPattern2AbstractType", abstractType2.getID());
		assertEquals("TypeInheritanceTestPatternItem.testPattern2SubType", subType2.getID());
		
		//creating instances
		try
		{
			abstractType.newItem(abstractTypeString.map("string1"), abstractTypeBoolean.map(Boolean.valueOf(true)));			
			fail();
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			//ok
		}
		
		final Item item = subType.newItem(abstractTypeString.map("string1"), abstractTypeBoolean.map(Boolean.valueOf(true)), subTypeInteger.map(1));
		deleteOnTearDown(item);
		
		//casting
		assertSame(item, abstractType.cast(item));
	}
}
