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
import java.util.Date;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.pattern.Schedule.Run;

public final class ScheduleTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(ScheduleItem.TYPE);
	
	public ScheduleTest()
	{
		super(MODEL);
	}
	
	ScheduleItem item;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new ScheduleItem());
	}

	public void testIt()
	{
		// test model
		
		assertEqualsUnmodifiable(list(TYPE, report.getRunType()), model.getTypes());
		assertEquals(ScheduleItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.hasUniqueJavaClass());
		assertEquals(null, item.TYPE.getPattern());
		
		assertEqualsUnmodifiable(list(TYPE.getThis(), report), TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				report.getRunType().getThis(),
				report.getRunParent(),
				report.getRunRuns(),
				report.getRunFrom(),
				report.getRunUntil()
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
		
		assertEquals("parent", report.getRunParent().getName());
		assertEquals("runs",   report.getRunRuns()  .getName());
		assertEquals("from",   report.getRunFrom()  .getName());
		assertEquals("until",  report.getRunUntil() .getName());
		
		// test persistence
		
		assertEquals(1, run(date("2008/03/14-01:49:49.888")));
		item.assertLogs(listg(log(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"))));
		item.assertRuns(listg(
				ern(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"))));
		
		assertEquals(0, run(date("2008/03/14-01:49:49.888")));
		item.assertLogs(ScheduleTest.<Log>listg());
		item.assertRuns(listg(
				ern(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"))));
		
		assertEquals(0, run(date("2008/03/14-23:59:59.999")));
		item.assertLogs(ScheduleTest.<Log>listg());
		item.assertRuns(listg(
				ern(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000"))));
		
		assertEquals(1, run(date("2008/03/15-00:00:00.000")));
		item.assertLogs(listg(log(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
		item.assertRuns(listg(
				ern(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000")),
				ern(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
		
		assertEquals(0, run(date("2008/03/15-00:00:00.000")));
		item.assertLogs(ScheduleTest.<Log>listg());
		item.assertRuns(listg(
				ern(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000")),
				ern(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
		
		assertEquals(0, run(date("2008/03/15-23:59:59.999")));
		item.assertLogs(ScheduleTest.<Log>listg());
		item.assertRuns(listg(
				ern(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000")),
				ern(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000"))));
		
		assertEquals(1, run(date("2008/03/17-00:00:00.000"))); // TODO should be 2
		item.assertLogs(listg(log(date("2008/03/16-00:00:00.000"), date("2008/03/17-00:00:00.000"))));
		item.assertRuns(listg(
				ern(date("2008/03/13-00:00:00.000"), date("2008/03/14-00:00:00.000")),
				ern(date("2008/03/14-00:00:00.000"), date("2008/03/15-00:00:00.000")),
				ern(date("2008/03/16-00:00:00.000"), date("2008/03/17-00:00:00.000"))));
	}
	
	private final int run(final Date now)
	{
		model.commit();
		final int result = report.run(ScheduleItem.class, now);
		model.startTransaction("ScheduleTest");
		return result;
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
	
	private final ExpectedRun ern(final Date from, final Date until)
	{
		return new ExpectedRun(from, until);
	}
	
	static class ExpectedRun
	{
		final Date from;
		final Date until;
		
		ExpectedRun(final Run run)
		{
			this(run.getFrom(), run.getUntil());
		}

		ExpectedRun(final Date from, final Date until)
		{
			this.from = from;
			this.until = until;
			assertNotNull(from);
			assertNotNull(until);
			assertTrue(from.before(until));
		}

		@Override
		public boolean equals(final Object other)
		{
			final ExpectedRun o = (ExpectedRun)other;
			return from.equals(o.from) && until.equals(o.until);
		}

		@Override
		public int hashCode()
		{
			return from.hashCode() ^ until.hashCode();
		}

		@Override
		public String toString()
		{
			return df.format(from) + "---" + df.format(until);
		}
	}
}
