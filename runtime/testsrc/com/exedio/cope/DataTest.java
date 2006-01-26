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
	// TODO rename by length
	private final byte[] data = new byte[]{-86,122,-8,23};
	private final byte[] data2 = new byte[]{-97,35,-126,86,19,-8};
	private final byte[] dataFile = new byte[]{-54,104,-63,23,19,-45,71,-23};
	private final byte[] data10 = new byte[]{-97,19,-8,35,-126,-86,122,86,19,-8};
	private final byte[] data11 = new byte[]{22,-97,19,-8,35,-126,-86,122,86,19,-8};
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
	
	private void assertIt(final byte[] expectedData) throws MandatoryViolationException, IOException
	{
		assertIt(expectedData, item);
	}
	
	private void assertIt(final byte[] expectedData, final DataItem item)
		throws MandatoryViolationException, IOException
	{
		assertIt(expectedData, item, oracle, model);
	}
	
	private static final void assertIt(final byte[] expectedData, final DataItem item, final boolean oracle, final Model model)
		throws MandatoryViolationException, IOException
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
	
	public void testData() throws MandatoryViolationException, IOException
	{
		assertEquals(10, data10.length);
		assertEquals(11, data11.length);
		
		// test model
		assertEquals(item.TYPE, item.data.getType());
		assertEquals("data", item.data.getName());
		assertEquals(false, item.data.isMandatory());
		assertEqualsUnmodifiable(list(), item.data.getPatterns());
		assertEquals(item.data.DEFAULT_LENGTH, item.data.getMaximumLength());
		
		assertEquals(item.TYPE, item.data10.getType());
		assertEquals("data10", item.data10.getName());
		assertEquals(false, item.data10.isMandatory());
		assertEqualsUnmodifiable(list(), item.data10.getPatterns());
		assertEquals(10, item.data10.getMaximumLength());
		
		try
		{
			new DataAttribute(Item.OPTIONAL).lengthMax(0);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("maximum length must be greater zero, but was 0.", e.getMessage());
		}
		try
		{
			new DataAttribute(Item.OPTIONAL).lengthMax(-10);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("maximum length must be greater zero, but was -10.", e.getMessage());
		}

		// test data
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
		assertStreamClosed();
		assertIt(data);

		item.setData(stream(data2));
		assertStreamClosed();
		assertIt(data2);

		item.setData(stream(dataEmpty));
		assertStreamClosed();
		assertIt(dataEmpty);

		item.setData(stream(dataBig));
		assertStreamClosed();
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
		
		final DataSubItem subItem = new DataSubItem();
		deleteOnTearDown(subItem);
		
		subItem.setData(stream(data));
		assertStreamClosed();
		assertIt(data, subItem);
		assertEquals(data.length, subItem.getDataLength());
		
		// test maximum length
		item.setData10(data10);
		assertData(data10, item.getData10());
		
		try
		{
			item.setData10(data11);
			fail();
		}
		catch(DataLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.data10, e.getDataAttribute());
			assertEquals(11, e.getLength());
			assertEquals("length violation on DataItem.0, 11 bytes is too long for DataItem#data10", e.getMessage());
		}
		assertData(data10, item.getData10());
		try
		{
			item.setData10(stream(data11));
			fail();
		}
		catch(DataLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.data10, e.getDataAttribute());
			assertEquals(-1, e.getLength());
			assertEquals(e.getMessage(), "length violation on DataItem.0, is too long for DataItem#data10", e.getMessage());
		}
		if(model.getProperties().hasDatadirPath()) // TODO should not be needed
			item.setData10(data10);
		assertData(data10, item.getData10());
		try
		{
			item.setData10(file(data11));
			fail();
		}
		catch(DataLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.data10, e.getDataAttribute());
			assertEquals(11, e.getLength());
			assertEquals("length violation on DataItem.0, 11 bytes is too long for DataItem#data10", e.getMessage());
		}
		assertData(data10, item.getData10());
	}
	
}
