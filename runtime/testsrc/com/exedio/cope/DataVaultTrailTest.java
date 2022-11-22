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
import static com.exedio.cope.SchemaInfo.checkVaultTrail;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.util.Hex.decodeLower;
import static com.exedio.cope.util.Hex.encodeLower;
import static com.exedio.cope.vault.VaultPropertiesTest.unsanitize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import com.exedio.cope.tojunit.SI;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vaultmock.VaultMockService;
import com.exedio.dsmf.Node;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DataVaultTrailTest extends TestWithEnvironment
{
	DataVaultTrailTest()
	{
		super(model);
		copeRule.omitTransaction();
	}

	@Test void testBytes() throws SQLException
	{
		test(s -> toValue(decodeLower(s)));
	}
	@Test void testStream() throws SQLException
	{
		test(s -> toValue(new TestByteArrayInputStream(decodeLower(s))));
	}
	@Test void testPath() throws SQLException
	{
		test(s ->
		{
			try
			{
				return toValue(files.newPath(decodeLower(s)));
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
		});
	}
	private void test(final java.util.function.Function<String, DataField.Value> f) throws SQLException
	{
		final String trailTab  = quoteName(model, "VaultTrail_myService_Key");
		final String trailTabD = quoteName(model, "VaultTrail_default");
		final String trailHash = quoteName(model, "hash");
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(MyItem.TYPE) + " " +
				"LEFT JOIN " + trailTab + " " +
				"ON " + SI.colq(MyItem.field) + "=" + trailTab + "." + trailHash + " " +
				"WHERE " + SI.colq(MyItem.field) + " IS NOT NULL " +
				"AND " + trailTab + "." + trailHash + " IS NULL",
				checkVaultTrail(MyItem.field));
		assertEquals(
				"SELECT COUNT(*) FROM " + SI.tab(MyItem.TYPE) + " " +
				"LEFT JOIN " + trailTabD + " " +
				"ON " + SI.colq(MyItem.other) + "=" + trailTabD + "." + trailHash + " " +
				"WHERE " + SI.colq(MyItem.other) + " IS NOT NULL " +
				"AND " + trailTabD + "." + trailHash + " IS NULL",
				checkVaultTrail(MyItem.other));
		assertEquals(0, MyItem.field.checkVaultTrail());
		assertEquals(0, MyItem.other.checkVaultTrail());

		queryTrail("myService_Key", rs -> {});
		queryTrail("default", rs -> {});

		final MyItem item = new MyItem(f.apply("abcdef"));
		queryTrail("myService_Key", rs ->
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs));

		item.setField(f.apply("abcdef"));
		queryTrail("myService_Key", rs ->
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs));

		item.setField(f.apply("abcdef"));
		queryTrail("myService_Key", rs ->
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs));

		item.setField(f.apply("abcde0"));
		queryTrail("myService_Key", rs ->
		{
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs);
			assertRow(abcde0Hash, 3, "abcde0", "MyItem.field", rs);
		});

		item.setField(f.apply("abcde01234"));
		queryTrail("myService_Key", rs ->
		{
			assertRow(abcde01234Hash, 5, "abcde01234", "MyItem.field", rs);
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs);
			assertRow(abcde0Hash, 3, "abcde0", "MyItem.field", rs);
		});

		queryTrail("default", rs -> {});
		item.setOther(f.apply("abcdef"));
		queryTrail("default", rs ->
			assertRow(abcdefHash, 3, "abcdef", "MyItem.other", rs));
		queryTrail("myService_Key", rs ->
		{
			assertRow(abcde01234Hash, 5, "abcde01234", "MyItem.field", rs);
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs);
			assertRow(abcde0Hash, 3, "abcde0", "MyItem.field", rs);
		});

		assertEquals(0, MyItem.field.checkVaultTrail());
		assertEquals(0, MyItem.other.checkVaultTrail());

		log.assertEmpty();
	}

	@Test void testRedundant() throws SQLException
	{
		queryTrail("myService_Key", rs -> {});
		assertEquals(0, MyItem.field.checkVaultTrail());
		assertEquals(0, MyItem.other.checkVaultTrail());

		final MyItem item = new MyItem(toValue(decodeLower("abcdef")));
		queryTrail("myService_Key", rs ->
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs));
		assertEquals(0, MyItem.field.checkVaultTrail());
		assertEquals(0, MyItem.other.checkVaultTrail());

		item.setField(toValue(decodeLower("abcdef")));
		queryTrail("myService_Key", rs ->
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs));
		assertEquals(0, MyItem.field.checkVaultTrail());
		assertEquals(0, MyItem.other.checkVaultTrail());

		updateTrail("DELETE FROM " + quoteName(model, "VaultTrail_myService_Key"));
		queryTrail("myService_Key", rs -> {});
		assertEquals(1, MyItem.field.checkVaultTrail());
		assertEquals(0, MyItem.other.checkVaultTrail());

		item.setField(toValue(decodeLower("abcdef")));
		queryTrail("myService_Key", rs -> {});
		assertEquals(1, MyItem.field.checkVaultTrail());
		assertEquals(0, MyItem.other.checkVaultTrail());

		log.assertEmpty();
	}

	@Test void testCollision() throws SQLException
	{
		queryTrail("myService_Key", rs -> {});

		final MyItem item = new MyItem(toValue(decodeLower("abcdef")));
		queryTrail("myService_Key", rs ->
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs));

		final VaultMockService vs = (VaultMockService)unsanitize(model.connect().vaults.get("myService-Key"));
		assertNotNull(vs);
		vs.clear();
		log.assertEmpty();
		item.setField(toValue(decodeLower("abcdef")));
		log.assertError(abcdefHash);
		queryTrail("myService_Key", rs ->
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs));

		log.assertEmpty();
	}

	@Test void testStartOverflow() throws SQLException
	{
		queryTrail("myService_Key", rs -> {});

		new MyItem(toValue(decodeLower("0001020304050607080900010203040506070809ab")));
		queryTrail("myService_Key", rs ->
				assertRow(
						"64551e4605d5b8973ddee826d90e0841b06dc933cf7874b81632cc0e67176d70a319ae6fe7b23bb400f0704be45abb3aa74eb29df34753c390bef492bff3baf5",
						21,
						"0001020304050607080900010203040506070809", "MyItem.field", rs));

		log.assertEmpty();
	}

	@Test void testStartOverflowAlmost() throws SQLException
	{
		queryTrail("myService_Key", rs -> {});

		new MyItem(toValue(decodeLower("0001020304050607080900010203040506070809")));
		queryTrail("myService_Key", rs ->
				assertRow(
						"7d1e2e25f25b9861f2cd8682301aa19cae07c8aa304418040d05c8926bc6ea995c923c0c5628c668980b099385f4ba58dc94e623f72f2d70cb24baf83636ce8c",
						20,
						"0001020304050607080900010203040506070809", "MyItem.field", rs));

		log.assertEmpty();
	}

	@Test void testMarkPutInitial() throws SQLException
	{
		assertEquals(false, model.isVaultRequiredToMarkPut("myService-Key"));
		queryTrail("myService_Key", rs -> {});

		model.setVaultRequiredToMarkPut("myService-Key", true);
		assertEquals(true, model.isVaultRequiredToMarkPut("myService-Key"));

		final MyItem item = new MyItem(toValue(decodeLower("abcdef")));
		queryTrail("myService_Key", rs ->
				assertRow(abcdefHash, 3, "abcdef", 1, "MyItem.field", rs));

		item.setField(toValue(decodeLower("abcdef")));
		queryTrail("myService_Key", rs ->
				assertRow(abcdefHash, 3, "abcdef", 1, "MyItem.field", rs));

		log.assertEmpty();
	}

	@Test void testMarkPutRedundant() throws SQLException
	{
		assertEquals(false, model.isVaultRequiredToMarkPut("myService-Key"));
		queryTrail("myService_Key", rs -> {});

		final MyItem item = new MyItem(toValue(decodeLower("abcdef")));
		queryTrail("myService_Key", rs ->
				assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs));

		model.setVaultRequiredToMarkPut("myService-Key", true);
		assertEquals(true, model.isVaultRequiredToMarkPut("myService-Key"));

		item.setField(toValue(decodeLower("abcdef")));
		queryTrail("myService_Key", rs ->
				assertRow(abcdefHash, 3, "abcdef", 1, "MyItem.field", rs));

		log.assertEmpty();
	}

	@Test void testFieldLong() throws SQLException
	{
		queryTrail("myService_Key", rs -> {});

		final MyItem item = new MyItem(toValue(decodeLower("abcdef")));
		queryTrail("myService_Key", rs ->
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs));

		final String field = "MyItem.veryLong0123456789012345678901234567890123456789012345678901234567890 ...";
		assertEquals(80, field.length());

		item.setVeryLong(toValue(decodeLower("abcde0")));
		queryTrail("myService_Key", rs ->
		{
			assertRow(abcdefHash, 3, "abcdef", "MyItem.field", rs);
			assertRow(abcde0Hash, 3, "abcde0", field, rs);
		});

		assertEquals(0, MyItem.field.checkVaultTrail());
		assertEquals(0, MyItem.veryLong.checkVaultTrail());

		log.assertEmpty();
	}


	private void queryTrail(final String serviceKey, final SQLRunnable runnable) throws SQLException
	{
		final String txName = model.currentTransaction().getName();
		model.commit();
		try(ResultSet rs = connection.executeQuery(
				"SELECT * FROM " + quoteName(model, "VaultTrail_" + serviceKey) + " " +
				"ORDER BY " + quoteName(model, "hash")))
		{
			final ResultSetMetaData md = rs.getMetaData();
			int column = 1;
			assertEquals("hash",    md.getColumnName(column++));
			assertEquals("length",  md.getColumnName(column++));
			assertEquals("start20", md.getColumnName(column++));
			assertEquals("markPut", md.getColumnName(column++));
			assertEquals("date",    md.getColumnName(column++));
			assertEquals("field",   md.getColumnName(column++));
			assertEquals("origin",  md.getColumnName(column++));
			assertEquals(column-1,  md.getColumnCount());

			runnable.run(rs);
			assertFalse(rs.next());
		}
		model.startTransaction(txName);
	}

	private static void assertRow(
			final String hash,
			final int length,
			final String start,
			final String field,
			final ResultSet rs) throws SQLException
	{
		assertRow(hash, length, start, null, field, rs);
	}

	private static void assertRow(
			final String hash,
			final int length,
			final String start,
			final Integer markPut,
			final String field,
			final ResultSet rs) throws SQLException
	{
		assertTrue(rs.next());
		int column = 1;
		assertEquals(hash,    rs.getString(column++), "hash");
		assertEquals(length,  rs.getInt   (column++), "length");
		assertEquals(start,   encodeLower(rs.getBytes(column++)), "start");
		assertEquals(markPut, integer(rs,  column++), "markPut");
		assertNotNull(        rs.getDate  (column++), "date");
		assertEquals(field,   rs.getString(column++), "field");
		//noinspection UnusedAssignment OK: bug in idea
		assertEquals(ORIGIN,  rs.getString(column++), "origin");
	}

	private static Integer integer(final ResultSet rs, final int columnIndex) throws SQLException
	{
		final int result = rs.getInt(columnIndex);
		return rs.wasNull() ? null : result;
	}

	private void updateTrail(final String sql) throws SQLException
	{
		final String txName = model.currentTransaction().getName();
		model.commit();
		connection.executeUpdate(sql);
		model.startTransaction(txName);
	}


	@Test void testSchema()
	{
		model.commit();
		assertEquals(Node.Color.OK, model.getVerifiedSchema().getCumulativeColor());
	}


	private final ConnectionRule connection = new ConnectionRule(model);
	private final MyTemporaryFolder files = new MyTemporaryFolder();
	private final LogRule log = new LogRule(VaultTrail.class);

	private static final String ORIGIN = getOrigin();

	private static String getOrigin()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch(final UnknownHostException e)
		{
			throw new RuntimeException(e);
		}
	}

	@FunctionalInterface
	private interface SQLRunnable
	{
		void run(ResultSet rs) throws SQLException;
	}

	private static final String abcdefHash = "d5d81c66c3b1a0efb49e980ebc5629c352342dc3332c0697cbeeb55f892a85264496aa239ee29997708fce8510594cb01fe08b8a6132b98a1e113ae96d016b42";
	private static final String abcde0Hash = "e1602c8a5003d0d6493ac36803f2441cf96036ba85ab642f7316c72840bcde43e1dd7b00f618c00d2298717f74a4f09e444e22c82d66385514c590294c5392a2";
	private static final String abcde01234Hash = "0675b012cc3da89b2b2a552e685f1853189c94a5478957578c05edeb53d7c76e763ffad50a7290f0e9a1d52d1b1e98ccbe7e712db6eed18e3c5ca60a13efe9db";

	@Override
	public Properties.Source override(final Properties.Source s)
	{
		if(!"true".equals(s.get("vault")))
			return s;

		assertEquals(null, s.get("vault.services")); // thus defaults to "default"
		final String service = s.get("vault.service");
		assertNotNull(service);
		return Sources.cascade(
				TestSources.single("vault.services", "default myService-Key"),
				TestSources.single("vault.service.default", service),
				TestSources.single("vault.service.myService-Key", service),
				s);
	}

	@BeforeEach
	void beforeEach() throws SQLException
	{
		final VaultProperties vaultProperties = model.getConnectProperties().getVaultProperties();
		assumeTrue(vaultProperties!=null && vaultProperties.isTrailEnabled());
		model.setVaultRequiredToMarkPut("myService-Key", false);
		model.setVaultRequiredToMarkPut("default", false);
		connection.executeUpdate("DELETE FROM " + quoteName(model, "VaultTrail_myService_Key"));
		connection.executeUpdate("DELETE FROM " + quoteName(model, "VaultTrail_default"));
		model.startTransaction(DataVaultTrailTest.class.getName());
	}

	private static final Model model = new Model(MyItem.TYPE);

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	private static class MyItem extends Item
	{
		@Vault("myService-Key")
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=Visibility.NONE)
		@Wrapper(wrap="set", parameters=DataField.Value.class, visibility=Visibility.DEFAULT)
		static final DataField field = new DataField();

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=Visibility.NONE)
		@Wrapper(wrap="set", parameters=DataField.Value.class, visibility=Visibility.DEFAULT)
		static final DataField other = new DataField().optional();

		@Vault("myService-Key")
		@CopeName("veryLong01234567890123456789012345678901234567890123456789012345678901234567890123456789")
		@CopeSchemaName("veryLongSchema")
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=Visibility.NONE)
		@Wrapper(wrap="set", parameters=DataField.Value.class, visibility=Visibility.DEFAULT)
		static final DataField veryLong = new DataField().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
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
		final void setField(@javax.annotation.Nonnull final com.exedio.cope.DataField.Value field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setOther(@javax.annotation.Nullable final com.exedio.cope.DataField.Value other)
		{
			MyItem.other.set(this,other);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setVeryLong(@javax.annotation.Nullable final com.exedio.cope.DataField.Value veryLong)
		{
			MyItem.veryLong.set(this,veryLong);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
