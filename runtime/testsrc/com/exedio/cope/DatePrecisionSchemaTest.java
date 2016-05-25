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

import static com.exedio.cope.DateField.Precision.Hours;
import static com.exedio.cope.DateField.Precision.Millis;
import static com.exedio.cope.DateField.Precision.Minutes;
import static com.exedio.cope.DateField.Precision.Seconds;
import static com.exedio.cope.DatePrecisionItem.TYPE;
import static com.exedio.cope.DatePrecisionItem.hours;
import static com.exedio.cope.DatePrecisionItem.millis;
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
		final Constraint millisC  = table.getConstraint("DatePrecisiItem_millis_Ck");
		final Constraint secondsC = table.getConstraint("DatePrecisiItem_second_Ck");
		final Constraint minutesC = table.getConstraint("DatePrecisiItem_minute_Ck");
		final Constraint hoursC   = table.getConstraint("DatePrecisioItem_hours_Ck");

		if(supportsNativeDate(model))
		{
			assertEquals(null, millisC);
			assertEquals(null, secondsC);
			assertEquals(null, minutesC);
			assertEquals(null, hoursC);
		}
		else
		{
			assertEquals(   range(millis ), millisC .getRequiredCondition());
			assertEquals(hp(range(seconds)) + " AND " + precision(seconds,    1000), secondsC.getRequiredCondition());
			assertEquals(hp(range(minutes)) + " AND " + precision(minutes,   60000), minutesC.getRequiredCondition());
			assertEquals(hp(range(hours  )) + " AND " + precision(hours  , 3600000), hoursC  .getRequiredCondition());
		}

		model.startTransaction(DatePrecisionSchemaTest.class.getName());
		assertSchema();
	}

	private final String range(final DateField field)
	{
		return
				"(" + q(field) + ">=" + Long.MIN_VALUE + ") AND " +
				"(" + q(field) + "<=" + Long.MAX_VALUE + ")";
	}

	private final String precision(final DateField field, final int divisor)
	{
		switch(dialect)
		{
			case hsqldb    : return "(MOD(" + q(field) + "," + divisor + ")=0)";
			case mysql     : return "((" + q(field) + " MOD " + divisor + ")=0)";
			case oracle    : // TODO
			case postgresql: // TODO
			default:
				throw new RuntimeException("" + dialect);
		}
	}

	private final String q(final Field<?> f)
	{
		return quoteName(model, getColumnName(f));
	}

	protected final String hp(final String s)
	{
		if(hsqldb)
			return "(" + s + ")";
		else
			return s;
	}

	@Test public void testEnumSchema()
	{
		assertEquals(asList(Millis, Seconds, Minutes, Hours), asList(Precision.values()));
		assertEquals(10, getColumnValue(Millis ));
		assertEquals(20, getColumnValue(Seconds  ));
		assertEquals(30, getColumnValue(Minutes ));
		assertEquals(40, getColumnValue(Hours));
	}
}
