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

import static com.exedio.cope.PrometheusMeterRegistrar.meter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Feature;
import com.exedio.cope.tojunit.MainRule;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

final class FeatureTimerTester extends MainRule
{
	private final Timer timer;
	private final String timerMessage;

	FeatureTimerTester(
			final Feature feature,
			final String nameSuffix)
	{
		this(feature.getClass(), nameSuffix, Tags.of("feature", feature.getID()));
	}

	FeatureTimerTester(
			final Feature feature,
			final String nameSuffix,
			final String key, final String value)
	{
		this(feature.getClass(), nameSuffix, Tags.of("feature", feature.getID(), key, value));
	}

	private FeatureTimerTester(
			final Class<?> nameClass,
			final String nameSuffix,
			final Tags tags)
	{
		timer = (Timer)meter(nameClass, nameSuffix, tags);
		timerMessage = timer.getId().toString();
	}



	private boolean initialized = false;
	private double lastActualCount;

	@Override
	protected void before()
	{
		assertFalse(initialized, "initialized");
		lastActualCount = timer.count();
		initialized = true;
	}

	void assertCount(final int expected)
	{
		assertTrue(initialized, "not initialized");
		final double actualCount = timer.count();
		assertEquals(expected, actualCount - lastActualCount, timerMessage);
		lastActualCount = actualCount;
	}
}
