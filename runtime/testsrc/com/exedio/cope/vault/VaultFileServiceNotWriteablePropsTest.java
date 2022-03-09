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

import static com.exedio.cope.tojunit.TestSources.single;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultFileService.Props;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see VaultFileServiceNotWriteableTest
 */
public class VaultFileServiceNotWriteablePropsTest extends AbstractVaultFileServiceTest
{
	@Override
	protected VaultService maskService(final VaultService originalService)
	{
		final Props props = new Props(Sources.cascade(
				single("root", getRoot()),
				single("writable", false)));
		assertEquals(false, props.writable);
		return new VaultFileService(
				new VaultServiceParameters(VaultProperties.factory().create(Sources.cascade(
						single("algorithm", ALGORITHM),
						single("service", getServiceClass()),
						single("service.root", "DUMMY"))),
						"testServiceKey",
						false), // not writable
				props);
	}

	@Override
	protected VaultService maskServicePut(final VaultService service)
	{
		putService = (VaultFileService)service;
		return service;
	}

	@BeforeEach
	final void setUp() throws IOException
	{
		// is not created by AbstractVaultFileServiceTest because service is not writable, thus has no tempDir
		Files.createDirectory(putService.tempDir);
	}

	private VaultFileService putService;

	@Test void serviceProperties()
	{
		final VaultFileService service = (VaultFileService)getService();
		assertEquaFA(null, service.fileAttributes());
		assertEquals(null, service.filePermissionsAfterwards);
		assertEquals("l=3", service.directory.toString());
		assertEquaFA(null, service.directoryAttributes());
		assertEquals(null, service.directoryPermissionsAfterwards);
		assertEquals(null, service.tempDir);
	}
}
