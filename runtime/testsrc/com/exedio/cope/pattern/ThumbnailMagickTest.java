/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import java.util.Set;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class ThumbnailMagickTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(ThumbnailMagickItem.TYPE);

	static
	{
		MODEL.enableSerialization(ThumbnailMagickTest.class, "MODEL");
	}

	public ThumbnailMagickTest()
	{
		super(MODEL);
	}

	private ThumbnailMagickItem item, jpg, png, gif, txt, emp;
	private final byte[] data  = {-86,122,-8,23};

	// Ok, because Media#set(Item,InputStream,String) closes the stream.
	@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		jpg = deleteOnTearDown(new ThumbnailMagickItem());
		png = deleteOnTearDown(new ThumbnailMagickItem());
		gif = deleteOnTearDown(new ThumbnailMagickItem());
		txt = deleteOnTearDown(new ThumbnailMagickItem());
		emp = deleteOnTearDown(new ThumbnailMagickItem());
		jpg.setFile(ThumbnailMagickTest.class.getResourceAsStream("thumbnail-test.jpg"), "image/jpeg");
		png.setFile(data, "image/png");
		gif.setFile(data, "image/gif");
		txt.setFile(data, "text/plain");
	}

	public void testThumbs() throws IOException
	{
		// test model
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.file,
				item.file.getBody(),
				item.file.getContentType(),
				item.file.getLastModified(),
				item.file.getUnison(),
				item.thumb,
				item.thumbFull,
			}), item.TYPE.getFeatures());
		assertEquals(item.TYPE, item.thumb.getType());
		assertEquals("thumb", item.thumb.getName());
		assertSame(item.file, item.thumb.getSource());
		assertEquals(20, item.thumb.getBoundX());
		assertEquals(30, item.thumb.getBoundY());
		final Set<String> sct = item.thumb.getSupportedSourceContentTypes();
		assertTrue(sct.toString(), sct.contains("image/jpeg"));
		assertTrue(sct.toString(), sct.contains("image/pjpeg"));
		assertTrue(sct.toString(), sct.contains("image/png"));
		assertTrue(sct.toString(), sct.contains("image/gif"));
		assertUnmodifiable(sct);

		assertEquals(item.file.isNull(), item.thumb.isNull());
		assertEquals(item.file.isNotNull(), item.thumb.isNotNull());

		assertSerializedSame(item.thumb, 393);

		try
		{
			new MediaThumbnail(null, 80, 80);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}
		try
		{
			new MediaThumbnail(item.file, 4, 80);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("boundX must be 5 or greater, but was 4", e.getMessage());
		}
		try
		{
			new MediaThumbnail(item.file, 80, 4);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("boundY must be 5 or greater, but was 4", e.getMessage());
		}

		// test content type
		assertEquals("image/jpeg", jpg.getThumbContentType());
		assertEquals("image/jpeg", png.getThumbContentType());
		assertEquals("image/jpeg", gif.getThumbContentType());
		assertEquals(null, txt.getThumbContentType());
		assertEquals(null, emp.getThumbContentType());

		// test url
		assertEquals(mediaRootUrl + "ThumbnailMagickItem/thumb/" + jpg.getCopeID() + ".jpg", jpg.getThumbURL());
		assertEquals(mediaRootUrl + "ThumbnailMagickItem/thumb/" + png.getCopeID() + ".jpg", png.getThumbURL());
		assertEquals(mediaRootUrl + "ThumbnailMagickItem/thumb/" + gif.getCopeID() + ".jpg", gif.getThumbURL());
		assertEquals(null, txt.getThumbURL());
		assertEquals(null, emp.getThumbURL());

		// test url fallback
		assertEquals(mediaRootUrl + "ThumbnailMagickItem/thumb/" + jpg.getCopeID() + ".jpg", jpg.getThumbURLWithFallbackToSource());
		assertEquals(mediaRootUrl + "ThumbnailMagickItem/thumb/" + png.getCopeID() + ".jpg", png.getThumbURLWithFallbackToSource());
		assertEquals(mediaRootUrl + "ThumbnailMagickItem/thumb/" + gif.getCopeID() + ".jpg", gif.getThumbURLWithFallbackToSource());
		assertEquals(mediaRootUrl + "ThumbnailMagickItem/file/"  + txt.getCopeID() + ".txt", txt.getThumbURLWithFallbackToSource());
		assertEquals(null, emp.getThumbURL());

		assertContains(emp, item.TYPE.search(item.file.isNull()));
		assertContains(jpg, png, gif, txt, item.TYPE.search(item.file.isNotNull()));
		assertContains(emp , item.TYPE.search(item.thumb.isNull())); // TODO check for getSupportedSourceContentTypes, add text
		assertContains(jpg, png, gif, txt, item.TYPE.search(item.thumb.isNotNull())); // TODO check for getSupportedSourceContentTypes, remove text

		// test get
		item.thumb.test();
		assertNotNull(jpg.getThumb());
		assertNull(txt.getThumb());
		assertNull(emp.getThumb());

		item.thumbFull.test();
		assertNotNull(jpg.getThumbFull());
		assertNull(txt.getThumbFull());
		assertNull(emp.getThumbFull());
	}
}
