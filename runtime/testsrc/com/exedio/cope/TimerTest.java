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

package com.exedio.cope;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Timer.Interval;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MainRule.Tag
public class TimerTest
{
	private static final Logger logger = LoggerFactory.getLogger(TimerTest.class);
	private final LogRule log = new LogRule(TimerTest.class);

	@Test void testNormal()
	{
		final Timer timer = new Timer(logger, "timerMsg");

		final Interval interval1 = timer.start();

		log.assertEmpty();
		interval1.finish("interval1Msg1");
		log.assertInfoMS("interval1Msg1 XXms total timerMsg XXms");

		log.assertEmpty();
		try
		{
			interval1.finish("interval1Msg2");
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("exhausted", e.getMessage());
		}
		log.assertEmpty();
	}

	@Test void testNullTimermsg()
	{
		final Timer timer = new Timer(logger, null);

		final Interval interval1 = timer.start();

		log.assertEmpty();
		interval1.finish("interval1Msg1");
		log.assertInfoMS("interval1Msg1 XXms total XXms");
	}

	@Test void testNullLogger()
	{
		try
		{
			new Timer(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("logger", e.getMessage());
		}
	}
}
