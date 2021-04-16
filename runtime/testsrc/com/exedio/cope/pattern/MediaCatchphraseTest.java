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

import static com.exedio.cope.pattern.MediaCatchPhraseItem.TYPE;
import static com.exedio.cope.pattern.MediaCatchPhraseSuperItem.feature;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class MediaCatchphraseTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(MediaCatchPhraseSuperItem.TYPE, TYPE);

	static
	{
		MODEL.enableSerialization(MediaCatchphraseTest.class, "MODEL");
	}

	public MediaCatchphraseTest()
	{
		super(MODEL);
	}

	private MediaCatchPhraseItem wrong, normal, all, single, empty, nulL;
	private MediaCatchPhraseSuperItem none;

	@BeforeEach void setUp()
	{
		normal = new MediaCatchPhraseItem("normal");
		all    = new MediaCatchPhraseItem("all01289ABCYZabcyz-_");
		single = new MediaCatchPhraseItem("S");
		empty  = new MediaCatchPhraseItem("");
		nulL   = new MediaCatchPhraseItem(null);
		none   = new MediaCatchPhraseSuperItem();

		wrong  = new MediaCatchPhraseItem("wrong/phrase");
	}

	@Test void testIt()
	{
		assertIt("MediaCatchPhraseSuperItem/feature/", normal, "/normal");
		assertIt("MediaCatchPhraseSuperItem/feature/", all,    "/all01289ABCYZabcyz-_");
		assertIt("MediaCatchPhraseSuperItem/feature/", single, "/S"     );
		assertIt("MediaCatchPhraseSuperItem/feature/", empty,  ""       );
		assertIt("MediaCatchPhraseSuperItem/feature/", nulL,   ""       );
		assertIt("MediaCatchPhraseSuperItem/feature/", none,   ""       );

		try
		{
			wrong.getFeatureLocator();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"illegal catchphrase on " + wrong + " for MediaCatchPhraseSuperItem.feature: >wrong/phrase< at position 5",
					e.getMessage());
		}
	}

	private static void assertIt(final String prefix, final MediaCatchPhraseSuperItem item, final String postfix)
	{
		assertLocator(feature, prefix + item.getCopeID() + postfix, item.getFeatureLocator());
	}
}
