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

import com.exedio.cope.Timer.Interval;
import junit.framework.TestCase;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerTest extends TestCase
{
	private static final Logger logger = LoggerFactory.getLogger(TimerTest.class);
	private static final org.apache.log4j.Logger loggerImpl = org.apache.log4j.Logger.getLogger(TimerTest.class);

	UtilTestLogAppender log = null;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		log = new UtilTestLogAppender();
		loggerImpl.addAppender(log);
	}

	@Override
	protected void tearDown() throws Exception
	{
		loggerImpl.removeAppender(log);
		log = null;
		super.tearDown();
	}

	public void testNormal()
	{
		final Timer timer = new Timer(logger, "timerMsg");

		final Interval interval1 = timer.start();

		log.assertEmpty();
		interval1.finish("interval1Msg1");
		log.assertMessageMs(Level.INFO, "interval1Msg1 XXms total timerMsg XXms");

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

	public void testNullTimermsg()
	{
		final Timer timer = new Timer(logger, null);

		final Interval interval1 = timer.start();

		log.assertEmpty();
		interval1.finish("interval1Msg1");
		log.assertMessageMs(Level.INFO, "interval1Msg1 XXms total XXms");
	}

	public void testNullLogger()
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
