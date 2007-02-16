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

		final DataField fileBody = t.file.getBody();
		assertSame(t.TYPE, fileBody.getType());
		assertSame("fileBody", fileBody.getName());
		assertEquals(false, fileBody.isFinal());
		assertEquals(true, fileBody.isMandatory());
		assertEquals(20, fileBody.getMaximumLength());
		assertEqualsUnmodifiable(list(t.file), fileBody.getPatterns());
		assertSame(t.file, Media.get(fileBody));
		
		final StringField fileMajor = t.file.getContentType().get(0);
		assertSame(t.TYPE, fileMajor.getType());
		assertEquals("fileMajor", fileMajor.getName());
		assertEquals(false, fileMajor.isFinal());
		assertEquals(true, fileMajor.isMandatory());
		assertEquals(null, fileMajor.getImplicitUniqueConstraint());
		assertEquals(1, fileMajor.getMinimumLength());
		assertEquals(30, fileMajor.getMaximumLength());
		assertEqualsUnmodifiable(list(t.file), fileMajor.getPatterns());
		
		final StringField fileMinor = t.file.getContentType().get(1);
		assertSame(t.TYPE, fileMinor.getType());
		assertEquals("fileMinor", fileMinor.getName());
		assertEquals(false, fileMinor.isFinal());
		assertEquals(true, fileMinor.isMandatory());
		assertEquals(null, fileMinor.getImplicitUniqueConstraint());
		assertEquals(1, fileMinor.getMinimumLength());
		assertEquals(30, fileMinor.getMaximumLength());
		assertEqualsUnmodifiable(list(t.file), fileMinor.getPatterns());
		
		assertEqualsUnmodifiable(list(fileMajor, fileMinor), t.file.getContentType());
		
		final DateField fileLastModified = t.file.getLastModified();
		assertSame(t.TYPE, fileLastModified.getType());
		assertEquals("fileLastModified", fileLastModified.getName());
		assertEquals(false, fileLastModified.isFinal());
		assertEquals(true, fileLastModified.isMandatory());
		assertEquals(null, fileLastModified.getImplicitUniqueConstraint());
		assertEqualsUnmodifiable(list(t.file), fileLastModified.getPatterns());
		assertSame(fileLastModified, t.file.getIsNull());
		
		// test persistence
		assertEquals(list(), t.TYPE.search());

		final Date before = new Date();
		final MediaMandatoryItem item = new MediaMandatoryItem(data20, "major", "minor");
		final Date after = new Date();
		deleteOnTearDown(item);
		assertFile(item, data20, before, after, "major/minor", "");
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
		assertFile(item, data20, before, after, "major/minor", "");
		
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
		assertFile(item, data20, before, after, "major/minor", "");
		
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
		assertFile(item, data20, before, after, "major/minor", "");

		final Date before19 = new Date();
		item.setFile(data19, "major19/minor19");
		final Date after19 = new Date();
		assertFile(item, data19, before19, after19, "major19/minor19", "");

		try
		{
			new MediaMandatoryItem(null, null, null);
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
			new MediaMandatoryItem(data20, null, null);
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
			new MediaMandatoryItem(data20, "major", null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(list(item.file), e.getFeature().getPatterns());
			assertEquals(null, e.getItem());
		}
		assertEquals(list(item), item.TYPE.search());
	}

	private void assertFile(
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
		assertEquals("media/MediaMandatoryItem/file/" + item.getCopeID() + expectedExtension, item.getFileURL());
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
