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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.MicrometerUtil.toMillies;
import static io.micrometer.core.instrument.Timer.start;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.tojunit.AssertionFailedErrorClock;
import com.exedio.cope.tojunit.AssertionFailedErrorMeterRegistry;
import com.exedio.cope.tojunit.AssertionFailedErrorTimer;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class MicrometerUtilTest
{
	@Test void testNormal()
	{
		final MockClock clock = new MockClock(55, 77);
		assertEquals(0, toMillies(timer, start(clock)));
		clock.assertEmpty();
		registry.assertIt("record " + timer + " 22ns");
		log.assertEmpty();
	}

	@Test void testZero()
	{
		final MockClock clock = new MockClock(55, 55);
		assertEquals(0, toMillies(timer, start(clock)));
		clock.assertEmpty();
		registry.assertIt("record " + timer + " 0ns");
		log.assertEmpty();
	}

	@Test void test049()
	{
		final MockClock clock = new MockClock(90_000_000, 90_500_000 - 1);
		assertEquals(0, toMillies(timer, start(clock)));
		clock.assertEmpty();
		registry.assertIt("record " + timer + " 499999ns");
		log.assertEmpty();
	}

	@Test void test050()
	{
		final MockClock clock = new MockClock(90_000_000, 90_500_000);
		assertEquals(1, toMillies(timer, start(clock)));
		clock.assertEmpty();
		registry.assertIt("record " + timer + " 500000ns");
		log.assertEmpty();
	}

	@Test void test051()
	{
		final MockClock clock = new MockClock(90_000_000, 90_500_000 + 1);
		assertEquals(1, toMillies(timer, start(clock)));
		clock.assertEmpty();
		registry.assertIt("record " + timer + " 500001ns");
		log.assertEmpty();
	}

	@Test void test149()
	{
		final MockClock clock = new MockClock(90_000_000, 91_500_000 - 1);
		assertEquals(1, toMillies(timer, start(clock)));
		clock.assertEmpty();
		registry.assertIt("record " + timer + " 1499999ns");
		log.assertEmpty();
	}

	@Test void test150()
	{
		final MockClock clock = new MockClock(90_000_000, 91_500_000);
		assertEquals(2, toMillies(timer, start(clock)));
		clock.assertEmpty();
		registry.assertIt("record " + timer + " 1500000ns");
		log.assertEmpty();
	}

	@Test void test151()
	{
		final MockClock clock = new MockClock(90_000_000, 91_500_000 + 1);
		assertEquals(2, toMillies(timer, start(clock)));
		clock.assertEmpty();
		registry.assertIt("record " + timer + " 1500001ns");
		log.assertEmpty();
	}

	@Test void testBackwards()
	{
		final MockClock clock = new MockClock(55, 54);
		assertEquals(0, toMillies(timer, start(clock)));
		clock.assertEmpty();
		registry.assertIt("record " + timer + " -1ns");
		log.assertError("backwards nanos " + timer + ": -1");
		log.assertEmpty();
	}


	private static final MockMeterRegistry registry = new MockMeterRegistry();
	private static final Timer timer = Timer.builder("name").register(registry);
	private final LogRule log = new LogRule(MicrometerUtil.class);

	private static final class MockMeterRegistry extends AssertionFailedErrorMeterRegistry
	{
		private final ArrayList<String> actual = new ArrayList<>();

		MockMeterRegistry()
		{
			super(new AssertionFailedErrorClock());
		}

		@Override
		protected Timer newTimer(
				final Meter.Id id,
				final DistributionStatisticConfig distributionStatisticConfig,
				final PauseDetector pauseDetector)
		{
			return new AssertionFailedErrorTimer()
			{
				@Override
				public void record(final long amount, final TimeUnit unit)
				{
					assertSame(TimeUnit.NANOSECONDS, unit);
					actual.add("record " + id + " " + amount + "ns");
				}

				@Override
				public Id getId()
				{
					return id;
				}

				@Override
				public String toString()
				{
					return id.toString();
				}
			};
		}

		@Override
		protected TimeUnit getBaseTimeUnit()
		{
			return TimeUnit.SECONDS;
		}

		@Override
		protected DistributionStatisticConfig defaultHistogramConfig()
		{
			return DistributionStatisticConfig.DEFAULT;
		}

		public void assertIt(final String... expected)
		{
			assertEquals(asList(expected), actual);
			actual.clear();
		}
	}

	private static final class MockClock extends AssertionFailedErrorClock
	{
		private final ArrayList<Long> monotonicTime = new ArrayList<>();

		MockClock(final long... monotonicTime)
		{
			for(final long l : monotonicTime)
				this.monotonicTime.add(l);
		}

		@Override
		public long monotonicTime()
		{
			return monotonicTime.remove(0);
		}

		void assertEmpty()
		{
			assertEquals(asList(), monotonicTime);
		}
	}
}
