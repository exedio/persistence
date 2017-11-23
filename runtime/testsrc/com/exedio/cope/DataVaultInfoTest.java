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

import static com.exedio.cope.DataField.toValue;
import static com.exedio.cope.tojunit.TestSources.minimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.util.Hex;
import com.exedio.cope.vaultmock.VaultMockService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataVaultInfoTest
{
	@Test
	public void testGetLength()
	{
		assertIt(0, 0, 0, 0, 0);

		final MyItem item = new MyItem(toValue(Hex.decodeLower("abcdef")));
		assertIt(0, 0, 0, 1, 0);

		assertEquals(3, item.getFieldLength());
		assertIt(1, 0, 0, 1, 0);

		assertEquals(3, item.getFieldLength());
		assertIt(2, 0, 0, 1, 0);
	}
	@Test
	public void testGetBytes()
	{
		assertIt(0, 0, 0, 0, 0);

		final MyItem item = new MyItem(toValue(Hex.decodeLower("abcdef")));
		assertIt(0, 0, 0, 1, 0);

		assertEquals("abcdef", Hex.encodeLower(item.getFieldArray()));
		assertIt(0, 1, 0, 1, 0);

		assertEquals("abcdef", Hex.encodeLower(item.getFieldArray()));
		assertIt(0, 2, 0, 1, 0);
	}
	@Test
	public void testGetStream() throws IOException
	{
		assertIt(0, 0, 0, 0, 0);

		final MyItem item = new MyItem(toValue(Hex.decodeLower("abcdef")));
		assertIt(0, 0, 0, 1, 0);

		final ByteArrayOutputStream s = new ByteArrayOutputStream();
		item.getField(s);
		assertIt(0, 0, 1, 1, 0);
		assertEquals("abcdef", Hex.encodeLower(s.toByteArray()));

		s.reset();
		item.getField(s);
		assertIt(0, 0, 2, 1, 0);
		assertEquals("abcdef", Hex.encodeLower(s.toByteArray()));
	}
	@Test
	public void testPut()
	{
		assertIt(0, 0, 0, 0, 0);

		final MyItem item = new MyItem(toValue(Hex.decodeLower("abcdef")));
		assertIt(0, 0, 0, 1, 0);

		item.setField(Hex.decodeLower("abcdef"));
		assertIt(0, 0, 0, 1, 1);

		item.setField(Hex.decodeLower("abcdef"));
		assertIt(0, 0, 0, 1, 2);

		item.setField(Hex.decodeLower("abcde0"));
		assertIt(0, 0, 0, 2, 2);
	}


	private static void assertIt(
			final int getLength,
			final int getBytes,
			final int getStream,
			final int putInitial,
			final int putRedundant)
	{
		final DataFieldVaultInfo actual = MyItem.field.getVaultInfo();
		assertNotNull(actual);
		assertSame(MyItem.field, actual.getField());
		assertEquals("VaultMockService:exampleDefault", actual.getService());
		assertEquals(getLength,    actual.getGetLengthCount(),    "getLength");
		assertEquals(getBytes,     actual.getGetBytesCount(),     "getBytes");
		assertEquals(getStream,    actual.getGetStreamCount(),    "getStream");
		assertEquals(putInitial,   actual.getPutInitialCount(),   "putInitial");
		assertEquals(putRedundant, actual.getPutRedundantCount(), "putRedundant");
		assertEquals(getLength+getBytes+getStream, actual.getGetCount(), "get");
		assertEquals(putInitial+putRedundant,      actual.getPutCount(), "put");
	}

	@SuppressWarnings("static-method")
	@BeforeEach final void setUp()
	{
		model.connect(ConnectProperties.create(cascade(
				single("dataField.vault", true),
				single("dataField.vault.service", VaultMockService.class),
				minimal()
		)));
		model.createSchema();
		model.startTransaction("DataVaultInfoTest");
	}

	@SuppressWarnings("static-method")
	@AfterEach public final void tearDown()
	{
		model.rollbackIfNotCommitted();
		model.tearDownSchema();
		if(model.isConnected())
			model.disconnect();
	}

	static final Model model = new Model(MyItem.TYPE);

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static class MyItem extends Item
	{
		@Vault
		static final DataField field = new DataField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		MyItem(
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.field.map(field),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final boolean isFieldNull()
		{
			return MyItem.field.isNull(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final long getFieldLength()
		{
			return MyItem.field.getLength(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		final byte[] getFieldArray()
		{
			return MyItem.field.getArray(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void getField(@javax.annotation.Nonnull final java.io.OutputStream field)
				throws
					java.io.IOException
		{
			MyItem.field.get(this,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void getField(@javax.annotation.Nonnull final java.io.File field)
				throws
					java.io.IOException
		{
			MyItem.field.get(this,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setField(@javax.annotation.Nonnull final com.exedio.cope.DataField.Value field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.field.set(this,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setField(@javax.annotation.Nonnull final byte[] field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.field.set(this,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setField(@javax.annotation.Nonnull final java.io.InputStream field)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.io.IOException
		{
			MyItem.field.set(this,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setField(@javax.annotation.Nonnull final java.io.File field)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.io.IOException
		{
			MyItem.field.set(this,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
