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

import static com.exedio.cope.TypesBound.newType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.DataField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.Test;

public class UniqueHashedMediaErrorTest
{
	@Test public void testMediaNull()
	{
		try
		{
			new UniqueHashedMedia(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void testMediaOptional()
	{
		try
		{
			new UniqueHashedMedia(new Media().optional());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("mediaTemplate must be mandatory", e.getMessage());
		}
	}


	@Test public void testDigestDefault()
	{
		final UniqueHashedMedia m = new UniqueHashedMedia(new Media());
		assertEquals(128, m.getHash().getMinimumLength());
		assertEquals(128, m.getHash().getMaximumLength());
		assertEquals("SHA-512", m.getMessageDigestAlgorithm());
	}

	@Test public void testDigestOther()
	{
		final UniqueHashedMedia m = new UniqueHashedMedia(new Media(), "SHA-224");
		assertEquals(56, m.getHash().getMinimumLength());
		assertEquals(56, m.getHash().getMaximumLength());
		assertEquals("SHA-224", m.getMessageDigestAlgorithm());
	}

	@Test public void testDigestNull()
	{
		try
		{
			new UniqueHashedMedia(new Media(), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void testDigestWrong()
	{
		try
		{
			new UniqueHashedMedia(new Media(), "XXX");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String m = e.getMessage();
			assertTrue(m, m.startsWith("no such MessageDigest XXX,"));
		}
	}


	@Test public void testNonCreateableAbstract()
	{
		try
		{
			newType(AbstractItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"UniqueHashedMedia AbstractItem.value cannot create instances of type AbstractItem, " +
					"because it is abstract.",
					e.getMessage());
		}
	}
	@SuppressWarnings({"ClassWithOnlyPrivateConstructors", "AbstractClassNeverImplemented"}) // OK: test broken input
	@WrapperIgnore abstract static class AbstractItem extends Item
	{
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media());

		private static final long serialVersionUID = 1l;
		private AbstractItem(final ActivationParameters ap) { super(ap); }
	}


	@Test public void testNonCreateableFunctionField()
	{
		try
		{
			new Model(NonCreateableFunctionFieldItem.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"UniqueHashedMedia NonCreateableFunctionFieldItem.value cannot create instances of type NonCreateableFunctionFieldItem, " +
					"because NonCreateableFunctionFieldItem.field is mandatory and has no default.",
					e.getMessage());
		}
	}
	@WrapperIgnore static final class NonCreateableFunctionFieldItem extends Item
	{
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media());
		static final IntegerField field = new IntegerField();
		static final Type<NonCreateableFunctionFieldItem> TYPE =
				TypesBound.newType(NonCreateableFunctionFieldItem.class);

		private static final long serialVersionUID = 1l;
		private NonCreateableFunctionFieldItem(final ActivationParameters ap) { super(ap); }
	}


	@Test public void testNonCreateableDataField()
	{
		try
		{
			new Model(NonCreateableDataFieldItem.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"UniqueHashedMedia NonCreateableDataFieldItem.value cannot create instances of type NonCreateableDataFieldItem, " +
					"because NonCreateableDataFieldItem.field is mandatory and has no default.",
					e.getMessage());
		}
	}
	@WrapperIgnore static final class NonCreateableDataFieldItem extends Item
	{
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media());
		static final DataField field = new DataField();
		static final Type<NonCreateableDataFieldItem> TYPE =
				TypesBound.newType(NonCreateableDataFieldItem.class);

		private static final long serialVersionUID = 1l;
		private NonCreateableDataFieldItem(final ActivationParameters ap) { super(ap); }
	}


	@Test public void testCreateable()
	{
		// test, that is does not throw an exception
		new Model(CreateableItem.TYPE);
	}
	@WrapperIgnore static final class CreateableItem extends Item
	{
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media());
		static final IntegerField optionalField = new IntegerField().optional();
		static final IntegerField defaultField = new IntegerField().defaultTo(77);
		static final DataField dataField = new DataField().optional();
		static final Type<CreateableItem> TYPE = TypesBound.newType(CreateableItem.class);

		private static final long serialVersionUID = 1l;
		private CreateableItem(final ActivationParameters ap) { super(ap); }
	}
}
