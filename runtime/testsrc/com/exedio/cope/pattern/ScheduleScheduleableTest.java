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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.pattern.Schedule.Interval.DAILY;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.pattern.Schedule.Interval;
import com.exedio.cope.pattern.Schedule.Run;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobContexts;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class ScheduleScheduleableTest extends TestWithEnvironment
{
	public ScheduleScheduleableTest()
	{
		super(MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	MyItem item;
	ArrayList<ExpectedRun> expectedRuns;

	@BeforeEach final void setUp()
	{
		item = new MyItem();
		final MyItem item = new MyItem();
		final Run run = MyItem.report.getRunType().newItem(
				SetValue.map(MyItem.reportRunParent(), item),
				SetValue.map(MyItem.report.getRunInterval(), DAILY),
				SetValue.map(MyItem.report.getRunFrom (), date("2014-11-29 00:00")),
				SetValue.map(MyItem.report.getRunUntil(), date("2014-11-30 00:00")),
				SetValue.map(MyItem.report.getRunRun  (), date("2014-11-30 00:00")),
				SetValue.map(MyItem.report.getRunProgress(), 0),
				SetValue.map(MyItem.report.getRunElapsed(), 5000l));
		clockRule.override(clock);
		expectedRuns = new ArrayList<>();
		expectedRuns.add(new ExpectedRun(run));
	}

	@Test void testDaily()
	{
		assertEquals(DAILY, item.getReportInterval());

		run("2008-03-14 01:49:49.888");
		assertRuns(
				ern(DAILY, "2008-03-13 00:00", "2008-03-14 00:00", "2008-03-14 01:49:49.888"));

		run("2008-03-14 01:49:49.888");
		assertRuns();

		run("2008-03-15 00:04:59.999");
		assertRuns();

		run("2008-03-15 00:05");
		assertRuns(
				ern(DAILY, "2008-03-14 00:00", "2008-03-15 00:00", "2008-03-15 00:05"));

		run("2008-03-15 00:00");
		assertRuns();

		run("2008-03-16 00:04:59.999");
		assertRuns();

		run("2008-03-17 00:05");
		assertRuns(
				ern(DAILY, "2008-03-15 00:00", "2008-03-16 00:00", "2008-03-17 00:05"),
				ern(DAILY, "2008-03-16 00:00", "2008-03-17 00:00", "2008-03-17 00:05"));

		run("2008-03-20 00:05");
		assertRuns(
				ern(DAILY, "2008-03-17 00:00", "2008-03-18 00:00", "2008-03-20 00:05"),
				ern(DAILY, "2008-03-18 00:00", "2008-03-19 00:00", "2008-03-20 00:05"),
				ern(DAILY, "2008-03-19 00:00", "2008-03-20 00:00", "2008-03-20 00:05"));
	}


	private void run(final String now)
	{
		try
		{
			model.commit();
			clock.add(date(now));
			MyItem.runReport(JobContexts.EMPTY);
		}
		finally
		{
			model.startTransaction("ScheduleScheduleableTest");
		}
		clock.assertEmpty();
	}

	private static SimpleDateFormat df()
	{
		final SimpleDateFormat result = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS",
				Locale.ENGLISH);
		result.setTimeZone(MyItem.report.getTimeZone());
		result.setLenient(false);
		return result;
	}

	private static Date date(final String s)
	{
		final String full;
		switch(s.length())
		{
			case 23: case 31: full = s; break;
			case 16: case 24: full = s + ":00.000"; break;
			default:
				throw new RuntimeException(s);
		}
		assertEquals(23, full.length());
		try
		{
			return df().parse(full);
		}
		catch(final ParseException e)
		{
			throw new RuntimeException(e);
		}
	}

	private ExpectedRun ern(final Interval interval, final String from, final String until, final String run)
	{
		return new ExpectedRun(item, interval, date(from), date(until), date(run));
	}

	private void assertRuns(final ExpectedRun... expectedMore)
	{
		expectedRuns.addAll(Arrays.asList(expectedMore));
		final List<Run> actualList = MyItem.report.getRunType().search(null, MyItem.report.getRunType().getThis(), true);
		final ArrayList<ExpectedRun> actual = new ArrayList<>();
		for(final Run run : actualList)
			actual.add(new ExpectedRun(run));
		assertEquals(expectedRuns, actual);
	}

	private static class ExpectedRun
	{
		final MyItem parent;
		final Interval interval;
		final Date from;
		final Date until;
		final Date run;

		ExpectedRun(final Run run)
		{
			this((MyItem)run.getParent(), run.getInterval(), run.getFrom(), run.getUntil(), run.getRun());
			assertTrue(run.getElapsed()>=0, String.valueOf(run.getElapsed()));
		}

		ExpectedRun(
				final MyItem parent,
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

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		@Override
		public boolean equals(final Object other)
		{
			final ExpectedRun o = (ExpectedRun)other;
			return
					parent.equals(o.parent) &&
					interval==o.interval &&
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
			return "" + parent + ' ' + interval + ' ' + df().format(from) + "---" + df().format(until) + "---" + df().format(run);
		}
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item implements Scheduleable
	{
		@SuppressWarnings("deprecation") // OK: testing deprecated API
		static final Schedule report = new Schedule(ZoneId.of("Europe/Berlin"));

		@Override
		public void run(final Schedule schedule, final Date from, final Date until, final JobContext ctx)
		{
			assertSame(report, schedule);
			assertNotNull(from);
			assertNotNull(until);
			assertTrue(from.before(until));
			assertNotNull(ctx);
			assertTrue(TYPE.getModel().hasCurrentTransaction());
			assertTrue(ctx.supportsProgress());
		}


		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean isReportEnabled()
		{
			return MyItem.report.isEnabled(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setReportEnabled(final boolean enabled)
		{
			MyItem.report.setEnabled(this,enabled);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.pattern.Schedule.Interval getReportInterval()
		{
			return MyItem.report.getInterval(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setReportInterval(@javax.annotation.Nonnull final com.exedio.cope.pattern.Schedule.Interval interval)
		{
			MyItem.report.setInterval(this,interval);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		static void runReport(@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
		{
			MyItem.report.run(MyItem.class,ctx);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static com.exedio.cope.ItemField<MyItem> reportRunParent()
		{
			return MyItem.report.getRunParent(MyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);
}
