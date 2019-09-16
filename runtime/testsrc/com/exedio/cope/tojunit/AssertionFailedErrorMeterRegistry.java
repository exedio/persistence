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

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import org.opentest4j.AssertionFailedError;

public class AssertionFailedErrorMeterRegistry extends MeterRegistry
{
	public AssertionFailedErrorMeterRegistry(final Clock clock)
	{
		super(clock);
	}
	@Override
	protected <T> Gauge newGauge(final Meter.Id id, final T obj, final ToDoubleFunction<T> valueFunction)
	{
		throw new AssertionFailedError();
	}
	@Override
	protected Counter newCounter(final Meter.Id id)
	{
		throw new AssertionFailedError();
	}
	@Override
	protected LongTaskTimer newLongTaskTimer(final Meter.Id id)
	{
		throw new AssertionFailedError();
	}
	@Override
	protected Timer newTimer(final Meter.Id id, final DistributionStatisticConfig distributionStatisticConfig, final PauseDetector pauseDetector)
	{
		throw new AssertionFailedError();
	}
	@Override
	protected DistributionSummary newDistributionSummary(final Meter.Id id, final DistributionStatisticConfig distributionStatisticConfig, final double scale)
	{
		throw new AssertionFailedError();
	}
	@Override
	protected Meter newMeter(final Meter.Id id, final Meter.Type type, final Iterable<Measurement> measurements)
	{
		throw new AssertionFailedError();
	}
	@Override
	protected <T> FunctionTimer newFunctionTimer(final Meter.Id id, final T obj, final ToLongFunction<T> countFunction, final ToDoubleFunction<T> totalTimeFunction, final TimeUnit totalTimeFunctionUnit)
	{
		throw new AssertionFailedError();
	}
	@Override
	protected <T> FunctionCounter newFunctionCounter(final Meter.Id id, final T obj, final ToDoubleFunction<T> countFunction)
	{
		throw new AssertionFailedError();
	}
	@Override
	protected TimeUnit getBaseTimeUnit()
	{
		throw new AssertionFailedError();
	}
	@Override
	protected DistributionStatisticConfig defaultHistogramConfig()
	{
		throw new AssertionFailedError();
	}
}
