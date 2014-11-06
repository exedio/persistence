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

import static com.exedio.cope.util.Clock.newDate;

import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import java.lang.reflect.AnnotatedElement;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DateField extends FunctionField<Date>
{
	private static final Logger logger = LoggerFactory.getLogger(DateField.class);

	private static final long serialVersionUID = 1l;

	private DateField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final ItemField<?>[] copyFrom,
			final DefaultSource<Date> defaultSource)
	{
		super(isfinal, optional, unique, copyFrom, Date.class, defaultSource);
		mountDefaultSource();
	}

	public DateField()
	{
		this(false, false, false, null, null);
	}

	@Override
	public DateField copy()
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultSource);
	}

	@Override
	public DateField toFinal()
	{
		return new DateField(true, optional, unique, copyFrom, defaultSource);
	}

	@Override
	public DateField optional()
	{
		return new DateField(isfinal, true, unique, copyFrom, defaultSource);
	}

	@Override
	public DateField unique()
	{
		return new DateField(isfinal, optional, true, copyFrom, defaultSource);
	}

	@Override
	public DateField nonUnique()
	{
		return new DateField(isfinal, optional, false, copyFrom, defaultSource);
	}

	@Override
	public DateField copyFrom(final ItemField<?> copyFrom)
	{
		return new DateField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultSource);
	}

	@Override
	public DateField noDefault()
	{
		return new DateField(isfinal, optional, unique, copyFrom, null);
	}

	@Override
	public DateField defaultTo(final Date defaultConstant)
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultConstantWithCreatedTime(defaultConstant));
	}

	private static final DefaultSource<Date> DEFAULT_TO_NOW = new DefaultSource<Date>()
	{
		@Override
		Date generate(final long now)
		{
			return new Date(now);
		}

		@Override
		DefaultSource<Date> forNewField()
		{
			return this;
		}

		@Override
		void mount(final FunctionField<Date> field)
		{
			// nothing to be checked
		}
	};

	public DateField defaultToNow()
	{
		return new DateField(isfinal, optional, unique, copyFrom, DEFAULT_TO_NOW);
	}

	public boolean isDefaultNow()
	{
		return defaultSource==DEFAULT_TO_NOW;
	}

	public SelectType<Date> getValueType()
	{
		return SimpleSelectType.DATE;
	}

	@Override
	final void mount(final Type<? extends Item> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);

		if(suspiciousForWrongDefaultNow() && logger.isWarnEnabled())
			logger.warn(
					"Very probably you called \"DateField.defaultTo(new Date())\" on field {}. " +
					"This will not work as expected, use \"defaultToNow()\" instead.",
					getID());
	}

	private boolean suspiciousForWrongDefaultNow()
	{
		final Date defaultConstant = getDefaultConstant();
		if(defaultConstant==null)
			return false;

		return Math.abs(defaultConstant.getTime()-getDefaultConstantCreatedTimeMillis())<100;
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return
				getType().getModel().connect().supportsNativeDate()
				? (Column)new TimestampColumn(table, name, optional)
				: (Column)new IntegerColumn(table, name, false, optional, Long.MIN_VALUE, Long.MAX_VALUE, true);
	}

	@Override
	Date get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : new Date(((Long)cell).longValue());
	}

	@Override
	void set(final Row row, final Date surface)
	{
		row.put(getColumn(), surface==null ? null : Long.valueOf(surface.getTime()));
	}

	/**
	 * @throws FinalViolationException
	 *         if this field is {@link #isFinal() final}.
	 */
	@Wrap(order=10,
			doc="Sets the current date for the date field {0}.", // TODO better text
			hide=FinalSettableGetter.class)
	public void touch(final Item item)
	{
		set(item, newDate()); // TODO: make a more efficient implementation
	}
}
