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

package com.exedio.cope.pattern;

import static com.exedio.cope.RuntimeAssert.assertData;
import static com.exedio.cope.pattern.MediaItem.TYPE;
import static com.exedio.cope.pattern.MediaItem.photo;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Condition;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.TestWithEnvironment;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MediaFixedTest extends TestWithEnvironment
{
	public MediaFixedTest()
	{
		super(MediaTest.MODEL);
	}

	protected MediaItem item;

	@BeforeEach final void setUp()
	{
		item = new MediaItem("test media item");
	}

	@Test void testIt() throws IOException
	{
		// test model

		assertEquals(false, photo.isInitial());
		assertEquals(false, photo.isFinal());
		assertEquals(false, photo.isMandatory());
		assertEquals(Media.Value.class, photo.getInitialType());
		assertContains(photo.getInitialExceptions());
		assertEquals(true, photo.checkContentType("image/jpeg"));
		assertEquals(false, photo.checkContentType("imaxge/jpeg"));
		assertEquals(false, photo.checkContentType("image/jpxeg"));
		assertEquals(10, photo.getContentTypeMaximumLength());
		assertEquals("image/jpeg", photo.getContentTypeDescription());
		assertEqualsUnmodifiable(list("image/jpeg"), photo.getContentTypesAllowed());
		assertEquals(2000, photo.getMaximumLength());

		final DataField body = photo.getBody();
		assertSame(TYPE, body.getType());
		assertSame("photo-body", body.getName());
		assertEquals(false, body.isFinal());
		assertEquals(false, body.isMandatory());
		assertEquals(2000, body.getMaximumLength());
		assertEquals(photo, body.getPattern());
		assertSame(photo, Media.get(body));

		assertEquals(null, photo.getContentType());

		final DateField lastModified = photo.getLastModified();
		assertSame(TYPE, lastModified.getType());
		assertEquals("photo-lastModified", lastModified.getName());
		assertEquals(photo, lastModified.getPattern());
		assertEquals(false, lastModified.isFinal());
		assertEquals(false, lastModified.isMandatory());
		assertEquals(null, lastModified.getImplicitUniqueConstraint());

		assertNull(photo.getUnison());

		assertEquals(Condition.TRUE,        photo.contentTypeEqual("image/jpeg"));
		assertEquals(Condition.FALSE,       photo.contentTypeEqual("major/minor"));
		assertEquals(lastModified.isNull(), photo.contentTypeEqual(null));

		// test persistence

		assertContentNull();

		item.setPhoto(stream(bytes4), "image/jpeg");
		assertStreamClosed();
		assertContent(bytes4);

		item.setPhoto(stream(bytes6), "image/jpeg");
		assertStreamClosed();
		assertContent(bytes6);

		try
		{
			item.setPhoto(stream(bytes4), "illegalContentType");
			fail();
		}
		catch(final IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(photo, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("illegalContentType", e.getContentType());
			assertEquals("illegal content type 'illegalContentType' on " + item + " for MediaItem.photo, allowed is 'image/jpeg' only.", e.getMessage());
			assertContent(bytes6);
		}

		try
		{
			item.setPhoto(stream(bytes4), "image/png");
			fail();
		}
		catch(final IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(photo, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("image/png", e.getContentType());
			assertEquals("illegal content type 'image/png' on " + item + " for MediaItem.photo, allowed is 'image/jpeg' only.", e.getMessage());
			assertContent(bytes6);
		}

		item.setPhoto((InputStream)null, null);
		assertContentNull();
	}

	private void assertContentNull()
	{
		assertTrue(photo.isNull(item));
		assertTrue(item.isPhotoNull());
		assertEquals(null, item.getPhotoBody());
		assertEquals(-1, item.getPhotoLength());
		assertEquals(null, item.getPhotoContentType());
		assertLocator(null, item.getPhotoLocator());
	}

	private void assertContent(final byte[] expectedData)
	{
		assertTrue(!item.isPhotoNull());
		assertData(expectedData, item.getPhotoBody());
		assertEquals(expectedData.length, item.getPhotoLength());
		assertEquals("image/jpeg", item.getPhotoContentType());
		assertLocator("MediaItem/photo/" + item.getCopeID() + ".jpg", item.getPhotoLocator());
	}

	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
}
