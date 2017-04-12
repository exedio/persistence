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

import static com.exedio.cope.SchemaInfoAssert.assertNoUpdateCounterColumn;
import static com.exedio.cope.pattern.Schedule.Interval.DAILY;
import static com.exedio.cope.pattern.Schedule.Interval.HOURLY;
import static com.exedio.cope.pattern.Schedule.Interval.MONTHLY;
import static com.exedio.cope.pattern.Schedule.Interval.WEEKLY;
import static com.exedio.cope.pattern.ScheduleItem.assertLogs;
import static com.exedio.cope.pattern.ScheduleItem.report;
import static com.exedio.cope.pattern.ScheduleItem.reportRunParent;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.pattern.Schedule.Interval;
import com.exedio.cope.pattern.Schedule.Run;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobStop;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class ScheduleTest extends TestWithEnvironment
{
	public ScheduleTest()
	{
		super(ScheduleModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	@Rule public final RuleChain ruleChain = RuleChain.outerRule(clockRule);

	ScheduleItem item;
	ArrayList<ExpectedRun> expectedRuns;

	@Before public final void setUp()
	{
		item = new ScheduleItem();
		final ScheduleItem disabledItem = new ScheduleItem();
		disabledItem.setReportEnabled(false);
		final ScheduleItem recentItem = new ScheduleItem();
		final Run recentRun = report.getRunType().newItem(
				reportRunParent().map(recentItem),
				report.getRunInterval().map(DAILY),
				report.getRunFrom ().map(date("2014/11/29-00:00")),
				report.getRunUntil().map(date("2014/11/30-00:00")),
				report.getRunRun  ().map(date("2014/11/30-00:00")),
				report.getRunProgress().map(0),
				report.getRunElapsed().map(5000l));
		clockRule.override(clock);
		expectedRuns = new ArrayList<>();
		expectedRuns.add(new ExpectedRun(recentRun));
		ScheduleItem.clearLogs();
	}

	@SuppressWarnings("static-method")
	@After public final void tearDown()
	{
		ScheduleItem.clearLogs();
	}

	@Test public void testNoUpdateCounterColumn()
	{
		assertNoUpdateCounterColumn(report.getRunType());
	}

	@Test public void testDaily()
	{
		assertEquals(DAILY, item.getReportInterval());

		run(1, "2008/03/14-01:49:49.888");
		assertLogs(
				log("2008/03/13-00:00", "2008/03/14-00:00"));
		assertRuns(
				ern(DAILY, "2008/03/13-00:00", "2008/03/14-00:00", "2008/03/14-01:49:49.888"));

		run(0, "2008/03/14-01:49:49.888");
		assertLogs();
		assertRuns();

		run(0, "2008/03/15-00:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2008/03/15-00:05");
		assertLogs(
				log("2008/03/14-00:00", "2008/03/15-00:00"));
		assertRuns(
				ern(DAILY, "2008/03/14-00:00", "2008/03/15-00:00", "2008/03/15-00:05"));

		run(0, "2008/03/15-00:00");
		assertLogs();
		assertRuns();

		run(0, "2008/03/16-00:04:59.999");
		assertLogs();
		assertRuns();

		run(2, "2008/03/17-00:05");
		assertLogs(
				log("2008/03/15-00:00", "2008/03/16-00:00", "1/2"),
				log("2008/03/16-00:00", "2008/03/17-00:00", "2/2"));
		assertRuns(
				ern(DAILY, "2008/03/15-00:00", "2008/03/16-00:00", "2008/03/17-00:05"),
				ern(DAILY, "2008/03/16-00:00", "2008/03/17-00:00", "2008/03/17-00:05"));

		run(3, "2008/03/20-00:05");
		assertLogs(
				log("2008/03/17-00:00", "2008/03/18-00:00", "1/3"),
				log("2008/03/18-00:00", "2008/03/19-00:00", "2/3"),
				log("2008/03/19-00:00", "2008/03/20-00:00", "3/3"));
		assertRuns(
				ern(DAILY, "2008/03/17-00:00", "2008/03/18-00:00", "2008/03/20-00:05"),
				ern(DAILY, "2008/03/18-00:00", "2008/03/19-00:00", "2008/03/20-00:05"),
				ern(DAILY, "2008/03/19-00:00", "2008/03/20-00:00", "2008/03/20-00:05"));
	}

	@Test public void testFail()
	{
		run(1, "2008/03/17-00:05");
		assertLogs(
				log("2008/03/16-00:00", "2008/03/17-00:00"));
		assertRuns(
				ern(DAILY, "2008/03/16-00:00", "2008/03/17-00:00", "2008/03/17-00:05"));

		item.setFail(true);
		run(0, "2008/03/17-00:00");
		assertLogs();
		assertRuns();

		try
		{
			run(0, "2008/03/18-00:05");
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("schedule test failure", e.getMessage());
		}
		assertLogs(
				log("2008/03/17-00:00", "2008/03/18-00:00"));
		assertRuns();

		item.setFail(false);
		run(1, "2008/03/18-00:05");
		assertLogs(
				log("2008/03/17-00:00", "2008/03/18-00:00"));
		assertRuns(
				ern(DAILY, "2008/03/17-00:00", "2008/03/18-00:00", "2008/03/18-00:05"));
	}

	@Test public void testStop2()
	{
		run(1, "2008/03/11-00:05", 2);
		assertLogs(
				log("2008/03/10-00:00", "2008/03/11-00:00"));
		assertRuns(
				ern(DAILY, "2008/03/10-00:00", "2008/03/11-00:00", "2008/03/11-00:05"));
	}

	@Test public void testStop1()
	{
		run(0, "2008/03/11-00:00", 1);
		assertLogs();
		assertRuns();
	}

	@Test public void testStop0()
	{
		run(0, "2008/03/11-00:00", 0);
		assertLogs();
		assertRuns();
	}

	@Test public void testHourly()
	{
		assertEquals(DAILY, item.getReportInterval());

		item.setReportInterval(HOURLY);
		assertEquals(HOURLY, item.getReportInterval());

		run(1, "2008/03/14-05:49:49.888");
		assertLogs(
				log("2008/03/14-04:00", "2008/03/14-05:00"));
		assertRuns(
				ern(HOURLY, "2008/03/14-04:00", "2008/03/14-05:00", "2008/03/14-05:49:49.888"));

		run(0, "2008/03/14-05:49:49.888");
		assertLogs();
		assertRuns();

		run(0, "2008/03/14-06:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2008/03/14-06:05");
		assertLogs(
				log("2008/03/14-05:00", "2008/03/14-06:00"));
		assertRuns(
				ern(HOURLY, "2008/03/14-05:00", "2008/03/14-06:00", "2008/03/14-06:05"));

		run(2, "2008/03/14-08:05");
		assertLogs(
				log("2008/03/14-06:00", "2008/03/14-07:00", "1/2"),
				log("2008/03/14-07:00", "2008/03/14-08:00", "2/2"));
		assertRuns(
				ern(HOURLY, "2008/03/14-06:00", "2008/03/14-07:00", "2008/03/14-08:05"),
				ern(HOURLY, "2008/03/14-07:00", "2008/03/14-08:00", "2008/03/14-08:05"));

		run(6, "2008/03/14-14:05"); // cross noon
		assertLogs(
				log("2008/03/14-08:00", "2008/03/14-09:00", "1/6"),
				log("2008/03/14-09:00", "2008/03/14-10:00", "2/6"),
				log("2008/03/14-10:00", "2008/03/14-11:00", "3/6"),
				log("2008/03/14-11:00", "2008/03/14-12:00", "4/6"),
				log("2008/03/14-12:00", "2008/03/14-13:00", "5/6"),
				log("2008/03/14-13:00", "2008/03/14-14:00", "6/6"));
		assertRuns(
				ern(HOURLY, "2008/03/14-08:00", "2008/03/14-09:00", "2008/03/14-14:05"),
				ern(HOURLY, "2008/03/14-09:00", "2008/03/14-10:00", "2008/03/14-14:05"),
				ern(HOURLY, "2008/03/14-10:00", "2008/03/14-11:00", "2008/03/14-14:05"),
				ern(HOURLY, "2008/03/14-11:00", "2008/03/14-12:00", "2008/03/14-14:05"),
				ern(HOURLY, "2008/03/14-12:00", "2008/03/14-13:00", "2008/03/14-14:05"),
				ern(HOURLY, "2008/03/14-13:00", "2008/03/14-14:00", "2008/03/14-14:05"));
	}

	@Test public void testWeekly()
	{
		assertEquals(DAILY, item.getReportInterval());

		item.setReportInterval(WEEKLY);
		assertEquals(WEEKLY, item.getReportInterval());

		run(1, "2008/03/14-01:49:49.888");
		assertLogs(
				log("2008/03/03-00:00", "2008/03/10-00:00"));
		assertRuns(
				ern(WEEKLY, "2008/03/03-00:00", "2008/03/10-00:00", "2008/03/14-01:49:49.888"));

		run(0, "2008/03/14-01:49:49.888");
		assertLogs();
		assertRuns();

		run(0, "2008/03/17-00:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2008/03/17-00:05");
		assertLogs(
				log("2008/03/10-00:00", "2008/03/17-00:00"));
		assertRuns(
				ern(WEEKLY, "2008/03/10-00:00", "2008/03/17-00:00", "2008/03/17-00:05"));

		run(2, "2008/03/31-00:05");
		assertLogs(
				log("2008/03/17-00:00", "2008/03/24-00:00", "1/2"),
				log("2008/03/24-00:00", "2008/03/31-00:00", "2/2"));
		assertRuns(
				ern(WEEKLY, "2008/03/17-00:00", "2008/03/24-00:00", "2008/03/31-00:05"),
				ern(WEEKLY, "2008/03/24-00:00", "2008/03/31-00:00", "2008/03/31-00:05"));

		run(3, "2008/04/21-00:05");
		assertLogs(
				log("2008/03/31-00:00", "2008/04/07-00:00", "1/3"),
				log("2008/04/07-00:00", "2008/04/14-00:00", "2/3"),
				log("2008/04/14-00:00", "2008/04/21-00:00", "3/3"));
		assertRuns(
				ern(WEEKLY, "2008/03/31-00:00", "2008/04/07-00:00", "2008/04/21-00:05"),
				ern(WEEKLY, "2008/04/07-00:00", "2008/04/14-00:00", "2008/04/21-00:05"),
				ern(WEEKLY, "2008/04/14-00:00", "2008/04/21-00:00", "2008/04/21-00:05"));
	}

	@Test public void testMonthly()
	{
		assertEquals(DAILY, item.getReportInterval());

		item.setReportInterval(MONTHLY);
		assertEquals(MONTHLY, item.getReportInterval());

		run(1, "2008/03/14-01:49:49.888");
		assertLogs(
				log("2008/02/01-00:00", "2008/03/01-00:00"));
		assertRuns(
				ern(MONTHLY, "2008/02/01-00:00", "2008/03/01-00:00", "2008/03/14-01:49:49.888"));

		run(0, "2008/03/14-01:49:49.888");
		assertLogs();
		assertRuns();

		run(0, "2008/04/01-00:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2008/04/01-00:05");
		assertLogs(
				log("2008/03/01-00:00", "2008/04/01-00:00"));
		assertRuns(
				ern(MONTHLY, "2008/03/01-00:00", "2008/04/01-00:00", "2008/04/01-00:05"));

		run(2, "2008/06/01-00:05");
		assertLogs(
				log("2008/04/01-00:00", "2008/05/01-00:00", "1/2"),
				log("2008/05/01-00:00", "2008/06/01-00:00", "2/2"));
		assertRuns(
				ern(MONTHLY, "2008/04/01-00:00", "2008/05/01-00:00", "2008/06/01-00:05"),
				ern(MONTHLY, "2008/05/01-00:00", "2008/06/01-00:00", "2008/06/01-00:05"));

		run(3, "2008/09/01-00:05");
		assertLogs(
				log("2008/06/01-00:00", "2008/07/01-00:00", "1/3"),
				log("2008/07/01-00:00", "2008/08/01-00:00", "2/3"),
				log("2008/08/01-00:00", "2008/09/01-00:00", "3/3"));
		assertRuns(
				ern(MONTHLY, "2008/06/01-00:00", "2008/07/01-00:00", "2008/09/01-00:05"),
				ern(MONTHLY, "2008/07/01-00:00", "2008/08/01-00:00", "2008/09/01-00:05"),
				ern(MONTHLY, "2008/08/01-00:00", "2008/09/01-00:00", "2008/09/01-00:05"));
	}

	@Test public void testEnabled()
	{
		assertEquals(true, item.isReportEnabled());
		assertEquals(DAILY, item.getReportInterval());

		run(1, "2008/03/14-01:49:49.888");
		assertLogs(
				log("2008/03/13-00:00", "2008/03/14-00:00"));
		assertRuns(
				ern(DAILY, "2008/03/13-00:00", "2008/03/14-00:00", "2008/03/14-01:49:49.888"));

		item.setReportEnabled(false);
		assertEquals(false, item.isReportEnabled());
		assertEquals(DAILY, item.getReportInterval());
		run(0, "2008/03/15-00:05");
		assertLogs();
		assertRuns();

		item.setReportEnabled(true);
		assertEquals(true, item.isReportEnabled());
		assertEquals(DAILY, item.getReportInterval());
		run(1, "2008/03/15-00:05");
		assertLogs(
				log("2008/03/14-00:00", "2008/03/15-00:00"));
		assertRuns(
				ern(DAILY, "2008/03/14-00:00", "2008/03/15-00:00", "2008/03/15-00:05"));
	}

	@Test public void testDaylightSavingDailyShorter()
	{
		assertEquals(24*3600000, date("2014/03/29-00:00").getTime()-date("2014/03/28-00:00").getTime());
		assertEquals(24*3600000, date("2014/03/30-00:00").getTime()-date("2014/03/29-00:00").getTime());
		assertEquals(23*3600000, date("2014/03/31-00:00").getTime()-date("2014/03/30-00:00").getTime());
		assertEquals(24*3600000, date("2014/04/01-00:00").getTime()-date("2014/03/31-00:00").getTime());
		assertEquals(date("TZ+0100 2014/03/29-00:00"), date("2014/03/29-00:00"));
		assertEquals(date("TZ+0100 2014/03/30-00:00"), date("2014/03/30-00:00"));
		assertEquals(date("TZ+0200 2014/03/31-00:00"), date("2014/03/31-00:00"));
		assertEquals(date("TZ+0200 2014/04/01-00:00"), date("2014/04/01-00:00"));

		assertEquals(DAILY, item.getReportInterval());

		run(1, "2014/03/30-00:05");
		assertLogs(
				log("2014/03/29-00:00", "2014/03/30-00:00"));
		assertRuns(
				ern(DAILY, "2014/03/29-00:00", "2014/03/30-00:00", "2014/03/30-00:05"));

		run(0, "2014/03/31-00:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2014/03/31-00:05");
		assertLogs(
				log("2014/03/30-00:00", "2014/03/31-00:00"));
		assertRuns(
				ern(DAILY, "2014/03/30-00:00", "2014/03/31-00:00", "2014/03/31-00:05"));

		run(0, "2014/04/01-00:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2014/04/01-00:05");
		assertLogs(
				log("2014/03/31-00:00", "2014/04/01-00:00"));
		assertRuns(
				ern(DAILY, "2014/03/31-00:00", "2014/04/01-00:00", "2014/04/01-00:05"));

		run(0, "2014/04/02-00:04:59.999");
		assertLogs();
		assertRuns();
	}

	@Test public void testDaylightSavingDailyLonger()
	{
		assertEquals(24*3600000, date("2014/10/25-00:00").getTime()-date("2014/10/24-00:00").getTime());
		assertEquals(24*3600000, date("2014/10/26-00:00").getTime()-date("2014/10/25-00:00").getTime());
		assertEquals(25*3600000, date("2014/10/27-00:00").getTime()-date("2014/10/26-00:00").getTime());
		assertEquals(24*3600000, date("2014/10/28-00:00").getTime()-date("2014/10/27-00:00").getTime());
		assertEquals(date("TZ+0200 2014/10/25-00:00"), date("2014/10/25-00:00"));
		assertEquals(date("TZ+0200 2014/10/26-00:00"), date("2014/10/26-00:00"));
		assertEquals(date("TZ+0100 2014/10/27-00:00"), date("2014/10/27-00:00"));
		assertEquals(date("TZ+0100 2014/10/28-00:00"), date("2014/10/28-00:00"));

		assertEquals(DAILY, item.getReportInterval());

		run(1, "2014/10/26-00:05");
		assertLogs(
				log("2014/10/25-00:00", "2014/10/26-00:00"));
		assertRuns(
				ern(DAILY, "2014/10/25-00:00", "2014/10/26-00:00", "2014/10/26-00:05"));

		run(0, "2014/10/27-00:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2014/10/27-00:05");
		assertLogs(
				log("2014/10/26-00:00", "2014/10/27-00:00"));
		assertRuns(
				ern(DAILY, "2014/10/26-00:00", "2014/10/27-00:00", "2014/10/27-00:05"));

		run(0, "2014/10/28-00:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2014/10/28-00:05");
		assertLogs(
				log("2014/10/27-00:00", "2014/10/28-00:00"));
		assertRuns(
				ern(DAILY, "2014/10/27-00:00", "2014/10/28-00:00", "2014/10/28-00:05"));

		run(0, "2014/10/29-00:04:59.999");
		assertLogs();
		assertRuns();
	}

	@Test public void testDaylightSavingHourlyShorter()
	{
		assertEquals(60000, date("2014/03/30-01:58").getTime()-date("2014/03/30-01:57").getTime());
		assertEquals(60000, date("2014/03/30-01:59").getTime()-date("2014/03/30-01:58").getTime());
		assertEquals(60000, date("2014/03/30-03:00").getTime()-date("2014/03/30-01:59").getTime());
		assertEquals(60000, date("2014/03/30-03:01").getTime()-date("2014/03/30-03:00").getTime());
		assertEquals(60000, date("2014/03/30-03:02").getTime()-date("2014/03/30-03:01").getTime());
		assertEquals(date("TZ+0100 2014/03/30-01:58"), date("2014/03/30-01:58"));
		assertEquals(date("TZ+0100 2014/03/30-01:59"), date("2014/03/30-01:59"));
		assertEquals(date("TZ+0200 2014/03/30-03:00"), date("2014/03/30-03:00"));
		assertEquals(date("TZ+0200 2014/03/30-03:01"), date("2014/03/30-03:01"));

		assertEquals(DAILY, item.getReportInterval());

		item.setReportInterval(HOURLY);
		assertEquals(HOURLY, item.getReportInterval());

		run(1, "2014/03/30-01:05");
		assertLogs(
				log("2014/03/30-00:00", "2014/03/30-01:00"));
		assertRuns(
				ern(HOURLY, "2014/03/30-00:00", "2014/03/30-01:00", "2014/03/30-01:05"));

		run(0, "2014/03/30-03:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2014/03/30-03:05");
		assertLogs(
				log("2014/03/30-01:00", "2014/03/30-03:00"));
		assertRuns(
				ern(HOURLY, "2014/03/30-01:00", "2014/03/30-03:00", "2014/03/30-03:05"));

		run(0, "2014/03/30-04:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2014/03/30-04:05");
		assertLogs(
				log("2014/03/30-03:00", "2014/03/30-04:00"));
		assertRuns(
				ern(HOURLY, "2014/03/30-03:00", "2014/03/30-04:00", "2014/03/30-04:05"));

		run(0, "2014/03/30-05:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "2014/03/30-05:05");
		assertLogs(
				log("2014/03/30-04:00", "2014/03/30-05:00"));
		assertRuns(
				ern(HOURLY, "2014/03/30-04:00", "2014/03/30-05:00", "2014/03/30-05:05"));
	}

	@Test public void testDaylightSavingHourlyLonger()
	{
		assertEquals(   60000, date("2014/10/26-01:58").getTime()-date("2014/10/26-01:57").getTime());
		assertEquals(   60000, date("2014/10/26-01:59").getTime()-date("2014/10/26-01:58").getTime());
		assertEquals(61*60000, date("2014/10/26-02:00").getTime()-date("2014/10/26-01:59").getTime());
		assertEquals(   60000, date("2014/10/26-02:01").getTime()-date("2014/10/26-02:00").getTime());
		assertEquals(   60000, date("2014/10/26-02:02").getTime()-date("2014/10/26-02:01").getTime());
		assertEquals(date("TZ+0200 2014/10/26-01:58"), date("2014/10/26-01:58"));
		assertEquals(date("TZ+0200 2014/10/26-01:59"), date("2014/10/26-01:59"));
		assertEquals(date("TZ+0100 2014/10/26-02:00"), date("2014/10/26-02:00"));
		assertEquals(date("TZ+0100 2014/10/26-02:01"), date("2014/10/26-02:01"));
		assertEquals(60000, date("TZ+0200 2014/10/26-01:58").getTime()-date("TZ+0200 2014/10/26-01:57").getTime());
		assertEquals(60000, date("TZ+0200 2014/10/26-01:59").getTime()-date("TZ+0200 2014/10/26-01:58").getTime());
		assertEquals(60000, date("TZ+0200 2014/10/26-02:00").getTime()-date("TZ+0200 2014/10/26-01:59").getTime());
		assertEquals(60000, date("TZ+0200 2014/10/26-02:01").getTime()-date("TZ+0200 2014/10/26-02:00").getTime());
		assertEquals(60000, date("TZ+0200 2014/10/26-02:02").getTime()-date("TZ+0200 2014/10/26-02:01").getTime());
		assertEquals(60000, date("TZ+0200 2014/10/26-02:58").getTime()-date("TZ+0200 2014/10/26-02:57").getTime());
		assertEquals(60000, date("TZ+0200 2014/10/26-02:59").getTime()-date("TZ+0200 2014/10/26-02:58").getTime());
		assertEquals(60000, date("TZ+0100 2014/10/26-02:00").getTime()-date("TZ+0200 2014/10/26-02:59").getTime());
		assertEquals(60000, date("TZ+0100 2014/10/26-02:01").getTime()-date("TZ+0100 2014/10/26-02:00").getTime());
		assertEquals(60000, date("TZ+0100 2014/10/26-02:02").getTime()-date("TZ+0100 2014/10/26-02:01").getTime());

		assertEquals(DAILY, item.getReportInterval());

		item.setReportInterval(HOURLY);
		assertEquals(HOURLY, item.getReportInterval());

		run(1, "TZ+0200 2014/10/26-01:05");
		assertLogs(
				log("TZ+0200 2014/10/26-00:00", "TZ+0200 2014/10/26-01:00"));
		assertRuns(
				ern(HOURLY, "TZ+0200 2014/10/26-00:00", "TZ+0200 2014/10/26-01:00", "TZ+0200 2014/10/26-01:05"));

		run(0, "TZ+0200 2014/10/26-02:04:59.999");
		assertLogs();
		assertRuns();

		assertEquals(1, date("TZ+0200 2014/10/26-02:00").getTime()-date("TZ+0200 2014/10/26-01:59:59.999").getTime());
		run(1, "TZ+0200 2014/10/26-02:05");
		assertLogs(
				log("TZ+0200 2014/10/26-01:00", "TZ+0200 2014/10/26-02:00"));
		assertRuns(
				ern(HOURLY, "TZ+0200 2014/10/26-01:00", "TZ+0200 2014/10/26-02:00", "TZ+0200 2014/10/26-02:05"));

		run(0, "TZ+0100 2014/10/26-02:04:59.999");
		assertLogs();
		assertRuns();

		assertEquals(3600000, date("TZ+0100 2014/10/26-02:00").getTime()-date("TZ+0200 2014/10/26-02:00").getTime());
		run(1, "TZ+0100 2014/10/26-02:05");
		assertLogs(
				log("TZ+0200 2014/10/26-02:00", "TZ+0100 2014/10/26-02:00"));
		assertRuns(
				ern(HOURLY, "TZ+0200 2014/10/26-02:00", "TZ+0100 2014/10/26-02:00", "TZ+0100 2014/10/26-02:05"));

		run(0, "TZ+0100 2014/10/26-03:04:59.999");
		assertLogs();
		assertRuns();

		run(1, "TZ+0100 2014/10/26-03:05");
		assertLogs(
				log("TZ+0100 2014/10/26-02:00", "TZ+0100 2014/10/26-03:00"));
		assertRuns(
				ern(HOURLY, "TZ+0100 2014/10/26-02:00", "TZ+0100 2014/10/26-03:00", "TZ+0100 2014/10/26-03:05"));
	}

	@Test public void testReconfigure()
	{
		assertEquals(DAILY, item.getReportInterval());

		run(1, "2008/01/17-01:49:49.888"); // Thursday
		assertLogs(
				log("2008/01/16-00:00", "2008/01/17-00:00"));
		assertRuns(
				ern(DAILY, "2008/01/16-00:00", "2008/01/17-00:00", "2008/01/17-01:49:49.888"));

		item.setReportInterval(WEEKLY);
		assertEquals(WEEKLY, item.getReportInterval());

		run(0, "2008/01/21-00:04:59.999"); // Monday
		assertLogs();
		assertRuns();

		run(1, "2008/01/21-00:05"); // Monday
		assertLogs(
				log("2008/01/17-00:00", "2008/01/21-00:00"));
		assertRuns(
				ern(WEEKLY, "2008/01/17-00:00", "2008/01/21-00:00", "2008/01/21-00:05"));
	}

	@Test public void testReconfigureWithExtraLag()
	{
		assertEquals(DAILY, item.getReportInterval());

		run(1, "2008/01/17-01:49:49.888"); // Thursday
		assertLogs(
				log("2008/01/16-00:00", "2008/01/17-00:00"));
		assertRuns(
				ern(DAILY, "2008/01/16-00:00", "2008/01/17-00:00", "2008/01/17-01:49:49.888"));

		item.setReportInterval(WEEKLY);
		assertEquals(WEEKLY, item.getReportInterval());

		run(0, "2008/01/21-00:04:59.999"); // Monday
		assertLogs();
		assertRuns();

		run(2, "2008/01/28-00:05"); // Monday a week later
		assertLogs(
				log("2008/01/17-00:00", "2008/01/21-00:00", "1/2"),
				log("2008/01/21-00:00", "2008/01/28-00:00", "2/2"));
		assertRuns(
				ern(WEEKLY, "2008/01/17-00:00", "2008/01/21-00:00", "2008/01/28-00:05"),
				ern(WEEKLY, "2008/01/21-00:00", "2008/01/28-00:00", "2008/01/28-00:05"));
	}

	@Test public void testProgress()
	{
		assertEquals(DAILY, item.getReportInterval());
		item.setProgress(5);

		run(6, "2008/03/14-01:49:49.888");
		final Iterator<Run> runs = report.getRunType().search(null, report.getRunType().getThis(), true).iterator();
		{
			final Run run = runs.next();
			assertEquals(0, run.getProgress());
		}
		{
			final Run run = runs.next();
			assertEquals(item, run.getParent());
			assertEquals(5, run.getProgress());
		}
		assertFalse(runs.hasNext());
	}

	private void run(final int progress, final String now)
	{
		final CountJobContext ctx = new CountJobContext();
		run(date(now), ctx);
		assertEquals(progress, ctx.progress);
	}

	private void run(final int progress, final String now, final int interruptRequests)
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

	private void run(final Date now, final JobContext ctx)
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

	static SimpleDateFormat df(final boolean withTimeZone)
	{
		final SimpleDateFormat result = new SimpleDateFormat(
				withTimeZone
				? "'TZ'Z yyyy/MM/dd-HH:mm:ss.SSS"
				:       "yyyy/MM/dd-HH:mm:ss.SSS",
				Locale.ENGLISH);
		result.setTimeZone(report.getTimeZone());
		result.setLenient(false);
		return result;
	}

	private static Date date(final String s)
	{
		final boolean withTimeZone = s.startsWith("TZ");
		final String full;
		switch(s.length())
		{
			case 23: case 31: full = s; break;
			case 16: case 24: full = s + ":00.000"; break;
			default:
				throw new RuntimeException(s);
		}
		assertEquals(withTimeZone?31:23, full.length());
		try
		{
			return df(withTimeZone).parse(full);
		}
		catch(final ParseException e)
		{
			throw new RuntimeException(e);
		}
	}

	private Log log(final String from, final String until)
	{
		return log(from, until, "1/1");
	}

	private Log log(final String from, final String until, final String count)
	{
		return new Log(item, date(from), date(until), "ScheduleItem.report " + item.getCopeID() + " run " + count);
	}

	static class Log
	{
		final ScheduleItem item;
		final Date from;
		final Date until;
		final String transactionName;

		Log(final ScheduleItem item, final Date from, final Date until, final String transactionName)
		{
			this.item = item;
			this.from = from;
			this.until = until;
			this.transactionName = transactionName;
			assertNotNull(item);
			assertNotNull(from);
			assertNotNull(until);
			assertNotNull(transactionName);
			assertTrue(from.before(until));
		}

		@SuppressFBWarnings({"NP_EQUALS_SHOULD_HANDLE_NULL_ARGUMENT", "BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS"})
		@Override
		public boolean equals(final Object other)
		{
			final Log o = (Log)other;
			return
					item.equals(o.item) &&
					from.equals(o.from) && until.equals(o.until) &&
					transactionName.equals(o.transactionName);
		}

		@Override
		public int hashCode()
		{
			return item.hashCode() ^ from.hashCode() ^ until.hashCode() ^ transactionName.hashCode();
		}

		@Override
		public String toString()
		{
			return
					item.toString() + "---" +
					df(true).format(from) + "---" + df(true).format(until) + "---" +
					transactionName;
		}
	}

	private ExpectedRun ern(final Interval interval, final String from, final String until, final String run)
	{
		return new ExpectedRun(item, interval, date(from), date(until), date(run));
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
		final ScheduleItem parent;
		final Interval interval;
		final Date from;
		final Date until;
		final Date run;

		ExpectedRun(final Run run)
		{
			this((ScheduleItem)run.getParent(), run.getInterval(), run.getFrom(), run.getUntil(), run.getRun());
			assertTrue(String.valueOf(run.getElapsed()), run.getElapsed()>=0);
		}

		ExpectedRun(
				final ScheduleItem parent,
				final Interval interval,
				final Date from,
				final Date until,
				final Date run)
		{
			this.parent = requireNonNull(parent);
			this.interval = requireNonNull(interval);
			this.from = requireNonNull(from);
			this.until = requireNonNull(until);
			this.run = requireNonNull(run);
			assertTrue(from.before(until));
			assertTrue(!run.before(until));
		}

		@SuppressFBWarnings({"NP_EQUALS_SHOULD_HANDLE_NULL_ARGUMENT", "BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS"})
		@Override
		public boolean equals(final Object other)
		{
			final ExpectedRun o = (ExpectedRun)other;
			return
					parent.equals(o.parent) &&
					interval.equals(o.interval) &&
					from.equals(o.from) &&
					until.equals(o.until) &&
					run.equals(o.run);
		}

		@Override
		public int hashCode()
		{
			return parent.hashCode() ^ interval.hashCode() ^ from.hashCode() ^ until.hashCode() ^ run.hashCode();
		}

		@Override
		public String toString()
		{
			return "" + parent + ' ' + interval + ' ' + df(true).format(from) + "---" + df(true).format(until) + "---" + df(true).format(run);
		}
	}
}
