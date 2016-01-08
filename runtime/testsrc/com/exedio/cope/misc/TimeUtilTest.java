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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.TimeUtil.toMillies;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.tojunit.TestLogAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TimeUtilTest
{
	private static final Logger logger = Logger.getLogger(TimeUtil.class);
	TestLogAppender log = null;

	@Before public final void setUp()
	{
		log = new TestLogAppender();
		logger.addAppender(log);
	}

	@After public final void tearDown()
	{
		logger.removeAppender(log);
		log = null;
	}

	@Test public void testIt()
	{
		assertEquals(0, toMillies(      0, 0));
		assertEquals(0, toMillies( 499999, 0));
		assertEquals(1, toMillies( 500000, 0));
		assertEquals(1, toMillies( 500001, 0));
		assertEquals(1, toMillies(1000000, 0));
		assertEquals(1, toMillies(1499999, 0));
		assertEquals(2, toMillies(1500000, 0));
		assertEquals(2, toMillies(1500001, 0));

		log.assertEmpty();
	}

	@Test public void testIllegal()
	{
		assertEquals(0, toMillies(-1, 0));
		log.assertMessage(Level.ERROR, "backwards nanos 0 -1");
	}
}
