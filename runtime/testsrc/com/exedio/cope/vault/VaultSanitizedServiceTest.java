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

import static com.exedio.cope.Vault.DEFAULT;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.MessageDigestUtil;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import com.exedio.cope.vaulttest.VaultServiceTest.AssertionErrorOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@MainRule.Tag
@SuppressWarnings("HardcodedLineSeparator")
public class VaultSanitizedServiceTest
{
	String emptyHash;
	VaultSanitizedService s;
	VaultMockService m;

	@BeforeEach void before()
	{
		final Source source =
				describe("DESC", cascade(
						single("algorithm", "MD5"),
						single("service", VaultMockService.class)
				));
		final VaultProperties props = factory.create(source);
		emptyHash = props.getAlgorithmDigestForEmptyByteSequence();
		s = (VaultSanitizedService)props.newServices(DEFAULT).get(DEFAULT);
		m = (VaultMockService)VaultPropertiesTest.unsanitize(s);
	}

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	@Test void purgeSchemaCtxNull()
	{
		assertFails(
				() -> s.purgeSchema(null),
				NullPointerException.class,
				"ctx");
		m.assertIt("");
	}

	@Test void hashNull()
	{
		for(final Executable executable : hashMethods(null))
		{
			assertFails(
					executable,
					NullPointerException.class,
					"hash");
			m.assertIt("");
		}
	}
	@Test void hashLength()
	{
		for(final Executable executable : hashMethods("0123456789abcdef0123456789abcde"))
		{
			assertFails(
					executable,
					IllegalArgumentException.class,
					"hash >0123456789abcdefxx31< must have length 32, but has 31");
			m.assertIt("");
		}
	}
	@Test void hashCharacters()
	{
		for(final Executable executable : hashMethods("0123456789abcdef0123456789abcdex"))
		{
			assertFails(
					executable,
					IllegalArgumentException.class,
					"hash >0123456789abcdefxx32< contains illegal character >x< at position 31");
			m.assertIt("");
		}
	}
	@Test void hashEmpty()
	{
		for(final Executable executable : hashMethods(emptyHash))
		{
			assertFails(
					executable,
					IllegalArgumentException.class,
					"hash of empty byte sequence");
			m.assertIt("");
		}
	}
	private List<Executable> hashMethods(final String hash)
	{
		return asList(
				() -> s.getLength(hash),
				() -> s.get(hash),
				() -> s.get(hash, null),
				() -> s.put(hash, (byte[])     null, null),
				() -> s.put(hash, (InputStream)null, null),
				() -> s.put(hash, (Path)       null, null));
	}

	@Test void getSinkNull()
	{
		assertFails(
				() -> s.get("0123456789abcdef0123456789abcdef", null),
				NullPointerException.class,
				"sink");
		m.assertIt("");
	}
	@Test void putBytesValueNull()
	{
		assertFails(
				() -> s.put("0123456789abcdef0123456789abcdef", (byte[])null, null),
				NullPointerException.class,
				"value");
		m.assertIt("");
	}
	@Test void putStreamValueNull()
	{
		assertFails(
				() -> s.put("0123456789abcdef0123456789abcdef", (InputStream)null, null),
				NullPointerException.class,
				"value");
		m.assertIt("");
	}
	@Test void putPathValueNull()
	{
		assertFails(
				() -> s.put("0123456789abcdef0123456789abcdef", (Path)null, null),
				NullPointerException.class,
				"value");
		m.assertIt("");
	}
	@Test void putBytesInfoNull()
	{
		final byte[] value = {};
		assertFails(
				() -> s.put("0123456789abcdef0123456789abcdef", value, null),
				NullPointerException.class,
				"info");
		m.assertIt("");
	}
	@Test void putStreamInfoNull()
	{
		final InputStream value = new ByteArrayInputStream(new byte[]{});
		assertFails(
				() -> s.put("0123456789abcdef0123456789abcdef", value, null),
				NullPointerException.class,
				"info");
		m.assertIt("");
	}
	@Test void putPathInfoNull()
	{
		final Path value = Paths.get("VaultSanitizedServiceTest");
		assertFails(
				() -> s.put("0123456789abcdef0123456789abcdef", value, null),
				NullPointerException.class,
				"info");
		m.assertIt("");
	}

