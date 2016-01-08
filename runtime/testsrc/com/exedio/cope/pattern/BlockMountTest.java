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

package com.exedio.cope.pattern;

import static com.exedio.cope.tojunit.Assert.reserialize;
import static java.lang.annotation.ElementType.FIELD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.IntegerField;
import com.exedio.cope.LongField;
import com.exedio.cope.StringField;
import com.exedio.cope.misc.Computed;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Test;

public class BlockMountTest
{
	@Test public void testType()
	{
		try
		{
			MyBlock.string4.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + blockName + "string4", e.getMessage());
		}
		try
		{
			MyBlock.intMax4.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + blockName + "intMax4", e.getMessage());
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

	@Test public void testName()
	{
		try
		{
			MyBlock.string4.getName();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + blockName + "string4", e.getMessage());
		}
		try
		{
			MyBlock.intMax4.getName();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + blockName + "intMax4", e.getMessage());
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

	@Test public void testID()
	{
		try
		{
			MyBlock.string4.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + blockName + "string4", e.getMessage());
		}
		try
		{
			MyBlock.intMax4.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a type: " + blockName + "intMax4", e.getMessage());
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

	@Test public void testToString()
	{
		assertEquals(blockName + "string4", MyBlock.string4.toString());
		assertEquals(blockName + "intMax4", MyBlock.intMax4.toString());

		final LongField negative = new LongField();
		assertTrue(negative.toString().startsWith(LongField.class.getName() + '@'));
	}

	@Test public void testGetAnnotation()
	{
		assertEquals("stringAnno", MyBlock.string4.getAnnotation(Anno.class).value());
		assertEquals("intAnno", MyBlock.intMax4.getAnnotation(Anno.class).value());
		assertNull(MyBlock.string4.getAnnotation(Anno2.class));
		assertNull(MyBlock.intMax4.getAnnotation(Anno2.class));

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

	@Test public void testIsAnnotation()
	{
		assertTrue (MyBlock.string4.isAnnotationPresent(Anno.class));
		assertTrue (MyBlock.intMax4.isAnnotationPresent(Anno.class));
		assertFalse(MyBlock.string4.isAnnotationPresent(Anno2.class));
		assertFalse(MyBlock.intMax4.isAnnotationPresent(Anno2.class));

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

	@Test public void testSerialization() throws IOException
	{
		assertSerializedSame(MyBlock.string4, 327);
		assertSerializedSame(MyBlock.intMax4, 327);

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


	static final class MyBlock extends Block
	{
		@Anno("stringAnno")
		static final StringField string4 = new StringField().lengthMax(4);
		@Anno("intAnno")
		static final IntegerField intMax4 = new IntegerField().max(4);

		private static final long serialVersionUID = 1l;
		static final BlockType<?> TYPE = BlockType.newType(MyBlock.class);
		private MyBlock(final BlockActivationParameters ap) { super(ap); }
	}

	private static final String blockName = MyBlock.class.getName() + '#';
}
