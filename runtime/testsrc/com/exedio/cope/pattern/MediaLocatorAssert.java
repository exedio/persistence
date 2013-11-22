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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.exedio.cope.Model;
import com.exedio.cope.Transaction;

public final class MediaLocatorAssert
{
	public static void assertLocator(
			final MediaPath feature,
			final String path,
			final MediaPath.Locator locator)
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
		}
		finally
		{
			model.joinTransaction(tx);
		}
	}

	public static void assertLocator(
			final String expectedPath,
			final MediaPath.Locator actualLocator)
	{
		assertEquals(expectedPath, actualLocator!=null ? actualLocator.getPath() : null);
	}


	private MediaLocatorAssert()
	{
		// prevent instantiation
	}
}
