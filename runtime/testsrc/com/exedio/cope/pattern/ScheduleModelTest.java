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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.SchemaInfo.getColumnValue;
import static com.exedio.cope.pattern.Schedule.Interval.DAILY;
import static com.exedio.cope.pattern.Schedule.Interval.HOURLY;
import static com.exedio.cope.pattern.Schedule.Interval.MONTHLY;
import static com.exedio.cope.pattern.Schedule.Interval.WEEKLY;
import static com.exedio.cope.pattern.ScheduleItem.TYPE;
import static com.exedio.cope.pattern.ScheduleItem.report;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.util.EmptyJobContext;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.TimeZoneStrict;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
public class ScheduleModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(ScheduleModelTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEqualsUnmodifiable(list(TYPE, report.getRunType()), MODEL.getTypes());
		assertEquals(ScheduleItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(), report, report.getEnabled(), report.getInterval(),
				ScheduleItem.fail, ScheduleItem.progress),
				TYPE.getFeatures());
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
				report.getRunProgress(),
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
	}

	@Test void testConstructionParameters()
	{
		assertEquals(ZoneId.of("Europe/Berlin"), report.getZoneId());
		assertEquals("Europe/Berlin", report.getTimeZone().getID());
	}

	@Deprecated // OK: testing deprecated api
	@Test void testConstructionParametersDeprecated()
	{
		try
		{
			report.getLocale();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(report.getID(), e.getMessage());
		}
		try
		{
			report.newGregorianCalendar();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(report.getID(), e.getMessage());
		}
	}

	@Deprecated // OK: testing deprecated api
	@Test void testConstructionParametersByTimeZone()
	{
		final Schedule s = new Schedule(TimeZoneStrict.getTimeZone("Europe/Berlin"), Locale.GERMAN);
		assertEquals(ZoneId.of("Europe/Berlin"), s.getZoneId());
		assertEquals("Europe/Berlin", s.getTimeZone().getID());
		assertSame(Locale.GERMAN, s.getLocale());
		final GregorianCalendar cal = s.newGregorianCalendar();
		assertEquals("Europe/Berlin", cal.getTimeZone().getID());
		assertEquals(false, cal.isLenient());
	}

	@Test void testZoneIdNull()
	{
		try
		{
			new Schedule((ZoneId)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("zoneId", e.getMessage());
		}
	}

	@Deprecated // OK: testing deprecated api
	@Test void testTimeZoneNull()
	{
		try
		{
			new Schedule(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("timeZone", e.getMessage());
		}
	}

	@Deprecated // OK: testing deprecated api
	@Test void testLocaleNull()
	{
		final TimeZone tz = TimeZone.getDefault();
		try
		{
			new Schedule(tz, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("locale", e.getMessage());
		}
	}

	@Test void testMoreGetters()
	{
		assertSame(report.getRunParent(), report.getRunRuns().getContainer());
		assertSame(report.getRunFrom(),   report.getRunRuns().getOrder());
		assertSame(report.getRunParent(), ScheduleItem.reportRunParent());
	}

	@Test void testAnnotations()
	{
		assertFalse(report.getEnabled   ().isAnnotationPresent(Computed.class));
		assertFalse(report.getInterval  ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunParent ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunFrom   ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunUntil  ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunRun    ().isAnnotationPresent(Computed.class));
		assertFalse(report.getRunElapsed().isAnnotationPresent(Computed.class));
		assertTrue (report.getRunType   ().isAnnotationPresent(Computed.class));
	}

	@Test void testSerialize()
	{
		assertSerializedSame(report, 385);
	}

	@Test void testEnumSchema()
	{
		assertEquals(asList(HOURLY, DAILY, WEEKLY, MONTHLY), asList(Schedule.Interval.values()));
		assertEquals( 7, getColumnValue(HOURLY ));
		assertEquals(10, getColumnValue(DAILY  ));
		assertEquals(20, getColumnValue(WEEKLY ));
		assertEquals(30, getColumnValue(MONTHLY));
	}

	@Test void testEnumLimit()
	{
		assertEquals(720, HOURLY .limit);
		assertEquals( 62, DAILY  .limit);
		assertEquals( 25, WEEKLY .limit);
		assertEquals( 12, MONTHLY.limit);
	}

	@Test void testRunParentClassNull()
	{
		assertFails(
				() -> report.run(null, (JobContext)null),
				NullPointerException.class, "parentClass");
	}

	@SuppressWarnings("unchecked") // OK: test bad api usage
	@Test void testRunParentClassWrong()
	{
		assertFails(
				() -> report.run((Class)HashItem.class, new EmptyJobContext()),
				ClassCastException.class,
				"parentClass requires " + ScheduleItem.class.getName() + ", " +
				"but was " + HashItem.class.getName());
	}

	@Test void testRunJobContextNull()
	{
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
}
