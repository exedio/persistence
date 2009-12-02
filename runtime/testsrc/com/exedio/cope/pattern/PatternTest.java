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
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestAnnotation;
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
		// type
		assertEqualsUnmodifiable(
				list(
					PatternTestItem.TYPE.getThis(),
					PatternTestItem.testPattern,
					PatternTestItem.testPattern2
				),
				PatternTestItem.TYPE.getFeatures());
		
		assertSame(PatternTestItem.testPattern, PatternTestItem.TYPE.getFeature("testPattern"));
		assertSame(PatternTestItem.testPattern2, PatternTestItem.TYPE.getFeature("testPattern2"));
		
		assertEqualsUnmodifiable(list(
				), PatternTestItem.testPattern.getSourceFields());
		assertEqualsUnmodifiable(list(
				), PatternTestItem.testPattern2.getSourceFields());
		
		// superType
		final Type<?> superType = PatternTestItem.testPattern.getSuperType();
		assertSame(PatternTestItem.testPattern.superTypeString, superType.getFeature(PatternTestPattern.SUPER_TYPE_STRING));
		assertSame(PatternTestItem.testPattern.superTypeBoolean, superType.getFeature(PatternTestPattern.SUPER_TYPE_BOOLEAN));

		// subType
		final Type<?> subType = PatternTestItem.testPattern.getSubType();
		assertSame(PatternTestItem.testPattern.subTypeInteger, subType.getFeature(PatternTestPattern.SUBTYPE_INTEGER));
		
		assertSame(PatternItem.class, superType.getJavaClass());
		assertSame(PatternItem.class, subType.getJavaClass());
		assertEquals(false, superType.isBound());
		assertEquals(false, subType.isBound());
		assertEquals(PatternTestItem.testPattern, superType.getPattern());
		assertEquals(PatternTestItem.testPattern, subType.getPattern());
		
		assertEqualsUnmodifiable(
				list(
					superType.getThis(),
					PatternTestItem.testPattern.superTypeString,
					PatternTestItem.testPattern.superTypeBoolean
				),
				superType.getFeatures());
		
		assertTrue(superType.isAbstract());
		assertFalse(subType.isAbstract());
		
		assertEqualsUnmodifiable(
				list(
					subType.getThis(),
					PatternTestItem.testPattern.superTypeString,
					PatternTestItem.testPattern.superTypeBoolean,
					PatternTestItem.testPattern.subTypeInteger
				),
				subType.getFeatures());
		
		//type hierarchy
		assertEquals(null, superType.getSupertype());
		assertEquals(superType, subType.getSupertype());
		
		assertEqualsUnmodifiable(list(subType), superType.getSubtypes());
		assertEqualsUnmodifiable(list(), subType.getSubtypes());
		assertEqualsUnmodifiable(list(superType, subType), superType.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(subType), subType.getSubtypesTransitively());
		assertEqualsUnmodifiable(
				list(
					subType
				),
				superType.getTypesOfInstances() );
		assertEqualsUnmodifiable(
				list(
					subType
				),
				subType.getTypesOfInstances() );
		
		
		//assignable
		try
		{
			superType.isAssignableFrom(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		assertTrue(superType.isAssignableFrom(subType));
		assertTrue(subType.isAssignableFrom(subType));
		assertTrue(superType.isAssignableFrom(superType));
		assertFalse(subType.isAssignableFrom(superType));
		
		final Type<?> superType2 = PatternTestItem.testPattern2.getSuperType();
		final Type<?> subType2 = PatternTestItem.testPattern2.getSubType();
		assertFalse(superType.isAssignableFrom(subType2));
		assertFalse(superType2.isAssignableFrom(subType));
		assertFalse(superType.isAssignableFrom(superType2));
		assertFalse(subType.isAssignableFrom(subType2));
		
		//getID
		assertEquals("PatternTestItem.testPatternUperType", superType.getID());
		assertEquals("PatternTestItem.testPatternSubType", subType.getID());
		assertEquals("PatternTestItem.testPattern2UperType", superType2.getID());
		assertEquals("PatternTestItem.testPattern2SubType", subType2.getID());
		
		//creating instances
		try
		{
			superType.newItem(
					PatternTestItem.testPattern.superTypeString.map("string1"),
					PatternTestItem.testPattern.superTypeBoolean.map(Boolean.valueOf(true)));			
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("cannot create item of abstract type PatternTestItem.testPatternUperType", e.getMessage());
		}
		
		final Item item = subType.newItem(
				PatternTestItem.testPattern.superTypeString.map("string1"),
				PatternTestItem.testPattern.superTypeBoolean.map(Boolean.valueOf(true)),
				PatternTestItem.testPattern.subTypeInteger.map(1));
		deleteOnTearDown(item);
		
		//casting
		assertSame(item, superType.cast(item));
		
		// getAnnotation
		assertNull(PatternTestItem.testPattern.getAnnotation(TestAnnotation.class));
		assertNull(PatternTestItem.testPattern.superTypeString.getAnnotation(TestAnnotation.class));
		assertNull(PatternTestItem.testPattern.superTypeBoolean.getAnnotation(TestAnnotation.class));
		assertNull(PatternTestItem.testPattern.subTypeInteger.getAnnotation(TestAnnotation.class));
		assertNull(superType.getAnnotation(TestAnnotation.class));
		assertNull(subType.getAnnotation(TestAnnotation.class));
	}
}
