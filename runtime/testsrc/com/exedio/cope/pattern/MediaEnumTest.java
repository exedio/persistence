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
import static com.exedio.cope.pattern.MediaItem.sheet;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.TestWithEnvironment;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MediaEnumTest extends TestWithEnvironment
{
	public MediaEnumTest()
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

		assertEquals(false, sheet.isInitial());
		assertEquals(false, sheet.isFinal());
		assertEquals(false, sheet.isMandatory());
		assertEquals(Media.Value.class, sheet.getInitialType());
		assertContains(sheet.getInitialExceptions());
		assertEquals(true, sheet.checkContentType("image/png"));
		assertEquals(false, sheet.checkContentType("image/jpg"));
		assertEquals(true, sheet.checkContentType("application/pdf"));
		assertEquals(false, sheet.checkContentType("application/msword"));
		assertEquals(15, sheet.getContentTypeMaximumLength());
		assertEquals("application/pdf,image/png", sheet.getContentTypeDescription());
		assertEqualsUnmodifiable(list("application/pdf", "image/png"), sheet.getContentTypesAllowed());
		assertEquals(5000, sheet.getMaximumLength());

		final DataField body = sheet.getBody();
		assertSame(TYPE, body.getType());
		assertSame("sheet-body", body.getName());
		assertEquals(false, body.isFinal());
		assertEquals(false, body.isMandatory());
		assertEquals(5000, body.getMaximumLength());
		assertEquals(sheet, body.getPattern());
		assertSame(sheet, Media.get(body));

		final IntegerField contentType = (IntegerField)sheet.getContentType();
		assertSame(TYPE, contentType.getType());
		assertEquals("sheet-contentType", contentType.getName());
		assertEquals(sheet, contentType.getPattern());
		assertEquals(false, contentType.isFinal());
		assertEquals(false, contentType.isMandatory());
		assertEquals(null, contentType.getImplicitUniqueConstraint());

		final DateField lastModified = sheet.getLastModified();
		assertSame(TYPE, lastModified.getType());
		assertEquals("sheet-lastModified", lastModified.getName());
		assertEquals(sheet, lastModified.getPattern());
		assertEquals(false, lastModified.isFinal());
		assertEquals(false, lastModified.isMandatory());
		assertEquals(null, lastModified.getImplicitUniqueConstraint());

		final CheckConstraint unison = sheet.getUnison();
		assertSame(TYPE, unison.getType());
		assertEquals("sheet-unison", unison.getName());
		assertEquals(sheet, unison.getPattern());
		assertEquals(Cope.or(
				contentType.isNull   ().and(lastModified.isNull   ()),
				contentType.isNotNull().and(lastModified.isNotNull())),
				unison.getCondition());

		assertEquals(contentType.equal(0),  sheet.contentTypeEqual("application/pdf"));
		assertEquals(contentType.equal(1),  sheet.contentTypeEqual("image/png"));
		assertEquals(Condition.FALSE,       sheet.contentTypeEqual("major/minor"));
		assertEquals(lastModified.isNull(), sheet.contentTypeEqual(null));

		// test persistence

		assertContentNull();

		item.setSheet(stream(bytes4), "application/pdf");
		assertStreamClosed();
		assertContent(bytes4, "application/pdf", 0, ".pdf");

		item.setSheet(stream(bytes6), "image/png");
		assertStreamClosed();
		assertContent(bytes6, "image/png", 1, ".png");

		try
		{
			item.setSheet(stream(bytes4), "illegalContentType");
			fail();
		}
		catch(final IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(sheet, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("illegalContentType", e.getContentType());
			assertEquals("illegal content type 'illegalContentType' on " + item + " for MediaItem.sheet, allowed is 'application/pdf,image/png' only.", e.getMessage());
			assertContent(bytes6, "image/png", 1, ".png");
		}

		try
		{
			item.setSheet(stream(bytes4), "image/jpeg");
			fail();
		}
		catch(final IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(sheet, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("image/jpeg", e.getContentType());
			assertEquals("illegal content type 'image/jpeg' on " + item + " for MediaItem.sheet, allowed is 'application/pdf,image/png' only.", e.getMessage());
			assertContent(bytes6, "image/png", 1, ".png");
		}

		item.setSheet((InputStream)null, null);
		assertContentNull();
	}

	private void assertContentNull()
	{
		assertTrue(item.isSheetNull());
		assertEquals(null, item.getSheetBody());
		assertEquals(-1, item.getSheetLength());
		assertEquals(null, item.getSheetContentType());
		assertEquals(null, sheet.getContentType().get(item));
		assertLocator(null, item.getSheetLocator());
	}

	private void assertContent(
			final byte[] expectedData,
			final String expectedContentType,
			final int expectedContentTypeNumber,
			final String expectedExtension)
	{
		assertTrue(!item.isSheetNull());
		assertData(expectedData, item.getSheetBody());
		assertEquals(expectedData.length, item.getSheetLength());
		assertEquals(expectedContentType, item.getSheetContentType());
		assertEquals(expectedContentTypeNumber, sheet.getContentType().get(item));
		assertLocator("MediaItem/sheet/" + item.getCopeID() + expectedExtension, item.getSheetLocator());
	}

	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
}
