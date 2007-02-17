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
import java.util.Arrays;
import java.util.Date;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.DataField;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;

public class MediaTest extends AbstractLibTest
{
	private static final Model MODEL = new Model(MediaItem.TYPE);

	public MediaTest()
	{
		super(MODEL);
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
				item.file.getContentType(),
				item.file.getLastModified(),
				item.image,
				item.image.getBody(),
				item.image.getContentType(),
				item.image.getLastModified(),
				item.photo,
				item.photo.getBody(),
				item.photo.getLastModified(),
				item.foto,
				item.nameServer,
			}), item.TYPE.getFeatures());

		// file
		assertEquals(true, item.file.checkContentType("some/thing"));
		assertEquals(false, item.file.checkContentType("something"));
		assertEquals("*/*", item.file.getContentTypeDescription());
		assertEquals(20, item.file.getMaximumLength());

		final DataField fileBody = item.file.getBody();
		assertSame(item.TYPE, fileBody.getType());
		assertSame("fileBody", fileBody.getName());
		assertEquals(false, fileBody.isFinal());
		assertEquals(false, fileBody.isMandatory());
		assertEquals(20, fileBody.getMaximumLength());
		assertEqualsUnmodifiable(list(item.file), fileBody.getPatterns());
		assertSame(item.file, Media.get(fileBody));
		
		final StringField fileMajor = item.file.getContentType();
		assertSame(item.TYPE, fileMajor.getType());
		assertEquals("fileContentType", fileMajor.getName());
		assertEqualsUnmodifiable(list(item.file), fileMajor.getPatterns());
		assertEquals(false, fileMajor.isFinal());
		assertEquals(false, fileMajor.isMandatory());
		assertEquals(null, fileMajor.getImplicitUniqueConstraint());
		assertEquals(1, fileMajor.getMinimumLength());
		assertEquals(61, fileMajor.getMaximumLength());
		
		final DateField fileLastModified = item.file.getLastModified();
		assertSame(item.TYPE, fileLastModified.getType());
		assertEquals("fileLastModified", fileLastModified.getName());
		assertEqualsUnmodifiable(list(item.file), fileLastModified.getPatterns());
		assertEquals(false, fileLastModified.isFinal());
		assertEquals(false, fileLastModified.isMandatory());
		assertEquals(null, fileLastModified.getImplicitUniqueConstraint());
		assertSame(fileLastModified, item.file.getIsNull());
		
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(stream(data4), "fileMajor/fileMinor");
			final Date after = new Date();
			assertStreamClosed();
			assertFile(data4, before, after, "fileMajor/fileMinor", "");
		}
		{
			final Date before = new Date();
			item.setFile(stream(data6), "fileMajor2/fileMinor2");
			final Date after = new Date();
			assertStreamClosed();
			assertFile(data6, before, after, "fileMajor2/fileMinor2", "");

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
				assertEquals("illegal content type 'illegalContentType' on MediaItem.0 for MediaItem.file, allowed is '*/*\' only.", e.getMessage());
				assertFile(data6, before, after, "fileMajor2/fileMinor2", "");
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
			assertFile(data0, before, after, "emptyMajor/emptyMinor", "");
		}
		item.setFile((InputStream)null, null);
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(file(data8), "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertFile(data8, before, after, "emptyMajor/emptyMinor", "");
		}
		item.setFile((File)null, null);
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(data8, "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertFile(data8, before, after, "emptyMajor/emptyMinor", "");
		}
		item.setFile((byte[])null, null);
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(data20, "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertFile(data20, before, after, "emptyMajor/emptyMinor", "");
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
			assertFile(data20, before, after, "emptyMajor/emptyMinor", "");
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
			assertFile(data20, before, after, "emptyMajor/emptyMinor", "");
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
		assertEquals(true, item.image.checkContentType("image/png"));
		assertEquals(true, item.image.checkContentType("image/jpg"));
		assertEquals(false, item.image.checkContentType("application/jpg"));
		assertEquals("image/*", item.image.getContentTypeDescription());
		assertEquals(Media.DEFAULT_LENGTH, item.image.getMaximumLength());

		final DataField imageBody = item.image.getBody();
		assertSame(item.TYPE, imageBody.getType());
		assertSame("imageBody", imageBody.getName());
		assertEquals(false, imageBody.isFinal());
		assertEquals(false, imageBody.isMandatory());
		assertEquals(Media.DEFAULT_LENGTH, imageBody.getMaximumLength());
		assertEqualsUnmodifiable(list(item.image), imageBody.getPatterns());
		assertSame(item.image, Media.get(imageBody));
		
		final StringField imageMinor = item.image.getContentType();
		assertSame(item.TYPE, imageMinor.getType());
		assertEquals("imageMinor", imageMinor.getName());
		assertEqualsUnmodifiable(list(item.image), imageMinor.getPatterns());
		assertEquals(false, imageMinor.isFinal());
		assertEquals(false, imageMinor.isMandatory());
		assertEquals(null, imageMinor.getImplicitUniqueConstraint());
		assertEquals(1, imageMinor.getMinimumLength());
		assertEquals(30, imageMinor.getMaximumLength());
		
		final DateField imageLastModified = item.image.getLastModified();
		assertSame(item.TYPE, imageLastModified.getType());
		assertEquals("imageLastModified", imageLastModified.getName());
		assertEqualsUnmodifiable(list(item.image), imageLastModified.getPatterns());
		assertEquals(false, imageLastModified.isFinal());
		assertEquals(false, imageLastModified.isMandatory());
		assertEquals(null, imageLastModified.getImplicitUniqueConstraint());
		assertSame(imageLastModified, item.image.getIsNull());

		assertImageNull();

		item.setImage(stream(data4), "image/imageMinor");
		assertStreamClosed();
		assertImage(data4, "image/imageMinor", "");

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
			assertSame(item.image, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("illegalContentType", e.getContentType());
			assertEquals("illegal content type 'illegalContentType' on MediaItem.0 for MediaItem.image, allowed is 'image/*\' only.", e.getMessage());
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
			assertSame(item.image, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("text/html", e.getContentType());
			assertEquals("illegal content type 'text/html' on MediaItem.0 for MediaItem.image, allowed is 'image/*\' only.", e.getMessage());
			assertImage(data6, "image/jpeg", ".jpg");
		}

		item.setImage((InputStream)null, null);
		assertImageNull();
		
		
		// photo
		assertEquals(true, item.photo.checkContentType("image/jpeg"));
		assertEquals(false, item.photo.checkContentType("imaxge/jpeg"));
		assertEquals(false, item.photo.checkContentType("image/jpxeg"));
		assertEquals("image/jpeg", item.photo.getContentTypeDescription());
		assertEquals(2000, item.photo.getMaximumLength());

		final DataField photoBody = item.photo.getBody();
		assertSame(item.TYPE, photoBody.getType());
		assertSame("photoBody", photoBody.getName());
		assertEquals(false, photoBody.isFinal());
		assertEquals(false, photoBody.isMandatory());
		assertEquals(2000, photoBody.getMaximumLength());
		assertEqualsUnmodifiable(list(item.photo), photoBody.getPatterns());
		assertSame(item.photo, Media.get(photoBody));

		assertEquals(null, item.photo.getContentType());

		final DateField photoLastModified = item.photo.getLastModified();
		assertSame(item.TYPE, photoLastModified.getType());
		assertEquals("photoLastModified", photoLastModified.getName());
		assertEqualsUnmodifiable(list(item.photo), photoLastModified.getPatterns());
		assertEquals(false, photoLastModified.isFinal());
		assertEquals(false, photoLastModified.isMandatory());
		assertEquals(null, photoLastModified.getImplicitUniqueConstraint());
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
			assertSame(item.photo, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("illegalContentType", e.getContentType());
			assertEquals("illegal content type 'illegalContentType' on MediaItem.0 for MediaItem.photo, allowed is 'image/jpeg\' only.", e.getMessage());
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
			assertSame(item.photo, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("image/png", e.getContentType());
			assertEquals("illegal content type 'image/png' on MediaItem.0 for MediaItem.photo, allowed is 'image/jpeg\' only.", e.getMessage());
			assertPhoto(data6);
		}

		item.setPhoto((InputStream)null, null);
		assertPhotoNull();
		
		
		// foto
		assertEquals(item.TYPE, item.foto.getType());
		assertEquals("foto", item.foto.getName());
		assertSame(item.photo, item.foto.getTarget());
		
		try
		{
			new MediaRedirect(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("target must not be null", e.getMessage());
		}
		
		assertEquals(null, item.getFotoContentType());
		assertEquals(null, item.getFotoURL());

		item.setPhoto(data4, "image/jpeg");
		assertPhoto(data4);
		assertEquals("image/jpeg", item.getFotoContentType());
		assertEquals("media/MediaItem/foto/" + item.getCopeID() + ".jpg", item.getFotoURL());
		
		item.setPhoto((InputStream)null, null);
		assertPhotoNull();
		assertEquals(null, item.getFotoContentType());
		assertEquals(null, item.getFotoURL());
		
		// nameServer
		assertEquals(item.TYPE, item.nameServer.getType());
		assertEquals("nameServer", item.nameServer.getName());
		assertSame(item.name, item.nameServer.getSource());
		assertEquals("text/plain", item.getNameServerContentType());
		assertEquals("media/MediaItem/nameServer/" + item.getCopeID() + ".txt", item.getNameServerURL());
		

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
		assertEquals("media/MediaItem/file/" + item.getCopeID() + expectedExtension, item.getFileURL());
	}
	
	private final void assertDataFile(final byte[] expectedData) throws IOException
	{
		final File tempFile = File.createTempFile("exedio-cope-MediaTest-", ".tmp");
		assertTrue(tempFile.delete());
		assertFalse(tempFile.exists());
		
		item.getFileBody(tempFile);
		assertEqualContent(expectedData, tempFile);
	}
	
	private void assertImageNull()
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
	{
		assertTrue(!item.isImageNull());
		assertData(expectedData, item.getImageBody());
		assertEquals(expectedData.length, item.getImageLength());
		assertEquals(expectedContentType, item.getImageContentType());
		assertEquals("media/MediaItem/image/" + item.getCopeID() + expectedExtension, item.getImageURL());
	}
	
	private void assertPhotoNull()
	{
		assertTrue(item.photo.isNull(item));
		assertTrue(item.isPhotoNull());
		assertEquals(null, item.getPhotoBody());
		assertEquals(-1, item.getPhotoLength());
		assertEquals(null, item.getPhotoContentType());
		assertEquals(null, item.getPhotoURL());
	}
	
	private void assertPhoto(final byte[] expectedData)
	{
		assertTrue(!item.isPhotoNull());
		assertData(expectedData, item.getPhotoBody());
		assertEquals(expectedData.length, item.getPhotoLength());
		assertEquals("image/jpeg", item.getPhotoContentType());
		assertEquals("media/MediaItem/photo/" + item.getCopeID() + ".jpg", item.getPhotoURL());
	}
}
