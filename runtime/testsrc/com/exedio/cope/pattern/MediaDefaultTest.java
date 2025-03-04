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

import static com.exedio.cope.AbstractRuntimeTest.assertEqualContent;
import static com.exedio.cope.RuntimeAssert.assertData;
import static com.exedio.cope.pattern.MediaItem.TYPE;
import static com.exedio.cope.pattern.MediaItem.file;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.DateField;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class MediaDefaultTest extends TestWithEnvironment
{
	public MediaDefaultTest()
	{
		super(MediaTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	protected MediaItem item;

	@BeforeEach final void setUp()
	{
		clockRule.override(clock);
		item = new MediaItem("test media item");
	}

	@Test void testIt() throws IOException
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
		assertEquals(61, file.getContentTypeMaximumLength());
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

		// test persistence
		assertEquals("file_body", SchemaInfo.getColumnName(file.getBody()));
		assertEquals("file_contentType", SchemaInfo.getColumnName(file.getContentType()));
		assertEquals("file_lastModified", SchemaInfo.getColumnName(file.getLastModified()));

		assertContentNull();
		assertContains(item, TYPE.search(file.isNull()));
		assertContains(TYPE.search(file.isNotNull()));
		{
			clock.add(123456001);
			item.setFile(stream(bytes4), "file-major/file-minor");
			clock.assertEmpty();
			assertStreamClosed();
			assertContent(bytes4, new Date(123456001), "file-major/file-minor", "");
		}
		assertContains(TYPE.search(file.isNull()));
		assertContains(item, TYPE.search(file.isNotNull()));
		{
			clock.add(123456002);
			item.setFile(stream(bytes6), "file-major2/file-minor2");
			clock.assertEmpty();
			assertStreamClosed();
			assertContent(bytes6, new Date(123456002), "file-major2/file-minor2", "");

			try(InputStream in = stream(bytes4))
			{
				item.setFile(in, "illegalContentType");
				fail();
			}
			catch(final IllegalContentTypeException e)
			{
				assertStreamClosed();
				assertSame(file, e.getFeature());
				assertEquals(item, e.getItem());
				assertEquals("illegalContentType", e.getContentType());
				assertEquals("illegal content type 'illegalContentType' on " + item + " for MediaItem.file, allowed is '*/*' only", e.getMessage());
				assertContent(bytes6, new Date(123456002), "file-major2/file-minor2", "");
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
		{
			clock.add(123456003);
			item.setFile(stream(bytes0), "empty-major/empty-minor");
			clock.assertEmpty();
			assertStreamClosed();
			assertContent(bytes0, new Date(123456003), "empty-major/empty-minor", "");
		}
		item.setFile((InputStream)null, null);
		assertContentNull();
		{
			clock.add(123457);
			item.setFile(files.newPath(bytes8), "empty-major/empty-minor");
			clock.assertEmpty();
			assertContent(bytes8, new Date(123457), "empty-major/empty-minor", "");
		}
		item.setFile((Path)null, null);
		assertContentNull();
		{
			clock.add(123457);
			item.setFile(files.newFile(bytes8), "empty-major/empty-minor");
			clock.assertEmpty();
			assertContent(bytes8, new Date(123457), "empty-major/empty-minor", "");
		}
		item.setFile((File)null, null);
		assertContentNull();
		{
			clock.add(123456004);
			item.setFile(bytes8, "empty-major/empty-minor");
			clock.assertEmpty();
			assertContent(bytes8, new Date(123456004), "empty-major/empty-minor", "");
		}
		item.setFile((byte[])null, null);
		assertContentNull();
		{
			clock.add(123456005);
			item.setFile(Media.toValue(bytes8, "empty-major/empty-minor"));
			clock.assertEmpty();
			assertContent(bytes8, new Date(123456005), "empty-major/empty-minor", "");
		}
		item.setFile(null);
		assertContentNull();
		{
			// test length violation
			clock.add(123456006);
			item.setFile(bytes20, "empty-major/empty-minor");
			clock.assertEmpty();
			assertContent(bytes20, new Date(123456006), "empty-major/empty-minor", "");

			// byte[]
			clock.add(123456007);
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
			clock.assertEmpty();
			assertContent(bytes20, new Date(123456006), "empty-major/empty-minor", "");

			clock.add(123456008);
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
			clock.assertEmpty();

			// path
			clock.add(123456009);
			try
			{
				item.setFile(files.newPath(bytes21), "empty-major-long/empty-minor-long");
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
			clock.assertEmpty();
			assertContent(bytes20, new Date(123456006), "empty-major/empty-minor", "");

			clock.add(123456010);
			try
			{
				new MediaItem(Media.toValue(files.newPath(bytes21), "empty-major-long/empty-minor-long"));
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
			clock.assertEmpty();

			// file
			clock.add(123456009);
			try
			{
				item.setFile(files.newFile(bytes21), "empty-major-long/empty-minor-long");
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
			clock.assertEmpty();
			assertContent(bytes20, new Date(123456006), "empty-major/empty-minor", "");

			clock.add(123456010);
			try
			{
				new MediaItem(Media.toValue(files.newFile(bytes21), "empty-major-long/empty-minor-long"));
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
			clock.assertEmpty();

			// stream
			clock.add(123456011);
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
			//assertContent(data20, new Date(123457), "empty-major-long/empty-minor-long", ".empty-major-long.empty-minor-long"); TODO
			clock.assertEmpty();

			clock.add(123456012);
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
			clock.assertEmpty();
		}
		item.setFile((byte[])null, null);
		assertContentNull();
	}

	private void assertContentNull() throws IOException
	{
		assertTrue(item.isFileNull());
		assertEquals(null, item.getFileBody());
		assertDataPath(null);
		assertDataFile(null);
		assertEquals(-1, item.getFileLength());
		assertEquals(null, item.getFileLastModified());
		assertEquals(null, item.getFileContentType());
		assertEquals(null, item.getFileLocator());
	}

	private void assertContent(
			final byte[] expectedData,
			final Date lastModified,
			final String expectedContentType, final String expectedExtension)
	throws IOException
	{
		final String path = "MediaItem/file/" + item.getCopeID() + expectedExtension;
		assertTrue(!item.isFileNull());
		assertData(expectedData, item.getFileBody());
		assertDataPath(expectedData);
		assertDataFile(expectedData);
		assertEquals(expectedData.length, item.getFileLength());
		assertEquals(lastModified, item.getFileLastModified());
		assertEquals(expectedContentType, item.getFileContentType());
		assertLocator(file, path, item.getFileLocator());
	}

	private void assertDataPath(final byte[] expectedData) throws IOException
	{
		final Path temp = files.newPathNotExists();
		item.getFileBody(temp);
		assertEqualContent(expectedData, temp.toFile());
	}

	private void assertDataFile(final byte[] expectedData) throws IOException
	{
		final File temp = files.newFileNotExists();
		item.getFileBody(temp);
		assertEqualContent(expectedData, temp);
	}

	private void assertExtension(final String contentType, final String extension)
		throws IOException
	{
		clock.add(9876543);
		item.setFile(stream(bytes6), contentType);
		clock.assertEmpty();
		assertStreamClosed();
		assertContent(bytes6, new Date(9876543), contentType, extension);
	}

	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
	private static final byte[] bytes8  = {-54,104,-63,23,19,-45,71,-23};
	private static final byte[] bytes20 = {-54,71,-86,122,-8,23,-23,104,-63,23,19,-45,-63,23,71,-23,19,-45,71,-23};
	private static final byte[] bytes21 = {-54,71,-86,122,-8,23,-23,104,-63,44,23,19,-45,-63,23,71,-23,19,-45,71,-23};
}
