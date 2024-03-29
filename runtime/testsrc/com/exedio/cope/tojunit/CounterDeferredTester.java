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

package com.exedio.cope.tojunit;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;

public final class CounterDeferredTester extends MainRule
{
	private final String name;
	private final Tags tags;
	private final String meterMessage;

	public CounterDeferredTester(final Class<?> clazz, final String name, final Tags tags)
	{
		this(clazz.getName() + "." + name, tags);
	}

	public CounterDeferredTester(final String name, final Tags tags)
	{
		this.name = requireNonNull(name);
		this.tags = requireNonNull(tags);
		this.meterMessage = name + " " + tags;
	}


	private boolean initialized = false;
	private Counter counter;
	private double countBefore;

	@Override
	protected void before()
	{
		assertFalse(initialized, "initialized");
		counter = getMeter();
		countBefore = counter!=null ? counter.count() : 0;
		initialized = true;
	}

	public void assertCount(final int expected)
	{
		assertTrue(initialized, "not initialized");
		if(counter==null)
			counter = requireNonNull(getMeter(), meterMessage);

		assertEquals(expected, counter.count() - countBefore, meterMessage);
	}

	private Counter getMeter()
	{
		return (Counter)meter(name, tags);
	}

	private static Meter meter(final String name, final Tags tags)
	{
		Meter result = null;
		for(final Meter m : Metrics.globalRegistry.getMeters())
		{
			final Meter.Id id = m.getId();
			if(id.getName().equals(name) &&
				Tags.of(id.getTags()).equals(tags))
			{
				assertNull(result);
				result = m;
			}
		}
		return result;
	}
}
