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

package com.exedio.cope;

import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.util.Day;
import java.lang.reflect.AnnotatedElement;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DayField extends FunctionField<Day>
{
	private static final Logger logger = LoggerFactory.getLogger(DayField.class);

	private static final long serialVersionUID = 1l;

	private DayField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final ItemField<?>[] copyFrom,
			final DefaultSource<Day> defaultSource)
	{
		super(isfinal, optional, unique, copyFrom, Day.class, defaultSource);
		mountDefaultSource();
	}

	public DayField()
	{
		this(false, false, false, null, null);
	}

	@Override
	public DayField copy()
	{
		return new DayField(isfinal, optional, unique, copyFrom, defaultSource);
	}

	@Override
	public DayField toFinal()
	{
		return new DayField(true, optional, unique, copyFrom, defaultSource);
	}

	@Override
	public DayField optional()
	{
		return new DayField(isfinal, true, unique, copyFrom, defaultSource);
	}

	@Override
	public DayField unique()
	{
		return new DayField(isfinal, optional, true, copyFrom, defaultSource);
	}

	@Override
	public DayField nonUnique()
	{
		return new DayField(isfinal, optional, false, copyFrom, defaultSource);
	}

	@Override
	public DayField copyFrom(final ItemField<?> copyFrom)
	{
		return new DayField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultSource);
	}

	@Override
	public DayField noDefault()
	{
		return new DayField(isfinal, optional, unique, copyFrom, null);
	}

	@Override
	public DayField defaultTo(final Day defaultConstant)
	{
		return new DayField(isfinal, optional, unique, copyFrom, defaultConstantWithCreatedTime(defaultConstant));
	}

	private static final class DefaultNow extends DefaultSource<Day>
	{
		final TimeZone zone;

		DefaultNow(final TimeZone zone)
		{
			this.zone = Objects.requireNonNull(zone, "zone");
		}

		@Override
		Day generate(final long now)
		{
			return new Day(new Date(now), zone);
		}

		@Override
		DefaultSource<Day> forNewField()
		{
			return this;
		}

		@Override
		void mount(final FunctionField<Day> field)
		{
			// nothing to be checked
		}
	}

	public DayField defaultToNow(final TimeZone zone)
	{
		return new DayField(isfinal, optional, unique, copyFrom, new DefaultNow(zone));
	}

	public boolean isDefaultNow()
	{
		return defaultSource instanceof DefaultNow;
	}

	public TimeZone getDefaultNowZimeZone()
	{
		return
			defaultSource instanceof DefaultNow
			? ((DefaultNow)defaultSource).zone
			: null;
	}

	public SelectType<Day> getValueType()
	{
		return SimpleSelectType.DAY;
	}

	@Override
	final void mount(final Type<? extends Item> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);

		if(suspiciousForWrongDefaultNow() && logger.isWarnEnabled())
			logger.warn(
					"Very probably you called \"DayField.defaultTo(new Day())\" on field {}. " +
					"This will not work as expected, use \"defaultToNow()\" instead.",
					getID());
	}

	private boolean suspiciousForWrongDefaultNow()
	{
		final Day defaultConstant = getDefaultConstant();
		if(defaultConstant==null)
			return false;

		return defaultConstant.equals(new Day(new Date(getDefaultConstantCreatedTimeMillis())));
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new DayColumn(table, name, optional);
	}

	@Override
	Day get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : DayColumn.getDay(((Integer)cell).intValue());
	}

	@Override
	void set(final Row row, final Day surface)
	{
		row.put(getColumn(), surface==null ? null : Integer.valueOf(DayColumn.getTransientNumber(surface)));
	}

	/**
	 * @throws FinalViolationException
	 *         if this field is {@link #isFinal() final}.
	 */
	@Wrap(order=10,
			doc="Sets today for the date field {0}.", // TODO better text
			hide=FinalSettableGetter.class)
	public void touch(final Item item)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		try
		{
			set(item, new Day()); // TODO: make a more efficient implementation
		}
		catch(final MandatoryViolationException e)
		{
			throw new RuntimeException(toString(), e);
		}
	}

	static Day unmarshal(final java.sql.Date cell)
	{
		// OK: need information about the day without taking time into account
		@SuppressWarnings("deprecation") final int year = cell.getYear();
		@SuppressWarnings("deprecation") final int month = cell.getMonth();
		@SuppressWarnings("deprecation") final int date = cell.getDate();
		return new Day(year+1900, month+1, date);
	}

	static java.sql.Date marshal(final Day cell)
	{
		final int year = cell.getYear()-1900;
		final int month = cell.getMonth()-1;
		final int day = cell.getDay();
		// OK: need information about the day without taking time into account
		@SuppressWarnings("deprecation")
		final java.sql.Date result = new java.sql.Date(year, month, day);
		return result;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #defaultToNow(TimeZone)} instead.
	 */
	@Deprecated
	public DayField defaultToNow()
	{
		return defaultToNow(TimeZone.getDefault());
	}
}
