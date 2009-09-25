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
import com.exedio.cope.pattern.SetField;
import com.exedio.cope.util.ReactivationConstructorDummy;

public class TypeAnnotationTest extends CopeAssert
{
	public void testType()
	{
		try
		{
			Type.forClass(AnItem.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.TypeAnnotationTest$AnItem", e.getMessage());
		}
		try
		{
			Type.forClassUnchecked(AnItem.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.TypeAnnotationTest$AnItem", e.getMessage());
		}
		
		
		final Type<AnItem> type = Item.newType(AnItem.class);
		
		assertSame(type, Type.forClass(AnItem.class));
		assertSame(type, Type.forClassUnchecked(AnItem.class));
		assertEquals("AnItemAnn", type.getID());
		assertEquals(AnItem.class, type.getJavaClass());
		assertEquals(true, type.isJavaClassExclusive());

		assertSame(type, type.getThis().getType());
		assertEquals("AnItemAnn.this", type.getThis().getID());
		assertEquals("AnItemAnn.this", type.getThis().toString());
		assertEquals("this", type.getThis().getName());
		
		assertEquals("intFieldAnn", AnItem.intField.getName());
		assertEquals(null, type.getFeature("intField"));
		assertEquals("boolField", AnItem.boolField.getName());
		assertSame(AnItem.intField, type.getFeature("intFieldAnn"));
		assertSame(AnItem.boolField, type.getFeature("boolField"));
		assertSame(AnItem.intField, type.getDeclaredFeature("intFieldAnn"));
		assertSame(AnItem.boolField, type.getDeclaredFeature("boolField"));
		
		assertEquals("AnItemAnnVal", type.getAnnotation(TestAnnotation.class).value());
		assertEquals(null,           type.getAnnotation(TestAnnotation2.class));
		assertEquals("intFieldAnnVal",  AnItem.intField.getAnnotation(TestAnnotation.class).value());
		assertEquals(null,              AnItem.intField.getAnnotation(TestAnnotation2.class));
		assertEquals(null,              AnItem.boolField.getAnnotation(TestAnnotation.class));
		assertNotNull(                  AnItem.boolField.getAnnotation(TestAnnotation2.class));
		assertEquals("setFieldAnnVal",  AnItem.setField.getAnnotation(TestAnnotation.class).value());
		assertEquals(null, AnItem.setField.getRelationType().getAnnotation(TestAnnotation.class));
		assertEquals(null, AnItem.setField.getElement().getAnnotation(TestAnnotation.class));
	}
	
	@CopeID("AnItemAnn")
	@TestAnnotation("AnItemAnnVal")
	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;

		private AnItem(final SetValue[] setValues)
		{
			super(setValues);
		}
		
		private AnItem(final ReactivationConstructorDummy reactivationDummy, final int pk)
		{
			super(reactivationDummy, pk);
		}
		
		@CopeID("intFieldAnn")
		@TestAnnotation("intFieldAnnVal")
		static final IntegerField intField = new IntegerField();
		
		@TestAnnotation2
		static final BooleanField boolField = new BooleanField();
		
		@TestAnnotation("setFieldAnnVal")
		static final SetField<String> setField = SetField.newSet(new StringField());
	}
}
