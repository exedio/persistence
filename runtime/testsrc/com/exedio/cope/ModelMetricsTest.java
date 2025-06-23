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

import static com.exedio.cope.ModelMetrics.toEpoch;
import static io.micrometer.core.instrument.Tags.empty;
import static io.micrometer.core.instrument.Tags.of;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.Month.OCTOBER;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class ModelMetricsTest
{
	private static final Class<?> CLAZZ = ChangeListener.class;
	private static final Class<?> CLAZZ2 = DataField.class;

	@Test void test()
	{
		final ModelMetrics metrics = ModelMetricsNonConnected.create("myModelName").name(CLAZZ);
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
		assertEquals(tags.and(word + "Key", word + "Value", "model", "myModelName").stream().toList(), id.getTags());
		assertEquals(word + "Desc", id.getDescription());
	}


	@Test void testToEpochAroundZero()
	{
		assertEquals( 44.123456789, toEpoch(LocalDateTime.of(1970, JANUARY,   1,  0,  0, 44, 123_456_789).toInstant(UTC)));
		assertEquals(-44.123456789, toEpoch(LocalDateTime.of(1969, DECEMBER, 31, 23, 59, 15, 876_543_211).toInstant(UTC)));
		assertEquals(  0.0,         toEpoch(LocalDateTime.of(1970, JANUARY,   1,  0,  0,  0,           0).toInstant(UTC)));
		assertEquals(  0.000000001, toEpoch(LocalDateTime.of(1970, JANUARY,   1,  0,  0,  0,           1).toInstant(UTC)));
	}

	@Test void testToEpochNow()
	{
		assertEquals(1664928000.0,         toEpoch(LocalDateTime.of(2022, OCTOBER, 5, 0, 0, 0,           0).toInstant(UTC)));
		assertEquals(1664960400.0,         toEpoch(LocalDateTime.of(2022, OCTOBER, 5, 9, 0, 0,           0).toInstant(UTC)));
		assertEquals(1664960520.0,         toEpoch(LocalDateTime.of(2022, OCTOBER, 5, 9, 2, 0,           0).toInstant(UTC)));
		assertEquals(1664960528.0,         toEpoch(LocalDateTime.of(2022, OCTOBER, 5, 9, 2, 8,           0).toInstant(UTC)));
		assertEquals(1664960528.987654321, toEpoch(LocalDateTime.of(2022, OCTOBER, 5, 9, 2, 8, 987_654_321).toInstant(UTC)));
	}
}
