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

import static com.exedio.cope.RuntimeAssert.assumeNotGithub;
import static com.exedio.cope.RuntimeAssert.probes;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static com.exedio.filevault.VaultFileServicePosixGroupTest.testGroupDirectory;
import static com.exedio.filevault.VaultFileServicePosixGroupTest.testGroupFile;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermissions.asFileAttribute;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.ProbeAbortedException;
import com.exedio.cope.util.Properties.Source;
import com.exedio.filevault.VaultFileService.Props;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
		final File temp = new File(root, ".tempVaultFileService");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		assertEquals(asList(
				"root",
				"content",
				"writable",
				"posixPermissions",
				"posixPermissionsAfterwards",
				"posixGroup",
				"directory",
				"directory.length",
				"directory.premised",
				"directory.posixPermissions",
				"directory.posixPermissionsAfterwards",
				"directory.posixGroup",
				"temp"),
				p.getFields().stream().map(Field::getKey).collect(toList()));
		assertEquals(root.toPath(), p.root);
		assertEquals(root.toPath(), p.content);
		assertEquals(temp.toPath(), p.tempDir());

		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
				"root.Exists",
				"root.Free",
				"content.Exists",
				"AtomicMove",
				"PosixGroup",
				"directory.Premised",
				"directory.AtomicMove",
				"directory.AtomicMoveEmpty",
				"directory.PosixGroup",
				"temp.Exists",
				"temp.Store"),
				new ArrayList<>(probes.keySet()));

		final Callable<?> groupd     = probes.get("directory.PosixGroup");
		final Callable<?> groupf     = probes.get("PosixGroup");
		final Callable<?> rootExists = probes.get("root.Exists");
		final Callable<?> rootFree   = probes.get("root.Free");
		final Callable<?> contExists = probes.get("content.Exists");
		final Callable<?> tempExists = probes.get("temp.Exists");
		final Callable<?> tempStore  = probes.get("temp.Store");
		final Callable<?> moveFile   = probes.get("AtomicMove");
		final Callable<?> moveDir    = probes.get("directory.AtomicMove");
		final Callable<?> moveDirE   = probes.get("directory.AtomicMoveEmpty");

		assertFails(
				groupd::call,
				ProbeAbortedException.class,
				"group disabled");
		assertFails(
				groupf::call,
				ProbeAbortedException.class,
				"group disabled");
		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"does not exist: " + root);
		assertFails(
				rootFree::call,
				NoSuchFileException.class,
				root.toString());
		assertFails(
				contExists::call,
				IllegalArgumentException.class,
				"does not exist: " + root);
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + temp);
		assertFails(
				tempStore::call,
				NoSuchFileException.class,
				root.toString());

		createDirectory(root.toPath());
		final FileStore store = Files.getFileStore(root.toPath());
		final String free =
				(store.getUsableSpace()*100/store.getTotalSpace()) + "% of " +
				(store.getTotalSpace()/(1024*1024*1024)) + "GiB";

		assertEquals(root.toPath(), rootExists.call());
		assertEquals(free, rootFree.call());
		assertEquals(root.toPath(), contExists.call());
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + temp);
		assertFails(
				tempStore::call,
				NoSuchFileException.class,
				temp.toString());

		createDirectory(temp.toPath());

		assertEquals(root.toPath(), rootExists.call());
		assertEquals(free, rootFree.call());
		assertEquals(root.toPath(), contExists.call());
		assertEquals(temp.toPath(), tempExists.call());
		assertEquals(store, tempStore.call());
		assertNull(moveFile.call());
		assertNull(moveDir .call());
		if (isWindows())
			assertFails(
					moveDirE::call,
					ProbeAbortedException.class,
					"Windows not supported for file vaults - don't use on production systems!"
			);
		else
			assertNull(moveDirE.call());
	}

	@Test void probeContent() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final File cont = new File(root, "mycontentdir");
		final File temp = new File(root, ".tempVaultFileService");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("content", "mycontentdir")
				));

		final Props p = new Props(source);
		assertEquals(asList(
						"root",
						"content",
						"writable",
						"posixPermissions",
						"posixPermissionsAfterwards",
						"posixGroup",
						"directory",
						"directory.length",
						"directory.premised",
						"directory.posixPermissions",
						"directory.posixPermissionsAfterwards",
						"directory.posixGroup",
						"temp"),
				p.getFields().stream().map(Field::getKey).collect(toList()));
		assertEquals(root.toPath(), p.root);
		assertEquals(cont.toPath(), p.content);
		assertEquals(temp.toPath(), p.tempDir());

		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
						"root.Exists",
						"root.Free",
						"content.Exists",
						"AtomicMove",
						"PosixGroup",
						"directory.Premised",
						"directory.AtomicMove",
						"directory.AtomicMoveEmpty",
						"directory.PosixGroup",
						"temp.Exists",
						"temp.Store"),
				new ArrayList<>(probes.keySet()));

		final Callable<?> rootExists = probes.get("root.Exists");
		final Callable<?> rootFree   = probes.get("root.Free");
		final Callable<?> contExists = probes.get("content.Exists");
		final Callable<?> tempExists = probes.get("temp.Exists");
		final Callable<?> tempStore  = probes.get("temp.Store");

		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"does not exist: " + root);
		assertFails(
				rootFree::call,
				NoSuchFileException.class,
				root.toString());
		assertFails(
				contExists::call,
				IllegalArgumentException.class,
				"does not exist: " + cont);
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + temp);
		assertFails(
				tempStore::call,
				NoSuchFileException.class,
				cont.toString());

		createDirectory(root.toPath());
		final FileStore store = Files.getFileStore(root.toPath());
		final String free =
				(store.getUsableSpace()*100/store.getTotalSpace()) + "% of " +
				(store.getTotalSpace()/(1024*1024*1024)) + "GiB";

		assertEquals(root.toPath(), rootExists.call());
		assertEquals(free, rootFree.call());
		assertFails(
				contExists::call,
				IllegalArgumentException.class,
				"does not exist: " + cont);
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + temp);
		assertFails(
				tempStore::call,
				NoSuchFileException.class,
				cont.toString());

		createDirectory(cont.toPath());

		assertEquals(root.toPath(), rootExists.call());
		assertEquals(free, rootFree.call());
		assertEquals(cont.toPath(), contExists.call());
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + temp);
		assertFails(
				tempStore::call,
				NoSuchFileException.class,
				temp.toString());

		createDirectory(temp.toPath());

		assertEquals(root.toPath(), rootExists.call());
		assertEquals(free, rootFree.call());
		assertEquals(cont.toPath(), contExists.call());
		assertEquals(temp.toPath(), tempExists.call());
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
				asList("root", "content", "writable", "directory", "directory.length"),
				p.getFields().stream().map(Field::getKey).collect(toList()));
		assertEquals(root.toPath(), p.root);
		assertFails(
				p::tempDir,
				IllegalArgumentException.class,
				"non-writable properties cannot be used in writable service");

		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
				"root.Exists",
				"root.Free",
				"content.Exists",
				"AtomicMove",
				"PosixGroup",
				"directory.Premised",
				"directory.AtomicMove",
				"directory.AtomicMoveEmpty",
				"directory.PosixGroup",
				"temp.Exists",
				"temp.Store"),
				new ArrayList<>(probes.keySet()));
		final Callable<?> groupd     = probes.get("directory.PosixGroup");
		final Callable<?> groupf     = probes.get("PosixGroup");
		final Callable<?> rootExists = probes.get("root.Exists");
		final Callable<?> rootFree   = probes.get("root.Free");
		final Callable<?> contExists = probes.get("content.Exists");
		final Callable<?> tempExists = probes.get("temp.Exists");
		final Callable<?> tempStore  = probes.get("temp.Store");
		final Callable<?> moveFile   = probes.get("AtomicMove");
		final Callable<?> moveDir    = probes.get("directory.AtomicMove");
		final Callable<?> moveDirE   = probes.get("directory.AtomicMoveEmpty");

		assertFails(
				groupd::call,
				ProbeAbortedException.class,
				"not writable");
		assertFails(
				groupf::call,
				ProbeAbortedException.class,
				"not writable");
		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"does not exist: " + root);
		assertFails(
				rootFree::call,
				NoSuchFileException.class,
				root.toString());
		assertFails(
				contExists::call,
				IllegalArgumentException.class,
				"does not exist: " + root);
		assertFails(
				tempExists::call,
				ProbeAbortedException.class,
				"not writable");
		assertFails(
				tempStore::call,
				ProbeAbortedException.class,
				"not writable");
		assertFails(
				moveFile::call,
				ProbeAbortedException.class,
				"not writable");
		assertFails(
				moveDir::call,
				ProbeAbortedException.class,
				"not writable");
		assertFails(
				moveDirE::call,
				ProbeAbortedException.class,
				"not writable");

		createDirectory(root.toPath());
		assertEquals(root.toPath(), rootExists.call());
		assertEquals(root.toPath(), contExists.call());
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
		final Callable<?> rootExists = requireNonNull(probes(p).get("root.Exists"));
		final Callable<?> contExists = requireNonNull(probes(p).get("content.Exists"));
		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"does not exist: " + root);
		assertFails(
				contExists::call,
				IllegalArgumentException.class,
				"does not exist: " + root);

		Files.createFile(root.toPath());
		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"is not a directory: " + root);
		assertFails(
				contExists::call,
				IllegalArgumentException.class,
				"is not a directory: " + root);
	}

	@Test void probeRootNotWritable() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		final Callable<?> rootExists = requireNonNull(probes(p).get("root.Exists"));
		final Callable<?> contExists = requireNonNull(probes(p).get("content.Exists"));
		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"does not exist: " + root);
		assertFails(
				contExists::call,
				IllegalArgumentException.class,
				"does not exist: " + root);

		assumeSupportsReadOnlyDirectories(root);
		createDirectory(root.toPath(), asFileAttribute(EnumSet.of(OWNER_READ)));
		assertFails(
				rootExists::call,
				IllegalArgumentException.class,
				"is not writable: " + root);
		assertFails(
				contExists::call,
				IllegalArgumentException.class,
				"is not writable: " + root);
	}

	@Test void probeTempRegularFile() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final File temp = new File(root, ".tempVaultFileService");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		final Callable<?> tempExists = requireNonNull(probes(p).get("temp.Exists"));
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + temp);

		createDirectory(root.toPath());
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + temp);

		Files.createFile(temp.toPath());
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"is not a directory: " + temp);
	}

	@Test void probeTempNotWritable() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final File temp = new File(root, ".tempVaultFileService");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		final Callable<?> tempExists = requireNonNull(probes(p).get("temp.Exists"));
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + temp);

		createDirectory(root.toPath());
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"does not exist: " + temp);

		assumeSupportsReadOnlyDirectories(root);
		createDirectory(temp.toPath(), asFileAttribute(EnumSet.of(OWNER_READ)));
		assertFails(
				tempExists::call,
				IllegalArgumentException.class,
				"is not writable: " + temp);
	}

	private static void assumeSupportsReadOnlyDirectories(final File p)
	{
		assumeTrue(
				Files.getFileAttributeView(p.toPath(), PosixFileAttributeView.class)!=null,
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
		final Callable<?> dirs = requireNonNull(probes(p).get("directory.Premised"));
		assertFails(
				dirs::call,
				ProbeAbortedException.class,
				"directories disabled");
	}

	@Test void probePremisedNotPremised()
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root)
				));

		final Props p = new Props(source);
		assertEquals(false, p.directory.premised);
		final Callable<?> dirs = requireNonNull(probes(p).get("directory.Premised"));
		assertFails(
				dirs::call,
				ProbeAbortedException.class,
				"premised disabled, yet to be created: 000,001,002,003,004,005,006,007,008,009,00a,00b,00c,00d,00e... (total 4096)");
	}

	@Test void probePremisedNotPremisedOne() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("directory.length", 1)
				));

		final Props p = new Props(source);
		assertEquals(false, p.directory.premised);
		final Callable<?> dirs = requireNonNull(probes(p).get("directory.Premised"));
		assertFails(
				dirs::call,
				ProbeAbortedException.class,
				"premised disabled, yet to be created: 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e... (total 16)");

		createDirectory(root.toPath());
		for(final String s : asList("0", "1", "2","3","4","5","6","7","8","9","a","b","c","d","e"))
			createDirectory(root.toPath().resolve(s));
		assertFails(
				dirs::call,
				ProbeAbortedException.class,
				"premised disabled, yet to be created: f");

		createDirectory(root.toPath().resolve("f"));
		assertEquals(16, dirs.call());
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
		final Callable<?> dirs = requireNonNull(probes(p).get("directory.Premised"));
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

	@Test void probePremisedOneContent() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final File cont = new File(root, "mycontentdir");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("content", "mycontentdir"),
						single("directory.length", 1),
						single("directory.premised", true)
				));

		final Props p = new Props(source);
		assertEquals(1, p.directory.length);
		assertEquals(true, p.directory.premised);
		final Callable<?> dirs = requireNonNull(probes(p).get("directory.Premised"));
		assertFails(dirs::call, IllegalStateException.class, "missing 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e... (total 16)");
		assertFails(dirs::call, IllegalStateException.class, "missing 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e... (total 16)");

		createDirectory(root.toPath());
		assertFails(dirs::call, IllegalStateException.class, "missing 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e... (total 16)");

		createDirectory(cont.toPath());
		assertFails(dirs::call, IllegalStateException.class, "missing 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e... (total 16)");

		createDirectory(cont.toPath().resolve("0"));
		assertFails(dirs::call, IllegalStateException.class, "missing 1,2,3,4,5,6,7,8,9,a,b,c,d,e,f");

		createDirectory(cont.toPath().resolve("1"));
		assertFails(dirs::call, IllegalStateException.class, "missing 2,3,4,5,6,7,8,9,a,b,c,d,e,f");

		for(final String s : asList("2","3","4","5","6","7","8","9","a","b","c","d","e"))
			createDirectory(cont.toPath().resolve(s));
		assertFails(dirs::call, IllegalStateException.class, "missing f");

		createDirectory(cont.toPath().resolve("f"));
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
		final Callable<?> dirs = requireNonNull(probes(p).get("directory.Premised"));

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
		final Callable<?> dirs = requireNonNull(probes(p).get("directory.Premised"));

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

	private static final List<String> HEX_DIGITS = List.of(
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f");


	@Test void probePremisedAtomic() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		final Source source =
				describe("DESC", cascade(
						single("root", root),
						single("directory.premised", true)
				));

		final Props p = new Props(source);
		assertEquals(true, p.directory.premised);
		final Map<String,Callable<?>> probes = probes(p);
		final Callable<?> moveFile   = probes.get("AtomicMove");
		final Callable<?> moveDir    = probes.get("directory.AtomicMove");
		final Callable<?> moveDirE   = probes.get("directory.AtomicMoveEmpty");

		assertFails(
				moveDir::call,
				ProbeAbortedException.class,
				"directories are premised");
		assertFails(
				moveDirE::call,
				ProbeAbortedException.class,
				"directories are premised");

		createDirectory(root.toPath());
		createDirectory(p.tempDir());
		assertNull(moveFile.call());
	}

	@Test void probeGroups() throws Exception
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		assumePosix(root);

		final Source source =
				describe("DESC", cascade(
						single("directory.posixGroup", testGroupDirectory),
						single("posixGroup",           testGroupFile),
						single("root", root)
				));

		final Props p = new Props(source);
		final Map<String,Callable<?>> probes = probes(p);
		final Callable<?> groupd = probes.get("directory.PosixGroup");
		final Callable<?> groupf = probes.get("PosixGroup");

		assumeNotGithub();
		assertEquals(testGroupDirectory, groupd.call().toString());
		assertEquals(testGroupFile,      groupf.call().toString());
	}

	@Test void probeGroupsDoNotExist()
	{
		final File root = new File(sandbox, "VaultFileServicePropertiesProbeTest");
		assumePosix(root);

		final Source source =
				describe("DESC", cascade(
						single("directory.posixGroup", "groupnotexistsdir"),
						single("posixGroup",           "groupnotexistsfile"),
						single("root", root)
				));

		final Props p = new Props(source);
		final Map<String,Callable<?>> probes = probes(p);
		final Callable<?> groupd = probes.get("directory.PosixGroup");
		final Callable<?> groupf = probes.get("PosixGroup");

		final RuntimeException ed = assertFails(
				groupd::call,
				RuntimeException.class,
				"groupnotexistsdir");
		final RuntimeException ef = assertFails(
				groupf::call,
				RuntimeException.class,
				"groupnotexistsfile");
		assertTrue(ed.getCause() instanceof UserPrincipalNotFoundException, ed.getCause().getClass().getName());
		assertTrue(ef.getCause() instanceof UserPrincipalNotFoundException, ef.getCause().getClass().getName());
	}

	private static void assumePosix(final File file)
	{
		assumeTrue(Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class)!=null);
	}

	private static boolean isWindows()
	{
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
}
