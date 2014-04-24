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
import static com.exedio.cope.pattern.Schedule.Interval.DAILY;
import static com.exedio.cope.pattern.Schedule.Interval.MONTHLY;
import static com.exedio.cope.pattern.Schedule.Interval.WEEKLY;
import static com.exedio.cope.pattern.ScheduleItem.TYPE;
import static com.exedio.cope.pattern.ScheduleItem.report;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;
import java.util.Locale;
import java.util.TimeZone;

public class ScheduleModelTest extends CopeAssert
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(ScheduleModelTest.class, "MODEL");
	}

	public void testIt()
	{
		assertEqualsUnmodifiable(list(TYPE, report.getRunType()), MODEL.getTypes());
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
		assertEquals(MODEL, report.getRunType().getModel());

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

		assertEquals("Europe/Berlin", report.getTimeZone().getID());
		assertSame(Locale.GERMAN, report.getLocale());
		try
		{
			new Schedule(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("timeZone", e.getMessage());
		}
		try
		{
			new Schedule(TimeZone.getDefault(), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("locale", e.getMessage());
		}

		assertSame(report.getRunParent(), report.getRunRuns().getContainer());
		assertSame(report.getRunFrom(),   report.getRunRuns().getOrder());
		assertSame(report.getRunParent(), ScheduleItem.reportRunParent());

		assertFalse(report.getEnabled   ().isAnnotationPresent(Computed.class));
		assertFalse(report.getInterval  ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunParent ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunFrom   ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunUntil  ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunRun    ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunElapsed().isAnnotationPresent(Computed.class));
		assertTrue (report.getRunType   ().isAnnotationPresent(Computed.class));

		assertSerializedSame(report, 385);

		assertEquals(10, getColumnValue(DAILY  ));
		assertEquals(20, getColumnValue(WEEKLY ));
		assertEquals(30, getColumnValue(MONTHLY));
	}
}
