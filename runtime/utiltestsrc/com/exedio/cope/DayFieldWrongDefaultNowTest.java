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

import static com.exedio.cope.TypesBound.newType;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Day;
import java.util.TimeZone;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class DayFieldWrongDefaultNowTest extends CopeAssert
{
	private static final Logger logger = Logger.getLogger(DayField.class);
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

	@Test public void testIt()
	{
		newType(AnItem.class);
		log.assertMessage(
				Level.WARN,
				"Very probably you called \"DayField.defaultTo(new Day())\" on field AnItem.wrong. " +
				"This will not work as expected, use \"defaultToNow()\" instead.");
		log.assertEmpty();
	}

	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;

		private AnItem(final ActivationParameters ap)
		{
			super(ap);
		}

		static final DayField wrong = new DayField().defaultTo(new Day(TimeZone.getDefault()));
		static final DayField ok = new DayField().defaultTo(new Day(2005, 10, 10));
	}
}
