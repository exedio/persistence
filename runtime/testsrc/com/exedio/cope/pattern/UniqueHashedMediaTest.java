/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertWithin;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.Condition;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.UnsupportedQueryException;
import com.exedio.cope.misc.Computed;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

public class UniqueHashedMediaTest extends TestWithEnvironment
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

	@Test void testModel()
	{
		assertEqualsUnmodifiable(
				asList(new Feature[]
				{
						TYPE.getThis(),
						value,
						value.getMedia(),
						value.getMedia().getBody(),
						value.getMedia().getContentType(),
						value.getMedia().getLastModified(),
						value.getHash(),
						value.getImplicitUniqueConstraint(),
						value.getHashConstraint(),
						w200, w300,
				}),
				TYPE.getFeatures());

		assertEquals(128, value.getHash().getMinimumLength());
		assertEquals(128, value.getHash().getMaximumLength());
		assertEquals("SHA-512", value.getMessageDigestAlgorithm());
		assertTrue(value.isFinal());
		assertTrue(value.isMandatory());
		assertFalse(value.isAnnotationPresent(Computed.class));
		assertTrue(value.getMedia().isAnnotationPresent(Computed.class));
		assertTrue(value.getHash().isAnnotationPresent(Computed.class));
		assertEquals(
				value.getHash() + "=SHA-512(" + value.getMedia().getBody() + ")",
				value.hashMatchesIfSupported().toString());
		assertEquals(
				"!(" + value.getHash() + "=SHA-512(" + value.getMedia().getBody() + "))",
				value.hashDoesNotMatchIfSupported().toString());

		final HashConstraint hashConstraint = value.getHashConstraint();
		assertSame(value.getHash(), hashConstraint.getHash());
		assertSame("SHA-512", hashConstraint.getAlgorithm());
		assertSame(value.getMedia().getBody(), hashConstraint.getData());
	}

	@Test void testData()
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

	@Test void testUniqueness()
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
			assertEquals(value.getImplicitUniqueConstraint(), e.getFeature());
		}
	}

	@Test void testConditions()
	{
		final UniqueHashedMediaItem mediaItem = new UniqueHashedMediaItem(toValue(bytes6, "image/jpeg"));
		assertEquals(bytes6DigestHex, mediaItem.getHash());
		assertEquals(mediaItem, forHash(bytes6DigestHex));
		// no item created with this digest, test if result is null but no exception thrown
		final UniqueHashedMediaItem notExistingMediaItem = forHash(bytes4DigestHex);
		assertNull(notExistingMediaItem);
	}

	@Test void testGetOrCreate()throws IOException
	{
		final UniqueHashedMediaItem mediaItem =  getOrCreate(toValue(bytes8, "image/jpeg"));
		assertEquals(bytes8DigestHex, mediaItem.getHash());
		final UniqueHashedMediaItem mediaItem2 = getOrCreate(toValue(bytes8, "image/jpeg"));
		assertEquals(mediaItem, mediaItem2);
		final UniqueHashedMediaItem anotherItem = getOrCreate(toValue(bytes4, "image/jpeg"));
		assertNotEqualsAndHash(mediaItem, anotherItem);
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

	@Test void testGetOrCreateNull()throws IOException
	{
		assertEquals(null, getOrCreate(null));
	}

	@Test void testHashMatches()
	{
		assumeTrue(
				model.getSupportedDataHashAlgorithms().contains(value.getMessageDigestAlgorithm()),
				model.getSupportedDataHashAlgorithms() + " contains " + value.getMessageDigestAlgorithm()
		);
		assumeNoVault();

		final UniqueHashedMediaItem item1 = new UniqueHashedMediaItem(toValue(bytes4, "image/jpeg"));
		final UniqueHashedMediaItem item2 = new UniqueHashedMediaItem(toValue(bytes6, "image/jpeg"));

		assertSearch(asList(item1, item2), value.hashMatchesIfSupported());
		assertSearch(asList(), value.hashDoesNotMatchIfSupported());

		final UniqueHashedMediaItem itemX = new UniqueHashedMediaItem(
				value.getMedia().map(toValue(bytes6, "image/jpeg")),
				value.getHash() .map(brokenDigestHex));

		assertSearch(asList(item1, item2), value.hashMatchesIfSupported());
		assertSearch(asList(itemX), value.hashDoesNotMatchIfSupported());
	}

	private static void assumeNoVault()
	{
		if(value.getMedia().getBody().getVaultInfo()==null)
			return;

		try
		{
			TYPE.search(value.hashMatchesIfSupported());
			fail();
		}
		catch(final UnsupportedQueryException e)
		{
			assertEquals(
					"DataField UniqueHashedMediaItem.value-media-body does not support hashMatches as it has vault enabled",
					e.getMessage());
			assumeTrue(false, "no vault");
		}
	}

	private static void assertSearch(final List<UniqueHashedMediaItem> expected, final Condition condition)
	{
		assertEquals(expected, TYPE.search(condition, TYPE.getThis(), true));
	}

	private static final byte[] bytes4 = { -86, 122, -8, 23 };
	private static final byte[] bytes6 = { -97, 35, -126, 86, 19, -8 };
	private static final byte[] bytes8 = { -54, 104, -63, 23, 19, -45, 71, -23 };

	private static final String bytes4DigestHex = "0d2c0948019645cc742f284e9d75bbf904ff035d42ed77c43fcceb8ab0918c15be18e7f8debce86775e498ad8c6e5e2d9cad80969efd2d5370b8db076a2a7060";
	private static final String bytes6DigestHex = "13d33eddb02728843ad607e97f5fb3cf0e036079ed139b8d8393aacfef31d3219a34c39498b959e56f2b4b981034063f2d2b89f7d5e9b7ec4a44f6b401e9a4bb";
	private static final String bytes8DigestHex = "21aee30f8333ecfe85fef21741f312589a6572dbd5f5b28fc292e2ed7937b87409513a021e0b0714cc8d3df40d46b31014abe38aa9d7c934dd4905a81e90c4fe";
	private static final String brokenDigestHex = "abcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcdabcd";
}
