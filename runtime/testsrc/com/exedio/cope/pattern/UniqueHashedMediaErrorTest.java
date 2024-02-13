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
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.DataField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.IllegalAlgorithmException;
import org.junit.jupiter.api.Test;

public class UniqueHashedMediaErrorTest
{
	@Test void testMediaNull()
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

	@Test void testMediaOptional()
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


	@Test void testDigestDefault()
	{
		final UniqueHashedMedia m = new UniqueHashedMedia(new Media());
		assertEquals(128, m.getHash().getMinimumLength());
		assertEquals(128, m.getHash().getMaximumLength());
		assertEquals("SHA-512", m.getMessageDigestAlgorithm());
	}

	@Test void testDigestOther()
	{
		final UniqueHashedMedia m = new UniqueHashedMedia(new Media(), "SHA-224");
		assertEquals(56, m.getHash().getMinimumLength());
		assertEquals(56, m.getHash().getMaximumLength());
		assertEquals("SHA-224", m.getMessageDigestAlgorithm());
	}

	@Test void testDigestNull()
	{
		try
		{
			new UniqueHashedMedia(new Media(), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("algorithm", e.getMessage());
		}
	}

	@Test void testDigestWrong()
	{
		try
		{
			new UniqueHashedMedia(new Media(), "XXX");
			fail();
		}
		catch(final IllegalAlgorithmException e)
		{
			assertEquals("XXX", e.getAlgorithm());
		}
	}


	@Test void testNonCreateableAbstract()
	{
		try
		{
			newType(AbstractItem.class, null);
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
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings({"AbstractClassNeverImplemented", "unused"}) // OK: test bad API usage
	private abstract static class AbstractItem extends Item
	{
		@WrapperIgnore
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media());

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 2l;

		@com.exedio.cope.instrument.Generated
		protected AbstractItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void testNonCreateableFunctionField()
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
					"because NonCreateableFunctionFieldItem.field is mandatory and has no default. May be suppressed by @SuppressCreateInstanceCheck.",
					e.getMessage());
		}
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false,
			typeSuppressWarnings="UnnecessarilyQualifiedStaticallyImportedElement")
	@SuppressWarnings("unused") // OK: test bad API usage
	private static final class NonCreateableFunctionFieldItem extends Item
	{
		@WrapperIgnore
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media());
		@WrapperIgnore
		static final IntegerField field = new IntegerField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement")
		private static final com.exedio.cope.Type<NonCreateableFunctionFieldItem> TYPE = com.exedio.cope.TypesBound.newType(NonCreateableFunctionFieldItem.class,NonCreateableFunctionFieldItem::new);

		@com.exedio.cope.instrument.Generated
		private NonCreateableFunctionFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void testNonCreateableDataField()
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
					"because NonCreateableDataFieldItem.field is mandatory and has no default. May be suppressed by @SuppressCreateInstanceCheck.",
					e.getMessage());
		}
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false,
			typeSuppressWarnings="UnnecessarilyQualifiedStaticallyImportedElement")
	@SuppressWarnings("unused") // OK: test bad API usage
	private static final class NonCreateableDataFieldItem extends Item
	{
		@WrapperIgnore
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media());
		@WrapperIgnore
		static final DataField field = new DataField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement")
		private static final com.exedio.cope.Type<NonCreateableDataFieldItem> TYPE = com.exedio.cope.TypesBound.newType(NonCreateableDataFieldItem.class,NonCreateableDataFieldItem::new);

		@com.exedio.cope.instrument.Generated
		private NonCreateableDataFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void testCreateable()
	{
		// test, that is does not throw an exception
		new Model(CreateableItem.TYPE);
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false,
			typeSuppressWarnings="UnnecessarilyQualifiedStaticallyImportedElement")
	private static final class CreateableItem extends Item
	{
		@WrapperIgnore
		@SuppressWarnings("unused")
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media());
		@WrapperIgnore
		@SuppressWarnings("unused")
		static final IntegerField optionalField = new IntegerField().optional();
		@WrapperIgnore
		@SuppressWarnings("unused")
		static final IntegerField defaultField = new IntegerField().defaultTo(77);
		@WrapperIgnore
		@SuppressWarnings("unused")
		static final DataField dataField = new DataField().optional();
		@WrapperIgnore
		@SuppressWarnings("unused")
		@UniqueHashedMedia.SuppressCreateInstanceCheck
		static final IntegerField suppressedField = new IntegerField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement")
		private static final com.exedio.cope.Type<CreateableItem> TYPE = com.exedio.cope.TypesBound.newType(CreateableItem.class,CreateableItem::new);

		@com.exedio.cope.instrument.Generated
		private CreateableItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
