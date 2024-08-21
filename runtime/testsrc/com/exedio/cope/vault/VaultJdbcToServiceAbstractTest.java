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

import static com.exedio.cope.vault.VaultJdbcToServiceErrorTest.writeProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.util.Hex;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.rules.TemporaryFolder;

@WrapInterim
public class VaultJdbcToServiceAbstractTest extends TestWithEnvironment
{
	VaultJdbcToServiceAbstractTest(final Model model)
	{
		super(model);
	}

	@BeforeEach
	final void setUp()
	{
		final ConnectProperties connect = model.getConnectProperties();
		assumeTrue(connect.getVaultProperties()==null, "vault");
		assumeTrue(!postgresql || "public".equals(connect.getField("dialect.connection.schema").get()));
	}

	@Nonnull
	Path createProperties(final Map<String, String> additional) throws IOException
	{
		final ConnectProperties connect = model.getConnectProperties();
		@SuppressWarnings("deprecation") // OK: just a test
		final String password = connect.getConnectionPassword();
		final Properties props = new Properties();
		props.setProperty("source.url", connect.getConnectionUrl());
		props.setProperty("source.username", connect.getConnectionUsername());
		props.setProperty("source.password", password);
		props.setProperty("target.algorithm", "MD5");
		props.setProperty("target.default.service", TestService.class.getName());
		for(final Map.Entry<String,String> e : additional.entrySet())
			props.setProperty(e.getKey(), e.getValue());
		final Path propsFile = files.newFile().toPath();
		writeProperties(props, propsFile);
		return propsFile;
	}

	private final TemporaryFolder files = new TemporaryFolder();



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

	static List<String> servicePuts()
	{
		final var result = List.copyOf(SERVICE_PUTS);
		SERVICE_PUTS.clear();
		return result;
	}

	@BeforeEach @AfterEach
	void clearServicePuts()
	{
		SERVICE_PUTS.clear();
	}
}
