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

import java.io.IOException;
import java.io.InputStream;

public class FieldMediaTest extends FieldTest
{
	public void testSomeData() throws IOException
	{
		assertEquals(item.TYPE, item.someData.getType());
		assertEquals("someData", item.someData.getName());
		assertEquals(true, item.someData.checkContentType("what/ever"));
		assertEquals("*/*", item.someData.getContentTypeDescription());

		// TODO: test with not null data
		assertEquals(item.TYPE, item.someData.getType());
		assertEquals(null, item.getSomeDataURL());
		assertEquals(null, item.getSomeDataBody());
		assertEquals(null, item.getSomeDataContentType());

		final byte[] bytes = {3,7,1,4};
		item.setSomeData(stream(bytes), "someMimeMajor/someMimeMinor");
		assertStreamClosed();

		final String prefix =
			model.getProperties().getMediaRootUrl() + "AttributeItem/someData/";
		final String expectedURL =
			prefix + item.getCopeID();
		//System.out.println(expectedURL);
		//System.out.println(item.getSomeDataURL());
		assertEquals(expectedURL, item.getSomeDataURL());
		assertData(bytes, item.getSomeDataBody());
		assertEquals("someMimeMajor/someMimeMinor", item.getSomeDataContentType());

		restartTransaction();
		assertEquals(expectedURL, item.getSomeDataURL());
		assertData(bytes, item.getSomeDataBody());
		assertEquals("someMimeMajor/someMimeMinor", item.getSomeDataContentType());

		assertDataMime(item, "image/jpeg", bytes, "jpg");
		assertDataMime(item, "image/pjpeg", bytes, "jpg");
		assertDataMime(item, "image/gif", bytes, "gif");
		assertDataMime(item, "image/png", bytes, "png");
		assertDataMime(item, "image/someMinor", bytes, null);

		final byte[] manyBytes = new byte[49467];
		for(int i = 0; i<manyBytes.length; i++)
		{
			manyBytes[i] = (byte)((121*i)%253);
			//System.out.print(manyBytes[i]+", ");
		}
		item.setSomeData(stream(manyBytes),"someMimeMajor/someMimeMinor");
		assertStreamClosed();
		assertData(manyBytes, item.getSomeDataBody());

		item.setSomeData((InputStream)null, null);
		assertEquals(null, item.getSomeDataURL());
		assertEquals(null, item.getSomeDataBody());
		assertEquals(null, item.getSomeDataContentType());
	}


}
