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

package com.exedio.cope;

import static com.exedio.cope.DateField.Precision.HOUR;
import static com.exedio.cope.DateField.Precision.MILLI;
import static com.exedio.cope.DateField.Precision.MINUTE;
import static com.exedio.cope.DateField.Precision.SECOND;
import static com.exedio.cope.DateField.RoundingMode.FUTURE;
import static com.exedio.cope.DateField.RoundingMode.PAST;
import static com.exedio.cope.DateField.RoundingMode.UNNECESSARY;
import static com.exedio.cope.DatePrecisionItem.TYPE;
import static com.exedio.cope.DatePrecisionItem.hours;
import static com.exedio.cope.DatePrecisionItem.minutes;
import static com.exedio.cope.DatePrecisionItem.seconds;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getColumnValue;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.SchemaInfo.supportsNativeDate;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.DateField.RoundingMode;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.Test;

public class DatePrecisionSchemaTest extends TestWithEnvironment
{
	public DatePrecisionSchemaTest()
	{
		super(DatePrecisionTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test public void testSchema()
	{
		final Schema schema = model.getSchema();
		schema.checkUnsupportedConstraints();

		final Table table = schema.getTable(getTableName(TYPE));

		final Constraint  millisPR = table.getConstraint("DatePrecisiItem_millis_PR");
		final Constraint secondsPR = table.getConstraint("DatePrecisiItem_second_PR");
		final Constraint minutesPR = table.getConstraint("DatePrecisiItem_minute_PR");
		final Constraint   hoursPR = table.getConstraint("DatePrecisioItem_hours_PR");

		final Constraint  millisPM = table.getConstraint("DatePrecisiItem_millis_PM");
		final Constraint secondsPM = table.getConstraint("DatePrecisiItem_second_PM");
		final Constraint minutesPM = table.getConstraint("DatePrecisiItem_minute_PM");
		final Constraint   hoursPM = table.getConstraint("DatePrecisioItem_hours_PM");

		final Constraint  millisPS = table.getConstraint("DatePrecisiItem_millis_PS");
		final Constraint secondsPS = table.getConstraint("DatePrecisiItem_second_PS");
		final Constraint minutesPS = table.getConstraint("DatePrecisiItem_minute_PS");
		final Constraint   hoursPS = table.getConstraint("DatePrecisioItem_hours_PS");

		if(supportsNativeDate(model))
		{
			assertEquals(null,  millisPR);
			assertEquals(null, secondsPR);
			assertEquals(null, minutesPR);
			assertEquals(null,   hoursPR);

			assertEquals(null,  millisPM);
			assertEquals(null, secondsPM);
			assertEquals(null, minutesPM);
			assertEquals(extract(hours  , MINUTE)+"=0",   hoursPM.getRequiredCondition());

			assertEquals(null, millisPS);
			assertEquals(extract(seconds, SECOND)+"=" + floor(extract(seconds, SECOND)), secondsPS.getRequiredCondition());
			assertEquals(extract(minutes, SECOND)+"=0", minutesPS.getRequiredCondition());
			assertEquals(extract(hours  , SECOND)+"=0",   hoursPS.getRequiredCondition());

		}
		else
		{
			assertEquals(null, millisPR);
			assertEquals(precision(seconds,    1000), secondsPR.getRequiredCondition());
			assertEquals(precision(minutes,   60000), minutesPR.getRequiredCondition());
			assertEquals(precision(hours  , 3600000),   hoursPR.getRequiredCondition());

			assertEquals(null,  millisPM);
			assertEquals(null, secondsPM);
			assertEquals(null, minutesPM);
			assertEquals(null,   hoursPM);

			assertEquals(null,  millisPS);
			assertEquals(null, secondsPS);
			assertEquals(null, minutesPS);
			assertEquals(null,   hoursPS);
		}

		model.startTransaction(DatePrecisionSchemaTest.class.getName());
		assertSchema();
	}

	// native date
	private final String extract(final DateField field, final Precision precision)
	{
		switch(dialect)
		{
			case hsqldb    : // fall through
			case oracle    : return "EXTRACT(" + precision.sql() + " FROM " + q(field) + ")";

			case postgresql: return "\"date_part\"('" + precision.sql() + "'," + q(field) + ")";

			case mysql: // MySQL does not support native date
			default:
				throw new RuntimeException("" + dialect);
		}
	}

	private final String floor(final String s)
	{
		switch(dialect)
		{
			case hsqldb    : // fall through
			case oracle    : return "FLOOR(" + s + ")";

			case postgresql: return "\"floor\"(" + s + ")";

			case mysql: // MySQL does not support native date
			default:
				throw new RuntimeException("" + dialect);
		}
	}

	// integer date
	private final String precision(final DateField field, final int divisor)
	{
		switch(dialect)
		{
			case hsqldb    : return "MOD(" + q(field) + "," + divisor + ")=0";
			case mysql     : return "(" + q(field) + " MOD " + divisor + ")=0";
			case postgresql: return "(" + q(field) +  " % "  + divisor + ")=0";
			case oracle: // TODO
			default:
				throw new RuntimeException("" + dialect);
		}
	}

	private final String q(final Field<?> f)
	{
		return quoteName(model, getColumnName(f));
	}

	@Test public void testEnumSchemaPrecision()
	{
		assertEquals(asList(MILLI, SECOND, MINUTE, HOUR), asList(Precision.values()));
		assertEquals(10, getColumnValue(MILLI ));
		assertEquals(20, getColumnValue(SECOND));
		assertEquals(30, getColumnValue(MINUTE));
		assertEquals(40, getColumnValue(HOUR  ));
	}

	@Test public void testEnumSchemaRoundingMode()
	{
		assertEquals(asList(FUTURE, PAST, UNNECESSARY), asList(RoundingMode.values()));
		assertEquals(10, getColumnValue(FUTURE));
		assertEquals(20, getColumnValue(PAST));
		assertEquals(30, getColumnValue(UNNECESSARY));
	}
}
