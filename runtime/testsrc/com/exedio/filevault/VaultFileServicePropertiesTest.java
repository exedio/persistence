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

import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Properties.Source;
import com.exedio.filevault.VaultFileService.Props;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class VaultFileServicePropertiesTest
{
	@Test void testDefaults()
	{
		final Source source = describe("DESC", cascade(
				single("root", "DONT_USE")
		));

		final Props p = new Props(source);
		assertEquals("rw-------", s(p.filePosixPermissions));
		assertEquals("rwx------", s(p.directory.posixPermissions));
		assertEquals(null, p.filePosixPermissionsAfterwards);
		assertEquals(null, p.directory.posixPermissionsAfterwards);
		assertEquals("", p.filePosixGroup);
		assertEquals("", p.directory.posixGroup);
	}

	@Test void testPosixPermissionsFile()
	{
		assertPosixPermissionsFile("---------", "rwx------");
		assertPosixPermissionsFile("r--------", "rwx------");
		assertPosixPermissionsFile("rw-------", "rwx------");
		assertPosixPermissionsFile("rw-r-----", "rwx--x---"); // a common case
		assertPosixPermissionsFile("r--r-----", "rwx--x---");
		assertPosixPermissionsFile("rw-rw----", "rwx--x---");
		assertPosixPermissionsFile("r--r--r--", "rwx--x--x");
		assertPosixPermissionsFile("rw-rw-rw-", "rwx--x--x");
		assertPosixPermissionsFile("rwxrwxrwx", "rwx--x--x");
	}
	private static void assertPosixPermissionsFile(final String file, final String directory)
	{
		final Source source = describe("DESC", cascade(
				single("root", "DONT_USE"),
				single("posixPermissions", file)
		));

		final Props p = new Props(source);
		assertEquals(file, s(p.filePosixPermissions));
		assertEquals(directory, s(p.directory.posixPermissions));
		assertEquals(null, p.filePosixPermissionsAfterwards);
		assertEquals(null, p.directory.posixPermissionsAfterwards);
	}
	@Test void testPosixPermissionsDirectory()
	{
		final Source source = describe("DESC", cascade(
				single("root", "DONT_USE"),
				single("directory.posixPermissions", "---------")
		));

		final Props p = new Props(source);
		assertEquals("rw-------", s(p.filePosixPermissions));
		assertEquals("---------", s(p.directory.posixPermissions));
		assertEquals(null, p.filePosixPermissionsAfterwards);
		assertEquals(null, p.directory.posixPermissionsAfterwards);
	}
	@Test void testPosixPermissionsFileAndDirectory()
	{
		final Source source = describe("DESC", cascade(
				single("root", "DONT_USE"),
				single("posixPermissions", "rwxrwxrwx"),
				single("directory.posixPermissions", "---------")
		));

		final Props p = new Props(source);
		assertEquals("rwxrwxrwx", s(p.filePosixPermissions));
		assertEquals("---------", s(p.directory.posixPermissions));
		assertEquals(null, p.filePosixPermissionsAfterwards);
		assertEquals(null, p.directory.posixPermissionsAfterwards);
	}
	private static String s(final Set<PosixFilePermission> perms)
	{
		return perms!=null ? PosixFilePermissions.toString(perms) : null;
	}

	@Test void testPosixGroupFile()
	{
		final Source source = describe("DESC", cascade(
				single("root", "DONT_USE"),
				single("posixGroup", "myFileValue")
		));

		final Props p = new Props(source);
		assertEquals("myFileValue", p.filePosixGroup);
		assertEquals("myFileValue", p.directory.posixGroup); // inherited from filePosixGroup
	}
	@Test void testPosixGroupDirectory()
	{
		final Source source = describe("DESC", cascade(
				single("root", "DONT_USE"),
				single("directory.posixGroup", "myDirValue")
		));

		final Props p = new Props(source);
		assertEquals("", p.filePosixGroup);
		assertEquals("myDirValue", p.directory.posixGroup);
	}
	@Test void testPosixGroupFileAndDirectory()
	{
		final Source source = describe("DESC", cascade(
				single("root", "DONT_USE"),
				single("posixGroup", "myFileValue"),
				single("directory.posixGroup", "myDirValue")
		));

		final Props p = new Props(source);
		assertEquals("myFileValue", p.filePosixGroup);
		assertEquals("myDirValue", p.directory.posixGroup);
	}
	@Test void testPosixGroupFileAndDirectoryEmpty()
	{
		final Source source = describe("DESC", cascade(
				single("root", "DONT_USE"),
				single("posixGroup", "myFileValue"),
				single("directory.posixGroup", "")
		));

		final Props p = new Props(source);
		assertEquals("myFileValue", p.filePosixGroup);
		assertEquals("", p.directory.posixGroup); // explicitly disabled directory.posixGroup still wins against filePosixGroup
	}
}
