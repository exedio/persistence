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

package com.exedio.cope.vaulttest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.util.Hex;
import com.exedio.cope.util.JobContexts;
import com.exedio.cope.util.MessageDigestUtil;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.BucketProperties;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultService;
import com.exedio.cope.vault.VaultServiceUnsupportedOperationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * This test is to be applied to all implementations of {@link VaultService}
 * for verifying general contracts of the interface.
 */
public abstract class VaultServiceTest
{
	protected static final String ALGORITHM = "SHA-512";

	private BucketProperties properties;
	private VaultService service;
	private VaultService servicePut;

	protected abstract Class<? extends VaultService> getServiceClass();

	/**
	 * Default implementation returns empty properties.
	 */
	@SuppressWarnings("RedundantThrows") // needed by copies of this file in other projects
	protected Properties getServiceProperties() throws Exception
	{
		return new Properties();
	}

	/**
	 * Default implementation returns true.
	 */
	protected boolean isServiceWritable()
	{
		return true;
	}

	/**
	 * Default implementation just returns parameter {@code service}.
	 */
	protected VaultService maskService(final VaultService service)
	{
		return service;
	}

	/**
	 * Default implementation just returns parameter {@code service}.
	 */
	protected VaultService maskServicePut(final VaultService service)
	{
		return service;
	}

	@BeforeEach final void setUpVaultServiceTest() throws Exception
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", ALGORITHM);
		source.setProperty("service", getServiceClass().getName());

		final Properties sp = getServiceProperties();
		for(final String key : sp.stringPropertyNames())
			source.setProperty("service." + key, sp.getProperty(key));

