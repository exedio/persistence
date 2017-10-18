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

import static org.junit.Assert.assertEquals;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.pattern.DispatcherItem.Log;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.util.EmptyJobContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class DispatcherUnpendTest extends TestWithEnvironment
{
	private static final Dispatcher.Config config = new Dispatcher.Config(3, 100);

	public DispatcherUnpendTest()
	{
		super(DispatcherModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	@Rule public final RuleChain ruleChain = RuleChain.outerRule(clockRule);

	DispatcherItem item;

	@Before public void setUp()
	{
		item = new DispatcherItem("item1", false);
		clockRule.override(clock);
	}


	@Test public void testOk()
	{
		assertIt(true,  null, null);
		dispatch(10000);
		assertIt(false, true, 10000);
		dispatch();
		assertIt(false, true, 10000);
	}

	@Test public void testFail()
	{
		DispatcherItem.logs.put(item, new Log(true));
		assertIt(true,  null,  null);
		dispatch(20000);
		assertIt(true,  null,  null);
		dispatch(40000);
		assertIt(true,  null,  null);
		dispatch(60000);
		assertIt(false, false, 60000);
		dispatch();
		assertIt(false, false, 60000);
	}


	private void dispatch()
	{
		model.commit();
		DispatcherItem.dispatchToTarget(config, new EmptyJobContext());
		clock.assertEmpty();
		model.startTransaction("DispatcherTest");
	}

	private void dispatch(final long date)
	{
		model.commit();
		clock.add(date);
		DispatcherItem.dispatchToTarget(config, new EmptyJobContext());
		clock.assertEmpty();
		model.startTransaction("DispatcherTest");
	}

	private void assertIt(
			final boolean pending,
			final Boolean success,
			final Integer date)
	{
		assertEquals("pending", pending, item.isToTargetPending());
		assertEquals("success", success, item.getToTargetUnpendSuccess());
		assertEquals("date",    date,    item.getToTargetUnpendDateMillis());
	}

	@After public void afterEach()
	{
		DispatcherItem.toTarget.reset();
	}
}
