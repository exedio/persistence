package com.exedio.cope.lib;

import java.io.IOException;

public class AttributeMediaTest extends AttributesTest
{
	public void testSomeMedia() throws IOException
	{
		// TODO: test with not null media
		assertEquals(item.TYPE, item.someMedia.getType());
		assertEquals(null, item.getSomeMediaURL());
		assertEquals(null, item.getSomeMediaURLSomeVariant());
		assertEquals(null, item.getSomeMediaData());
		assertEquals(null, item.getSomeMediaMimeMajor());
		assertEquals(null, item.getSomeMediaMimeMinor());

		final byte[] bytes = new byte[]{3,7,1,4};
		item.setSomeMediaData(stream(bytes),"someMimeMajor", "someMimeMinor");

		final String prefix =
			"/medias/ItemWithManyAttributes/someMedia/";
		final String pkString = String.valueOf(Search.pk2id(item.pk));
		final String expectedURL =
			prefix + pkString + ".someMimeMajor.someMimeMinor";
		final String expectedURLSomeVariant =
			prefix + "SomeVariant/" + pkString + ".someMimeMajor.someMimeMinor";
		//System.out.println(expectedURL);
		//System.out.println(item.getSomeMediaURL());
		assertEquals(expectedURL, item.getSomeMediaURL());
		assertEquals(expectedURLSomeVariant, item.getSomeMediaURLSomeVariant());
		assertData(bytes, item.getSomeMediaData());
		assertEquals("someMimeMajor", item.getSomeMediaMimeMajor());
		assertEquals("someMimeMinor", item.getSomeMediaMimeMinor());

		item.passivate();
		assertEquals(expectedURL, item.getSomeMediaURL());
		assertEquals(expectedURLSomeVariant, item.getSomeMediaURLSomeVariant());
		assertData(bytes, item.getSomeMediaData());
		assertEquals("someMimeMajor", item.getSomeMediaMimeMajor());
		assertEquals("someMimeMinor", item.getSomeMediaMimeMinor());

		assertMediaMime(item, "image", "jpeg", bytes, "jpg");
		assertMediaMime(item, "image", "pjpeg", bytes, "jpg");
		assertMediaMime(item, "image", "gif", bytes, "gif");
		assertMediaMime(item, "image", "png", bytes, "png");
		assertMediaMime(item, "image", "someMinor", bytes, "image.someMinor");

		final byte[] manyBytes = new byte[49467];
		for(int i = 0; i<manyBytes.length; i++)
		{
			manyBytes[i] = (byte)((121*i)%253);
			//System.out.print(manyBytes[i]+", ");
		}
		item.setSomeMediaData(stream(manyBytes),"someMimeMajor", "someMimeMinor");
		assertData(manyBytes, item.getSomeMediaData());

		item.setSomeMediaData(null, null, null);
		assertEquals(null, item.getSomeMediaURL());
		assertEquals(null, item.getSomeMediaURLSomeVariant());
		assertEquals(null, item.getSomeMediaData());
		assertEquals(null, item.getSomeMediaMimeMajor());
		assertEquals(null, item.getSomeMediaMimeMinor());
	}


}
