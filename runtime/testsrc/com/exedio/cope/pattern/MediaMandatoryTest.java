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
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.pattern.MediaMandatoryItem.TYPE;
import static com.exedio.cope.pattern.MediaMandatoryItem.file;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.assertWithin;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Date;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class MediaMandatoryTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(MediaMandatoryTest.class, "MODEL");
	}

	public MediaMandatoryTest()
	{
		super(MODEL);
	}

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	private final byte[] data19 = {-54,71,-86,122,-8,23,-23,104,-63,23,19,-45,-63,23,71,-23,19,-45,71};
	private final byte[] data20 = {-54,71,-86,122,-8,23,-23,104,-63,23,19,-45,-63,23,71,-23,19,-45,71,-23};

	@Test void testData() throws IOException
	{
		assertEquals(19, data19.length);
		assertEquals(20, data20.length);

		// test model
		assertEquals(true, file.isInitial());
		assertEquals(false, file.isFinal());
		assertEquals(true, file.isMandatory());
		assertEquals(Media.Value.class, file.getInitialType());
		assertContains(MandatoryViolationException.class, file.getInitialExceptions());
		assertEquals(true, file.checkContentType("irgendwas/anderswas"));
		assertEquals("*/*", file.getContentTypeDescription());
		assertEquals(null, file.getContentTypesAllowed());
		assertEquals(20, file.getMaximumLength());

		final DataField body = file.getBody();
		assertSame(TYPE, body.getType());
		assertSame("file-body", body.getName());
		assertEquals(false, body.isFinal());
		assertEquals(true, body.isMandatory());
		assertEquals(20, body.getMaximumLength());
		assertEquals(file, body.getPattern());
		assertSame(file, Media.get(body));

		final StringField contentType = (StringField)file.getContentType();
		assertSame(TYPE, contentType.getType());
		assertEquals("file-contentType", contentType.getName());
		assertEquals(false, contentType.isFinal());
		assertEquals(true, contentType.isMandatory());
		assertEquals(null, contentType.getImplicitUniqueConstraint());
		assertEquals(1, contentType.getMinimumLength());
		assertEquals(61, contentType.getMaximumLength());
		assertEquals(file, contentType.getPattern());

		final DateField lastModified = file.getLastModified();
		assertSame(TYPE, lastModified.getType());
		assertEquals("file-lastModified", lastModified.getName());
		assertEquals(false, lastModified.isFinal());
		assertEquals(true, lastModified.isMandatory());
		assertEquals(null, lastModified.getImplicitUniqueConstraint());
		assertEquals(file, lastModified.getPattern());

		assertNull(file.getUnison());

		// test persistence
		assertEquals(list(), TYPE.search());

		final Date before = new Date();
		final MediaMandatoryItem item = new MediaMandatoryItem(data20, "major/minor");
		final Date after = new Date();
		assertContent(item, data20, before, after, "major/minor", "");
		assertEquals(list(item), TYPE.search());

		try
		{
			item.setFile((byte[])null, null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(file, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertContent(item, data20, before, after, "major/minor", "");

		try
		{
			item.setFile((InputStream)null, null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(file, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertContent(item, data20, before, after, "major/minor", "");

		try
		{
			item.setFile((Path)null, null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(file, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertContent(item, data20, before, after, "major/minor", "");

		try
		{
			item.setFile((File)null, null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(file, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertContent(item, data20, before, after, "major/minor", "");

		final Date before19 = new Date();
		item.setFile(data19, "major19/minor19");
		final Date after19 = new Date();
		assertContent(item, data19, before19, after19, "major19/minor19", "");

		try
		{
			new MediaMandatoryItem(null, null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(file, e.getFeature());
			assertEquals(null, e.getItem());
		}
		assertEquals(list(item), TYPE.search());

		try
		{
			new MediaMandatoryItem();
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(file.getBody(), e.getFeature()); // TODO should be media itself, not the body
			assertEquals(null, e.getItem());
		}
		assertEquals(list(item), TYPE.search());

		try
		{
			new MediaMandatoryItem(data20, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("if body is not null, content type must also be not null", e.getMessage());
		}
		assertEquals(list(item), TYPE.search());

		try
		{
			new MediaMandatoryItem(null, "major/minor");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("if body is null, content type must also be null", e.getMessage());
		}
		assertEquals(list(item), TYPE.search());
	}

	private void assertContent(
			final MediaMandatoryItem item,
			final byte[] expectedData,
			final Date before, final Date after,
			final String expectedContentType, final String expectedExtension)
	throws IOException
	{
		assertTrue(!item.isFileNull());
		assertData(expectedData, item.getFileBody());
		assertDataFile(item, expectedData);
		assertEquals(expectedData.length, item.getFileLength());
		assertWithin(before, after, item.getFileLastModified());
		assertEquals(expectedContentType, item.getFileContentType());
		assertLocator("MediaMandatoryItem/file/" + item.getCopeID() + expectedExtension, item.getFileLocator());
	}

	private void assertDataFile(final MediaMandatoryItem item, final byte[] expectedData) throws IOException
	{
		final File temp = files.newFileNotExists();
		item.getFileBody(temp);
		assertEqualContent(expectedData, temp);
	}

	@Test void testSetLastModified()
	{
		final MediaMandatoryItem item = new MediaMandatoryItem(data20, "major/minor");
		file.setLastModified(item, new Date(987654321));

		assertEquals(new Date(987654321), file.getLastModified().get(item));
		assertEquals(new Date(987654321), file.getLastModified(item));
		assertEquals(new Date(987654321), item.getFileLastModified());

		assertFails(
				() -> file.setLastModified(item, null),
				NullPointerException.class, null);

		assertEquals(new Date(987654321), file.getLastModified().get(item));
		assertEquals(new Date(987654321), file.getLastModified(item));
		assertEquals(new Date(987654321), item.getFileLastModified());
	}
}
