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

package com.exedio.filevault;

import static com.exedio.cope.Vault.DEFAULT;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Factory;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultProperties;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class VaultFileServiceErrorTest
{
	@Test void ok()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.directory.length", "31");
		source.setProperty("default.service.temp", "t");

		final VaultProperties properties = VaultProperties.factory().create(Sources.view(source, "DESC"));
		assertEquals(asList(
				"trail.startLimit",
				"trail.fieldLimit",
				"trail.originLimit",
				"buckets",
				"default.algorithm",
				"default.service",
				"default.service.root",
				"default.service.content",
				"default.service.writable",
				"default.service.posixPermissions",
				"default.service.posixPermissionsAfterwards",
				"default.service.posixGroup",
				"default.service.directory",
				"default.service.directory.length",
				"default.service.directory.premised",
				"default.service.directory.posixPermissions",
				"default.service.directory.posixPermissionsAfterwards",
				"default.service.directory.posixGroup",
				"default.service.temp",
				"default.trail.startLimit",
				"default.trail.fieldLimit",
				"default.trail.originLimit",
				"isAppliedToAllFields"),
				properties.getFields().stream().map(Field::getKey).collect(toList()));

		final VaultFileService service = (VaultFileService)properties.newServicesNonResilient(DEFAULT).get(DEFAULT);

		assertEquals("l=31", service.directory.toString());
		assertEquals(Paths.get("rootDir/t"), service.tempDir);
	}

	@Test void notWritable()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.writable", "false");

		final VaultProperties properties = VaultProperties.factory().create(Sources.view(source, "DESC"));
		assertEquals(asList(
				"trail.startLimit",
				"trail.fieldLimit",
				"trail.originLimit",
				"buckets",
				"default.algorithm",
				"default.service",
				"default.service.root",
				"default.service.content",
				"default.service.writable",
				"default.service.directory",
				"default.service.directory.length",
				"default.trail.startLimit",
				"default.trail.fieldLimit",
				"default.trail.originLimit",
				"isAppliedToAllFields"),
				properties.getFields().stream().map(Field::getKey).collect(toList()));

		final RuntimeException e2 = assertFails(
				properties::newServices,
				RuntimeException.class,
				"com.exedio.filevault.VaultFileService(com.exedio.cope.vault.VaultServiceParameters,com.exedio.filevault.VaultFileService$Props)");
		final IllegalArgumentException e = (IllegalArgumentException)e2.getCause().getCause();
		assertEquals(
				"non-writable properties cannot be used in writable service",
				e.getMessage());
	}

	@Test void posixBroken()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.posixPermissions", "zack");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property default.service.posixPermissions in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was 'zack'");
	}

	@Test void posixEmpty()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.posixPermissions", "");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property default.service.posixPermissions in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was ''");
	}

	@Test void posixAfterwardsBroken()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.posixPermissionsAfterwards", "zack");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property default.service.posixPermissionsAfterwards in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was 'zack'");
	}

	@Test void posixDirectoryBroken()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.directory.posixPermissions", "zack");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property default.service.directory.posixPermissions in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was 'zack'");
	}

	@Test void posixDirectoryEmpty()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.directory.posixPermissions", "");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property default.service.directory.posixPermissions in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was ''");
	}

	@Test void posixDirectoryAfterwardsBroken()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.directory.posixPermissionsAfterwards", "zack");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property default.service.directory.posixPermissionsAfterwards in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was 'zack'");
	}

	@Test void directoryLengthTooLong()
	{
		final Properties source = new Properties();
		source.setProperty("default.algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.directory.length", "32");

		final VaultProperties properties = VaultProperties.factory().create(Sources.view(source, "DESC"));
		final RuntimeException e2 = assertFails(
				properties::newServices,
				RuntimeException.class,
				"com.exedio.filevault.VaultFileService(com.exedio.cope.vault.VaultServiceParameters,com.exedio.filevault.VaultFileService$Props)");
		final IllegalArgumentException e = (IllegalArgumentException)e2.getCause().getCause();
		assertEquals(
				"directory.length must be less the length of algorithm MD5, but was 32>=32",
				e.getMessage());
	}

	@Test void contentTrim()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.content", " x");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property default.service.content in DESC must be trimmed, but was > x<");
	}

	@Test void tempEmpty()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.temp", "");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property default.service.temp in DESC must not be empty");
	}

	@Test void tempTrim()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("default.service", VaultFileService.class.getName());
		source.setProperty("default.service.root", "rootDir");
		source.setProperty("default.service.temp", " x");

		final Factory<VaultProperties> factory = VaultProperties.factory();
		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> factory.create(sourceView),
				IllegalPropertiesException.class,
				"property default.service.temp in DESC must be trimmed, but was > x<");
	}
}
