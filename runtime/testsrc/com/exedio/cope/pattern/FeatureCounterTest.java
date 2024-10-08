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
import static com.exedio.cope.pattern.FeatureCounter.counter;
import static com.exedio.cope.pattern.FeatureMeter.onMount;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.AssertionFailedErrorClock;
import com.exedio.cope.tojunit.AssertionFailedErrorCounter;
import com.exedio.cope.tojunit.AssertionFailedErrorMeterRegistry;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import java.io.Serial;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see MediaCounterTest
 */
@MainRule.Tag
public class FeatureCounterTest
{
	private final LogRule log = new LogRule(FeatureMeter.class);

	@Test void test()
	{
		log.setLevelDebug();
		final FeatureCounter meter = counter("myNameSuffix", "myDescription");
		registry.assertIt();
		log.assertEmpty();

		meter.increment();
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.increment();
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		meter.increment();
		registry.assertIt();
		log.assertError("unmounted myNameSuffix myDescription");

		onMount(MyItem.myFeature, meter);
		registry.assertIt();
		log.assertEmpty();

		meter.increment();
		registry.assertIt("record MeterId{name='com.exedio.cope.StringField.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.increment();
		registry.assertIt("record MeterId{name='com.exedio.cope.StringField.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();

		meter.increment();
		registry.assertIt("record MeterId{name='com.exedio.cope.StringField.myNameSuffix', tags=[tag(feature=MyItem.myFeature)]}");
		log.assertEmpty();
	}

	@Test void testNewValue()
	{
		final FeatureCounter meter1 = counter("myNameSuffixTags", "myDescription", "myKey", "myValue1");
		final FeatureCounter meter2 = meter1.newValue("myValue2");
		registry.assertIt();

		onMount(MyItem.myFeature, meter1, meter2);
		registry.assertIt();

		meter1.increment();
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.StringField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue1)]}");

		meter1.increment();
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.StringField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue1)]}");

		meter2.increment();
		registry.assertIt(
				"record MeterId{" +
				"name='com.exedio.cope.StringField.myNameSuffixTags', " +
				"tags=[tag(feature=MyItem.myFeature),tag(myKey=myValue2)]}");
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MyItem extends Item
	{
		@WrapperIgnore
		static final StringField myFeature = new StringField();

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
		FeatureMeter.registry = registry;
	}

	@AfterEach void after()
	{
		//noinspection AssignmentToStaticFieldFromInstanceMethod
		FeatureMeter.registry = Metrics.globalRegistry;
	}

	@Test void testNameSuffixNull()
	{
		assertFails(
				() -> counter(null, null),
				NullPointerException.class,
				"nameSuffix");
	}

	@Test void testNameSuffixEmpty()
	{
		assertFails(
				() -> counter("", null),
				IllegalArgumentException.class,
				"nameSuffix must not be empty");
	}

	@Test void testNameSuffixChars()
	{
		assertFails(
				() -> counter("abc.xyz", "myDescription"),
				IllegalArgumentException.class,
				"character not allowed at position 3: >abc.xyz<");
	}

	@Test void testDescriptionNull()
	{
		assertFails(
				() -> counter("myNameSuffix", null),
				NullPointerException.class,
				"description");
	}

	@Test void testDescriptionEmpty()
	{
		assertFails(
				() -> counter("myNameSuffix", ""),
				IllegalArgumentException.class,
				"description must not be empty");
	}

	@Test void testKeyNull()
	{
		assertFails(
				() -> counter("myNameSuffix", "myDescription", null, null),
				NullPointerException.class,
				"key");
	}

	@Test void testKeyEmpty()
	{
		assertFails(
				() -> counter("myNameSuffix", "myDescription", "", null),
				IllegalArgumentException.class,
				"key must not be empty");
	}

	@Test void testValueNull()
	{
		assertFails(
				() -> counter("myNameSuffix", "myDescription", "myKey", null),
				NullPointerException.class,
				"value");
	}

	@Test void testValueEmpty()
	{
		assertFails(
				() -> counter("myNameSuffix", "myDescription", "myKey", ""),
				IllegalArgumentException.class,
				"value must not be empty");
	}

	@Test void testNewValueNull()
	{
		final FeatureCounter meter = counter("myNameSuffix", "myDescription", "myKey", "myValue");
		assertFails(
				() -> meter.newValue(null),
				NullPointerException.class,
				"value");
	}

	@Test void testNewValueEmpty()
	{
		final FeatureCounter meter = counter("myNameSuffix", "myDescription", "myKey", "myValue");
		assertFails(
				() -> meter.newValue(""),
				IllegalArgumentException.class,
				"value must not be empty");
	}

	@Test void testNewValueSame()
	{
		final FeatureCounter meter = counter("myNameSuffix", "myDescription", "myKey", "myValue");
		assertFails(
				() -> meter.newValue("myValue"),
				IllegalArgumentException.class,
				"value must be different");
	}

	@Test void testNewValueWithoutKey()
	{
		final FeatureCounter meter = counter("myNameSuffix", "myDescription");
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

	@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
	@Test void testMountTwice()
	{
		final FeatureCounter meter = counter("myNameSuffix" + (testMountTwiceSuffix++), "myDescription");
		onMount(MyItem.myFeature, meter);
		assertFails(
				() -> onMount(MyItem.myFeature, meter),
				IllegalStateException.class,
				"already mounted");
	}
	private static int testMountTwiceSuffix = 0;

	@Test void testFeatureNotFinal()
	{
		final NonFinalPattern hash = new NonFinalPattern();
		assertFails(
				() -> onMount(hash),
				IllegalArgumentException.class,
				"not final: " + NonFinalPattern.class + " " + hash);
	}
	static class NonFinalPattern extends Pattern
	{
		@Serial
		private static final long serialVersionUID = 1l;
	}

	private static final class MockMeterRegistry extends AssertionFailedErrorMeterRegistry
	{
		private final ArrayList<String> actual = new ArrayList<>();

		MockMeterRegistry()
		{
			super(new AssertionFailedErrorClock());
		}

		@Override
		protected Counter newCounter(
				final Meter.Id id)
		{
			return new AssertionFailedErrorCounter()
			{
				@Override
				public void increment(final double amount)
				{
					assertEquals(1.0, amount);
					actual.add("record " + id);
				}
			};
		}

		public void assertIt(final String... expected)
		{
			assertEquals(asList(expected), actual);
			actual.clear();
		}
	}
}

