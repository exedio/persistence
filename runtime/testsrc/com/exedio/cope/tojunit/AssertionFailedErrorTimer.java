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

package com.exedio.cope.tojunit;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.opentest4j.AssertionFailedError;

public class AssertionFailedErrorTimer implements Timer
{
	@Override
	public void record(final long amount, final TimeUnit unit)
	{
		throw new AssertionFailedError();
	}

	@Override
	public void record(final Duration duration)
	{
		throw new AssertionFailedError();
	}

	@Override
	public <T> T record(final Supplier<T> f)
	{
		throw new AssertionFailedError();
	}

	@Override
	@SuppressWarnings("RedundantThrows") // OK: for subclasses
	public <T> T recordCallable(final Callable<T> f) throws Exception
	{
		throw new AssertionFailedError();
	}

	@Override
	public void record(final Runnable f)
	{
		throw new AssertionFailedError();
	}

	@Override
	public Runnable wrap(final Runnable f)
	{
		throw new AssertionFailedError();
	}

	@Override
	public <T> Callable<T> wrap(final Callable<T> f)
	{
		throw new AssertionFailedError();
	}

	@Override
	public <T> Supplier<T> wrap(final Supplier<T> f)
	{
		throw new AssertionFailedError();
	}

	@Override
	public long count()
	{
		throw new AssertionFailedError();
	}

	@Override
	public double totalTime(final TimeUnit unit)
	{
		throw new AssertionFailedError();
	}

	@Override
	public double mean(final TimeUnit unit)
	{
		throw new AssertionFailedError();
	}

	@Override
	public double max(final TimeUnit unit)
	{
		throw new AssertionFailedError();
	}

	@Override
	public Iterable<Measurement> measure()
	{
		throw new AssertionFailedError();
	}

	@Override
	public <T> T match(final Function<Gauge, T> visitGauge,
							 final Function<Counter, T> visitCounter,
							 final Function<Timer, T> visitTimer,
							 final Function<DistributionSummary, T> visitSummary,
							 final Function<LongTaskTimer, T> visitLongTaskTimer,
							 final Function<TimeGauge, T> visitTimeGauge,
							 final Function<FunctionCounter, T> visitFunctionCounter,
							 final Function<FunctionTimer, T> visitFunctionTimer,
							 final Function<Meter, T> visitMeter)
	{
		throw new AssertionFailedError();
	}

	@Override
	public void use(final Consumer<Gauge> visitGauge,
						 final Consumer<Counter> visitCounter,
						 final Consumer<Timer> visitTimer,
						 final Consumer<DistributionSummary> visitSummary,
						 final Consumer<LongTaskTimer> visitLongTaskTimer,
						 final Consumer<TimeGauge> visitTimeGauge,
						 final Consumer<FunctionCounter> visitFunctionCounter,
						 final Consumer<FunctionTimer> visitFunctionTimer,
						 final Consumer<Meter> visitMeter)
	{
		throw new AssertionFailedError();
	}

	@Override
	public void close()
	{
		throw new AssertionFailedError();
	}

	@Override
	@SuppressWarnings("deprecation")
	public double histogramCountAtValue(final long valueNanos)
	{
		throw new AssertionFailedError();
	}

	@Override
	@SuppressWarnings("deprecation")
	public double percentile(final double percentile, final TimeUnit unit)
	{
		throw new AssertionFailedError();
	}

	@Override
	public TimeUnit baseTimeUnit()
	{
		throw new AssertionFailedError();
	}

	@Override
	public HistogramSnapshot takeSnapshot()
	{
		throw new AssertionFailedError();
	}

	@Override
	@SuppressWarnings("deprecation")
	public HistogramSnapshot takeSnapshot(final boolean supportsAggregablePercentiles)
	{
		throw new AssertionFailedError();
	}

	@Override
	public Id getId()
	{
		throw new AssertionFailedError();
	}
}
