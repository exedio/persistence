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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.vault.VaultTester.serviceParameters;
import static java.lang.System.getProperty;
import static java.nio.file.Files.createDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vault.VaultService;
import com.exedio.cope.vaulttest.VaultServiceTest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class VaultHttpServiceTest extends VaultServiceTest
{
	@Override
	protected final Class<? extends VaultService> getServiceClass()
	{
		return VaultHttpService.class;
	}

	@Override
	protected boolean isServiceWritable()
	{
		return false;
	}

	private static final String CLASS_NAME = VaultHttpServiceTest.class.getName();
	private static final String CONTENT_DIR = "myContent";
	/**
	 * Configuration of a local apache for running these tests:
	 * <pre>
	 * Alias "/VaultHttpServiceDocumentRoot" "PROJECT_DIR/VaultHttpServiceDocumentRoot/myContent"
	 *
	 * &lt;Location /VaultHttpServiceDocumentRoot>
	 *    Require all granted
	 * &lt;/Location>
	 * </pre>
	 */
	private static final String URL =           getProperty(CLASS_NAME + ".url", "http://localhost/VaultHttpServiceDocumentRoot");
	private static final Path   DIR = Paths.get(getProperty(CLASS_NAME + ".dir", "VaultHttpServiceDocumentRoot"));
	static
	{
		System.out.println(CLASS_NAME + ' ' + URL + ' ' + DIR);
	}

	@Override
	protected Properties getServiceProperties()
	{
		final Properties result = new Properties();
		result.setProperty("root", URL);
		return result;
	}

	protected boolean directoryEnabled()
	{
		return true;
	}

	@Override
	protected final VaultService maskServicePut(final VaultService service)
	{
		final VaultFileService.Props props = new VaultFileService.Props(Sources.cascade(
				single("root", DIR),
				single("content", CONTENT_DIR),
				sourcesPut(),
				directoryEnabled() ? single("directory.posixPermissions", "rwxrwxrwx") : Sources.EMPTY,
				single("posixPermissions", "rw-rw-rw-")));
		assertEquals(true, props.writable);
		assertEquals(Set.of(), props.getOrphanedKeys(), "orphanedKeys");
		return new VaultFileService(
				serviceParameters(VaultProperties.factory().create(Sources.cascade(
						single("algorithm", ALGORITHM),
						single("default.service", VaultFileService.class),
						single("default.service.root", DIR))),
						"testBucket",
						true, // writable
						() -> false), // markPut
				props);
	}

	abstract Source sourcesPut();

	@BeforeEach final void setUp() throws IOException
	{
		assumeNotGithub();
		flushDir();
		createDirectoryIfNotExists(DIR); // may exist already, because flushDir does not delete it
		createDirectoryIfNotExists(DIR.resolve(CONTENT_DIR)); // may exist already, because flushDir does not delete it
		createDirectory(DIR.resolve(".tempVaultFileService"));
	}

	private static void createDirectoryIfNotExists(final Path dir) throws IOException
	{
		try
		{
			createDirectory(dir);
		}
		catch(final FileAlreadyExistsException ignored)
		{
			// ok
		}
	}

	@AfterEach
	final void flushDir() throws IOException
	{
		if(!Files.exists(DIR))
			return;

		final Path contentDir = DIR.resolve(CONTENT_DIR);
		Files.walkFileTree(DIR, new SimpleFileVisitor<>()
		{
			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
			{
				Files.delete(file);
				return super.visitFile(file, attrs);
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException
			{
				if(!DIR.equals(dir) && !contentDir.equals(dir)) // must not delete DIR or CONTENT_DIR, because then the docker --mount in Jenkinsfile is lost
					Files.delete(dir);
				return super.postVisitDirectory(dir, exc);
			}
		});
	}

	@Test final void testToString()
	{
		assertEquals("VaultHttpService:" + URL, getService().toString());
	}

	@Test final void testProbeRootExists() throws Exception
	{
		@SuppressWarnings("OptionalGetWithoutIsPresent") // OK: will fail if not present
		final Callable<?> probe = getProperties().getProbes().stream().
				filter(s -> "service.root.Exists".equals(s.toString())).
				findFirst().
				get();
		assertEquals(new java.net.URI(URL+'/'), probe.call());
	}

	@Override
	@Test protected final void probeBucketTag() throws Exception
	{
		final Path keyDir = DIR.resolve(CONTENT_DIR).resolve("VaultBucketTag");
		final Path keyPath = keyDir.resolve("my-Bucket");
		assertProbeBucketTagFails("response code 404");
		assertThrows(NoSuchFileException.class, () -> getServicePut().probeBucketTag("my-Bucket"));

		createDirectory(keyDir);
		assertProbeBucketTagFails("response code 404");
		assertThrows(NoSuchFileException.class, () -> getServicePut().probeBucketTag("my-Bucket"));

		Files.write(keyPath, new byte[]{});
		assertEquals(
				new java.net.URI(URL + "/VaultBucketTag/my-Bucket"),
				getService().probeBucketTag("my-Bucket"));
		assertEquals(
				keyPath.toAbsolutePath(),
				getServicePut().probeBucketTag("my-Bucket"));
		assertFails(
				() -> getService().probeBucketTag("My-Bucket"), // wrong case
				IllegalStateException.class,
				"response code 404:" + URL + "/VaultBucketTag/My-Bucket");
	}
	@Test protected final void probeBucketTagNonEmpty() throws Exception
	{
		final Path keyDir = DIR.resolve(CONTENT_DIR).resolve("VaultBucketTag");
		final Path keyPath = keyDir.resolve("my-Bucket");
		assertProbeBucketTagFails("response code 404");
		assertThrows(NoSuchFileException.class, () -> getServicePut().probeBucketTag("my-Bucket"));

		createDirectory(keyDir);
		assertProbeBucketTagFails("response code 404");
		assertThrows(NoSuchFileException.class, () -> getServicePut().probeBucketTag("my-Bucket"));

		Files.write(keyPath, new byte[]{1});
		assertProbeBucketTagFails("is not empty, but has size 1");
		assertFails(
				() -> getServicePut().probeBucketTag("my-Bucket"),
				IllegalStateException.class,
				"is not empty, but has size 1: " + keyPath.toAbsolutePath());
	}
	private void assertProbeBucketTagFails(final String reason)
	{
		assertFails(
				() -> getService().probeBucketTag("my-Bucket"),
				IllegalStateException.class,
				reason + ":" + URL + "/VaultBucketTag/my-Bucket");
	}

	@Test void notFoundAnonymousBytes()
	{
		final VaultHttpService service = (VaultHttpService)getService();
		final VaultNotFoundException notFound = assertFails(
				() -> service.get("abcdefghijklmnopq"),
				VaultNotFoundException.class,
				"hash not found in vault: abcdefghijklmnopxx17");
		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());
		assertNull(notFound.getCause());
	}
	@Test void notFoundAnonymousSink()
	{
		final VaultHttpService service = (VaultHttpService)getService();
		final ByteArrayOutputStream sink = new ByteArrayOutputStream();
		final VaultNotFoundException notFound = assertFails(
				() -> service.get("abcdefghijklmnopq", sink),
				VaultNotFoundException.class,
				"hash not found in vault: abcdefghijklmnopxx17");
		assertEquals("abcdefghijklmnopq", notFound.getHashComplete());
		assertEquals("abcdefghijklmnopxx17", notFound.getHashAnonymous());
		assertNull(notFound.getCause());
	}
}
