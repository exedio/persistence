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
import static com.exedio.cope.tojunit.TestSources.single;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultFileService.Props;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.opentest4j.AssertionFailedError;

/**
 * @see VaultFileServiceNotWriteablePropsTest
 */
public class VaultFileServiceNotWriteableTest extends AbstractVaultFileServiceTest
{
	@Override
	protected VaultService maskService(final VaultService originalService)
	{
		final Props props = new Props(Sources.cascade(
				single("root", getRoot())));
		assertEquals(true, props.writable);
		return new VaultFileService(
				new VaultServiceParameters(VaultProperties.factory().create(Sources.cascade(
						single("algorithm", ALGORITHM),
						single("default.service", getServiceClass()),
						single("default.service.root", "DUMMY"))),
						"testBucket",
						false, // not writable
						() -> { throw new AssertionFailedError(); }), // markPut
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
		assertEquals(null, service.fileGroup);
		assertEquals("l=3", service.directory.toString());
		assertEquaFA(null, service.directoryAttributes());
		assertEquals(null, service.directoryPermissionsAfterwards);
		assertEquals(null, service.directoryGroup);
		assertEquals(null, service.tempDir);
	}

	@Test void putFails()
	{
		final VaultFileService service = (VaultFileService)getService();
		final String hash = hash("abcdef01234567");
		assertTempNull(
				() -> service.put(hash, new byte[]{1,2,3}, PUT_INFO));
		assertTempNull(
				() -> service.put(hash, new ByteArrayInputStream(new byte[]{1,2,3}), PUT_INFO));
		assertTempNull(
				() -> service.put(hash, Paths.get("putFails"), PUT_INFO));
	}

	private static void assertTempNull(final Executable executable)
	{
		final NullPointerException e = assertFails(
				executable,
				NullPointerException.class,
				null);
		final StackTraceElement steFiles = e.getStackTrace()[1];
		assertAll(
				() -> assertEquals("java.nio.file.Files", steFiles.getClassName()),
				() -> assertEquals("createTempFile", steFiles.getMethodName()));
		final StackTraceElement steService = e.getStackTrace()[2];
		assertAll(
				() -> assertEquals("com.exedio.cope.vault.VaultFileService", steService.getClassName()),
				() -> assertEquals("createTempFile", steService.getMethodName()));
	}
}