		properties = BucketProperties.create("myBucketKey", isServiceWritable(), Sources.view(source, "DESC"));
		assertEquals(Set.of(), properties.getOrphanedKeys(), "orphanedKeys");
		final VaultService service = properties.newServiceNonResilient(() -> markPut);
		this.service = maskService(service);
		this.servicePut = maskServicePut(service);
	}

	protected boolean markPut = false;

	@AfterEach final void tearDownVaultServiceTest()
	{
		final boolean putIsSeparate = service!=servicePut;
		if(service!=null)
		{
			service.close();
			service = null;
		}
		if(servicePut!=null && putIsSeparate)
			servicePut.close();
		servicePut = null;
	}

	protected final BucketProperties getProperties()
	{
		return properties;
	}

	protected final VaultService getService()
	{
		return service;
	}

	protected final VaultService getServicePut()
	{
		return servicePut;
	}


	@Test final void vaultPropertiesAlgorithm()
	{
		assertEquals(ALGORITHM, properties.getAlgorithm());
		assertEquals(hash("ab").length(), properties.getAlgorithmLength());
		assertEquals(hash(""), properties.getAlgorithmDigestForEmptyByteSequence());
	}

	@Test final void testPurgeSchema()
	{
		service.purgeSchema(JobContexts.EMPTY);
	}

	@Test final void notFoundContains() throws VaultServiceUnsupportedOperationException
	{
		final String hash = hash("ab");
		assertContains(false, hash);
	}

	@Test final void notFoundGetBytes()
	{
		final String hash = hash("ab");
		assertNotFound(() -> service.get(hash), hash);
	}

	@Test final void notFoundGetStream()
	{
		final String hash = hash("ab");
		final AssertionErrorOutputStream sink = new AssertionErrorOutputStream();
		assertNotFound(() -> service.get(hash, sink), hash);
	}

	@Test final void foundContains() throws VaultServiceUnsupportedOperationException
	{
		final String hash = putHash("abcdef01234567");
		assertContains(true, hash);
	}

	@Test final void foundGetBytes() throws VaultNotFoundException
	{
		final String hash = putHash("abcdef01234567");
		assertEquals("abcdef01234567", hex(service.get(hash)));
	}

	@Test final void foundGetStream() throws VaultNotFoundException, IOException
	{
		final String hash = putHash("abcdef01234567");
		final NonCloseableOrFlushableOutputStream sink = new NonCloseableOrFlushableOutputStream();
		service.get(hash, sink);
		assertEquals("abcdef01234567", hex(sink.toByteArray()));
	}

	@Test final void putBytes() throws VaultNotFoundException
	{
		final String hash = hash("abcdef01234567");
		assertTrue(servicePut.put(hash, unhex("abcdef01234567")));

		assertEquals("abcdef01234567", hex(service.get(hash)));
	}

	@Test final void putBytesMarked() throws VaultNotFoundException
	{
		markPut = true;
		final String hash = hash("abcdef01234567");
		assertTrue(servicePut.put(hash, unhex("abcdef01234567")));

		assertEquals("abcdef01234567", hex(service.get(hash)));

		assertFalse(servicePut.put(hash, unhex("abcdef01234567")));
	}

	@Test final void putStream() throws VaultNotFoundException, IOException
	{
		final String hash = hash("abcdef01234567");
		final ByteArrayInputStream value = new ByteArrayInputStream(unhex("abcdef01234567"));
		assertTrue(servicePut.put(hash, value));

		assertEquals("abcdef01234567", hex(service.get(hash)));
	}

	@Test final void putStreamMarked() throws VaultNotFoundException, IOException
	{
		markPut = true;
		final String hash = hash("abcdef01234567");
		final ByteArrayInputStream value = new ByteArrayInputStream(unhex("abcdef01234567"));
		assertTrue(servicePut.put(hash, value));

		assertEquals("abcdef01234567", hex(service.get(hash)));

		final ByteArrayInputStream value2 = new ByteArrayInputStream(unhex("abcdef01234567"));
		assertFalse(servicePut.put(hash, value2));
	}

	@Test final void putPath() throws VaultNotFoundException, IOException
	{
		final String hash = hash("abcdef01234567");
		final Path value = Files.createTempFile("VaultServiceTest", ".dat");
		try(OutputStream s = Files.newOutputStream(value))
		{
			s.write(unhex("abcdef01234567"));
		}
		assertTrue(servicePut.put(hash, value));

		assertEquals("abcdef01234567", hex(service.get(hash)));
	}

	@Test final void putPathMarked() throws VaultNotFoundException, IOException
	{
		markPut = true;
		final String hash = hash("abcdef01234567");
		final Path value = Files.createTempFile("VaultServiceTest", ".dat");
		try(OutputStream s = Files.newOutputStream(value))
		{
			s.write(unhex("abcdef01234567"));
		}
		assertTrue(servicePut.put(hash, value));

		assertEquals("abcdef01234567", hex(service.get(hash)));

		assertFalse(servicePut.put(hash, value));
	}

	@Test final void putMany() throws VaultNotFoundException
	{
		final String hash = hash("abcdef01234567");
		final String hash2 = hash("0102abcdef01234567");

		assertTrue(servicePut.put(hash, unhex("abcdef01234567")));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertNotFound(() -> service.get(hash2), hash2);

		assertFalse(servicePut.put(hash, unhex("abcdef01234567")));
		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertNotFound(() -> service.get(hash2), hash2);

		assertTrue(servicePut.put(hash2, unhex("0102abcdef01234567")));
		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals("0102abcdef01234567", hex(service.get(hash2)));
	}


	protected static final String hash(final String value) // protected visibility needed by copies of this file in other projects
	{
		assertNotNull(value);

		return Hex.encodeLower(
				MessageDigestUtil.getInstance(ALGORITHM).
						digest(Hex.decodeLower(value)));
	}

	private String putHash(final String value)
	{
		assertNotNull(value);

		final String hash = hash(value);
		assertTrue(servicePut.put(hash, unhex(value)));
		return hash;
	}

	private static void assertNotFound(final Executable executable, final String hash)
	{
		final VaultNotFoundException e =
				assertThrows(VaultNotFoundException.class, executable);
		assertEquals(hash, e.getHashComplete());
	}

	public static final class AssertionErrorOutputStream extends ByteArrayOutputStream
	{
		@Override public synchronized void write(final int b)
		{
			throw new AssertionError();
		}
		@Override public void write(final byte[] b)
		{
			throw new AssertionError();
		}
		@Override public synchronized void write(final byte[] b, final int off, final int len)
		{
			throw new AssertionError();
		}
		@Override public void flush()
		{
			throw new AssertionError();
		}
		@Override public void close()
		{
			throw new AssertionError();
		}
	}

	public static final class NonCloseableOrFlushableOutputStream extends ByteArrayOutputStream
	{
		@Override public void flush()
		{
			throw new AssertionError();
		}
		@Override public void close()
		{
			throw new AssertionError();
		}
	}

	private static String hex(final byte[] bytes)
	{
		return Hex.encodeLower(bytes);
	}

	private static byte[] unhex(final String hex)
	{
		return Hex.decodeLower(hex);
	}

	private void assertContains(
			final boolean expected,
			final String hash)
			throws VaultServiceUnsupportedOperationException
	{
		final boolean supports = supportsContains();
		if(supports)
		{
			assertEquals(expected, service.contains(hash));
		}
		else
		{
			final Throwable e = assertThrows(
					VaultServiceUnsupportedOperationException.class,
					() -> service.contains(hash));
			assertEquals(service.getClass().getName(), e.getMessage());
			assumeTrue(supports, "supportsContains");
		}
	}

	protected boolean supportsContains()
	{
		return true;
	}


	@Test protected void probeBucketTag() throws Exception
	{
		final Exception e = assertThrows(
				Exception.class,
				() -> servicePut.probeBucketTag("my-Bucket"));
		assertEquals("com.exedio.cope.vault.BucketTagNotSupported", e.getClass().getName());
		assertEquals("not supported by " + servicePut.getClass().getName(), e.getMessage());
	}
}
