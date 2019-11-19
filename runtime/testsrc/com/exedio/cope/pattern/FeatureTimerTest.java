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
import static com.exedio.cope.pattern.FeatureMeter.onMount;
import static com.exedio.cope.pattern.FeatureTimer.timer;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.AssertionFailedErrorClock;
import com.exedio.cope.tojunit.AssertionFailedErrorMeterRegistry;
import com.exedio.cope.tojunit.AssertionFailedErrorTimer;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import org.opentest4j.AssertionFailedError;

@MainRule.Tag
@SuppressFBWarnings({"NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS","NP_NULL_PARAM_DEREF_NONVIRTUAL","RV_RETURN_VALUE_IGNORED_INFERRED"})
public class FeatureTimerTest
{
	private final LogRule log = new LogRule(FeatureMeter.class);

	@Test void test()
	{
		log.setLevelDebug();
		final FeatureTimer meter = timer("myNameSuffix", "myDescription");
		registry.assertIt();
		log.assertEmpty();

		meter.record(5, NANOSECONDS);
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.stop(Timer.start());
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.stopMillies(Timer.start());
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.stop(Timer.start());
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.stopMillies(Timer.start());
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.stop(Timer.start());
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.stopMillies(Timer.start());
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		onMount(MyItem.myFeature, meter);
		registry.assertIt();
		log.assertEmpty();

		meter.record(5, NANOSECONDS);
		registry.assertIt("record MeterId{name='com.exedio.cope.StringField.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.stop(Timer.start());
		registry.assertIt("record MeterId{name='com.exedio.cope.StringField.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.stopMillies(Timer.start());
		registry.assertIt("record MeterId{name='com.exedio.cope.StringField.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.stop(Timer.start());
		registry.assertIt("record MeterId{name='com.exedio.cope.StringField.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.stopMillies(Timer.start());
		registry.assertIt("record MeterId{name='com.exedio.cope.StringField.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.stop(Timer.start());
		registry.assertIt("record MeterId{name='com.exedio.cope.StringField.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.stopMillies(Timer.start());
		registry.assertIt("record MeterId{name='com.exedio.cope.StringField.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();
	}

	@Test void testNewValue()
	{
		final FeatureTimer meter1 = timer("myNameSuffixTags", "myDescription", "myKey", "myValue1");
		final FeatureTimer meter2 = meter1.newValue("myValue2");
		registry.assertIt();

		onMount(MyItem.myFeature, meter1, meter2);
		registry.assertIt();

		meter1.record(5, NANOSECONDS);
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.StringField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue1)]}");

		meter1.stop(Timer.start());
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.StringField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue1)]}");

		meter1.stopMillies(Timer.start());
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.StringField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue1)]}");

		meter2.record(5, NANOSECONDS);
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.StringField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue2)]}");

		meter2.stop(Timer.start());
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.StringField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue2)]}");

		meter2.stopMillies(Timer.start());
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.StringField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue2)]}");
	}

	@Test void testFeatureClass()
	{
		final FeatureTimer meter = timer("myNameSuffixTags", "myDescription", "myKey", "myValue1");
		registry.assertIt();

		onMount(FunctionField.class, MyItem.myFeature, meter);
		registry.assertIt();

		meter.record(5, NANOSECONDS);
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.FunctionField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue1)]}");

		meter.stop(Timer.start());
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.FunctionField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue1)]}");

		meter.stopMillies(Timer.start());
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.FunctionField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue1)]}");
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class MyItem extends Item
	{
		@WrapperIgnore
		static final StringField myFeature = new StringField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	private final MockMeterRegistry registry = new MockMeterRegistry();

	@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
	@BeforeEach void before()
	{
		//noinspection AssignmentToStaticFieldFromInstanceMethod
		FeatureMeter.registry = registry;
	}

	@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
	@AfterEach void after()
	{
		//noinspection AssignmentToStaticFieldFromInstanceMethod
		FeatureMeter.registry = Metrics.globalRegistry;
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
		final FeatureTimer meter = timer("myNameSuffix", "myDescription", "myKey", "myValue");
		assertFails(
				() -> meter.newValue(null),
				NullPointerException.class,
				"value");
	}

	@Test void testNewValueEmpty()
	{
		final FeatureTimer meter = timer("myNameSuffix", "myDescription", "myKey", "myValue");
		assertFails(
				() -> meter.newValue(""),
				IllegalArgumentException.class,
				"value must not be empty");
	}

	@Test void testNewValueSame()
	{
		final FeatureTimer meter = timer("myNameSuffix", "myDescription", "myKey", "myValue");
		assertFails(
				() -> meter.newValue("myValue"),
				IllegalArgumentException.class,
				"value must be different");
	}

	@Test void testNewValueWithoutKey()
	{
		final FeatureTimer meter = timer("myNameSuffix", "myDescription");
		assertFails(
				() -> meter.newValue("myValue"),
				IllegalArgumentException.class,
				"not allowed without key");
	}

	@Test void testOnMountFeatureNull()
	{
		assertFails(
				() -> onMount(null),
				NullPointerException.class,
				"feature");
	}

	@Test void testOnMountClassFeatureClassNull()
	{
		assertFails(
				() -> onMount(null, null, new FeatureTimer[]{}),
				NullPointerException.class,
				"featureClass");
	}

	@Test void testOnMountClassFeatureNull()
	{
		assertFails(
				() -> onMount(StringField.class, null, new FeatureTimer[]{}),
				NullPointerException.class,
				"feature");
	}

	@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
	@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
	@Test void testMountTwice()
	{
		final FeatureTimer meter = timer("myNameSuffix" + (testMountTwiceSuffix++), "myDescription");
		onMount(MyItem.myFeature, meter);
		assertFails(
				() -> onMount(MyItem.myFeature, meter),
				IllegalStateException.class,
				"already mounted");
	}
	private static int testMountTwiceSuffix = 0;

	@Test void testFeatureNotFinal()
	{
		final Hash hash = new Hash(new HashAlgorithm()
		{
			@Override
			public String getID()
			{
				return "id";
			}
			@Override
			public String getDescription()
			{
				throw new AssertionFailedError();
			}
			@Override
			public StringField constrainStorage(final StringField storage)
			{
				return storage;
			}
			@Override
			public String hash(final String plainText)
			{
				throw new AssertionFailedError();
			}
			@Override
			public boolean check(final String plainText, final String hash)
			{
				throw new AssertionFailedError();
			}
		});
		assertFails(
				() -> onMount(hash),
				IllegalArgumentException.class,
				"not final: " + Hash.class + " " + hash);
	}

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

