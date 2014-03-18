/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.UtilTestLogAppender;
import com.exedio.cope.junit.CopeAssert;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class TimeUtilTest extends CopeAssert
{
	private static final Logger logger = Logger.getLogger(TimeUtil.class);
	UtilTestLogAppender log = null;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		log = new UtilTestLogAppender();
		logger.addAppender(log);
	}

	@Override
	protected void tearDown() throws Exception
	{
		logger.removeAppender(log);
		log = null;
		super.tearDown();
	}

	public void testIt()
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

	public void testIllegal()
	{
		assertEquals(0, toMillies(-1, 0));
		log.assertMessage(Level.ERROR, "backwards nanos 0 -1");
	}
}
