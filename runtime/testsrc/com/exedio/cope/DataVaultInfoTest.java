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
import static com.exedio.cope.PrometheusMeterRegistrar.meter;
import static com.exedio.cope.PrometheusMeterRegistrar.tag;
import static com.exedio.cope.tojunit.TestSources.minimal;
import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.CounterDeferredTester;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import com.exedio.cope.util.Hex;
import com.exedio.cope.vaultmock.VaultMockService;
import com.exedio.cope.vaulttest.VaultServiceTest.NonCloseableOrFlushableOutputStream;
import com.exedio.dsmf.Column;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DataVaultInfoTest
{
	@Test void testGetLength()
	{
		assertIt(0, 0, 0, 0, 0, 0);

		final MyItem item = new MyItem(toValue(Hex.decodeLower("abcdef")));
		assertIt(0, 0, 1, 3, 0, 0);

		assertEquals(3, item.getFieldLength());
		assertIt(0, 0, 1, 3, 0, 0);

		assertEquals(3, item.getFieldLength());
		assertIt(0, 0, 1, 3, 0, 0);
	}
	@Test void testGetBytes()
	{
		assertIt(0, 0, 0, 0, 0, 0);

		final MyItem item = new MyItem(toValue(Hex.decodeLower("abcdef")));
		assertIt(0, 0, 1, 3, 0, 0);

		assertEquals("abcdef", Hex.encodeLower(item.getFieldArray()));
		assertIt(1, 0, 1, 3, 0, 0);

		assertEquals("abcdef", Hex.encodeLower(item.getFieldArray()));
		assertIt(2, 0, 1, 3, 0, 0);
	}
	@Test void testGetStream() throws IOException
	{
		assertIt(0, 0, 0, 0, 0, 0);

		final MyItem item = new MyItem(toValue(Hex.decodeLower("abcdef")));
		assertIt(0, 0, 1, 3, 0, 0);

		final NonCloseableOrFlushableOutputStream s = new NonCloseableOrFlushableOutputStream();
		item.getField(s);
		assertIt(0, 1, 1, 3, 0, 0);
		assertEquals("abcdef", Hex.encodeLower(s.toByteArray()));

		s.reset();
		item.getField(s);
		assertIt(0, 2, 1, 3, 0, 0);
		assertEquals("abcdef", Hex.encodeLower(s.toByteArray()));
	}
	@Test void testPutBytes()
	{
		testPut(s -> toValue(Hex.decodeLower(s)));
	}
	@Test void testPutStream()
	{
		testPut(s -> toValue(new TestByteArrayInputStream(Hex.decodeLower(s))));
	}
	@Test void testPutPath()
	{
		testPut(s ->
		{
			try
			{
				return toValue(files.newPath(Hex.decodeLower(s)));
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
		});
	}
	private final MyTemporaryFolder files = new MyTemporaryFolder();
	private void testPut(final java.util.function.Function<String, DataField.Value> f)
	{
		assertIt(0, 0, 0, 0, 0, 0);

		final MyItem item = new MyItem(f.apply("abcdef"));
		assertIt(0, 0, 1, 3, 0, 0);

		item.setField(f.apply("abcdef"));
		assertIt(0, 0, 1, 3, 1, 3);

		item.setField(f.apply("abcdef"));
		assertIt(0, 0, 1, 3, 2, 6);

		item.setField(f.apply("abcde0"));
		assertIt(0, 0, 2, 6, 2, 6);

		item.setField(f.apply("abcde01234"));
		assertIt(0, 0, 3, 11, 2, 6);
	}


	private void assertIt(
			final int getBytes,
			final int getStream,
			final int putInitial,
			final int putInitialSize,
			final int putRedundant,
			final int putRedundantSize)
	{
		assertEquals(Vault.DEFAULT, MyItem.field.getVaultBucket());

		final DataFieldVaultInfo actual = MyItem.field.getVaultInfo();
		assertNotNull(actual);
		assertSame(MyItem.field, actual.getField());
		assertEquals(Vault.DEFAULT, actual.getBucket());
		assertEquals("VaultMockService:exampleDefault", actual.getService());
		assertEquals(getBytes,     actual.getGetBytesCount()    -onSetup.getGetBytesCount(),     "getBytes");
		assertEquals(getStream,    actual.getGetStreamCount()   -onSetup.getGetStreamCount(),    "getStream");
		assertEquals(putInitial,   actual.getPutInitialCount()  -onSetup.getPutInitialCount(),   "putInitial");
		assertEquals(putRedundant, actual.getPutRedundantCount()-onSetup.getPutRedundantCount(), "putRedundant");
		assertEquals(getBytes+getStream,      actual.getGetCount()-onSetup.getGetCount(), "get");
		assertEquals(putInitial+putRedundant, actual.getPutCount()-onSetup.getPutCount(), "put");

		assertCount("get", Tags.of("sink", "bytes"),  actual.getGetBytesCount());
		assertCount("get", Tags.of("sink", "stream"), actual.getGetStreamCount());
		assertCount("put", Tags.of("result", "initial"),   actual.getPutInitialCount());
		assertCount("put", Tags.of("result", "redundant"), actual.getPutRedundantCount());
		this.putInitialSize  .assertCount(putInitialSize);
		this.putRedundantSize.assertCount(putRedundantSize);
	}

	@Test void testDisconnected()
	{
		tearDown();

		assertThrows(
				Model.NotConnectedException.class,
				MyItem.field::getVaultBucket);
		assertThrows(
				Model.NotConnectedException.class,
				MyItem.field::checkVaultTrail);
		assertThrows(
				Model.NotConnectedException.class,
				() -> SchemaInfo.checkVaultTrail(MyItem.field));
		assertThrows(
				Model.NotConnectedException.class,
				MyItem.field::getVaultInfo);

		assertCount("get", Tags.of("sink", "bytes"),  onSetup.getGetBytesCount());
		assertCount("get", Tags.of("sink", "stream"), onSetup.getGetStreamCount());
		assertCount("put", Tags.of("result", "initial"),   onSetup.getPutInitialCount());
		assertCount("put", Tags.of("result", "redundant"), onSetup.getPutRedundantCount());
		this.putInitialSize  .assertCount(0);
		this.putRedundantSize.assertCount(0);
	}

	private static void assertCount(final String nameSuffix, final Tags tags, final long actual)
	{
		assertEquals(
				((Counter)meter(
						DataField.class, "vault." + nameSuffix,
						tag(model).and(tag(MyItem.field)).and("bucket", "default").and(tags))).count(),
				actual,
				nameSuffix);
	}

	private final CounterDeferredTester putInitialSize   = counter("putSize", Tags.of("result", "initial"));
	private final CounterDeferredTester putRedundantSize = counter("putSize", Tags.of("result", "redundant"));

	private static CounterDeferredTester counter(final String name, final Tags tags)
	{
		return new CounterDeferredTester(
				DataField.class, "vault." + name,
				tag(model).and(tag(MyItem.field)).and("bucket", "default").and(tags));
	}


	@Test void testSchema()
	{
		final com.exedio.dsmf.Table tab = model.getSchema().getTable("VaultTrail_default");
		assertNotNull(tab);
		assertEquals(
				Arrays.asList("hash", "length", "start20", "markPut", "date", "field", "origin"),
				tab.getColumns().stream().map(Column::getName).toList());
		assertEquals("VARCHAR(128)"     + " not null", tab.getColumn("hash")   .getType());
		assertEquals("BIGINT"           + " not null", tab.getColumn("length") .getType());
		assertEquals("BLOB"             + " not null", tab.getColumn("start20").getType());
		assertEquals("TINYINT",                        tab.getColumn("markPut").getType());
		assertEquals("TIMESTAMP(3) WITHOUT TIME ZONE", tab.getColumn("date")   .getType());
		assertEquals("VARCHAR(80)",                    tab.getColumn("field")  .getType());
		assertEquals("VARCHAR(80)",                    tab.getColumn("origin") .getType());
	}


	private DataFieldVaultInfo onSetup;

	@BeforeEach final void setUp()
	{
		model.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.default.service", VaultMockService.class),
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

	@WrapperType(indent=2, comments=false)
	private static class MyItem extends Item
	{
		@Vault
		@Wrapper(wrap="set", visibility=Visibility.NONE)
		@Wrapper(wrap="set", parameters=DataField.Value.class, visibility=Visibility.DEFAULT)
		static final DataField field = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.field,field),
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
		@java.lang.Deprecated
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
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
