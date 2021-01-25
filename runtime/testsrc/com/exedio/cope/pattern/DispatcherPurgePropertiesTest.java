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

import static com.exedio.cope.pattern.DispatcherPurgeProperties.factory;
import static java.time.Duration.ZERO;
import static java.time.Duration.ofDays;
import static java.time.Duration.ofHours;
import static java.time.Duration.ofNanos;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.pattern.DispatcherPurgeProperties.Factory;
import com.exedio.cope.util.Sources;
import org.junit.jupiter.api.Test;

public class DispatcherPurgePropertiesTest
{
	@Test void testDefault()
	{
		final DispatcherPurgeProperties props = create(factory());
		assertEquals(ZERO, props.retainSuccess);
		assertEquals(ZERO, props.retainFinalFailure);
	}

	@Test void testCustom()
	{
		final DispatcherPurgeProperties props = create(factory().retainDaysDefault(4, 6));
		assertEquals(ofDays(4), props.retainSuccess);
		assertEquals(ofDays(6), props.retainFinalFailure);
	}

	@Test void testCustomDuration()
	{
		final DispatcherPurgeProperties props = create(factory().retainDefault(ofHours(4), ofHours(6)));
		assertEquals(ofHours(4), props.retainSuccess);
		assertEquals(ofHours(6), props.retainFinalFailure);
	}

	@Test void testMinimum()
	{
		final DispatcherPurgeProperties props = create(factory().retainDaysDefault(1));
		assertEquals(ofDays(1), props.retainSuccess);
		assertEquals(ofDays(1), props.retainFinalFailure);
	}

	@Test void testMinimumDuration()
	{
		final DispatcherPurgeProperties props = create(factory().retainDefault(ofNanos(1)));
		assertEquals(ofNanos(1), props.retainSuccess);
		assertEquals(ofNanos(1), props.retainFinalFailure);
	}

	@Test void testOmit()
	{
		final DispatcherPurgeProperties props = create(factory().retainDaysDefault(0));
		assertEquals(ZERO, props.retainSuccess);
		assertEquals(ZERO, props.retainFinalFailure);
	}

	@Test void testOmitDuration()
	{
		final DispatcherPurgeProperties props = create(factory().retainDefault(ZERO));
		assertEquals(ZERO, props.retainSuccess);
		assertEquals(ZERO, props.retainFinalFailure);
	}

	@Test void testNegativeSuccess()
	{
		final Factory factory = factory();
		try
		{
			factory.retainDaysDefault(-1, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("retainSuccess must not be negative, but was PT-24H", e.getMessage());
		}
	}

	@Test void testNegativeSuccessDuration()
	{
		final Factory factory = factory();
		try
		{
			factory.retainDefault(ofNanos(-1), ZERO);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("retainSuccess must not be negative, but was PT-0.000000001S", e.getMessage());
		}
	}

	@Test void testNegativeFailure()
	{
		final Factory factory = factory();
		try
		{
			factory.retainDaysDefault(0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("retainFinalFailure must not be negative, but was PT-24H", e.getMessage());
		}
	}

	@Test void testNegativeFailureDuration()
	{
		final Factory factory = factory();
		try
		{
			factory.retainDefault(ZERO, ofNanos(-1));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("retainFinalFailure must not be negative, but was PT-0.000000001S", e.getMessage());
		}
	}


	private static DispatcherPurgeProperties create(final Factory factory)
	{
		return factory.create(Sources.EMPTY);
	}
}
