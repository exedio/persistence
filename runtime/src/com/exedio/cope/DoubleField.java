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

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
			final CopyFrom[] copyFrom,
			final DefaultSupplier<Double> defaultS,
			final double minimum,
			final double maximum)
	{
		super(isfinal, optional, Double.class, unique, copyFrom, defaultS);
		this.minimum = minimum;
		this.maximum = maximum;

		assertLimit(minimum, "minimum");
		assertLimit(maximum, "maximum");
		if(minimum>maximum)
			throw new IllegalArgumentException("maximum must be at least minimum, but was " + maximum + " and " + minimum);

		mountDefault();
	}

	private static void assertLimit(final double value, final String name)
	{
		if(Double.isInfinite(value))
			throw new IllegalArgumentException(name + " must not be infinite, but was " + value);
		if(Double.isNaN(value))
			throw new IllegalArgumentException(name + " must not be NaN, but was " + value);
	}

	public DoubleField()
	{
		this(false, false, false, null, null, MIN, MAX);
	}

	@Override
	public DoubleField copy()
	{
		return new DoubleField(isfinal, optional, unique, copyFrom, defaultS, minimum, maximum);
	}

	@Override
	public DoubleField toFinal()
	{
		return new DoubleField(true, optional, unique, copyFrom, defaultS, minimum, maximum);
	}

	@Override
	public DoubleField optional()
	{
		return new DoubleField(isfinal, true, unique, copyFrom, defaultS, minimum, maximum);
	}

	@Override
	public DoubleField unique()
	{
		return new DoubleField(isfinal, optional, true, copyFrom, defaultS, minimum, maximum);
	}

	@Override
	public DoubleField nonUnique()
	{
		return new DoubleField(isfinal, optional, false, copyFrom, defaultS, minimum, maximum);
	}

	@Override
	public DoubleField copyFrom(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target));
	}

	@Override
	public DoubleField copyFrom(final ItemField<?> target, final Supplier<? extends FunctionField<Double>> template)
	{
		return copyFrom(new CopyFrom(target, template));
	}

	@Override
	public DoubleField copyFromSelf(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.SELF_TEMPLATE));
	}

	private DoubleField copyFrom(final CopyFrom copyFrom)
	{
		return new DoubleField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultS, minimum, maximum);
	}

	@Override
	public DoubleField noCopyFrom()
	{
		return new DoubleField(isfinal, optional, unique, null, defaultS, minimum, maximum);
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

	public DoubleField rangeEvenIfRedundant(final double minimum, final double maximum)
	{
		return new DoubleField(isfinal, optional, unique, copyFrom, defaultS, minimum, maximum);
	}

	public DoubleField range(final double minimum, final double maximum)
	{
		return rangeEvenIfRedundant(minimum, maximum).failOnRedundantRange();
	}

	public DoubleField min(final double minimum)
	{
		return new DoubleField(isfinal, optional, unique, copyFrom, defaultS, minimum, maximum).failOnRedundantRange();
	}

	public DoubleField max(final double maximum)
	{
		return new DoubleField(isfinal, optional, unique, copyFrom, defaultS, minimum, maximum).failOnRedundantRange();
	}

	private DoubleField failOnRedundantRange()
	{
		//noinspection FloatingPointEquality OK: the redundant case
		if(minimum==maximum)
			throw new IllegalArgumentException(
					"Redundant field with minimum==maximum " +
					"(" + minimum + ") is probably a mistake. " +
					"You may call method rangeEvenIfRedundant if you are sure this is ok.");
		return this;
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
		//noinspection FloatingPointEquality
		if(minimum!=MIN || maximum!=MAX)
			result.add(DoubleRangeViolationException.class);
		return result;
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<Double> getValueType()
	{
		return SimpleSelectType.DOUBLE;
	}

	@Override
	Column createColumn(
			final Table table,
			final String name,
			final boolean optional,
			final Connect connect,
			final ModelMetrics metrics)
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
	void checkNotNull(final Double value, final Item exceptionItem)
	{
		final double valuePrimitive = value;

		if(Double.isNaN(valuePrimitive))
			throw new DoubleNaNException(this, exceptionItem);

		if(valuePrimitive<minimum)
			throw new DoubleRangeViolationException(this, exceptionItem, value, true, minimum);
		if(valuePrimitive>maximum)
			throw new DoubleRangeViolationException(this, exceptionItem, value, false, maximum);
	}

	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	@Wrap(order=10, name="get{0}", doc=Wrap.GET_DOC, hide=OptionalGetter.class)
	public double getMandatory(@Nonnull final Item item)
	{
		return getMandatoryObject(item);
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			hide={FinalSettableGetter.class, RedundantByCopyConstraintGetter.class, OptionalGetter.class},
			thrownGetter=InitialThrown.class)
	public void set(@Nonnull final Item item, final double value)
	{
		set(item, Double.valueOf(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 * @see FunctionField#searchUnique(Class, Object)
	 */
	@Wrap(order=100, name=Wrap.FOR_NAME,
			doc=Wrap.FOR_DOC,
			docReturn=Wrap.FOR_RETURN,
			hide={OptionalGetter.class, NonUniqueGetter.class})
	@Nullable
	public <P extends Item> P searchUnique(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc=Wrap.FOR_PARAM) final double value)
	{
		return searchUnique(typeClass, Double.valueOf(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws NullPointerException if value is null.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	@Wrap(order=110, name=Wrap.FOR_STRICT_NAME,
			doc=Wrap.FOR_DOC,
			hide={OptionalGetter.class, NonUniqueGetter.class},
			thrown=@Wrap.Thrown(value=IllegalArgumentException.class, doc="if there is no matching item."))
	@Nonnull
	public <P extends Item> P searchUniqueStrict(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.") final double value)
			throws IllegalArgumentException
	{
		return searchUniqueStrict(typeClass, Double.valueOf(value));
	}

	@Override
	public Condition equal(final Double value)
	{
		if(value!=null)
		{
			// TODO does not work with BindFunction
			final double valuePrimitive = value;
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.ofFalse();
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
			// TODO does not work with BindFunction
			final double valuePrimitive = value;
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.ofTrue();
			else
				return super.notEqual(value);
		}
		else
			return isNotNull();
	}
	// TODO the same for less, lessEqual, greater, greaterEqual
}
