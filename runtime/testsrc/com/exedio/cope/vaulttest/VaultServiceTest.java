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

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.DataField;
import com.exedio.cope.Item;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.JobContexts;
import com.exedio.cope.util.MessageDigestUtil;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vault.VaultPutInfo;
import com.exedio.cope.vault.VaultService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
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
	private static final String ALGORITHM = "SHA-512";

	private VaultProperties properties;
	private VaultService service;

	protected abstract Class<? extends VaultService> getServiceClass();

	/**
	 * Default implementation returns empty properties.
	 */
	@SuppressWarnings("RedundantThrows") // needed by copies of this file in other projects
	protected Properties getServiceProperties() throws Exception
	{
		return new Properties();
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@BeforeEach final void setUpVaultServiceTest() throws Exception
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

	@AfterEach final void tearDownVaultServiceTest()
	{
		if(service!=null)
		{
			service.close();
			service = null;
		}
	}

	protected final VaultProperties getProperties()
	{
		return properties;
	}

	protected final VaultService getService()
	{
		return service;
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

	@Test final void notFoundGetLength()
	{
		final String hash = hash("ab");
		assertNotFound(() -> service.getLength(hash), hash);
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

	@Test final void foundGetLength() throws VaultNotFoundException
	{
		final String hash = putHash("abcdef01234567");
		assertEquals(7, service.getLength(hash));
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
		assertTrue(service.put(hash, unhex("abcdef01234567"), PUT_INFO));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
	}

	@Test final void putBytesInfo() throws VaultNotFoundException
	{
		final String hash = hash("abcdef01234567");
		assertTrue(service.put(hash, unhex("abcdef01234567"), PUT_INFO_REGULAR));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
	}

	@Test final void putStream() throws VaultNotFoundException, IOException
	{
		final String hash = hash("abcdef01234567");
		final ByteArrayInputStream value = new ByteArrayInputStream(unhex("abcdef01234567"));
		assertTrue(service.put(hash, value, PUT_INFO));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
	}

	@Test final void putStreamInfo() throws VaultNotFoundException, IOException
	{
		final String hash = hash("abcdef01234567");
		final ByteArrayInputStream value = new ByteArrayInputStream(unhex("abcdef01234567"));
		assertTrue(service.put(hash, value, PUT_INFO_REGULAR));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
	}

	@Test final void putPath() throws VaultNotFoundException, IOException
	{
		final String hash = hash("abcdef01234567");
		final Path value = Files.createTempFile("VaultServiceTest", ".dat");
		try(OutputStream s = Files.newOutputStream(value))
		{
			s.write(unhex("abcdef01234567"));
		}
		assertTrue(service.put(hash, value, PUT_INFO));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
	}

	@Test final void putPathInfo() throws VaultNotFoundException, IOException
	{
		final String hash = hash("abcdef01234567");
		final Path value = Files.createTempFile("VaultServiceTest", ".dat");
		try(OutputStream s = Files.newOutputStream(value))
		{
			s.write(unhex("abcdef01234567"));
		}
		assertTrue(service.put(hash, value, PUT_INFO_REGULAR));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
	}

	@Test final void putMany() throws VaultNotFoundException
	{
		final String hash = hash("abcdef01234567");
		final String hash2 = hash("0102abcdef01234567");

		assertTrue(service.put(hash, unhex("abcdef01234567"), PUT_INFO));

		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
		assertNotFound(() -> service.get(hash2), hash2);

		assertFalse(service.put(hash, unhex("abcdef01234567"), PUT_INFO));
		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
		assertNotFound(() -> service.get(hash2), hash2);

		assertTrue(service.put(hash2, unhex("0102abcdef01234567"), PUT_INFO));
		assertEquals("abcdef01234567", hex(service.get(hash)));
		assertEquals(7, service.getLength(hash));
		assertEquals("0102abcdef01234567", hex(service.get(hash2)));
		assertEquals(9, service.getLength(hash2));
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
		assertTrue(service.put(hash, unhex(value), PUT_INFO));
		return hash;
	}

	private static void assertNotFound(final Executable executable, final String hash)
	{
		final VaultNotFoundException e =
				assertThrows(VaultNotFoundException.class, executable);
		assertEquals(hash, e.getHashComplete());
	}

	protected static final VaultPutInfo PUT_INFO = new VaultPutInfo()
	{
		@Override
		public String getOrigin()
		{
			return null;
		}
		@Override
		public String toString()
		{
			return "VaultServiceTest#PUT_INFO";
		}
	};

	protected static final VaultPutInfo PUT_INFO_REGULAR = new VaultPutInfo()
	{
		@Override
		public DataField getField()
		{
			return InfoItem.infoField;
		}
		@Override
		public Item getItem()
		{
			try
			{
				final Constructor<ActivationParameters> constructor =
						ActivationParameters.class.getDeclaredConstructor(Type.class, long.class);
				constructor.setAccessible(true);
				return new InfoItem(constructor.newInstance(InfoItem.TYPE, 556677));
			}
			catch(final ReflectiveOperationException e)
			{
				throw new RuntimeException(e);
			}
		}
		@Override
		public String getOrigin()
		{
			return "VaultServiceTest#PUT_INFO_REGULAR#origin";
		}
		@Override
		public String toString()
		{
			return "VaultServiceTest#PUT_INFO_REGULAR";
		}
	};

	@WrapperIgnore
	private static final class InfoItem extends Item
	{
		static final DataField infoField = new DataField();
		static final Type<InfoItem> TYPE = TypesBound.newType(InfoItem.class);
		private InfoItem(final ActivationParameters ap){ super(ap); }
		private static final long serialVersionUID = 1l;
	}

	public static final class AssertionErrorOutputStream extends ByteArrayOutputStream
	{
		@Override public synchronized void write(final int b)
		{
			throw new AssertionError();
		}
		@Override public void write(final byte b[])
		{
			throw new AssertionError();
		}
		@Override public synchronized void write(final byte b[], final int off, final int len)
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
}
