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
import com.exedio.cope.util.CharSet;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FeatureCounter
{
	static MeterRegistry registry = Metrics.globalRegistry;

	static FeatureCounter counter(
			final String nameSuffix,
			final String description)
	{
		return new FeatureCounter(nameSuffix, description, null, null);
	}

	static FeatureCounter counter(
			final String nameSuffix,
			final String description,
			final String key, final String value)
	{
		return new FeatureCounter(
				nameSuffix, description,
				requireNonEmpty(key, "key"),
				requireNonEmpty(value, "value"));
	}

	private final String nameSuffix;
	private final String description;
	private final String key;
	private final String value;
	private Counter meter = new LogMeter(Metrics.counter(FeatureCounter.class.getName()));

	private FeatureCounter(
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

	FeatureCounter newValue(final String value)
	{
		if(key==null)
			throw new IllegalArgumentException("not allowed without key");
		if(this.value.equals(value))
			throw new IllegalArgumentException("value must be different");

		return new FeatureCounter(nameSuffix, description, key, requireNonEmpty(value, "value"));
	}

	static void onMount(
			final Feature feature,
			final FeatureCounter... meters)
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
			final FeatureCounter[] meters)
	{
		onMount((Class)featureClass, feature, meters);
	}

	static <F extends Feature> void onMount(
			final Class<F> featureClass,
			final F feature,
			final FeatureCounter... meters)
	{
		requireNonNull(featureClass, "featureClass");
		requireNonNull(feature, "feature");
		for(final FeatureCounter meter : meters)
			meter.onMount(featureClass, feature);
	}

	private <F extends Feature> void onMount(final Class<F> featureClass, final F feature)
	{
		if(!(meter instanceof LogMeter))
			throw new IllegalStateException("already mounted");

		final Tags tags = key!=null ? Tags.of(key, value) : Tags.empty();
		meter = Counter.builder(featureClass.getName() + '.' + nameSuffix).
				tags(tags.and(Tags.of("feature", feature.getID()))).
				description(description).
				register(registry);
	}

	void increment()
	{
		meter.increment();
	}

	private final class LogMeter implements Counter
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
			logger.error("unmounted {} {}", nameSuffix, description);
		}

		@Override
		public double count()
		{
			throw new NoSuchMethodError();
		}

		@Override
		public Id getId()
		{
			throw new NoSuchMethodError();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(FeatureCounter.class);
}
