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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.DataField;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.DateField;
import com.exedio.cope.StringField;

public class MediaDefaultTest extends AbstractLibTest
{
	public MediaDefaultTest()
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
		assertEquals(false, item.file.isInitial());
		assertEquals(false, item.file.isFinal());
		assertEquals(Media.Value.class, item.file.getWrapperSetterType());
		assertContains(item.file.getSetterExceptions());
		assertEquals(true, item.file.checkContentType("some/thing"));
		assertEquals(false, item.file.checkContentType("something"));
		assertEquals("*/*", item.file.getContentTypeDescription());
		assertEquals(20, item.file.getMaximumLength());

		final DataField body = item.file.getBody();
		assertSame(item.TYPE, body.getType());
		assertSame("fileBody", body.getName());
		assertEquals(false, body.isFinal());
		assertEquals(false, body.isMandatory());
		assertEquals(20, body.getMaximumLength());
		assertEqualsUnmodifiable(list(item.file), body.getPatterns());
		assertSame(item.file, Media.get(body));
		
		final StringField contentType = (StringField)item.file.getContentType();
		assertSame(item.TYPE, contentType.getType());
		assertEquals("fileContentType", contentType.getName());
		assertEqualsUnmodifiable(list(item.file), contentType.getPatterns());
		assertEquals(false, contentType.isFinal());
		assertEquals(false, contentType.isMandatory());
		assertEquals(null, contentType.getImplicitUniqueConstraint());
		assertEquals(1, contentType.getMinimumLength());
		assertEquals(61, contentType.getMaximumLength());
		
		final DateField lastModified = item.file.getLastModified();
		assertSame(item.TYPE, lastModified.getType());
		assertEquals("fileLastModified", lastModified.getName());
		assertEqualsUnmodifiable(list(item.file), lastModified.getPatterns());
		assertEquals(false, lastModified.isFinal());
		assertEquals(false, lastModified.isMandatory());
		assertEquals(null, lastModified.getImplicitUniqueConstraint());
		assertEquals(lastModified.isNull(), item.file.isNull());
		assertEquals(lastModified.isNotNull(), item.file.isNotNull());
		
