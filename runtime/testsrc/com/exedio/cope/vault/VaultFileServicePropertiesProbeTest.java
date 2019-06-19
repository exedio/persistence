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
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermissions.asFileAttribute;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Properties.ProbeAbortedException;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vault.VaultFileService.Props;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@MainRule.Tag
public class VaultFileServicePropertiesProbeTest
{
	private final TemporaryFolder files = new TemporaryFolder();

	private File sandbox;

	@BeforeEach void setUp() throws IOException
	{
		sandbox = files.newFolder();
	}

	@Test void probe() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		final Iterator<? extends Callable<?>> probes = p.getProbes().iterator();
		assertEquals("directory.Exists", probes.next().toString());
		final Callable<?> rootExists = probes.next();
		final Callable<?> rootFree   = probes.next();
		final Callable<?> tempExists = probes.next();
		final Callable<?> tempStore  = probes.next();
		assertFalse(probes.hasNext());

		assertEquals("root.Exists", rootExists.toString());
		assertEquals("root.Free",   rootFree  .toString());
		assertEquals("temp.Exists", tempExists.toString());
		assertEquals("temp.Store",  tempStore .toString());

		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"does not exist: " + p.root.toAbsolutePath());
		assertFails(
				rootFree::call,
				NoSuchFileException.class,
				p.root.toAbsolutePath().toString());
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + p.tempDir().toAbsolutePath());
		assertFails(
				tempStore::call,
				NoSuchFileException.class,
				p.root.toAbsolutePath().toString());

		createDirectory(p.root);
		final FileStore store = Files.getFileStore(root.toPath());
		final String free =
				(store.getUsableSpace()*100/store.getTotalSpace()) + "% of " +
				(store.getTotalSpace()/(1024*1024*1024)) + "GiB";

		assertEquals(p.root, rootExists.call());
		assertEquals(free, rootFree.call());
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + p.tempDir().toAbsolutePath());
		assertFails(
				tempStore::call,
				NoSuchFileException.class,
				p.tempDir().toAbsolutePath().toString());

		createDirectory(p.tempDir());

		assertEquals(p.root, rootExists.call());
		assertEquals(free, rootFree.call());
		assertEquals(p.tempDir(), tempExists.call());
		assertEquals(store, tempStore.call());
	}

	@Test void probeRootRegularFile() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		final Callable<?> rootExists = p.getProbes().stream().
				filter(c -> "root.Exists".equals(c.toString())).
				findFirst().
				get();

		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"does not exist: " + p.root.toAbsolutePath());

		Files.createFile(p.root);
		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"is not a directory: " + p.root);
	}

	@Test void probeRootNotWritable() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		final Callable<?> rootExists = p.getProbes().stream().
				filter(c -> "root.Exists".equals(c.toString())).
				findFirst().
				get();

		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"does not exist: " + p.root.toAbsolutePath());

		assumeSupportsReadOnlyDirectories(p.root);
		createDirectory(p.root, asFileAttribute(EnumSet.of(OWNER_READ)));
		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"is not writable: " + p.root);
	}

	@Test void probeTempRegularFile() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		final Callable<?> tempExists = p.getProbes().stream().
				filter(c -> "temp.Exists".equals(c.toString())).
				findFirst().
				get();

		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + p.tempDir().toAbsolutePath());

		createDirectory(p.root);
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + p.tempDir().toAbsolutePath());

		Files.createFile(p.tempDir());
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"is not a directory: " + p.tempDir().toAbsolutePath());
	}

	@Test void probeTempNotWritable() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		final Callable<?> tempExists = p.getProbes().stream().
				filter(c -> "temp.Exists".equals(c.toString())).
				findFirst().
				get();

		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + p.tempDir().toAbsolutePath());

		createDirectory(p.root);
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + p.tempDir().toAbsolutePath());

		assumeSupportsReadOnlyDirectories(p.root);
		createDirectory(p.tempDir(), asFileAttribute(EnumSet.of(OWNER_READ)));
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"is not writable: " + p.tempDir().toAbsolutePath());
	}

	private static void assumeSupportsReadOnlyDirectories(final Path p)
	{
		assumeTrue(
				Files.getFileAttributeView(p, PosixFileAttributeView.class)!=null,
				"does not support read only directories");
	}

	@Test void probeDirectoryExistsDisabled()
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("directory", false)
				));

		final Props p = new Props(source);
		assertNull(p.directory);
		final Callable<?> dirs = p.getProbes().stream().
				filter(c -> "directory.Exists".equals(c.toString())).
				findFirst().
				get();

		assertFails(
				dirs::call,
				ProbeAbortedException.class,
				"directories disabled");
	}

	@Test void probeDirectoryExistsCreateAsNeeded()
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		assertEquals(true, p.directory.createAsNeeded);
		final Callable<?> dirs = p.getProbes().stream().
				filter(c -> "directory.Exists".equals(c.toString())).
				findFirst().
				get();

		assertFails(
				dirs::call,
				ProbeAbortedException.class,
				"directories created as needed");
	}

	@Test void probeDirectoryExistsOne() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("directory.length", 1),
						single("directory.createAsNeeded", false)
				));

		final Props p = new Props(source);
		assertEquals(1, p.directory.length);
		assertEquals(false, p.directory.createAsNeeded);
		final Callable<?> dirs = p.getProbes().stream().
				filter(c -> "directory.Exists".equals(c.toString())).
				findFirst().
				get();

		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "0");
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "0");

		createDirectory(root.toPath());
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "0");

		createDirectory(root.toPath().resolve("0"));
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "1");

		createDirectory(root.toPath().resolve("1"));
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "2");

		for(final String s : asList("2","3","4","5","6","7","8","9","a","b","c","d","e"))
			createDirectory(root.toPath().resolve(s));
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "f");

		createDirectory(root.toPath().resolve("f"));
		assertEquals(16, dirs.call());
	}

	@Test void probeDirectoryExistsTwo() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("directory.length", 2),
						single("directory.createAsNeeded", false)
				));

		final Props p = new Props(source);
		assertEquals(2, p.directory.length);
		assertEquals(false, p.directory.createAsNeeded);
		final Callable<?> dirs = p.getProbes().stream().
				filter(c -> "directory.Exists".equals(c.toString())).
				findFirst().
				get();

		createDirectory(root.toPath());
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "00");

		createDirectory(root.toPath().resolve("00"));
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "01");

		createDirectory(root.toPath().resolve("01"));
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "02");

		for(final String s : asList("2","3","4","5","6","7","8","9","a","b","c","d","e"))
			createDirectory(root.toPath().resolve("0"+s));
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "0f");

		createDirectory(root.toPath().resolve("0f"));
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "10");

		for(final String s1 : asList("1","2","3","4","5","6","7","8","9","a","b","c","d","e"))
			for(final String s2 : asList("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"))
				createDirectory(root.toPath().resolve(s1+s2));
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "f0");

		for(final String s : asList("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e"))
			createDirectory(root.toPath().resolve("f"+s));
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "ff");

		createDirectory(root.toPath().resolve("ff"));
		assertEquals(256, dirs.call());
	}

	@Test void probeDirectoryExistsThree() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("directory.createAsNeeded", false)
				));

		final Props p = new Props(source);
		assertEquals(3, p.directory.length);
		assertEquals(false, p.directory.createAsNeeded);
		final Callable<?> dirs = p.getProbes().stream().
				filter(c -> "directory.Exists".equals(c.toString())).
				findFirst().
				get();

		createDirectory(root.toPath());
		assertFails(dirs::call, IllegalStateException.class, root + File.separator + "000");

		final List<String> hexDigits = asList("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f");
		for(final String s1 : hexDigits)
			for(final String s2 : hexDigits)
				for(final String s3 : hexDigits)
					createDirectory(root.toPath().resolve(s1+s2+s3));
		assertEquals(4096, dirs.call());
	}
}
