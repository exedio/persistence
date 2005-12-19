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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	private byte[] dataBig;
	
	public void setUp() throws Exception
	{
		super.setUp();
		
		final int dataFileLength = dataFile.length;
		final int dataBigLength = (1024*1024) + 77;
		dataBig = new byte[dataBigLength];
		for(int i = 0; i<dataBigLength; i++)
			dataBig[i] = dataFile[i % dataFileLength];
		
		deleteOnTearDown(item = new DataItem());
	}
	
	public void tearDown() throws Exception
	{
		// release memory
		dataBig = null;
		
		super.tearDown();
	}
	
	private void assertIt(final byte[] expectedData) throws IOException
	{
		if(expectedData!=null && !(oracle && !model.getProperties().hasDatadirPath() && expectedData.length==0))
		{
			assertTrue(!item.isDataNull());
			assertEquals(expectedData.length, item.getDataLength());
			assertData(expectedData, item.getData());

			final ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
			item.getData(tempStream);
			assertData(expectedData, tempStream.toByteArray());
			
			final File tempFile = File.createTempFile("cope-DataTest.", ".tmp");
			assertTrue(tempFile.delete());
			assertFalse(tempFile.exists());
			item.getData(tempFile);
			assertTrue(tempFile.exists());
			assertEqualContent(expectedData, tempFile);
		}
		else
		{
			assertTrue(item.isDataNull());
			assertEquals(-1, item.getDataLength());
			assertEquals(null, item.getData());
			
			final ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
			item.getData(tempStream);
			assertEquals(0, tempStream.toByteArray().length);
			
			final File tempFile = File.createTempFile("cope-DataTest.", ".tmp");
			assertTrue(tempFile.delete());
			assertFalse(tempFile.exists());
			item.getData(tempFile);
			assertFalse(tempFile.exists());
		}
	}
	
	public void testData() throws IOException
	{
		assertIt(null);

		// set byte[]
		item.setData(data);
		assertIt(data);

		item.setData(data2);
		assertIt(data2);

		item.setData(dataEmpty);
		assertIt(dataEmpty);

		item.setData(dataBig);
		assertIt(dataBig);

		item.setData((byte[])null);
		assertIt(null);


		// set InputStream
		item.setData(stream(data));
		assertIt(data);

		item.setData(stream(data2));
		assertIt(data2);

		item.setData(stream(dataEmpty));
		assertIt(dataEmpty);

		item.setData(stream(dataBig));
		assertIt(dataBig);

		item.setData((InputStream)null);
		assertIt(null);

		
		// set File
		item.setData(file(dataFile));
		assertIt(dataFile);

		item.setData(file(dataEmpty));
		assertIt(dataEmpty);

		item.setData(file(dataBig));
		assertIt(dataBig);

		item.setData((File)null);
		assertIt(null);
		
		
		try
		{
			item.getData((OutputStream)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			item.getData((File)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	
}
