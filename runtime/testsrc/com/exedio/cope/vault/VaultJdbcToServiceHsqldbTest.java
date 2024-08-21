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

import static com.exedio.cope.tojunit.Assert.readAllLines;
import static com.exedio.cope.vault.VaultJdbcToServiceErrorTest.writeProperties;
import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.ServiceProperties;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.opentest4j.AssertionFailedError;

@MainRule.Tag
public class VaultJdbcToServiceHsqldbTest
{
	@Test void testNormal() throws IOException, SQLException
	{
		final Path propsFile = createProperties(
				"VALUES " +
						"('012345678901234567890123456789ab', '050401')," +
						"('022345678901234567890123456789ab', '050402')", Map.of());
		final var out = new ByteArrayOutputStream();
		VaultJdbcToService.mainInternal(
				new PrintStream(out, true, US_ASCII),
				propsFile.toString());

		assertEquals(List.of(
				"012345678901234567890123456789ab - 050401",
				"022345678901234567890123456789ab - 050402",
				"close"),
				SERVICE_PUTS);
		assertEquals(List.of(
				"Fetch size set to 1",
				"Query 1/1 importing: VALUES " +
						"('012345678901234567890123456789ab', '050401')," +
						"('022345678901234567890123456789ab', '050402')",
				"Finished query 1/1 after 2 rows, skipped 0, redundant 0"),
				readAllLines(out));
	}

	@Test void testMultipleLines() throws IOException, SQLException
	{
		final Path propsFile = createProperties(
				"VALUES " +
						"('012345678901234567890123456789ab', '050401')," +
						"('022345678901234567890123456789ab', '050402')," +
						"(NULL, 'fa0401')," +
						"(NULL, 'fa0401')," +
						"(NULL, 'fa0401')," +
						"('fb0145678901234567890123456789ab', 'fb0401')," +
						"('fb0245678901234567890123456789ab', 'fb0402')" +
						lineSeparator() +
				lineSeparator() + // test empty line
				"VALUES " +
						"('032345678901234567890123456789ab', '050403')," +
						"('042345678901234567890123456789ab', '050404')," +
						"('052345678901234567890123456789ab', '050405')", Map.of());
		final var out = new ByteArrayOutputStream();
		VaultJdbcToService.mainInternal(
				new PrintStream(out, true, US_ASCII),
				propsFile.toString());

		assertEquals(List.of(
				"012345678901234567890123456789ab - 050401",
				"022345678901234567890123456789ab - 050402",
				"fb0145678901234567890123456789ab - fb0401 - redundant",
				"fb0245678901234567890123456789ab - fb0402 - redundant",
				"032345678901234567890123456789ab - 050403",
				"042345678901234567890123456789ab - 050404",
				"052345678901234567890123456789ab - 050405",
				"close"),
				SERVICE_PUTS);
		assertEquals(List.of(
				"Fetch size set to 1",
				"Query 1/2 importing: VALUES " +
						"('012345678901234567890123456789ab', '050401')," +
						"('022345678901234567890123456789ab', '050402')," +
						"(NULL, 'fa0401')," + // row 2
						"(NULL, 'fa0401')," + // row 3
						"(NULL, 'fa0401')," + // row 4
						"('fb0145678901234567890123456789ab', 'fb0401')," + // row 5
						"('fb0245678901234567890123456789ab', 'fb0402')",   // row 6
				"Skipping null at row 2: hash",
				"Skipping null at row 3: hash",
				"Skipping null at row 4: hash",
				"Redundant put at row 5 for hash fb0145678901234567890123456789ab",
				"Redundant put at row 6 for hash fb0245678901234567890123456789ab",
				"Finished query 1/2 after 7 rows, skipped 3, redundant 2",
				"Query 2/2 importing: VALUES " +
						"('032345678901234567890123456789ab', '050403')," +
						"('042345678901234567890123456789ab', '050404')," +
						"('052345678901234567890123456789ab', '050405')",
				"Finished query 2/2 after 3 rows, skipped 0, redundant 0",
				"Finished 2 queries after 10 rows, skipped 3, redundant 2"),
				readAllLines(out));
	}

