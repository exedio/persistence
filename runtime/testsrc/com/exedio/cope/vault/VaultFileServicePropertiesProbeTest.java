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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Properties.Source;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributeView;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

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

		final VaultFileService.Props p = new VaultFileService.Props(source);
		final Iterator<? extends Callable<?>> probes = p.getProbes().iterator();
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

		final VaultFileService.Props p = new VaultFileService.Props(source);
		final Iterator<? extends Callable<?>> probes = p.getProbes().iterator();
		final Callable<?> rootExists = probes.next();
		assertEquals("root.Free",   probes.next().toString());
		assertEquals("temp.Exists", probes.next().toString());
		assertEquals("temp.Store",  probes.next().toString());
		assertFalse(probes.hasNext());

		assertEquals("root.Exists", rootExists.toString());
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

		final VaultFileService.Props p = new VaultFileService.Props(source);
		final Iterator<? extends Callable<?>> probes = p.getProbes().iterator();
		final Callable<?> rootExists = probes.next();
		assertEquals("root.Free",   probes.next().toString());
		assertEquals("temp.Exists", probes.next().toString());
		assertEquals("temp.Store",  probes.next().toString());
		assertFalse(probes.hasNext());

		assertEquals("root.Exists", rootExists.toString());
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

		final VaultFileService.Props p = new VaultFileService.Props(source);
		final Iterator<? extends Callable<?>> probes = p.getProbes().iterator();
		assertEquals("root.Exists", probes.next().toString());
		assertEquals("root.Free",   probes.next().toString());
		final Callable<?> tempExists = probes.next();
		assertEquals("temp.Store",  probes.next().toString());
		assertFalse(probes.hasNext());

		assertEquals("temp.Exists", tempExists.toString());
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

		final VaultFileService.Props p = new VaultFileService.Props(source);
		final Iterator<? extends Callable<?>> probes = p.getProbes().iterator();
		assertEquals("root.Exists", probes.next().toString());
		assertEquals("root.Free",   probes.next().toString());
		final Callable<?> tempExists = probes.next();
		assertEquals("temp.Store",  probes.next().toString());
		assertFalse(probes.hasNext());

		assertEquals("temp.Exists", tempExists.toString());
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
}
