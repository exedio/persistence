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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class VaultFileServiceFlatTest extends AbstractVaultFileServiceTest
{
	@Override
	protected Properties getServiceProperties() throws IOException
	{
		final Properties result = super.getServiceProperties();
		result.setProperty("directory", "false");
		return result;
	}

	@Test void serviceProperties()
	{
		final VaultFileService service = (VaultFileService)getService();
		assertEquaFA("posix:permissions->[OWNER_READ, OWNER_WRITE]", service.fileAttributes());
		assertEquals(null, service.filePermissionsAfterwards);
		assertEquals("", service.fileGroup);
		assertEquals("flat", service.directory.toString());
		assertEquaFA(null, service.directoryAttributes());
		assertEquals(null, service.directoryPermissionsAfterwards);
		assertEquals(null, service.directoryGroup);
		assertNotNull(service.tempDir);
	}

	@Test void directoryStructure()
	{
		final File root = getRoot();
		final File temp = new File(root, ".tempVaultFileService");
		assertTrue(temp.isDirectory());

		final File d = new File(root, "abcd");
		final File f = new File(root, "abcf");
		assertContains(root, temp);
		assertContains(temp);
		assertFalse(d.exists());
		assertFalse(f.exists());

		final byte[] value = {1,2,3};
		final VaultFileService service = (VaultFileService)getService();

		assertTrue(service.put("abcd", value, PUT_INFO));
		assertContains(root, temp, d);
		assertContains(temp);
		assertTrue(d.isFile());
		assertFalse(f.exists());

		assertFalse(service.put("abcd", value, PUT_INFO));
		assertContains(root, temp, d);
		assertContains(temp);
		assertTrue(d.isFile());
		assertFalse(f.exists());

		assertTrue(service.put("abcf", value, PUT_INFO));
		assertContains(root, temp, d, f);
		assertContains(temp);
		assertTrue(d.isFile());
		assertTrue(f.isFile());
	}

	@Test void notFoundAnonymous()
	{
		final VaultFileService service = (VaultFileService)getService();
		final VaultNotFoundException notFound = assertFails(
				() -> service.getLength("abcdefghijklmnopq"),
				VaultNotFoundException.class,
				"hash not found in vault: abcdefghijklmnopxx17");
		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause = notFound.getCause();
		assertEquals(
				NoSuchFileException.class.getName() + ": " + getRoot() + File.separator + "abcdefghijklmnopxx17",
				cause.getMessage());
		assertEquals(VaultNotFoundException.class.getName() + "$Anonymous", cause.getClass().getName());
		assertNull(cause.getCause());
	}
}
