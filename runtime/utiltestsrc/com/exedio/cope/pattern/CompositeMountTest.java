/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.Composite.getTemplateName;
import static java.lang.annotation.ElementType.FIELD;

import com.exedio.cope.IntegerField;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class CompositeMountTest extends CopeAssert
{
	public void testType()
	{
		try
		{
			Value.string4.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + valueName + "string4", e.getMessage());
		}
		try
		{
			Value.intMax4.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + valueName + "intMax4", e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			negative.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	public void testName()
	{
		try
		{
			Value.string4.getName();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + valueName + "string4", e.getMessage());
		}
		try
		{
			Value.intMax4.getName();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + valueName + "intMax4", e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			negative.getName();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	public void testTemplateName()
	{
		assertEquals("string4", getTemplateName(Value.string4));
		assertEquals("intMax4", getTemplateName(Value.intMax4));

		try
		{
			getTemplateName(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("template", e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			getTemplateName(negative);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a composite: " + negative, e.getMessage());
		}
	}

	public void testID()
	{
		try
		{
			Value.string4.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + valueName + "string4", e.getMessage());
		}
		try
		{
			Value.intMax4.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + valueName + "intMax4", e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			negative.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	public void testToString()
	{
		assertEquals(valueName + "string4", Value.string4.toString());
		assertEquals(valueName + "intMax4", Value.intMax4.toString());

		final LongField negative = new LongField();
		assertTrue(negative.toString().startsWith(LongField.class.getName() + '@'));
	}

	public void testGetAnnotation()
	{
		assertEquals("stringAnno", Value.string4.getAnnotation(Anno.class).value());
		assertEquals("intAnno", Value.intMax4.getAnnotation(Anno.class).value());
		assertNull(Value.string4.getAnnotation(Anno2.class));
		assertNull(Value.intMax4.getAnnotation(Anno2.class));

		final LongField negative = new LongField();
		try
		{
			negative.getAnnotation(Computed.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	public void testIsAnnotation()
	{
		assertTrue (Value.string4.isAnnotationPresent(Anno.class));
		assertTrue (Value.intMax4.isAnnotationPresent(Anno.class));
		assertFalse(Value.string4.isAnnotationPresent(Anno2.class));
		assertFalse(Value.intMax4.isAnnotationPresent(Anno2.class));

		final LongField negative = new LongField();
		try
		{
			negative.isAnnotationPresent(Computed.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	@Target(FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Anno
	{
		String value();
	}

	@Target(FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Anno2
	{
		String value();
	}

	public void testSerialization() throws IOException
	{
		assertSerializedSame(Value.string4, 285);
		assertSerializedSame(Value.intMax4, 285);

		final LongField negative = new LongField();
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(bos);
		try
		{
			oos.writeObject(negative);
		}
		catch(final NotSerializableException e)
		{
			assertEquals("com.exedio.cope.LongField", e.getMessage());
		}
	}

	private static void assertSerializedSame(final Serializable value, final int expectedSize)
	{
		assertSame(value, reserialize(value, expectedSize));
	}


	static final class Value extends Composite
	{
		@Anno("stringAnno")
		static final StringField string4 = new StringField().lengthMax(4);
		@Anno("intAnno")
		static final IntegerField intMax4 = new IntegerField().max(4);

		Value(final SetValue<?>[] setValues)
		{
			super(setValues);
		}

		private static final long serialVersionUID = 1l;
	}

	private static final String valueName = Value.class.getName() + '#';

	/**
	 * Needed to instantiate {@link CompositeType}.
	 */
	static final CompositeField<?> field = CompositeField.create(Value.class);
}
