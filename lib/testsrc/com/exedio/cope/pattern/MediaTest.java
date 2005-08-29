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

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.DataAttribute;
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
	private final byte[] dataEmpty = new byte[]{};
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new MediaItem());
	}
	
	private void assertExtension(final String mimeMajor, final String mimeMinor, final String extension)
		throws IOException
	{
		item.setFile(stream(data2), mimeMajor, mimeMinor);
		assertEquals(mimeMajor, item.getFileMimeMajor());
		assertEquals(mimeMinor, item.getFileMimeMinor());
		assertTrue(item.getFileURL().endsWith(extension));
	}
	
	public void testData() throws IOException
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				item.file,
				item.file.getData(),
				item.file.getMimeMajor(),
				item.file.getMimeMinor(),
				item.image,
				item.image.getData(),
				item.image.getMimeMinor(),
				item.photo,
				item.photo.getData(),
				item.photo.getExists(),
				item.foto,
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
		assertEquals(null, item.file.getExists());
		
		assertTrue(item.isFileNull());
		assertEquals(null, item.getFileData());
		assertEquals(-1, item.file.getDataLength(item));
		assertEquals(-1, item.file.getDataLastModified(item));
		assertEquals(null, item.getFileMimeMajor());
		assertEquals(null, item.getFileMimeMinor());
		assertEquals(null, item.getFileContentType());
		assertEquals(null, item.getFileURL());

		final Date beforeData = new Date();
		item.setFile(stream(data), "fileMajor", "fileMinor");
		final Date afterData = new Date();
		assertTrue(!item.isFileNull());
		assertData(data, item.getFileData());
		assertEquals(data.length, item.file.getDataLength(item));
		assertWithin(1000, beforeData, afterData, new Date(item.file.getDataLastModified(item)));
		assertEquals("fileMajor", item.getFileMimeMajor());
		assertEquals("fileMinor", item.getFileMimeMinor());
		assertEquals("fileMajor/fileMinor", item.getFileContentType());
		assertTrue(item.getFileURL().endsWith(".fileMajor.fileMinor"));
		
		final Date beforeData2 = new Date();
		item.setFile(stream(data2), "fileMajor2", "fileMinor2");
		final Date afterData2 = new Date();
		assertTrue(!item.isFileNull());
		assertData(data2, item.getFileData());
		assertEquals(data2.length, item.file.getDataLength(item));
		assertWithin(1000, beforeData2, afterData2, new Date(item.file.getDataLastModified(item)));
		assertEquals("fileMajor2", item.getFileMimeMajor());
		assertEquals("fileMinor2", item.getFileMimeMinor());
		assertEquals("fileMajor2/fileMinor2", item.getFileContentType());
		assertTrue(item.getFileURL().endsWith(".fileMajor2.fileMinor2"));
		
		assertExtension("image", "jpeg", ".jpg");
		assertExtension("image", "pjpeg", ".jpg");
		assertExtension("image", "png", ".png");
		assertExtension("image", "gif", ".gif");
		assertExtension("text", "html", ".html");
		assertExtension("text", "plain", ".txt");
		assertExtension("text", "css", ".css");
		
		final Date beforeDataEmpty = new Date();
		item.setFile(stream(dataEmpty), "emptyMajor", "emptyMinor");
		final Date afterDataEmpty = new Date();
		assertTrue(!item.isFileNull());
		assertData(dataEmpty, item.getFileData());
		assertEquals(0, item.file.getDataLength(item));
		assertWithin(1000, beforeData2, afterData2, new Date(item.file.getDataLastModified(item)));
		assertEquals("emptyMajor", item.getFileMimeMajor());
		assertEquals("emptyMinor", item.getFileMimeMinor());
		assertEquals("emptyMajor/emptyMinor", item.getFileContentType());
		assertTrue(item.getFileURL().endsWith(".emptyMajor.emptyMinor"));

		item.setFile(null, null, null);
		assertTrue(item.isFileNull());
		assertEquals(-1, item.file.getDataLength(item));
		assertEquals(-1, item.file.getDataLastModified(item));
		assertEquals(null, item.getFileData());
		assertEquals(null, item.getFileMimeMajor());
		assertEquals(null, item.getFileMimeMinor());
		assertEquals(null, item.getFileContentType());
		assertEquals(null, item.getFileURL());


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
		assertEquals(null, item.image.getExists());

		assertTrue(item.isImageNull());
		assertEquals(null, item.getImageData());
		assertEquals(-1, item.image.getDataLength(item));
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
		assertEquals(null, item.getImageContentType());
		assertEquals(null, item.getImageURL());

		item.setImage(stream(data), "imageMinor");
		assertTrue(!item.isImageNull());
		assertData(data, item.getImageData());
		assertEquals(data.length, item.image.getDataLength(item));
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("imageMinor", item.getImageMimeMinor());
		assertEquals("image/imageMinor", item.getImageContentType());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".image.imageMinor"));

		item.setImage(stream(data2), "jpeg");
		assertTrue(!item.isImageNull());
		assertData(data2, item.getImageData());
		assertEquals(data2.length, item.image.getDataLength(item));
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("jpeg", item.getImageMimeMinor());
		assertEquals("image/jpeg", item.getImageContentType());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".jpg"));

		item.setImage(null, null);
		assertTrue(item.isImageNull());
		assertEquals(null, item.getImageData());
		assertEquals(-1, item.image.getDataLength(item));
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
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
		final BooleanAttribute photoExists = item.photo.getExists();
		assertSame(item.TYPE, photoExists.getType());
		assertSame("photoExists", photoExists.getName());
		assertEqualsUnmodifiable(list(item.photo), photoExists.getPatterns());

		assertTrue(item.photo.isNull(item));
		assertTrue(item.isPhotoNull());
		assertEquals(null, item.getPhotoData());
		assertEquals(-1, item.photo.getDataLength(item));
		assertEquals(null, item.getPhotoMimeMajor());
		assertEquals(null, item.getPhotoMimeMinor());
		assertEquals(null, item.getPhotoContentType());
		assertEquals(null, item.getPhotoURL());

		item.setPhoto(stream(data));
		assertTrue(!item.isPhotoNull());
		assertData(data, item.getPhotoData());
		assertEquals(data.length, item.photo.getDataLength(item));
		assertEquals("image", item.getPhotoMimeMajor());
		assertEquals("jpeg", item.getPhotoMimeMinor());
		assertEquals("image/jpeg", item.getPhotoContentType());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));

		item.setPhoto(stream(data2));
		assertTrue(!item.isPhotoNull());
		assertData(data2, item.getPhotoData());
		assertEquals(data2.length, item.photo.getDataLength(item));
		assertEquals("image", item.getPhotoMimeMajor());
		assertEquals("jpeg", item.getPhotoMimeMinor());
		assertEquals("image/jpeg", item.getPhotoContentType());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));

		item.setPhoto(null);
		assertTrue(item.isPhotoNull());
		assertEquals(null, item.getPhotoData());
		assertEquals(-1, item.photo.getDataLength(item));
		assertEquals(null, item.getPhotoMimeMajor());
		assertEquals(null, item.getPhotoMimeMinor());
		assertEquals(null, item.getPhotoContentType());
		assertEquals(null, item.getPhotoURL());
		
		
		// foto
		assertEquals(item.TYPE, item.foto.getType());
		assertEquals("foto", item.foto.getName());
		assertSame(item.photo, item.foto.getTarget());
		

		// logs -----------------------------------------------
		
		assertEquals(0, item.photo.entityFound.get());
		assertEquals(0, item.photo.itemFound.get());
		assertEquals(0, item.photo.dataNotNull.get());
		assertEquals(0, item.photo.modified.get());
		assertEquals(0, item.photo.fullyDelivered.get());
		
		item.photo.entityFound.increment();
		assertEquals(1, item.photo.entityFound.get());
		assertEquals(0, item.photo.itemFound.get());
		assertEquals(0, item.photo.dataNotNull.get());
		assertEquals(0, item.photo.modified.get());
		assertEquals(0, item.photo.fullyDelivered.get());

		item.photo.entityFound.increment();
		item.photo.itemFound.increment();
		item.photo.dataNotNull.increment();
		item.photo.modified.increment();
		item.photo.fullyDelivered.increment();
		assertEquals(2, item.photo.entityFound.get());
		assertEquals(1, item.photo.itemFound.get());
		assertEquals(1, item.photo.dataNotNull.get());
		assertEquals(1, item.photo.modified.get());
		assertEquals(1, item.photo.fullyDelivered.get());

		final Date beforeReset = new Date();
		item.photo.resetLogs();
		final Date afterReset = new Date();
		assertEquals(0, item.photo.entityFound.get());
		assertEquals(0, item.photo.itemFound.get());
		assertEquals(0, item.photo.dataNotNull.get());
		assertEquals(0, item.photo.modified.get());
		assertEquals(0, item.photo.fullyDelivered.get());
		assertWithin(beforeReset, afterReset, item.photo.getStart());
	}

}
