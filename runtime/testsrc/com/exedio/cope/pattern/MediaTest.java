/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.DataAttribute;
import com.exedio.cope.DateAttribute;
import com.exedio.cope.Feature;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.TestmodelTest;
import com.exedio.cope.testmodel.MediaItem;

public class MediaTest extends TestmodelTest
{
	// TODO test various combinations of internal, external implicit, and external explicit source

	private MediaItem item;
	private final byte[] data = new byte[]{-86,122,-8,23};
	private final byte[] data2 = new byte[]{-97,35,-126,86,19,-8};
	private final byte[] dataFile = new byte[]{-54,104,-63,23,19,-45,71,-23};
	private final byte[] dataEmpty = new byte[]{};
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new MediaItem("test media item"));
	}
	
	private void assertExtension(final String contentType, final String extension)
		throws IOException
	{
		item.setFile(stream(data2), contentType);
		assertEquals(contentType, item.getFileContentType());
		assertTrue(item.getFileURL().endsWith(extension));
	}
	
	public void testData() throws IOException
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				item.name,
				item.file,
				item.file.getData(),
				item.file.getMimeMajor(),
				item.file.getMimeMinor(),
				item.file.getLastModified(),
				item.image,
				item.image.getData(),
				item.image.getMimeMinor(),
				item.image.getLastModified(),
				item.photo,
				item.photo.getData(),
				item.photo.getLastModified(),
				item.photo.getExists(),
				item.foto,
				item.nameServer,
			}), item.TYPE.getFeatures());

		// file
		assertEquals(null, item.file.getFixedMimeMajor());
		assertEquals(null, item.file.getFixedMimeMinor());
		final DataAttribute fileData = item.file.getData();
		assertSame(item.TYPE, fileData.getType());
		assertSame("fileData", fileData.getName());
		assertEqualsUnmodifiable(list(item.file), fileData.getPatterns());
		assertSame(item.file, Media.get(fileData));
		final StringAttribute fileMajor = item.file.getMimeMajor();
		assertSame(item.TYPE, fileMajor.getType());
		assertEquals("fileMajor", fileMajor.getName());
		assertEqualsUnmodifiable(list(item.file), fileMajor.getPatterns());
		final StringAttribute fileMinor = item.file.getMimeMinor();
		assertSame(item.TYPE, fileMinor.getType());
		assertEquals("fileMinor", fileMinor.getName());
		assertEqualsUnmodifiable(list(item.file), fileMinor.getPatterns());
		final DateAttribute fileLastModified = item.file.getLastModified();
		assertSame(item.TYPE, fileLastModified.getType());
		assertEquals("fileLastModified", fileLastModified.getName());
		assertEqualsUnmodifiable(list(item.file), fileLastModified.getPatterns());
		assertEquals(null, item.file.getExists());
		assertSame(fileMajor, item.file.getIsNull());
		
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(stream(data), "fileMajor/fileMinor");
			final Date after = new Date();
			assertTrue(!item.isFileNull());
			assertData(data, item.getFileData());
			assertDataFile(data);
			assertEquals(data.length, item.getFileLength());
			assertWithin(before, after, new Date(item.getFileLastModified()));
			assertEquals("fileMajor/fileMinor", item.getFileContentType());
			assertTrue(item.getFileURL().endsWith(".fileMajor.fileMinor"));
		}
		{
			final Date before = new Date();
			item.setFile(stream(data2), "fileMajor2/fileMinor2");
			final Date after = new Date();
			assertTrue(!item.isFileNull());
			assertData(data2, item.getFileData());
			assertDataFile(data2);
			assertEquals(data2.length, item.getFileLength());
			assertWithin(before, after, new Date(item.getFileLastModified()));
			assertEquals("fileMajor2/fileMinor2", item.getFileContentType());
			assertTrue(item.getFileURL().endsWith(".fileMajor2.fileMinor2"));

			try
			{
				item.setFile(stream(data), "illegalContentType");
				fail();
			}
			catch(IllegalContentTypeException e)
			{
				assertEquals("illegalContentType", e.getMessage());
				assertTrue(!item.isFileNull());
				assertData(data2, item.getFileData());
				assertDataFile(data2);
				assertEquals(data2.length, item.getFileLength());
				assertWithin(before, after, new Date(item.getFileLastModified()));
				assertEquals("fileMajor2/fileMinor2", item.getFileContentType());
				assertTrue(item.getFileURL().endsWith(".fileMajor2.fileMinor2"));
			}
		}
		assertExtension("image/jpeg", ".jpg");
		assertExtension("image/pjpeg", ".jpg");
		assertExtension("image/png", ".png");
		assertExtension("image/gif", ".gif");
		assertExtension("text/html", ".html");
		assertExtension("text/plain", ".txt");
		assertExtension("text/css", ".css");
		{
			final Date before = new Date();
			item.setFile(stream(dataEmpty), "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertTrue(!item.isFileNull());
			assertData(dataEmpty, item.getFileData());
			assertDataFile(dataEmpty);
			assertEquals(0, item.getFileLength());
			assertWithin(before, after, new Date(item.getFileLastModified()));
			assertEquals("emptyMajor/emptyMinor", item.getFileContentType());
			assertTrue(item.getFileURL().endsWith(".emptyMajor.emptyMinor"));
		}
		item.setFile((InputStream)null, null);
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(file(dataFile), "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertTrue(!item.isFileNull());
			assertData(dataFile, item.getFileData());
			assertDataFile(dataFile);
			assertEquals(dataFile.length, item.getFileLength());
			assertWithin(before, after, new Date(item.getFileLastModified()));
			assertEquals("emptyMajor/emptyMinor", item.getFileContentType());
			assertTrue(item.getFileURL().endsWith(".emptyMajor.emptyMinor"));
		}
		item.setFile((File)null, null);
		assertFileNull();
		{
			final Date before = new Date();
			item.setFile(dataFile, "emptyMajor/emptyMinor");
			final Date after = new Date();
			assertTrue(!item.isFileNull());
			assertData(dataFile, item.getFileData());
			assertDataFile(dataFile);
			assertEquals(dataFile.length, item.getFileLength());
			assertWithin(before, after, new Date(item.getFileLastModified()));
			assertEquals("emptyMajor/emptyMinor", item.getFileContentType());
			assertTrue(item.getFileURL().endsWith(".emptyMajor.emptyMinor"));
		}
		item.setFile((byte[])null, null);
		assertFileNull();


		// image
		assertEquals("image", item.image.getFixedMimeMajor());
		assertEquals(null, item.image.getFixedMimeMinor());
		final DataAttribute imageData = item.image.getData();
		assertSame(item.TYPE, imageData.getType());
		assertSame("imageData", imageData.getName());
		assertEqualsUnmodifiable(list(item.image), imageData.getPatterns());
		assertSame(item.image, Media.get(imageData));
		assertEquals(null, item.image.getMimeMajor());
		final StringAttribute imageMinor = item.image.getMimeMinor();
		assertSame(item.TYPE, imageMinor.getType());
		assertEquals("imageMinor", imageMinor.getName());
		assertEqualsUnmodifiable(list(item.image), imageMinor.getPatterns());
		final DateAttribute imageLastModified = item.image.getLastModified();
		assertSame(item.TYPE, imageLastModified.getType());
		assertEquals("imageLastModified", imageLastModified.getName());
		assertEqualsUnmodifiable(list(item.image), imageLastModified.getPatterns());
		assertEquals(null, item.image.getExists());
		assertSame(imageMinor, item.image.getIsNull());

		assertTrue(item.isImageNull());
		assertEquals(null, item.getImageData());
		assertEquals(-1, item.getImageLength());
		assertEquals(null, item.getImageContentType());
		assertEquals(null, item.getImageURL());

		item.setImage(stream(data), "image/imageMinor");
		assertTrue(!item.isImageNull());
		assertData(data, item.getImageData());
		assertEquals(data.length, item.getImageLength());
		assertEquals("image/imageMinor", item.getImageContentType());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".image.imageMinor"));

		item.setImage(stream(data2), "image/jpeg");
		assertTrue(!item.isImageNull());
		assertData(data2, item.getImageData());
		assertEquals(data2.length, item.getImageLength());
		assertEquals("image/jpeg", item.getImageContentType());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".jpg"));

		try
		{
			item.setImage(stream(data), "illegalContentType");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertEquals("illegalContentType", e.getMessage());
			assertTrue(!item.isImageNull());
			assertData(data2, item.getImageData());
			assertEquals(data2.length, item.getImageLength());
			assertEquals("image/jpeg", item.getImageContentType());
			assertTrue(item.getImageURL().endsWith(".jpg"));
		}

		try
		{
			item.setImage(stream(data), "text/html");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertEquals("text/html", e.getMessage());
			assertTrue(!item.isImageNull());
			assertData(data2, item.getImageData());
			assertEquals(data2.length, item.getImageLength());
			assertEquals("image/jpeg", item.getImageContentType());
			assertTrue(item.getImageURL().endsWith(".jpg"));
		}

		item.setImage((InputStream)null, null);
		assertTrue(item.isImageNull());
		assertEquals(null, item.getImageData());
		assertEquals(-1, item.getImageLength());
		assertEquals(null, item.getImageContentType());
		assertEquals(null, item.getImageURL());
		
		
		// photo
		assertEquals("image", item.photo.getFixedMimeMajor());
		assertEquals("jpeg", item.photo.getFixedMimeMinor());
		final DataAttribute photoData = item.photo.getData();
		assertSame(item.TYPE, photoData.getType());
		assertSame("photoData", photoData.getName());
		assertEqualsUnmodifiable(list(item.photo), photoData.getPatterns());
		assertSame(item.photo, Media.get(photoData));
		assertEquals(null, item.photo.getMimeMajor());
		assertEquals(null, item.photo.getMimeMinor());
		final DateAttribute photoLastModified = item.photo.getLastModified();
		assertSame(item.TYPE, photoLastModified.getType());
		assertEquals("photoLastModified", photoLastModified.getName());
		assertEqualsUnmodifiable(list(item.photo), photoLastModified.getPatterns());
		final BooleanAttribute photoExists = item.photo.getExists();
		assertSame(item.TYPE, photoExists.getType());
		assertSame("photoExists", photoExists.getName());
		assertEqualsUnmodifiable(list(item.photo), photoExists.getPatterns());
		assertSame(photoExists, item.photo.getIsNull());

		assertTrue(item.photo.isNull(item));
		assertTrue(item.isPhotoNull());
		assertEquals(null, item.getPhotoData());
		assertEquals(-1, item.getPhotoLength());
		assertEquals(null, item.getPhotoContentType());
		assertEquals(null, item.getPhotoURL());

		item.setPhoto(stream(data), "image/jpeg");
		assertTrue(!item.isPhotoNull());
		assertData(data, item.getPhotoData());
		assertEquals(data.length, item.getPhotoLength());
		assertEquals("image/jpeg", item.getPhotoContentType());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));

		item.setPhoto(stream(data2), "image/jpeg");
		assertTrue(!item.isPhotoNull());
		assertData(data2, item.getPhotoData());
		assertEquals(data2.length, item.getPhotoLength());
		assertEquals("image/jpeg", item.getPhotoContentType());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));
		
		try
		{
			item.setPhoto(stream(data), "illegalContentType");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertEquals("illegalContentType", e.getMessage());
			assertTrue(!item.isPhotoNull());
			assertData(data2, item.getPhotoData());
			assertEquals(data2.length, item.getPhotoLength());
			assertEquals("image/jpeg", item.getPhotoContentType());
			assertTrue(item.getPhotoURL().endsWith(".jpg"));
		}

		try
		{
			item.setPhoto(stream(data), "image/png");
			fail();
		}
		catch(IllegalContentTypeException e)
		{
			assertEquals("image/png", e.getMessage());
			assertTrue(!item.isPhotoNull());
			assertData(data2, item.getPhotoData());
			assertEquals(data2.length, item.getPhotoLength());
			assertEquals("image/jpeg", item.getPhotoContentType());
			assertTrue(item.getPhotoURL().endsWith(".jpg"));
		}

		item.setPhoto((InputStream)null, null);
		assertTrue(item.isPhotoNull());
		assertEquals(null, item.getPhotoData());
		assertEquals(-1, item.getPhotoLength());
		assertEquals(null, item.getPhotoContentType());
		assertEquals(null, item.getPhotoURL());
		
		
		// foto
		assertEquals(item.TYPE, item.foto.getType());
		assertEquals("foto", item.foto.getName());
		assertSame(item.photo, item.foto.getTarget());
		
		
		// nameServer
		assertEquals(item.TYPE, item.nameServer.getType());
		assertEquals("nameServer", item.nameServer.getName());
		assertSame(item.name, item.nameServer.getSource());
		

		// logs -----------------------------------------------
		
		assertEquals(0, item.photo.noSuchItem.get());
		assertEquals(0, item.photo.dataIsNull.get());
		assertEquals(0, item.photo.notModified.get());
		assertEquals(0, item.photo.delivered.get());
		
		item.photo.noSuchItem.increment();
		assertEquals(1, item.photo.noSuchItem.get());
		assertEquals(0, item.photo.dataIsNull.get());
		assertEquals(0, item.photo.notModified.get());
		assertEquals(0, item.photo.delivered.get());

		item.photo.noSuchItem.increment();
		item.photo.dataIsNull.increment();
		item.photo.notModified.increment();
		item.photo.delivered.increment();
		assertEquals(2, item.photo.noSuchItem.get());
		assertEquals(1, item.photo.dataIsNull.get());
		assertEquals(1, item.photo.notModified.get());
		assertEquals(1, item.photo.delivered.get());
	}

	private void assertFileNull() throws IOException
	{
		assertTrue(item.isFileNull());
		assertEquals(null, item.getFileData());
		assertDataFile(null);
		assertEquals(-1, item.getFileLength());
		assertEquals(-1, item.getFileLastModified());
		assertEquals(null, item.getFileContentType());
		assertEquals(null, item.getFileURL());
	}
	
	private final void assertDataFile(final byte[] expectedData) throws IOException
	{
		final File tempFile = File.createTempFile("cope-MediaTest.", ".tmp");
		assertTrue(tempFile.delete());
		assertFalse(tempFile.exists());
		
		item.getFileData(tempFile);
		assertEqualContent(expectedData, tempFile);
	}
	
}
