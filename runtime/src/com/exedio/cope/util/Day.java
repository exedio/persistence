/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.util;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * The class <tt>Day</tt> represents a specific day.
 * It is similar to {@link java.util.Date},
 * but with &quot;day precision&quot; instead of millisecond precision.
 * Like {@link java.util.Date} its immutable,
 * so you cannot change the value of an instance of this class.
 * <p>
 * This class is used within cope as a value class for
 * {@link com.exedio.cope.DayField}.
 *
 * @author Ralf Wiebicke
 */
public final class Day // TODO implement Comparable
{
	private final int year;
	private final int month;
	private final int day;
	
   /**
    * Creates a new <tt>Day</tt> object,
    * that represents today.
    */
	public Day()
	{
		this(makeCalendar(System.currentTimeMillis()));
	}
	
	public Day(final Date date)
	{
		this(makeCalendar(date.getTime()));
	}
	
	private static final GregorianCalendar makeCalendar(final long time)
	{
		final GregorianCalendar result = new GregorianCalendar();
		result.setTimeInMillis(time);
		return result;
	}
	
	private Day(final GregorianCalendar c)
	{
		this(c.get(GregorianCalendar.YEAR), c.get(GregorianCalendar.MONTH)+1, c.get(GregorianCalendar.DAY_OF_MONTH));
	}
	
	public Day(final int year, final int month, final int day)
	{
		// mysql supports 1000/01/01 to 9999/12/31
		// oracle supports 4712/01/01 BC to 9999/12/31
		if(year<1000 || year>9999)
			throw new IllegalArgumentException("year must be in range 1000..9999, but was: " + year);
		if(month<1 || month>12)
			throw new IllegalArgumentException("month must be in range 1..12, but was: " + month);
		if(day<1 || day>31)
			throw new IllegalArgumentException("day must be in range 1..31, but was: " + day);

		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public int getYear()
	{
		return year;
	}
	
	public int getMonth()
	{
		return month;
	}
	
	public int getDay()
	{
		return day;
	}
	
	public long getTimeInMillis()
	{
		return new GregorianCalendar(year, month-1, day).getTimeInMillis();
	}
	
	public Day add(final int days)
	{
		final GregorianCalendar cal = new GregorianCalendar(year, month-1, day);
		cal.add(cal.DATE, days);
		return new Day(cal);
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(!(o instanceof Day))
			return false;

		final Day d = (Day)o;
		return day==d.day && month==d.month && year==d.year;
	}
	
	@Override
	public int hashCode()
	{
		return day ^ month ^ year;
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(year) + '/' + month + '/' + day;
	}
	
}
