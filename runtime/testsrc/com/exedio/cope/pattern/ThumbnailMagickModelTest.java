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

import static com.exedio.cope.pattern.MediaType.JPEG;

import java.util.Arrays;
import java.util.Set;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;

public final class ThumbnailMagickModelTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(ThumbnailMagickItem.TYPE);

	static
	{
		MODEL.enableSerialization(ThumbnailMagickModelTest.class, "MODEL");
	}

	public ThumbnailMagickModelTest()
	{
		super(MODEL);
	}

	private ThumbnailMagickItem item;

	public void testThumbs()
	{
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
		assertTrue(sct.toString(), sct.contains(JPEG));
		assertTrue(sct.toString(), sct.contains("image/pjpeg"));
		assertTrue(sct.toString(), sct.contains("image/png"));
		assertTrue(sct.toString(), sct.contains("image/gif"));
		assertUnmodifiable(sct);

		assertEquals(item.file.isNull(), item.thumb.isNull());
		assertEquals(item.file.isNotNull(), item.thumb.isNotNull());

		assertSerializedSame(item.thumb, 398);

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
	}
}
