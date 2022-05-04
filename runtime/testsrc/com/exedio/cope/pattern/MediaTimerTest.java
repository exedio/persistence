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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.pattern.MediaTimer.timer;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.AssertionFailedErrorClock;
import com.exedio.cope.tojunit.AssertionFailedErrorCounter;
import com.exedio.cope.tojunit.AssertionFailedErrorMeterRegistry;
import com.exedio.cope.tojunit.AssertionFailedErrorTimer;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see FeatureTimerTest
 */
@MainRule.Tag
public class MediaTimerTest
{
	private final LogRule log = new LogRule(MediaMeter.class);

	@Test void test()
	{
		log.setLevelDebug();
		final MediaTimer meter = timer("myNameSuffix", "myDescription");
		registry.assertIt();
		log.assertEmpty();

		meter.record(5, NANOSECONDS);
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.stop(Timer.start());
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.stop(Timer.start());
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.stop(Timer.start());
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.onMount(MyItem.myFeature);
		registry.assertIt();
		log.assertEmpty();

		meter.record(5, NANOSECONDS);
		registry.assertIt("record MeterId{name='com.exedio.cope.pattern.MediaPath.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.stop(Timer.start());
		registry.assertIt("record MeterId{name='com.exedio.cope.pattern.MediaPath.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.stop(Timer.start());
		registry.assertIt("record MeterId{name='com.exedio.cope.pattern.MediaPath.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.stop(Timer.start());
		registry.assertIt("record MeterId{name='com.exedio.cope.pattern.MediaPath.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();
	}

	@Test void testNewValue()
	{
		final MediaTimer meter1 = timer("myNameSuffixTags", "myDescription", "myKey", "myValue1");
		final MediaTimer meter2 = meter1.newValue("myValue2");
		registry.assertIt();

		meter1.onMount(MyItem.myFeature);
		meter2.onMount(MyItem.myFeature);
		registry.assertIt();

		meter1.record(5, NANOSECONDS);
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.pattern.MediaPath.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue1)]}");

		meter1.stop(Timer.start());
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.pattern.MediaPath.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue1)]}");

		meter2.record(5, NANOSECONDS);
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.pattern.MediaPath.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue2)]}");

		meter2.stop(Timer.start());
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.pattern.MediaPath.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue2)]}");
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MyItem extends Item
	{
		@WrapperIgnore
		static final Media myFeature = new Media();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	private final MockMeterRegistry registry = new MockMeterRegistry();

	@BeforeEach void before()
	{
		//noinspection AssignmentToStaticFieldFromInstanceMethod
		MediaMeter.registry = registry;
	}

	@AfterEach void after()
	{
		//noinspection AssignmentToStaticFieldFromInstanceMethod
		MediaMeter.registry = Metrics.globalRegistry;
	}

	@Test void testNameSuffixNull()
	{
		assertFails(
				() -> timer(null, null),
				NullPointerException.class,
				"nameSuffix");
	}

	@Test void testNameSuffixEmpty()
	{
		assertFails(
				() -> timer("", null),
				IllegalArgumentException.class,
				"nameSuffix must not be empty");
	}

	@Test void testNameSuffixChars()
	{
		assertFails(
				() -> timer("abc.xyz", "myDescription"),
				IllegalArgumentException.class,
				"character not allowed at position 3: >abc.xyz<");
	}

	@Test void testDescriptionNull()
	{
		assertFails(
				() -> timer("myNameSuffix", null),
				NullPointerException.class,
				"description");
	}

	@Test void testDescriptionEmpty()
	{
		assertFails(
				() -> timer("myNameSuffix", ""),
				IllegalArgumentException.class,
				"description must not be empty");
	}

	@Test void testKeyNull()
	{
		assertFails(
				() -> timer("myNameSuffix", "myDescription", null, null),
				NullPointerException.class,
				"key");
	}

	@Test void testKeyEmpty()
	{
		assertFails(
				() -> timer("myNameSuffix", "myDescription", "", null),
				IllegalArgumentException.class,
				"key must not be empty");
	}

	@Test void testValueNull()
	{
		assertFails(
				() -> timer("myNameSuffix", "myDescription", "myKey", null),
				NullPointerException.class,
				"value");
	}

	@Test void testValueEmpty()
	{
		assertFails(
				() -> timer("myNameSuffix", "myDescription", "myKey", ""),
				IllegalArgumentException.class,
				"value must not be empty");
	}

	@Test void testNewValueNull()
	{
		final MediaTimer meter = timer("myNameSuffix", "myDescription", "myKey", "myValue");
		assertFails(
				() -> meter.newValue(null),
				NullPointerException.class,
				"value");
	}

	@Test void testNewValueEmpty()
	{
		final MediaTimer meter = timer("myNameSuffix", "myDescription", "myKey", "myValue");
		assertFails(
				() -> meter.newValue(""),
				IllegalArgumentException.class,
				"value must not be empty");
	}

	@Test void testNewValueSame()
	{
		final MediaTimer meter = timer("myNameSuffix", "myDescription", "myKey", "myValue");
		assertFails(
				() -> meter.newValue("myValue"),
				IllegalArgumentException.class,
				"value must be different");
	}

	@Test void testNewValueWithoutKey()
	{
		final MediaTimer meter = timer("myNameSuffix", "myDescription");
		assertFails(
				() -> meter.newValue("myValue"),
				IllegalArgumentException.class,
				"not allowed without key");
	}

	@Test void testOnMountFeatureNull()
	{
		final MediaTimer meter = timer("myNameSuffix", "myDescription");
		meter.onMount(null);
		log.assertEmpty();

		meter.record(5, NANOSECONDS);
		registry.assertIt("record MeterId{name='com.exedio.cope.pattern.MediaPath.myNameSuffix', tags=[tag(feature=NONE)]}");
		log.assertEmpty();
	}

	@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
	@Test void testMountTwice()
	{
		final MediaTimer meter = timer("myNameSuffix" + (testMountTwiceSuffix++), "myDescription");
		meter.onMount(MyItem.myFeature);
		assertFails(
				() -> meter.onMount(MyItem.myFeature),
				IllegalStateException.class,
				"already mounted");
	}
	private static int testMountTwiceSuffix = 0;

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
					assertSame(NANOSECONDS, unit);
					actual.add("record " + id);
				}
			};
		}

		@Override
		protected Counter newCounter(
				final Meter.Id id)
		{
			return new AssertionFailedErrorCounter();
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
}

