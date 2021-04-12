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
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Factory;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class VaultFileServiceErrorTest
{
	@Test void ok()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("service", VaultFileService.class.getName());
		source.setProperty("service.root", "rootDir");
		source.setProperty("service.directory.length", "31");
		source.setProperty("service.temp", "t");

		final VaultProperties properties = VaultProperties.factory().create(Sources.view(source, "DESC"));
		assertEquals(asList(
				"algorithm",
				"services",
				"service",
				"service.root",
				"service.writable",
				"service.directory",
				"service.directory.length",
				"service.directory.premised",
				"service.temp",
				"isAppliedToAllFields"),
				properties.getFields().stream().map(Field::getKey).collect(toList()));

		@SuppressWarnings({"resource", "deprecation"})
		final VaultFileService service = (VaultFileService)properties.newService();

		assertEquals("l=31", service.directory.toString());
		assertEquals(Paths.get("rootDir/t"), service.tempDir);
	}

	@Test void notWritable()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("service", VaultFileService.class.getName());
		source.setProperty("service.root", "rootDir");
		source.setProperty("service.writable", "false");

		final VaultProperties properties = VaultProperties.factory().create(Sources.view(source, "DESC"));
		assertEquals(asList(
				"algorithm",
				"services",
				"service",
				"service.root",
				"service.writable",
				"service.directory",
				"service.directory.length",
				"isAppliedToAllFields"),
				properties.getFields().stream().map(Field::getKey).collect(toList()));

		final RuntimeException e2 = assertFails(
				properties::newServices,
				RuntimeException.class,
				"com.exedio.cope.vault.VaultFileService(com.exedio.cope.vault.VaultServiceParameters,com.exedio.cope.vault.VaultFileService$Props)");
		final IllegalArgumentException e = (IllegalArgumentException)e2.getCause().getCause();
		assertEquals(
				"non-writable properties cannot be used in writable service",
				e.getMessage());
	}

	@Test void directoryLengthTooLong()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("service", VaultFileService.class.getName());
		source.setProperty("service.root", "rootDir");
		source.setProperty("service.directory.length", "32");

		final VaultProperties properties = VaultProperties.factory().create(Sources.view(source, "DESC"));
		final RuntimeException e2 = assertFails(
				properties::newServices,
				RuntimeException.class,
				"com.exedio.cope.vault.VaultFileService(com.exedio.cope.vault.VaultServiceParameters,com.exedio.cope.vault.VaultFileService$Props)");
		final IllegalArgumentException e = (IllegalArgumentException)e2.getCause().getCause();
		assertEquals(
				"directory.length must be less the length of algorithm MD5, but was 32>=32",
				e.getMessage());
	}

	@Test void tempEmpty()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("service", VaultFileService.class.getName());
		source.setProperty("service.root", "rootDir");
		source.setProperty("service.temp", "");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property service.temp in DESC must not be empty");
	}

	@Test void tempTrim()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("service", VaultFileService.class.getName());
		source.setProperty("service.root", "rootDir");
		source.setProperty("service.temp", " x");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property service.temp in DESC must be trimmed, but was > x<");
	}
}
