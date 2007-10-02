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

package com.exedio.cope.pattern;

import java.io.IOException;
import java.io.InputStream;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;

public class MediaEnumTest extends AbstractLibTest
{
	public MediaEnumTest()
	{
		super(MediaTest.MODEL);
	}

	protected MediaItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new MediaItem("test media item"));
	}
	
	public void testIt() throws IOException
	{
		assertEquals(false, item.sheet.isInitial());
		assertEquals(false, item.sheet.isFinal());
		assertEquals(Media.Value.class, item.sheet.getWrapperSetterType());
		assertContains(item.sheet.getSetterExceptions());
		assertEquals(true, item.sheet.checkContentType("image/png"));
		assertEquals(false, item.sheet.checkContentType("image/jpg"));
		assertEquals(true, item.sheet.checkContentType("application/pdf"));
		assertEquals(false, item.sheet.checkContentType("application/msword"));
		assertEquals("application/pdf,image/png", item.sheet.getContentTypeDescription());
		assertEquals(5000, item.sheet.getMaximumLength());

		final DataField body = item.sheet.getBody();
		assertSame(item.TYPE, body.getType());
		assertSame("sheetBody", body.getName());
		assertEquals(false, body.isFinal());
		assertEquals(false, body.isMandatory());
		assertEquals(5000, body.getMaximumLength());
		assertEqualsUnmodifiable(list(item.sheet), body.getPatterns());
		assertSame(item.sheet, Media.get(body));
		
		final IntegerField contentType = (IntegerField)item.sheet.getContentType();
		assertSame(item.TYPE, contentType.getType());
		assertEquals("sheetContentType", contentType.getName());
		assertEqualsUnmodifiable(list(item.sheet), contentType.getPatterns());
		assertEquals(false, contentType.isFinal());
		assertEquals(false, contentType.isMandatory());
		assertEquals(null, contentType.getImplicitUniqueConstraint());
		
		final DateField lastModified = item.sheet.getLastModified();
		assertSame(item.TYPE, lastModified.getType());
		assertEquals("sheetLastModified", lastModified.getName());
		assertEqualsUnmodifiable(list(item.sheet), lastModified.getPatterns());
		assertEquals(false, lastModified.isFinal());
		assertEquals(false, lastModified.isMandatory());
		assertEquals(null, lastModified.getImplicitUniqueConstraint());

		assertNull();

		item.setSheet(stream(data4), "application/pdf");
		assertStreamClosed();
		assertContent(data4, "application/pdf", 0, "");

		item.setSheet(stream(data6), "image/png");
		assertStreamClosed();
		assertContent(data6, "image/png", 1, ".png");

		try
		{
			item.setSheet(stream(data4), "illegalContentType");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(item.sheet, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("illegalContentType", e.getContentType());
			assertEquals("illegal content type 'illegalContentType' on " + item + " for MediaItem.sheet, allowed is 'application/pdf,image/png' only.", e.getMessage());
			assertContent(data6, "image/png", 1, ".png");
		}

		try
		{
			item.setSheet(stream(data4), "image/jpeg");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertSame(item.sheet, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("image/jpeg", e.getContentType());
			assertEquals("illegal content type 'image/jpeg' on " + item + " for MediaItem.sheet, allowed is 'application/pdf,image/png' only.", e.getMessage());
			assertContent(data6, "image/png", 1, ".png");
		}

		item.setSheet((InputStream)null, null);
		assertNull();
	}
	
	private void assertNull()
	{
		assertTrue(item.isSheetNull());
		assertEquals(null, item.getSheetBody());
		assertEquals(-1, item.getSheetLength());
		assertEquals(null, item.getSheetContentType());
		assertEquals(null, item.sheet.getContentType().get(item));
		assertEquals(null, item.getSheetURL());
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
		assertEquals(expectedContentTypeNumber, item.sheet.getContentType().get(item));
		assertEquals(mediaRootUrl + "MediaItem/sheet/" + item.getCopeID() + expectedExtension, item.getSheetURL());
	}
}
