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
import com.exedio.cope.DateField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;

public class MediaMandatoryTest extends AbstractLibTest
{
	private static final Model MODEL = new Model(MediaMandatoryItem.TYPE);

	public MediaMandatoryTest()
	{
		super(MODEL);
	}
	
	private final byte[] data19 = {-54,71,-86,122,-8,23,-23,104,-63,23,19,-45,-63,23,71,-23,19,-45,71};
	private final byte[] data20 = {-54,71,-86,122,-8,23,-23,104,-63,23,19,-45,-63,23,71,-23,19,-45,71,-23};
	
	public void testData() throws IOException
	{
		assertEquals(19, data19.length);
		assertEquals(20, data20.length);
		
		// test model
		final MediaMandatoryItem t = null;
		assertEquals(true, t.file.checkContentType("irgendwas/anderswas"));
		assertEquals("*/*", t.file.getContentTypeDescription());
		assertEquals(20, t.file.getMaximumLength());

		final DataField body = t.file.getBody();
		assertSame(t.TYPE, body.getType());
		assertSame("fileBody", body.getName());
		assertEquals(false, body.isFinal());
		assertEquals(true, body.isMandatory());
		assertEquals(20, body.getMaximumLength());
		assertEqualsUnmodifiable(list(t.file), body.getPatterns());
		assertSame(t.file, Media.get(body));
		
		final StringField contentType = (StringField)t.file.getContentType();
		assertSame(t.TYPE, contentType.getType());
		assertEquals("fileContentType", contentType.getName());
		assertEquals(false, contentType.isFinal());
		assertEquals(true, contentType.isMandatory());
		assertEquals(null, contentType.getImplicitUniqueConstraint());
		assertEquals(1, contentType.getMinimumLength());
		assertEquals(61, contentType.getMaximumLength());
		assertEqualsUnmodifiable(list(t.file), contentType.getPatterns());
		
		final DateField lastModified = t.file.getLastModified();
		assertSame(t.TYPE, lastModified.getType());
		assertEquals("fileLastModified", lastModified.getName());
		assertEquals(false, lastModified.isFinal());
		assertEquals(true, lastModified.isMandatory());
		assertEquals(null, lastModified.getImplicitUniqueConstraint());
		assertEqualsUnmodifiable(list(t.file), lastModified.getPatterns());
		assertSame(lastModified, t.file.getIsNull());
		
		// test persistence
		assertEquals(list(), t.TYPE.search());

		final Date before = new Date();
		final MediaMandatoryItem item = new MediaMandatoryItem(data20, "major/minor");
		final Date after = new Date();
		deleteOnTearDown(item);
		assertContent(item, data20, before, after, "major/minor", "");
		assertEquals(list(item), item.TYPE.search());
		
		try
		{
			item.setFile((byte[])null, null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(list(item.file), e.getFeature().getPatterns());
			assertEquals(item, e.getItem());
		}
		assertContent(item, data20, before, after, "major/minor", "");
		
		try
		{
			item.setFile((InputStream)null, null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(list(item.file), e.getFeature().getPatterns());
			assertEquals(item, e.getItem());
		}
		assertContent(item, data20, before, after, "major/minor", "");
		
		try
		{
			item.setFile((File)null, null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(list(item.file), e.getFeature().getPatterns());
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
		catch(MandatoryViolationException e)
		{
			assertEquals(list(item.file), e.getFeature().getPatterns());
			assertEquals(null, e.getItem());
		}
		assertEquals(list(item), item.TYPE.search());

		try
		{
			new MediaMandatoryItem(data20, null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("if body is not null, content type must also be not null", e.getMessage());
		}
		assertEquals(list(item), item.TYPE.search());

		try
		{
			new MediaMandatoryItem(null, "major/minor");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("if body is null, content type must also be null", e.getMessage());
		}
		assertEquals(list(item), item.TYPE.search());
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
		assertWithin(before, after, new Date(item.getFileLastModified()));
		assertEquals(expectedContentType, item.getFileContentType());
		assertEquals(mediaRootUrl + "MediaMandatoryItem/file/" + item.getCopeID() + expectedExtension, item.getFileURL());
	}
	
	private final void assertDataFile(final MediaMandatoryItem item, final byte[] expectedData) throws IOException
	{
		final File tempFile = File.createTempFile("exedio-cope-MediaTest-", ".tmp");
		assertTrue(tempFile.delete());
		assertFalse(tempFile.exists());
		
		item.getFileBody(tempFile);
		assertEqualContent(expectedData, tempFile);
	}
}
