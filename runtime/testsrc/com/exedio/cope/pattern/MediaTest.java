/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import java.util.Arrays;
import java.util.Date;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.DataField;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.Main;
import com.exedio.cope.StringField;

public class MediaTest extends AbstractLibTest
{
	public MediaTest()
	{
		super(Main.mediaModel);
	}
	
	// TODO test various combinations of internal, external implicit, and external explicit source

	private MediaItem item;
	private final byte[] data0  = {};
	private final byte[] data4  = {-86,122,-8,23};
	private final byte[] data6  = {-97,35,-126,86,19,-8};
	private final byte[] data8  = {-54,104,-63,23,19,-45,71,-23};
	private final byte[] data20 = {-54,71,-86,122,-8,23,-23,104,-63,23,19,-45,-63,23,71,-23,19,-45,71,-23};
	private final byte[] data21 = {-54,71,-86,122,-8,23,-23,104,-63,44,23,19,-45,-63,23,71,-23,19,-45,71,-23};
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new MediaItem("test media item"));
	}
	
	private void assertExtension(final String contentType, final String extension)
		throws IOException
	{
		final Date before = new Date();
		item.setFile(stream(data6), contentType);
		final Date after = new Date();
		assertStreamClosed();
		assertFile(data6, before, after, contentType, extension);
	}
	
	public void testData() throws IOException
	{
		assertEquals(0, data0.length);
		assertEquals(4, data4.length);
		assertEquals(6, data6.length);
		assertEquals(8, data8.length);
		assertEquals(20, data20.length);
		assertEquals(21, data21.length);
		
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.name,
				item.file,
				item.file.getBody(),
				item.file.getMimeMajor(),
				item.file.getMimeMinor(),
				item.file.getLastModified(),
				item.image,
				item.image.getBody(),
				item.image.getMimeMinor(),
				item.image.getLastModified(),
				item.photo,
				item.photo.getBody(),
				item.photo.getLastModified(),
				item.foto,
				item.photo80,
				item.nameServer,
			}), item.TYPE.getFeatures());

		// file
		assertEquals(null, item.file.getFixedMimeMajor());
		assertEquals(null, item.file.getFixedMimeMinor());
		assertEquals(20, item.file.getMaximumLength());
		final DataField fileBody = item.file.getBody();
		assertSame(item.TYPE, fileBody.getType());
		assertSame("fileBody", fileBody.getName());
		assertEquals(20, fileBody.getMaximumLength());
		assertEqualsUnmodifiable(list(item.file), fileBody.getPatterns());
		assertSame(item.file, Media.get(fileBody));
		final StringField fileMajor = item.file.getMimeMajor();
		assertSame(item.TYPE, fileMajor.getType());
		assertEquals("fileMajor", fileMajor.getName());
		assertEqualsUnmodifiable(list(item.file), fileMajor.getPatterns());
		final StringField fileMinor = item.file.getMimeMinor();
		assertSame(item.TYPE, fileMinor.getType());
		assertEquals("fileMinor", fileMinor.getName());
		assertEqualsUnmodifiable(list(item.file), fileMinor.getPatterns());
		final DateField fileLastModified = item.file.getLastModified();
		assertSame(item.TYPE, fileLastModified.getType());
		assertEquals("fileLastModified", fileLastModified.getName());
		assertEqualsUnmodifiable(list(item.file), fileLastModified.getPatterns());
		assertSame(fileLastModified, item.file.getIsNull());
		
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(stream(data4), "fileMajor/fileMinor");
			final Date after = new Date();
			assertStreamClosed();
			assertFile(data4, before, after, "fileMajor/fileMinor", null);
		}
		{
			final Date before = new Date();
			item.setFile(stream(data6), "fileMajor2/fileMinor2");
			final Date after = new Date();
			assertStreamClosed();
			assertFile(data6, before, after, "fileMajor2/fileMinor2", null);

			try
			{
				item.setFile(stream(data4), "illegalContentType");
				fail();
			}
			catch(IllegalContentTypeException e)
			{
				assertStreamClosed();
				assertEquals("illegalContentType", e.getMessage());
				assertFile(data6, before, after, "fileMajor2/fileMinor2", null);
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
			assertFile(data0, before, after, "emptyMajor/emptyMinor", null);
		}
		item.setFile((InputStream)null, null);
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(file(data8), "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertFile(data8, before, after, "emptyMajor/emptyMinor", null);
		}
		item.setFile((File)null, null);
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(data8, "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertFile(data8, before, after, "emptyMajor/emptyMinor", null);
		}
		item.setFile((byte[])null, null);
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(data20, "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertFile(data20, before, after, "emptyMajor/emptyMinor", null);
			try
			{
				item.setFile(data21, "emptyMajorLong/emptyMinorLong");
				fail();
			}
			catch(DataLengthViolationException e)
			{
				assertSame(fileBody, e.getFeature());
				assertSame(fileBody, e.getFeature());
				assertSame(item, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(true, e.isLengthExact());
				assertEquals("length violation on MediaItem.0, 21 bytes is too long for " + item.file.getBody(), e.getMessage());
			}
			assertFile(data20, before, after, "emptyMajor/emptyMinor", null);
			try
			{
				item.setFile(file(data21), "emptyMajorLong/emptyMinorLong");
				fail();
			}
			catch(DataLengthViolationException e)
			{
				assertSame(fileBody, e.getFeature());
				assertSame(fileBody, e.getFeature());
				assertSame(item, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(true, e.isLengthExact());
				assertEquals("length violation on MediaItem.0, 21 bytes is too long for " + item.file.getBody(), e.getMessage());
			}
			assertFile(data20, before, after, "emptyMajor/emptyMinor", null);
			try
			{
				item.setFile(stream(data21), "emptyMajorLong/emptyMinorLong");
				fail();
			}
			catch(DataLengthViolationException e)
			{
				assertSame(fileBody, e.getFeature());
				assertSame(fileBody, e.getFeature());
				assertSame(item, e.getItem());
				assertEquals(21, e.getLength());
				assertEquals(false, e.isLengthExact());
				assertEquals("length violation on MediaItem.0, 21 bytes or more is too long for " + item.file.getBody(), e.getMessage());
			}
			assertStreamClosed();
			//assertFile(data20, before, after, "emptyMajorLong/emptyMinorLong", ".emptyMajorLong.emptyMinorLong"); TODO
		}
		item.setFile((byte[])null, null);
		assertFileNull();


		// image
		assertEquals("image", item.image.getFixedMimeMajor());
		assertEquals(null, item.image.getFixedMimeMinor());
		assertEquals(Media.DEFAULT_LENGTH, item.image.getMaximumLength());
		final DataField imageBody = item.image.getBody();
		assertSame(item.TYPE, imageBody.getType());
		assertSame("imageBody", imageBody.getName());
		assertEquals(Media.DEFAULT_LENGTH, imageBody.getMaximumLength());
		assertEqualsUnmodifiable(list(item.image), imageBody.getPatterns());
		assertSame(item.image, Media.get(imageBody));
		assertEquals(null, item.image.getMimeMajor());
		final StringField imageMinor = item.image.getMimeMinor();
		assertSame(item.TYPE, imageMinor.getType());
		assertEquals("imageMinor", imageMinor.getName());
		assertEqualsUnmodifiable(list(item.image), imageMinor.getPatterns());
		final DateField imageLastModified = item.image.getLastModified();
		assertSame(item.TYPE, imageLastModified.getType());
		assertEquals("imageLastModified", imageLastModified.getName());
		assertEqualsUnmodifiable(list(item.image), imageLastModified.getPatterns());
		assertSame(imageLastModified, item.image.getIsNull());

		assertImageNull();

		item.setImage(stream(data4), "image/imageMinor");
		assertStreamClosed();
		assertImage(data4, "image/imageMinor", null);

		item.setImage(stream(data6), "image/jpeg");
		assertStreamClosed();
		assertImage(data6, "image/jpeg", ".jpg");

		try
		{
			item.setImage(stream(data4), "illegalContentType");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertEquals("illegalContentType", e.getMessage());
			assertImage(data6, "image/jpeg", ".jpg");
		}

		try
		{
			item.setImage(stream(data4), "text/html");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertEquals("text/html", e.getMessage());
			assertImage(data6, "image/jpeg", ".jpg");
		}

		item.setImage((InputStream)null, null);
		assertImageNull();
		
		
		// photo
		assertEquals("image", item.photo.getFixedMimeMajor());
		assertEquals("jpeg", item.photo.getFixedMimeMinor());
		assertEquals(2000, item.photo.getMaximumLength());
		final DataField photoBody = item.photo.getBody();
		assertSame(item.TYPE, photoBody.getType());
		assertSame("photoBody", photoBody.getName());
		assertEquals(2000, photoBody.getMaximumLength());
		assertEqualsUnmodifiable(list(item.photo), photoBody.getPatterns());
		assertSame(item.photo, Media.get(photoBody));
		assertEquals(null, item.photo.getMimeMajor());
		assertEquals(null, item.photo.getMimeMinor());
		final DateField photoLastModified = item.photo.getLastModified();
		assertSame(item.TYPE, photoLastModified.getType());
		assertEquals("photoLastModified", photoLastModified.getName());
		assertEqualsUnmodifiable(list(item.photo), photoLastModified.getPatterns());
		assertSame(photoLastModified, item.photo.getIsNull());

		assertPhotoNull();

		item.setPhoto(stream(data4), "image/jpeg");
		assertStreamClosed();
		assertPhoto(data4);

		item.setPhoto(stream(data6), "image/jpeg");
		assertStreamClosed();
		assertPhoto(data6);
		
		try
		{
			item.setPhoto(stream(data4), "illegalContentType");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertEquals("illegalContentType", e.getMessage());
			assertPhoto(data6);
		}

		try
		{
			item.setPhoto(stream(data4), "image/png");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertStreamClosed();
			assertEquals("image/png", e.getMessage());
			assertPhoto(data6);
		}

		item.setPhoto((InputStream)null, null);
		assertPhotoNull();
		
		
		// foto
		assertEquals(item.TYPE, item.foto.getType());
		assertEquals("foto", item.foto.getName());
		assertSame(item.photo, item.foto.getTarget());
		
		
		// photo80
		assertEquals(item.TYPE, item.photo80.getType());
		assertEquals("photo80", item.photo80.getName());
		assertSame(item.photo, item.photo80.getMedia());
		assertEquals(80, item.photo80.getBoundX());
		assertEquals(60, item.photo80.getBoundY());
		assertBB(160, 120, 80, 60);
		assertBB(160, 100, 80, 50);
		assertBB(140, 120, 70, 60);
		assertBBNone(80, 60);
		assertBBNone(10, 10);
		
		
		// nameServer
		assertEquals(item.TYPE, item.nameServer.getType());
		assertEquals("nameServer", item.nameServer.getName());
		assertSame(item.name, item.nameServer.getSource());
		

		// logs -----------------------------------------------
		
		assertEquals(0, item.photo.noSuchItem.get());
		assertEquals(0, item.photo.isNull.get());
		assertEquals(0, item.photo.notModified.get());
		assertEquals(0, item.photo.delivered.get());
		
		item.photo.noSuchItem.increment();
		assertEquals(1, item.photo.noSuchItem.get());
		assertEquals(0, item.photo.isNull.get());
		assertEquals(0, item.photo.notModified.get());
		assertEquals(0, item.photo.delivered.get());

		item.photo.noSuchItem.increment();
		item.photo.isNull.increment();
		item.photo.notModified.increment();
		item.photo.delivered.increment();
		assertEquals(2, item.photo.noSuchItem.get());
		assertEquals(1, item.photo.isNull.get());
		assertEquals(1, item.photo.notModified.get());
		assertEquals(1, item.photo.delivered.get());
	}

	private void assertFileNull() throws IOException
	{
		assertTrue(item.isFileNull());
		assertEquals(null, item.getFileBody());
		assertDataFile(null);
		assertEquals(-1, item.getFileLength());
		assertEquals(-1, item.getFileLastModified());
		assertEquals(null, item.getFileContentType());
		assertEquals(null, item.getFileURL());
	}
	
	private void assertFile(
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
		assertTrue(item.getFileURL().endsWith(expectedExtension!=null ? (item.getCopeID() + expectedExtension) : item.getCopeID()));
	}
	
	private final void assertDataFile(final byte[] expectedData) throws IOException
	{
		final File tempFile = File.createTempFile("exedio-cope-MediaTest-", ".tmp");
		assertTrue(tempFile.delete());
		assertFalse(tempFile.exists());
		
		item.getFileBody(tempFile);
		assertEqualContent(expectedData, tempFile);
	}
	
	private void assertImageNull() throws IOException
	{
		assertTrue(item.isImageNull());
		assertEquals(null, item.getImageBody());
		assertEquals(-1, item.getImageLength());
		assertEquals(null, item.getImageContentType());
		assertEquals(null, item.getImageURL());
	}
	
	private void assertImage(
			final byte[] expectedData,
			final String expectedContentType, final String expectedExtension)
	throws IOException
	{
		assertTrue(!item.isImageNull());
		assertData(expectedData, item.getImageBody());
		assertEquals(expectedData.length, item.getImageLength());
		assertEquals(expectedContentType, item.getImageContentType());
		assertTrue(item.getImageURL().endsWith(expectedExtension!=null ? (item.getCopeID() + expectedExtension) : item.getCopeID()));
	}
	
	private void assertPhotoNull() throws IOException
	{
		assertTrue(item.photo.isNull(item));
		assertTrue(item.isPhotoNull());
		assertEquals(null, item.getPhotoBody());
		assertEquals(-1, item.getPhotoLength());
		assertEquals(null, item.getPhotoContentType());
		assertEquals(null, item.getPhotoURL());
	}
	
	private void assertPhoto(final byte[] expectedData)
	throws IOException
	{
		assertTrue(!item.isPhotoNull());
		assertData(expectedData, item.getPhotoBody());
		assertEquals(expectedData.length, item.getPhotoLength());
		assertEquals("image/jpeg", item.getPhotoContentType());
		assertTrue(item.getPhotoURL().endsWith(item.getCopeID() + ".jpg"));
	}

	private void assertBB(final int srcX, final int srcY, final int tgtX, final int tgtY)
	{
		final int[] bb = item.photo80.boundingBox(srcX, srcY);
		assertEquals("width", tgtX, bb[0]);
		assertEquals("height", tgtY, bb[1]);
	}

	private void assertBBNone(final int srcX, final int srcY)
	{
		final int[] bb = item.photo80.boundingBox(srcX, srcY);
		assertNull(bb);
	}
}
