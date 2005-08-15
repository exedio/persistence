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

package com.exedio.cope;

import java.io.IOException;
import java.util.Date;

import com.exedio.cope.testmodel.DataItem;

public class DataTest extends TestmodelTest
{
	private DataItem item;
	private final byte[] data = new byte[]{-86,122,-8,23};
	private final byte[] data2 = new byte[]{-97,35,-126,86,19,-8};
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new DataItem());
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
		// file
		assertEquals(null, item.file.getFixedMimeMajor());
		assertEquals(null, item.file.getFixedMimeMinor());
		
		assertEquals(null, item.getFileData());
		assertEquals(-1, item.getDataLength(item.file));
		assertEquals(-1, item.getDataLastModified(item.file));
		assertEquals(null, item.getFileMimeMajor());
		assertEquals(null, item.getFileMimeMinor());
		assertEquals(null, item.getFileURL());

		final Date beforeData = new Date();
		item.setFile(stream(data), "fileMajor", "fileMinor");
		final Date afterData = new Date();
		assertData(data, item.getFileData());
		assertEquals(data.length, item.getDataLength(item.file));
		assertWithin(1000, beforeData, afterData, new Date(item.getDataLastModified(item.file)));
		assertEquals("fileMajor", item.getFileMimeMajor());
		assertEquals("fileMinor", item.getFileMimeMinor());
		assertTrue(item.getFileURL().endsWith(".fileMajor.fileMinor"));
		
		final Date beforeData2 = new Date();
		item.setFile(stream(data2), "fileMajor2", "fileMinor2");
		final Date afterData2 = new Date();
		assertData(data2, item.getFileData());
		assertEquals(data2.length, item.getDataLength(item.file));
		assertWithin(1000, beforeData2, afterData2, new Date(item.getDataLastModified(item.file)));
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
		
		item.setFile(null, null, null);
		assertEquals(-1, item.getDataLength(item.file));
		assertEquals(-1, item.getDataLastModified(item.file));
		assertEquals(null, item.getFileData());
		assertEquals(null, item.getFileMimeMajor());
		assertEquals(null, item.getFileMimeMinor());
		assertEquals(null, item.getFileURL());


		// image
		assertEquals("image", item.image.getFixedMimeMajor());
		assertEquals(null, item.image.getFixedMimeMinor());

		assertEquals(null, item.getImageData());
		assertEquals(-1, item.getDataLength(item.image));
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
		assertEquals(null, item.getImageURL());

		item.setImage(stream(data), "imageMinor");
		assertData(data, item.getImageData());
		assertEquals(data.length, item.getDataLength(item.image));
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("imageMinor", item.getImageMimeMinor());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".image.imageMinor"));

		item.setImage(stream(data2), "jpeg");
		assertData(data2, item.getImageData());
		assertEquals(data2.length, item.getDataLength(item.image));
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("jpeg", item.getImageMimeMinor());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".jpg"));

		item.setImage(null, null);
		assertEquals(null, item.getImageData());
		assertEquals(-1, item.getDataLength(item.image));
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
		assertEquals(null, item.getImageURL());
		
		
		// photo
		assertEquals("image", item.photo.getFixedMimeMajor());
		assertEquals("jpeg", item.photo.getFixedMimeMinor());

		assertEquals(null, item.getPhotoData());
		assertEquals(-1, item.getDataLength(item.photo));
		assertEquals(null, item.getPhotoMimeMajor());
		assertEquals(null, item.getPhotoMimeMinor());
		assertEquals(null, item.getPhotoURL());

		item.setPhoto(stream(data));
		assertData(data, item.getPhotoData());
		assertEquals(data.length, item.getDataLength(item.photo));
		assertEquals("image", item.getPhotoMimeMajor());
		assertEquals("jpeg", item.getPhotoMimeMinor());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));

		item.setPhoto(stream(data2));
		assertData(data2, item.getPhotoData());
		assertEquals(data2.length, item.getDataLength(item.photo));
		assertEquals("image", item.getPhotoMimeMajor());
		assertEquals("jpeg", item.getPhotoMimeMinor());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));

		item.setPhoto(null);
		assertEquals(null, item.getPhotoData());
		assertEquals(-1, item.getDataLength(item.photo));
		assertEquals(null, item.getPhotoMimeMajor());
		assertEquals(null, item.getPhotoMimeMinor());
		assertEquals(null, item.getPhotoURL());
	}

}
