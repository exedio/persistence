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

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.GROUP_WRITE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import static java.nio.file.attribute.PosixFilePermissions.asFileAttribute;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.junit.HolderExtension;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Properties;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class VaultFileServicePosixPermissionAfterwardsTest extends AbstractVaultFileServiceTest
{
	private EnumSet<PosixFilePermission> filePerms;
	private EnumSet<PosixFilePermission> dirPerms;

	@Override
	protected Properties getServiceProperties() throws IOException
	{
		filePerms = EnumSet.allOf(PosixFilePermission.class);
		dirPerms  = EnumSet.allOf(PosixFilePermission.class);
		filePerms.remove(GROUP_WRITE);
		dirPerms .remove(GROUP_EXECUTE);
		final Properties result = super.getServiceProperties();
		result.setProperty(          "posixPermissionsAfterwards", "rwxr-xrwx");
		result.setProperty("directory.posixPermissionsAfterwards", "rwxrw-rwx");
		return result;
	}

	@BeforeEach
	void assumePosixBeforeEach()
	{
		assumePosix();
	}

	@Test void serviceProperties()
	{
		final VaultFileService service = (VaultFileService)getService();
		assertEquaFA("posix:permissions->[OWNER_READ, OWNER_WRITE]", service.fileAttributes());
		assertEqualsUnmodifiable(filePerms, service.filePermissionsAfterwards);
		assertEquals("", service.fileGroup);
		assertEquals("l=3", service.directory.toString());
		assertEquaFA("posix:permissions->[OWNER_READ, OWNER_WRITE, OWNER_EXECUTE]", service.directoryAttributes());
		assertEqualsUnmodifiable(dirPerms, service.directoryPermissionsAfterwards);
		assertEquals("", service.directoryGroup);
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

		assertTrue(service.put("abcd", value));
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d);
		assertTrue(d.isFile());
		assertFalse(f.exists());
		assertPosix(dirPerms, rootGroup(), abc);
		assertPosix(filePerms, rootGroup(), d);

		assertFalse(service.put("abcd", value));
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d);
		assertTrue(d.isFile());
		assertFalse(f.exists());
		assertPosix(dirPerms, rootGroup(), abc);
		assertPosix(filePerms, rootGroup(), d);

		// test that directory.posixPermissionsAfterwards is not applied to already existing directories
		final EnumSet<PosixFilePermission> reducedPerms = EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE);
		Files.getFileAttributeView(abc.toPath(), PosixFileAttributeView.class).setPermissions(reducedPerms);

		assertTrue(service.put("abcf", value));
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d, f);
		assertTrue(d.isFile());
		assertTrue(f.isFile());
		assertPosix(reducedPerms, rootGroup(), abc);
		assertPosix(filePerms, rootGroup(), d);
		assertPosix(filePerms, rootGroup(), f);
	}

	@Test void putByStream() throws IOException
	{
		final VaultFileService service = (VaultFileService)getService();
		final File abc = new File(getRoot(), "abc");
		final InputStream value = new ByteArrayInputStream(new byte[]{1,2,3});
		final File valueFile = new File(abc, "def");
		assertFalse(valueFile.isFile());

		assertTrue(service.put("abcdef", value));
		assertContains(abc, valueFile);
		assertTrue(valueFile.isFile());
		assertPosix(filePerms, rootGroup(), valueFile);

		assertFalse(service.put("abcdef", value));
		assertContains(abc, valueFile);
		assertTrue(valueFile.isFile());
		assertPosix(filePerms, rootGroup(), valueFile);
	}

	@Test void putByPath() throws IOException
	{
		final VaultFileService service = (VaultFileService)getService();
		final File abc = new File(getRoot(), "abc");
		final Path value = files.newFile().toPath();
		Files.write(value, new byte[]{1,2,3});
		final File valueFile = new File(abc, "def");
		assertFalse(valueFile.isFile());

		assertTrue(service.put("abcdef", value));
		assertContains(abc, valueFile);
		assertTrue(valueFile.isFile());
		assertPosix(filePerms, rootGroup(), valueFile);

		assertFalse(service.put("abcdef", value));
		assertContains(abc, valueFile);
		assertTrue(valueFile.isFile());
		assertPosix(filePerms, rootGroup(), valueFile);
	}

	@ExtendWith(CreateDirectoryIfNotExistsPrelude.class)
	@Test void raceConditionPutDirectoryEmpty(final CreateDirectoryIfNotExistsPrelude prelude) throws IOException
	{
		final File root = getRoot();
		final File temp = new File(root, ".tempVaultFileService");
		assertTrue(temp.isDirectory());

		final File abc = new File(root, "abc");
		final File d = new File(abc, "d");
		assertContains(root, temp);
		assertContains(temp);
		assertFalse(abc.exists());
		assertFalse(d.exists());

		final byte[] value = {1,2,3};
		final var extraDirPermissions = EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE);
		final VaultFileService service = (VaultFileService)getService();

		prelude.override(dir ->
		{
			try
			{
				Files.createDirectory(dir, asFileAttribute(extraDirPermissions));
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
			assertFalse(createDirectoryIfNotExistsPreludeCalled);
			createDirectoryIfNotExistsPreludeCalled = true;
		});
		assertFalse(createDirectoryIfNotExistsPreludeCalled);
		service.put("abcd", value);
		assertTrue(createDirectoryIfNotExistsPreludeCalled);
		log.assertEmpty();
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d);
		assertTrue(d.isFile());
		assertArrayEquals(value, readAllBytes(d.toPath()));
		assertPosix(dirPerms, rootGroup(), abc);
		assertPosix(filePerms, rootGroup(), d);
	}

	@ExtendWith(CreateDirectoryIfNotExistsPrelude.class)
	@Test void raceConditionPutDirectoryNonEmpty(final CreateDirectoryIfNotExistsPrelude prelude) throws IOException
	{
		final File root = getRoot();
		final File temp = new File(root, ".tempVaultFileService");
		assertTrue(temp.isDirectory());

		final File abc = new File(root, "abc");
		final File d = new File(abc, "d");
		assertContains(root, temp);
		assertContains(temp);
		assertFalse(abc.exists());
		assertFalse(d.exists());

		final byte[] value = {1,2,3};
		final var extraDirPermissions = EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE);
		final VaultFileService service = (VaultFileService)getService();

		prelude.override(dir ->
		{
			try
			{
				Files.createDirectory(dir, asFileAttribute(extraDirPermissions));
				Files.createFile(dir.resolve("dirMustNotBeEmpty"));
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
			assertFalse(createDirectoryIfNotExistsPreludeCalled);
			createDirectoryIfNotExistsPreludeCalled = true;
		});
		assertFalse(createDirectoryIfNotExistsPreludeCalled);
		service.put("abcd", value);
		assertTrue(createDirectoryIfNotExistsPreludeCalled);
		log.assertError("concurrent directory creation (should happen rarely)");
		log.assertEmpty();
		assertContains(root, temp, abc);
		final File[] tempFiles = temp.listFiles();
		assertEquals(1, tempFiles.length);
		assertTrue(tempFiles[0].getName().startsWith("abcd-"), tempFiles[0].getName());
		assertContains(abc, d, new File(abc, "dirMustNotBeEmpty"));
		assertTrue(d.isFile());
		assertArrayEquals(value, readAllBytes(d.toPath()));
		assertPosix(extraDirPermissions, rootGroup(), abc);
		assertPosix(filePerms, rootGroup(), d);
	}

	public static final class CreateDirectoryIfNotExistsPrelude extends HolderExtension<Consumer<Path>>
	{
		public CreateDirectoryIfNotExistsPrelude()
		{
			super(VaultFileService.createDirectoryIfNotExistsPrelude);
		}
	}

	private boolean createDirectoryIfNotExistsPreludeCalled = false;
}
