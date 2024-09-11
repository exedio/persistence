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
import static com.exedio.cope.SchemaInfo.getColumnValue;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.supportsNativeDate;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.DateField.RoundingMode;
import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class DatePrecisionSchemaTest extends TestWithEnvironment
{
	public DatePrecisionSchemaTest()
	{
		super(DatePrecisionTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test void testSchema()
	{
		final Schema schema = model.getSchema();
		schema.checkUnsupportedConstraints();

		final Table table = schema.getTable(getTableName(TYPE));

		final Constraint secondsPR = table.getConstraint("Main_seconds_PR");
		final Constraint minutesPR = table.getConstraint("Main_minutes_PR");
		final Constraint   hoursPR = table.getConstraint("Main_hours_PR");

		final Constraint   hoursPM = table.getConstraint("Main_hours_PM");

		final Constraint secondsPS = table.getConstraint("Main_seconds_PS");
		final Constraint minutesPS = table.getConstraint("Main_minutes_PS");
		final Constraint   hoursPS = table.getConstraint("Main_hours_PS");

		if(supportsNativeDate(model))
		{
			assertEquals(extract(hours  , MINUTE)+"=0",   hoursPM.getRequiredCondition());

			assertEquals(mysql?"EXTRACT("+    "MICROSECOND FROM `seconds`)=0":extract(seconds, SECOND)+"=FLOOR("+extract(seconds, SECOND)+")", secondsPS.getRequiredCondition());
			assertEquals(mysql?"EXTRACT(SECOND_MICROSECOND FROM `minutes`)=0":extract(minutes, SECOND)+"=0", minutesPS.getRequiredCondition());
			assertEquals(mysql?"EXTRACT(SECOND_MICROSECOND FROM `hours`)=0"  :extract(hours  , SECOND)+"=0",   hoursPS.getRequiredCondition());

			assertEquals(asList(
					secondsPS, minutesPS, hoursPM, hoursPS),
					getDateCheckConstraints(table));
		}
		else
		{
			assertEquals(precision(seconds,    1000), secondsPR.getRequiredCondition());
			assertEquals(precision(minutes,   60000), minutesPR.getRequiredCondition());
			assertEquals(precision(hours  , 3600000),   hoursPR.getRequiredCondition());

			assertEquals(asList(
					secondsPR, minutesPR, hoursPR),
					getDateCheckConstraints(table));
		}

		model.startTransaction(DatePrecisionSchemaTest.class.getName());
		assertSchema();
	}

	// native date
	private String extract(final DateField field, final Precision precision)
	{
		return switch(dialect)
		{
			case hsqldb,
					mysql     -> "EXTRACT(" + precision.sql() + " FROM " + SI.col(field) + ")";

			case postgresql -> "\"date_part\"('" + precision.sql() + "', " + SI.col(field) + ")";



		};
	}

	// integer date
	private String precision(final DateField field, final int divisor)
	{
		return switch(dialect)
		{
			case hsqldb     -> "MOD(" + SI.col(field) + ","  + divisor + ")=0";
			case mysql      -> "(" + SI.col(field) + " MOD " + divisor + ")=0";
			case postgresql -> "(" + SI.col(field) +  " % "  + divisor + ")=0";


		};
	}

	private static ArrayList<CheckConstraint> getDateCheckConstraints(final Table table)
	{
		final ArrayList<CheckConstraint> result = new ArrayList<>();
		for(final Constraint c : table.getConstraints())
		{
			final String name = c.getName();
			if(!name.endsWith("_MN") &&
				!name.endsWith("_MX") &&
				c instanceof CheckConstraint)
				result.add((CheckConstraint)c);
		}
		return result;
	}

	@Test void testEnumSchemaPrecision()
	{
		assertEquals(asList(MILLI, SECOND, MINUTE, HOUR), asList(Precision.values()));
		assertEquals(10, getColumnValue(MILLI ));
		assertEquals(20, getColumnValue(SECOND));
		assertEquals(30, getColumnValue(MINUTE));
		assertEquals(40, getColumnValue(HOUR  ));
	}

	@Test void testEnumSchemaRoundingMode()
	{
		assertEquals(asList(FUTURE, PAST, UNNECESSARY), asList(RoundingMode.values()));
		assertEquals(10, getColumnValue(FUTURE));
		assertEquals(20, getColumnValue(PAST));
		assertEquals(30, getColumnValue(UNNECESSARY));
	}
}
