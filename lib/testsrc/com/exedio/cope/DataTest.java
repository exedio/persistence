/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class DataTest extends AbstractLibTest
{
	public DataTest()
	{
		super(Main.dataModel);
	}
	
	private DataItem item;
	private final byte[] data = new byte[]{-86,122,-8,23};
	private final byte[] data2 = new byte[]{-97,35,-126,86,19,-8};
	private final byte[] dataFile = new byte[]{-54,104,-63,23,19,-45,71,-23};
	private final byte[] dataEmpty = new byte[]{};
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new DataItem());
	}
	
	public void testData() throws IOException
	{
		assertTrue(item.isDataNull());
		assertEquals(null, item.getData());
		assertDataFile(null);
		assertEquals(-1, item.getDataLength());
		assertEquals(-1, item.getDataLastModified());

		{
			sleepForFileLastModified();
			final Date before = new Date();
			item.setData(stream(data));
			final Date after = new Date();
			assertTrue(!item.isDataNull());
			assertData(data, item.getData());
			assertDataFile(data);
			assertEquals(data.length, item.getDataLength());
			assertWithinFileLastModified(before, after, new Date(item.getDataLastModified()));
		}
		{
			sleepForFileLastModified();
			final Date before = new Date();
			item.setData(stream(data2));
			final Date after = new Date();
			assertTrue(!item.isDataNull());
			assertData(data2, item.getData());
			assertDataFile(data2);
			assertEquals(data2.length, item.getDataLength());
			assertWithinFileLastModified(before, after, new Date(item.getDataLastModified()));
		}
		{
			sleepForFileLastModified();
			final Date before = new Date();
			item.setData(stream(dataEmpty));
			final Date after = new Date();
			assertTrue(!item.isDataNull());
			assertData(dataEmpty, item.getData());
			assertDataFile(dataEmpty);
			assertEquals(0, item.getDataLength());
			assertWithinFileLastModified(before, after, new Date(item.getDataLastModified()));
		}
		item.setData((InputStream)null);
		assertTrue(item.isDataNull());
		assertEquals(-1, item.getDataLength());
		assertEquals(-1, item.getDataLastModified());
		assertEquals(null, item.getData());
		assertDataFile(null);
		{
			sleepForFileLastModified();
			final Date before = new Date();
			item.setData(file(dataFile));
			final Date after = new Date();
			assertTrue(!item.isDataNull());
			assertData(dataFile, item.getData());
			assertDataFile(dataFile);
			assertEquals(dataFile.length, item.getDataLength());
			assertWithinFileLastModified(before, after, new Date(item.getDataLastModified()));
		}
		{
			sleepForFileLastModified();
			final Date before = new Date();
			item.setData(file(dataEmpty));
			final Date after = new Date();
			assertTrue(!item.isDataNull());
			assertData(dataEmpty, item.getData());
			assertDataFile(dataEmpty);
			assertEquals(dataEmpty.length, item.getDataLength());
			assertWithinFileLastModified(before, after, new Date(item.getDataLastModified()));
		}
		item.setData((File)null);
		assertTrue(item.isDataNull());
		assertEquals(-1, item.getDataLength());
		assertEquals(-1, item.getDataLastModified());
		assertEquals(null, item.getData());
		assertDataFile(null);
		
		try
		{
			item.getData(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	
	private final void assertDataFile(final byte[] expectedData) throws IOException
	{
		final File tempFile = File.createTempFile("cope-DataTest.", ".tmp");
		assertTrue(tempFile.delete());
		assertFalse(tempFile.exists());
		
		item.getData(tempFile);
		assertEqualContent(expectedData, tempFile);
	}
	
}
