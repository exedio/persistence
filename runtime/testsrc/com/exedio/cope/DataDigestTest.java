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

package com.exedio.cope;

import static com.exedio.cope.DataField.toValue;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.util.StrictFile.delete;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.DataField.Value;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DataDigestTest
{
	private final MyTemporaryFolder files = new MyTemporaryFolder();

	@Test void testUpdate() throws IOException
	{
		assertUpdate("904ac396ac3d50faa666e57146fe7862", bytes4);
		assertUpdate(
				"6ce62d0dbd8e8b3f453ba742c102cd0b",
				"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
				"knolloknolloknolloknolloknolloknolloknolloknolloknolloknolloknollo" +
				"knolloknolloknolloknolloknollo");

		// reference example from https://de.wikipedia.org/wiki/MD5
		assertUpdate(
				"d41d8cd98f00b204e9800998ecf8427e",
				bytes0);
		assertUpdate(
				"a3cca2b2aa1e3b5b3b5aad99a8529074",
				"Franz jagt im komplett verwahrlosten Taxi quer durch Bayern");
		assertUpdate(
				"7e716d0e702df0505fc72e2b89467910",
				"Frank jagt im komplett verwahrlosten Taxi quer durch Bayern");
	}

	private void assertUpdate(final String hash, final String input) throws IOException
	{
		assertUpdate(hash, input.getBytes(UTF_8));
	}

	private void assertUpdate(final String hash, final byte[] input) throws IOException
	{
		messageDigest.reset();
		toValue(input).update(messageDigest);
		assertEquals(hash, Hex.encodeLower(messageDigest.digest()));

		messageDigest.reset();
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
		toValue(inputStream).update(messageDigest);
		assertEquals(hash, Hex.encodeLower(messageDigest.digest()));

		messageDigest.reset();
		final Path inputPath = files.newPath(input);
		toValue(inputPath).update(messageDigest);
		assertEquals(hash, Hex.encodeLower(messageDigest.digest()));
		Files.delete(inputPath);

		messageDigest.reset();
		final File inputFile = files.newFile(input);
		toValue(inputFile).update(messageDigest);
		assertEquals(hash, Hex.encodeLower(messageDigest.digest()));
		delete(inputFile);
	}

	private static final MessageDigest messageDigest = MessageDigestUtil.getInstance("MD5");

	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};


	@Test void testExhaustionArray()
	{
		final Value value = toValue(bytes4);
		assertAsArray(bytes4, value);
		assertExhausted(value);
	}

	@Test void testExhaustionStream()
	{
		final ByteArrayInputStream stream = new ByteArrayInputStream(bytes4);
		final Value value = toValue(stream);
		assertAsArray(bytes4, value);
		assertExhausted(value);
	}

	@Test void testExhaustionPath() throws IOException
	{
		final Path inputPath = files.newPath(bytes4);
		final Value value = toValue(inputPath);
		assertAsArray(bytes4, value);
		assertExhausted(value);
		Files.delete(inputPath);
	}

	@Test void testExhaustionFile() throws IOException
	{
		final File inputFile = files.newFile(bytes4);
		final Value value = toValue(inputFile);
		assertAsArray(bytes4, value);
		assertExhausted(value);
		delete(inputFile);
	}

	@Test void testExhaustionZip() throws IOException, URISyntaxException
	{
		try(ZipFile file = DataTest.openZip())
		{
			final ZipEntry entry = file.getEntry("bytes4.dat");
			final Value value = toValue(file, entry);
			assertAsArray(bytes4, value);
			assertExhausted(value);
		}
	}


	@Test void testExhaustionArrayUpdate() throws IOException
	{
		final Value value = toValue(bytes4);
		messageDigest.reset();
		final Value replacementValue = value.update(messageDigest);
		assertNotSame(replacementValue, value);
		assertEquals("904ac396ac3d50faa666e57146fe7862", Hex.encodeLower(messageDigest.digest()));

		assertExhausted(value);
		assertAsArray(bytes4, replacementValue);
	}

	@Test void testExhaustionStreamUpdate() throws IOException
	{
		final ByteArrayInputStream stream = new ByteArrayInputStream(bytes4);
		final Value value = toValue(stream);

		messageDigest.reset();
		final Value replacementValue = value.update(messageDigest);
		assertNotSame(replacementValue, value);
		assertEquals("904ac396ac3d50faa666e57146fe7862", Hex.encodeLower(messageDigest.digest()));

		assertExhausted(value);
		assertAsArray(bytes4, replacementValue);
	}

	@Test void testExhaustionPathUpdate() throws IOException
	{
		final Path inputPath = files.newPath(bytes4);
		final Value value = toValue(inputPath);

		messageDigest.reset();
		final Value replacementValue = value.update(messageDigest);
		assertNotSame(replacementValue, value);
		assertEquals("904ac396ac3d50faa666e57146fe7862", Hex.encodeLower(messageDigest.digest()));

		assertExhausted(value);
		assertAsArray(bytes4, replacementValue);

		Files.delete(inputPath);
	}

	@Test void testExhaustionFileUpdate() throws IOException
	{
		final File inputFile = files.newFile(bytes4);
		final Value value = toValue(inputFile);

		messageDigest.reset();
		final Value replacementValue = value.update(messageDigest);
		assertNotSame(replacementValue, value);
		assertEquals("904ac396ac3d50faa666e57146fe7862", Hex.encodeLower(messageDigest.digest()));

		assertExhausted(value);
		assertAsArray(bytes4, replacementValue);

		delete(inputFile);
	}

	@Test void testExhaustionZipUpdate() throws IOException, URISyntaxException
	{
		try(ZipFile file = DataTest.openZip())
		{
			final ZipEntry entry = file.getEntry("bytes4.dat");
			final Value value = toValue(file, entry);

			messageDigest.reset();
			final Value replacementValue = value.update(messageDigest);
			assertNotSame(replacementValue, value);
			assertEquals("904ac396ac3d50faa666e57146fe7862", Hex.encodeLower(messageDigest.digest()));

			assertExhausted(value);
			assertAsArray(bytes4, replacementValue);
		}
	}

	private static void assertData(final byte[] expectedData, final byte[] actualData)
	{
		if(!Arrays.equals(expectedData, actualData))
			fail("expected " + Arrays.toString(expectedData) + ", but was " + Arrays.toString(actualData));
	}

	private static void assertExhausted(final Value value)
	{
		messageDigest.reset();
		assertFails(
				() -> value.update(messageDigest),
				IllegalStateException.class,
				"Value already exhausted: " + value + ". " +
				"Each DataField.Value can be used for at most one setter action.");
		assertFails(
				() -> value.asArray(null, null),
				IllegalStateException.class,
				"Value already exhausted: " + value + ". " +
				"Each DataField.Value can be used for at most one setter action.");
	}

	public void assertAsArray(final byte[] expected, final Value value)
	{
		final DataField field = new DataField();
		assertData(expected, value.asArray(field, null));
	}
}
