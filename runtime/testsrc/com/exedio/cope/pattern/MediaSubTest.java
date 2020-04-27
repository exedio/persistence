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
import static com.exedio.cope.pattern.MediaItem.image;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MediaSubTest extends TestWithEnvironment
{
	public MediaSubTest()
	{
		super(MediaTest.MODEL);
	}

	protected MediaItem item;

	@BeforeEach final void setUp()
	{
		item = new MediaItem("test media item");
	}

	@SuppressFBWarnings("ES_COMPARING_STRINGS_WITH_EQ")
	@Test void testIt() throws IOException
	{
		// test model

		assertEquals(false, image.isInitial());
		assertEquals(false, image.isFinal());
		assertEquals(false, image.isMandatory());
		assertEquals(Media.Value.class, image.getInitialType());
		assertContains(image.getInitialExceptions());
		assertEquals(true, image.checkContentType("image/png"));
		assertEquals(true, image.checkContentType("image/jpg"));
		assertEquals(true, image.checkContentType("image/svg+xml"));
		assertEquals(false, image.checkContentType("application/jpg"));
		assertEquals(36, image.getContentTypeMaximumLength());
		assertEquals("image/*", image.getContentTypeDescription());
		assertEquals(null, image.getContentTypesAllowed());
		assertEquals(Media.DEFAULT_LENGTH, image.getMaximumLength());

		final DataField body = image.getBody();
		assertSame(TYPE, body.getType());
		assertSame("image-body", body.getName());
		assertEquals(false, body.isFinal());
		assertEquals(false, body.isMandatory());
		assertEquals(Media.DEFAULT_LENGTH, body.getMaximumLength());
		assertEquals(image, body.getPattern());
		assertSame(image, Media.get(body));

		final StringField contentType = (StringField)image.getContentType();
		assertSame(TYPE, contentType.getType());
		assertEquals("image-minor", contentType.getName());
		assertEquals(image, contentType.getPattern());
		assertEquals(false, contentType.isFinal());
		assertEquals(false, contentType.isMandatory());
		assertEquals(null, contentType.getImplicitUniqueConstraint());
		assertEquals(1, contentType.getMinimumLength());
		assertEquals(30, contentType.getMaximumLength());

		final DateField lastModified = image.getLastModified();
		assertSame(TYPE, lastModified.getType());
		assertEquals("image-lastModified", lastModified.getName());
		assertEquals(image, lastModified.getPattern());
		assertEquals(false, lastModified.isFinal());
		assertEquals(false, lastModified.isMandatory());
		assertEquals(null, lastModified.getImplicitUniqueConstraint());

		final CheckConstraint unison = image.getUnison();
		assertSame(TYPE, unison.getType());
		assertEquals("image-unison", unison.getName());
		assertEquals(image, unison.getPattern());
		assertEquals(Cope.or(
				contentType.isNull   ().and(lastModified.isNull   ()),
				contentType.isNotNull().and(lastModified.isNotNull())),
				unison.getCondition());

		assertEquals(contentType.equal("png"),  image.contentTypeEqual("image/png"));
		assertEquals(contentType.equal("jpeg"), image.contentTypeEqual("image/jpeg"));
		assertEquals(contentType.equal("svg+xml"),  image.contentTypeEqual("image/svg+xml"));
		assertEquals(Condition.FALSE,           image.contentTypeEqual("image"));
		assertEquals(Condition.FALSE,           image.contentTypeEqual("major/minor"));
		assertEquals(lastModified.isNull(),     image.contentTypeEqual(null));

		// test persistence

		assertContentNull();

		item.setImage(stream(bytes4), "image/image-minor");
		assertStreamClosed();
		assertContent(bytes4, "image/image-minor", "");

		item.setImage(stream(bytes8), "image/svg+xml");
		assertStreamClosed();
		assertContent(bytes8, "image/svg+xml", ".svg");

		item.setImage(stream(bytes6), "image/jpeg");
		assertStreamClosed();
		assertContent(bytes6, "image/jpeg", ".jpg");

		try
		{
			item.setImage(stream(bytes4), "illegalContentType");
			fail();
		}
		catch(final IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(image, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("illegalContentType", e.getContentType());
			assertEquals("illegal content type 'illegalContentType' on " + item + " for MediaItem.image, allowed is 'image/*' only.", e.getMessage());
			assertContent(bytes6, "image/jpeg", ".jpg");
		}

		try
		{
			item.setImage(stream(bytes4), "text/html");
			fail();
		}
		catch(final IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(image, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("text/html", e.getContentType());
			assertEquals("illegal content type 'text/html' on " + item + " for MediaItem.image, allowed is 'image/*' only.", e.getMessage());
			assertContent(bytes6, "image/jpeg", ".jpg");
		}

		item.setImage((InputStream)null, null);
		assertContentNull();
	}

	private void assertContentNull()
	{
		assertTrue(item.isImageNull());
		assertEquals(null, item.getImageBody());
		assertEquals(-1, item.getImageLength());
		assertEquals(null, item.getImageContentType());
		assertLocator(null, item.getImageLocator());
	}

	private void assertContent(
			final byte[] expectedData,
			final String expectedContentType, final String expectedExtension)
	{
		assertTrue(!item.isImageNull());
		assertData(expectedData, item.getImageBody());
		assertEquals(expectedData.length, item.getImageLength());
		assertEquals(expectedContentType, item.getImageContentType());
		assertLocator("MediaItem/image/" + item.getCopeID() + expectedExtension, item.getImageLocator());
	}

	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
	private static final byte[] bytes8  = {-54,104,-63,23,19,-45,71,-23};
}
