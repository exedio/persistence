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

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;

public final class PrometheusMeterRegistrar
{
	public static void load()
	{
		// just for executing the static block
	}

	private static final PrometheusMeterRegistry PROMETHEUS_REGISTRY = new PrometheusMeterRegistry(
			PrometheusConfig.DEFAULT,
			CollectorRegistry.defaultRegistry,
			Clock.SYSTEM);

	static
	{
		Metrics.globalRegistry.add(PROMETHEUS_REGISTRY);
	}

	public static Iterable<Meter> getMeters()
	{
		return PROMETHEUS_REGISTRY.getMeters();
	}

	public static Meter meter(
			final Class<?> nameClass,
			final String nameSuffix,
			final Tags tags)
	{
		return meter(
				PROMETHEUS_REGISTRY,
				nameClass, nameSuffix, tags);
	}

	static Meter meterCope(
			final Class<?> nameClass,
			final String nameSuffix,
			final Tags tags)
	{
		assertTrue(asList(
				// Model.class and Sequence.class not needed, as there are gauges only, no counters or timers
				Transaction.class,
				DataField.class,
				ChangeListener.class,
				Cluster.class,
				ItemCache.class,
				QueryCache.class
				).contains(nameClass), nameClass.getName());
		return meter(
				InfoRegistry.REGISTRY,
				nameClass, nameSuffix, tags);
	}

	private static Meter meter(
			final MeterRegistry registry,
			final Class<?> nameClass,
			final String nameSuffix,
			final Tags tags)
	{
		assertTrue(Metrics.globalRegistry.getRegistries().contains(registry));
		final String name = nameClass.getName() + "." + nameSuffix;
		Meter result = null;
		for(final Meter m : registry.getMeters())
		{
			final Meter.Id id = m.getId();
			if(id.getName().equals(name) &&
				Tags.of(id.getTags()).equals(tags))
			{
				assertNotNull(      id.getDescription(), "description: " + name);
				assertNotEquals("", id.getDescription(), "description: " + name);
				assertNull(result);
				result = m;
			}
		}
		assertNotNull(result, "not found: >" + name + "< " + tags);
		return result;
	}


	static Tags tag(final Model model)
	{
		return Tags.of("model", model.toString());
	}

	static Tags tag(final Type<?> type)
	{
		return tag(type.getModel()).and("type", type.getID());
	}

	public static Tags tag(final Feature feature)
	{
		return Tags.of("feature", feature.getID());
	}


	private PrometheusMeterRegistrar()
	{
		// prevent instantiation
	}
}
