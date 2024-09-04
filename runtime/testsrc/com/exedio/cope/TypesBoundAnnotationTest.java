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

import static com.exedio.cope.AbstractRuntimeTest.assertTestAnnotation;
import static com.exedio.cope.AbstractRuntimeTest.assertTestAnnotation2;
import static com.exedio.cope.AbstractRuntimeTest.assertTestAnnotation2Null;
import static com.exedio.cope.AbstractRuntimeTest.assertTestAnnotationNull;
import static com.exedio.cope.RuntimeAssert.failingActivator;
import static com.exedio.cope.TypesBound.forClass;
import static com.exedio.cope.TypesBound.forClassUnchecked;
import static com.exedio.cope.TypesBound.newType;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.SetField;
import org.junit.jupiter.api.Test;

public class TypesBoundAnnotationTest
{
	@Test void testType()
	{
		try
		{
			forClass(AnItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("there is no type for class " + AnItem.class.getName(), e.getMessage());
		}
		try
		{
			forClassUnchecked(AnItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("there is no type for class " + AnItem.class.getName(), e.getMessage());
		}


		final Type<AnItem> type = newType(AnItem.class, failingActivator());

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

	@CopeName("AnItemAnn")
	@TestAnnotation("AnItemAnnVal")
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@CopeName("intFieldAnn")
		@TestAnnotation("intFieldAnnVal")
		@WrapperIgnore static final IntegerField intField = new IntegerField();

		@TestAnnotation2
		@WrapperIgnore static final BooleanField boolField = new BooleanField();

		@TestAnnotation("setFieldAnnVal")
		@WrapperIgnore static final SetField<String> setField = SetField.create(new StringField());

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
