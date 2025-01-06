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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.BucketProperties;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.filevault.VaultFileService.Props;
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

		final BucketProperties properties = BucketProperties.create("myBucketKey", true, Sources.view(source, "DESC"));
		assertEquals(asList(
				"algorithm",
				"service",
				"service.root",
				"service.content",
				"service.writable",
				"service.posixPermissions",
				"service.posixPermissionsAfterwards",
				"service.posixGroup",
				"service.directory",
				"service.directory.length",
				"service.directory.premised",
				"service.directory.posixPermissions",
				"service.directory.posixPermissionsAfterwards",
				"service.directory.posixGroup",
				"service.temp",
				"trail.startLimit",
				"trail.fieldLimit",
				"trail.originLimit"),
				properties.getFields().stream().map(Field::getKey).collect(toList()));
		//noinspection resource
		final VaultFileService service = (VaultFileService)properties.newServiceNonResilient(() -> false);

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

		final BucketProperties properties = BucketProperties.create("myBucketKey", true, Sources.view(source, "DESC"));
		assertEquals(asList(
				"algorithm",
				"service",
				"service.root",
				"service.content",
				"service.writable",
				"service.directory",
				"service.directory.length",
				"trail.startLimit",
				"trail.fieldLimit",
				"trail.originLimit"),
				properties.getFields().stream().map(Field::getKey).collect(toList()));
		//noinspection resource
		final RuntimeException e2 = assertFails(
				() -> properties.newServiceNonResilient(() -> false),
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
		source.setProperty("root", "rootDir");
		source.setProperty("posixPermissions", "zack");

		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> new Props(sourceView),
				IllegalPropertiesException.class,
				"property posixPermissions in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was 'zack'");
	}

	@Test void posixEmpty()
	{
		final Properties source = new Properties();
		source.setProperty("root", "rootDir");
		source.setProperty("posixPermissions", "");

		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> new Props(sourceView),
				IllegalPropertiesException.class,
				"property posixPermissions in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was ''");
	}

	@Test void posixAfterwardsBroken()
	{
		final Properties source = new Properties();
		source.setProperty("root", "rootDir");
		source.setProperty("posixPermissionsAfterwards", "zack");

		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> new Props(sourceView),
				IllegalPropertiesException.class,
				"property posixPermissionsAfterwards in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was 'zack'");
	}

	@Test void posixDirectoryBroken()
	{
		final Properties source = new Properties();
		source.setProperty("root", "rootDir");
		source.setProperty("directory.posixPermissions", "zack");

		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> new Props(sourceView),
				IllegalPropertiesException.class,
				"property directory.posixPermissions in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was 'zack'");
	}

	@Test void posixDirectoryEmpty()
	{
		final Properties source = new Properties();
		source.setProperty("root", "rootDir");
		source.setProperty("directory.posixPermissions", "");

		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> new Props(sourceView),
				IllegalPropertiesException.class,
				"property directory.posixPermissions in DESC " +
				"must be posix file permissions according to PosixFilePermissions.fromString, " +
				"but was ''");
	}

	@Test void posixDirectoryAfterwardsBroken()
	{
		final Properties source = new Properties();
		source.setProperty("root", "rootDir");
		source.setProperty("directory.posixPermissionsAfterwards", "zack");

		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> new Props(sourceView),
				IllegalPropertiesException.class,
				"property directory.posixPermissionsAfterwards in DESC " +
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
		source.setProperty("root", "rootDir");
		source.setProperty("content", " x");

		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> new Props(sourceView),
				IllegalPropertiesException.class,
				"property content in DESC must be trimmed, but was > x<");
	}

	@Test void tempEmpty()
	{
		final Properties source = new Properties();
		source.setProperty("root", "rootDir");
		source.setProperty("temp", "");

		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> new Props(sourceView),
				IllegalPropertiesException.class,
				"property temp in DESC must not be empty");
	}

	@Test void tempTrim()
	{
		final Properties source = new Properties();
		source.setProperty("root", "rootDir");
		source.setProperty("temp", " x");

		final Source sourceView = Sources.view(source, "DESC");
		assertFails(
				() -> new Props(sourceView),
				IllegalPropertiesException.class,
				"property temp in DESC must be trimmed, but was > x<");
	}
}
