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
import static java.nio.file.Files.createDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VaultFileServicePremisedTest extends AbstractVaultFileServiceTest
{
	@Override
	protected Properties getServiceProperties() throws IOException
	{
		final Properties result = super.getServiceProperties();
		result.setProperty("directory.premised", "true");
		return result;
	}

	@BeforeEach
	final void setUpVaultFileServicePremisedTest() throws IOException
	{
		final Path root = getRoot().toPath();
		createDirectory(root.resolve("bf6"));
		createDirectory(root.resolve("bd9"));
	}

	@Test void serviceProperties()
	{
		final VaultFileService service = (VaultFileService)getService();
		assertEquaFA("posix:permissions->[OWNER_READ, OWNER_WRITE]", service.fileAttributes());
		assertEquals(null, service.filePermissionsAfterwards);
		assertEquals("l=3 premised", service.directory.toString());
		assertEquaFA(null, service.directoryAttributes());
		assertEquals(null, service.directoryPermissionsAfterwards);
		assertNotNull(service.tempDir);
	}

	@Test void directoryDoesNotExist() throws IOException
	{
		final byte[] value = {1,2,3};
		final VaultFileService service = (VaultFileService)getService();
		final Path root = getRoot().toPath();

		final RuntimeException e = assertFails(
				() -> service.put("abcd", value, PUT_INFO),
				RuntimeException.class,
				root + ":abcd");
		assertEquals(NoSuchFileException.class, e.getCause().getClass());

		createDirectory(root.resolve("abc"));
		assertEquals(true,  service.put("abcd", value, PUT_INFO));
		assertEquals(false, service.put("abcd", value, PUT_INFO));
	}
}
