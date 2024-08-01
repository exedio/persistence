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
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.vaulttest.VaultServiceTest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

@MainRule.Tag
public abstract class AbstractVaultFileServiceTest extends VaultServiceTest
{
	@Override
	protected final Class<? extends VaultService> getServiceClass()
	{
		return VaultFileService.class;
	}

	private File root;
	private boolean posixAvailable;

	protected final TemporaryFolder files = new TemporaryFolder();
	private final LogRule log = new LogRule(VaultFileService.class);

	@Override
	protected Properties getServiceProperties() throws IOException
	{
		root = files.newFolder();
		posixAvailable = Files.getFileAttributeView(root.toPath(), PosixFileAttributeView.class)!=null;
		final Properties result = new Properties();
		result.setProperty("root", root.getAbsolutePath());
		return result;
	}

	final File getRoot()
	{
		return root;
	}

	@BeforeEach final void setUpAbstractVaultFileServiceTest() throws IOException
	{
		final Path tempDir = ((VaultFileService)getService()).tempDir;
		if(tempDir!=null)
			Files.createDirectory(tempDir);
	}

	@AfterEach final void assertEmptyLog()
	{
		log.assertEmpty();
	}

	@Test final void testToString()
	{
		assertEquals("VaultFileService:" + root.getAbsolutePath(), getService().toString());
	}

	protected static final void assertContains(final File directory, final File... content)
	{
		final File[] actual = directory.listFiles();
		assertNotNull(actual);
		assertEquals(
				new TreeSet<>(asList(content)),
				new TreeSet<>(asList(actual)));
	}

	protected final void assumePosix()
	{
		assumeTrue(posixAvailable);
	}

	protected final void assertPosix(
			final EnumSet<PosixFilePermission> expectedPermissions,
			final String expectedGroup,
			final File actual)
			throws IOException
	{
		if(posixAvailable)
		{
			assertEquals(
					expectedPermissions,
					EnumSet.copyOf( // normalizes order of set, makes failure message much more readable
							Files.getFileAttributeView(actual.toPath(), PosixFileAttributeView.class).
									readAttributes().permissions()));
			assertEquals(
					expectedGroup,
					Files.getFileAttributeView(actual.toPath(), PosixFileAttributeView.class).
							readAttributes().group().getName());
		}
	}

	protected final String rootGroup() throws IOException
	{
		if(!posixAvailable)
			return null;

		return
				Files.getFileAttributeView(root.toPath(), PosixFileAttributeView.class).
						readAttributes().
						group().
						getName();
	}

	protected final void assertEquaFA(
			final String expected,
			final Stream<FileAttribute<?>> actual)
	{
		assertEquals(
				(!posixAvailable&&expected!=null) ? "" : expected,
				actual!=null
				? actual.
						map(fa -> fa.name() + "->" + normalize(fa.value())).
						collect(Collectors.joining(","))
				: null
		);
	}

	private static Object normalize(final Object o)
	{
		if(o instanceof Set) // implementation detail of PosixFilePermissions#asFileAttribute
		{
			//noinspection OverlyStrongTypeCast
			return new TreeSet<>((Set<?>)o);
		}
		else
			return o;
	}

	@Test final void directoryTraversalGetBytes()
	{
		assertDirectoryTraversal(
				"012.456789abcdef0", '.', "012.456789abcdefxx17", h -> getService().get(h));
		assertDirectoryTraversal(
				"012/456789abcdef0", '/', "012/456789abcdefxx17", h -> getService().get(h));
		assertDirectoryTraversal(
				"012\\456789abcdef0", '\\', "012\\456789abcdefxx17", h -> getService().get(h));
	}
	@Test final void directoryTraversalGetStream()
	{
		final OutputStream sink = new AssertionErrorOutputStream();
		assertDirectoryTraversal(
				"012.456789abcdef0", '.', "012.456789abcdefxx17", h -> getService().get(h, sink));
		assertDirectoryTraversal(
				"012/456789abcdef0", '/', "012/456789abcdefxx17", h -> getService().get(h, sink));
		assertDirectoryTraversal(
				"012\\456789abcdef0", '\\', "012\\456789abcdefxx17", h -> getService().get(h, sink));
	}
	@Test final void directoryTraversalPutBytes()
	{
		final byte[] value = {};
		assertDirectoryTraversal(
				"012.456789abcdef0", '.', "012.456789abcdefxx17", h -> getService().put(h, value));
		assertDirectoryTraversal(
				"012/456789abcdef0", '/', "012/456789abcdefxx17", h -> getService().put(h, value));
		assertDirectoryTraversal(
				"012\\456789abcdef0", '\\', "012\\456789abcdefxx17", h -> getService().put(h, value));
	}
	@Test final void directoryTraversalPutStream()
	{
		final InputStream value = new ByteArrayInputStream(new byte[]{});
		assertDirectoryTraversal(
				"012.456789abcdef0", '.', "012.456789abcdefxx17", h -> getService().put(h, value));
		assertDirectoryTraversal(
				"012/456789abcdef0", '/', "012/456789abcdefxx17", h -> getService().put(h, value));
		assertDirectoryTraversal(
				"012\\456789abcdef0", '\\', "012\\456789abcdefxx17", h -> getService().put(h, value));
	}
	@Test final void directoryTraversalPutPath()
	{
		final Path value = Paths.get("AbstractVaultFileServiceTest");
		assertDirectoryTraversal(
				"012.456789abcdef0", '.', "012.456789abcdefxx17", h -> getService().put(h, value));
		assertDirectoryTraversal(
				"012/456789abcdef0", '/', "012/456789abcdefxx17", h -> getService().put(h, value));
		assertDirectoryTraversal(
				"012\\456789abcdef0", '\\', "012\\456789abcdefxx17", h -> getService().put(h, value));
	}
	private static void assertDirectoryTraversal(
			final String hash,
			final char ch,
			final String hashInMessage,
			final ExecutableStringFunction executable)
	{
		assertFails(
				() -> executable.execute(hash),
				IllegalArgumentException.class,
				"illegal character >" + ch + "< at position 3 " +
				"is likely a directory traversal attack in >" + hashInMessage + "<");
	}
	@FunctionalInterface
	interface ExecutableStringFunction
	{
		void execute(String parameter) throws Throwable;
	}


