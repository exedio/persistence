package com.exedio.cope;

import java.time.Duration;
import java.util.function.LongSupplier;

class CallRateTracker
{
	private static final long WINDOW_NANOS = Duration.ofSeconds(60).toNanos();

	private final LongSupplier clock;

	private long windowStart;
	private long currentCount = 0;
	private long previousCount = 0;

	CallRateTracker()
	{
		this(System::nanoTime);
	}

	CallRateTracker(final LongSupplier clock)
	{
		this.clock = clock;
		windowStart = clock.getAsLong();
	}

	/**
	 * Records a call and returns the approximate number of calls
	 * in the last minute (minimum 1, since this call itself counts).
	 * <p>
	 * Not synchronized - caller must ensure thread safety
	 * (e.g. via an enclosing synchronized block).
	 */
	int recordCallAndGetRate()
	{
		final long now = clock.getAsLong();
		rotateIfNeeded(now);
		currentCount++;

		final long elapsedInWindow = now - windowStart;
		final double weight = 1.0 - (double)elapsedInWindow / WINDOW_NANOS;
		final double rate = previousCount * weight + currentCount;

		final int result = (int)Math.round(rate);
		return Math.max(result, 1);
	}

	private void rotateIfNeeded(final long now)
	{
		final long elapsed = now - windowStart;
		if(elapsed < WINDOW_NANOS)
		{
			return;
		}
		final long windowsPassed = elapsed / WINDOW_NANOS;
		// only carry over the previous window's count if it's the
		// immediately preceding one; otherwise too much time has
		// passed and it's no longer relevant
		previousCount = (windowsPassed==1) ? currentCount : 0;
		currentCount = 0;
		windowStart += windowsPassed * WINDOW_NANOS;
	}
}