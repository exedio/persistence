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
import static com.exedio.cope.pattern.MediaType.PNG;
import static com.exedio.cope.pattern.ThumbnailMagickItem.TYPE;
import static com.exedio.cope.pattern.ThumbnailMagickItem.file;
import static com.exedio.cope.pattern.ThumbnailMagickItem.thumb;
import static com.exedio.cope.pattern.ThumbnailMagickItem.thumbFull;

import java.io.IOException;
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

	@SuppressWarnings("static-method")
	public void testThumbs() throws IOException
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				file,
				file.getBody(),
				file.getContentType(),
				file.getLastModified(),
				file.getUnison(),
				thumb,
				thumbFull,
			}), TYPE.getFeatures());
		assertEquals(TYPE, thumb.getType());
		assertEquals("thumb", thumb.getName());
		assertSame(file, thumb.getSource());
		assertEquals(20, thumb.getBoundX());
		assertEquals(30, thumb.getBoundY());
		final Set<String> sct = thumb.getSupportedSourceContentTypes();
		assertTrue(sct.toString(), sct.contains(JPEG));
		assertTrue(sct.toString(), sct.contains("image/pjpeg"));
		assertTrue(sct.toString(), sct.contains("image/png"));
		assertTrue(sct.toString(), sct.contains("image/gif"));
		assertUnmodifiable(sct);

		assertEquals(JPEG, thumb.getOutputContentType());
		assertEquals(PNG, thumbFull.getOutputContentType());

		assertEquals(file.isNull(), thumb.isNull());
		assertEquals(file.isNotNull(), thumb.isNotNull());

		assertSerializedSame(thumb, 398);

		thumb.test();
		thumbFull.test();

		try
		{
			new MediaImageMagickThumbnail(null, 80, 80);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}
		try
		{
			new MediaImageMagickThumbnail(file, 4, 80);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("boundX must be 5 or greater, but was 4", e.getMessage());
		}
		try
		{
			new MediaImageMagickThumbnail(file, 80, 4);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("boundY must be 5 or greater, but was 4", e.getMessage());
		}
		final MediaImageMagickThumbnail template = new MediaImageMagickThumbnail(file, 80, 80);
		try
		{
			template.outputContentType(null);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			template.outputContentType("non/sense");
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("non/sense", e.getMessage());
		}
	}
}
