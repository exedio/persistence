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

import static com.exedio.cope.pattern.MediaType.GIF;
import static com.exedio.cope.pattern.MediaType.JPEG;
import static com.exedio.cope.pattern.MediaType.PNG;
import static com.exedio.cope.pattern.ThumbnailMagickItem.TYPE;
import static com.exedio.cope.pattern.ThumbnailMagickItem.file;
import static com.exedio.cope.pattern.ThumbnailMagickItem.thumb;
import static com.exedio.cope.pattern.ThumbnailMagickItem.thumbFull;
import static com.exedio.cope.pattern.ThumbnailMagickItem.thumbSame;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class ThumbnailMagickModelTest extends CopeAssert
{
	static final Model MODEL = new Model(ThumbnailMagickItem.TYPE);

	static
	{
		MODEL.enableSerialization(ThumbnailMagickModelTest.class, "MODEL");
	}

	@SuppressWarnings("static-method")
	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
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
				thumbSame,
			}), TYPE.getFeatures());
		assertEquals(TYPE, thumb.getType());
		assertEquals("thumb", thumb.getName());
		assertSame(file, thumb.getSource());
		assertEquals(20, thumb.getBoundX());
		assertEquals(30, thumb.getBoundY());
		assertEquals(
				new HashSet<String>(Arrays.asList(JPEG, "image/pjpeg", PNG, "image/x-png", GIF)),
				thumb.getSupportedSourceContentTypes());

		assertEquals(JPEG, thumb.getOutputContentType());
		assertEquals(PNG, thumbFull.getOutputContentType());
		assertEquals(null, thumbSame.getOutputContentType());

		assertEquals(file.isNull(), thumb.isNull());
		assertEquals(file.isNotNull(), thumb.isNotNull());

		assertSerializedSame(thumb, 398);

		thumb.test();
		thumbFull.test();
		thumbSame.test();

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
		catch(final NullPointerException e)
		{
			assertEquals("outputContentType", e.getMessage());
		}
		try
		{
			template.outputContentType(MediaType.ZIP);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("unsupported outputContentType >application/zip<", e.getMessage());
		}
		try
		{
			template.outputContentType("non/sense");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("unsupported outputContentType >non/sense<", e.getMessage());
		}
		try
		{
			template.density(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("density must be 0 or greater, but was -1", e.getMessage());
		}
	}

	private static final void assertSerializedSame(final Serializable value, final int expectedSize)
	{
		assertSame(value, reserialize(value, expectedSize));
	}
}
