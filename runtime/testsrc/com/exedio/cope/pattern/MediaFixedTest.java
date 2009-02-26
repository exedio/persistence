/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.io.IOException;
import java.io.InputStream;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;

public class MediaFixedTest extends AbstractRuntimeTest
{
	public MediaFixedTest()
	{
		super(MediaTest.MODEL);
	}

	protected MediaItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MediaItem("test media item"));
	}
	
	public void testIt() throws IOException
	{
		assertEquals(false, item.photo.isInitial());
		assertEquals(false, item.photo.isFinal());
		assertEquals(false, item.photo.isMandatory());
		assertEquals(Media.Value.class, item.photo.getInitialType());
		assertContains(item.photo.getInitialExceptions());
		assertEquals(true, item.photo.checkContentType("image/jpeg"));
		assertEquals(false, item.photo.checkContentType("imaxge/jpeg"));
		assertEquals(false, item.photo.checkContentType("image/jpxeg"));
		assertEquals("image/jpeg", item.photo.getContentTypeDescription());
		assertEqualsUnmodifiable(list("image/jpeg"), item.photo.getContentTypesAllowed());
		assertEquals(2000, item.photo.getMaximumLength());

		final DataField body = item.photo.getBody();
		assertSame(item.TYPE, body.getType());
		assertSame("photoBody", body.getName());
		assertEquals(false, body.isFinal());
		assertEquals(false, body.isMandatory());
		assertEquals(2000, body.getMaximumLength());
		assertEquals(item.photo, body.getPattern());
		assertSame(item.photo, Media.get(body));

		assertEquals(null, item.photo.getContentType());

		final DateField lastModified = item.photo.getLastModified();
		assertSame(item.TYPE, lastModified.getType());
		assertEquals("photoLastModified", lastModified.getName());
		assertEquals(item.photo, lastModified.getPattern());
		assertEquals(false, lastModified.isFinal());
		assertEquals(false, lastModified.isMandatory());
		assertEquals(null, lastModified.getImplicitUniqueConstraint());

		assertNull();

		item.setPhoto(stream(data4), "image/jpeg");
		assertStreamClosed();
		assertContent(data4);

		item.setPhoto(stream(data6), "image/jpeg");
		assertStreamClosed();
		assertContent(data6);
		
		try
		{
			item.setPhoto(stream(data4), "illegalContentType");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(item.photo, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("illegalContentType", e.getContentType());
			assertEquals("illegal content type 'illegalContentType' on " + item + " for MediaItem.photo, allowed is 'image/jpeg\' only.", e.getMessage());
			assertContent(data6);
		}

		try
		{
			item.setPhoto(stream(data4), "image/png");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(item.photo, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("image/png", e.getContentType());
			assertEquals("illegal content type 'image/png' on " + item + " for MediaItem.photo, allowed is 'image/jpeg\' only.", e.getMessage());
			assertContent(data6);
		}

		item.setPhoto((InputStream)null, null);
		assertNull();
	}

	private void assertNull()
	{
		assertTrue(item.photo.isNull(item));
		assertTrue(item.isPhotoNull());
		assertEquals(null, item.getPhotoBody());
		assertEquals(-1, item.getPhotoLength());
		assertEquals(null, item.getPhotoContentType());
		assertEquals(null, item.getPhotoURL());
	}
	
	private void assertContent(final byte[] expectedData)
	{
		assertTrue(!item.isPhotoNull());
		assertData(expectedData, item.getPhotoBody());
		assertEquals(expectedData.length, item.getPhotoLength());
		assertEquals("image/jpeg", item.getPhotoContentType());
		assertEquals(mediaRootUrl + "MediaItem/photo/" + item.getCopeID() + ".jpg", item.getPhotoURL());
	}
}
