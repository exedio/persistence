/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
	static final Model MODEL = new Model(DataItem.TYPE, DataSubItem.TYPE);

	public DataTest()
	{
		super(MODEL);
	}
	
	private DataItem item;
	private final byte[] data0  = {};
	private final byte[] data4  = {-86,122,-8,23};
	private final byte[] data6  = {-97,35,-126,86,19,-8};
	private final byte[] data8  = {-54,104,-63,23,19,-45,71,-23};
	private final byte[] data10 = {-97,19,-8,35,-126,-86,122,86,19,-8};
	private final byte[] data11 = {22,-97,19,-8,35,-126,-86,122,86,19,-8};
	private byte[] dataBig;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		final int data8Length = data8.length;
		// must be substantially larger than
		// dataAttribute.bufferSize* values in cope.properties
		final int dataBigLength = (50*1024) + 77;
		dataBig = new byte[dataBigLength];
		for(int i = 0; i<dataBigLength; i++)
			dataBig[i] = data8[i % data8Length];
		
		deleteOnTearDown(item = new DataItem());
	}
	
	@Override
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
			
			final File tempFile = File.createTempFile("exedio-cope-DataTest-", ".tmp");
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
		assertEquals(byte[].class, item.data.getValueClass());
		
		assertEquals(item.TYPE, item.data10.getType());
		assertEquals("data10", item.data10.getName());
		assertEquals(false, item.data10.isMandatory());
		assertEqualsUnmodifiable(list(), item.data10.getPatterns());
		assertEquals(10, item.data10.getMaximumLength());
		assertEquals(byte[].class, item.data10.getValueClass());

		try
		{
			new DataField(Item.OPTIONAL).lengthMax(0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("maximum length must be greater zero, but was 0.", e.getMessage());
		}
		try
		{
			new DataField(Item.OPTIONAL).lengthMax(-10);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("maximum length must be greater zero, but was -10.", e.getMessage());
		}

		// test data
		assertIt(null);

		// set byte[]
		item.setData(data4);
		assertIt(data4);

		item.setData(data6);
		assertIt(data6);

		item.setData(data0);
		assertIt(data0);

		item.setData(dataBig);
		assertIt(dataBig);

		item.setData((byte[])null);
		assertIt(null);


		// set InputStream
		item.setData(stream(data4));
		assertStreamClosed();
		assertIt(data4);

		item.setData(stream(data6));
		assertStreamClosed();
		assertIt(data6);

		item.setData(stream(data0));
		assertStreamClosed();
		assertIt(data0);

		item.setData(stream(dataBig));
		assertStreamClosed();
		assertIt(dataBig);

		item.setData((InputStream)null);
		assertIt(null);

		
		// set File
		item.setData(file(data8));
		assertIt(data8);

		item.setData(file(data0));
		assertIt(data0);

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
		
		subItem.setData(stream(data4));
		assertStreamClosed();
		assertIt(data4, subItem);
		assertEquals(data4.length, subItem.getDataLength());
		
		// test maximum length
		item.setData10(data0);
		item.setData10(data4);
		item.setData10(data6);
		item.setData10(data8);
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
			assertEquals(item.data10, e.getFeature());
			assertEquals(item.data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(true, e.isLengthExact());
			assertEquals("length violation on " + item + ", 11 bytes is too long for " + item.data10, e.getMessage());
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
			assertEquals(item.data10, e.getFeature());
			assertEquals(item.data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(false, e.isLengthExact());
			assertEquals(e.getMessage(), "length violation on " + item + ", 11 bytes or more is too long for " + item.data10, e.getMessage());
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
			assertEquals(item.data10, e.getFeature());
			assertEquals(item.data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(true, e.isLengthExact());
			assertEquals("length violation on " + item + ", 11 bytes is too long for " + item.data10, e.getMessage());
		}
		assertData(data10, item.getData10());
		

		// implements Settable
		assertNull(item.getName());
		item.set(new SetValue[]{
				DataItem.data.map(data8),
				DataItem.data10.map(data10),
				DataItem.name.map("eins"),
		});
		assertData(data8, item.getData());
		assertData(data10, item.getData10());
		assertEquals("eins", item.getName());

		item.set(new SetValue[]{
				DataItem.data.map(data11),
				DataItem.data10.map(data10),
		});
		assertData(data11, item.getData());
		assertData(data10, item.getData10());
		assertEquals("eins", item.getName());
		
		{
			final DataItem item2 = new DataItem(data4, data10);
			deleteOnTearDown(item2);
			assertData(data4, item2.getData());
			assertData(data10, item2.getData10());
		}
		{
			final DataItem item3 = DataItem.TYPE.newItem(new SetValue[]{
					DataItem.data.map(data6),
					DataItem.data10.map(data10),
			});
			deleteOnTearDown(item3);
			assertData(data6, item3.getData());
			assertData(data10, item3.getData10());
		}

		assertData(data10, item.getData10());
		try
		{
			item.set(new SetValue[]{DataItem.data10.map(data11)});
		}
		catch(DataLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.data10, e.getFeature());
			assertEquals(item.data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(true, e.isLengthExact());
			assertEquals("length violation on " + item + ", 11 bytes is too long for " + item.data10, e.getMessage());
		}
		assertData(data10, item.getData10());

		try
		{
			DataItem.TYPE.newItem(new SetValue[]{DataItem.data10.map(data11)});
		}
		catch(DataLengthViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.data10, e.getFeature());
			assertEquals(item.data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(true, e.isLengthExact());
			assertEquals("length violation on a newly created item, 11 bytes is too long for " + item.data10, e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		item.data.set(item, data8);
		assertData(data8, item.getData());
		try
		{
			item.set(new SetValue[]{
					new SetValue(DataItem.data, "zack"),
			});
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a byte[], but was a java.lang.String for " + item.data + '.', e.getMessage());
		}
		assertData(data8, item.getData());
		
		try
		{
			DataItem.TYPE.newItem(new SetValue[]{
					new SetValue(DataItem.data, new Integer(1)),
			});
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a byte[], but was a java.lang.Integer for " + item.data + '.', e.getMessage());
		}
		assertData(data8, item.getData());
	}
	
}
