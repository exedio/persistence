
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
	
	private void assertExtension(final String mimeMajor, final String mimeMinor, final String extension)
		throws IOException
	{
		item.setFileData(stream(data2), mimeMajor, mimeMinor);
		assertEquals(mimeMajor, item.getFileMimeMajor());
		assertEquals(mimeMinor, item.getFileMimeMinor());
		assertTrue(item.getFileURL().endsWith(extension));
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
		
		assertExtension("image", "jpeg", ".jpg");
		assertExtension("image", "pjpeg", ".jpg");
		assertExtension("image", "png", ".png");
		assertExtension("image", "gif", ".gif");
		assertExtension("text", "html", ".html");
		assertExtension("text", "plain", ".txt");
		assertExtension("text", "css", ".css");
		
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
		assertEquals(null, item.getImageURLBB240());

		item.setImageData(stream(data), "imageMinor");
		assertData(data, item.getImageData());
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("imageMinor", item.getImageMimeMinor());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".image.imageMinor"));
		//System.out.println(item.getImageURLBB240());
		assertTrue(item.getImageURLBB240().endsWith(".image.imageMinor"));
		assertTrue(item.getImageURLBB240().indexOf("/BB240/")>=0);

		item.setImageData(stream(data2), "jpeg");
		assertData(data2, item.getImageData());
		assertEquals("image", item.getImageMimeMajor());
		assertEquals("jpeg", item.getImageMimeMinor());
		//System.out.println(item.getImageURL());
		assertTrue(item.getImageURL().endsWith(".jpg"));
		//System.out.println(item.getImageURLBB240());
		assertTrue(item.getImageURLBB240().endsWith(".jpg"));
		assertTrue(item.getImageURLBB240().indexOf("/BB240/")>=0);

		item.setImageData(null, null);
		assertEquals(null, item.getImageData());
		assertEquals(null, item.getImageMimeMajor());
		assertEquals(null, item.getImageMimeMinor());
		assertEquals(null, item.getImageURL());
		assertEquals(null, item.getImageURLBB240());
		
		
		// photo
		assertEquals(null, item.getPhotoData());
		assertEquals(null, item.getPhotoMimeMajor());
		assertEquals(null, item.getPhotoMimeMinor());
		assertEquals(null, item.getPhotoURL());
		assertEquals(null, item.getPhotoURLProgressive());

		item.setPhotoData(stream(data));
		assertData(data, item.getPhotoData());
		assertEquals("image", item.getPhotoMimeMajor());
		assertEquals("jpeg", item.getPhotoMimeMinor());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));
		//System.out.println(item.getPhotoURLProgressive());
		assertTrue(item.getPhotoURLProgressive().endsWith(".jpg"));
		assertTrue(item.getPhotoURLProgressive().indexOf("/Progressive/")>=0);

		item.setPhotoData(stream(data2));
		assertData(data2, item.getPhotoData());
		assertEquals("image", item.getPhotoMimeMajor());
		assertEquals("jpeg", item.getPhotoMimeMinor());
		//System.out.println(item.getPhotoURL());
		assertTrue(item.getPhotoURL().endsWith(".jpg"));
		//System.out.println(item.getPhotoURLProgressive());
		assertTrue(item.getPhotoURLProgressive().endsWith(".jpg"));
		assertTrue(item.getPhotoURLProgressive().indexOf("/Progressive/")>=0);

		item.setPhotoData(null);
		assertEquals(null, item.getPhotoData());
		assertEquals(null, item.getPhotoMimeMajor());
		assertEquals(null, item.getPhotoMimeMinor());
		assertEquals(null, item.getPhotoURL());
		assertEquals(null, item.getPhotoURLProgressive());
	}

}
