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
import static com.exedio.cope.vault.VaultFileToTrailTest.readAllLines;
import static com.exedio.cope.vault.VaultJdbcToServiceErrorTest.writeProperties;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.DataField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.tojunit.SI;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.ServiceProperties;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.opentest4j.AssertionFailedError;

public class VaultJdbcToServiceTest extends TestWithEnvironment
{
	VaultJdbcToServiceTest()
	{
		super(MODEL);
	}

	@Test void test() throws IOException, SQLException
	{
		final ConnectProperties connect = MODEL.getConnectProperties();
		assumeTrue(MyItem.value.getVaultInfo()==null, "vault");
		assumeTrue(!postgresql || "public".equals(connect.getField("dialect.connection.schema").get()));

		new MyItem(null, null); // row 0
		new MyItem("", null);   // row 1
		new MyItem("ab", null); // row 2
		new MyItem("01x345678901234567890123456789ab", null);  // row 3
		new MyItem("01aa45678901234567890123456789ab", null);  // row 4
		new MyItem("d41d8cd98f00b204e9800998ecf8427e", toValue(new byte[]{})); // row 5, hash of empty, handled by VaultResilientServiceProxy
		new MyItem("01bb45678901234567890123456789ff", toValue(new byte[]{})); // row 6
		new MyItem("01cc45678901234567890123456789ab", toValue(new byte[]{1,2,3})); // row 7
		new MyItem("fa2345678901234567890123456789ab", toValue(new byte[]{1,2,4})); // row 8
		MODEL.commit();

		@SuppressWarnings("deprecation") // OK: just a test
		final String password = connect.getConnectionPassword();
		final Properties props = new Properties();
		props.setProperty("source.url", connect.getConnectionUrl());
		props.setProperty("source.username", connect.getConnectionUsername());
		props.setProperty("source.password", password);
		props.setProperty("source.query",
				"SELECT " + SI.col(MyItem.hash) + "," + SI.col(MyItem.value) + " " +
				"FROM " + SI.tab(MyItem.TYPE) + " " +
				"ORDER BY " + SI.pk(MyItem.TYPE));
		props.setProperty("target.algorithm", "MD5");
		props.setProperty("target.service", TestService.class.getName());
		props.setProperty("targetProbesSuppressed", "4Fails 4FailsOther");
		final Path propsFile = files.newFile().toPath();
		writeProperties(props, propsFile);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		VaultJdbcToService.mainInternal(
				new PrintStream(out, false, US_ASCII),
				propsFile.toAbsolutePath().toString());

		assertEquals(List.of(
				"01cc45678901234567890123456789ab - 010203",
				"fa2345678901234567890123456789ab - 010204 - redundant"),
				SERVICE_PUTS);
		assertEquals(List.of(
				"Probing 1Ok ...",
				"  success: probe1Ok result",
				"Probing 2OkVoid ...",
				"  success",
				"Probing 3Aborts ...",
				"  aborted: probe3Aborts cause",
				"Probing 4Fails suppressed",
				"Probing 4FailsOther suppressed",
				"Skipping null at row 0: hash",
				"Skipping illegal argument at row 1: hash >< must have length 32, but has 0",
				"Skipping illegal argument at row 2: hash >ab< must have length 32, but has 2",
				"Skipping illegal argument at row 3: hash >01x3456789012345xx32< contains illegal character >x< at position 2",
				"Skipping null at row 4: value",
				"Redundant put at row 5 for hash d41d8cd98f00b204e9800998ecf8427e", // empty hash handled by VaultResilientServiceProxy
				"Skipping illegal argument at row 6: hash >01bb456789012345xx32< put with empty value, but empty hash is >d41d8cd98f00b204e9800998ecf8427e<", // empty value handled by VaultResilientServiceProxy
				"Redundant put at row 8 for hash fa2345678901234567890123456789ab",
				"Finished after 9 rows, skipped 6, redundant 2"),
				readAllLines(out));
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		static final StringField hash = new StringField().toFinal().optional().lengthMin(0);
		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		static final DataField value = new DataField().toFinal().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nullable final java.lang.String hash,
					@javax.annotation.Nullable final com.exedio.cope.DataField.Value value)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.hash,hash),
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

	@ServiceProperties(TestProperties.class)
	private static final class TestService extends AssertionErrorVaultService
	{
		TestService(final VaultServiceParameters parameters, final TestProperties properties)
		{
			assertNotNull(parameters);
			assertEquals("MD5", parameters.getVaultProperties().getAlgorithm());
			assertEquals("default", parameters.getServiceKey());
			assertEquals(true, parameters.isWritable());
			assertNotNull(properties);
		}

		@Override
		public boolean put(final String hash, final byte[] value, final VaultPutInfo info)
		{
			final boolean result = !hash.startsWith("fa");
			SERVICE_PUTS.add(hash + " - " + Hex.encodeLower(value) + (result ? "" : " - redundant"));
			return result;
		}
	}

	private static final class TestProperties extends com.exedio.cope.util.Properties
	{
		TestProperties(final Source source)
		{
			super(source);
		}
		@Probe String probe1Ok()
		{
			return "probe1Ok result";
		}
		@Probe void probe2OkVoid()
		{
			// do nothing
		}
		@Probe String probe3Aborts() throws ProbeAbortedException
		{
			throw newProbeAbortedException("probe3Aborts cause");
		}
		@Probe String probe4Fails()
		{
			throw new AssertionFailedError("probe4Fails cause");
		}
		@Probe String probe4FailsOther()
		{
			throw new AssertionFailedError("probe4FailsOther cause");
		}
	}

	private static final ArrayList<String> SERVICE_PUTS = new ArrayList<>();

	@BeforeEach @AfterEach void clearServicePuts()
	{
		SERVICE_PUTS.clear();
	}

	protected final TemporaryFolder files = new TemporaryFolder();
}
