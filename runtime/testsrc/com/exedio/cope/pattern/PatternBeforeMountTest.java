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

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Feature;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class PatternBeforeMountTest
{
	@Test void getSourceFeaturesGather()
	{
		final PatternTestPattern pattern = new PatternTestPattern();
		final Map<String,Feature> expected = new HashMap<>();
		expected.put("ownString", pattern.ownString);
		expected.put("ownInt", pattern.ownInt);
		assertEqualsUnmodifiable(expected, pattern.getSourceFeaturesGather());
	}

	@Test void getSourceFeatures()
	{
		try
		{
			new PatternTestPattern().getSourceFeatures();
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("getSourceFeatures can be called only after pattern is mounted, not before", e.getMessage());
		}
	}
}
