package com.exedio.cope.pattern;

import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.Feature;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class PatternBeforeMountTest
{
	@Test public void getSourceFeaturesGather()
	{
		final PatternTestPattern pattern = new PatternTestPattern();
		final Map<String,Feature> expected = new HashMap<>();
		expected.put("ownString", pattern.ownString);
		expected.put("ownInt", pattern.ownInt);
		assertEqualsUnmodifiable(expected, pattern.getSourceFeaturesGather());
	}

	@Test public void getSourceFeatures()
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
