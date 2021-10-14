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

import static com.exedio.cope.util.Check.requireNonEmpty;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;

/**
 * @see FeatureCounter
 */
final class MediaCounter extends MediaMeter<Counter>
{
	static MediaCounter counter(
			final String nameSuffix,
			final String description)
	{
		return new MediaCounter(nameSuffix, description, null, null);
	}

	static MediaCounter counter(
			final String nameSuffix,
			final String description,
			final String key, final String value)
	{
		return new MediaCounter(
				nameSuffix, description,
				requireNonEmpty(key, "key"),
				requireNonEmpty(value, "value"));
	}

	private MediaCounter(
			final String nameSuffix,
			final String description,
			final String key, final String value)
	{
		super(nameSuffix, description, key, value);
	}

	@Override
	LogMeter newLogMeter()
	{
		return new LogMeter(Metrics.counter(MediaCounter.class.getName()));
	}

	@Override
	MediaCounter newValue(final String value)
	{
		onNewValue(value);

		return new MediaCounter(nameSuffix, description, key, value);
	}

	@Override
	Counter onMount(final String name, final Tags tags, final String description, final MeterRegistry registry)
	{
		return Counter.builder(name).
				tags(tags).
				description(description).
				register(registry);
	}

	void increment()
	{
		meter.increment();
	}

	int get()
	{
		final double d = meter.count();
		final long l = Math.round(d);
		//noinspection FloatingPointEquality OK: tests backward conversion
		if(l!=d)
			throw new IllegalStateException(nameSuffix + '/' + d);
		return toIntMetrics(l);
	}

	private final class LogMeter extends MediaMeter<?>.LogMeter implements Counter
	{
		private final Counter back;

		private LogMeter(final Counter back)
		{
			this.back = back;
		}

		@Override
		public void increment(final double amount)
		{
			back.increment(amount);
			log();
		}

		@Override
		public double count()
		{
			// Needed because some counters are never mounted for certain MediaPath:
			// - redirectFrom if there are no @RedirectFrom
			// - preventUrlGuessing if there ist no @PreventUrlGuessing
			// - isNull if the MediaPath is mandatory
			return 0;
		}
	}
}
