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

import static com.exedio.cope.RuntimeAssert.assertData;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.testmodel.AttributeItem.TYPE;
import static com.exedio.cope.testmodel.AttributeItem.someData;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.testmodel.AttributeItem;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

public class FieldMediaTest extends FieldTest
{
	@Test void testSomeData() throws IOException
	{
		assertEquals(TYPE, someData.getType());
		assertEquals("someData", someData.getName());
		assertEquals(true, someData.checkContentType("what/ever"));
		assertEquals("*/*", someData.getContentTypeDescription());
		assertEquals(null, someData.getContentTypesAllowed());

		// TODO: test with not null data
		assertEquals(TYPE, someData.getType());
		assertEquals(null, item.getSomeDataLocator());
		assertEquals(null, item.getSomeDataBody());
		assertEquals(null, item.getSomeDataContentType());

		final byte[] bytes = {3,7,1,4};
		item.setSomeData(stream(bytes), "some-mime-major/some-mime-minor");
		assertStreamClosed();

		final String prefix =
			"AttributeItem/someData/";
		final String expectedURL =
			prefix + item.getCopeID();
		assertLocator(expectedURL, item.getSomeDataLocator());
		assertData(bytes, item.getSomeDataBody());
		assertEquals("some-mime-major/some-mime-minor", item.getSomeDataContentType());

		restartTransaction();
		assertLocator(expectedURL, item.getSomeDataLocator());
		assertData(bytes, item.getSomeDataBody());
		assertEquals("some-mime-major/some-mime-minor", item.getSomeDataContentType());

		assertDataMime(item, "image/jpeg", bytes, "jpg");
		assertDataMime(item, "image/pjpeg", bytes, "jpg");
		assertDataMime(item, "image/gif", bytes, "gif");
		assertDataMime(item, "image/png", bytes, "png");
		assertDataMime(item, "image/some-minor", bytes, null);

		final byte[] manyBytes = new byte[49467];
		for(int i = 0; i<manyBytes.length; i++)
		{
			manyBytes[i] = (byte)((121*i)%253);
			//System.out.print(manyBytes[i]+", ");
		}
		item.setSomeData(stream(manyBytes),"some-mime-major/some-mime-minor");
		assertStreamClosed();
		assertData(manyBytes, item.getSomeDataBody());

		item.setSomeData((InputStream)null, null);
		assertEquals(null, item.getSomeDataLocator());
		assertEquals(null, item.getSomeDataBody());
		assertEquals(null, item.getSomeDataContentType());
	}

	private static void assertDataMime(final AttributeItem item,
											final String contentType,
											final byte[] data,
											final String url)
	{
		try
		{
			item.setSomeData(new ByteArrayInputStream(data), contentType);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		final String prefix = "AttributeItem/someData/";
		final String expectedURL = prefix + item.getCopeID() + (url!=null ? ('.' + url) : "");
		assertLocator(expectedURL, item.getSomeDataLocator());
		assertData(data, item.getSomeDataBody());
		assertEquals(contentType, item.getSomeDataContentType());
	}
}
