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

import static com.exedio.cope.RuntimeAssert.assertData;
import static com.exedio.cope.pattern.Media.toValue;
import static com.exedio.cope.pattern.UniqueHashedMediaItem.TYPE;
import static com.exedio.cope.pattern.UniqueHashedMediaItem.forHash;
import static com.exedio.cope.pattern.UniqueHashedMediaItem.getOrCreate;
import static com.exedio.cope.pattern.UniqueHashedMediaItem.value;
import static com.exedio.cope.pattern.UniqueHashedMediaItem.w200;
import static com.exedio.cope.pattern.UniqueHashedMediaItem.w300;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.misc.Computed;
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

	@SuppressWarnings("static-method")
	public void testModel()
	{
		assertEqualsUnmodifiable(
				Arrays.asList(new Feature[]
				{
						TYPE.getThis(),
						value,
						value.getMedia(),
						value.getMedia().getBody(),
						value.getMedia().getContentType(),
						value.getMedia().getLastModified(),
						value.getHash(),
						value.getImplicitUniqueConstraint(),
						w200, w300,
				}),
				TYPE.getFeatures());

		 assertEquals(32, value.getHash().getMinimumLength());
		 assertEquals(32, value.getHash().getMaximumLength());
		 assertEquals("MD5", value.getMessageDigestAlgorithm());
		 assertTrue(value.isFinal());
		 assertTrue(value.isMandatory());
		 assertFalse(value.isAnnotationPresent(Computed.class));
		 assertTrue(value.getMedia().isAnnotationPresent(Computed.class));
		 assertTrue(value.getHash().isAnnotationPresent(Computed.class));
	}

	@SuppressWarnings("static-method")
	public void testData()
	{
		final Date before = new Date();
		final UniqueHashedMediaItem mediaItem = new UniqueHashedMediaItem(toValue(bytes4, "image/jpeg"));
		final Date after = new Date();
		assertEquals(model.getConnectProperties().getMediaRootUrl() + "UniqueHashedMediaItem/value-media/UniqueHashedMediaItem-0.jpg", mediaItem.getURL());
		assertEquals("UniqueHashedMediaItem/value-media/UniqueHashedMediaItem-0.jpg", mediaItem.getLocator().getPath());
		assertData(bytes4, mediaItem.getBody());
		assertEquals("image/jpeg", mediaItem.getContentType());
		assertEquals(bytes4DigestHex, mediaItem.getHash());
		assertEquals(4, mediaItem.getLength());
		assertWithin(before, after, mediaItem.getLastModified());

		assertEquals("UniqueHashedMediaItem/w200/UniqueHashedMediaItem-0.jpg", mediaItem.getW200Locator().getPath());
		assertEquals("UniqueHashedMediaItem/w300/UniqueHashedMediaItem-0.jpg", mediaItem.getW300Locator().getPath());
	}

	@SuppressWarnings("static-method")
	public void testUniqueness()
	{
		new UniqueHashedMediaItem(toValue(bytes4, "image/jpeg"));

		// recreate the value as previous one is exhausted
		try
		{
		   new UniqueHashedMediaItem(toValue(bytes4, "image/jpeg"));
		   fail("No ConstraintViolationException for duplicate HashedMediaItem which should be unique");
		}
		catch (final UniqueViolationException e)
		{
			// expected
		}
	}

	@SuppressWarnings("static-method")
	public void testConditions()
	{
		final UniqueHashedMediaItem mediaItem = new UniqueHashedMediaItem(toValue(bytes6, "image/jpeg"));
		assertEquals(bytes6DigestHex, mediaItem.getHash());
		assertEquals(mediaItem, forHash(bytes6DigestHex));
		// no item created with this digest, test if result is null but no exception thrown
		final UniqueHashedMediaItem notExistingMediaItem = forHash(bytes4DigestHex);
		assertNull(notExistingMediaItem);
	}

	@SuppressWarnings("static-method")
	public void testGetOrCreate()throws IOException
	{
		final UniqueHashedMediaItem mediaItem =  getOrCreate(toValue(bytes8, "image/jpeg"));
		assertEquals(bytes8DigestHex, mediaItem.getHash());
		final UniqueHashedMediaItem mediaItem2 = getOrCreate(toValue(bytes8, "image/jpeg"));
		assertEquals(mediaItem, mediaItem2);
		final UniqueHashedMediaItem anotherItem = getOrCreate(toValue(bytes4, "image/jpeg"));
		assertNotEqualsStrict(mediaItem, anotherItem);
		try
		{
			getOrCreate(toValue(bytes4, "image/gif"));
		   fail("No IllegalArgumentException for content type missmatch.");
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals(
					"Given content type 'image/gif' does not match " +
					"content type of already stored value 'image/jpeg' for UniqueHashedMediaItem-1",
					e.getMessage());
		}
	}

	@SuppressWarnings("static-method")
	public void testGetOrCreateNull()throws IOException
	{
		assertEquals(null, getOrCreate(null));
	}

	private static final byte[] bytes4 = { -86, 122, -8, 23 };
	private static final byte[] bytes6 = { -97, 35, -126, 86, 19, -8 };
	private static final byte[] bytes8 = { -54, 104, -63, 23, 19, -45, 71, -23 };

	private static final String bytes4DigestHex = "904ac396ac3d50faa666e57146fe7862";
	private static final String bytes6DigestHex = "fd1a3fcf7460406cbd20205e3185419a";
	private static final String bytes8DigestHex = "3421f7e61c6657dff8de513e544dafc7";
}
