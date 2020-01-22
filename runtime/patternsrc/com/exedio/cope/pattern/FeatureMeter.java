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
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class FeatureMeter<M extends Meter>
{
	static MeterRegistry registry = Metrics.globalRegistry;

	final String nameSuffix;
	final String description;
	final String key;
	final String value;
	M meter;

	FeatureMeter(
			final String nameSuffix,
			final String description,
			final String key, final String value)
	{
		this.meter = newLogMeter();
		this.nameSuffix = requireNonEmpty(nameSuffix, "nameSuffix");
		this.description = requireNonEmpty(description, "description");
		this.key = key;
		this.value = value;

		//noinspection UnnecessarilyQualifiedInnerClassAccess OK: bug in idea
		if(!(meter instanceof FeatureMeter.LogMeter))
			throw new IllegalArgumentException(meter.toString());

		{
			final int pos = CharSet.ALPHA_NUMERIC.indexOfNotContains(nameSuffix);
			if(pos>=0)
				throw new IllegalArgumentException(
						"character not allowed at position " + pos + ": >" + nameSuffix + "<");
		}
	}

	abstract M newLogMeter();

	void onNewValue(final String value)
	{
		if(key==null)
			throw new IllegalArgumentException("not allowed without key");
		if(this.value.equals(value))
			throw new IllegalArgumentException("value must be different");
		requireNonEmpty(value, "value");
	}

	@SuppressWarnings("unused") // OK: requires subclasses to have this method
	abstract FeatureMeter<M> newValue(String value);

	static void onMount(
			final Feature feature,
			final FeatureMeter<?>... meters)
	{
		requireNonNull(feature, "feature");
		final Class<? extends Feature> featureClass = feature.getClass();
		if(!Modifier.isFinal(featureClass.getModifiers()))
			throw new IllegalArgumentException("not final: " + featureClass + ' ' + feature);

		for(final FeatureMeter<?> meter : meters)
			meter.onMount(featureClass, feature);
	}

	private void onMount(final Class<? extends Feature> featureClass, final Feature feature)
	{
		//noinspection UnnecessarilyQualifiedInnerClassAccess OK: bug in idea
		if(!(meter instanceof FeatureMeter.LogMeter))
			throw new IllegalStateException("already mounted");

		final Tags tags = key!=null ? Tags.of(key, value) : Tags.empty();
		meter = onMount(
				featureClass.getName() + '.' + nameSuffix,
				tags.and(Tags.of("feature", feature.getID())),
				description,
				registry);
	}

	abstract M onMount(String name, Tags tags, String description, MeterRegistry registry);

	abstract class LogMeter implements Meter
	{
		final void log()
		{
			logger.error("unmounted {} {}", nameSuffix, description);
		}

		@Override
		public final Iterable<Measurement> measure()
		{
			throw new NoSuchMethodError();
		}

		@Override
		public final Id getId()
		{
			throw new NoSuchMethodError();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(FeatureMeter.class);
}
