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

package com.exedio.cope.vault;

import static com.exedio.cope.DataField.toValue;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Wrapper.ALL_WRAPS;
import static com.exedio.cope.tojunit.Assert.readAllLines;
import static com.exedio.cope.vault.VaultJdbcToServiceErrorTest.writeProperties;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.DataField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.tojunit.SI;
import com.exedio.cope.util.Hex;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @see VaultJdbcToServiceTest
 */
public class VaultJdbcToServiceComputeTest extends TestWithEnvironment
{
	VaultJdbcToServiceComputeTest()
	{
		super(MODEL);
	}

	@Test void test() throws IOException, SQLException
	{
		final ConnectProperties connect = MODEL.getConnectProperties();
		assumeTrue(MyItem.value.getVaultInfo()==null, "vault");
		assumeTrue(!postgresql || "public".equals(connect.getField("dialect.connection.schema").get()));

		new MyItem(toValue((byte[])null)); // row 0
		new MyItem(toValue(new byte[]{})); // row 1, hash of empty, handled by VaultResilientServiceProxy
		new MyItem(toValue(new byte[]{1,2,3})); // row 2
		new MyItem(toValue(new byte[]{1,2,4})); // row 3
		new MyItem(toValue(new byte[]{1,2,4})); // row 4
		MODEL.commit();

		@SuppressWarnings("deprecation") // OK: just a test
		final String password = connect.getConnectionPassword();
		final Properties props = new Properties();
		props.setProperty("source.url", connect.getConnectionUrl());
		props.setProperty("source.username", connect.getConnectionUsername());
		props.setProperty("source.password", password);
		props.setProperty("source.query",
				"SELECT " + SI.col(MyItem.value) + " " +
				"FROM " + SI.tab(MyItem.TYPE) + " " +
				"ORDER BY " + SI.pk(MyItem.TYPE));
		props.setProperty("source.queryHash", "false");
		props.setProperty("target.algorithm", "MD5");
		props.setProperty("target.default.service", TestService.class.getName());
		final Path propsFile = files.newFile().toPath();
		writeProperties(props, propsFile);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		VaultJdbcToService.mainInternal(
				new PrintStream(out, false, US_ASCII),
				propsFile.toAbsolutePath().toString());

		assertEquals(List.of(
				"5289df737df57326fcdd22597afb1fac - 010203",
				"57db60e93fb52657521f8f99cbc7398f - 010204",
				"57db60e93fb52657521f8f99cbc7398f - 010204 - redundant",
				"close"),
				SERVICE_PUTS);
		assertEquals(List.of(
				"Fetch size set to " + ((mysql&&!mariaDriver)?"-2147483648":"1"),
				"Query 1/1 importing: " + props.getProperty("source.query"),
				"Skipping null at row 0",
				"Redundant put at row 1 for hash d41d8cd98f00b204e9800998ecf8427e", // empty hash handled by VaultResilientServiceProxy
				"Redundant put at row 4 for hash 57db60e93fb52657521f8f99cbc7398f",
				"Finished query 1/1 after 5 rows, skipped 1, redundant 2"),
				readAllLines(out));
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		static final DataField value = new DataField().toFinal().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nullable final com.exedio.cope.DataField.Value value)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.value,value),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	private static final class TestService extends AssertionErrorVaultService
	{
		private final HashSet<String> content = new HashSet<>();

		TestService(final VaultServiceParameters parameters)
		{
			assertNotNull(parameters);
			assertEquals("MD5", parameters.getMessageDigestAlgorithm());
			assertEquals("default", parameters.getBucket());
			assertEquals(true, parameters.isWritable());
		}

		@Override
		public boolean put(final String hash, final byte[] value)
		{
			final boolean result = content.add(hash);
			SERVICE_PUTS.add(hash + " - " + Hex.encodeLower(value) + (result ? "" : " - redundant"));
			return result;
		}

		@Override
		public void close()
		{
			SERVICE_PUTS.add("close");
		}
	}

	private static final ArrayList<String> SERVICE_PUTS = new ArrayList<>();

	@BeforeEach @AfterEach void clearServicePuts()
	{
		SERVICE_PUTS.clear();
	}

	private final TemporaryFolder files = new TemporaryFolder();
}
