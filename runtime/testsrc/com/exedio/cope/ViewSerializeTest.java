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
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static com.exedio.cope.tojunit.Assert.serialize;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.jupiter.api.Test;

public class ViewSerializeTest
{
	@Test void testField()
	{
		assertSerializedSame(field, 370);
		assertEquals("AnItem.field", field.toString());
	}

	@Test void testView()
	{
		assertSerializedSame(view,  369);
		assertEquals("AnItem.view", view.toString());
	}

	@Test void testViewNonMounted()
	{
		final UppercaseView feature = field.toUpperCase();
		assertEquals(asList(field), feature.getSources());
		assertSame  (       field , feature.getSources().get(0));
		assertEquals(TYPE, feature.getType());
		assertEquals("upper(AnItem.field)", feature.toString());

		final UppercaseView reserialized = reserialize(feature, 1247);
		assertNotSame(feature, reserialized);
		assertEquals(asList(field), reserialized.getSources());
		assertSame  (       field , reserialized.getSources().get(0));
		assertEquals(TYPE, reserialized.getType());
		assertEquals("upper(AnItem.field)", reserialized.toString());
	}

	@Test void testViewWithFieldNonMounted()
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

	@Test void testFieldNonMounted()
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

	@Test void testType()
	{
		assertEquals(asList(TYPE.getThis(), field, view), TYPE.getFeatures());
	}


	private static void assertSerializedSame(final Feature value, final int expectedSize)
	{
		assertSame(value, reserialize(value, expectedSize));
	}

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class AnItem extends Item
	{
		@WrapperIgnore static final StringField field = new StringField();
		@WrapperIgnore static final UppercaseView view = field.toUpperCase();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(ViewSerializeTest.class, "MODEL");
	}

	private static String toStringObject(final Object object)
	{
		return object.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(object));
	}
}
