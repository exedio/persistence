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

import static com.exedio.cope.pattern.MediaItem.TYPE;
import static com.exedio.cope.pattern.MediaItem.custom;
import static com.exedio.cope.pattern.MediaItem.file;
import static com.exedio.cope.pattern.MediaItem.foto;
import static com.exedio.cope.pattern.MediaItem.image;
import static com.exedio.cope.pattern.MediaItem.name;
import static com.exedio.cope.pattern.MediaItem.photo;
import static com.exedio.cope.pattern.MediaItem.sheet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.misc.Computed;

public final class MediaTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(TYPE, MediaItemHolder.TYPE);

	static
	{
		MODEL.enableSerialization(MediaTest.class, "MODEL");
	}

	public MediaTest()
	{
		super(MODEL);
	}

	// TODO test various combinations of internal, external implicit, and external explicit source

	private MediaItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MediaItem("test media item"));
	}

	public void testData() throws IOException
	{
		assertEquals(0, bytes0.length);
		assertEquals(4, bytes4.length);
		assertEquals(6, bytes6.length);
		assertEquals(8, bytes8.length);
		assertEquals(20, bytes20.length);
		assertEquals(21, bytes21.length);

		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				name,
				file,
				file.getBody(),
				file.getContentType(),
				file.getLastModified(),
				file.getUnison(),
				image,
				image.getBody(),
				image.getContentType(),
				image.getLastModified(),
				image.getUnison(),
				photo,
				photo.getBody(),
				photo.getLastModified(),
				foto,
				sheet,
				sheet.getBody(),
				sheet.getContentType(),
				sheet.getLastModified(),
				sheet.getUnison(),
				custom,
			}), TYPE.getFeatures());

		// foto
		assertEquals(TYPE, foto.getType());
		assertEquals("foto", foto.getName());
		assertSame(photo, foto.getTarget());
		assertEquals(photo.isNull(), foto.isNull());
		assertEquals(photo.isNotNull(), foto.isNotNull());

		assertEquals(null, item.getFotoContentType());
		assertEquals(null, item.getFotoURL());

		item.setPhoto(bytes4, "image/jpeg");
		assertEquals("image/jpeg", item.getFotoContentType());
		assertEquals(mediaRootUrl + "MediaItem/foto/" + item.getCopeID() + ".jpg", item.getFotoURL());

		item.setPhoto((InputStream)null, null);
		assertEquals(null, item.getFotoContentType());
		assertEquals(null, item.getFotoURL());

		// custom
		assertEquals(TYPE, custom.getType());
		assertEquals("custom", custom.getName());
		assertSame(name, custom.getSource());
		assertEquals("text/plain", item.getCustomContentType());
		assertEquals(mediaRootUrl + "MediaItem/custom/" + item.getCopeID() + ".txt", item.getCustomURL());

		assertFalse(file.isAnnotationPresent(Computed.class));
		assertTrue(file.getBody        ().isAnnotationPresent(Computed.class));
		assertTrue(file.getContentType ().isAnnotationPresent(Computed.class));
		assertTrue(file.getLastModified().isAnnotationPresent(Computed.class));

		assertSerializedSame(file,   372);
		assertSerializedSame(image,  373);
		assertSerializedSame(photo,  373);
		assertSerializedSame(foto,   372);
		assertSerializedSame(sheet,  373);
		assertSerializedSame(custom, 374);
	}

	public void testConditions()
	{
		final MediaItem item2 = deleteOnTearDown(new MediaItem("other media item"));
		deleteOnTearDown(new MediaItemHolder(item));
		final MediaItemHolder m2 = deleteOnTearDown(new MediaItemHolder(item2));

		assertEquals(list(item, item2), MediaItem.TYPE.search(MediaItem.photo.isNull()));
		assertEquals(list(), MediaItem.TYPE.search(MediaItem.photo.isNotNull()));

		{
			final Query<MediaItemHolder> query = MediaItemHolder.TYPE.newQuery();
			final Join join1 = query.join(MediaItem.TYPE);
			join1.setCondition(MediaItemHolder.mediaItem.equalTarget(join1) );
			query.narrow( MediaItem.name.bind(join1).startsWith("other") );

			final Join join2 = query.join(MediaItem.TYPE);
			join2.setCondition(MediaItemHolder.mediaItem.equalTarget(join2) );
			query.narrow( MediaItem.photo.isNull(join2) );

			assertEquals( list(m2), query.search() );
		}

		{
			final Query<MediaItemHolder> query = MediaItemHolder.TYPE.newQuery();
			final Join join1 = query.join(MediaItem.TYPE);
			join1.setCondition(MediaItemHolder.mediaItem.equalTarget(join1) );
			query.narrow( MediaItem.name.bind(join1).startsWith("other") );

			final Join join2 = query.join(MediaItem.TYPE);
			join2.setCondition(MediaItemHolder.mediaItem.equalTarget(join2) );
			query.narrow( MediaItem.photo.isNotNull(join2) );

			assertEquals( list(), query.search() );
		}
	}


	@Deprecated
	public static void testDeprecated()
	{
		try
		{
			new MediaRedirect(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("target", e.getMessage());
		}
	}
}
