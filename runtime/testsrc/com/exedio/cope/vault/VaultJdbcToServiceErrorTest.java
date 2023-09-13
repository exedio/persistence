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
import static java.nio.charset.StandardCharsets.US_ASCII;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.IllegalPropertiesException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
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
		props.setProperty("target.service", UnusedPropertyService.class.getName());
		final Path propsFile = files.newFile().toPath();
		writeProperties(props, propsFile);
		assertFails(
				() -> VaultJdbcToService.mainInternal(null, propsFile.toString()),
				IllegalArgumentException.class,
				"property unusedProperty in " +
				propsFile + " " +
				"is not allowed, but only one of [" +
				"source.url, source.username, source.password, source.query, " +
				"target.algorithm, target.services, target.service, " +
				"target.trail.startLimit, target.trail.fieldLimit, target.trail.originLimit, " +
				"target.isAppliedToAllFields].");
	}
	private static final class UnusedPropertyService extends AssertionErrorVaultService
	{
		UnusedPropertyService(@SuppressWarnings("unused") final VaultServiceParameters parameters)
		{
			throw new AssertionError();
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
