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

import static com.exedio.cope.tojunit.Assert.deserialize;
import static com.exedio.cope.tojunit.Assert.serialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
import com.exedio.cope.pattern.MediaPath.Locator;

public final class MediaLocatorAssert
{
	public static void assertLocator(
			final MediaPath feature,
			final String path,
			final Locator locator)
	{
		final Model model = feature.getType().getModel();

		// locator methods must work without transaction
		final Transaction tx = model.leaveTransaction();
		try
		{
			assertSame(feature, locator.getFeature());
			assertEquals(path, locator.getPath());
			assertEquals(path, locator.toString());

			final StringBuilder bf = new StringBuilder();
			locator.appendPath(bf);
			assertEquals(path, bf.toString());

			final Locator reserialized = reserialize(locator);
			assertSame(feature, reserialized.getFeature());
			assertEquals(path, reserialized.getPath());
			assertEquals(path, reserialized.toString());
		}
		finally
		{
			model.joinTransaction(tx);
		}
	}

	public static void assertLocator(
			final String expectedPath,
			final Locator actualLocator)
	{
		assertEquals(expectedPath, actualLocator!=null ? actualLocator.getPath() : null);

		if(actualLocator!=null)
			assertEquals(expectedPath, reserialize(actualLocator).getPath());
	}

	private static Locator reserialize(final Locator value)
	{
		final byte[] bytes = serialize(value);
		assertTrue(bytes.length<1000, String.valueOf(bytes.length));
		return (Locator)deserialize(bytes);
	}


	private MediaLocatorAssert()
	{
		// prevent instantiation
	}
}
