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
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Feature;
import com.exedio.cope.misc.MicrometerUtil;
import com.exedio.cope.util.CharSet;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import java.lang.reflect.Modifier;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FeatureTimer
{
	static MeterRegistry registry = Metrics.globalRegistry;

	static FeatureTimer timer(
			final String nameSuffix,
			final String description)
	{
		return new FeatureTimer(nameSuffix, description, null, null);
	}

	static FeatureTimer timer(
			final String nameSuffix,
			final String description,
			final String key, final String value)
	{
		return new FeatureTimer(
				nameSuffix, description,
				requireNonEmpty(key, "key"),
				requireNonEmpty(value, "value"));
	}

	private final String nameSuffix;
	private final String description;
	private final String key;
	private final String value;
	private Timer meter = new LogMeter(Metrics.timer(FeatureTimer.class.getName()));

	private FeatureTimer(
			final String nameSuffix,
			final String description,
			final String key, final String value)
	{
		this.nameSuffix = requireNonEmpty(nameSuffix, "nameSuffix");
		this.description = requireNonEmpty(description, "description");
		this.key = key;
		this.value = value;

		{
			final int pos = CharSet.ALPHA_NUMERIC.indexOfNotContains(nameSuffix);
			if(pos>=0)
				throw new IllegalArgumentException(
						"character not allowed at position " + pos + ": >" + nameSuffix + "<");
		}
	}

	FeatureTimer newValue(final String value)
	{
		if(key==null)
			throw new IllegalArgumentException("not allowed without key");
		if(this.value.equals(value))
			throw new IllegalArgumentException("value must be different");

		return new FeatureTimer(nameSuffix, description, key, requireNonEmpty(value, "value"));
	}

	static void onMount(
			final Feature feature,
			final FeatureTimer... meters)
	{
		requireNonNull(feature, "feature");
		final Class<?> featureClass = feature.getClass();
		if(!Modifier.isFinal(featureClass.getModifiers()))
			throw new IllegalArgumentException("not final: " + featureClass + ' ' + feature);

		onMountInternal(featureClass, feature, meters);
	}

	@SuppressWarnings("unchecked")
	private static void onMountInternal(
			final Class<?> featureClass,
			final Feature feature,
			final FeatureTimer[] meters)
	{
		onMount((Class)featureClass, feature, meters);
	}

	static <F extends Feature> void onMount(
			final Class<F> featureClass,
			final F feature,
			final FeatureTimer... meters)
	{
		requireNonNull(featureClass, "featureClass");
		requireNonNull(feature, "feature");
		for(final FeatureTimer meter : meters)
			meter.onMount(featureClass, feature);
	}

	private <F extends Feature> void onMount(final Class<F> featureClass, final F feature)
	{
		if(!(meter instanceof LogMeter))
			throw new IllegalStateException("already mounted");

		final Tags tags = key!=null ? Tags.of(key, value) : Tags.empty();
		meter = Timer.builder(featureClass.getName() + '.' + nameSuffix).
				tags(tags.and(Tags.of("feature", feature.getID()))).
				description(description).
				register(registry);
	}

	void stop(final Timer.Sample sample)
	{
		sample.stop(meter);
	}

	long stopMillies(final Timer.Sample sample)
	{
		return MicrometerUtil.toMillies(meter, sample);
	}

	private final class LogMeter implements Timer
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
			logger.error("unmounted {} {}", nameSuffix, description);
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

		@Override
		public Id getId()
		{
			throw new NoSuchMethodError();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(FeatureTimer.class);
}
