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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Test;

public class DataValueTest
{
	@Test
	public void bytesShort()
	{
		assertEquals("DataField.Value:aa7af817", toValue(bytes4).toString());
	}
	@Test
	public void bytesLong()
	{
		assertEquals("DataField.Value:9f13f82382aa7a5613f8", toValue(bytes10).toString());
	}
	@Test
	public void bytesTooLong()
	{
		assertEquals("DataField.Value:169f13f82382aa7a5613...(11)", toValue(bytes11).toString());
	}
	@Test
	public void bytesMuchTooLong()
	{
		assertEquals("DataField.Value:ca47aa7af817e968c12c...(21)", toValue(bytes21).toString());
	}
	@Test
	public void bytesNull()
	{
		assertEquals(null, toValue((byte[])null));
	}
	@Test
	public void stream()
	{
		final ByteArrayInputStream testBaos = new ByteArrayInputStream(bytes4);
		assertEquals("DataField.Value:"+testBaos, toValue(testBaos).toString());
	}
	@Test
	public void streamNull()
	{
		assertEquals(null, toValue((InputStream)null));
	}
	@Test
	public void file()
	{
		assertEquals("DataField.Value:hallo.txt", toValue(new File("hallo.txt")).toString());
	}
	@Test
	public void fileNull()
	{
		assertEquals(null, toValue((File)null));
	}
	@Test
	public void zip() throws URISyntaxException, IOException
	{
		final String filePath = DataValueTest.class.getResource("DataValueTest.zip").toURI().getPath();
		final ZipFile file = new ZipFile(filePath);
		final ZipEntry entry = file.getEntry("hallo.txt");

		assertEquals("DataField.Value:" + filePath + "#hallo.txt", toValue(file, entry).toString());
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
	@Test
	public void zipNull()
	{
		assertEquals(null, toValue(null, null));
	}

	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes10 = {-97,19,-8,35,-126,-86,122,86,19,-8};
	private static final byte[] bytes11 = {22,-97,19,-8,35,-126,-86,122,86,19,-8};
	private static final byte[] bytes21 = {-54,71,-86,122,-8,23,-23,104,-63,44,23,19,-45,-63,23,71,-23,19,-45,71,-23};
}
