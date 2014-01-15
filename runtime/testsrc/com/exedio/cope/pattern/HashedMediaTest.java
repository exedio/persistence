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

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.HashedMedia.Value;
import java.io.IOException;
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

	public void testData() throws IOException
	{
		final Value valueWithHash = HashedMediaItem.uniqueFinalHashedMedia.createValueWithHash(Media.toValue(bytes4, "image/jpeg"));
		final HashedMediaItem mediaItem = deleteOnTearDown(new HashedMediaItem(valueWithHash));

		assertData(bytes4, mediaItem.getUniqueFinalHashedMediaBody());
		assertEquals("image/jpeg", mediaItem.getUniqueFinalHashedMediaContentType());
		assertEquals(bytes4DigestHex, mediaItem.getUniqueFinalHashedMediaHash());

		assertNull(mediaItem.getOptionalHashedMediaBody());
		assertNull(mediaItem.getOptionalHashedMediaContentType());
		assertNull(mediaItem.getOptionalHashedMediaHash());
	}

	public void testUniqueness() throws IOException
	{
		Value valueWithHash = HashedMediaItem.uniqueFinalHashedMedia.createValueWithHash(Media.toValue(bytes4, "image/jpeg"));
	   deleteOnTearDown(new HashedMediaItem(valueWithHash));

		// recreate the value as previous one is exhausted
		valueWithHash = HashedMediaItem.uniqueFinalHashedMedia.createValueWithHash(Media.toValue(bytes4, "image/jpeg"));
		try
		{
		   final HashedMediaItem mediaItem2 = new HashedMediaItem(valueWithHash);
		   deleteOnTearDown(mediaItem2);
		   fail("No ConstraintViolationException for duplicate HashedMediaItem which should be unique");
		}
		catch (final UniqueViolationException e)
		{
			// expected
		}
	}

	public void testConditions()throws IOException
	{
		final Value valueWithHash = HashedMediaItem.uniqueFinalHashedMedia.createValueWithHash(Media.toValue(bytes6, "image/jpeg"));
		final HashedMediaItem mediaItem = deleteOnTearDown(new HashedMediaItem(valueWithHash));
		assertEquals(bytes6DigestHex, mediaItem.getUniqueFinalHashedMediaHash());
		assertEquals(mediaItem, HashedMediaItem.forUniqueFinalHashedMedia(bytes6DigestHex));
	}

	public void testGetOrCreate()throws IOException
	{
		final HashedMediaItem mediaItem =  HashedMediaItem.getOrCreateForUniqueFinalHashedMedia(Media.toValue(bytes8, "image/jpeg"));
		deleteOnTearDown(mediaItem);
		assertEquals(bytes8DigestHex, mediaItem.getUniqueFinalHashedMediaHash());
		final HashedMediaItem mediaItem2 = HashedMediaItem.getOrCreateForUniqueFinalHashedMedia(Media.toValue(bytes8, "image/jpeg"));
		assertEquals(mediaItem, mediaItem2);
	}

	private static final byte[] bytes4 = { -86, 122, -8, 23 };
	private static final byte[] bytes6 = { -97, 35, -126, 86, 19, -8 };
	private static final byte[] bytes8 = { -54, 104, -63, 23, 19, -45, 71, -23 };

	private static final String bytes4DigestHex = "904AC396AC3D50FAA666E57146FE7862";
	private static final String bytes6DigestHex = "FD1A3FCF7460406CBD20205E3185419A";
	private static final String bytes8DigestHex = "3421F7E61C6657DFF8DE513E544DAFC7";
}
