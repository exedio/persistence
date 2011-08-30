/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.lang.reflect.AnnotatedElement;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.Day;

public final class DayField extends FunctionField<Day>
{
	static final Logger logger = Logger.getLogger(DayField.class.getName());

	private static final long serialVersionUID = 1l;

	private final long defaultConstantSet;
	final boolean defaultNow;

	private DayField(
			final boolean isfinal, final boolean optional, final boolean unique,
			final Day defaultConstant, final long defaultConstantSet, final boolean defaultNow)
	{
		super(isfinal, optional, unique, Day.class, defaultConstant);
		this.defaultConstantSet = defaultConstantSet;
		this.defaultNow = defaultNow;

		if(defaultConstant!=null && defaultNow)
			throw new IllegalStateException("cannot use defaultConstant and defaultNow together");
		assert (defaultConstant!=null) == (defaultConstantSet!=Integer.MIN_VALUE);
		checkDefaultConstant();
	}

	public DayField()
	{
		this(false, false, false, null, Integer.MIN_VALUE, false);
	}

	@Override
	public DayField copy()
	{
		return new DayField(isfinal, optional, unique, defaultConstant, defaultConstantSet, defaultNow);
	}

	@Override
	public DayField toFinal()
	{
		return new DayField(true, optional, unique, defaultConstant, defaultConstantSet, defaultNow);
	}

	@Override
	public DayField optional()
	{
		return new DayField(isfinal, true, unique, defaultConstant, defaultConstantSet, defaultNow);
	}

	@Override
	public DayField unique()
	{
		return new DayField(isfinal, optional, true, defaultConstant, defaultConstantSet, defaultNow);
	}

	@Override
	public DayField nonUnique()
	{
		return new DayField(isfinal, optional, false, defaultConstant, defaultConstantSet, defaultNow);
	}

	@Override
	public DayField noDefault()
	{
		return new DayField(isfinal, optional, unique, null, Integer.MIN_VALUE, false);
	}

	@Override
	public DayField defaultTo(final Day defaultConstant)
	{
		return new DayField(isfinal, optional, unique, defaultConstant, System.currentTimeMillis(), defaultNow);
	}

	public DayField defaultToNow()
	{
		return new DayField(isfinal, optional, unique, defaultConstant, defaultConstantSet, true);
	}

	public boolean isDefaultNow()
	{
		return defaultNow;
	}

	public SelectType<Day> getValueType()
	{
		return SimpleSelectType.DAY;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(DayField.class, this, super.getWrappers());
	}

	@Override
	public boolean isInitial()
	{
		return !defaultNow && super.isInitial();
	}

	@Override
	final void mount(final Type<? extends Item> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);

		if(suspiciousForWrongDefaultNow() && logger.isEnabledFor(Level.WARN))
			logger.warn( MessageFormat.format(
					"Very probably you called \"DayField.defaultTo(new Day())\" on field {0}. " +
					"This will not work as expected, use \"defaultToNow()\" instead.",
					getID() ) );
	}

	private boolean suspiciousForWrongDefaultNow()
	{
		return defaultConstant!=null && defaultConstant.equals(new Day(new Date(defaultConstantSet)));
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
			hide=FinalGetter.class)
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
}
