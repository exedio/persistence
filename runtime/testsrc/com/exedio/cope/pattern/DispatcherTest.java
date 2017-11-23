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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfoAssert.assertNoUpdateCounterColumn;
import static com.exedio.cope.pattern.DispatcherItem.historyAdd;
import static com.exedio.cope.pattern.DispatcherItem.historyAssert;
import static com.exedio.cope.pattern.DispatcherItem.toTarget;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.Dispatcher.Result;
import com.exedio.cope.pattern.Dispatcher.Run;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.EmptyJobContext;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobStop;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DispatcherTest extends TestWithEnvironment
{
	private static final Dispatcher.Config config = new Dispatcher.Config(3, 2);

	public DispatcherTest()
	{
		super(DispatcherModelTest.MODEL);
	}

	private final ClockRule clockRule = new ClockRule();

	private final LogRule log = new LogRule(Dispatcher.class.getName() + '.' + toTarget.getID());

	DispatcherItem item1;
	DispatcherItem item2;
	DispatcherItem item3;
	DispatcherItem item4;

	@BeforeEach public final void setUp()
	{
		item1 = new DispatcherItem("item1", false);
		item2 = new DispatcherItem("item2", true);
		item3 = new DispatcherItem("item3", false);
		item4 = new DispatcherItem("item4", true);
		log.setLevelDebug();
		DispatcherItem.historyClear();
	}

	@Test void testIt()
	{
		assertNoUpdateCounterColumn(toTarget.getRunType());
		assertEquals("success", getColumnName(toTarget.getRunResult()));

		assertPending(item1);
		assertPending(item2);
		assertPending(item3);
		assertPending(item4);

		final Date[] d1 = dispatch();
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "clock", "dispatch " + item2, "ctx progress",
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item3, "ctx progress",
				"ctx stop", "ctx defer", "clock", "dispatch " + item4, "ctx progress");
		log.assertDebug("dispatching " + item1);
		log.assertInfo("success for " + item1 + ", " + "took " + item1.lastElapsed() + "ms");
		log.assertDebug("dispatching " + item2);
		log.assertWarn("transient failure for " + item2 + ", " + "took " + item2.lastElapsed() + "ms");
		log.assertDebug("dispatching " + item3);
		log.assertInfo("success for " + item3 + ", " + "took " + item3.lastElapsed() + "ms");
		log.assertDebug("dispatching " + item4);
		log.assertWarn("transient failure for " + item4 + ", " + "took " + item4.lastElapsed() + "ms");
		log.assertEmpty();
		assertSuccess(item1, 1, d1[0], success(d1[0]));
		assertPending(item2, failure(d1[1]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertPending(item4, failure(d1[3]));

		final Date[] d2 = dispatch();
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item2, "ctx progress",
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item4, "ctx progress");
		log.assertDebug("dispatching " + item2);
		log.assertWarn("transient failure for " + item2 + ", " + "took " + item2.lastElapsed() + "ms");
		log.assertDebug("dispatching " + item4);
		log.assertWarn("transient failure for " + item4 + ", " + "took " + item4.lastElapsed() + "ms");
		log.assertEmpty();
		assertSuccess(item1, 1, d1[0], success(d1[0]));
		assertPending(item2, failure(d1[1]), failure(d2[0]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertPending(item4, failure(d1[3]), failure(d2[1]));

		DispatcherItem.logs.get(item2).fail = false;
		final Date[] d3 = dispatch();
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item2, "ctx progress",
				"ctx stop", "ctx defer", "clock", "dispatch " + item4, "notifyFinalFailure " + item4, "ctx progress");
		log.assertDebug("dispatching " + item2);
		log.assertInfo("success for " + item2 + ", " + "took " + item2.lastElapsed() + "ms");
		log.assertDebug("dispatching " + item4);
		log.assertError("final failure for " + item4 + ", " + "took " + item4.lastElapsed() + "ms" );
		log.assertEmpty();
		assertSuccess(item1, 1, d1[0], success(d1[0]));
		assertSuccess(item2, 1, d3[0], failure(d1[1]), failure(d2[0]), success(d3[0]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertFailed (item4, failure(d1[3]), failure(d2[1]), finalFailure(d3[1]));

		dispatch();
		historyAssert();
		log.assertEmpty();
		assertSuccess(item1, 1, d1[0], success(d1[0]));
		assertSuccess(item2, 1, d3[0], failure(d1[1]), failure(d2[0]), success(d3[0]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertFailed (item4, failure(d1[3]), failure(d2[1]), finalFailure(d3[1]));

		item1.setToTargetPending(true);
		final Date[] d4 = dispatch();
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item1, "ctx progress");
		log.assertDebug("dispatching " + item1);
		log.assertInfo("success for " + item1 + ", " + "took " + item1.lastElapsed() + "ms");
		log.assertEmpty();
		assertSuccess(item1, 2, d4[0], success(d1[0]), success(d4[0]));
		assertSuccess(item2, 1, d3[0], failure(d1[1]), failure(d2[0]), success(d3[0]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertFailed (item4, failure(d1[3]), failure(d2[1]), finalFailure(d3[1]));

		dispatch();
		historyAssert();
		log.assertEmpty();
		assertSuccess(item1, 2, d4[0], success(d1[0]), success(d4[0]));
		assertSuccess(item2, 1, d3[0], failure(d1[1]), failure(d2[0]), success(d3[0]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertFailed (item4, failure(d1[3]), failure(d2[1]), finalFailure(d3[1]));

		log.assertEmpty();
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad api usage
	@Test void testUnchecked()
	{
		try
		{
			toTarget.dispatch((Class)HashItem.class, new Dispatcher.Config(), new EmptyJobContext());
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected " + HashItem.class.getName() + ", but was " + DispatcherItem.class.getName(), e.getMessage());
		}
		try
		{
			toTarget.dispatch((Class)HashItem.class, null, (JobContext)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("config", e.getMessage());
		}
		try
		{
			toTarget.dispatch((Class)HashItem.class, new Dispatcher.Config(), (JobContext)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("ctx", e.getMessage());
		}
	}

	@Test void testStop0()
	{
		dispatch(0);
		historyAssert(
				"ctx STOP");
		assertPending(item1);
		assertPending(item2);
		assertPending(item3);
		assertPending(item4);
	}

	@Test void testStop0Probe()
	{
		//noinspection PointlessArithmeticExpression
		dispatch(0 + 1); // 1 probe
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx STOP");
		assertPending(item1);
		assertPending(item2);
		assertPending(item3);
		assertPending(item4);
	}

	@Test void testStop1()
	{
		final Date[] d = dispatch(1 + 1); // 1 probe
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item1, "ctx progress",
				"ctx STOP");
		assertSuccess(item1, 1, d[0], success(d[0]));
		assertPending(item2);
		assertPending(item3);
		assertPending(item4);
	}

	@Test void testStop2()
	{
		final Date[] d = dispatch(2 + 1); // 1 probe
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "clock", "dispatch " + item2, "ctx progress",
				"ctx STOP");
		assertSuccess(item1, 1, d[0], success(d[0]));
		assertPending(item2, failure(d[1]));
		assertPending(item3);
		assertPending(item4);
	}

	@Test void testStop3()
	{
		final Date[] d = dispatch(3 + 2); // 2 probes
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "clock", "dispatch " + item2, "ctx progress",
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item3, "ctx progress",
				"ctx STOP");
		assertSuccess(item1, 1, d[0], success(d[0]));
		assertPending(item2, failure(d[1]));
		assertSuccess(item3, 1, d[2], success(d[2]));
		assertPending(item4);
	}

	@Test void testStop4()
	{
		final Date[] d = dispatch(4 + 2); // 2 probes
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "clock", "dispatch " + item2, "ctx progress",
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item3, "ctx progress",
				"ctx stop", "ctx defer", "clock", "dispatch " + item4, "ctx progress");
		assertSuccess(item1, 1, d[0], success(d[0]));
		assertPending(item2, failure(d[1]));
		assertSuccess(item3, 1, d[2], success(d[2]));
		assertPending(item4, failure(d[3]));
	}

	@Test void testStop5()
	{
		final Date[] d = dispatch(5 + 2); // 2 probes
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "clock", "dispatch " + item2, "ctx progress",
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "clock", "dispatch " + item3, "ctx progress",
				"ctx stop", "ctx defer", "clock", "dispatch " + item4, "ctx progress");
		assertSuccess(item1, 1, d[0], success(d[0]));
		assertPending(item2, failure(d[1]));
		assertSuccess(item3, 1, d[2], success(d[2]));
		assertPending(item4, failure(d[3]));
	}

	private Date[] dispatch()
	{
		return dispatch(new JC(Integer.MAX_VALUE));
	}

	private Date[] dispatch(final JC ctx)
	{
		model.commit();
		log.assertEmpty();
		final Runnable probe = () -> historyAdd("probe");
		historyAssert();
		final CS clock = new CS();
		clockRule.override(clock);
		try
		{
			DispatcherItem.dispatchToTarget(config, probe, ctx);
		}
		catch(final JobStop js)
		{
			assertEquals("JC", js.getMessage());
		}
		clockRule.clear();
		model.startTransaction("DispatcherTest");
		return clock.close();
	}

	private static class JC extends AssertionErrorJobContext
	{
		final int requestsBeforeStop;
		private int requestsToStopInternal = 0;

		JC(final int requestsBeforeStop)
		{
			this.requestsBeforeStop = requestsBeforeStop;
		}

		@Override public void stopIfRequested()
		{
			assertFalse(DispatcherModelTest.MODEL.hasCurrentTransaction());
			if((requestsToStopInternal++)>=requestsBeforeStop)
			{
				historyAdd("ctx STOP");
				throw new JobStop("JC");
			}
			else
			{
				historyAdd("ctx stop");
			}
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
	}

	private static class CS implements Clock.Strategy
	{
		private long date = 1000l*60*60*24*1000;
		private ArrayList<Date> history = new ArrayList<>();

		@Override
		public long currentTimeMillis()
		{
			date += 1000l*60;
			history.add(new Date(date));
			historyAdd("clock");
			return date;
		}

		Date[] close()
		{
			final Date[] result = history.toArray(new Date[0]);
			history = null;
			return result;
		}
	}

	private Date[] dispatch(final int requestsBeforeStop)
	{
		return dispatch(new JC(requestsBeforeStop));
	}

	private static void assertSuccess(
			final DispatcherItem item,
			final int dispatchCountCommitted,
			final Date date,
			final ExpectedRun... expectedRuns)
	{
		assertEquals(false, item.isToTargetPending());
		assertEquals(date, item.getToTargetLastSuccessDate());
		assertIt(dispatchCountCommitted, expectedRuns.length, asList(expectedRuns), item, 0);
	}

	private static void assertPending(
			final DispatcherItem item,
			final ExpectedRun... expectedRuns)
	{
		assertTrue(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
		assertNull(item.getToTargetLastSuccessElapsed());
		assertIt(0, expectedRuns.length, asList(expectedRuns), item, 0);
	}

	private static void assertFailed(
			final DispatcherItem item,
			final ExpectedRun... expectedRuns)
	{
		assertFalse(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
		assertNull(item.getToTargetLastSuccessElapsed());
		assertIt(0, expectedRuns.length, asList(expectedRuns), item, 1);
	}

	private static void assertIt(
			final int dispatchCountCommitted,
			final int dispatchCount,
			final List<ExpectedRun> expectedRuns,
			final DispatcherItem item,
			final int notifyFinalFailureCount)
	{
		assertEquals(dispatchCountCommitted, item.getDispatchCountCommitted());
		assertEquals(dispatchCount, DispatcherItem.logs.get(item).dispatchCount);

		final List<Run> actualRuns = item.getToTargetRuns();
		assertTrue(actualRuns.size()<=3);
		assertEquals(expectedRuns.size(), actualRuns.size());

		final List<Long> runsElapsed = DispatcherItem.logs.get(item).dispatchRunElapsed;
		assertEquals(expectedRuns.size(), runsElapsed.size());
		final Iterator<Long> runElapsedIter = runsElapsed.iterator();

		final Iterator<ExpectedRun> expectedRunIter = expectedRuns.iterator();
		final ArrayList<Run> expectedFailures = new ArrayList<>();
		for(final Run actual : actualRuns)
		{
			final Long runElapsed = runElapsedIter.next();
			final ExpectedRun expected = expectedRunIter.next();
			assertSame(toTarget, actual.getPattern());
			assertEquals(item, actual.getParent());
			expected.assertIt(actual);
			assertTrue(actual.getElapsed()>=runElapsed.intValue(), String.valueOf(actual.getElapsed()) + ">=" + runElapsed);
			if(expected.success)
			{
				assertEquals(null, actual.getFailure());
			}
			else
			{
				expectedFailures.add(actual);
				assertTrue(actual.getFailure().startsWith(IOException.class.getName()+": "+item.getBody()), actual.getFailure());
			}
		}
		assertEquals(expectedFailures, item.getToTargetFailures());
		assertEquals(notifyFinalFailureCount, DispatcherItem.logs.get(item).notifyFinalFailureCount);
	}


	private static ExpectedRun success(final Date date)
	{
		return new ExpectedRun(date, Result.success, true);
	}

	private static ExpectedRun failure(final Date date)
	{
		return new ExpectedRun(date, Result.transientFailure, false);
	}

	private static ExpectedRun finalFailure(final Date date)
	{
		return new ExpectedRun(date, Result.finalFailure, false);
	}

	private static final class ExpectedRun
	{
		final Date date;
		final Result result;
		final boolean success;

		ExpectedRun(final Date date, final Result result, final boolean success)
		{
			this.date = new Date(date.getTime()); // Date is not immutable
			this.result = result;
			this.success = success;
			assertNotNull(date);
		}

		void assertIt(final Run actual)
		{
			assertEquals(date, actual.getDate());
			assertEquals(result, actual.getResult());
			assertEquals(success, actual.isSuccess());
		}
	}

	@AfterEach public void afterEach()
	{
		toTarget.reset();
		DispatcherItem.historyClear();
	}
}
