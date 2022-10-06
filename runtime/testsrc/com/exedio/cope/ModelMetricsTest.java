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

import static io.micrometer.core.instrument.Tags.empty;
import static io.micrometer.core.instrument.Tags.of;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Test;

public class ModelMetricsTest
{
	private static final Class<?> CLAZZ = ChangeListener.class;
	private static final Class<?> CLAZZ2 = DataField.class;

	@Test void test()
	{
		final ModelMetrics metrics = new ModelMetrics(null, "myModelName").name(CLAZZ);
		assertEquals("myModelName", metrics.modelName);
		assertIt("counter", CLAZZ, empty(), metrics.counter("counterNameSuffix", "counterDesc", of("counterKey", "counterValue")));
		assertIt("timer",   CLAZZ, empty(), metrics.timer(  "timerNameSuffix",   "timerDesc",   of("timerKey",   "timerValue")));

		final ModelMetrics metricsName = metrics.name(CLAZZ2);
		assertEquals("myModelName", metricsName.modelName);
		assertIt("counter2", CLAZZ2, empty(), metricsName.counter("counter2NameSuffix", "counter2Desc", of("counter2Key", "counter2Value")));
		assertIt("timer2",   CLAZZ2, empty(), metricsName.timer(  "timer2NameSuffix",   "timer2Desc",   of("timer2Key",   "timer2Value")));

		final ModelMetrics metricsOneTag = metrics.tag("oneKey", "oneValue");
		assertEquals("myModelName", metricsOneTag.modelName);
		assertIt("counter2", CLAZZ, of("oneKey", "oneValue"), metricsOneTag.counter("counter2NameSuffix", "counter2Desc", of("counter2Key", "counter2Value")));
		assertIt("timer2",   CLAZZ, of("oneKey", "oneValue"), metricsOneTag.timer(  "timer2NameSuffix",   "timer2Desc",   of("timer2Key",   "timer2Value")));

		final ModelMetrics metricsTwoTags = metrics.tag(of("twoKey1", "twoValue1", "twoKey2", "twoValue2"));
		assertEquals("myModelName", metricsTwoTags.modelName);
		assertIt("counter2", CLAZZ, of("twoKey1", "twoValue1", "twoKey2", "twoValue2"), metricsTwoTags.counter("counter2NameSuffix", "counter2Desc", of("counter2Key", "counter2Value")));
		assertIt("timer2",   CLAZZ, of("twoKey1", "twoValue1", "twoKey2", "twoValue2"), metricsTwoTags.timer(  "timer2NameSuffix",   "timer2Desc",   of("timer2Key",   "timer2Value")));
	}

	private static void assertIt(final String word, final Class<?> nameClass, final Tags tags, final Meter meter)
	{
		final Meter.Id id = meter.getId();
		assertEquals(nameClass.getName() + "." + word + "NameSuffix", id.getName());
		assertEquals(tags.and(word + "Key", word + "Value", "model", "myModelName").stream().collect(toList()), id.getTags());
		assertEquals(word + "Desc", id.getDescription());
	}
}
