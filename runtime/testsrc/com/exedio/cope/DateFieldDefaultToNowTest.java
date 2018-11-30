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

import static com.exedio.cope.DateFieldDefaultToNowItem.dateNone;
import static com.exedio.cope.DateFieldDefaultToNowItem.dateNow;
import static com.exedio.cope.DateFieldDefaultToNowItem.dateNowOpt;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DateFieldDefaultToNowTest extends TestWithEnvironment
{
	public DateFieldDefaultToNowTest()
	{
		super(DateFieldDefaultToNowModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();


	@BeforeEach final void setUp()
	{
		clockRule.override(clock);
	}

	@Test void testIt()
	{
		{
			final Date now = clock.add(1111);
			final DateFieldDefaultToNowItem item = new DateFieldDefaultToNowItem(
			);
			clock.assertEmpty();

			assertEquals(now, item.getDateNow());
			assertEquals(now, item.getDateNowOpt());
			assertEquals(null, item.getDateNone());
		}
		{
			final Date now = clock.add(2222);
			final DateFieldDefaultToNowItem item = new DateFieldDefaultToNowItem(
			);
			clock.assertEmpty();

			assertEquals(now, item.getDateNow());
			assertEquals(now, item.getDateNowOpt());
			assertEquals(null, item.getDateNone());
		}
		{
			clock.assertEmpty();
			final DateFieldDefaultToNowItem item = new DateFieldDefaultToNowItem(
					dateNow.map(date(501)),
					dateNowOpt.map(date(502)),
					dateNone.map(date(503))
			);
			clock.assertEmpty();

			assertEquals(date(501), item.getDateNow());
			assertEquals(date(502), item.getDateNowOpt());
			assertEquals(date(503), item.getDateNone());
		}
		{
			final Date now = clock.add(4444);
			final DateFieldDefaultToNowItem item = new DateFieldDefaultToNowItem(
					dateNowOpt.map(null),
					dateNone.map(null)
			);
			clock.assertEmpty();

			assertEquals(now, item.getDateNow());
			assertEquals(null, item.getDateNowOpt());
			assertEquals(null, item.getDateNone());
		}
	}

	private static Date date(final long l)
	{
		return new Date(l);
	}
}
