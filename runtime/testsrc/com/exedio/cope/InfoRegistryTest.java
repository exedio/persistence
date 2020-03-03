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

import static com.exedio.cope.InfoRegistry.REGISTRY;
import static io.micrometer.core.instrument.Metrics.counter;
import static io.micrometer.core.instrument.Metrics.timer;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Meter;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
public class InfoRegistryTest
{
	@Test void testMinimum()
	{
		assertContains(true, counter("com.exedio.cope.DataField."));
	}
	@Test void testWithoutDot()
	{
		assertContains(false, counter("com.exedio.cope.DataField"));
	}
	@Test void testWithoutDotTimer()
	{
		assertContains(false, timer("com.exedio.cope.DataFieldx"));
	}
	@Test void testLongerNameCounter()
	{
		assertContains(true, counter("com.exedio.cope.DataField.longer.name.counter"));
	}
	@Test void testLongerNameTimer()
	{
		assertContains(true, timer("com.exedio.cope.DataField.longer.name.timer"));
	}
	@Test void testTagsCounter()
	{
		assertContains(true, counter("com.exedio.cope.DataField.tagsCounter", "typex", "counterx"));
	}
	@Test void testTagsTimer()
	{
		assertContains(true, timer("com.exedio.cope.DataField.tagsTimer", "typex", "timerx"));
	}

	private static void assertContains(final boolean expected, final Meter meter)
	{
		final List<Meter.Id> meters =
				REGISTRY.getMeters().stream().map(Meter::getId).collect(toList());
		assertEquals(expected, meters.contains(meter.getId()), meters.toString());
	}

	static
	{
		REGISTRY.getMeters(); // load class
	}
}
