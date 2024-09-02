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

import com.exedio.cope.util.Day;
import java.io.Serial;
import java.time.temporal.WeekFields;

public final class DayPartView extends NumberView<Integer>
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final Function<Day> source;
	private final Part part;

	enum Part {

		YEAR("year")
		{
			@Override
			int getPart(final Day day)
			{
				return day.getYear();
			}

			@Override
			String getNameForDialect(final Dialect dialect)
			{
				return "YEAR";
			}
		},
		MONTH("month")
		{
			@Override
			int getPart(final Day day)
			{
				return day.getMonthValue();
			}

			@Override
			String getNameForDialect(final Dialect dialect)
			{
				return "MONTH";
			}
		},
		/**
		 * ISO 8601:
		 * Weeks start with Monday.
		 * Each week's year is the Gregorian year in which the Thursday falls.
		 * The first week of the year, hence, always contains 4 January.
		 * <a href="https://en.wikipedia.org/wiki/ISO_week_date">ISO week date</a>
		 */
		WEEK_OF_YEAR("weekOfYear")
		{
			@Override
			int getPart(final Day day)
			{
				return day.toLocalDate().get(WeekFields.ISO.weekOfWeekBasedYear());
			}

			@Override
			String getNameForDialect(final Dialect dialect)
			{
				return dialect.getWeekOfYear();
			}
		},
		DAY_OF_MONTH("dayOfMonth")
		{
			@Override
			int getPart(final Day day)
			{
				return day.getDayOfMonth();
			}

			@Override
			String getNameForDialect(final Dialect dialect)
			{
				return "DAY";
			}
		};

		final String viewName;

		Part(final String viewName)
		{
			this.viewName = viewName;
		}

		abstract String getNameForDialect(final Dialect dialect);

		abstract int getPart(final Day day);
	}

	DayPartView(final Function<Day> source, final Part part)
	{
		super(new Function<?>[]{source}, part.viewName, Integer.class);
		this.source = source;
		this.part = part;
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<Integer> getValueType()
	{
		return SimpleSelectType.INTEGER;
	}

	@Override
	public DayPartView bind(final Join join)
	{
		return new DayPartView(source.bind(join), part);
	}

	Part getPart()
	{
		return part;
	}

	Function<Day> getSource()
	{
		return source;
	}

	@Override
	public Integer mapJava(final Object[] sourceValues)
	{
		assert sourceValues.length==1;
		final Object sourceValue = sourceValues[0];
		if (sourceValue == null)
		{
			return null;
		}
		return part.getPart((Day)sourceValue);
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(@SuppressWarnings("ClassEscapesDefinedScope") final Statement bf, final Join join)
	{
		bf.dialect.appendDatePartExtraction(this, bf, join);
	}
}
