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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.Test;

public class DataValueTest
{
	@Test void bytesShort()
	{
		assertEquals("DataField.Value:aa7af817", toValue(bytes4).toString());

		final SetValue<?> sv = FIELD.map(bytes4);
		assertSame(FIELD, sv.settable);
		assertEquals("DataField.Value:aa7af817", sv.value.toString());
	}
	@Test void bytesLong()
	{
		assertEquals("DataField.Value:9f13f82382aa7a5613f8", toValue(bytes10).toString());
	}
	@Test void bytesTooLong()
	{
		assertEquals("DataField.Value:169f13f82382aa7a5613...(11)", toValue(bytes11).toString());
	}
	@Test void bytesMuchTooLong()
	{
		assertEquals("DataField.Value:ca47aa7af817e968c12c...(21)", toValue(bytes21).toString());
	}
	@Test void bytesNull()
	{
		assertEquals(null, toValue((byte[])null));

		final SetValue<?> sv = FIELD.map((byte[])null);
		assertSame(FIELD, sv.settable);
		assertEquals(null, sv.value);
	}
	@Test void stream()
	{
		final ByteArrayInputStream testBaos = new ByteArrayInputStream(bytes4);
		//noinspection ObjectToString
		assertEquals("DataField.Value:"+testBaos, toValue(testBaos).toString());

		final SetValue<?> sv = FIELD.map(testBaos);
		assertSame(FIELD, sv.settable);
		//noinspection ObjectToString
		assertEquals("DataField.Value:"+testBaos, sv.value.toString());
	}
	@Test void streamNull()
	{
		assertEquals(null, toValue((InputStream)null));

		final SetValue<?> sv = FIELD.map((InputStream)null);
		assertSame(FIELD, sv.settable);
		assertEquals(null, sv.value);
	}
	@Test void path()
	{
		final Path path = Paths.get("hallo.txt");
		assertEquals("DataField.Value:hallo.txt", toValue(path).toString());

		final SetValue<?> sv = FIELD.map(path);
		assertSame(FIELD, sv.settable);
		assertEquals("DataField.Value:hallo.txt", sv.value.toString());
	}
	@Test void pathNull()
	{
		assertEquals(null, toValue((Path)null));

		final SetValue<?> sv = FIELD.map((Path)null);
		assertSame(FIELD, sv.settable);
		assertEquals(null, sv.value);
	}
	@Test void file()
	{
		assertEquals("DataField.Value:hallo.txt", toValue(new File("hallo.txt")).toString());

		final SetValue<?> sv = FIELD.map(new File("hallo.txt"));
		assertSame(FIELD, sv.settable);
		assertEquals("DataField.Value:hallo.txt", sv.value.toString());
	}
	@Test void fileNull()
	{
		assertEquals(null, toValue((File)null));

		final SetValue<?> sv = FIELD.map((File)null);
		assertSame(FIELD, sv.settable);
		assertEquals(null, sv.value);
	}
	@Test void zip() throws URISyntaxException, IOException
	{
		final String filePath = DataValueTest.class.getResource("DataValueTest.zip").toURI().getPath();
		final ZipFile file = new ZipFile(filePath);
		final ZipEntry entry = file.getEntry("hallo.txt");

		assertEquals("DataField.Value:" + new File(filePath) + "#hallo.txt", toValue(file, entry).toString());
		try
		{
			toValue(null, entry);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("if file is null, entry must also be null", e.getMessage());
		}
		try
		{
			toValue(file, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("if file is not null, entry must also be not null", e.getMessage());
		}
	}
	@Test void zipNull()
	{
		assertEquals(null, toValue(null, null));
	}

	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes10 = {-97,19,-8,35,-126,-86,122,86,19,-8};
	private static final byte[] bytes11 = {22,-97,19,-8,35,-126,-86,122,86,19,-8};
	private static final byte[] bytes21 = {-54,71,-86,122,-8,23,-23,104,-63,44,23,19,-45,-63,23,71,-23,19,-45,71,-23};
	private static final DataField FIELD = new DataField();
}