		assertNull();
		assertContains(item, item.TYPE.search(item.file.isNull()));
		assertContains(item.TYPE.search(item.file.isNotNull()));
		{
			final Date before = new Date();
			item.setFile(stream(data4), "fileMajor/fileMinor");
			final Date after = new Date();
			assertStreamClosed();
			assertContent(data4, before, after, "fileMajor/fileMinor", "");
		}
		assertContains(item.TYPE.search(item.file.isNull()));
		assertContains(item, item.TYPE.search(item.file.isNotNull()));
		{
			final Date before = new Date();
			item.setFile(stream(data6), "fileMajor2/fileMinor2");
			final Date after = new Date();
			assertStreamClosed();
			assertContent(data6, before, after, "fileMajor2/fileMinor2", "");

			try
			{
				item.setFile(stream(data4), "illegalContentType");
				fail();
			}
			catch(IllegalContentTypeException e)
			{
				assertStreamClosed();
				assertSame(item.file, e.getFeature());
				assertEquals(item, e.getItem());
				assertEquals("illegalContentType", e.getContentType());
				assertEquals("illegal content type 'illegalContentType' on " + item + " for MediaItem.file, allowed is '*/*\' only.", e.getMessage());
				assertContent(data6, before, after, "fileMajor2/fileMinor2", "");
			}
		}
		assertExtension("image/jpeg", ".jpg");
		assertExtension("image/pjpeg", ".jpg");
		assertExtension("image/png", ".png");
		assertExtension("image/gif", ".gif");
		assertExtension("text/html", ".html");
		assertExtension("text/plain", ".txt");
		assertExtension("text/css", ".css");
		assertExtension("application/java-archive", ".jar");
		if(!oracle)
		{
			final Date before = new Date();
			item.setFile(stream(data0), "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertStreamClosed();
			assertContent(data0, before, after, "emptyMajor/emptyMinor", "");
		}
		item.setFile((InputStream)null, null);
		assertNull();
		{
			final Date before = new Date();
			item.setFile(file(data8), "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertContent(data8, before, after, "emptyMajor/emptyMinor", "");
		}
		item.setFile((File)null, null);
		assertNull();
		{
			final Date before = new Date();
			item.setFile(data8, "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertContent(data8, before, after, "emptyMajor/emptyMinor", "");
		}
		item.setFile((byte[])null, null);
		assertNull();
		{
			final Date before = new Date();
			item.setFile(data20, "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertContent(data20, before, after, "emptyMajor/emptyMinor", "");
			try
			{
				item.setFile(data21, "emptyMajorLong/emptyMinorLong");
				fail();
			}
			catch(DataLengthViolationException e)
			{
				assertSame(body, e.getFeature());
				assertSame(body, e.getFeature());
				assertSame(item, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(true, e.isLengthExact());
				assertEquals("length violation on " + item + ", 21 bytes is too long for " + body, e.getMessage());
			}
			assertContent(data20, before, after, "emptyMajor/emptyMinor", "");
			try
			{
				item.setFile(file(data21), "emptyMajorLong/emptyMinorLong");
				fail();
			}
			catch(DataLengthViolationException e)
			{
				assertSame(body, e.getFeature());
				assertSame(body, e.getFeature());
				assertSame(item, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(true, e.isLengthExact());
				assertEquals("length violation on " + item + ", 21 bytes is too long for " + body, e.getMessage());
			}
			assertContent(data20, before, after, "emptyMajor/emptyMinor", "");
			try
			{
				item.setFile(stream(data21), "emptyMajorLong/emptyMinorLong");
				fail();
			}
			catch(DataLengthViolationException e)
			{
				assertSame(body, e.getFeature());
				assertSame(body, e.getFeature());
				assertSame(item, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(false, e.isLengthExact());
				assertEquals("length violation on " + item + ", 21 bytes or more is too long for " + body, e.getMessage());
			}
			assertStreamClosed();
			//assertContent(data20, before, after, "emptyMajorLong/emptyMinorLong", ".emptyMajorLong.emptyMinorLong"); TODO
		}
		item.setFile((byte[])null, null);
		assertNull();
	}

	private void assertNull() throws IOException
	{
		assertTrue(item.isFileNull());
		assertEquals(null, item.getFileBody());
		assertDataFile(null);
		assertEquals(-1, item.getFileLength());
		assertEquals(-1, item.getFileLastModified());
		assertEquals(null, item.getFileContentType());
		assertEquals(null, item.getFileURL());
	}
	
	private void assertContent(
			final byte[] expectedData,
			final Date before, final Date after,
			final String expectedContentType, final String expectedExtension)
	throws IOException
	{
		assertTrue(!item.isFileNull());
		assertData(expectedData, item.getFileBody());
		assertDataFile(expectedData);
		assertEquals(expectedData.length, item.getFileLength());
		assertWithin(before, after, new Date(item.getFileLastModified()));
		assertEquals(expectedContentType, item.getFileContentType());
		assertEquals(mediaRootUrl + "MediaItem/file/" + item.getCopeID() + expectedExtension, item.getFileURL());
	}
	
	private final void assertDataFile(final byte[] expectedData) throws IOException
	{
		final File tempFile = File.createTempFile("exedio-cope-MediaTest-", ".tmp");
		assertTrue(tempFile.delete());
		assertFalse(tempFile.exists());
		
		item.getFileBody(tempFile);
		assertEqualContent(expectedData, tempFile);
	}
	
	private void assertExtension(final String contentType, final String extension)
		throws IOException
	{
		final Date before = new Date();
		item.setFile(stream(data6), contentType);
		final Date after = new Date();
		assertStreamClosed();
		assertContent(data6, before, after, contentType, extension);
	}
}
