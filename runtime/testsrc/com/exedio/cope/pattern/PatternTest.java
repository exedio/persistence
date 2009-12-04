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
					PatternTestItem.testPattern.ownString,
					PatternTestItem.testPattern.ownInt,
					PatternTestItem.testPattern.getOwnItem(),
					PatternTestItem.testPattern2,
					PatternTestItem.testPattern2.ownString,
					PatternTestItem.testPattern2.ownInt,
					PatternTestItem.testPattern2.getOwnItem()
				),
				PatternTestItem.TYPE.getFeatures());
		
		assertSame(PatternTestItem.testPattern, PatternTestItem.TYPE.getFeature("testPattern"));
		assertSame(PatternTestItem.testPattern2, PatternTestItem.TYPE.getFeature("testPattern2"));
		
		assertEqualsUnmodifiable(list(
					PatternTestItem.testPattern.ownString,
					PatternTestItem.testPattern.ownInt,
					PatternTestItem.testPattern.getOwnItem()
				), PatternTestItem.testPattern.getSourceFields());
		assertEqualsUnmodifiable(list(
					PatternTestItem.testPattern2.ownString,
					PatternTestItem.testPattern2.ownInt,
					PatternTestItem.testPattern2.getOwnItem()
				), PatternTestItem.testPattern2.getSourceFields());
		
		assertSame(PatternTestItem.testPattern, PatternTestItem.testPattern.ownString.getPattern());
		assertSame(PatternTestItem.testPattern, PatternTestItem.testPattern.ownInt.getPattern());
		assertSame(PatternTestItem.testPattern, PatternTestItem.testPattern.getOwnItem().getPattern());
		assertSame(PatternTestItem.testPattern2, PatternTestItem.testPattern2.ownString.getPattern());
		assertSame(PatternTestItem.testPattern2, PatternTestItem.testPattern2.ownInt.getPattern());
		assertSame(PatternTestItem.testPattern2, PatternTestItem.testPattern2.getOwnItem().getPattern());
		
		assertSame(PatternTestItem.TYPE, PatternTestItem.testPattern.ownString    .getType());
		assertSame(PatternTestItem.TYPE, PatternTestItem.testPattern.ownInt       .getType());
		assertSame(PatternTestItem.TYPE, PatternTestItem.testPattern.getOwnItem() .getType());
		assertSame(PatternTestItem.TYPE, PatternTestItem.testPattern2.ownString   .getType());
		assertSame(PatternTestItem.TYPE, PatternTestItem.testPattern2.ownInt      .getType());
		assertSame(PatternTestItem.TYPE, PatternTestItem.testPattern2.getOwnItem().getType());
		
		assertSame("testPatternOwnString" , PatternTestItem.testPattern.ownString    .getName());
		assertSame("testPatternOwnInt"    , PatternTestItem.testPattern.ownInt       .getName());
		assertSame("testPatternOwnItem"   , PatternTestItem.testPattern.getOwnItem() .getName());
		assertSame("testPattern2OwnString", PatternTestItem.testPattern2.ownString   .getName());
		assertSame("testPattern2OwnInt"   , PatternTestItem.testPattern2.ownInt      .getName());
		assertSame("testPattern2OwnItem"  , PatternTestItem.testPattern2.getOwnItem().getName());
		
		assertSame("PatternTestItem.testPatternOwnString" , PatternTestItem.testPattern.ownString    .getID());
		assertSame("PatternTestItem.testPatternOwnInt"    , PatternTestItem.testPattern.ownInt       .getID());
		assertSame("PatternTestItem.testPatternOwnItem"   , PatternTestItem.testPattern.getOwnItem() .getID());
		assertSame("PatternTestItem.testPattern2OwnString", PatternTestItem.testPattern2.ownString   .getID());
		assertSame("PatternTestItem.testPattern2OwnInt"   , PatternTestItem.testPattern2.ownInt      .getID());
		assertSame("PatternTestItem.testPattern2OwnItem"  , PatternTestItem.testPattern2.getOwnItem().getID());
		
		assertTestAnnotationNull(PatternTestItem.testPattern.ownString);
		assertTestAnnotationNull(PatternTestItem.testPattern2.ownString);
		assertTestAnnotation("ownIntAnn",  PatternTestItem.testPattern.ownInt);
		assertTestAnnotation("ownItemAnn", PatternTestItem.testPattern.getOwnItem());
		assertTestAnnotation("ownIntAnn",  PatternTestItem.testPattern2.ownInt);
		assertTestAnnotation("ownItemAnn", PatternTestItem.testPattern2.getOwnItem());
		
		// superType
		final Type<PatternTestTypeItem> superType = PatternTestItem.testPattern.getSuperType();
		assertSame(PatternTestItem.testPattern.superTypeString, superType.getFeature(PatternTestPattern.SUPER_TYPE_STRING));
		assertSame(PatternTestItem.testPattern.superTypeBoolean, superType.getFeature(PatternTestPattern.SUPER_TYPE_BOOLEAN));

		// subType
		final Type<PatternTestTypeItem> subType = PatternTestItem.testPattern.getSubType();
		assertSame(PatternTestItem.testPattern.subTypeInteger, subType.getFeature(PatternTestPattern.SUBTYPE_INTEGER));
		
		assertSame(PatternTestTypeItem.class, superType.getJavaClass());
		assertSame(PatternTestTypeItem.class, subType.getJavaClass());
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
		assertTestAnnotationNull(PatternTestItem.testPattern);
		assertTestAnnotation("superTypeStringAnn", PatternTestItem.testPattern.superTypeString);
		assertTestAnnotationNull(PatternTestItem.testPattern.superTypeBoolean);
		assertTestAnnotation("subTypeIntegerAnn", PatternTestItem.testPattern.subTypeInteger);
		assertTestAnnotation("PatternTestTypeItemAnn", superType);
		assertTestAnnotation("PatternTestTypeItemAnn", subType);
	}
}
