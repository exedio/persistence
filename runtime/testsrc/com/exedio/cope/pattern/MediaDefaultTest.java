/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.pattern.MediaItem.file;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.util.StrictFile.delete;
import static java.io.File.createTempFile;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.DateField;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.StringField;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class MediaDefaultTest extends AbstractRuntimeTest
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
		{
			final DataField.Value dataValue = DataField.toValue(bytes8);
			assertSame(dataValue, Media.toValue(dataValue, "zack").getBody());
			assertSame("zack",    Media.toValue(dataValue, "zack").getContentType());
		}

		// test model

		assertEquals(false, file.isInitial());
		assertEquals(false, file.isFinal());
		assertEquals(false, file.isMandatory());
		assertEquals(Media.Value.class, file.getInitialType());
		assertContains(file.getInitialExceptions());
		assertEquals(true, file.checkContentType("some/thing"));
		assertEquals(false, file.checkContentType("something"));
		assertEquals("*/*", file.getContentTypeDescription());
		assertEquals(null, file.getContentTypesAllowed());
		assertEquals(20, file.getMaximumLength());

		final DataField body = file.getBody();
		assertSame(TYPE, body.getType());
		assertSame("file-body", body.getName());
		assertEquals(false, body.isFinal());
		assertEquals(false, body.isMandatory());
		assertEquals(20, body.getMaximumLength());
		assertEquals(file, body.getPattern());
		assertSame(file, Media.get(body));

		final StringField contentType = (StringField)file.getContentType();
		assertSame(TYPE, contentType.getType());
		assertEquals("file-contentType", contentType.getName());
		assertEquals(file, contentType.getPattern());
		assertEquals(false, contentType.isFinal());
		assertEquals(false, contentType.isMandatory());
		assertEquals(null, contentType.getImplicitUniqueConstraint());
		assertEquals(1, contentType.getMinimumLength());
		assertEquals(61, contentType.getMaximumLength());

		final DateField lastModified = file.getLastModified();
		assertSame(TYPE, lastModified.getType());
		assertEquals("file-lastModified", lastModified.getName());
		assertEquals(file, lastModified.getPattern());
		assertEquals(false, lastModified.isFinal());
		assertEquals(false, lastModified.isMandatory());
		assertEquals(null, lastModified.getImplicitUniqueConstraint());
		assertEquals(lastModified.isNull(), file.isNull());
		assertEquals(lastModified.isNotNull(), file.isNotNull());
		final CheckConstraint unison = file.getUnison();
		assertSame(TYPE, unison.getType());
		assertEquals("file-unison", unison.getName());
		assertEquals(file, unison.getPattern());
		assertEquals(Cope.or(
				contentType.isNull   ().and(lastModified.isNull   ()),
				contentType.isNotNull().and(lastModified.isNotNull())),
				unison.getCondition());

		assertEqualsUnmodifiable(list(body, contentType, lastModified, unison), file.getSourceFeatures());

		assertEquals(contentType.equal("major/minor"), file.contentTypeEqual("major/minor"));
		assertEquals(lastModified.isNull(),            file.contentTypeEqual(null));

		// test persistence
		assertEquals("file_body", SchemaInfo.getColumnName(file.getBody()));
		assertEquals("file_contentType", SchemaInfo.getColumnName(file.getContentType()));
		assertEquals("file_lastModified", SchemaInfo.getColumnName(file.getLastModified()));

		assertNull();
		assertContains(item, TYPE.search(file.isNull()));
		assertContains(TYPE.search(file.isNotNull()));
		{
			final Date before = new Date();
			item.setFile(stream(bytes4), "file-major/file-minor");
			final Date after = new Date();
			assertStreamClosed();
			assertContent(bytes4, before, after, "file-major/file-minor", "");
		}
		assertContains(TYPE.search(file.isNull()));
		assertContains(item, TYPE.search(file.isNotNull()));
		{
			final Date before = new Date();
			item.setFile(stream(bytes6), "file-major2/file-minor2");
			final Date after = new Date();
			assertStreamClosed();
			assertContent(bytes6, before, after, "file-major2/file-minor2", "");

			try
			{
				item.setFile(stream(bytes4), "illegalContentType");
				fail();
			}
			catch(final IllegalContentTypeException e)
			{
				assertStreamClosed();
				assertSame(file, e.getFeature());
				assertEquals(item, e.getItem());
				assertEquals("illegalContentType", e.getContentType());
				assertEquals("illegal content type 'illegalContentType' on " + item + " for MediaItem.file, allowed is '*/*\' only.", e.getMessage());
				assertContent(bytes6, before, after, "file-major2/file-minor2", "");
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
			item.setFile(stream(bytes0), "empty-major/empty-minor");
			final Date after = new Date();
			assertStreamClosed();
			assertContent(bytes0, before, after, "empty-major/empty-minor", "");
		}
		item.setFile((InputStream)null, null);
		assertNull();
		{
			final Date before = new Date();
			item.setFile(file(bytes8), "empty-major/empty-minor");
			final Date after = new Date();
			assertContent(bytes8, before, after, "empty-major/empty-minor", "");
		}
		item.setFile((File)null, null);
		assertNull();
		{
			final Date before = new Date();
			item.setFile(bytes8, "empty-major/empty-minor");
			final Date after = new Date();
			assertContent(bytes8, before, after, "empty-major/empty-minor", "");
		}
		item.setFile((byte[])null, null);
		assertNull();
		{
			final Date before = new Date();
			item.setFile(Media.toValue(bytes8, "empty-major/empty-minor"));
			final Date after = new Date();
			assertContent(bytes8, before, after, "empty-major/empty-minor", "");
		}
		item.setFile((Media.Value)null);
		assertNull();
		{
			// test length violation
			final Date before = new Date();
			item.setFile(bytes20, "empty-major/empty-minor");
			final Date after = new Date();
			assertContent(bytes20, before, after, "empty-major/empty-minor", "");

			// byte[]
			try
			{
				item.setFile(bytes21, "empty-major-long/empty-minor-long");
				fail();
			}
			catch(final DataLengthViolationException e)
			{
				assertSame(body, e.getFeature());
				assertSame(body, e.getFeature());
				assertSame(item, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(true, e.isLengthExact());
				assertEquals("length violation on " + item + ", 21 bytes is too long for " + body, e.getMessage());
			}
			assertContent(bytes20, before, after, "empty-major/empty-minor", "");
			try
			{
				new MediaItem(Media.toValue(bytes21, "empty-major-long/empty-minor-long"));
				fail();
			}
			catch(final DataLengthViolationException e)
			{
				assertSame(body, e.getFeature());
				assertSame(body, e.getFeature());
				assertSame(null, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(true, e.isLengthExact());
				assertEquals("length violation, 21 bytes is too long for " + body, e.getMessage());
			}

			// file
			try
			{
				item.setFile(file(bytes21), "empty-major-long/empty-minor-long");
				fail();
			}
			catch(final DataLengthViolationException e)
			{
				assertSame(body, e.getFeature());
				assertSame(body, e.getFeature());
				assertSame(item, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(true, e.isLengthExact());
				assertEquals("length violation on " + item + ", 21 bytes is too long for " + body, e.getMessage());
			}
			assertContent(bytes20, before, after, "empty-major/empty-minor", "");
			try
			{
				new MediaItem(Media.toValue(file(bytes21), "empty-major-long/empty-minor-long"));
				fail();
			}
			catch(final DataLengthViolationException e)
			{
				assertSame(body, e.getFeature());
				assertSame(body, e.getFeature());
				assertSame(null, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(true, e.isLengthExact());
				assertEquals("length violation, 21 bytes is too long for " + body, e.getMessage());
			}

			// stream
			try
			{
				item.setFile(stream(bytes21), "empty-major-long/empty-minor-long");
				fail();
			}
			catch(final DataLengthViolationException e)
			{
				assertSame(body, e.getFeature());
				assertSame(body, e.getFeature());
				assertSame(item, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(false, e.isLengthExact());
				assertEquals("length violation on " + item + ", 21 bytes or more is too long for " + body, e.getMessage());
			}
			assertStreamClosed();
			//assertContent(data20, before, after, "empty-major-long/empty-minor-long", ".empty-major-long.empty-minor-long"); TODO
			try
			{
				new MediaItem(Media.toValue(stream(bytes21), "empty-major-long/empty-minor-long"));
				fail();
			}
			catch(final DataLengthViolationException e)
			{
				assertSame(body, e.getFeature());
				assertSame(body, e.getFeature());
				assertSame(null, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(false, e.isLengthExact());
				assertEquals("length violation, 21 bytes or more is too long for " + body, e.getMessage());
			}
			assertStreamClosed();
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
		assertEquals(null, item.getFileLastModified());
		assertEquals(null, item.getFileContentType());
		assertEquals(null, item.getFileLocator());
	}

	private void assertContent(
			final byte[] expectedData,
			final Date before, final Date after,
			final String expectedContentType, final String expectedExtension)
	throws IOException
	{
		final String path = "MediaItem/file/" + item.getCopeID() + expectedExtension;
		assertTrue(!item.isFileNull());
		assertData(expectedData, item.getFileBody());
		assertDataFile(expectedData);
		assertEquals(expectedData.length, item.getFileLength());
		assertWithin(before, after, item.getFileLastModified());
		assertEquals(expectedContentType, item.getFileContentType());
		assertLocator(file, path, item.getFileLocator());
	}

	private final void assertDataFile(final byte[] expectedData) throws IOException
	{
		final File tempFile = createTempFile(MediaDefaultTest.class.getName(), ".tmp");
		delete(tempFile);
		assertFalse(tempFile.exists());

		item.getFileBody(tempFile);
		assertEqualContent(expectedData, tempFile);
	}

	private void assertExtension(final String contentType, final String extension)
		throws IOException
	{
		final Date before = new Date();
		item.setFile(stream(bytes6), contentType);
		final Date after = new Date();
		assertStreamClosed();
		assertContent(bytes6, before, after, contentType, extension);
	}

	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
	private static final byte[] bytes8  = {-54,104,-63,23,19,-45,71,-23};
	private static final byte[] bytes20 = {-54,71,-86,122,-8,23,-23,104,-63,23,19,-45,-63,23,71,-23,19,-45,71,-23};
	private static final byte[] bytes21 = {-54,71,-86,122,-8,23,-23,104,-63,44,23,19,-45,-63,23,71,-23,19,-45,71,-23};
}
