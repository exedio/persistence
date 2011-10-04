/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.pattern.Dispatcher.Run;
import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.EmptyJobContext;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobStop;

public class DispatcherTest extends AbstractRuntimeTest
{
	private static final Dispatcher.Config config = new Dispatcher.Config(3, 2);

	public DispatcherTest()
	{
		super(DispatcherModelTest.MODEL);
	}

	DispatcherItem item;
	DispatcherItem item1;
	DispatcherItem item2;
	DispatcherItem item3;
	DispatcherItem item4;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new DispatcherItem("item1", false));
		item2 = deleteOnTearDown(new DispatcherItem("item2", true));
		item3 = deleteOnTearDown(new DispatcherItem("item3", false));
		item4 = deleteOnTearDown(new DispatcherItem("item4", true));
	}

	public void testIt()
	{
		assertPending(item1, 0, list());
		assertPending(item2, 0, list());
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());

		final DateRange d1 = dispatch(2);
		assertSuccess(item1, 1, d1, list());
		assertPending(item2, 0, list(d1));
		assertSuccess(item3, 1, d1, list());
		assertPending(item4, 0, list(d1));

		final DateRange d2 = dispatch(0);
		assertSuccess(item1, 1, d1, list());
		assertPending(item2, 0, list(d1, d2));
		assertSuccess(item3, 1, d1, list());
		assertPending(item4, 0, list(d1, d2));

		DispatcherItem.logs.get(item2).fail = false;
		final DateRange d3 = dispatch(1);
		assertSuccess(item1, 1, d1, list());
		assertSuccess(item2, 1, d3, list(d1, d2));
		assertSuccess(item3, 1, d1, list());
		assertFailed (item4, 0, list(d1, d2, d3));

		dispatch(0);
		assertSuccess(item1, 1, d1, list());
		assertSuccess(item2, 1, d3, list(d1, d2));
		assertSuccess(item3, 1, d1, list());
		assertFailed (item4, 0, list(d1, d2, d3));

		item1.setToTargetPending(true);
		final DateRange d4 = dispatch(1);
		assertSuccess(item1, 2, d4, list());
		assertSuccess(item2, 1, d3, list(d1, d2));
		assertSuccess(item3, 1, d1, list());
		assertFailed (item4, 0, list(d1, d2, d3));

		dispatch(0);
		assertSuccess(item1, 2, d4, list());
		assertSuccess(item2, 1, d3, list(d1, d2));
		assertSuccess(item3, 1, d1, list());
		assertFailed (item4, 0, list(d1, d2, d3));

		try
		{
			DispatcherItem.toTarget.dispatch(HashItem.class, new Dispatcher.Config(), new EmptyJobContext());
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected " + HashItem.class.getName() + ", but was " + DispatcherItem.class.getName(), e.getMessage());
		}
		try
		{
			DispatcherItem.toTarget.dispatch(HashItem.class, null, (JobContext)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("config", e.getMessage());
		}
		try
		{
			DispatcherItem.toTarget.dispatch(HashItem.class, new Dispatcher.Config(), (JobContext)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("ctx", e.getMessage());
		}
	}

	public void testStop0()
	{
		dispatch(0, 0);
		assertPending(item1, 0, list());
		assertPending(item2, 0, list());
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
	}

	public void testStop1()
	{
		final DateRange d = dispatch(1, 1);
		assertSuccess(item1, 1, d, list());
		assertPending(item2, 0, list());
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
	}

	public void testStop2()
	{
		final DateRange d = dispatch(1, 2);
		assertSuccess(item1, 1, d, list());
		assertPending(item2, 0, list(d));
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
	}

	public void testStop3()
	{
		final DateRange d = dispatch(2, 3);
		assertSuccess(item1, 1, d, list());
		assertPending(item2, 0, list(d));
		assertSuccess(item3, 1, d, list());
		assertPending(item4, 0, list());
	}

	public void testStop4()
	{
		final DateRange d = dispatch(2, 4, 4);
		assertSuccess(item1, 1, d, list());
		assertPending(item2, 0, list(d));
		assertSuccess(item3, 1, d, list());
		assertPending(item4, 0, list(d));
	}

	public void testStop5()
	{
		final DateRange d = dispatch(2, 5, 4);
		assertSuccess(item1, 1, d, list());
		assertPending(item2, 0, list(d));
		assertSuccess(item3, 1, d, list());
		assertPending(item4, 0, list(d));
	}

	private static class DateRange
	{
		final Date before;
		final Date after;

		DateRange(final Date before, final Date after)
		{
			this.before = before;
			this.after = after;
			assertTrue(!after.before(before));
		}
	}

	private DateRange dispatch(final int expectedProgress)
	{
		final JC ci = new JC(Integer.MAX_VALUE);
		final DateRange result = dispatch(ci);
		assertEquals(expectedProgress, ci.progress);
		return result;
	}

	private DateRange dispatch(final JC ctx)
	{
		model.commit();
		final Date before = new Date();
		try
		{
			item.dispatchToTarget(config, ctx);
		}
		catch(final JobStop js)
		{
			assertEquals("JC", js.getMessage());
		}
		final Date after = new Date();
		model.startTransaction("DispatcherTest");
		return new DateRange(before, after);
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
			if((requestsToStop++)>=requestsBeforeStop) throw new JobStop("JC");
		}

		@Override
		public void incrementProgress()
		{
			progress++;
		}
	}

	private DateRange dispatch(
			final int expectedProgress,
			final int requestsBeforeStop)
	{
		return dispatch(
				expectedProgress,
				requestsBeforeStop,
				requestsBeforeStop+1);
	}

	private DateRange dispatch(
			final int expectedProgress,
			final int requestsBeforeStop,
			final int expectedRequestsToStop)
	{
		final JC ci = new JC(requestsBeforeStop);
		final DateRange result = dispatch(ci);
		assertEquals(expectedRequestsToStop, ci.requestsToStop);
		assertEquals(expectedProgress, ci.progress);
		return result;
	}

	private static void assertSuccess(
			final DispatcherItem item,
			final int dispatchCountCommitted,
			final DateRange date,
			final List failures)
	{
		final DispatcherItem.Log log = DispatcherItem.logs.get(item);
		assertEquals(false, item.isToTargetPending());
		assertWithin(date.before, date.after, item.getToTargetLastSuccessDate());
		assertTrue(
				String.valueOf(item.getToTargetLastSuccessElapsed())+">="+log.dispatchLastSuccessElapsed,
				item.getToTargetLastSuccessElapsed()>=log.dispatchLastSuccessElapsed);
		assertIt(dispatchCountCommitted, failures.size()+dispatchCountCommitted, failures, item, 0);
	}

	private static void assertPending(
			final DispatcherItem item,
			final int dispatchCountCommitted,
			final List failures)
	{
		assertTrue(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
		assertNull(item.getToTargetLastSuccessElapsed());
		assertIt(dispatchCountCommitted, failures.size(), failures, item, 0);
	}

	private static void assertFailed(
			final DispatcherItem item,
			final int dispatchCountCommitted,
			final List failures)
	{
		assertFalse(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
		assertNull(item.getToTargetLastSuccessElapsed());
		assertIt(dispatchCountCommitted, failures.size(), failures, item, 1);
	}

	private static void assertIt(
			final int dispatchCountCommitted,
			final int dispatchCount,
			final List failures,
			final DispatcherItem item,
			final int notifyFinalFailureCount)
	{
		assertEquals(dispatchCountCommitted, item.getDispatchCountCommitted());
		assertEquals(dispatchCount, DispatcherItem.logs.get(item).dispatchCount);

		final List<Run> actualFailures = item.getToTargetFailures();
		assertTrue(actualFailures.size()<=3);
		assertEquals(failures.size(), actualFailures.size());

		final List<Long> failuresElapsed = DispatcherItem.logs.get(item).dispatchFailureElapsed;
		assertEquals(failures.size(), failuresElapsed.size());
		final Iterator<Long> failureElapsedIter = failuresElapsed.iterator();

		final Iterator expectedFailureIter = failures.iterator();
		for(final Run actual : actualFailures)
		{
			final Long failureElapsed = failureElapsedIter.next();
			final DateRange expected = (DateRange)expectedFailureIter.next();
			assertSame(item.toTarget, actual.getPattern());
			assertEquals(item, actual.getParent());
			assertWithin(expected.before, expected.after, actual.getDate());
			assertTrue(String.valueOf(actual.getElapsed())+">="+failureElapsed, actual.getElapsed()>=failureElapsed.intValue());
			assertFalse(actual.isSuccess());
			assertTrue(actual.getFailure(), actual.getFailure().startsWith(IOException.class.getName()+": "+item.getBody()));
		}
		assertEquals(notifyFinalFailureCount, DispatcherItem.logs.get(item).notifyFinalFailureCount);
	}
}
