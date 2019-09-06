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
import static com.exedio.cope.pattern.DispatcherWithoutPurgeItem.toTarget;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.Dispatcher.Run;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.RelativeMockClockStrategy;
import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.JobStop;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DispatcherWithoutPurgeTest extends TestWithEnvironment
{
	private static final Dispatcher.Config config = new Dispatcher.Config(3, 2);

	public DispatcherWithoutPurgeTest()
	{
		super(DispatcherWithoutPurgeModelTest.MODEL);
	}

	private final RelativeMockClockStrategy clock = new RelativeMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	private final LogRule log = new LogRule(Dispatcher.class.getName() + '.' + toTarget.getID());

	DispatcherWithoutPurgeItem item1;
	DispatcherWithoutPurgeItem item2;
	DispatcherWithoutPurgeItem item3;
	DispatcherWithoutPurgeItem item4;

	@BeforeEach final void setUp()
	{
		item1 = new DispatcherWithoutPurgeItem("item1", false);
		item2 = new DispatcherWithoutPurgeItem("item2", true);
		item3 = new DispatcherWithoutPurgeItem("item3", false);
		item4 = new DispatcherWithoutPurgeItem("item4", true);
		clockRule.override(clock);
		log.setLevelDebug();
	}

	@Test void testIt()
	{
		assertNoUpdateCounterColumn(toTarget.getRunType());
		assertEquals("success", getColumnName(toTarget.getRunResult()));

		assertPending(item1, 0, list());
		assertPending(item2, 0, list());
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());

		final Date[] d1 = dispatch(4);
		log.assertDebug("probing");
		log.assertInfoMS("probed, took XXms");
		log.assertDebug("dispatching " + item1);
		log.assertInfo("success for " + item1 + ", took " + item1.lastElapsed() + "ms");
		log.assertDebug("dispatching " + item2);
		log.assertWarn("transient failure for " + item2 + ", took " + item2.lastElapsed() + "ms, 2 of 3 runs remaining");
		log.assertDebug("probing");
		log.assertInfoMS("probed, took XXms");
		log.assertDebug("dispatching " + item3);
		log.assertInfo("success for " + item3 + ", took " + item3.lastElapsed() + "ms");
		log.assertDebug("dispatching " + item4);
		log.assertWarn("transient failure for " + item4 + ", took " + item4.lastElapsed() + "ms, 2 of 3 runs remaining");
		log.assertEmpty();
		assertSuccess(item1, 1, d1[0], list());
		assertPending(item2, 0, list(d1[1]));
		assertSuccess(item3, 1, d1[2], list());
		assertPending(item4, 0, list(d1[3]));

		final Date[] d2 = dispatch(2);
		log.assertDebug("probing");
		log.assertInfoMS("probed, took XXms");
		log.assertDebug("dispatching " + item2);
		log.assertWarn("transient failure for " + item2 + ", took " + item2.lastElapsed() + "ms, 1 of 3 runs remaining");
		log.assertDebug("probing");
		log.assertInfoMS("probed, took XXms");
		log.assertDebug("dispatching " + item4);
		log.assertWarn("transient failure for " + item4 + ", took " + item4.lastElapsed() + "ms, 1 of 3 runs remaining");
		log.assertEmpty();
		assertSuccess(item1, 1, d1[0], list());
		assertPending(item2, 0, list(d1[1], d2[0]));
		assertSuccess(item3, 1, d1[2], list());
		assertPending(item4, 0, list(d1[3], d2[1]));

		DispatcherWithoutPurgeItem.logs.get(item2).fail = false;
		final Date[] d3 = dispatch(2);
		log.assertDebug("probing");
		log.assertInfoMS("probed, took XXms");
		log.assertDebug("dispatching " + item2);
		log.assertInfo("success for " + item2 + ", took " + item2.lastElapsed() + "ms");
		log.assertDebug("dispatching " + item4);
		log.assertError("final failure for " + item4 + ", took " + item4.lastElapsed() + "ms, 3 runs exhausted" );
		log.assertEmpty();
		assertSuccess(item1, 1, d1[0], list());
		assertSuccess(item2, 1, d3[0], list(d1[1], d2[0]));
		assertSuccess(item3, 1, d1[2], list());
		assertFailed (item4, 0, list(d1[3], d2[1], d3[1]));

		dispatch(0);
		log.assertEmpty();
		assertSuccess(item1, 1, d1[0], list());
		assertSuccess(item2, 1, d3[0], list(d1[1], d2[0]));
		assertSuccess(item3, 1, d1[2], list());
		assertFailed (item4, 0, list(d1[3], d2[1], d3[1]));

		item1.setToTargetPending(true);
		final Date[] d4 = dispatch(1);
		log.assertDebug("probing");
		log.assertInfoMS("probed, took XXms");
		log.assertDebug("dispatching " + item1);
		log.assertInfo("success for " + item1 + ", took " + item1.lastElapsed() + "ms");
		log.assertEmpty();
		assertSuccess(item1, 2, d4[0], list());
		assertSuccess(item2, 1, d3[0], list(d1[1], d2[0]));
		assertSuccess(item3, 1, d1[2], list());
		assertFailed (item4, 0, list(d1[3], d2[1], d3[1]));

		dispatch(0);
		log.assertEmpty();
		assertSuccess(item1, 2, d4[0], list());
		assertSuccess(item2, 1, d3[0], list(d1[1], d2[0]));
		assertSuccess(item3, 1, d1[2], list());
		assertFailed (item4, 0, list(d1[3], d2[1], d3[1]));

		log.assertEmpty();
	}

	@Test void testStop0()
	{
		dispatch(0, 0);
		assertPending(item1, 0, list());
		assertPending(item2, 0, list());
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
	}

	@Test void testStop0Probe()
	{
		//noinspection PointlessArithmeticExpression
		dispatch(0, 0 + 1); // 1 probe
		assertPending(item1, 0, list());
		assertPending(item2, 0, list());
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
	}

	@Test void testStop1()
	{
		final Date[] d = dispatch(1, 1 + 1); // 1 probe
		assertSuccess(item1, 1, d[0], list());
		assertPending(item2, 0, list());
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
	}

	@Test void testStop2()
	{
		final Date[] d = dispatch(2, 2 + 1); // 1 probe
		assertSuccess(item1, 1, d[0], list());
		assertPending(item2, 0, list(d[1]));
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
	}

	@Test void testStop3()
	{
		final Date[] d = dispatch(3, 3 + 2); // 2 probes
		assertSuccess(item1, 1, d[0], list());
		assertPending(item2, 0, list(d[1]));
		assertSuccess(item3, 1, d[2], list());
		assertPending(item4, 0, list());
	}

	@Test void testStop4()
	{
		final Date[] d = dispatch(4, 4 + 2, 4 + 2); // 2 probes
		assertSuccess(item1, 1, d[0], list());
		assertPending(item2, 0, list(d[1]));
		assertSuccess(item3, 1, d[2], list());
		assertPending(item4, 0, list(d[3]));
	}

	@Test void testStop5()
	{
		final Date[] d = dispatch(4, 5 + 2, 4 + 2); // 2 probes
		assertSuccess(item1, 1, d[0], list());
		assertPending(item2, 0, list(d[1]));
		assertSuccess(item3, 1, d[2], list());
		assertPending(item4, 0, list(d[3]));
	}

	private Date[] dispatch(final int expectedProgress)
	{
		final JC ci = new JC(Integer.MAX_VALUE);
		final Date[] result = dispatch(expectedProgress, ci);
		assertEquals(expectedProgress, ci.progress);
		return result;
	}

	private Date[] dispatch(final int expectedDates, final JC ctx)
	{
		model.commit();
		final Date[] dates = new Date[expectedDates];
		for(int i = 0; i<expectedDates; i++)
			dates[i] = new Date(clock.addOffset(10));
		log.assertEmpty();
		try
		{
			DispatcherWithoutPurgeItem.dispatchToTarget(config, ctx);
		}
		catch(final JobStop js)
		{
			assertEquals("JC", js.getMessage());
		}
		clock.assertEmpty();
		model.startTransaction("DispatcherTest");
		return dates;
	}

	private static class JC extends AssertionErrorJobContext
	{
		final int requestsBeforeStop;
		int requestsToStop = 0;
		int progress = 0;

		JC(final int requestsBeforeStop)
		{
			this.requestsBeforeStop = requestsBeforeStop;
		}

		@Override public void stopIfRequested()
		{
			assertFalse(DispatcherWithoutPurgeModelTest.MODEL.hasCurrentTransaction());
			if((requestsToStop++)>=requestsBeforeStop) throw new JobStop("JC");
		}

		@Override public Duration requestsDeferral()
		{
			assertFalse(DispatcherWithoutPurgeModelTest.MODEL.hasCurrentTransaction());
			return Duration.ZERO;
		}

		@Override public boolean supportsMessage()
		{
			assertFalse(DispatcherModelTest.MODEL.hasCurrentTransaction());
			return false;
		}

		@Override
		public void incrementProgress()
		{
			progress++;
		}
	}

	private Date[] dispatch(
			final int expectedProgress,
			final int requestsBeforeStop)
	{
		return dispatch(
				expectedProgress,
				requestsBeforeStop,
				requestsBeforeStop+1);
	}

	private Date[] dispatch(
			final int expectedProgress,
			final int requestsBeforeStop,
			final int expectedRequestsToStop)
	{
		final JC ci = new JC(requestsBeforeStop);
		final Date[] result = dispatch(expectedProgress, ci);
		assertEquals(expectedRequestsToStop, ci.requestsToStop);
		assertEquals(expectedProgress, ci.progress);
		return result;
	}

	private static void assertSuccess(
			final DispatcherWithoutPurgeItem item,
			final int dispatchCountCommitted,
			final Date date,
			final List<?> failures)
	{
		final DispatcherWithoutPurgeItem.Log log = DispatcherWithoutPurgeItem.logs.get(item);
		assertEquals(false, item.isToTargetPending());
		assertEquals(date, item.getToTargetLastSuccessDate());
		assertTrue(
				item.getToTargetLastSuccessElapsed()>=log.dispatchLastSuccessElapsed,
				item.getToTargetLastSuccessElapsed()+">="+log.dispatchLastSuccessElapsed);
		assertIt(dispatchCountCommitted, failures.size()+dispatchCountCommitted, failures, item, 0);
	}

	private static void assertPending(
			final DispatcherWithoutPurgeItem item,
			final int dispatchCountCommitted,
			final List<?> failures)
	{
		assertTrue(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
		assertNull(item.getToTargetLastSuccessElapsed());
		assertIt(dispatchCountCommitted, failures.size(), failures, item, 0);
	}

	private static void assertFailed(
			final DispatcherWithoutPurgeItem item,
			final int dispatchCountCommitted,
			final List<?> failures)
	{
		assertFalse(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
		assertNull(item.getToTargetLastSuccessElapsed());
		assertIt(dispatchCountCommitted, failures.size(), failures, item, 1);
	}

	private static void assertIt(
			final int dispatchCountCommitted,
			final int dispatchCount,
			final List<?> failures,
			final DispatcherWithoutPurgeItem item,
			final int notifyFinalFailureCount)
	{
		assertEquals(dispatchCountCommitted, item.getDispatchCountCommitted());
		assertEquals(dispatchCount, DispatcherWithoutPurgeItem.logs.get(item).dispatchCount);

		final List<Run> actualFailures = item.getToTargetFailures();
		assertTrue(actualFailures.size()<=3);
		assertEquals(failures.size(), actualFailures.size());

		final List<Long> failuresElapsed = DispatcherWithoutPurgeItem.logs.get(item).dispatchFailureElapsed;
		assertEquals(failures.size(), failuresElapsed.size());
		final Iterator<Long> failureElapsedIter = failuresElapsed.iterator();

		final Iterator<?> expectedFailureIter = failures.iterator();
		for(final Run actual : actualFailures)
		{
			final Long failureElapsed = failureElapsedIter.next();
			final Date expected = (Date)expectedFailureIter.next();
			assertSame(toTarget, actual.getPattern());
			assertEquals(item, actual.getParent());
			assertEquals(expected, actual.getDate());
			assertTrue(actual.getElapsed()>=failureElapsed.intValue(), actual.getElapsed()+">="+failureElapsed);
			assertFalse(actual.isSuccess());
			assertTrue(actual.getFailure().startsWith(IOException.class.getName()+": "+item.getBody()), actual.getFailure());
		}
		assertEquals(notifyFinalFailureCount, DispatcherWithoutPurgeItem.logs.get(item).notifyFinalFailureCount);
	}

	@AfterEach void afterEach()
	{
		toTarget.reset();
	}
}
