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

import java.util.Set;

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;

public final class DoubleField extends NumberField<Double>
{
	private static final long serialVersionUID = 1l;

	private static final double MIN = -Double.MAX_VALUE;
	private static final double MAX = Double.MAX_VALUE;

	private final double minimum;
	private final double maximum;

	private DoubleField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final ItemField<?>[] copyFrom,
			final DefaultSource<Double> defaultSource,
			final double minimum,
			final double maximum)
	{
		super(isfinal, optional, unique, copyFrom, Double.class, defaultSource);
		this.minimum = minimum;
		this.maximum = maximum;

		assertLimit(minimum, "minimum");
		assertLimit(maximum, "maximum");
		if(minimum>=maximum)
			throw new IllegalArgumentException("maximum must be greater than mimimum, but was " + maximum + " and " + minimum + '.');

		mountDefaultSource();
	}

	private static final void assertLimit(final double value, final String name)
	{
		if(Double.isInfinite(value))
			throw new IllegalArgumentException(name + " must not be infinite, but was " + value + '.');
		if(Double.isNaN(value))
			throw new IllegalArgumentException(name + " must not be NaN, but was " + value + '.');
	}

	public DoubleField()
	{
		this(false, false, false, null, null, MIN, MAX);
	}

	@Override
	public DoubleField copy()
	{
		return new DoubleField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public DoubleField toFinal()
	{
		return new DoubleField(true, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public DoubleField optional()
	{
		return new DoubleField(isfinal, true, unique, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public DoubleField unique()
	{
		return new DoubleField(isfinal, optional, true, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public DoubleField nonUnique()
	{
		return new DoubleField(isfinal, optional, false, copyFrom, defaultSource, minimum, maximum);
	}

	@Override
	public DoubleField copyFrom(final ItemField<?> copyFrom)
	{
		return new DoubleField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultSource, minimum, maximum);
	}

	@Override
	public DoubleField noDefault()
	{
		return new DoubleField(isfinal, optional, unique, copyFrom, null, minimum, maximum);
	}

	@Override
	public DoubleField defaultTo(final Double defaultConstant)
	{
		return new DoubleField(isfinal, optional, unique, copyFrom, defaultConstant(defaultConstant), minimum, maximum);
	}

	public DoubleField range(final double minimum, final double maximum)
	{
		return new DoubleField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	public DoubleField min(final double minimum)
	{
		return new DoubleField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	public DoubleField max(final double maximum)
	{
		return new DoubleField(isfinal, optional, unique, copyFrom, defaultSource, minimum, maximum);
	}

	public double getMinimum()
	{
		return minimum;
	}

	public double getMaximum()
	{
		return maximum;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
		if(minimum!=MIN || maximum!=MAX)
			result.add(DoubleRangeViolationException.class);
		return result;
	}

	@Deprecated
	@Override
	public Class<?> getInitialType()
	{
		return optional ? Double.class : double.class;
	}

	public SelectType<Double> getValueType()
	{
		return SimpleSelectType.DOUBLE;
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new DoubleColumn(table, name, optional, minimum, maximum);
	}

	@Override
	Double get(final Row row)
	{
		return (Double)row.get(getColumn());
	}

	@Override
	void set(final Row row, final Double surface)
	{
		row.put(getColumn(), surface);
	}

	@Override
	void checkNotNull(final Double value, final Item exceptionItem) throws IntegerRangeViolationException
	{
		final double valuePrimitive = value.doubleValue();

		// TODO better exceptions
		if(Double.isInfinite(valuePrimitive))
			throw new RuntimeException(getID() + '#' + valuePrimitive);
		if(Double.isNaN(valuePrimitive))
			throw new RuntimeException(getID() + '#' + valuePrimitive);

		if(valuePrimitive<minimum)
			throw new DoubleRangeViolationException(this, exceptionItem, value, true, minimum);
		if(valuePrimitive>maximum)
			throw new DoubleRangeViolationException(this, exceptionItem, value, false, maximum);
	}

	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	@Wrap(order=10, name="get{0}", doc="Returns the value of {0}.", hide=OptionalGetter.class)
	public final double getMandatory(final Item item)
	{
		return getMandatoryObject(item).doubleValue();
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			hide={FinalGetter.class, OptionalGetter.class},
			thrownGetter=InitialThrown.class)
	public final void set(final Item item, final double value)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		try
		{
			set(item, Double.valueOf(value));
		}
		catch(final MandatoryViolationException e)
		{
			throw new RuntimeException(toString(), e);
		}
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
			@Parameter(doc="shall be equal to field {0}.") final double value)
	{
		return super.searchUnique(typeClass, Double.valueOf(value));
	}

	@Override
	public Condition equal(final Double value)
	{
		if(value!=null)
		{
			final double valuePrimitive = value.doubleValue();
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.FALSE;
			else
				return super.equal(value);
		}
		else
			return isNull();
	}

	@Override
	public Condition notEqual(final Double value)
	{
		if(value!=null)
		{
			final double valuePrimitive = value.doubleValue();
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.TRUE;
			else
				return super.notEqual(value);
		}
		else
			return isNotNull();
	}
	// TODO the same for less, lessEqual, greater, greaterEqual
}
