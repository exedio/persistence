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

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @see FeatureTimer
 */
final class MediaTimer extends MediaMeter<Timer>
{
	static MediaTimer timer(
			final String nameSuffix,
			final String description)
	{
		return new MediaTimer(nameSuffix, description, null, null);
	}

	static MediaTimer timer(
			final String nameSuffix,
			final String description,
			final String key, final String value)
	{
		return new MediaTimer(
				nameSuffix, description,
				requireNonEmpty(key, "key"),
				requireNonEmpty(value, "value"));
	}

	private MediaTimer(
			final String nameSuffix,
			final String description,
			final String key, final String value)
	{
		super(nameSuffix, description, key, value);
	}

	@Override
	LogMeter newLogMeter()
	{
		return new LogMeter(Metrics.timer(MediaTimer.class.getName()));
	}

	@Override
	MediaTimer newValue(final String value)
	{
		onNewValue(value);

		return new MediaTimer(nameSuffix, description, key, value);
	}

	@Override
	Timer onMount(final String name, final Tags tags, final String description, final MeterRegistry registry)
	{
		return Timer.builder(name).
				tags(tags).
				description(description).
				register(registry);
	}

	void record(final long amount, final TimeUnit unit)
	{
		meter.record(amount, unit);
	}

	void stop(final Timer.Sample sample)
	{
		sample.stop(meter);
	}

	long get()
	{
		return meter.count();
	}

	private final class LogMeter extends MediaMeter<?>.LogMeter implements Timer
	{
		private final Timer back;

		private LogMeter(final Timer back)
		{
			this.back = back;
		}

		@Override
		public void record(final long amount, final TimeUnit unit)
		{
			back.record(amount, unit);
			log();
		}

		@Override
		public <T> T record(final Supplier<T> f)
		{
			throw new NoSuchMethodError();
		}

		@Override
		public <T> T recordCallable(final Callable<T> f)
		{
			throw new NoSuchMethodError();
		}

		@Override
		public void record(final Runnable f)
		{
			throw new NoSuchMethodError();
		}

		@Override
		public long count()
		{
			throw new NoSuchMethodError();
		}

		@Override
		public double totalTime(final TimeUnit unit)
		{
			throw new NoSuchMethodError();
		}

		@Override
		public double max(final TimeUnit unit)
		{
			throw new NoSuchMethodError();
		}

		@Override
		public TimeUnit baseTimeUnit()
		{
			throw new NoSuchMethodError();
		}

		@Override
		public HistogramSnapshot takeSnapshot()
		{
			throw new NoSuchMethodError();
		}
	}
}