	@Test void purgeSchemaClosed()
	{
		s.close();
		m.assertIt("close\n");
		final JobContext ctx = new AssertionErrorJobContext();
		assertFails(
				() -> s.purgeSchema(ctx),
				IllegalStateException.class,
				"closed");
		m.assertIt("");
	}
	@Test void getLengthClosed()
	{
		s.close();
		m.assertIt("close\n");
		assertFails(
				() -> s.getLength("0123456789abcdef0123456789abcdef"),
				IllegalStateException.class,
				"closed");
		m.assertIt("");
	}
	@Test void getBytesClosed()
	{
		s.close();
		m.assertIt("close\n");
		assertFails(
				() -> s.get("0123456789abcdef0123456789abcdef"),
				IllegalStateException.class,
				"closed");
		m.assertIt("");
	}
	@Test void getStreamClosed()
	{
		s.close();
		m.assertIt("close\n");
		final OutputStream sink = new AssertionErrorOutputStream();
		assertFails(
				() -> s.get("0123456789abcdef0123456789abcdef", sink),
				IllegalStateException.class,
				"closed");
		m.assertIt("");
	}
	@Test void putBytesClosed()
	{
		s.close();
		m.assertIt("close\n");
		final byte[] value = {1,2,3};
		assertFails(
				() -> s.put(hash(value), value, PUT_INFO),
				IllegalStateException.class,
				"closed");
		m.assertIt("");
	}
	@Test void putStreamClosed()
	{
		s.close();
		m.assertIt("close\n");
		final byte[] value = {1,2,3};
		final InputStream stream = new ByteArrayInputStream(value);
		assertFails(
				() -> s.put(hash(value), stream, PUT_INFO),
				IllegalStateException.class,
				"closed");
		m.assertIt("");
	}
	@Test void putPathClosed() throws IOException
	{
		s.close();
		m.assertIt("close\n");
		final byte[] value = {1,2,3};
		final Path path = files.newPath(value);
		Files.write(path, value);
		assertFails(
				() -> s.put(hash(value), path, PUT_INFO),
				IllegalStateException.class,
				"closed");
		m.assertIt("");
	}

	@Test void purgeSchema()
	{
		final JobContext ctx = new AssertionErrorJobContext();
		s.purgeSchema(ctx);
		m.assertIt("purgeSchema\n");
	}
	@Test void getLength()
	{
		assertFails(
				() -> s.getLength("0123456789abcdef0123456789abcdef"),
				VaultNotFoundException.class,
				"hash not found in vault: 0123456789abcdefxx32");
		m.assertIt("getLength\n");
	}
	@Test void getBytes()
	{
		assertFails(
				() -> s.get("0123456789abcdef0123456789abcdef"),
				VaultNotFoundException.class,
				"hash not found in vault: 0123456789abcdefxx32");
		m.assertIt("getBytes\n");
	}
	@Test void getStream()
	{
		final OutputStream sink = new AssertionErrorOutputStream();
		assertFails(
				() -> s.get("0123456789abcdef0123456789abcdef", sink),
				VaultNotFoundException.class,
				"hash not found in vault: 0123456789abcdefxx32");
		m.assertIt("getStream\n");
	}
	@Test void putBytes()
	{
		final byte[] value = {1,2,3};
		s.put(hash(value), value, PUT_INFO);
		m.assertIt("5289df737df57326fcdd22597afb1fac", "010203", "putBytes VaultServiceTest#PUT_INFO\n");
	}
	@Test void putStream() throws IOException
	{
		final byte[] value = {1,2,3};
		s.put(hash(value), new ByteArrayInputStream(value), PUT_INFO);
		m.assertIt("5289df737df57326fcdd22597afb1fac", "010203", "putStream VaultServiceTest#PUT_INFO\n");
	}
	@Test void putPath() throws IOException
	{
		final byte[] value = {1,2,3};
		final Path path = files.newPath(value);
		Files.write(path, value);
		s.put(hash(value), path, PUT_INFO);
		m.assertIt("5289df737df57326fcdd22597afb1fac", "010203", "putFile VaultServiceTest#PUT_INFO\n");
	}

	private static final Properties.Factory<VaultProperties> factory = VaultProperties.factory();

	private static String hash(final byte[] value)
	{
		assertNotNull(value);

		return Hex.encodeLower(
				MessageDigestUtil.getInstance("MD5").digest(value));
	}

	private static final VaultPutInfo PUT_INFO = new VaultPutInfo()
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


	@Test void testToString()
	{
		assertEquals("VaultMockService:exampleDefault", s.toString());
		m.assertIt("");
	}
}
