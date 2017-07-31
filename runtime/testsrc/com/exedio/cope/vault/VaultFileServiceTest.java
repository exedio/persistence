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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.junit.Test;

public class VaultFileServiceTest extends AbstractVaultFileServiceTest
{
	@Override
	protected Properties getServiceProperties() throws IOException
	{
		final Properties result = super.getServiceProperties();
		result.setProperty("directory", "false");
		return result;
	}

	@Test public void serviceProperties()
	{
		final VaultFileService service = (VaultFileService)getService();
		assertEquals(0, service.directoryLength);
		assertEquals(2, service.bufferSize);
	}

	@Test public void directoryStructure()
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

		assertTrue(service.put("abcd", value));
		assertContains(root, temp, d);
		assertContains(temp);
		assertTrue(d.isFile());
		assertFalse(f.exists());

		assertFalse(service.put("abcd", value));
		assertContains(root, temp, d);
		assertContains(temp);
		assertTrue(d.isFile());
		assertFalse(f.exists());

		assertTrue(service.put("abcf", value));
		assertContains(root, temp, d, f);
		assertContains(temp);
		assertTrue(d.isFile());
		assertTrue(f.isFile());
	}
}
