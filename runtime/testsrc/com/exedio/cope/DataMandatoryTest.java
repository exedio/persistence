/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

public class DataMandatoryTest extends AbstractLibTest
{
	private static final Model MODEL = new Model(DataMandatoryItem.TYPE);
	private static final DataField data = DataMandatoryItem.data;

	public DataMandatoryTest()
	{
		super(MODEL);
	}
	
	private final byte[] data4  = {-86,122,-8,23};
	private final byte[] data6  = {-97,35,-126,86,19,-8};
	
	private DataMandatoryItem item;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new DataMandatoryItem(data4));
	}
	
	public void testData() throws MandatoryViolationException, IOException
	{
		// test model
		assertEquals(false, data.isFinal());
		assertEquals(true, data.isMandatory());
		
		// test persistence
		assertData(data4, item.getDataArray());

		item.setData(data6);
		assertData(data6, item.getDataArray());

		try
		{
			item.setData((byte[])null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(data6, item.getDataArray());

		item.setData(stream(data4));
		assertData(data4, item.getDataArray());

		try
		{
			item.setData((InputStream)null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(data4, item.getDataArray());

		item.setData(file(data6));
		assertData(data6, item.getDataArray());
		
		try
		{
			item.setData((File)null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(data6, item.getDataArray());

		try
		{
			new DataMandatoryItem((byte[])null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(null, e.getItem());
		}
		assertEquals(list(item), item.TYPE.search());

		try
		{
			new DataMandatoryItem(new SetValue[0]);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(null, e.getItem());
		}
		assertEquals(list(item), item.TYPE.search());
	}
}
