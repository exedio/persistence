package com.exedio.cope;

import java.util.Collections;
import java.util.LinkedList;
import java.util.function.LongSupplier;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CallRateTrackerTest
{
	@Test
	void firstCallReturnsOne()
	{
		final CallRateTracker tracker = new CallRateTracker();
		assertEquals(1, tracker.recordCallAndGetRate());
	}

	@Test
	void multipleCallsWithinSameWindowAreCountedExactly()
	{
		final TimeSource clock = new TimeSource();
		final CallRateTracker tracker = new CallRateTracker(clock);

		clock.add(Duration.ofSeconds(0));
		assertEquals(1, tracker.recordCallAndGetRate());
		clock.add(Duration.ofSeconds(0));
		assertEquals(2, tracker.recordCallAndGetRate());
		clock.add(Duration.ofMillis(10));
		assertEquals(3, tracker.recordCallAndGetRate());
		clock.assertEmpty();
	}

	@Test
	void rareCallsBelowOnePerMinuteStillReturnOne()
	{
		final TimeSource clock = new TimeSource();
		final CallRateTracker tracker = new CallRateTracker(clock);
		clock.add(Duration.ofMillis(0));
		assertEquals(1, tracker.recordCallAndGetRate());

		// no calls at all for five minutes
		clock.add(Duration.ofMinutes(5));
		assertEquals(1, tracker.recordCallAndGetRate());
		clock.assertEmpty();
	}

	@Test
	void previousWindowCountDecaysLinearlyAsCurrentWindowProgresses()
	{
		final TimeSource clock = new TimeSource();
		final CallRateTracker tracker = new CallRateTracker(clock);

		// 10 calls in the first window
		for(int i = 0; i < 10; i++)
		{
			clock.add(Duration.ofMillis(i));
			assertEquals(i+1, tracker.recordCallAndGetRate());
		}

		// move to exactly the start of the next window;
		// previous window's count (10) should count in full
		clock.add(Duration.ofSeconds(60));
		assertEquals(11, tracker.recordCallAndGetRate()); // 10 (fully weighted) + 1 new call

		// move to the middle of the current window;
		// previous window's count should now be weighted by ~0.5
		clock.add(Duration.ofSeconds(90));
		assertEquals(7, tracker.recordCallAndGetRate()); // round(10 * 0.5) + 2 new calls

		clock.assertEmpty();
	}

	@Test
	void countResetsAfterMultipleWindowsWithoutCalls()
	{
		final TimeSource clock = new TimeSource();
		final CallRateTracker tracker = new CallRateTracker(clock);

		for(int i = 0; i < 20; i++)
		{
			clock.add(Duration.ofMillis(0));
			assertEquals(i+1, tracker.recordCallAndGetRate());
		}

		// more than one full window passes with no calls at all
		clock.add(Duration.ofMinutes(3));
		assertEquals(1, tracker.recordCallAndGetRate());

		clock.assertEmpty();
	}

	private final class TimeSource implements LongSupplier
	{
		private final LinkedList<Long> values = new LinkedList<>();

		TimeSource()
		{
			add(Duration.ofNanos(0)); // all tests start a time=0
		}

		private void add(final Duration timeAfterZero)
		{
			values.add(timeAfterZero.toNanos());
		}

		@Override
		public long getAsLong()
		{
			assertFalse(values.isEmpty());
			return values.removeFirst();
		}

		void assertEmpty()
		{
			assertEquals(Collections.emptyList(), values);
		}
	}
}
