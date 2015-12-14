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

import static com.exedio.cope.ViewSerializeTest.AnItem.TYPE;
import static com.exedio.cope.ViewSerializeTest.AnItem.field;
import static com.exedio.cope.ViewSerializeTest.AnItem.view;
import static com.exedio.cope.junit.CopeAssert.reserialize;
import static com.exedio.cope.junit.CopeAssert.serialize;
import static java.util.Arrays.asList;

import junit.framework.TestCase;
import org.junit.Test;

public class ViewSerializeTest extends TestCase
{
	@Test public void testField()
	{
		assertSerializedSame(field, 370);
		assertEquals("AnItem.field", field.toString());
	}

	@Test public void testView()
	{
		assertSerializedSame(view,  369);
		assertEquals("AnItem.view", view.toString());
	}

	@Test public void testViewNonMounted()
	{
		final UppercaseView feature = field.toUpperCase();
		assertEquals(asList(field), feature.getSources());
		assertSame  (       field , feature.getSources().get(0));
		assertEquals(TYPE, feature.getType());
		assertEquals("upper(AnItem.field)", feature.toString());

		final UppercaseView reserialized = reserialize(feature, 1147);
		assertNotSame(feature, reserialized);
		assertEquals(asList(field), reserialized.getSources());
		assertSame  (       field , reserialized.getSources().get(0));
		assertEquals(TYPE, reserialized.getType());
		assertEquals("upper(AnItem.field)", reserialized.toString());
	}

	@Test public void testViewWithFieldNonMounted()
	{
		final StringField source = new StringField();
		final UppercaseView feature = source.toUpperCase();
		try
		{
			serialize(feature);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("java.io.NotSerializableException: " + StringField.class.getName(), e.getMessage());
		}
		assertEquals("upper(" + toStringObject(source) + ")", feature.toString());
	}

	@Test public void testFieldNonMounted()
	{
		final StringField feature = new StringField();
		try
		{
			serialize(feature);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("java.io.NotSerializableException: " + StringField.class.getName(), e.getMessage());
		}
		assertEquals(toStringObject(feature), feature.toString());
	}

	@Test public void testType()
	{
		assertEquals(asList(TYPE.getThis(), field, view), TYPE.getFeatures());
	}


	private static final void assertSerializedSame(final Feature value, final int expectedSize)
	{
		assertSame(value, reserialize(value, expectedSize));
	}

	static final class AnItem extends Item
	{
		static final StringField field = new StringField();
		static final UppercaseView view = field.toUpperCase();

		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private AnItem(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
	}

	private static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(ViewSerializeTest.class, "MODEL");
	}

	private static String toStringObject(final Object object)
	{
		return object.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(object));
	}
}
