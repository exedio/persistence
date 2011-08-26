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

import java.util.List;
import java.util.Set;

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;

public final class LongField extends NumberField<Long>
{
	private static final long serialVersionUID = 1l;

	private final long minimum;
	private final long maximum;

	private LongField(
			final boolean isfinal, final boolean optional, final boolean unique,
			final Long defaultConstant,
			final long minimum, final long maximum)
	{
		super(isfinal, optional, unique, Long.class, defaultConstant);
		this.minimum = minimum;
		this.maximum = maximum;
		checkDefaultConstant();
	}

	public LongField()
	{
		this(false, false, false, null, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	@Override
	public LongField copy()
	{
		return new LongField(isfinal, optional, unique, defaultConstant, minimum, maximum);
	}

	@Override
	public LongField toFinal()
	{
		return new LongField(true, optional, unique, defaultConstant, minimum, maximum);
	}

	@Override
	public LongField optional()
	{
		return new LongField(isfinal, true, unique, defaultConstant, minimum, maximum);
	}

	@Override
	public LongField unique()
	{
		return new LongField(isfinal, optional, true, defaultConstant, minimum, maximum);
	}

	@Override
	public LongField nonUnique()
	{
		return new LongField(isfinal, optional, false, defaultConstant, minimum, maximum);
	}

	@Override
	public LongField noDefault()
	{
		return new LongField(isfinal, optional, unique, null, minimum, maximum);
	}

	@Override
	public LongField defaultTo(final Long defaultConstant)
	{
		return new LongField(isfinal, optional, unique, defaultConstant, minimum, maximum);
	}

	public LongField range(final int minimum, final int maximum)
	{
		return new LongField(isfinal, optional, unique, defaultConstant, minimum, maximum);
	}

	public LongField min(final int minimum)
	{
		return new LongField(isfinal, optional, unique, defaultConstant, minimum, maximum);
	}

	public LongField max(final int maximum)
	{
		return new LongField(isfinal, optional, unique, defaultConstant, minimum, maximum);
	}

	public long getMinimum()
	{
		return minimum;
	}

	public long getMaximum()
	{
		return maximum;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
		if(minimum!=Long.MIN_VALUE || maximum!=Long.MAX_VALUE)
			result.add(LongRangeViolationException.class);
		return result;
	}

	@Deprecated
	@Override
	public Class getInitialType()
	{
		return optional ? Long.class : long.class;
	}

	public SelectType<Long> getValueType()
	{
		return SimpleSelectType.LONG;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		return adjustOrderForPrimitiveOperations(Wrapper.getByAnnotations(LongField.class, this, super.getWrappers()));
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new IntegerColumn(table, this, name, false, optional, minimum, maximum, true);
	}

	@Override
	Long get(final Row row)
	{
		return (Long)row.get(getColumn());
	}

	@Override
	void set(final Row row, final Long surface)
	{
		row.put(getColumn(), surface);
	}

	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	@Wrap(order=10, name="get{0}", doc="Returns the value of {0}.", hide=OptionalGetter.class)
	public final long getMandatory(final Item item)
	{
		return getMandatoryObject(item).longValue();
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			hide={FinalGetter.class, OptionalGetter.class},
			thrownGetter=InitialThrown.class)
	public final void set(final Item item, final long value)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		set(item, Long.valueOf(value));
	}

	/**
	 * Finds an item by it's unique fields.
	 * @return null if there is no matching item.
	 * @see FunctionField#searchUnique(Class, Object)
	 */
	@Wrap(order=100, name="for{0}",
			doc="Finds a {2} by it''s {0}.",
			docReturn="null if there is no matching item.",
			hide={OptionalGetter.class, NonUniqueGetter.class})
	public final <P extends Item> P searchUnique(
			final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.") final long value)
	{
		return super.searchUnique(typeClass, Long.valueOf(value));
	}

	@Override
	void checkNotNull(final Long value, final Item exceptionItem) throws LongRangeViolationException
	{
		final long valuePrimitive = value.longValue();
		if(valuePrimitive<minimum)
			throw new LongRangeViolationException(this, exceptionItem, value, true, minimum);
		if(valuePrimitive>maximum)
			throw new LongRangeViolationException(this, exceptionItem, value, false, maximum);
	}
}
