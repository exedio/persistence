package com.exedio.cope;

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SequenceImplSequenceTest
{
	private SimpleMeterRegistry registry;

	@BeforeEach
	void beforeEach()
	{
		registry = new SimpleMeterRegistry();
	}

	@AfterEach
	void afterEach()
	{
		registry.close();
	}

	@Test
	void fetchCountAtLeastTwo()
	{
		final Counter counter = Counter.builder("prefetch").register(registry);
		assertFails(() -> new SequenceImplSequence.Prefetch(1, counter), RuntimeException.class, "1");
		assertFails(() -> new SequenceImplSequence.Prefetch(0, counter), RuntimeException.class, "0");
		assertFails(() -> new SequenceImplSequence.Prefetch(-1, counter), RuntimeException.class, "-1");
	}

	@Test
	void counterNotNull()
	{
		assertFails(() -> new SequenceImplSequence.Prefetch(2, null), NullPointerException.class, null);
	}

	@Test
	void startsEmpty()
	{
		final SequenceImplSequence.Prefetch prefetch = newPrefetch(3);
		assertFalse(prefetch.hasPrefetchedValue());
	}

	@Test
	void startsEmptyAndLimitsFetchCount()
	{
		final SequenceImplSequence.Prefetch prefetch = newPrefetch(3);
		assertEquals(1, prefetch.limitFetchCount(1));
		assertEquals(3, prefetch.limitFetchCount(3));
		assertEquals(3, prefetch.limitFetchCount(4));
	}

	@Test
	void prefetchedValuesInOrder()
	{
		final SequenceImplSequence.Prefetch prefetch = newPrefetch(3);
		prefetch.replacePrefetchedValues(new long[]{10, 20, 30});

		assertTrue(prefetch.hasPrefetchedValue());
		assertEquals(10, prefetch.consumePrefetchedValue());
		assertTrue(prefetch.hasPrefetchedValue());
		assertEquals(20, prefetch.consumePrefetchedValue());
		assertTrue(prefetch.hasPrefetchedValue());
		assertEquals(30, prefetch.consumePrefetchedValue());
		assertFalse(prefetch.hasPrefetchedValue());
	}

	@Test
	void replacingValuesResetsConsumption()
	{
		final SequenceImplSequence.Prefetch prefetch = newPrefetch(3);
		prefetch.replacePrefetchedValues(new long[]{10, 20});
		assertEquals(10, prefetch.consumePrefetchedValue());
		assertTrue(prefetch.hasPrefetchedValue());

		prefetch.replacePrefetchedValues(new long[]{30, 40});
		assertTrue(prefetch.hasPrefetchedValue());
		assertEquals(30, prefetch.consumePrefetchedValue());
		assertEquals(40, prefetch.consumePrefetchedValue());
		assertFalse(prefetch.hasPrefetchedValue());
	}

	@Test
	void flush()
	{
		final SequenceImplSequence.Prefetch prefetch = newPrefetch(3);
		prefetch.replacePrefetchedValues(new long[]{10, 20});
		assertEquals(10, prefetch.consumePrefetchedValue());
		assertTrue(prefetch.hasPrefetchedValue());
		assertEquals(1, prefetch.recordCallAndGetRate());
		assertEquals(2, prefetch.recordCallAndGetRate());

		prefetch.flush();
		assertFalse(prefetch.hasPrefetchedValue());
		assertEquals(1, prefetch.recordCallAndGetRate());
	}

	@Test
	void incrementsFetchCounter()
	{
		final SequenceImplSequence.Prefetch prefetch = newPrefetch(3);
		prefetch.incrementCounter(2);
		assertEquals(2, registry.get("prefetch").counter().count());
	}

	private SequenceImplSequence.Prefetch newPrefetch(
			final int maximumFetchCount)
	{
		return new SequenceImplSequence.Prefetch(
				maximumFetchCount,
				Counter.builder("prefetch").register(registry));
	}
}
