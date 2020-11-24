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
import static com.exedio.cope.PrometheusMeterRegistrar.meterCope;
import static com.exedio.cope.PrometheusMeterRegistrar.tag;
import static com.exedio.cope.tojunit.TestSources.minimal;
import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.util.Hex;
import com.exedio.cope.vaultmock.VaultMockService;
import com.exedio.cope.vaulttest.VaultServiceTest.NonCloseableOrFlushableOutputStream;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class DataVaultInfoTest
{
	@Test void testGetLength()
	{
		assertIt(0, 0, 0, 0, 0);

		final MyItem item = new MyItem(toValue(Hex.decodeLower("abcdef")));
		assertIt(0, 0, 0, 1, 0);

		assertEquals(3, item.getFieldLength());
		assertIt(1, 0, 0, 1, 0);

		assertEquals(3, item.getFieldLength());
		assertIt(2, 0, 0, 1, 0);
	}
	@Test void testGetBytes()
	{
		assertIt(0, 0, 0, 0, 0);

		final MyItem item = new MyItem(toValue(Hex.decodeLower("abcdef")));
		assertIt(0, 0, 0, 1, 0);

		assertEquals("abcdef", Hex.encodeLower(item.getFieldArray()));
		assertIt(0, 1, 0, 1, 0);

		assertEquals("abcdef", Hex.encodeLower(item.getFieldArray()));
		assertIt(0, 2, 0, 1, 0);
	}
	@Test void testGetStream() throws IOException
	{
		assertIt(0, 0, 0, 0, 0);

		final MyItem item = new MyItem(toValue(Hex.decodeLower("abcdef")));
		assertIt(0, 0, 0, 1, 0);

		final NonCloseableOrFlushableOutputStream s = new NonCloseableOrFlushableOutputStream();
		item.getField(s);
		assertIt(0, 0, 1, 1, 0);
		assertEquals("abcdef", Hex.encodeLower(s.toByteArray()));

		s.reset();
		item.getField(s);
		assertIt(0, 0, 2, 1, 0);
		assertEquals("abcdef", Hex.encodeLower(s.toByteArray()));
	}
	@Test void testPut()
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


	private void assertIt(
			final int getLength,
			final int getBytes,
			final int getStream,
			final int putInitial,
			final int putRedundant)
	{
		final DataFieldVaultInfo actual = MyItem.field.getVaultInfo();
		assertNotNull(actual);
		assertSame(MyItem.field, actual.getField());
		assertEquals(Vault.DEFAULT, actual.getServiceKey());
		assertEquals("VaultMockService:exampleDefault", actual.getService());
		assertEquals(getLength,    actual.getGetLengthCount()   -onSetup.getGetLengthCount(),    "getLength");
		assertEquals(getBytes,     actual.getGetBytesCount()    -onSetup.getGetBytesCount(),     "getBytes");
		assertEquals(getStream,    actual.getGetStreamCount()   -onSetup.getGetStreamCount(),    "getStream");
		assertEquals(putInitial,   actual.getPutInitialCount()  -onSetup.getPutInitialCount(),   "putInitial");
		assertEquals(putRedundant, actual.getPutRedundantCount()-onSetup.getPutRedundantCount(), "putRedundant");
		assertEquals(getLength+getBytes+getStream, actual.getGetCount()-onSetup.getGetCount(), "get");
		assertEquals(putInitial+putRedundant,      actual.getPutCount()-onSetup.getPutCount(), "put");

		assertCount("getLength", Tags.empty(), actual.getGetLengthCount());
		assertCount("get", Tags.of("sink", "bytes"),  actual.getGetBytesCount());
		assertCount("get", Tags.of("sink", "stream"), actual.getGetStreamCount());
		assertCount("put", Tags.of("result", "initial"),   actual.getPutInitialCount());
		assertCount("put", Tags.of("result", "redundant"), actual.getPutRedundantCount());
	}

	@Test void testDisconnected()
	{
		tearDown();

		assertThrows(
				Model.NotConnectedException.class,
				MyItem.field::getVaultInfo);

		assertCount("getLength", Tags.empty(), onSetup.getGetLengthCount());
		assertCount("get", Tags.of("sink", "bytes"),  onSetup.getGetBytesCount());
		assertCount("get", Tags.of("sink", "stream"), onSetup.getGetStreamCount());
		assertCount("put", Tags.of("result", "initial"),   onSetup.getPutInitialCount());
		assertCount("put", Tags.of("result", "redundant"), onSetup.getPutRedundantCount());
	}

	private static void assertCount(final String nameSuffix, final Tags tags, final long actual)
	{
		assertEquals(
				((Counter)meterCope(
						DataField.class, "vault." + nameSuffix,
						tag(MyItem.field).and("service", "default").and(tags))).count(),
				actual,
				nameSuffix);
	}

	private DataFieldVaultInfo onSetup;

	@BeforeEach final void setUp()
	{
		model.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.service", VaultMockService.class),
				minimal()
		)));
		setupSchemaMinimal(model);
		model.startTransaction("DataVaultInfoTest");
		onSetup = MyItem.field.getVaultInfo();
	}

	@AfterEach final void tearDown()
	{
		if(model.isConnected())
		{
			model.rollbackIfNotCommitted();
			model.tearDownSchema();
			model.disconnect();
		}
	}

	static final Model model = new Model(MyItem.TYPE);

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static class MyItem extends Item
	{
		@Vault
		static final DataField field = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem(
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.field.map(field),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final boolean isFieldNull()
		{
			return MyItem.field.isNull(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final long getFieldLength()
		{
			return MyItem.field.getLength(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final byte[] getFieldArray()
		{
			return MyItem.field.getArray(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void getField(@javax.annotation.Nonnull final java.io.OutputStream field)
				throws
					java.io.IOException
		{
			MyItem.field.get(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void getField(@javax.annotation.Nonnull final java.nio.file.Path field)
				throws
					java.io.IOException
		{
			MyItem.field.get(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void getField(@javax.annotation.Nonnull final java.io.File field)
				throws
					java.io.IOException
		{
			MyItem.field.get(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setField(@javax.annotation.Nonnull final com.exedio.cope.DataField.Value field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setField(@javax.annotation.Nonnull final byte[] field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setField(@javax.annotation.Nonnull final java.io.InputStream field)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.io.IOException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setField(@javax.annotation.Nonnull final java.nio.file.Path field)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.io.IOException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setField(@javax.annotation.Nonnull final java.io.File field)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.io.IOException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
