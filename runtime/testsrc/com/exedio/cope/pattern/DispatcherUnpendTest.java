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

import static com.exedio.cope.pattern.DispatcherItem.historyAdd;
import static com.exedio.cope.pattern.DispatcherItem.historyAssert;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.pattern.DispatcherItem.Log;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.AssertionErrorJobContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class DispatcherUnpendTest extends TestWithEnvironment
{
	private static final Dispatcher.Config config = new Dispatcher.Config(3, 100);

	public DispatcherUnpendTest()
	{
		super(DispatcherModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	DispatcherItem item;

	@BeforeEach void setUp()
	{
		item = new DispatcherItem("item1", false);
		clockRule.override(clock);
		DispatcherItem.historyClear();
	}


	@Test void testOk()
	{
		assertIt(true,  null, null);
		dispatch(10000);
		historyAssert(
				"ctx stop", "ctx defer", "dispatch " + item, "ctx progress");
		assertIt(false, true, 10000l);

		dispatch();
		historyAssert();
		assertIt(false, true, 10000l);
	}

	@Test void testFail()
	{
		DispatcherItem.logs.put(item, new Log(true));
		assertIt(true,  null,  null);

		dispatch(20000);
		historyAssert(
				"ctx stop", "ctx defer", "dispatch " + item, "ctx progress");
		assertIt(true,  null,  null);

		dispatch(40000);
		historyAssert(
				"ctx stop", "ctx defer", "dispatch " + item, "ctx progress");
		assertIt(true,  null,  null);

		dispatch(60000);
		historyAssert(
				"ctx stop", "ctx defer", "dispatch " + item, "notifyFinalFailure " + item, "ctx progress");
		assertIt(false, false, 60000l);

		dispatch();
		historyAssert();
		assertIt(false, false, 60000l);
	}


	private void dispatch()
	{
		model.commit();
		historyAssert();
		DispatcherItem.dispatchToTarget(config, CTX);
		clock.assertEmpty();
		model.startTransaction("DispatcherTest");
	}

	private void dispatch(final long date)
	{
		model.commit();
		clock.add(date);
		historyAssert();
		DispatcherItem.dispatchToTarget(config, CTX);
		clock.assertEmpty();
		model.startTransaction("DispatcherTest");
	}

	private static final AssertionErrorJobContext CTX = new AssertionErrorJobContext()
	{
		@Override public void stopIfRequested()
		{
			assertFalse(DispatcherModelTest.MODEL.hasCurrentTransaction());
			historyAdd("ctx stop");
		}
		@Override public Duration requestsDeferral()
		{
			assertFalse(DispatcherModelTest.MODEL.hasCurrentTransaction());
			historyAdd("ctx defer");
			return Duration.ZERO;
		}
		@Override public boolean supportsMessage()
		{
			assertFalse(DispatcherModelTest.MODEL.hasCurrentTransaction());
			return false;
		}
		@Override public void incrementProgress()
		{
			historyAdd("ctx progress");
		}
	};

	private void assertIt(
			final boolean pending,
			final Boolean success,
			final Long date)
	{
		assertEquals(pending, item.isToTargetPending(),           "pending");
		assertEquals(success, item.getToTargetUnpendSuccess(),    "success");
		assertEquals(date,    item.getToTargetUnpendDateMillis(), "date");
	}

	@AfterEach void afterEach()
	{
		DispatcherItem.toTarget.reset();
	}
}
