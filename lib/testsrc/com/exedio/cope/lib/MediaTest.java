
package com.exedio.cope.lib;

import java.io.IOException;

public class MediaTest extends DatabaseLibTest
{
	private MediaItem item;
	private final byte[] data = new byte[]{-86,122,-8,23};
	private final byte[] data2 = new byte[]{-97,35,-126,86,19,-8};
	
	public void setUp() throws Exception
	{
		super.setUp();
		item = new MediaItem();
	}
	
	public void tearDown() throws Exception
	{
		item.delete();
	}
	
	public void testMedia() throws IOException
	{
		// file
		assertEquals(null, item.getFileData());
		assertEquals(null, item.getFileMimeMajor());
		assertEquals(null, item.getFileMimeMinor());
		assertEquals(null, item.getFileURL());
		
		item.setFileData(stream(data), "fileMajor", "fileMinor");
		assertData(data, item.getFileData());
		assertEquals("fileMajor", item.getFileMimeMajor());
		assertEquals("fileMinor", item.getFileMimeMinor());
		assertTrue(item.getFileURL().endsWith(".fileMajor.fileMinor"));
		
		item.setFileData(stream(data2), "fileMajor2", "fileMinor2");
		assertData(data2, item.getFileData());
		assertEquals("fileMajor2", item.getFileMimeMajor());
		assertEquals("fileMinor2", item.getFileMimeMinor());
		assertTrue(item.getFileURL().endsWith(".fileMajor2.fileMinor2"));
		
		item.setFileData(null, null, null);
		assertEquals(null, item.getFileData());
		assertEquals(null, item.getFileMimeMajor());
		assertEquals(null, item.getFileMimeMinor());
		assertEquals(null, item.getFileURL());


		// image
		assertEquals(null, item.getImageData());
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
		assertEquals(null, item.getImageURL());

		item.setImageData(stream(data), "imageMinor");
		assertData(data, item.getImageData());
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("imageMinor", item.getImageMimeMinor());
		assertTrue(item.getImageURL().endsWith(".image.imageMinor"));

		item.setImageData(stream(data2), "jpeg");
		assertData(data2, item.getImageData());
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("jpeg", item.getImageMimeMinor());
		assertTrue(item.getImageURL().endsWith(".jpg"));

		item.setImageData(null, null);
		assertEquals(null, item.getImageData());
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
		assertEquals(null, item.getImageURL());
	}

}
