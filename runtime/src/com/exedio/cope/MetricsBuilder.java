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

import com.exedio.cope.util.CharSet;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.ToDoubleFunction;

final class MetricsBuilder
{
	private final String nameClass;
	private final Tags tags;

	MetricsBuilder(
			final Class<?> nameClass,
			final Model model)
	{
		this(nameClass, Tags.of("model", model.toString()));
	}

	MetricsBuilder(
			final Class<?> nameClass,
			final Tags tags)
	{
		this.nameClass = nameClass.getName();
		this.tags = tags;
	}

	private String name(final String suffix)
	{
		final int pos = SUFFIX_CHARSET.indexOfNotContains(suffix);
		if(pos>=0)
			throw new IllegalArgumentException(
					"character not allowed at position " + pos + ": >" + suffix + "<");

		return nameClass + '.' + suffix;
	}

	private static final CharSet SUFFIX_CHARSET = new CharSet('.', '.', '0', '9', 'A', 'Z', 'a', 'z');

	Counter counter(
			final String nameSuffix,
			final String description,
			final Tags tags)
	{
		return InfoRegistry.counter(name(nameSuffix)).
				description(description).
				tags(this.tags.and(tags)).
				register(Metrics.globalRegistry);
	}

	Timer timer(
			final String nameSuffix,
			final String description,
			final Tags tags)
	{
		return InfoRegistry.timer(name(nameSuffix)).
				description(description).
				tags(this.tags.and(tags)).
				register(Metrics.globalRegistry);
	}

	<T> void gauge(
			final T obj,
			final ToDoubleFunction<T> f,
			final String nameSuffix,
			final String description)
	{
		gauge(obj, f, nameSuffix, description, Tags.empty());
	}

	<T> void gauge(
			final T obj,
			final ToDoubleFunction<T> f,
			final String nameSuffix,
			final String description,
			final Tags tags)
	{
		Gauge.builder(name(nameSuffix), obj, f).
				description(description).
				tags(this.tags.and(tags)).
				register(Metrics.globalRegistry);
	}


	static Tags tag(final String key, final Instant value)
	{
		return Tags.of(key, DateTimeFormatter.ISO_INSTANT.format(value));
	}
}
