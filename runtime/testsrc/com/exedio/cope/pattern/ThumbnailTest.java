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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.pattern.ThumbnailItem.TYPE;
import static com.exedio.cope.pattern.ThumbnailItem.file;
import static com.exedio.cope.pattern.ThumbnailItem.thumb;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public final class ThumbnailTest extends AbstractRuntimeTest
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

	private ThumbnailItem jpg, png, gif, txt, emp;
	private final byte[] data  = {-86,122,-8,23};

	// Ok, because Media#set(Item,InputStream,String) closes the stream.
	@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")

	@Override()
	public void setUp() throws Exception
	{
		super.setUp();
		jpg = deleteOnTearDown(new ThumbnailItem());
		png = deleteOnTearDown(new ThumbnailItem());
		gif = deleteOnTearDown(new ThumbnailItem());
		txt = deleteOnTearDown(new ThumbnailItem());
		emp = deleteOnTearDown(new ThumbnailItem());
		jpg.setFile(ThumbnailTest.class.getResourceAsStream("thumbnail-test2.jpg"), "image/jpeg");
		png.setFile(data, "image/png");
		gif.setFile(data, "image/gif");
		txt.setFile(data, "text/plain");
	}

	public void testThumbs() throws IOException
	{
		// test model
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				file,
				file.getBody(),
				file.getContentType(),
				file.getLastModified(),
				file.getUnison(),
				thumb,
			}), TYPE.getFeatures());
		assertEquals(TYPE, thumb.getType());
		assertEquals("thumb", thumb.getName());
		assertSame(file, thumb.getSource());
		assertEquals(20, thumb.getBoundX());
		assertEquals(30, thumb.getBoundY());
		final Set<String> sct = thumb.getSupportedSourceContentTypes();
		assertTrue(sct.toString(), sct.contains("image/jpeg"));
		assertTrue(sct.toString(), sct.contains("image/pjpeg"));
		assertTrue(sct.toString(), sct.contains("image/png"));
		assertTrue(sct.toString(), sct.contains("image/gif"));
		assertUnmodifiable(sct);

		assertEquals(file.isNull(), thumb.isNull());
		assertEquals(file.isNotNull(), thumb.isNotNull());

		assertSerializedSame(thumb, 381);

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
			new MediaThumbnail(file, 4, 80);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("boundX must be 5 or greater, but was 4", e.getMessage());
		}
		try
		{
			new MediaThumbnail(file, 80, 4);
			fail();
		}
		catch(final IllegalArgumentException e)
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

		// url
		assertLocator("ThumbnailItem/thumb/" + jpg.getCopeID() + ".jpg", jpg.getThumbLocator());
		assertLocator("ThumbnailItem/thumb/" + png.getCopeID() + ".jpg", png.getThumbLocator());
		assertLocator("ThumbnailItem/thumb/" + gif.getCopeID() + ".jpg", gif.getThumbLocator());
		assertLocator(null, txt.getThumbLocator());
		assertLocator(null, emp.getThumbLocator());

		// url fallback
		assertEquals(jpg.getThumbLocator().getURLByConnect(), jpg.getThumbURLWithFallbackToSource());
		assertEquals(png.getThumbLocator().getURLByConnect(), png.getThumbURLWithFallbackToSource());
		assertEquals(gif.getThumbLocator().getURLByConnect(), gif.getThumbURLWithFallbackToSource());
		assertEquals(txt.getFileLocator ().getURLByConnect(), txt.getThumbURLWithFallbackToSource());
		assertEquals(null, emp.getThumbURLWithFallbackToSource());

		// locator fallback
		assertEquals("ThumbnailItem/thumb/" + jpg.getCopeID() + ".jpg", jpg.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailItem/thumb/" + png.getCopeID() + ".jpg", png.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailItem/thumb/" + gif.getCopeID() + ".jpg", gif.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals("ThumbnailItem/file/"  + txt.getCopeID() + ".txt", txt.getThumbLocatorWithFallbackToSource().getPath());
		assertEquals(null, emp.getThumbLocatorWithFallbackToSource());

		// isNull
		assertContains(emp, TYPE.search(file.isNull()));
		assertContains(jpg, png, gif, txt, TYPE.search(file.isNotNull()));
		assertContains(emp , TYPE.search(thumb.isNull())); // TODO check for getSupportedSourceContentTypes, add text
		assertContains(jpg, png, gif, txt, TYPE.search(thumb.isNotNull())); // TODO check for getSupportedSourceContentTypes, remove text

		// test get
		assertNotNull(jpg.getThumb());
		assertNull(txt.getThumb());
		assertNull(emp.getThumb());
	}

	private static void assertBB(final int srcX, final int srcY, final int tgtX, final int tgtY)
	{
		final int[] bb = thumb.boundingBox(srcX, srcY);
		assertEquals("width", tgtX, bb[0]);
		assertEquals("height", tgtY, bb[1]);
	}
}
