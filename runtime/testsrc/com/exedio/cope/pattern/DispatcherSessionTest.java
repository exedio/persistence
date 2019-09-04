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

import static com.exedio.cope.PrometheusMeterRegistrar.getMeters;
import static com.exedio.cope.pattern.DispatcherSessionItem.historyAdd;
import static com.exedio.cope.pattern.DispatcherSessionItem.historyAssert;
import static com.exedio.cope.pattern.DispatcherSessionItem.toTarget;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.Dispatcher.Result;
import com.exedio.cope.pattern.Dispatcher.Run;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.JobContext;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DispatcherSessionTest extends TestWithEnvironment
{
	private static final Dispatcher.Config config = new Dispatcher.Config(3, 2);

	public DispatcherSessionTest()
	{
		super(MODEL);
	}

	private final LogRule log = new LogRule(Dispatcher.class.getName() + '.' + toTarget.getID());

	@BeforeEach final void setUp()
	{
		log.setLevelDebug();
		DispatcherSessionItem.historyClear();
	}

	@Test void testSuccess()
	{
		assertEquals(asList(
				"dispatch result=failure",
				"dispatch result=success",
				"probe",
				"purge",
				"session event=close",
				"session event=create"),
				getMeters(toTarget));

		final DispatcherSessionItem item1 = new DispatcherSessionItem(false);
		final DispatcherSessionItem item2 = new DispatcherSessionItem(false);
		assertPending(item1);
		assertPending(item2);

		dispatch(2);
		historyAssert(
				"ctx stop", "ctx defer", "session 0 new",
				"ctx stop", "ctx defer", "session 0 dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "session 0 dispatch " + item2, "ctx progress",
				"session 0 close");
		log.assertDebug("creating session");
		log.assertInfoMS("created session, took XXms");
		log.assertDebug("dispatching " + item1);
		log.assertInfoMS("success for " + item1 + ", took XXms");
		log.assertDebug("dispatching " + item2);
		log.assertInfoMS("success for " + item2 + ", took XXms");
		log.assertDebug("closing session after dispatching");
		log.assertInfoMS("closed session after dispatching, took XXms");
		log.assertEmpty();
		createT.assertCount(1);
		closeT .assertCount(1);
		assertSuccess(item1, success());
		assertSuccess(item2, success());
		DispatcherSessionItem.assertSessionsClosed(1);
	}

	@Test void testFailure()
	{
		final DispatcherSessionItem item1 = new DispatcherSessionItem(true);
		final DispatcherSessionItem item2 = new DispatcherSessionItem(true);
		assertPending(item1);
		assertPending(item2);

		dispatch(1000);
		historyAssert(
				"ctx stop", "ctx defer", "session 0 new",
				"ctx stop", "ctx defer", "session 0 dispatch " + item1, "session 0 close", "ctx progress",
				"ctx stop", "ctx defer", "session 1 new",
				"ctx stop", "ctx defer", "session 1 dispatch " + item2, "session 1 close", "ctx progress");
		log.assertDebug("creating session");
		log.assertInfoMS("created session, took XXms");
		log.assertDebug("dispatching " + item1);
		log.assertWarnMS("transient failure for " + item1 + ", took XXms, 2 of 3 runs remaining");
		log.assertDebug("closing session after failure");
		log.assertInfoMS("closed session after failure, took XXms");
		log.assertDebug("creating session");
		log.assertInfoMS("created session, took XXms");
		log.assertDebug("dispatching " + item2);
		log.assertWarnMS("transient failure for " + item2 + ", took XXms, 2 of 3 runs remaining");
		log.assertDebug("closing session after failure");
		log.assertInfoMS("closed session after failure, took XXms");
		log.assertEmpty();
		createT.assertCount(2);
		closeT .assertCount(2);
		assertPending(item1, failure());
		assertPending(item2, failure());
		DispatcherSessionItem.assertSessionsClosed(2);
	}

	@Test void testEmpty()
	{
		dispatch(1);
		historyAssert();
		log.assertEmpty();
		createT.assertCount(0);
		closeT .assertCount(0);
		DispatcherSessionItem.assertSessionsClosed(0);
	}

	@Test void testRestart1()
	{
		final DispatcherSessionItem item1 = new DispatcherSessionItem(false);
		final DispatcherSessionItem item2 = new DispatcherSessionItem(false);
		assertPending(item1);
		assertPending(item2);

		dispatch(1);
		historyAssert(
				"ctx stop", "ctx defer", "session 0 new",
				"ctx stop", "ctx defer", "session 0 dispatch " + item1, "ctx progress", "session 0 close",
				"ctx stop", "ctx defer", "session 1 new",
				"ctx stop", "ctx defer", "session 1 dispatch " + item2, "ctx progress",
				"session 1 close");
		log.assertDebug("creating session");
		log.assertInfoMS("created session, took XXms");
		log.assertDebug("dispatching " + item1);
		log.assertInfoMS("success for " + item1 + ", took XXms");
		log.assertDebug("closing session after exhausting limit of 1");
		log.assertInfoMS("closed session after exhausting limit of 1, took XXms");
		log.assertDebug("creating session");
		log.assertInfoMS("created session, took XXms");
		log.assertDebug("dispatching " + item2);
		log.assertInfoMS("success for " + item2 + ", took XXms");
		log.assertDebug("closing session after dispatching");
		log.assertInfoMS("closed session after dispatching, took XXms");
		log.assertEmpty();
		createT.assertCount(2);
		closeT .assertCount(2);
		assertSuccess(item1, success());
		assertSuccess(item2, success());
		DispatcherSessionItem.assertSessionsClosed(2);
	}

	@Test void testRestartLong()
	{
		final DispatcherSessionItem item1 = new DispatcherSessionItem(false);
		final DispatcherSessionItem item2 = new DispatcherSessionItem(false);
		final DispatcherSessionItem item3 = new DispatcherSessionItem(false);
		final DispatcherSessionItem item4 = new DispatcherSessionItem(false);
		final DispatcherSessionItem item5 = new DispatcherSessionItem(false);
		assertPending(item1);
		assertPending(item2);
		assertPending(item3);
		assertPending(item4);
		assertPending(item5);

		dispatch(2);
		historyAssert(
				"ctx stop", "ctx defer", "session 0 new",
				"ctx stop", "ctx defer", "session 0 dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "session 0 dispatch " + item2, "ctx progress", "session 0 close",
				"ctx stop", "ctx defer", "session 1 new",
				"ctx stop", "ctx defer", "session 1 dispatch " + item3, "ctx progress",
				"ctx stop", "ctx defer", "session 1 dispatch " + item4, "ctx progress", "session 1 close",
				"ctx stop", "ctx defer", "session 2 new",
				"ctx stop", "ctx defer", "session 2 dispatch " + item5, "ctx progress",
				"session 2 close");
		log.assertDebug("creating session");
		log.assertInfoMS("created session, took XXms");
		log.assertDebug("dispatching " + item1);
		log.assertInfoMS("success for " + item1 + ", took XXms");
		log.assertDebug("dispatching " + item2);
		log.assertInfoMS("success for " + item2 + ", took XXms");
		log.assertDebug("closing session after exhausting limit of 2");
		log.assertInfoMS("closed session after exhausting limit of 2, took XXms");
		log.assertDebug("creating session");
		log.assertInfoMS("created session, took XXms");
		log.assertDebug("dispatching " + item3);
		log.assertInfoMS("success for " + item3 + ", took XXms");
		log.assertDebug("dispatching " + item4);
		log.assertInfoMS("success for " + item4 + ", took XXms");
		log.assertDebug("closing session after exhausting limit of 2");
		log.assertInfoMS("closed session after exhausting limit of 2, took XXms");
		log.assertDebug("creating session");
		log.assertInfoMS("created session, took XXms");
		log.assertDebug("dispatching " + item5);
		log.assertInfoMS("success for " + item5 + ", took XXms");
		log.assertDebug("closing session after dispatching");
		log.assertInfoMS("closed session after dispatching, took XXms");
		log.assertEmpty();
		createT.assertCount(3);
		closeT .assertCount(3);
		assertSuccess(item1, success());
		assertSuccess(item2, success());
		DispatcherSessionItem.assertSessionsClosed(3);
	}


	private void dispatch(final int sessionLimit)
	{
		model.commit();
		log.assertEmpty();
		historyAssert();
		DispatcherSessionItem.dispatchToTarget(config.sessionLimit(sessionLimit), CONTEXT);
		model.startTransaction("DispatcherSessionTest");
	}

	private static final JobContext CONTEXT = new AssertionErrorJobContext()
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
			return false;
		}

		@Override
		public void incrementProgress()
		{
			historyAdd("ctx progress");
		}
	};

	private static void assertSuccess(
			final DispatcherSessionItem item,
			final ExpectedRun... expectedRuns)
	{
		assertEquals(false, item.isToTargetPending());
		assertIt(1, asList(expectedRuns), item);
	}

	private static void assertPending(
			final DispatcherSessionItem item,
			final ExpectedRun... expectedRuns)
	{
		assertTrue(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
		assertNull(item.getToTargetLastSuccessElapsed());
		assertIt(0, asList(expectedRuns), item);
	}

	private static void assertIt(
			final int dispatchCountCommitted,
			final List<ExpectedRun> expectedRuns,
			final DispatcherSessionItem item)
	{
		assertEquals(dispatchCountCommitted, item.getDispatchCountCommitted());

		final List<Run> actualRuns = item.getToTargetRuns();
		assertTrue(actualRuns.size()<=3);
		assertEquals(expectedRuns.size(), actualRuns.size());

		final Iterator<ExpectedRun> expectedRunIter = expectedRuns.iterator();
		final ArrayList<Run> expectedFailures = new ArrayList<>();
		for(final Run actual : actualRuns)
		{
			final ExpectedRun expected = expectedRunIter.next();
			assertSame(toTarget, actual.getPattern());
			assertEquals(item, actual.getParent());
			expected.assertIt(actual);
			if(expected.success)
			{
				assertEquals(null, actual.getFailure());
			}
			else
			{
				expectedFailures.add(actual);
				assertTrue(actual.getFailure().startsWith(IOException.class.getName()), actual.getFailure());
			}
		}
		assertEquals(expectedFailures, item.getToTargetFailures());
	}


	private static ExpectedRun success()
	{
		return new ExpectedRun(0, Result.success, true);
	}

	private static ExpectedRun failure()
	{
		return new ExpectedRun(2, Result.transientFailure, false);
	}

	private static final class ExpectedRun
	{
		final int remaining;
		final Result result;
		final boolean success;

		ExpectedRun(
				final int remaining,
				final Result result,
				final boolean success)
		{
			this.remaining = remaining;
			this.result = result;
			this.success = success;
		}

		void assertIt(final Run actual)
		{
			assertAll(
					() -> assertEquals(remaining, actual.getRemaining(), "remaining"),
					() -> assertEquals(3, actual.getLimit(), "limit"),
					() -> assertEquals(result, actual.getResult(), "result"),
					() -> assertEquals(success, actual.isSuccess(), "success"));
		}
	}

	private final FeatureTimerTester createT = new FeatureTimerTester(toTarget, "session", "event", "create");
	private final FeatureTimerTester closeT  = new FeatureTimerTester(toTarget, "session", "event", "close");

	@AfterEach void afterEach()
	{
		toTarget.reset();
		DispatcherSessionItem.historyClear();
	}

	static final Model MODEL = new Model(DispatcherSessionItem.TYPE);
}
