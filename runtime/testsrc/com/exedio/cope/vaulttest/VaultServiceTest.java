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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vault.VaultService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test is to be applied to all implementations of {@link VaultService}
 * for verifying general contracts of the interface.
 */
public abstract class VaultServiceTest
{
	private static final String ALGORITHM = "SHA-512";

	private VaultProperties properties;
	private VaultService service;

	protected abstract Class<? extends VaultService> getServiceClass();

	/**
	 * Default implementation returns empty properties.
	 */
	protected Properties getServiceProperties() throws Exception
	{
		return new Properties();
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@Before public final void setUpVaultServiceTest() throws Exception
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", ALGORITHM);
		source.setProperty("service", getServiceClass().getName());

		final Properties sp = getServiceProperties();
		for(final String key : sp.stringPropertyNames())
			source.setProperty("service." + key, sp.getProperty(key));

		properties = VaultProperties.factory().create(Sources.view(source, "DESC"));
		service = properties.newService();
	}

	@After public final void tearDownVaultServiceTest()
	{
		service.close();
		service = null;
	}

	protected final VaultProperties getProperties()
	{
		return properties;
	}

	protected final VaultService getService()
	{
		return service;
	}


	@Test public final void vaultPropertiesAlgorithm()
	{
		assertEquals(ALGORITHM, properties.getAlgorithm());
		assertEquals(hash("ab").length(), properties.getAlgorithmLength());
		assertEquals(hash(""), properties.getAlgorithmDigestForEmptyByteSequence());
	}

	@Test public final void notFoundGetLength()
	{
		final String hash = hash("ab");
		try
		{
			service.getLength(hash);
			fail();
		}
		catch(final VaultNotFoundException e)
		{
			assertEquals(hash, e.getHashComplete());
		}
	}

	@Test public final void notFoundGetBytes()
	{
		final String hash = hash("ab");
		try
		{
			service.get(hash);
			fail();
		}
		catch(final VaultNotFoundException e)
		{
			assertEquals(hash, e.getHashComplete());
		}
	}

	@Test public final void notFoundGetStream() throws IOException
	{
		final String hash = hash("ab");
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try
		{
			service.get(hash, stream);
			fail();
		}
		catch(final VaultNotFoundException e)
		{
			assertEquals(hash, e.getHashComplete());
		}
	}

	@Test public final void foundGetLength() throws VaultNotFoundException
	{
		final String hash = putHash("abcdef01234567");
		assertEquals(7, service.getLength(hash));
	}

	@Test public final void foundGetBytes() throws VaultNotFoundException
	{
		final String hash = putHash("abcdef01234567");
		assertEquals("abcdef01234567", hex(service.get(hash)));
	}

	@Test public final void foundGetStream() throws VaultNotFoundException, IOException
	{
		final String hash = putHash("abcdef01234567");
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		service.get(hash, stream);
		assertEquals("abcdef01234567", hex(stream.toByteArray()));
	}

	@Test public final void putBytes() throws VaultNotFoundException
	{
		final String hash = hash("abcdef01234567");
		assertTrue(service.put(hash, unhex("abcdef01234567")));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
	}

	@Test public final void putStream() throws VaultNotFoundException, IOException
	{
		final String hash = hash("abcdef01234567");
		final ByteArrayInputStream value = new ByteArrayInputStream(unhex("abcdef01234567"));
		assertTrue(service.put(hash, value));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
	}

	@Test public final void putFile() throws VaultNotFoundException, IOException
	{
		final String hash = hash("abcdef01234567");
		final File value = File.createTempFile("VaultServiceTest", ".dat");
		try(FileOutputStream s = new FileOutputStream(value))
		{
			s.write(unhex("abcdef01234567"));
		}
		assertTrue(service.put(hash, value));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
	}

	@Test public final void putMany() throws VaultNotFoundException
	{
		final String hash = hash("abcdef01234567");
		final String hash2 = hash("0102abcdef01234567");

		assertTrue(service.put(hash, unhex("abcdef01234567")));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
		try
		{
			service.get(hash2);
			fail();
		}
		catch(final VaultNotFoundException e)
		{
			assertEquals(hash2, e.getHashComplete());
		}

		assertFalse(service.put(hash, unhex("abcdef01234567")));
		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
		try
		{
			service.get(hash2);
			fail();
		}
		catch(final VaultNotFoundException e)
		{
			assertEquals(hash2, e.getHashComplete());
		}

		assertTrue(service.put(hash2, unhex("0102abcdef01234567")));
		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
		assertEquals("0102abcdef01234567", hex(service.get(hash2)));
		assertEquals(9, service.getLength(hash2));
	}


	private static String hash(final String value)
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
		assertTrue(service.put(hash, unhex(value)));
		return hash;
	}

	private static String hex(final byte[] bytes)
	{
		return Hex.encodeLower(bytes);
	}

	private static byte[] unhex(final String hex)
	{
		return Hex.decodeLower(hex);
	}
}
