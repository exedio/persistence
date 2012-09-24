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

import java.lang.reflect.AnnotatedElement;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.exedio.cope.instrument.Wrap;

public final class DateField extends FunctionField<Date>
{
	static final Logger logger = Logger.getLogger(DateField.class.getName());

	private static final long serialVersionUID = 1l;

	private final long defaultConstantSet;
	final boolean defaultNow;

	private DateField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final ItemField<?> copyFrom,
			final Date defaultConstant,
			final long defaultConstantSet,
			final boolean defaultNow)
	{
		super(isfinal, optional, unique, copyFrom, Date.class, defaultConstant);
		this.defaultConstantSet = defaultConstantSet;
		this.defaultNow = defaultNow;

		if(defaultConstant!=null && defaultNow)
			throw new IllegalStateException("cannot use defaultConstant and defaultNow together");
		assert (defaultConstant!=null) == (defaultConstantSet!=Integer.MIN_VALUE);
		checkDefaultConstant();
	}

	public DateField()
	{
		this(false, false, false, null, null, Integer.MIN_VALUE, false);
	}

	@Override
	public DateField copy()
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultConstant, defaultConstantSet, defaultNow);
	}

	@Override
	public DateField toFinal()
	{
		return new DateField(true, optional, unique, copyFrom, defaultConstant, defaultConstantSet, defaultNow);
	}

	@Override
	public DateField optional()
	{
		return new DateField(isfinal, true, unique, copyFrom, defaultConstant, defaultConstantSet, defaultNow);
	}

	@Override
	public DateField unique()
	{
		return new DateField(isfinal, optional, true, copyFrom, defaultConstant, defaultConstantSet, defaultNow);
	}

	@Override
	public DateField nonUnique()
	{
		return new DateField(isfinal, optional, false, copyFrom, defaultConstant, defaultConstantSet, defaultNow);
	}

	public DateField copyFrom(final ItemField<?> copyFrom)
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultConstant, defaultConstantSet, defaultNow);
	}

	@Override
	public DateField noDefault()
	{
		return new DateField(isfinal, optional, unique, copyFrom, null, Integer.MIN_VALUE, false);
	}

	@Override
	public DateField defaultTo(final Date defaultConstant)
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultConstant, System.currentTimeMillis(), defaultNow);
	}

	public DateField defaultToNow()
	{
		return new DateField(isfinal, optional, unique, copyFrom, defaultConstant, defaultConstantSet, true);
	}

	public boolean isDefaultNow()
	{
		return defaultNow;
	}

	public SelectType<Date> getValueType()
	{
		return SimpleSelectType.DATE;
	}

	/**
	 * Returns true, if a value for the field should be specified
	 * on the creation of an item.
	 * This implementation returns
	 * <tt>({@link #isFinal() isFinal()} || {@link #isMandatory() isMandatory()}) && {@link #getDefaultConstant() getDefaultConstant()}==null && ! {@link #isDefaultNow()}</tt>.
	 */
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
					"Very probably you called \"DateField.defaultTo(new Date())\" on field {0}. " +
					"This will not work as expected, use \"defaultToNow()\" instead.",
					getID() ) );
	}

	private boolean suspiciousForWrongDefaultNow()
	{
		return defaultConstant!=null && Math.abs(defaultConstant.getTime()-defaultConstantSet)<100;
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
			hide=FinalGetter.class)
	public void touch(final Item item)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		try
		{
			set(item, new Date()); // TODO: make a more efficient implementation
		}
		catch(final MandatoryViolationException e)
		{
			throw new RuntimeException(toString(), e);
		}
	}
}
