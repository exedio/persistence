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

import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.misc.Computed;
import java.util.Arrays;

public final class HashedMediaTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(HashedMediaItem.TYPE);

	static
	{
		MODEL.enableSerialization(HashedMediaTest.class, "MODEL");
	}

	public HashedMediaTest()
	{
		super(MODEL);
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
	}

	@SuppressWarnings("static-method")
	public void testModel()
	{
		assertEqualsUnmodifiable(
				Arrays.asList(new Feature[]
				{
						HashedMediaItem.TYPE.getThis(),
						HashedMediaItem.optionalHashedMedia,
						HashedMediaItem.optionalHashedMedia.getMedia(),
						HashedMediaItem.optionalHashedMedia.getMedia().getBody(),
						HashedMediaItem.optionalHashedMedia.getMedia().getContentType(),
						HashedMediaItem.optionalHashedMedia.getMedia().getLastModified(),
						HashedMediaItem.optionalHashedMedia.getMedia().getUnison(),
						HashedMediaItem.optionalHashedMedia.getHash(),
						HashedMediaItem.optionalHashedMedia.getUnison(),
						HashedMediaItem.uniqueFinalHashedMedia,
						HashedMediaItem.uniqueFinalHashedMedia.getMedia(),
						HashedMediaItem.uniqueFinalHashedMedia.getMedia().getBody(),
						HashedMediaItem.uniqueFinalHashedMedia.getMedia().getContentType(),
						HashedMediaItem.uniqueFinalHashedMedia.getMedia().getLastModified(),
						HashedMediaItem.uniqueFinalHashedMedia.getHash(),
						HashedMediaItem.uniqueFinalHashedMedia.getImplicitUniqueConstraint()
				}),
				HashedMediaItem.TYPE.getFeatures());

		 assertEquals(32, HashedMediaItem.optionalHashedMedia.getHash().getMinimumLength());
		 assertEquals(32, HashedMediaItem.optionalHashedMedia.getHash().getMaximumLength());
		 assertEquals(32, HashedMediaItem.uniqueFinalHashedMedia.getHash().getMinimumLength());
		 assertEquals(32, HashedMediaItem.uniqueFinalHashedMedia.getHash().getMaximumLength());
		 assertFalse(HashedMediaItem.optionalHashedMedia.isAnnotationPresent(Computed.class));
		 assertTrue(HashedMediaItem.optionalHashedMedia.getHash().isAnnotationPresent(Computed.class));
		 assertFalse(HashedMediaItem.uniqueFinalHashedMedia.isAnnotationPresent(Computed.class));
		 assertTrue(HashedMediaItem.uniqueFinalHashedMedia.getHash().isAnnotationPresent(Computed.class));
	}

	public void testData()
	{
		final HashedMediaItem mediaItem = deleteOnTearDown(new HashedMediaItem(Media.toValue(bytes4, "image/jpeg")));

		assertData(bytes4, mediaItem.getUniqueFinalHashedMediaBody());
		assertEquals("image/jpeg", mediaItem.getUniqueFinalHashedMediaContentType());
		assertLocator("HashedMediaItem/uniqueFinalHashedMedia/" + mediaItem.getCopeID() + ".jpg", mediaItem.getUniqueFinalHashedMediaLocator());
		assertEquals(bytes4DigestHex, mediaItem.getUniqueFinalHashedMediaHash());

		assertNull(mediaItem.getOptionalHashedMediaBody());
		assertNull(mediaItem.getOptionalHashedMediaContentType());
		assertNull(mediaItem.getOptionalHashedMediaLocator());
		assertNull(mediaItem.getOptionalHashedMediaHash());
	}

	public void testConditions()
	{
		final HashedMediaItem mediaItem = deleteOnTearDown(new HashedMediaItem(Media.toValue(bytes6, "image/jpeg")));
		assertEquals(bytes6DigestHex, mediaItem.getUniqueFinalHashedMediaHash());
		assertEquals(mediaItem, HashedMediaItem.forUniqueFinalHashedMedia(bytes6DigestHex));
	}

	static final byte[] bytes4 = { -86, 122, -8, 23 };
	static final byte[] bytes6 = { -97, 35, -126, 86, 19, -8 };
	static final byte[] bytes8 = { -54, 104, -63, 23, 19, -45, 71, -23 };

	static final String bytes4DigestHex = "904AC396AC3D50FAA666E57146FE7862";
	static final String bytes6DigestHex = "FD1A3FCF7460406CBD20205E3185419A";
	static final String bytes8DigestHex = "3421F7E61C6657DFF8DE513E544DAFC7";
}
