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
import java.util.Date;

import com.exedio.cope.DataAttribute;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.TestmodelTest;
import com.exedio.cope.testmodel.HttpEntityItem;

public class HttpEntityTest extends TestmodelTest
{
	// TODO test various combinations of internal, external implicit, and external explicit source

	private HttpEntityItem item;
	private final byte[] data = new byte[]{-86,122,-8,23};
	private final byte[] data2 = new byte[]{-97,35,-126,86,19,-8};
	private final byte[] dataEmpty = new byte[]{};
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new HttpEntityItem());
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
		// TODO: test item.TYPE.getPatterns

		// file
		assertEquals(null, item.file.getFixedMimeMajor());
		assertEquals(null, item.file.getFixedMimeMinor());
		final DataAttribute fileData = item.file.getData();
		assertSame(item.TYPE, fileData.getType());
		assertSame("fileData", fileData.getName());
		final StringAttribute fileMajor = item.file.getMimeMajor();
		assertSame(item.TYPE, fileMajor.getType());
		assertEquals("fileMajor", fileMajor.getName());
		final StringAttribute fileMinor = item.file.getMimeMinor();
		assertSame(item.TYPE, fileMinor.getType());
		assertEquals("fileMinor", fileMinor.getName());
		assertSame(item.file, HttpEntity.get(fileData));
		
		assertTrue(item.file.isNull(item));
		assertEquals(null, item.getFileData());
		assertEquals(-1, item.file.getDataLength(item));
		assertEquals(-1, item.file.getDataLastModified(item));
		assertEquals(null, item.getFileMimeMajor());
		assertEquals(null, item.getFileMimeMinor());
		assertEquals(null, item.getFileURL());

		final Date beforeData = new Date();
		item.setFile(stream(data), "fileMajor", "fileMinor");
		final Date afterData = new Date();
		assertTrue(!item.file.isNull(item));
		assertData(data, item.getFileData());
		assertEquals(data.length, item.file.getDataLength(item));
		assertWithin(1000, beforeData, afterData, new Date(item.file.getDataLastModified(item)));
		assertEquals("fileMajor", item.getFileMimeMajor());
		assertEquals("fileMinor", item.getFileMimeMinor());
		assertTrue(item.getFileURL().endsWith(".fileMajor.fileMinor"));
		
		final Date beforeData2 = new Date();
		item.setFile(stream(data2), "fileMajor2", "fileMinor2");
		final Date afterData2 = new Date();
		assertTrue(!item.file.isNull(item));
		assertData(data2, item.getFileData());
		assertEquals(data2.length, item.file.getDataLength(item));
		assertWithin(1000, beforeData2, afterData2, new Date(item.file.getDataLastModified(item)));
		assertEquals("fileMajor2", item.getFileMimeMajor());
		assertEquals("fileMinor2", item.getFileMimeMinor());
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
		assertTrue(!item.file.isNull(item));
		assertData(dataEmpty, item.getFileData());
		assertEquals(0, item.file.getDataLength(item));
		assertWithin(1000, beforeData2, afterData2, new Date(item.file.getDataLastModified(item)));
		assertEquals("emptyMajor", item.getFileMimeMajor());
		assertEquals("emptyMinor", item.getFileMimeMinor());
		assertTrue(item.getFileURL().endsWith(".emptyMajor.emptyMinor"));

		item.setFile(null, null, null);
		assertTrue(item.file.isNull(item));
		assertEquals(-1, item.file.getDataLength(item));
		assertEquals(-1, item.file.getDataLastModified(item));
		assertEquals(null, item.getFileData());
		assertEquals(null, item.getFileMimeMajor());
		assertEquals(null, item.getFileMimeMinor());
		assertEquals(null, item.getFileURL());


		// image
		assertEquals("image", item.image.getFixedMimeMajor());
		assertEquals(null, item.image.getFixedMimeMinor());
		assertSame(item.TYPE, item.image.getData().getType());
		assertSame("imageData", item.image.getData().getName());
		assertEquals(null, item.image.getMimeMajor());
		assertSame(item.TYPE, item.image.getMimeMinor().getType());
		assertEquals("imageMinor", item.image.getMimeMinor().getName());
		assertSame(item.image, HttpEntity.get(item.image.getData()));

		assertTrue(item.image.isNull(item));
		assertEquals(null, item.getImageData());
		assertEquals(-1, item.image.getDataLength(item));
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
		assertEquals(null, item.getImageURL());

		item.setImage(stream(data), "imageMinor");
		assertTrue(!item.image.isNull(item));
		assertData(data, item.getImageData());
		assertEquals(data.length, item.image.getDataLength(item));
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("imageMinor", item.getImageMimeMinor());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".image.imageMinor"));

		item.setImage(stream(data2), "jpeg");
		assertTrue(!item.image.isNull(item));
		assertData(data2, item.getImageData());
		assertEquals(data2.length, item.image.getDataLength(item));
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("jpeg", item.getImageMimeMinor());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".jpg"));

		item.setImage(null, null);
		assertTrue(item.image.isNull(item));
		assertEquals(null, item.getImageData());
		assertEquals(-1, item.image.getDataLength(item));
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
		assertEquals(null, item.getImageURL());
		
		
		// photo
		assertEquals("image", item.photo.getFixedMimeMajor());
		assertEquals("jpeg", item.photo.getFixedMimeMinor());
		assertSame(item.TYPE, item.photo.getData().getType());
		assertSame("photoData", item.photo.getData().getName());
		assertEquals(null, item.photo.getMimeMajor());
		assertEquals(null, item.photo.getMimeMinor());
		assertSame(item.photo, HttpEntity.get(item.photo.getData()));

		assertTrue(item.photo.isNull(item));
		assertEquals(null, item.getPhotoData());
		assertEquals(-1, item.photo.getDataLength(item));
		assertEquals(null, item.getPhotoMimeMajor());
		assertEquals(null, item.getPhotoMimeMinor());
		assertEquals(null, item.getPhotoURL());

		item.setPhoto(stream(data));
		assertTrue(!item.photo.isNull(item));
		assertData(data, item.getPhotoData());
		assertEquals(data.length, item.photo.getDataLength(item));
		assertEquals("image", item.getPhotoMimeMajor());
		assertEquals("jpeg", item.getPhotoMimeMinor());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));

		item.setPhoto(stream(data2));
		assertTrue(!item.photo.isNull(item));
		assertData(data2, item.getPhotoData());
		assertEquals(data2.length, item.photo.getDataLength(item));
		assertEquals("image", item.getPhotoMimeMajor());
		assertEquals("jpeg", item.getPhotoMimeMinor());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));

		item.setPhoto(null);
		assertTrue(item.photo.isNull(item));
		assertEquals(null, item.getPhotoData());
		assertEquals(-1, item.photo.getDataLength(item));
		assertEquals(null, item.getPhotoMimeMajor());
		assertEquals(null, item.getPhotoMimeMinor());
		assertEquals(null, item.getPhotoURL());
	}

}