	@Override
	@Test protected final void probeBucketTag() throws Exception
	{
		final Path keyDir = getBucketTagRoot().toPath().resolve("VaultGenuineServiceKey");
		final Path keyPath = keyDir.resolve("my-Bucket");
		assertProbeBucketTagFailsNew();

		Files.createDirectory(keyDir);
		assertProbeBucketTagFails();

		Files.write(keyPath, new byte[]{});
		assertEquals("deprecated: " + keyPath, getService().probeGenuineServiceKey("my-Bucket"));
	}
	@Test protected final void probeBucketTagNew() throws Exception
	{
		final Path keyDir = getBucketTagRoot().toPath().resolve("VaultBucketTag");
		final Path keyPath = keyDir.resolve("my-Bucket");
		assertProbeBucketTagFailsNew();

		Files.createDirectory(keyDir);
		assertProbeBucketTagFailsNew();

		Files.write(keyPath, new byte[]{});
		assertEquals(keyPath, getService().probeGenuineServiceKey("my-Bucket"));
	}
	@Test protected final void probeBucketTagNonEmpty() throws Exception
	{
		final Path keyDir = getBucketTagRoot().toPath().resolve("VaultGenuineServiceKey");
		final Path keyPath = keyDir.resolve("my-Bucket");
		assertProbeBucketTagFailsNew();

		Files.createDirectory(keyDir);
		assertProbeBucketTagFails();

		Files.write(keyPath, new byte[]{1});
		assertProbeBucketTagFails("is not empty, but has size 1");

		Files.write(keyPath, new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13});
		assertProbeBucketTagFails("is not empty, but has size 13");
	}
	@Test protected final void probeBucketTagNonEmptyNew() throws Exception
	{
		final Path keyDir = getBucketTagRoot().toPath().resolve("VaultBucketTag");
		final Path keyPath = keyDir.resolve("my-Bucket");
		assertProbeBucketTagFailsNew();

		Files.createDirectory(keyDir);
		assertProbeBucketTagFailsNew();

		Files.write(keyPath, new byte[]{1});
		assertProbeBucketTagFailsNew("is not empty, but has size 1");

		Files.write(keyPath, new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13});
		assertProbeBucketTagFailsNew("is not empty, but has size 13");
	}
	@Test final void probeBucketTagDirectory() throws Exception
	{
		final Path keyDir = getBucketTagRoot().toPath().resolve("VaultGenuineServiceKey");
		final Path keyPath = keyDir.resolve("my-Bucket");
		assertProbeBucketTagFailsNew();

		Files.createDirectory(keyDir);
		assertProbeBucketTagFails();

		Files.createDirectory(keyPath);
		assertProbeBucketTagFails("is not a regular file");
	}
	@Test final void probeBucketTagDirectoryNew() throws Exception
	{
		final Path keyDir = getBucketTagRoot().toPath().resolve("VaultBucketTag");
		final Path keyPath = keyDir.resolve("my-Bucket");
		assertProbeBucketTagFailsNew();

		Files.createDirectory(keyDir);
		assertProbeBucketTagFailsNew();

		Files.createDirectory(keyPath);
		assertProbeBucketTagFailsNew("is not a regular file");
	}
	protected File getBucketTagRoot()
	{
		return getRoot();
	}
	private void assertProbeBucketTagFails(final String reason)
	{
		assertFails(
				() -> getService().probeGenuineServiceKey("my-Bucket"),
				IllegalStateException.class,
				reason + ": " + getBucketTagRoot() + File.separator + "VaultGenuineServiceKey" + File.separator + "my-Bucket");
	}
	private void assertProbeBucketTagFailsNew(final String reason)
	{
		assertFails(
				() -> getService().probeGenuineServiceKey("my-Bucket"),
				IllegalStateException.class,
				reason + ": " + getBucketTagRoot() + File.separator + "VaultBucketTag" + File.separator + "my-Bucket");
	}
	private void assertProbeBucketTagFails()
	{
		assertFails(
				() -> getService().probeGenuineServiceKey("my-Bucket"),
				NoSuchFileException.class,
				getBucketTagRoot() + File.separator + "VaultGenuineServiceKey" + File.separator + "my-Bucket");
	}
	private void assertProbeBucketTagFailsNew()
	{
		assertFails(
				() -> getService().probeGenuineServiceKey("my-Bucket"),
				NoSuchFileException.class,
				getBucketTagRoot() + File.separator + "VaultBucketTag" + File.separator + "my-Bucket");
	}
}
