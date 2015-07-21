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
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DayPartView extends NumberView<Integer>
{
	private static final long serialVersionUID = 1l;

	private final Function<Day> source;
	private final DayPartField dayPartField;

	enum DayPartField {
		DAY, MONTH, YEAR, WEEK;
	}

	/**
	 * Creates a new DatePart.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper methods
	 * {@link DayField#day()}, {@link DayField#month()}, {@link DayField#year()} or {@link DayField#week()}.
	 */
	protected DayPartView(final Function<Day> source, final DayPartField dayPartField)
	{
		super(new Function<?>[]{source}, "datePart_" + dayPartField.name(), Integer.class);
		this.dayPartField = dayPartField;
		this.source = source;
	}

	@Override
	public SelectType<Integer> getValueType()
	{
		return SimpleSelectType.INTEGER;
	}

	@Override
	public final Integer mapJava(final Object[] sourceValues)
	{
		assert sourceValues.length==1;
		final Object sourceValue = sourceValues[0];
		if (sourceValue == null)
		{
			return null;
		}
		final Day sourceValueAsDay = (Day)sourceValue;
		switch (dayPartField)
		{
			case DAY:
				return sourceValueAsDay.getDay();
			case MONTH:
				return sourceValueAsDay.getMonth();
			case YEAR:
				return sourceValueAsDay.getYear();
			case WEEK:
				return sourceValueAsDay.getGregorianCalendar(TimeZone.getDefault()).get(GregorianCalendar.WEEK_OF_YEAR);
			default:
				throw new IllegalArgumentException("Unkown DayPartField");
		}
	}

	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		bf.append(bf.dialect.getDatePartExtractionPrefix(dayPartField))
				.append(source, join)
				.append(bf.dialect.getDatePartExtractionSuffix(dayPartField));
	}
}
