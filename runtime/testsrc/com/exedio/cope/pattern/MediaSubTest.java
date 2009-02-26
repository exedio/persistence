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
import com.exedio.cope.StringField;

public class MediaSubTest extends AbstractRuntimeTest
{
	public MediaSubTest()
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
		assertEquals(false, item.image.isInitial());
		assertEquals(false, item.image.isFinal());
		assertEquals(false, item.image.isMandatory());
		assertEquals(Media.Value.class, item.image.getInitialType());
		assertContains(item.image.getInitialExceptions());
		assertEquals(true, item.image.checkContentType("image/png"));
		assertEquals(true, item.image.checkContentType("image/jpg"));
		assertEquals(false, item.image.checkContentType("application/jpg"));
		assertEquals("image/*", item.image.getContentTypeDescription());
		assertEquals(null, item.image.getContentTypesAllowed());
		assertEquals(Media.DEFAULT_LENGTH, item.image.getMaximumLength());

		final DataField body = item.image.getBody();
		assertSame(item.TYPE, body.getType());
		assertSame("imageBody", body.getName());
		assertEquals(false, body.isFinal());
		assertEquals(false, body.isMandatory());
		assertEquals(Media.DEFAULT_LENGTH, body.getMaximumLength());
		assertEquals(item.image, body.getPattern());
		assertSame(item.image, Media.get(body));
		
		final StringField contentType = (StringField)item.image.getContentType();
		assertSame(item.TYPE, contentType.getType());
		assertEquals("imageMinor", contentType.getName());
		assertEquals(item.image, contentType.getPattern());
		assertEquals(false, contentType.isFinal());
		assertEquals(false, contentType.isMandatory());
		assertEquals(null, contentType.getImplicitUniqueConstraint());
		assertEquals(1, contentType.getMinimumLength());
		assertEquals(30, contentType.getMaximumLength());
		
		final DateField lastModified = item.image.getLastModified();
		assertSame(item.TYPE, lastModified.getType());
		assertEquals("imageLastModified", lastModified.getName());
		assertEquals(item.image, lastModified.getPattern());
		assertEquals(false, lastModified.isFinal());
		assertEquals(false, lastModified.isMandatory());
		assertEquals(null, lastModified.getImplicitUniqueConstraint());

		assertNull();

		item.setImage(stream(data4), "image/image-minor");
		assertStreamClosed();
		assertContent(data4, "image/image-minor", "");

		item.setImage(stream(data6), "image/jpeg");
		assertStreamClosed();
		assertContent(data6, "image/jpeg", ".jpg");

		try
		{
			item.setImage(stream(data4), "illegalContentType");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(item.image, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("illegalContentType", e.getContentType());
			assertEquals("illegal content type 'illegalContentType' on " + item + " for MediaItem.image, allowed is 'image/*\' only.", e.getMessage());
			assertContent(data6, "image/jpeg", ".jpg");
		}

		try
		{
			item.setImage(stream(data4), "text/html");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(item.image, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("text/html", e.getContentType());
			assertEquals("illegal content type 'text/html' on " + item + " for MediaItem.image, allowed is 'image/*\' only.", e.getMessage());
			assertContent(data6, "image/jpeg", ".jpg");
		}

		item.setImage((InputStream)null, null);
		assertNull();
	}
	
	private void assertNull()
	{
		assertTrue(item.isImageNull());
		assertEquals(null, item.getImageBody());
		assertEquals(-1, item.getImageLength());
		assertEquals(null, item.getImageContentType());
		assertEquals(null, item.getImageURL());
	}
	
	private void assertContent(
			final byte[] expectedData,
			final String expectedContentType, final String expectedExtension)
	{
		assertTrue(!item.isImageNull());
		assertData(expectedData, item.getImageBody());
		assertEquals(expectedData.length, item.getImageLength());
		assertEquals(expectedContentType, item.getImageContentType());
		assertEquals(mediaRootUrl + "MediaItem/image/" + item.getCopeID() + expectedExtension, item.getImageURL());
	}
}
