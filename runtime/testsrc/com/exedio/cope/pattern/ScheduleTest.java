/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.ScheduleItem.TYPE;
import static com.exedio.cope.pattern.ScheduleItem.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.pattern.Schedule.Interval;
import com.exedio.cope.pattern.Schedule.Run;
import com.exedio.cope.util.Interrupter;

public final class ScheduleTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(ScheduleItem.TYPE);
	
	public ScheduleTest()
	{
		super(MODEL);
	}
	
	ScheduleItem item;
	ArrayList<ExpectedRun> expectedRuns;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new ScheduleItem());
		expectedRuns = new ArrayList<ExpectedRun>();
	}

	public void testIt()
	{
		// test model
		
		assertEqualsUnmodifiable(list(TYPE, report.getRunType()), model.getTypes());
		assertEquals(ScheduleItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.hasUniqueJavaClass());
		assertEquals(null, item.TYPE.getPattern());
		
		assertEqualsUnmodifiable(list(TYPE.getThis(), report, report.getEnabled(), report.getInterval(), ScheduleItem.fail), TYPE.getFeatures());
		assertEquals(TYPE, report.getInterval().getType());
		assertEquals("reportInterval", report.getInterval().getName());
		assertEquals(TYPE, report.getEnabled().getType());
		assertEquals("reportEnabled", report.getEnabled().getName());
		
		assertEqualsUnmodifiable(list(
				report.getRunType().getThis(),
				report.getRunParent(),
				report.getRunRuns(),
				report.getRunFrom(),
				report.getRunUntil(),
				report.getRunRun(),
				report.getRunElapsed()
			), report.getRunType().getFeatures());
		
		assertEquals("ScheduleItem.reportRun", report.getRunType().getID());
		assertEquals(Schedule.Run.class, report.getRunType().getJavaClass());
		assertEquals(false, report.getRunType().hasUniqueJavaClass());
		assertSame(report, report.getRunType().getPattern());
		assertEquals(null, report.getRunType().getSupertype());
		assertEqualsUnmodifiable(list(), report.getRunType().getSubTypes());
		assertEquals(false, report.getRunType().isAbstract());
		assertEquals(Item.class, report.getRunType().getThis().getValueClass().getSuperclass());
		assertEquals(report.getRunType(), report.getRunType().getThis().getValueType());
		assertEquals(model, report.getRunType().getModel());
		
		assertEquals(report.getRunType(), report.getRunParent().getType());
		assertEquals(report.getRunType(), report.getRunRuns()  .getType());
		assertEquals(report.getRunType(), report.getRunFrom()  .getType());
		assertEquals(report.getRunType(), report.getRunUntil() .getType());
		assertEquals(report.getRunType(), report.getRunRun()   .getType());
		assertEquals(report.getRunType(), report.getRunElapsed().getType());
		
		assertEquals("parent", report.getRunParent().getName());
		assertEquals("runs",   report.getRunRuns()  .getName());
		assertEquals("from",   report.getRunFrom()  .getName());
		assertEquals("until",  report.getRunUntil() .getName());
		assertEquals("run",    report.getRunRun()   .getName());
		assertEquals("elapsed",report.getRunElapsed().getName());
		
		// test persistence
		assertEquals(Interval.DAILY, item.getReportInterval());
		if(oracle) // TODO
			return;
		
		assertEquals(1, run(date("2008/03/14-01:49:49.888")));
		item.assertLogs(listg(log(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"), date("2008/03/14-01:49:49.888"))));
		
		assertEquals(0, run(date("2008/03/14-01:49:49.888")));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		assertEquals(0, run(date("2008/03/14-23:59:59.999")));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		assertEquals(1, run(date("2008/03/15-00:00:00.000")));
		item.assertLogs(listg(log(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
		
		assertEquals(0, run(date("2008/03/15-00:00:00.000")));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		assertEquals(0, run(date("2008/03/15-23:59:59.999")));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		assertEquals(1, run(date("2008/03/17-00:00:00.000"))); // TODO should be 2
		item.assertLogs(listg(log(date("2008/03/16-00:00:00.000"), date("2008/03/17-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/16-00:00:00.000"), date("2008/03/17-00:00:00.000"), date("2008/03/17-00:00:00.000"))));

		item.setFail(true);
		assertEquals(0, run(date("2008/03/17-00:00:00.000")));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		try
		{
			run(date("2008/03/18-00:00:00.000"));
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("schedule test failure", e.getMessage());
		}
		item.assertLogs(listg(log(date("2008/03/17-00:00:00.000"), date("2008/03/18-00:00:00.000"))));
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		item.setFail(false);
		assertEquals(1, run(date("2008/03/18-00:00:00.000")));
		item.assertLogs(listg(log(date("2008/03/17-00:00:00.000"), date("2008/03/18-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/17-00:00:00.000"), date("2008/03/18-00:00:00.000"), date("2008/03/18-00:00:00.000"))));
	}
	
	public void testInterrupter1()
	{
		assertEquals(1, run(date("2008/03/11-00:00:00.000"), 1));
		item.assertLogs(listg(log(date("2008/03/10-00:00:00.000"), date("2008/03/11-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/10-00:00:00.000"), date("2008/03/11-00:00:00.000"), date("2008/03/11-00:00:00.000"))));
	}
	
	public void testInterrupter0()
	{
		assertEquals(0, run(date("2008/03/11-00:00:00.000"), 0));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
	}
	
	public void testWeekly()
	{
		assertEquals(Interval.DAILY, item.getReportInterval());
		
		item.setReportInterval(Interval.WEEKLY);
		assertEquals(Interval.WEEKLY, item.getReportInterval());
		
		if(oracle) // TODO
			return;
		
		assertEquals(1, run(date("2008/03/14-01:49:49.888")));
		item.assertLogs(listg(log(date("2008/03/03-00:00:00.000"), date("2008/03/10-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/03-00:00:00.000"), date("2008/03/10-00:00:00.000"), date("2008/03/14-01:49:49.888"))));
		
		assertEquals(0, run(date("2008/03/14-01:49:49.888")));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		assertEquals(0, run(date("2008/03/16-23:59:59.999")));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		assertEquals(1, run(date("2008/03/17-00:00:00.000")));
		item.assertLogs(listg(log(date("2008/03/10-00:00:00.000"), date("2008/03/17-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/10-00:00:00.000"), date("2008/03/17-00:00:00.000"), date("2008/03/17-00:00:00.000"))));
		
		assertEquals(1, run(date("2008/03/31-00:00:00.000"))); // TODO should be 2
		item.assertLogs(listg(log(date("2008/03/24-00:00:00.000"), date("2008/03/31-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/24-00:00:00.000"), date("2008/03/31-00:00:00.000"), date("2008/03/31-00:00:00.000"))));
	}
	
	public void testMonthly()
	{
		assertEquals(Interval.DAILY, item.getReportInterval());
		
		item.setReportInterval(Interval.MONTHLY);
		assertEquals(Interval.MONTHLY, item.getReportInterval());
		
		if(oracle) // TODO
			return;
		
		assertEquals(1, run(date("2008/03/14-01:49:49.888")));
		item.assertLogs(listg(log(date("2008/02/01-00:00:00.000"), date("2008/03/01-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/02/01-00:00:00.000"), date("2008/03/01-00:00:00.000"), date("2008/03/14-01:49:49.888"))));
		
		assertEquals(0, run(date("2008/03/14-01:49:49.888")));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		assertEquals(0, run(date("2008/03/31-23:59:59.999")));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		assertEquals(1, run(date("2008/04/01-00:00:00.000")));
		item.assertLogs(listg(log(date("2008/03/01-00:00:00.000"), date("2008/04/01-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/01-00:00:00.000"), date("2008/04/01-00:00:00.000"), date("2008/04/01-00:00:00.000"))));
		
		assertEquals(1, run(date("2008/06/01-00:00:00.000"))); // TODO should be 2
		item.assertLogs(listg(log(date("2008/05/01-00:00:00.000"), date("2008/06/01-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/05/01-00:00:00.000"), date("2008/06/01-00:00:00.000"), date("2008/06/01-00:00:00.000"))));
	}
	
	public void testEnabled()
	{
		assertEquals(true, item.isReportEnabled());
		assertEquals(Interval.DAILY, item.getReportInterval());
		
		if(oracle) // TODO
			return;
		
		assertEquals(1, run(date("2008/03/14-01:49:49.888")));
		item.assertLogs(listg(log(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"), date("2008/03/14-01:49:49.888"))));
		
		item.setReportEnabled(false);
		assertEquals(false, item.isReportEnabled());
		assertEquals(Interval.DAILY, item.getReportInterval());
		assertEquals(0, run(date("2008/03/15-00:00:00.000")));
		item.assertLogs(ScheduleTest.<Log>listg());
		assertRuns(ScheduleTest.<ExpectedRun>listg());
		
		item.setReportEnabled(true);
		assertEquals(true, item.isReportEnabled());
		assertEquals(Interval.DAILY, item.getReportInterval());
		assertEquals(1, run(date("2008/03/15-00:00:00.000")));
		item.assertLogs(listg(log(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
		assertRuns(listg(
				ern(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
	}
	
	private final int run(final Date now)
	{
		return run(now, null);
	}
	
	private final int run(final Date now, final int interruptRequests)
	{
		return run(now, new Interrupter(){
			int i = interruptRequests;
			public boolean isRequested()
			{
				return (i--)<=0;
			}
		});
	}
	
	private final int run(final Date now, final Interrupter interrupter)
	{
		try
		{
			model.commit();
			return report.run(ScheduleItem.class, interrupter, now);
		}
		finally
		{
			model.startTransaction("ScheduleTest");
		}
	}
	
	static final SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS");
	
	private static final Date date(final String s)
	{
		try
		{
			return df.parse(s);
		}
		catch(ParseException e)
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
			return item.toString() + "---" + df.format(from) + "---" + df.format(until);
		}
	}
	
	private final ExpectedRun ern(final Date from, final Date until, final Date run)
	{
		return new ExpectedRun(from, until, run);
	}
	
	void assertRuns(final List<ExpectedRun> expectedMore)
	{
		expectedRuns.addAll(expectedMore);
		final List<Run> actualList = report.runType.search(null, report.runType.getThis(), true);
		final ArrayList<ExpectedRun> actual = new ArrayList<ExpectedRun>();
		for(final Run run : actualList)
			actual.add(new ExpectedRun(run));
		assertEquals(expectedRuns, actual);
	}
	
	static class ExpectedRun
	{
		final Date from;
		final Date until;
		final Date run;
		
		ExpectedRun(final Run run)
		{
			this(run.getFrom(), run.getUntil(), run.getRun());
			assertTrue(run.getElapsed()>=0);
		}

		ExpectedRun(final Date from, final Date until, final Date run)
		{
			this.from = from;
			this.until = until;
			this.run = run;
			assertNotNull(from);
			assertNotNull(until);
			assertNotNull(run);
			assertTrue(from.before(until));
			assertTrue(!run.before(until));
		}

		@Override
		public boolean equals(final Object other)
		{
			final ExpectedRun o = (ExpectedRun)other;
			return from.equals(o.from) && until.equals(o.until) && run.equals(o.run);
		}

		@Override
		public int hashCode()
		{
			return from.hashCode() ^ until.hashCode() ^ run.hashCode();
		}

		@Override
		public String toString()
		{
			return df.format(from) + "---" + df.format(until) + "---" + df.format(run);
		}
	}
}
