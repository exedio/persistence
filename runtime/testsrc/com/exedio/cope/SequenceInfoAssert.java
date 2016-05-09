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

package com.exedio.cope;

import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SequenceInfoAssert
{
	public static void assertInfo(final Type<?> type, final int count, final int first, final int last, final SequenceInfo info)
	{
		assertInfoAny(type.getThis(), 0, 0, Integer.MAX_VALUE, count, first, last, info);
	}

	public static void assertInfo(final IntegerField feature, final int count, final int first, final int last, final SequenceInfo info)
	{
		assertInfoAny(feature, feature.getDefaultNextStart().intValue(), feature.getMinimum(), feature.getMaximum(), count, first, last, info);
	}

	public static void assertInfo(final Sequence feature, final int count, final int first, final int last, final SequenceInfo info)
	{
		assertInfoAny(feature, feature.getStart(), feature.getStart(), feature.getEnd(), count, first, last, info);
	}

	static void assertInfoAny(final Feature feature, final int start, final int minimum, final int maximum, final int count, final int first, final int last, final SequenceInfo info)
	{
		assertSame("feature", feature, info.getFeature());
		assertEquals("start", start, info.getStart());
		assertEquals("minimum", minimum, info.getMinimum());
		assertEquals("maximum", maximum, info.getMaximum());
		assertEquals("count", count, info.getCount());
		assertTrue("known", info.isKnown());
		assertEquals("first", first, info.getFirst());
		assertEquals("last", last, info.getLast());
	}

	public static void assertInfo(final Type<?> type, final SequenceInfo info)
	{
		assertInfoAny(type.getThis(), 0, 0, Integer.MAX_VALUE, info);
	}

	public static void assertInfo(final IntegerField feature, final SequenceInfo info)
	{
		assertInfoAny(feature, feature.getDefaultNextStart().intValue(), feature.getMinimum(), feature.getMaximum(), info);
	}

	public static void assertInfo(final Sequence feature, final SequenceInfo info)
	{
		assertInfoAny(feature, feature.getStart(), feature.getStart(), feature.getEnd(), info);
	}

	static void assertInfoAny(final Feature feature, final int start, final int minimum, final int maximum, final SequenceInfo info)
	{
		assertSame("feature", feature, info.getFeature());
		assertEquals("start", start, info.getStart());
		assertEquals("minimum", minimum, info.getMinimum());
		assertEquals("maximum", maximum, info.getMaximum());
		assertEquals("count", 0, info.getCount());
		assertFalse("known", info.isKnown());
		try
		{
			info.getFirst();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not known", e.getMessage());
		}
		try
		{
			info.getLast();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("not known", e.getMessage());
		}
	}

	public static void assertInfo(final List<SequenceInfo> actual, final Feature... expected)
	{
		assertUnmodifiable(actual);
		final ArrayList<Feature> actualTypes = new ArrayList<>();
		for(final SequenceInfo i : actual)
			actualTypes.add(i.getFeature());
		assertEquals(Arrays.asList(expected), actualTypes);
	}

	private SequenceInfoAssert()
	{
		// prevent instantiation
	}
}
