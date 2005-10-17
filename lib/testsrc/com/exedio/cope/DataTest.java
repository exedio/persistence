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

import java.io.IOException;
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
		assertEquals(-1, item.getDataLength(item.data));
		assertEquals(-1, item.getDataLastModified(item.data));

		{
			sleepForFileLastModified();
			final Date beforeData = new Date();
			item.setData(stream(data));
			final Date afterData = new Date();
			assertTrue(!item.isDataNull());
			assertData(data, item.getData());
			assertEquals(data.length, item.getDataLength(item.data));
			assertWithinFileLastModified(beforeData, afterData, new Date(item.getDataLastModified(item.data)));
		}
		{
			sleepForFileLastModified();
			final Date beforeData2 = new Date();
			item.setData(stream(data2));
			final Date afterData2 = new Date();
			assertTrue(!item.isDataNull());
			assertData(data2, item.getData());
			assertEquals(data2.length, item.getDataLength(item.data));
			assertWithinFileLastModified(beforeData2, afterData2, new Date(item.getDataLastModified(item.data)));
		}
		{
			sleepForFileLastModified();
			final Date beforeDataEmpty = new Date();
			item.setData(stream(dataEmpty));
			final Date afterDataEmpty = new Date();
			assertTrue(!item.isDataNull());
			assertData(dataEmpty, item.getData());
			assertEquals(0, item.getDataLength(item.data));
			assertWithinFileLastModified(beforeDataEmpty, afterDataEmpty, new Date(item.getDataLastModified(item.data)));
		}
		item.setData(null);
		assertTrue(item.isDataNull());
		assertEquals(-1, item.getDataLength(item.data));
		assertEquals(-1, item.getDataLastModified(item.data));
		assertEquals(null, item.getData());
	}

}
