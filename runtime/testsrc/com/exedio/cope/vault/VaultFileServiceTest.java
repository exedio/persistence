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
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.junit.HolderExtension;
import com.exedio.cope.tojunit.AssertionFailedClock;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class VaultFileServiceTest extends AbstractVaultFileServiceTest
{
	private static final EnumSet<PosixFilePermission> filePerms = EnumSet.of(OWNER_READ, OWNER_WRITE);
	private static final EnumSet<PosixFilePermission> dirPerms  = EnumSet.of(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE);

	@Test void serviceProperties()
	{
		final VaultFileService service = (VaultFileService)getService();
		assertEquaFA("posix:permissions->[OWNER_READ, OWNER_WRITE]", service.fileAttributes());
		assertEquals(null, service.filePermissionsAfterwards);
		assertEquals("", service.fileGroup);
		assertEquals("l=3", service.directory.toString());
		assertEquaFA("posix:permissions->[OWNER_READ, OWNER_WRITE, OWNER_EXECUTE]", service.directoryAttributes());
		assertEquals(null, service.directoryPermissionsAfterwards);
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

		assertTrue(service.put("abcd", value, PUT_INFO));
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d);
		assertTrue(d.isFile());
		assertFalse(f.exists());
		assertPosix(dirPerms, rootGroup(), abc);
		assertPosix(filePerms, rootGroup(), d);

		assertFalse(service.put("abcd", value, PUT_INFO));
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d);
		assertTrue(d.isFile());
		assertFalse(f.exists());
		assertPosix(dirPerms, rootGroup(), abc);
		assertPosix(filePerms, rootGroup(), d);

		assertTrue(service.put("abcf", value, PUT_INFO));
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d, f);
		assertTrue(d.isFile());
		assertTrue(f.isFile());
		assertPosix(dirPerms, rootGroup(), abc);
		assertPosix(filePerms, rootGroup(), d);
		assertPosix(filePerms, rootGroup(), f);
	}

	@ExtendWith(MarkPutClock.class)
	@Test void putRedundantAndMarkFile(final MarkPutClock clock) throws IOException
	{
		clock.override(new AssertionFailedClock());

		final Path file = getRoot().toPath().resolve("abc").resolve("d");
		assertFalse(Files.exists(file));

		final byte[] value = {1,2,3};
		final VaultFileService service = (VaultFileService)getService();
		assertTrue(service.put("abcd", value, PUT_INFO));
		assertTrue(Files.isRegularFile(file));

		assertFalse(service.put("abcd", value, PUT_INFO));

		markPut = true;
		clock.override(new AssertionFailedClock()
		{
			@Override
			public long millis()
			{
				return markPutInstant.toEpochMilli();
			}
		});
		final Instant instant1 = LocalDateTime.of(2022, JULY, 23, 6, 7, 32).toInstant(UTC);
		markPutInstant = instant1;
		assertFalse(service.put("abcd", value, PUT_INFO));
		assertEquals(FileTime.from(instant1), getLastModifiedTime(file));

		final Instant instant2 = LocalDateTime.of(2021, JUNE, 22, 5, 6, 21).toInstant(UTC);
		markPutInstant = instant2;
		markPut = false;
		assertFalse(service.put("abcd", value, PUT_INFO));
		assertEquals(FileTime.from(instant1), getLastModifiedTime(file));

		markPut = true;
		assertFalse(service.put("abcd", value, PUT_INFO));
		assertEquals(FileTime.from(instant2), getLastModifiedTime(file));
	}

	private Instant markPutInstant = null;

	public static final class MarkPutClock extends HolderExtension<Clock>
	{
		public MarkPutClock()
		{
			super(VaultFileService.markRedundantPutClock);
		}
	}

	@Test void notFoundAnonymousLength()
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
				NoSuchFileException.class.getName() + ": " + getRoot() + File.separator + "abc" + File.separator + "defghijklmnopxx17",
				cause.getMessage());
		assertEquals(VaultNotFoundException.class.getName() + "$Anonymous", cause.getClass().getName());
		assertNull(cause.getCause());
	}
	@Test void notFoundAnonymousBytes()
	{
		final VaultFileService service = (VaultFileService)getService();
		final VaultNotFoundException notFound = assertFails(
				() -> service.get("abcdefghijklmnopq"),
				VaultNotFoundException.class,
				"hash not found in vault: abcdefghijklmnopxx17");
		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause = notFound.getCause();
		assertEquals(
				NoSuchFileException.class.getName() + ": " + getRoot() + File.separator + "abc" + File.separator + "defghijklmnopxx17",
				cause.getMessage());
		assertEquals(VaultNotFoundException.class.getName() + "$Anonymous", cause.getClass().getName());
		assertNull(cause.getCause());
	}
	@Test void notFoundAnonymousSink()
	{
		final VaultFileService service = (VaultFileService)getService();
		final ByteArrayOutputStream sink = new ByteArrayOutputStream();
		final VaultNotFoundException notFound = assertFails(
				() -> service.get("abcdefghijklmnopq", sink),
				VaultNotFoundException.class,
				"hash not found in vault: abcdefghijklmnopxx17");
		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());

		final Throwable cause = notFound.getCause();
		assertEquals(
				NoSuchFileException.class.getName() + ": " + getRoot() + File.separator + "abc" + File.separator + "defghijklmnopxx17",
				cause.getMessage());
		assertEquals(VaultNotFoundException.class.getName() + "$Anonymous", cause.getClass().getName());
		assertNull(cause.getCause());
	}

	@ExtendWith(MoveIfDestDoesNotExistPrelude.class)
	@Test void raceConditionPutFile(final MoveIfDestDoesNotExistPrelude prelude) throws IOException
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
		final VaultFileService service = (VaultFileService)getService();

		prelude.override(dest ->
		{
			try
			{
				Files.createFile(dest);
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
		});
		service.put("abcd", value, PUT_INFO);
		assertContains(root, temp, abc);
		assertContains(temp);
		assertContains(abc, d);
		assertTrue(d.isFile());
		assertArrayEquals(value, readAllBytes(d.toPath()));
		assertPosix(dirPerms, rootGroup(), abc);
		assertPosix(filePerms, rootGroup(), d);
	}

	public static final class MoveIfDestDoesNotExistPrelude extends HolderExtension<Consumer<Path>>
	{
		public MoveIfDestDoesNotExistPrelude()
		{
			super(VaultFileService.moveIfDestDoesNotExistPrelude);
		}
	}
}
