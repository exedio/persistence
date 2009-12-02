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

public class PatternTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(PatternTestItem.TYPE);
	
	public PatternTest()
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
		final Type<?> abstractType = PatternTestItem.testPattern.getAbstractType();
		final StringField superTypeString = (StringField)abstractType.getFeature(PatternTestPattern.ABSTRACTTYPE_STRING);
		final BooleanField superTypeBoolean = (BooleanField)abstractType.getFeature(PatternTestPattern.ABSTRACTTYPE_BOOLEAN);

		//sub type
		final Type<?> subType = PatternTestItem.testPattern.getSubType();
		final IntegerField subTypeInteger = (IntegerField)subType.getFeature(PatternTestPattern.SUBTYPE_INTEGER);
		
		assertSame(PatternItem.class, abstractType.getJavaClass());
		assertSame(PatternItem.class, subType.getJavaClass());
		assertEquals(false, abstractType.isBound());
		assertEquals(false, subType.isBound());
		assertEquals(PatternTestItem.testPattern, abstractType.getPattern());
		assertEquals(PatternTestItem.testPattern, subType.getPattern());
		
		assertEqualsUnmodifiable(
				list(
					superTypeString,
					superTypeBoolean
				),
				abstractType.getFields() );
		
		assertTrue(abstractType.isAbstract());
		assertFalse(subType.isAbstract());
		
		assertEqualsUnmodifiable(
				list(
					superTypeString,
					superTypeBoolean,
					subTypeInteger
				),
				subType.getFields() );
		
		//type hierarchy
		assertEquals(null, abstractType.getSupertype());
		assertEquals(abstractType, subType.getSupertype());
		
		assertEqualsUnmodifiable(list(subType), abstractType.getSubtypes());
		assertEqualsUnmodifiable(list(), subType.getSubtypes());
		assertEqualsUnmodifiable(list(abstractType, subType), abstractType.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(subType), subType.getSubtypesTransitively());
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
		try
		{
			abstractType.isAssignableFrom(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		assertTrue(abstractType.isAssignableFrom(subType));
		assertTrue(subType.isAssignableFrom(subType));
		assertTrue(abstractType.isAssignableFrom(abstractType));
		assertFalse(subType.isAssignableFrom(abstractType));
		
		final Type<?> abstractType2 = PatternTestItem.testPattern2.getAbstractType();
		final Type<?> subType2 = PatternTestItem.testPattern2.getSubType();
		assertFalse(abstractType.isAssignableFrom(subType2));
		assertFalse(abstractType2.isAssignableFrom(subType));
		assertFalse(abstractType.isAssignableFrom(abstractType2));
		assertFalse(subType.isAssignableFrom(subType2));
		
		//getID
		assertEquals("PatternTestItem.testPatternAbstractType", abstractType.getID());
		assertEquals("PatternTestItem.testPatternSubType", subType.getID());
		assertEquals("PatternTestItem.testPattern2AbstractType", abstractType2.getID());
		assertEquals("PatternTestItem.testPattern2SubType", subType2.getID());
		
		//creating instances
		try
		{
			abstractType.newItem(
					superTypeString.map("string1"),
					superTypeBoolean.map(Boolean.valueOf(true)));			
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("cannot create item of abstract type PatternTestItem.testPatternAbstractType", e.getMessage());
		}
		
		final Item item = subType.newItem(
				superTypeString.map("string1"),
				superTypeBoolean.map(Boolean.valueOf(true)),
				subTypeInteger.map(1));
		deleteOnTearDown(item);
		
		//casting
		assertSame(item, abstractType.cast(item));
	}
}
