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

import static com.exedio.cope.AbstractRuntimeTest.assertData;
import static com.exedio.cope.pattern.UniqueHashedMediaItem.TYPE;
import static com.exedio.cope.pattern.UniqueHashedMediaItem.forHashedMedia;
import static com.exedio.cope.pattern.UniqueHashedMediaItem.getOrCreate;
import static com.exedio.cope.pattern.UniqueHashedMediaItem.hashedMedia;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.UniqueHashedMedia.Value;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public final class UniqueHashedMediaTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(UniqueHashedMediaTest.class, "MODEL");
	}

	public UniqueHashedMediaTest()
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
						TYPE.getThis(),
						hashedMedia,
						hashedMedia.getMedia(),
						hashedMedia.getMedia().getBody(),
						hashedMedia.getMedia().getContentType(),
						hashedMedia.getMedia().getLastModified(),
						hashedMedia.getHash(),
						hashedMedia.getImplicitUniqueConstraint()
				}),
				TYPE.getFeatures());

		 assertEquals(32, hashedMedia.getHash().getMinimumLength());
		 assertEquals(32, hashedMedia.getHash().getMaximumLength());
		 assertEquals("MD5", hashedMedia.getMessageDigestAlgorithm());
		 assertTrue(hashedMedia.isUnique());
		 assertTrue(hashedMedia.isFinal());
		 assertTrue(hashedMedia.isMandatory());
		 assertFalse(hashedMedia.isAnnotationPresent(Computed.class));
		 assertTrue(hashedMedia.getMedia().isAnnotationPresent(Computed.class));
		 assertTrue(hashedMedia.getHash().isAnnotationPresent(Computed.class));
	}

	@SuppressWarnings("static-method")
	public void testData() throws IOException
	{
		final Date before = new Date();
		final Value valueWithHash = hashedMedia.createValueWithHash(Media.toValue(bytes4, "image/jpeg"));
		final UniqueHashedMediaItem mediaItem = new UniqueHashedMediaItem(valueWithHash);
		final Date after = new Date();
		assertEquals(model.getConnectProperties().getMediaRootUrl() + "UniqueHashedMediaItem/hashedMedia-media/UniqueHashedMediaItem-0.jpg", mediaItem.getHashedMediaURL());
		assertEquals("UniqueHashedMediaItem/hashedMedia-media/UniqueHashedMediaItem-0.jpg", mediaItem.getHashedMediaLocator().getPath());
		assertData(bytes4, mediaItem.getHashedMediaBody());
		assertEquals("image/jpeg", mediaItem.getHashedMediaContentType());
		assertEquals(bytes4DigestHex, mediaItem.getHashedMediaHash());
		assertEquals(4, mediaItem.getHashedMediaLength());
		assertWithin(before, after, mediaItem.getHashedMediaLastModified());
	}

	@SuppressWarnings("static-method")
	public void testUniqueness() throws IOException
	{
		Value valueWithHash = hashedMedia.createValueWithHash(Media.toValue(bytes4, "image/jpeg"));
	   new UniqueHashedMediaItem(valueWithHash);

		// recreate the value as previous one is exhausted
		valueWithHash = hashedMedia.createValueWithHash(Media.toValue(bytes4, "image/jpeg"));
		try
		{
		   new UniqueHashedMediaItem(valueWithHash);
		   fail("No ConstraintViolationException for duplicate HashedMediaItem which should be unique");
		}
		catch (final UniqueViolationException e)
		{
			// expected
		}
	}

	@SuppressWarnings("static-method")
	public void testConditions()throws IOException
	{
		final Value valueWithHash = hashedMedia.createValueWithHash(Media.toValue(bytes6, "image/jpeg"));
		final UniqueHashedMediaItem mediaItem = new UniqueHashedMediaItem(valueWithHash);
		assertEquals(bytes6DigestHex, mediaItem.getHashedMediaHash());
		assertEquals(mediaItem, forHashedMedia(bytes6DigestHex));
		// no item created with this digest, test if result is null but no exception thrown
		final UniqueHashedMediaItem notExistingMediaItem = forHashedMedia(bytes4DigestHex);
		assertNull(notExistingMediaItem);
	}

	@SuppressWarnings("static-method")
	public void testGetOrCreate()throws IOException
	{
		final UniqueHashedMediaItem mediaItem =  getOrCreate(Media.toValue(bytes8, "image/jpeg"));
		assertEquals(bytes8DigestHex, mediaItem.getHashedMediaHash());
		final UniqueHashedMediaItem mediaItem2 = getOrCreate(Media.toValue(bytes8, "image/jpeg"));
		assertEquals(mediaItem, mediaItem2);
		final UniqueHashedMediaItem anotherItem = getOrCreate(Media.toValue(bytes4, "image/jpeg"));
		assertNotEqualsStrict(mediaItem, anotherItem);
		try
		{
			getOrCreate(Media.toValue(bytes4, "image/gif"));
		   fail("No IllegalArgumentException for content type missmatch.");
		}
		catch (final IllegalArgumentException e)
		{
			// expected
		}
	}

	private static final byte[] bytes4 = { -86, 122, -8, 23 };
	private static final byte[] bytes6 = { -97, 35, -126, 86, 19, -8 };
	private static final byte[] bytes8 = { -54, 104, -63, 23, 19, -45, 71, -23 };

	private static final String bytes4DigestHex = "904AC396AC3D50FAA666E57146FE7862";
	private static final String bytes6DigestHex = "FD1A3FCF7460406CBD20205E3185419A";
	private static final String bytes8DigestHex = "3421F7E61C6657DFF8DE513E544DAFC7";
}
