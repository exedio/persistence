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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.DataField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperType;
import java.util.function.Supplier;
import org.junit.Test;

public class HashConstraintTest
{
	@Test public void testGettersLiteral()
	{
		assertSame(AnItem.hash, AnItem.constraintLiteral.getHash());
		assertEquals("ALGO-LITERAL", AnItem.constraintLiteral.getAlgorithm());
		assertSame(AnItem.data, AnItem.constraintLiteral.getData());
	}

	@Test public void testGettersSupplier()
	{
		assertSame(AnItem.hash, AnItem.constraintSupplier.getHash());
		assertEquals("ALGO-SUPPLIER", AnItem.constraintSupplier.getAlgorithm());
		assertSame(AnItem.data, AnItem.constraintSupplier.getData());
	}

	@Test public void testFeature()
	{
		assertEquals("constraintLiteral", AnItem.constraintLiteral.getName());
		assertEquals("AnItem.constraintLiteral", AnItem.constraintLiteral.getID());
		assertSame(AnItem.TYPE, AnItem.constraintLiteral.getType());
	}

	@Test public void testHashMatches()
	{
		assertEquals(  "AnItem.hash=ALGO-LITERAL(AnItem.data)"  , AnItem.constraintLiteral .hashMatchesIfSupported     ().toString());
		assertEquals("!(AnItem.hash=ALGO-LITERAL(AnItem.data))" , AnItem.constraintLiteral .hashDoesNotMatchIfSupported().toString());
		assertEquals(  "AnItem.hash=ALGO-SUPPLIER(AnItem.data)" , AnItem.constraintSupplier.hashMatchesIfSupported     ().toString());
		assertEquals("!(AnItem.hash=ALGO-SUPPLIER(AnItem.data))", AnItem.constraintSupplier.hashDoesNotMatchIfSupported().toString());
	}

	@Test public void testHashNullString()
	{
		try
		{
			new HashConstraint(null, (String)null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("hash", e.getMessage());
		}
	}

	@Test public void testHashNullSupplier()
	{
		try
		{
			new HashConstraint(null, (Supplier<String>)null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("hash", e.getMessage());
		}
	}

	@Test public void testAlgorithmNullString()
	{
		final StringField hash = new StringField();
		final DataField data = new DataField();
		try
		{
			new HashConstraint(hash, (String)null, data);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("algorithm", e.getMessage());
		}
	}

	@Test public void testAlgorithmNullSupplier()
	{
		final StringField hash = new StringField();
		try
		{
			new HashConstraint(hash, (Supplier<String>)null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("algorithm", e.getMessage());
		}
	}

	@Test public void testDataNullString()
	{
		final StringField hash = new StringField();
		try
		{
			new HashConstraint(hash, "X", null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("data", e.getMessage());
		}
	}

	@Test public void testDataNullSupplier()
	{
		final StringField hash = new StringField();
		try
		{
			new HashConstraint(hash, () -> "X", null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("data", e.getMessage());
		}
	}


	@WrapperType(indent=2)
	static final class AnItem extends Item
	{
		static final StringField hash = new StringField();
		static final DataField data = new DataField();
		static final HashConstraint constraintLiteral  = new HashConstraint(hash, "ALGO-LITERAL", data);
		static final HashConstraint constraintSupplier = new HashConstraint(hash, () -> "ALGO-SUPPLIER", data);



		/**
		 * Creates a new AnItem with all the fields initially needed.
		 * @param hash the initial value for field {@link #hash}.
		 * @param data the initial value for field {@link #data}.
		 * @throws com.exedio.cope.MandatoryViolationException if hash, data is null.
		 * @throws com.exedio.cope.StringLengthViolationException if hash violates its length constraint.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		AnItem(
					@javax.annotation.Nonnull final java.lang.String hash,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value data)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnItem.hash.map(hash),
				AnItem.data.map(data),
			});
		}

		/**
		 * Creates a new AnItem and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private AnItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #hash}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		final java.lang.String getHash()
		{
			return AnItem.hash.get(this);
		}

		/**
		 * Sets a new value for {@link #hash}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setHash(@javax.annotation.Nonnull final java.lang.String hash)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			AnItem.hash.set(this,hash);
		}

		/**
		 * Returns, whether there is no data for field {@link #data}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
		final boolean isDataNull()
		{
			return AnItem.data.isNull(this);
		}

		/**
		 * Returns the length of the data of the data field {@link #data}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
		final long getDataLength()
		{
			return AnItem.data.getLength(this);
		}

		/**
		 * Returns the value of the persistent field {@link #data}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getArray")
		@javax.annotation.Nullable
		final byte[] getDataArray()
		{
			return AnItem.data.getArray(this);
		}

		/**
		 * Writes the data of this persistent data field into the given stream.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		final void getData(@javax.annotation.Nonnull final java.io.OutputStream data)
				throws
					java.io.IOException
		{
			AnItem.data.get(this,data);
		}

		/**
		 * Writes the data of this persistent data field into the given file.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		final void getData(@javax.annotation.Nonnull final java.io.File data)
				throws
					java.io.IOException
		{
			AnItem.data.get(this,data);
		}

		/**
		 * Sets a new value for the persistent field {@link #data}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setData(@javax.annotation.Nonnull final com.exedio.cope.DataField.Value data)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			AnItem.data.set(this,data);
		}

		/**
		 * Sets a new value for the persistent field {@link #data}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setData(@javax.annotation.Nonnull final byte[] data)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			AnItem.data.set(this,data);
		}

		/**
		 * Sets a new value for the persistent field {@link #data}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setData(@javax.annotation.Nonnull final java.io.InputStream data)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.io.IOException
		{
			AnItem.data.set(this,data);
		}

		/**
		 * Sets a new value for the persistent field {@link #data}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setData(@javax.annotation.Nonnull final java.io.File data)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.io.IOException
		{
			AnItem.data.set(this,data);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for anItem.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}