	@Test void testCompute() throws IOException, SQLException
	{
		final Path propsFile = createProperties(
				"VALUES " +
						"(NULL)," +
						"('010203')," +
						"('010204')",
				Map.of("source.queryHash", "false"));
		final var out = new ByteArrayOutputStream();
		VaultJdbcToService.mainInternal(
				new PrintStream(out, true, US_ASCII),
				propsFile.toString());

		assertEquals(List.of(
				"5289df737df57326fcdd22597afb1fac - 010203",
				"57db60e93fb52657521f8f99cbc7398f - 010204",
				"close"),
				SERVICE_PUTS);
		assertEquals(List.of(
				"Fetch size set to 1",
				"Query 1/1 importing: VALUES " +
						"(NULL)," +
						"('010203')," +
						"('010204')",
				"Skipping null at row 0",
				"Finished query 1/1 after 3 rows, skipped 1, redundant 0"),
				readAllLines(out));
	}

	@Test void testProps() throws IOException, SQLException
	{
		final Path propsFile = createProperties(
				"VALUES " +
				"('012345678901234567890123456789ab', '050401')," +
				"('022345678901234567890123456789ab', '050402')",
				TestServiceProps.class,
				Map.of(
						"targetProbesSuppressed", "4Fails 4FailsOther"));
		final var out = new ByteArrayOutputStream();
		VaultJdbcToService.mainInternal(
				new PrintStream(out, true, US_ASCII),
				propsFile.toString());

		assertEquals(List.of(
				"012345678901234567890123456789ab - 050401",
				"022345678901234567890123456789ab - 050402",
				"close"),
				SERVICE_PUTS);
		assertEquals(List.of(
				"Fetch size set to 1",
				"Probing 1Ok ...",
				"  success: probe1Ok result",
				"Probing 2OkVoid ...",
				"  success",
				"Probing 3Aborts ...",
				"  aborted: probe3Aborts cause",
				"Probing 4Fails suppressed",
				"Probing 4FailsOther suppressed",
				"Query 1/1 importing: VALUES " +
				"('012345678901234567890123456789ab', '050401')," +
				"('022345678901234567890123456789ab', '050402')",
				"Finished query 1/1 after 2 rows, skipped 0, redundant 0"),
				readAllLines(out));
	}
	@ServiceProperties(TestProperties.class)
	private static final class TestServiceProps extends TestService
	{
		TestServiceProps(final VaultServiceParameters parameters, final TestProperties properties)
		{
			super(parameters);
			assertNotNull(properties);
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


	private static class TestService extends AssertionErrorVaultService
	{
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
			final boolean result = !hash.startsWith("fb");
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

	@BeforeEach
	@AfterEach
	void clearServicePuts()
	{
		SERVICE_PUTS.clear();
	}


	@Nonnull
	private Path createProperties(final String query, final Map<String, String> additional) throws IOException
	{
		return createProperties(query, TestService.class, additional);
	}

	@Nonnull
	private Path createProperties(
			final String query,
			final Class<? extends AssertionErrorVaultService> service,
			final Map<String, String> additional) throws IOException
	{
		final Properties props = new Properties();
		props.setProperty("source.url", "jdbc:hsqldb:mem:VaultJdbcToServiceHsqldbTest");
		props.setProperty("source.username", "sa");
		props.setProperty("source.password", "");
		props.setProperty("source.query", query);
		props.setProperty("target.algorithm", "MD5");
		props.setProperty("target.default.service", service.getName());
		for(final Map.Entry<String,String> e : additional.entrySet())
			props.setProperty(e.getKey(), e.getValue());
		final Path propsFile = files.newFile().toPath();
		writeProperties(props, propsFile);
		return propsFile;
	}

	private final TemporaryFolder files = new TemporaryFolder();
}
