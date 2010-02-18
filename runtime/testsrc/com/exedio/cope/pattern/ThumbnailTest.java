/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.Arrays;
import java.util.Set;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;

public class ThumbnailTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(ThumbnailItem.TYPE);
	
	static
	{
		MODEL.enableSerialization(ThumbnailTest.class, "MODEL");
	}

	public ThumbnailTest()
	{
		super(MODEL);
	}
	
	private ThumbnailItem item, jpg, png, gif, txt, emp;
	private final byte[] data  = {-86,122,-8,23};
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		jpg = deleteOnTearDown(new ThumbnailItem());
		png = deleteOnTearDown(new ThumbnailItem());
		gif = deleteOnTearDown(new ThumbnailItem());
		txt = deleteOnTearDown(new ThumbnailItem());
		emp = deleteOnTearDown(new ThumbnailItem());
		jpg.setFile(data, "image/jpeg");
		png.setFile(data, "image/png");
		gif.setFile(data, "image/gif");
		txt.setFile(data, "text/plain");
	}
	
	public void testThumbs()
	{
		// test model
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.file,
				item.file.getBody(),
				item.file.getContentType(),
				item.file.getLastModified(),
				item.thumb,
				item.thumM,
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
		
		assertSerializedSame(item.thumb, 381);
		assertSerializedSame(item.thumM, 381);
		
		try
		{
			new MediaThumbnail(null, 80, 80);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}
		try
		{
			new MediaThumbnail(item.file, 4, 80);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("boundX must be 5 or greater, but was 4", e.getMessage());
		}
		try
		{
			new MediaThumbnail(item.file, 80, 4);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("boundY must be 5 or greater, but was 4", e.getMessage());
		}
		
		// test sizing algorithm
		assertBB(40, 60, 20, 30);
		assertBB(40, 50, 20, 25);
		assertBB(30, 60, 15, 30);
		assertBB(20, 30, 20, 30);
		assertBB(10, 10, 20, 20);
		
		// test content type
		assertEquals("image/jpeg", jpg.getThumbContentType());
		assertEquals("image/jpeg", png.getThumbContentType());
		assertEquals("image/jpeg", gif.getThumbContentType());
		assertEquals(null, txt.getThumbContentType());
		assertEquals(null, emp.getThumbContentType());
		
		// test url
		assertEquals(mediaRootUrl + "ThumbnailItem/thumb/" + jpg.getCopeID() + ".jpg", jpg.getThumbURL());
		assertEquals(mediaRootUrl + "ThumbnailItem/thumb/" + png.getCopeID() + ".jpg", png.getThumbURL());
		assertEquals(mediaRootUrl + "ThumbnailItem/thumb/" + gif.getCopeID() + ".jpg", gif.getThumbURL());
		assertEquals(null, txt.getThumbURL());
		assertEquals(null, emp.getThumbURL());
		
		// test url fallback
		assertEquals(mediaRootUrl + "ThumbnailItem/thumb/" + jpg.getCopeID() + ".jpg", jpg.getThumbURLWithFallbackToSource());
		assertEquals(mediaRootUrl + "ThumbnailItem/thumb/" + png.getCopeID() + ".jpg", png.getThumbURLWithFallbackToSource());
		assertEquals(mediaRootUrl + "ThumbnailItem/thumb/" + gif.getCopeID() + ".jpg", gif.getThumbURLWithFallbackToSource());
		assertEquals(mediaRootUrl + "ThumbnailItem/file/"  + txt.getCopeID() + ".txt", txt.getThumbURLWithFallbackToSource());
		assertEquals(null, emp.getThumbURL());
		
		assertContains(emp, item.TYPE.search(item.file.isNull()));
		assertContains(jpg, png, gif, txt, item.TYPE.search(item.file.isNotNull()));
		assertContains(emp , item.TYPE.search(item.thumb.isNull())); // TODO check for getSupportedSourceContentTypes, add text
		assertContains(jpg, png, gif, txt, item.TYPE.search(item.thumb.isNotNull())); // TODO check for getSupportedSourceContentTypes, remove text
	}
	
	private void assertBB(final int srcX, final int srcY, final int tgtX, final int tgtY)
	{
		final int[] bb = item.thumb.boundingBox(srcX, srcY);
		assertEquals("width", tgtX, bb[0]);
		assertEquals("height", tgtY, bb[1]);
	}
}
