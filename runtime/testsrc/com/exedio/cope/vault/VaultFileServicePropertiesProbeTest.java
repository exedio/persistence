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
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Properties.Field;
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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
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
		assertEquals(asList(
				"root",
				"writable",
				"posixPermissions",
				"directory",
				"directory.length",
				"directory.premised",
				"directory.posixPermissions",
				"temp"),
				p.getFields().stream().map(Field::getKey).collect(toList()));

		final Iterator<? extends Callable<?>> probes = p.getProbes().iterator();
		assertEquals("directory.Premised", probes.next().toString());
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

	@Test void probeNotWritable() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("writable", false)
				));

		final Props p = new Props(source);
		assertEquals(
				asList("root", "writable", "directory", "directory.length"),
				p.getFields().stream().map(Field::getKey).collect(toList()));

		final Iterator<? extends Callable<?>> probes = p.getProbes().iterator();
		assertEquals("directory.Premised", probes.next().toString());
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
				ProbeAbortedException.class,
				"not writable");
		assertFails(
				tempStore::call,
				ProbeAbortedException.class,
				"not writable");

		createDirectory(p.root);
		assertEquals(p.root, rootExists.call());
		assertFails(
				tempExists::call,
				ProbeAbortedException.class,
				"not writable");
		assertFails(
				tempStore::call,
				ProbeAbortedException.class,
				"not writable");
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

	@Test void probePremisedDisabled()
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
				filter(c -> "directory.Premised".equals(c.toString())).
				findFirst().
				get();

		assertFails(
				dirs::call,
				ProbeAbortedException.class,
				"directories disabled");
	}

	@Test void probePremisedNotPremised() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		assertEquals(false, p.directory.premised);
		final Callable<?> dirs = p.getProbes().stream().
				filter(c -> "directory.Premised".equals(c.toString())).
				findFirst().
				get();

		assertEquals(
				"missing 000,001,002,003,004,005,006,007,008,009,00a,00b,00c,00d,00e... (total 4096)",
				dirs.call());
	}

	@Test void probePremisedOne() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("directory.length", 1),
						single("directory.premised", true)
				));

		final Props p = new Props(source);
		assertEquals(1, p.directory.length);
		assertEquals(true, p.directory.premised);
		final Callable<?> dirs = p.getProbes().stream().
				filter(c -> "directory.Premised".equals(c.toString())).
				findFirst().
				get();

		assertFails(dirs::call, IllegalStateException.class, "missing 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e... (total 16)");
		assertFails(dirs::call, IllegalStateException.class, "missing 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e... (total 16)");

		createDirectory(root.toPath());
		assertFails(dirs::call, IllegalStateException.class, "missing 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e... (total 16)");

		createDirectory(root.toPath().resolve("0"));
		assertFails(dirs::call, IllegalStateException.class, "missing 1,2,3,4,5,6,7,8,9,a,b,c,d,e,f");

		createDirectory(root.toPath().resolve("1"));
		assertFails(dirs::call, IllegalStateException.class, "missing 2,3,4,5,6,7,8,9,a,b,c,d,e,f");

		for(final String s : asList("2","3","4","5","6","7","8","9","a","b","c","d","e"))
			createDirectory(root.toPath().resolve(s));
		assertFails(dirs::call, IllegalStateException.class, "missing f");

		createDirectory(root.toPath().resolve("f"));
		assertEquals(16, dirs.call());
	}

	@Test void probePremisedTwo() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("directory.length", 2),
						single("directory.premised", true)
				));

		final Props p = new Props(source);
		assertEquals(2, p.directory.length);
		assertEquals(true, p.directory.premised);
		final Callable<?> dirs = p.getProbes().stream().
				filter(c -> "directory.Premised".equals(c.toString())).
				findFirst().
				get();

		createDirectory(root.toPath());
		assertFails(dirs::call, IllegalStateException.class, "missing 00,01,02,03,04,05,06,07,08,09,0a,0b,0c,0d,0e... (total 256)");

		createDirectory(root.toPath().resolve("00"));
		assertFails(dirs::call, IllegalStateException.class, "missing 01,02,03,04,05,06,07,08,09,0a,0b,0c,0d,0e,0f... (total 255)");

		createDirectory(root.toPath().resolve("01"));
		assertFails(dirs::call, IllegalStateException.class, "missing 02,03,04,05,06,07,08,09,0a,0b,0c,0d,0e,0f,10... (total 254)");

		for(final String s : asList("2","3","4","5","6","7","8","9","a","b","c","d","e"))
			createDirectory(root.toPath().resolve("0"+s));
		assertFails(dirs::call, IllegalStateException.class, "missing 0f,10,11,12,13,14,15,16,17,18,19,1a,1b,1c,1d... (total 241)");

		createDirectory(root.toPath().resolve("0f"));
		assertFails(dirs::call, IllegalStateException.class, "missing 10,11,12,13,14,15,16,17,18,19,1a,1b,1c,1d,1e... (total 240)");

		for(final String s1 : asList("1","2","3","4","5","6","7","8","9","a","b","c","d","e"))
			for(final String s2 : HEX_DIGITS)
				createDirectory(root.toPath().resolve(s1+s2));
		assertFails(dirs::call, IllegalStateException.class, "missing f0,f1,f2,f3,f4,f5,f6,f7,f8,f9,fa,fb,fc,fd,fe... (total 16)");

		for(final String s : asList("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e"))
			createDirectory(root.toPath().resolve("f"+s));
		assertFails(dirs::call, IllegalStateException.class, "missing ff");

		createDirectory(root.toPath().resolve("ff"));
		assertEquals(256, dirs.call());
	}

	@Test void probePremisedThree() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("directory.premised", true)
				));

		final Props p = new Props(source);
		assertEquals(3, p.directory.length);
		assertEquals(true, p.directory.premised);
		final Callable<?> dirs = p.getProbes().stream().
				filter(c -> "directory.Premised".equals(c.toString())).
				findFirst().
				get();

		createDirectory(root.toPath());
		assertFails(dirs::call, IllegalStateException.class, "missing 000,001,002,003,004,005,006,007,008,009,00a,00b,00c,00d,00e... (total 4096)");

		for(final String s1 : HEX_DIGITS)
			for(final String s2 : HEX_DIGITS)
				for(final String s3 : HEX_DIGITS)
					createDirectory(root.toPath().resolve(s1+s2+s3));
		assertEquals(4096, dirs.call());
	}

	@Test void iterateOne()
	{
		iterate(
				cascade(single("length", 1)),
				HEX_DIGITS);
	}

	@Test void iterateTwo()
	{
		final ArrayList<String> expected = new ArrayList<>();
		for(final String s1 : HEX_DIGITS)
			for(final String s2 : HEX_DIGITS)
				expected.add(s1+s2);
		iterate(
				cascade(single("length", 2)),
				expected);
	}

	@Test void iterateThree()
	{
		final ArrayList<String> expected = new ArrayList<>();
		for(final String s1 : HEX_DIGITS)
			for(final String s2 : HEX_DIGITS)
				for(final String s3 : HEX_DIGITS)
					expected.add(s1+s2+s3);
		iterate(
				cascade(single("length", 3)),
				expected);
	}

	private static void iterate(final Properties.Source source, final List<String> expected)
	{
		final Iterator<String> iterator = new VaultDirectory.Properties(source, true).iterator();
		final ArrayList<String> actual = new ArrayList<>();
		iterator.forEachRemaining(actual::add);
		assertEquals(expected, actual);
		assertEquals(false, iterator.hasNext());
		assertFails(
				iterator::next,
				NoSuchElementException.class,
				null);
	}

	private static final List<String> HEX_DIGITS = unmodifiableList(asList(
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"));
}
