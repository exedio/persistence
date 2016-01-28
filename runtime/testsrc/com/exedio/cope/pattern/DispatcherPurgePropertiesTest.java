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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.pattern.DispatcherPurgeProperties.Factory;
import com.exedio.cope.util.Sources;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

public class DispatcherPurgePropertiesTest
{
	@Test public void testDefault()
	{
		final DispatcherPurgeProperties props = create(factory());
		assertEquals(0, props.delayDaysSuccess);
		assertEquals(0, props.delayDaysFinalFailure);
	}

	@Test public void testCustom()
	{
		final DispatcherPurgeProperties props = create(factory().delayDaysDefault(4, 6));
		assertEquals(4, props.delayDaysSuccess);
		assertEquals(6, props.delayDaysFinalFailure);
	}

	@Test public void testMinimum()
	{
		final DispatcherPurgeProperties props = create(factory().delayDaysDefault(1));
		assertEquals(1, props.delayDaysSuccess);
		assertEquals(1, props.delayDaysFinalFailure);
	}

	@Test public void testOmit()
	{
		final DispatcherPurgeProperties props = create(factory().delayDaysDefault(0));
		assertEquals(0, props.delayDaysSuccess);
		assertEquals(0, props.delayDaysFinalFailure);
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test public void testNegativeSuccess()
	{
		final Factory factory = factory();
		try
		{
			factory.delayDaysDefault(-1, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("delayDaysSuccess must not be negative, but was -1", e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test public void testNegativeFailure()
	{
		final Factory factory = factory();
		try
		{
			factory.delayDaysDefault(0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("delayDaysFinalFailure must not be negative, but was -1", e.getMessage());
		}
	}


	private static DispatcherPurgeProperties create(final Factory factory)
	{
		return factory.create(Sources.view(new java.util.Properties(), "description"));
	}
}
