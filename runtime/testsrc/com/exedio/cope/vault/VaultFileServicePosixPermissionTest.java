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

import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class VaultFileServicePosixPermissionTest extends AbstractVaultFileServiceTest
{
	@Override
	protected Properties getServiceProperties() throws IOException
	{
		final Properties result = super.getServiceProperties();
		result.setProperty("posixPermissions", "rw-r-----");
		result.setProperty("directory.posixPermissions", "rwx---r-x");
		return result;
	}

	@Test void serviceProperties()
	{
		final VaultFileService service = (VaultFileService)getService();
		assertEquaFA("posix:permissions->[OWNER_READ, OWNER_WRITE, GROUP_READ]", service.fileAttributes());
		assertEquals("l=3", service.directory.toString());
		assertEquaFA("posix:permissions->[OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, OTHERS_READ, OTHERS_EXECUTE]", service.directoryAttributes());
		assertNotNull(service.tempDir);
	}

	@Test void directoryStructure() throws IOException
	{
		final File root = getRoot();
		final File temp = new File(root, ".tempVaultFileService");
		assertTrue(temp.isDirectory());

		final File abc = new File(root, "abc");
		final File d = new File(abc, "d");
		final File f = new File(abc, "f");
		assertContains(root, temp);
		assertContains(temp);
		assertFalse(abc.exists());
		assertFalse(d.exists());
		assertFalse(f.exists());

		final byte[] value = {1,2,3};
		final VaultFileService service = (VaultFileService)getService();

		assertTrue(service.put("abcd", value, PUT_INFO));
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d);
		assertTrue(d.isFile());
		assertFalse(f.exists());
		assertPosixPermissions(EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, OTHERS_READ, OTHERS_EXECUTE), abc);
		assertPosixPermissions(EnumSet.of(OWNER_READ, OWNER_WRITE, GROUP_READ), d);

		assertFalse(service.put("abcd", value, PUT_INFO));
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d);
		assertTrue(d.isFile());
		assertFalse(f.exists());
		assertPosixPermissions(EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, OTHERS_READ, OTHERS_EXECUTE), abc);
		assertPosixPermissions(EnumSet.of(OWNER_READ, OWNER_WRITE, GROUP_READ), d);

		assertTrue(service.put("abcf", value, PUT_INFO));
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d, f);
		assertTrue(d.isFile());
		assertTrue(f.isFile());
		assertPosixPermissions(EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE, OTHERS_READ, OTHERS_EXECUTE), abc);
		assertPosixPermissions(EnumSet.of(OWNER_READ, OWNER_WRITE, GROUP_READ), d);
		assertPosixPermissions(EnumSet.of(OWNER_READ, OWNER_WRITE, GROUP_READ), f);
	}
}
