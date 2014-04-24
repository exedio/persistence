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

package com.exedio.cope.pattern;

import static com.exedio.cope.SchemaInfoAssert.assertNoUpdateCounterColumn;
import static com.exedio.cope.pattern.Schedule.Interval.DAILY;
import static com.exedio.cope.pattern.Schedule.Interval.MONTHLY;
import static com.exedio.cope.pattern.Schedule.Interval.WEEKLY;
import static com.exedio.cope.pattern.ScheduleItem.assertLogs;
import static com.exedio.cope.pattern.ScheduleItem.report;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.pattern.Schedule.Interval;
import com.exedio.cope.pattern.Schedule.Run;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobStop;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public final class ScheduleTest extends AbstractRuntimeModelTest
{
	public ScheduleTest()
	{
		super(ScheduleModelTest.MODEL);
	}

	ScheduleItem item;
	AbsoluteMockClockStrategy clock;
	ArrayList<ExpectedRun> expectedRuns;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = new ScheduleItem();
		clock = new AbsoluteMockClockStrategy();
		Clock.override(clock);
		expectedRuns = new ArrayList<>();
	}

	@Override
	protected void tearDown() throws Exception
	{
		Clock.clearOverride();
		super.tearDown();
	}

	public void testIt()
	{
		assertNoUpdateCounterColumn(report.getRunType());

		assertEquals(DAILY, item.getReportInterval());
		if(oracle) // TODO
			return;

		run(1, "2008/03/14-01:49:49.888");
		assertLogs(log("2008/03/13-00:00:00.000", "2008/03/14-00:00:00.000"));
		assertRuns(
				ern(DAILY, "2008/03/13-00:00:00.000", "2008/03/14-00:00:00.000", "2008/03/14-01:49:49.888"));

		run(0, "2008/03/14-01:49:49.888");
		assertLogs();
		assertRuns();

		run(0, "2008/03/14-23:59:59.999");
		assertLogs();
		assertRuns();

		run(1, "2008/03/15-00:00:00.000");
		assertLogs(log("2008/03/14-00:00:00.000", "2008/03/15-00:00:00.000"));
		assertRuns(
				ern(DAILY, "2008/03/14-00:00:00.000", "2008/03/15-00:00:00.000", "2008/03/15-00:00:00.000"));

		run(0, "2008/03/15-00:00:00.000");
		assertLogs();
		assertRuns();

		run(0, "2008/03/15-23:59:59.999");
		assertLogs();
		assertRuns();

		run(1, "2008/03/17-00:00:00.000"); // TODO should be 2
		assertLogs(log("2008/03/16-00:00:00.000", "2008/03/17-00:00:00.000"));
		assertRuns(
				ern(DAILY, "2008/03/16-00:00:00.000", "2008/03/17-00:00:00.000", "2008/03/17-00:00:00.000"));

		item.setFail(true);
		run(0, "2008/03/17-00:00:00.000");
		assertLogs();
		assertRuns();

		try
		{
			run(0, "2008/03/18-00:00:00.000");
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("schedule test failure", e.getMessage());
		}
		assertLogs(log("2008/03/17-00:00:00.000", "2008/03/18-00:00:00.000"));
		assertRuns();

		item.setFail(false);
		run(1, "2008/03/18-00:00:00.000");
		assertLogs(log("2008/03/17-00:00:00.000", "2008/03/18-00:00:00.000"));
		assertRuns(
				ern(DAILY, "2008/03/17-00:00:00.000", "2008/03/18-00:00:00.000", "2008/03/18-00:00:00.000"));

		try
		{
			ScheduleItem.runReport((JobContext)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("ctx", e.getMessage());
		}
	}

	public void testInterrupter1()
	{
		run(1, "2008/03/11-00:00:00.000", 1);
		assertLogs(log("2008/03/10-00:00:00.000", "2008/03/11-00:00:00.000"));
		assertRuns(
				ern(DAILY, "2008/03/10-00:00:00.000", "2008/03/11-00:00:00.000", "2008/03/11-00:00:00.000"));
	}

	public void testInterrupter0()
	{
		run(0, "2008/03/11-00:00:00.000", 0);
		assertLogs();
		assertRuns();
	}

	public void testWeekly()
	{
		assertEquals(DAILY, item.getReportInterval());

		item.setReportInterval(WEEKLY);
		assertEquals(WEEKLY, item.getReportInterval());

		if(oracle) // TODO
			return;

		run(1, "2008/03/14-01:49:49.888");
		assertLogs(log("2008/03/03-00:00:00.000", "2008/03/10-00:00:00.000"));
		assertRuns(
				ern(WEEKLY, "2008/03/03-00:00:00.000", "2008/03/10-00:00:00.000", "2008/03/14-01:49:49.888"));

		run(0, "2008/03/14-01:49:49.888");
		assertLogs();
		assertRuns();

		run(0, "2008/03/16-23:59:59.999");
		assertLogs();
		assertRuns();

		run(1, "2008/03/17-00:00:00.000");
		assertLogs(log("2008/03/10-00:00:00.000", "2008/03/17-00:00:00.000"));
		assertRuns(
				ern(WEEKLY, "2008/03/10-00:00:00.000", "2008/03/17-00:00:00.000", "2008/03/17-00:00:00.000"));

		run(1, "2008/03/31-00:00:00.000"); // TODO should be 2
		assertLogs(log("2008/03/24-00:00:00.000", "2008/03/31-00:00:00.000"));
		assertRuns(
				ern(WEEKLY, "2008/03/24-00:00:00.000", "2008/03/31-00:00:00.000", "2008/03/31-00:00:00.000"));
	}

	public void testMonthly()
	{
		assertEquals(DAILY, item.getReportInterval());

		item.setReportInterval(MONTHLY);
		assertEquals(MONTHLY, item.getReportInterval());

		if(oracle) // TODO
			return;

		run(1, "2008/03/14-01:49:49.888");
		assertLogs(log("2008/02/01-00:00:00.000", "2008/03/01-00:00:00.000"));
		assertRuns(
				ern(MONTHLY, "2008/02/01-00:00:00.000", "2008/03/01-00:00:00.000", "2008/03/14-01:49:49.888"));

		run(0, "2008/03/14-01:49:49.888");
		assertLogs();
		assertRuns();

		run(0, "2008/03/31-23:59:59.999");
		assertLogs();
		assertRuns();

		run(1, "2008/04/01-00:00:00.000");
		assertLogs(log("2008/03/01-00:00:00.000", "2008/04/01-00:00:00.000"));
		assertRuns(
				ern(MONTHLY, "2008/03/01-00:00:00.000", "2008/04/01-00:00:00.000", "2008/04/01-00:00:00.000"));

		run(1, "2008/06/01-00:00:00.000"); // TODO should be 2
		assertLogs(log("2008/05/01-00:00:00.000", "2008/06/01-00:00:00.000"));
		assertRuns(
				ern(MONTHLY, "2008/05/01-00:00:00.000", "2008/06/01-00:00:00.000", "2008/06/01-00:00:00.000"));
	}

	public void testEnabled()
	{
		assertEquals(true, item.isReportEnabled());
		assertEquals(DAILY, item.getReportInterval());

		if(oracle) // TODO
			return;

		run(1, "2008/03/14-01:49:49.888");
		assertLogs(log("2008/03/13-00:00:00.000", "2008/03/14-00:00:00.000"));
		assertRuns(
				ern(DAILY, "2008/03/13-00:00:00.000", "2008/03/14-00:00:00.000", "2008/03/14-01:49:49.888"));

		item.setReportEnabled(false);
		assertEquals(false, item.isReportEnabled());
		assertEquals(DAILY, item.getReportInterval());
		run(0, "2008/03/15-00:00:00.000");
		assertLogs();
		assertRuns();

		item.setReportEnabled(true);
		assertEquals(true, item.isReportEnabled());
		assertEquals(DAILY, item.getReportInterval());
		run(1, "2008/03/15-00:00:00.000");
		assertLogs(log("2008/03/14-00:00:00.000", "2008/03/15-00:00:00.000"));
		assertRuns(
				ern(DAILY, "2008/03/14-00:00:00.000", "2008/03/15-00:00:00.000", "2008/03/15-00:00:00.000"));
	}

	private final void run(final int progress, final String now)
	{
		final CountJobContext ctx = new CountJobContext();
		run(date(now), ctx);
		assertEquals(progress, ctx.progress);
	}

	private final void run(final int progress, final String now, final int interruptRequests)
	{
		final CountJobContext ctx = new CountJobContext(){
			int i = interruptRequests;
			@Override public void stopIfRequested()
			{
				if((i--)<=0) throw new JobStop("requestLimit");
			}
		};
		run(date(now), ctx);
		assertEquals(progress, ctx.progress);
	}

	private final void run(final Date now, final JobContext ctx)
	{
		try
		{
			model.commit();
			clock.add(now);
			ScheduleItem.runReport(ctx);
		}
		catch(final JobStop js)
		{
			assertEquals("requestLimit", js.getMessage());
		}
		finally
		{
			model.startTransaction("ScheduleTest");
		}
		clock.assertEmpty();
	}

	static SimpleDateFormat df()
	{
		final SimpleDateFormat result = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS");
		result.setLenient(false);
		return result;
	}

	private static final Date date(final String s)
	{
		try
		{
			return df().parse(s);
		}
		catch(final ParseException e)
		{
			throw new RuntimeException(e);
		}
	}

	private final Log log(final String from, final String until)
	{
		return new Log(item, date(from), date(until));
	}

	static class Log
	{
		final ScheduleItem item;
		final Date from;
		final Date until;

		Log(final ScheduleItem item, final Date from, final Date until)
		{
			this.item = item;
			this.from = from;
			this.until = until;
			assertNotNull(item);
			assertNotNull(from);
			assertNotNull(until);
			assertTrue(from.before(until));
		}

		@SuppressFBWarnings({"NP_EQUALS_SHOULD_HANDLE_NULL_ARGUMENT", "BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS"})
		@Override
		public boolean equals(final Object other)
		{
			final Log o = (Log)other;
			return item.equals(o.item) && from.equals(o.from) && until.equals(o.until);
		}

		@Override
		public int hashCode()
		{
			return item.hashCode() ^ from.hashCode() ^ until.hashCode();
		}

		@Override
		public String toString()
		{
			return item.toString() + "---" + df().format(from) + "---" + df().format(until);
		}
	}

	private static final ExpectedRun ern(final Interval interval, final String from, final String until, final String run)
	{
		return new ExpectedRun(interval, date(from), date(until), date(run));
	}

	void assertRuns(final ExpectedRun... expectedMore)
	{
		expectedRuns.addAll(Arrays.asList(expectedMore));
		final List<Run> actualList = report.getRunType().search(null, report.getRunType().getThis(), true);
		final ArrayList<ExpectedRun> actual = new ArrayList<>();
		for(final Run run : actualList)
			actual.add(new ExpectedRun(run));
		assertEquals(expectedRuns, actual);
	}

	static class ExpectedRun
	{
		final Interval interval;
		final Date from;
		final Date until;
		final Date run;

		ExpectedRun(final Run run)
		{
			this(run.getInterval(), run.getFrom(), run.getUntil(), run.getRun());
			assertTrue(String.valueOf(run.getElapsed()), run.getElapsed()>=0);
		}

		ExpectedRun(final Interval interval, final Date from, final Date until, final Date run)
		{
			this.interval = interval;
			this.from = from;
			this.until = until;
			this.run = run;
			assertNotNull(from);
			assertNotNull(until);
			assertNotNull(run);
			assertTrue(from.before(until));
			assertTrue(!run.before(until));
		}

		@SuppressFBWarnings({"NP_EQUALS_SHOULD_HANDLE_NULL_ARGUMENT", "BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS"})
		@Override
		public boolean equals(final Object other)
		{
			final ExpectedRun o = (ExpectedRun)other;
			return interval.equals(o.interval) && from.equals(o.from) && until.equals(o.until) && run.equals(o.run);
		}

		@Override
		public int hashCode()
		{
			return interval.hashCode() ^ from.hashCode() ^ until.hashCode() ^ run.hashCode();
		}

		@Override
		public String toString()
		{
			return "" + interval + ' ' + df().format(from) + "---" + df().format(until) + "---" + df().format(run);
		}
	}
}
