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

import com.exedio.cope.testmodel.DataItem;

public class DataTest extends DatabaseLibTest
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
		assertEquals(list(), item.file.getVariants());
		assertUnmodifiable(item.file.getVariants());
		
		assertEquals(null, item.getFileData());
		assertEquals(null, item.getFileMimeMajor());
		assertEquals(null, item.getFileMimeMinor());
		assertEquals(null, item.getFileURL());
		
		item.setFile(stream(data), "fileMajor", "fileMinor");
		assertData(data, item.getFileData());
		assertEquals("fileMajor", item.getFileMimeMajor());
		assertEquals("fileMinor", item.getFileMimeMinor());
		assertTrue(item.getFileURL().endsWith(".fileMajor.fileMinor"));
		
		item.setFile(stream(data2), "fileMajor2", "fileMinor2");
		assertData(data2, item.getFileData());
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
		assertEquals(null, item.getFileData());
		assertEquals(null, item.getFileMimeMajor());
		assertEquals(null, item.getFileMimeMinor());
		assertEquals(null, item.getFileURL());


		// image
		assertEquals("image", item.image.getFixedMimeMajor());
		assertEquals(null, item.image.getFixedMimeMinor());
		assertEquals(list(item.imageBB240), item.image.getVariants());
		assertUnmodifiable(item.image.getVariants());
		assertEquals(item.image, item.imageBB240.getAttribute());
		assertEquals(item.TYPE, item.imageBB240.getType());
		assertEquals("BB240", item.imageBB240.getName());

		assertEquals(null, item.getImageData());
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
		assertEquals(null, item.getImageURL());
		assertEquals(null, item.getImageURLBB240());

		item.setImage(stream(data), "imageMinor");
		assertData(data, item.getImageData());
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("imageMinor", item.getImageMimeMinor());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".image.imageMinor"));
		//System.out.println(item.getImageURLBB240());
		assertTrue(item.getImageURLBB240().endsWith(".image.imageMinor"));
		assertTrue(item.getImageURLBB240().indexOf("/BB240/")>=0);

		item.setImage(stream(data2), "jpeg");
		assertData(data2, item.getImageData());
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("jpeg", item.getImageMimeMinor());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".jpg"));
		//System.out.println(item.getImageURLBB240());
		assertTrue(item.getImageURLBB240().endsWith(".jpg"));
		assertTrue(item.getImageURLBB240().indexOf("/BB240/")>=0);

		item.setImage(null, null);
		assertEquals(null, item.getImageData());
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
		assertEquals(null, item.getImageURL());
		assertEquals(null, item.getImageURLBB240());
		
		
		// photo
		assertEquals("image", item.photo.getFixedMimeMajor());
		assertEquals("jpeg", item.photo.getFixedMimeMinor());
		assertEquals(list(item.photoBB65, item.photoProgressive, item.lowQuality), item.photo.getVariants());
		assertUnmodifiable(item.photo.getVariants());
		assertEquals(item.photo, item.photoBB65.getAttribute());
		assertEquals(item.TYPE, item.photoBB65.getType());
		assertEquals("BB65", item.photoBB65.getName());

		assertEquals(item.photo, item.photoProgressive.getAttribute());
		assertEquals(item.TYPE, item.photoProgressive.getType());
		assertEquals("progressive", item.photoProgressive.getName());

		assertEquals(item.photo, item.lowQuality.getAttribute());
		assertEquals(item.TYPE, item.lowQuality.getType());
		assertEquals("lowQuality", item.lowQuality.getName());

		assertEquals(null, item.getPhotoData());
		assertEquals(null, item.getPhotoMimeMajor());
		assertEquals(null, item.getPhotoMimeMinor());
		assertEquals(null, item.getPhotoURL());
		assertEquals(null, item.getPhotoURLBB65());
		assertEquals(null, item.getPhotoURLProgressive());

		item.setPhoto(stream(data));
		assertData(data, item.getPhotoData());
		assertEquals("image", item.getPhotoMimeMajor());
		assertEquals("jpeg", item.getPhotoMimeMinor());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));
		//System.out.println(item.getPhotoURLBB65());
		assertTrue(item.getPhotoURLBB65().endsWith(".jpg"));
		assertTrue(item.getPhotoURLBB65().indexOf("/BB65/")>=0);
		//System.out.println(item.getPhotoURLProgressive());
		assertTrue(item.getPhotoURLProgressive().endsWith(".jpg"));
		assertTrue(item.getPhotoURLProgressive().indexOf("/progressive/")>=0);
		assertTrue(item.getPhotoURLlowQuality().endsWith(".jpg"));
		assertTrue(item.getPhotoURLlowQuality().indexOf("/lowQuality/")>=0);

		item.setPhoto(stream(data2));
		assertData(data2, item.getPhotoData());
		assertEquals("image", item.getPhotoMimeMajor());
		assertEquals("jpeg", item.getPhotoMimeMinor());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));
		//System.out.println(item.getPhotoURLBB65());
		assertTrue(item.getPhotoURLBB65().endsWith(".jpg"));
		assertTrue(item.getPhotoURLBB65().indexOf("/BB65/")>=0);
		//System.out.println(item.getPhotoURLProgressive());
		assertTrue(item.getPhotoURLProgressive().endsWith(".jpg"));
		assertTrue(item.getPhotoURLProgressive().indexOf("/progressive/")>=0);
		assertTrue(item.getPhotoURLlowQuality().endsWith(".jpg"));
		assertTrue(item.getPhotoURLlowQuality().indexOf("/lowQuality/")>=0);

		item.setPhoto(null);
		assertEquals(null, item.getPhotoData());
		assertEquals(null, item.getPhotoMimeMajor());
		assertEquals(null, item.getPhotoMimeMinor());
		assertEquals(null, item.getPhotoURL());
		assertEquals(null, item.getPhotoURLBB65());
		assertEquals(null, item.getPhotoURLProgressive());
		assertEquals(null, item.getPhotoURLlowQuality());
	}

}
