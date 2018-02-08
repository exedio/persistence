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

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VaultFileServiceDirectoryNotCreatedAsNeededTest extends AbstractVaultFileServiceTest
{
	@Override
	protected Properties getServiceProperties() throws IOException
	{
		final Properties result = super.getServiceProperties();
		result.setProperty("directory.createAsNeeded", "false");
		return result;
	}

	@BeforeEach
	final void setUpVaultFileServiceDirectoryNotCreatedAsNeededTest() throws IOException
	{
		final Path root = getRoot().toPath();
		createDirectory(root.resolve("bf6"));
		createDirectory(root.resolve("bd9"));
	}

	@Test void serviceProperties()
	{
		final VaultFileService service = (VaultFileService)getService();
		assertEquals(3, service.directoryLength);
		assertEquals(false, service.directoryCreate);
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
