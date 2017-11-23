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
import static com.exedio.cope.pattern.DispatcherItem.toTarget;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.AssertionErrorJobContext;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DispatcherJobContextMessageTest extends TestWithEnvironment
{
	public DispatcherJobContextMessageTest()
	{
		super(DispatcherModelTest.MODEL);
	}

	DispatcherItem item1, item2, item3, item4;

	@BeforeEach public final void setUp()
	{
		item1 = new DispatcherItem("item1", false);
		item2 = new DispatcherItem("item2", true);
		item3 = new DispatcherItem("item3", false);
		item4 = new DispatcherItem("item4", true);
		DispatcherItem.historyClear();
	}

	@Test public void testSupportsOn()
	{
		assertPending(item1);
		assertPending(item2);
		assertPending(item3);
		assertPending(item4);

		dispatch(new JC());
		historyAssert(
				"supportsMessage=true", "message(probe)",     "stop", "defer", "probe",
				"supportsMessage=true", "message("+item1+")", "stop", "defer", "dispatch " + item1, "progress",
				"supportsMessage=true", "message("+item2+")", "stop", "defer", "dispatch " + item2, "progress",
				"supportsMessage=true", "message(probe)",     "stop", "defer", "probe",
				"supportsMessage=true", "message("+item3+")", "stop", "defer", "dispatch " + item3, "progress",
				"supportsMessage=true", "message("+item4+")", "stop", "defer", "dispatch " + item4, "progress");
		assertSuccess(item1);
		assertPending(item2);
		assertSuccess(item3);
		assertPending(item4);
	}

	@Test public void testSupportsOff()
	{
		assertPending(item1);
		assertPending(item2);
		assertPending(item3);
		assertPending(item4);

		final JC ctx = new JC();
		ctx.supportsMessage = false;
		dispatch(ctx);
		historyAssert(
				"supportsMessage=false", "stop", "defer", "probe",
				"supportsMessage=false", "stop", "defer", "dispatch " + item1, "progress",
				"supportsMessage=false", "stop", "defer", "dispatch " + item2, "progress",
				"supportsMessage=false", "stop", "defer", "probe",
				"supportsMessage=false", "stop", "defer", "dispatch " + item3, "progress",
				"supportsMessage=false", "stop", "defer", "dispatch " + item4, "progress");
		assertSuccess(item1);
		assertPending(item2);
		assertSuccess(item3);
		assertPending(item4);
	}

	private void dispatch(final JC ctx)
	{
		model.commit();
		final Dispatcher.Config config = new Dispatcher.Config();
		final Runnable probe = () -> historyAdd("probe");
		historyAssert();
		DispatcherItem.dispatchToTarget(config, probe, ctx);
		model.startTransaction("DispatcherTest");
	}

	private static class JC extends AssertionErrorJobContext
	{
		@Override public void stopIfRequested()
		{
			assertNoTx();
			historyAdd("stop");
		}
		@Override public Duration requestsDeferral()
		{
			assertNoTx();
			historyAdd("defer");
			return Duration.ZERO;
		}
		boolean supportsMessage = true;
		@Override public boolean supportsMessage()
		{
			assertNoTx();
			historyAdd("supportsMessage=" + supportsMessage);
			return supportsMessage;
		}
		@Override public void setMessage(final String message)
		{
			assertNoTx();
			historyAdd("message(" + message + ")");
		}
		@Override public void incrementProgress()
		{
			assertNoTx();
			historyAdd("progress");
		}
		private static void assertNoTx()
		{
			assertFalse(DispatcherModelTest.MODEL.hasCurrentTransaction());
		}
	}

	private static void assertSuccess(final DispatcherItem item)
	{
		assertEquals(false, item.isToTargetPending());
		assertNotNull(item.getToTargetLastSuccessDate());
	}

	private static void assertPending(final DispatcherItem item)
	{
		assertTrue(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
	}

	@AfterEach public void afterEach()
	{
		toTarget.reset();
		DispatcherItem.historyClear();
	}
}
