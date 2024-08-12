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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.readAllLines;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.ServiceProperties;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

@MainRule.Tag
public class VaultJdbcToServiceErrorTest
{
	@Test void testMissingConfig()
	{
		assertFails(
				() -> VaultJdbcToService.mainInternal(null),
				IllegalArgumentException.class,
				"config file must be specified as first and only parameter");
	}

	@Test void testMissingProperty() throws IOException
	{
		final Properties props = new Properties();
		props.setProperty("source.url", "someUrl");
		final Path propsFile = files.newFile().toPath();
		writeProperties(props, propsFile);
		assertFails(
				() -> VaultJdbcToService.mainInternal(null, propsFile.toString()),
				IllegalPropertiesException.class,
				"property source.username in " +
				propsFile + " " +
				"must be specified as there is no default");
	}

	@Test void testUnusedProperty() throws IOException
	{
		final Properties props = new Properties();
		props.setProperty("unusedProperty", "whatever");
		props.setProperty("source.url", "someUrl");
		props.setProperty("source.username", "someUsername");
		props.setProperty("source.password", "somePassword");
		props.setProperty("source.query", "SELECT ");
		props.setProperty("target.default.service", UnusedPropertyService.class.getName());
		final Path propsFile = files.newFile().toPath();
		writeProperties(props, propsFile);
		final var out = new ByteArrayOutputStream();
		assertFails(
				() -> VaultJdbcToService.mainInternal(new PrintStream(out, true, US_ASCII), propsFile.toString()),
				IllegalArgumentException.class,
				"property unusedProperty in " +
				propsFile + " " +
				"is not allowed, but only one of [" +
				"source.url, source.username, source.password, source.query, source.fetchSize, " +
				"target.algorithm, " +
				"target.trail.startLimit, target.trail.fieldLimit, target.trail.originLimit, " +
				"target.buckets, target.default.service, " +
				"target.default.trail.startLimit, target.default.trail.fieldLimit, target.default.trail.originLimit, " +
				"target.isAppliedToAllFields, " +
				"targetProbesSuppressed].");
		assertEquals(List.of(
				"Fetch size set to 1"),
				readAllLines(out));
	}
	private static final class UnusedPropertyService extends AssertionErrorVaultService
	{
		UnusedPropertyService(@SuppressWarnings("unused") final VaultServiceParameters parameters)
		{
			throw new AssertionError();
		}
	}


	@Test void testProbeFailed() throws IOException
	{
		final Properties props = new Properties();
		props.setProperty("source.url", "someUrl");
		props.setProperty("source.username", "someUsername");
		props.setProperty("source.password", "somePassword");
		props.setProperty("source.query", "SELECT ");
		props.setProperty("target.default.service", TestServiceProbeFailed.class.getName());
		final Path propsFile = files.newFile().toPath();
		writeProperties(props, propsFile);
		final var out = new ByteArrayOutputStream();
		assertFails(
				() -> VaultJdbcToService.mainInternal(new PrintStream(out, true, US_ASCII), propsFile.toString()),
				IllegalArgumentException.class,
				"probeFails cause");
		assertEquals(List.of(
				"Fetch size set to 1",
				"Probing Fails ..."),
				readAllLines(out));
	}
	@ServiceProperties(TestServiceProbeFailed.Props.class)
	private static final class TestServiceProbeFailed extends AssertionErrorVaultService
	{
		TestServiceProbeFailed(
				@SuppressWarnings("unused") final VaultServiceParameters parameters,
				@SuppressWarnings("unused") final Props props)
		{
		}

		static final class Props extends com.exedio.cope.util.Properties
		{
			Props(final Source source)
			{
				super(source);
			}
			@Probe String probeFails()
			{
				throw new IllegalArgumentException("probeFails cause");
			}
		}
	}


	@Test void testProbeFailedChecked() throws IOException
	{
		final Properties props = new Properties();
		props.setProperty("source.url", "someUrl");
		props.setProperty("source.username", "someUsername");
		props.setProperty("source.password", "somePassword");
		props.setProperty("source.query", "SELECT ");
		props.setProperty("target.default.service", TestServiceProbeFailedChecked.class.getName());
		final Path propsFile = files.newFile().toPath();
		writeProperties(props, propsFile);
		final var out = new ByteArrayOutputStream();
		assertFails(
				() -> VaultJdbcToService.mainInternal(new PrintStream(out, true, US_ASCII), propsFile.toString()),
				RuntimeException.class,
				"java.io.IOException: probeFails cause");
		assertEquals(List.of(
				"Fetch size set to 1",
				"Probing Fails ..."),
				readAllLines(out));
	}
	@ServiceProperties(TestServiceProbeFailedChecked.Props.class)
	private static final class TestServiceProbeFailedChecked extends AssertionErrorVaultService
	{
		TestServiceProbeFailedChecked(
				@SuppressWarnings("unused") final VaultServiceParameters parameters,
				@SuppressWarnings("unused") final Props props)
		{
		}

		static final class Props extends com.exedio.cope.util.Properties
		{
			Props(final Source source)
			{
				super(source);
			}
			@Probe String probeFails() throws IOException
			{
				throw new IOException("probeFails cause");
			}
		}
	}


	static void writeProperties(final Properties props, final Path file) throws IOException
	{
		try(FileWriter w = new FileWriter(file.toFile(), US_ASCII))
		{
			props.store(w, null);
		}
	}

	protected final TemporaryFolder files = new TemporaryFolder();
}
