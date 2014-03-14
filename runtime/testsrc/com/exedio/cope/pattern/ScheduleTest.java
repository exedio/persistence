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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.SchemaInfo.getColumnValue;
import static com.exedio.cope.SchemaInfoAssert.assertNoUpdateCounterColumn;
import static com.exedio.cope.pattern.Schedule.Interval.DAILY;
import static com.exedio.cope.pattern.Schedule.Interval.MONTHLY;
import static com.exedio.cope.pattern.Schedule.Interval.WEEKLY;
import static com.exedio.cope.pattern.ScheduleItem.TYPE;
import static com.exedio.cope.pattern.ScheduleItem.assertLogs;
import static com.exedio.cope.pattern.ScheduleItem.report;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.Schedule.Interval;
import com.exedio.cope.pattern.Schedule.Run;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobStop;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class ScheduleTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(ScheduleTest.class, "MODEL");
	}

	public ScheduleTest()
	{
		super(MODEL);
	}

	ScheduleItem item;
	AbsoluteMockClockStrategy clock;
	ArrayList<ExpectedRun> expectedRuns;

	@Override()
	protected void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new ScheduleItem());
		clock = new AbsoluteMockClockStrategy();
		Clock.override(clock);
		expectedRuns = new ArrayList<ExpectedRun>();
	}

	@Override()
	protected void tearDown() throws Exception
	{
		Clock.clearOverride();
		super.tearDown();
	}

	public void testIt()
	{
		// test model

		assertEqualsUnmodifiable(list(TYPE, report.getRunType()), model.getTypes());
		assertEquals(ScheduleItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());

		assertEqualsUnmodifiable(list(TYPE.getThis(), report, report.getEnabled(), report.getInterval(), ScheduleItem.fail), TYPE.getFeatures());
		assertEquals(TYPE, report.getInterval().getType());
		assertEquals("report-interval", report.getInterval().getName());
		assertEquals(TYPE, report.getEnabled().getType());
		assertEquals("report-enabled", report.getEnabled().getName());

		assertEqualsUnmodifiable(list(
				report.getRunType().getThis(),
				report.getRunParent(),
				report.getRunInterval(),
				report.getRunFrom(),
				report.getRunRuns(),
				report.getRunUntil(),
				report.getRunRun(),
				report.getRunElapsed()
			), report.getRunType().getFeatures());

		assertEquals("ScheduleItem-report-Run", report.getRunType().getID());
		assertEquals(Schedule.Run.class, report.getRunType().getJavaClass());
		assertEquals(false, report.getRunType().isBound());
		assertSame(report, report.getRunType().getPattern());
		assertEquals(null, report.getRunType().getSupertype());
		assertEqualsUnmodifiable(list(), report.getRunType().getSubtypes());
		assertEquals(false, report.getRunType().isAbstract());
		assertEquals(Item.class, report.getRunType().getThis().getValueClass().getSuperclass());
		assertEquals(report.getRunType(), report.getRunType().getThis().getValueType());
		assertEquals(model, report.getRunType().getModel());

		assertEquals(report.getRunType(), report.getRunParent().getType());
		assertEquals(report.getRunType(), report.getRunRuns()  .getType());
		assertEquals(report.getRunType(), report.getRunInterval().getType());
		assertEquals(report.getRunType(), report.getRunFrom()  .getType());
		assertEquals(report.getRunType(), report.getRunUntil() .getType());
		assertEquals(report.getRunType(), report.getRunRun()   .getType());
		assertEquals(report.getRunType(), report.getRunElapsed().getType());

		assertEquals("parent", report.getRunParent().getName());
		assertEquals("runs",   report.getRunRuns()  .getName());
		assertEquals("interval",report.getRunInterval().getName());
		assertEquals("from",   report.getRunFrom()  .getName());
		assertEquals("until",  report.getRunUntil() .getName());
		assertEquals("run",    report.getRunRun()   .getName());
		assertEquals("elapsed",report.getRunElapsed().getName());

		assertSame(Locale.GERMAN, report.getLocale());
		try
		{
			new Schedule(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("locale", e.getMessage());
		}

		assertSame(report.getRunParent(), report.getRunRuns().getContainer());
		assertSame(report.getRunFrom(),   report.getRunRuns().getOrder());

		assertFalse(report.getEnabled   ().isAnnotationPresent(Computed.class));
		assertFalse(report.getInterval  ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunParent ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunFrom   ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunUntil  ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunRun    ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunElapsed().isAnnotationPresent(Computed.class));
		assertTrue (report.getRunType   ().isAnnotationPresent(Computed.class));

		assertSerializedSame(report, 380);

		assertEquals(10, getColumnValue(DAILY  ));
		assertEquals(20, getColumnValue(WEEKLY ));
		assertEquals(30, getColumnValue(MONTHLY));

		// test persistence
		assertNoUpdateCounterColumn(report.getRunType());

		assertEquals(DAILY, item.getReportInterval());
		if(oracle) // TODO
			return;

		assertEquals(1, run(date("2008/03/14-01:49:49.888")));
		assertLogs(listg(log(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"))));
		assertRuns(listg(
				ern(DAILY, date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"), date("2008/03/14-01:49:49.888"))));

		assertEquals(0, run(date("2008/03/14-01:49:49.888")));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());

		assertEquals(0, run(date("2008/03/14-23:59:59.999")));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());

		assertEquals(1, run(date("2008/03/15-00:00:00.000")));
		assertLogs(listg(log(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
		assertRuns(listg(
				ern(DAILY, date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"), date("2008/03/15-00:00:00.000"))));

		assertEquals(0, run(date("2008/03/15-00:00:00.000")));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());

		assertEquals(0, run(date("2008/03/15-23:59:59.999")));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());

		assertEquals(1, run(date("2008/03/17-00:00:00.000"))); // TODO should be 2
		assertLogs(listg(log(date("2008/03/16-00:00:00.000"), date("2008/03/17-00:00:00.000"))));
		assertRuns(listg(
				ern(DAILY, date("2008/03/16-00:00:00.000"), date("2008/03/17-00:00:00.000"), date("2008/03/17-00:00:00.000"))));

		item.setFail(true);
		assertEquals(0, run(date("2008/03/17-00:00:00.000")));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());

		try
		{
			run(date("2008/03/18-00:00:00.000"));
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("schedule test failure", e.getMessage());
		}
		assertLogs(listg(log(date("2008/03/17-00:00:00.000"), date("2008/03/18-00:00:00.000"))));
		assertRuns(CopeAssert.<ExpectedRun>listg());

		item.setFail(false);
		assertEquals(1, run(date("2008/03/18-00:00:00.000")));
		assertLogs(listg(log(date("2008/03/17-00:00:00.000"), date("2008/03/18-00:00:00.000"))));
		assertRuns(listg(
				ern(DAILY, date("2008/03/17-00:00:00.000"), date("2008/03/18-00:00:00.000"), date("2008/03/18-00:00:00.000"))));

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
		assertEquals(1, run(date("2008/03/11-00:00:00.000"), 1));
		assertLogs(listg(log(date("2008/03/10-00:00:00.000"), date("2008/03/11-00:00:00.000"))));
		assertRuns(listg(
				ern(DAILY, date("2008/03/10-00:00:00.000"), date("2008/03/11-00:00:00.000"), date("2008/03/11-00:00:00.000"))));
	}

	public void testInterrupter0()
	{
		assertEquals(0, run(date("2008/03/11-00:00:00.000"), 0));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());
	}

	public void testWeekly()
	{
		assertEquals(DAILY, item.getReportInterval());

		item.setReportInterval(WEEKLY);
		assertEquals(WEEKLY, item.getReportInterval());

		if(oracle) // TODO
			return;

		assertEquals(1, run(date("2008/03/14-01:49:49.888")));
		assertLogs(listg(log(date("2008/03/03-00:00:00.000"), date("2008/03/10-00:00:00.000"))));
		assertRuns(listg(
				ern(WEEKLY, date("2008/03/03-00:00:00.000"), date("2008/03/10-00:00:00.000"), date("2008/03/14-01:49:49.888"))));

		assertEquals(0, run(date("2008/03/14-01:49:49.888")));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());

		assertEquals(0, run(date("2008/03/16-23:59:59.999")));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());

		assertEquals(1, run(date("2008/03/17-00:00:00.000")));
		assertLogs(listg(log(date("2008/03/10-00:00:00.000"), date("2008/03/17-00:00:00.000"))));
		assertRuns(listg(
				ern(WEEKLY, date("2008/03/10-00:00:00.000"), date("2008/03/17-00:00:00.000"), date("2008/03/17-00:00:00.000"))));

		assertEquals(1, run(date("2008/03/31-00:00:00.000"))); // TODO should be 2
		assertLogs(listg(log(date("2008/03/24-00:00:00.000"), date("2008/03/31-00:00:00.000"))));
		assertRuns(listg(
				ern(WEEKLY, date("2008/03/24-00:00:00.000"), date("2008/03/31-00:00:00.000"), date("2008/03/31-00:00:00.000"))));
	}

	public void testMonthly()
	{
		assertEquals(DAILY, item.getReportInterval());

		item.setReportInterval(MONTHLY);
		assertEquals(MONTHLY, item.getReportInterval());

		if(oracle) // TODO
			return;

		assertEquals(1, run(date("2008/03/14-01:49:49.888")));
		assertLogs(listg(log(date("2008/02/01-00:00:00.000"), date("2008/03/01-00:00:00.000"))));
		assertRuns(listg(
				ern(MONTHLY, date("2008/02/01-00:00:00.000"), date("2008/03/01-00:00:00.000"), date("2008/03/14-01:49:49.888"))));

		assertEquals(0, run(date("2008/03/14-01:49:49.888")));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());

		assertEquals(0, run(date("2008/03/31-23:59:59.999")));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());

		assertEquals(1, run(date("2008/04/01-00:00:00.000")));
		assertLogs(listg(log(date("2008/03/01-00:00:00.000"), date("2008/04/01-00:00:00.000"))));
		assertRuns(listg(
				ern(MONTHLY, date("2008/03/01-00:00:00.000"), date("2008/04/01-00:00:00.000"), date("2008/04/01-00:00:00.000"))));

		assertEquals(1, run(date("2008/06/01-00:00:00.000"))); // TODO should be 2
		assertLogs(listg(log(date("2008/05/01-00:00:00.000"), date("2008/06/01-00:00:00.000"))));
		assertRuns(listg(
				ern(MONTHLY, date("2008/05/01-00:00:00.000"), date("2008/06/01-00:00:00.000"), date("2008/06/01-00:00:00.000"))));
	}

	public void testEnabled()
	{
		assertEquals(true, item.isReportEnabled());
		assertEquals(DAILY, item.getReportInterval());

		if(oracle) // TODO
			return;

		assertEquals(1, run(date("2008/03/14-01:49:49.888")));
		assertLogs(listg(log(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"))));
		assertRuns(listg(
				ern(DAILY, date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"), date("2008/03/14-01:49:49.888"))));

		item.setReportEnabled(false);
		assertEquals(false, item.isReportEnabled());
		assertEquals(DAILY, item.getReportInterval());
		assertEquals(0, run(date("2008/03/15-00:00:00.000")));
		assertLogs(CopeAssert.<Log>listg());
		assertRuns(CopeAssert.<ExpectedRun>listg());

		item.setReportEnabled(true);
		assertEquals(true, item.isReportEnabled());
		assertEquals(DAILY, item.getReportInterval());
		assertEquals(1, run(date("2008/03/15-00:00:00.000")));
		assertLogs(listg(log(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
		assertRuns(listg(
				ern(DAILY, date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
	}

	private final int run(final Date now)
	{
		final CountJobContext ctx = new CountJobContext();
		run(now, ctx);
		return ctx.progress;
	}

	private final int run(final Date now, final int interruptRequests)
	{
		final CountJobContext ctx = new CountJobContext(){
			int i = interruptRequests;
			@Override() public void stopIfRequested()
			{
				if((i--)<=0) throw new JobStop("requestLimit");
			}
		};
		run(now, ctx);
		return ctx.progress;
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
		return new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS");
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

	private final Log log(final Date from, final Date until)
	{
		return new Log(item, from, until);
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
		@Override()
		public boolean equals(final Object other)
		{
			final Log o = (Log)other;
			return item.equals(o.item) && from.equals(o.from) && until.equals(o.until);
		}

		@Override()
		public int hashCode()
		{
			return item.hashCode() ^ from.hashCode() ^ until.hashCode();
		}

		@Override()
		public String toString()
		{
			return item.toString() + "---" + df().format(from) + "---" + df().format(until);
		}
	}

	private static final ExpectedRun ern(final Interval interval, final Date from, final Date until, final Date run)
	{
		return new ExpectedRun(interval, from, until, run);
	}

	void assertRuns(final List<ExpectedRun> expectedMore)
	{
		expectedRuns.addAll(expectedMore);
		final List<Run> actualList = report.getRunType().search(null, report.getRunType().getThis(), true);
		final ArrayList<ExpectedRun> actual = new ArrayList<ExpectedRun>();
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
		@Override()
		public boolean equals(final Object other)
		{
			final ExpectedRun o = (ExpectedRun)other;
			return interval.equals(o.interval) && from.equals(o.from) && until.equals(o.until) && run.equals(o.run);
		}

		@Override()
		public int hashCode()
		{
			return interval.hashCode() ^ from.hashCode() ^ until.hashCode() ^ run.hashCode();
		}

		@Override()
		public String toString()
		{
			return "" + interval + ' ' + df().format(from) + "---" + df().format(until) + "---" + df().format(run);
		}
	}
}
