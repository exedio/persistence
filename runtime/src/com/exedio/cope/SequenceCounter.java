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

import static java.lang.Long.MAX_VALUE;
import static java.util.Objects.requireNonNull;

import io.micrometer.core.instrument.Tags;
import java.util.function.ToDoubleFunction;

@SuppressWarnings("VolatileLongOrDoubleField")
final class SequenceCounter
{
	private final Feature feature;
	private final long start;
	private final long minimum;
	private final long maximum;

	SequenceCounter(final Feature feature, final long start, final long minimum, final long maximum)
	{
		if(start<minimum || start>maximum)
			throw new IllegalArgumentException(String.valueOf(start) + '/' + minimum + '/' + maximum);

		this.feature = requireNonNull(feature);
		this.start = start;
		this.minimum = minimum;
		this.maximum = maximum;
	}

	private volatile long count = 0;
	private volatile long first = MAX_VALUE;
	private volatile double last = Double.NaN;

	void next(final long result)
	{
		if(result<minimum || result>maximum)
			throw new IllegalStateException(
					"sequence overflow to " + result + " in " + feature +
					" limited to " + minimum + ',' + maximum);
		//noinspection NonAtomicOperationOnVolatileField
		if((count++)==0)
			first = result;
		last = result;
	}

	void flush()
	{
		count = 0;
		first = MAX_VALUE;
		last  = Double.NaN;
	}

	SequenceInfo getInfo()
	{
		final double last = this.last;
		return
			!Double.isNaN(last)
			? new SequenceInfo(feature, start, minimum, maximum, count, first, last)
			: new SequenceInfo(feature, start, minimum, maximum);
	}

	void onModelNameSet(final ModelMetrics metricsTemplate)
	{
		final Metrics metrics = new Metrics(metricsTemplate, this);
		metrics.value(c -> c.start,   "start", "The initial value this sequence started with.");
		metrics.value(c -> c.maximum, "end",   "The largest value possible value this sequence can produce.");
		metrics.value(c -> c.last,    "last",  "The last value produced by this sequence. None, if no value was produced since last restart.");
	}

	private static final class Metrics
	{
		final ModelMetrics back;
		final SequenceCounter counter;

		Metrics(final ModelMetrics metricsTemplate, final SequenceCounter counter)
		{
			this.back = metricsTemplate.name(Sequence.class).tag(counter.feature);
			this.counter = counter;
		}

		void value(
				final ToDoubleFunction<SequenceCounter> f,
				final String type,
				final String description)
		{
			back.gauge(counter, f, "value", description, Tags.of("type", type));
		}
	}
}
