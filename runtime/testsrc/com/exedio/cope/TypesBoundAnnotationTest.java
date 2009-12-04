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

import static com.exedio.cope.AbstractRuntimeTest.assertTestAnnotation;
import static com.exedio.cope.AbstractRuntimeTest.assertTestAnnotation2;
import static com.exedio.cope.AbstractRuntimeTest.assertTestAnnotation2Null;
import static com.exedio.cope.AbstractRuntimeTest.assertTestAnnotationNull;
import static com.exedio.cope.TypesBound.forClass;
import static com.exedio.cope.TypesBound.forClassUnchecked;
import static com.exedio.cope.TypesBound.newType;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.pattern.SetField;

public class TypesBoundAnnotationTest extends CopeAssert
{
	public void testType()
	{
		try
		{
			forClass(AnItem.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class " + AnItem.class.getName(), e.getMessage());
		}
		try
		{
			forClassUnchecked(AnItem.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class " + AnItem.class.getName(), e.getMessage());
		}
		
		
		final Type<AnItem> type = newType(AnItem.class);
		
		assertSame(type, forClass(AnItem.class));
		assertSame(type, forClassUnchecked(AnItem.class));
		assertEquals("AnItemAnn", type.getID());
		assertEquals(AnItem.class, type.getJavaClass());
		assertEquals(true, type.isBound());

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
		
		assertTestAnnotation("AnItemAnnVal", type);
		assertTestAnnotation2Null(type);
		assertTestAnnotation("intFieldAnnVal",  AnItem.intField);
		assertTestAnnotation2Null(AnItem.intField);
		assertTestAnnotationNull(AnItem.boolField);
		assertTestAnnotation2(AnItem.boolField);
		assertTestAnnotation("setFieldAnnVal",  AnItem.setField);
		assertTestAnnotationNull(AnItem.setField.getRelationType());
		assertTestAnnotationNull(AnItem.setField.getElement());
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
		
		private AnItem(final ActivationParameters ap)
		{
			super(ap);
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
