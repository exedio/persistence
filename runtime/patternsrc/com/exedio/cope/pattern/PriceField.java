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

package com.exedio.cope.pattern;

import com.exedio.cope.CompareCondition;
import com.exedio.cope.CompareFunctionCondition;
import com.exedio.cope.Condition;
import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IsNullCondition;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.LongField;
import com.exedio.cope.LongRangeViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import java.util.Set;
import javax.annotation.Nonnull;

@WrapFeature
public final class PriceField extends Pattern implements Settable<Price>, Copyable, PriceFunction
{
	private static final long serialVersionUID = 1l;

	private final LongField integer;
	private final boolean isfinal;
	private final boolean mandatory;

	public PriceField()
	{
		this(new LongField().range(
				Price.MIN_VALUE.store(),
				Price.MAX_VALUE.store()));
	}

	private PriceField(final LongField integer)
	{
		this.integer = addSourceFeature(integer, "int", ComputedElement.get());
		this.isfinal = integer.isFinal();
		this.mandatory = integer.isMandatory();
	}

	@Override
	public PriceField copy(final CopyMapper mapper)
	{
		return new PriceField(mapper.copy(integer));
	}

	public PriceField toFinal()
	{
		return new PriceField(integer.toFinal());
	}

	public PriceField optional()
	{
		return new PriceField(integer.optional());
	}

	public PriceField defaultTo(final Price defaultConstant)
	{
		return new PriceField(integer.defaultTo(defaultConstant.store()));
	}

	public PriceField rangeEvenIfRedundant(final Price minimum, final Price maximum)
	{
		return new PriceField(integer.rangeEvenIfRedundant(minimum.store(), maximum.store()));
	}

	public PriceField range(final Price minimum, final Price maximum)
	{
		return new PriceField(integer.range(minimum.store(), maximum.store()));
	}

	public PriceField min(final Price minimum)
	{
		return new PriceField(integer.min(minimum.store()));
	}

	public PriceField minZero()
	{
		return min(Price.ZERO);
	}

	public PriceField max(final Price maximum)
	{
		return new PriceField(integer.max(maximum.store()));
	}

	public LongField getInt()
	{
		return integer;
	}

	@Override
	public boolean isInitial()
	{
		return integer.isInitial();
	}

	@Override
	public boolean isFinal()
	{
		return isfinal;
	}

	@Override
	public boolean isMandatory()
	{
		return mandatory;
	}

	@Override
	public Class<?> getInitialType()
	{
		return Price.class;
	}

	public Price getDefaultConstant()
	{
		return Price.storeOf(integer.getDefaultConstant());
	}

	public Price getMinimum()
	{
		return Price.storeOf(integer.getMinimum());
	}

	public Price getMaximum()
	{
		return Price.storeOf(integer.getMaximum());
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = integer.getInitialExceptions();

		if(integer.getMinimum()==Price.MIN_VALUE.store() &&
			integer.getMaximum()==Price.MAX_VALUE.store())
			result.remove(LongRangeViolationException.class);

		return result;
	}

	@Wrap(order=10, doc=Wrap.GET_DOC, nullability=NullableIfOptional.class)
	public Price get(@Nonnull final Item item)
	{
		return
			mandatory
			? Price.storeOf(integer.getMandatory(item))
			: Price.storeOf(integer.get(item));
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final Price value)
	{
		FinalViolationException.check(this, item);
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, item);

		integer.set(item, value!=null ? value.store() : null);
	}

	@Override
	public SetValue<?>[] execute(final Price value, final Item exceptionItem)
	{
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, exceptionItem);

		return new SetValue<?>[]{ SetValue.map(integer, value!=null ? value.store() : null) };
	}

	// convenience methods for conditions and views ---------------------------------

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 * Replace {@code field.bind(j).equal(Price.ZERO)} by {@code field.equal(Price.ZERO).bind(j)}.
	 */
	@Deprecated
	public PriceFunction bind(final Join join)
	{
		return new PriceBindFunction(this, join);
	}

	@Override
	public IsNullCondition<?> isNull()
	{
		return integer.isNull();
	}

	@Override
	public IsNullCondition<?> isNotNull()
	{
		return integer.isNotNull();
	}

	@Override
	public Condition equal(final Price value)
	{
		return value!=null ? integer.equal(value.store()) : integer.isNull();
	}

	@Override
	public Condition notEqual(final Price value)
	{
		return value!=null ? integer.notEqual(value.store()) : integer.isNotNull();
	}

	@Override
	public CompareCondition<?> less(final Price value)
	{
		return integer.less(value.store());
	}

	@Override
	public CompareCondition<?> lessOrEqual(final Price value)
	{
		return integer.lessOrEqual(value.store());
	}

	@Override
	public CompareCondition<?> greater(final Price value)
	{
		return integer.greater(value.store());
	}

	@Override
	public CompareCondition<?> greaterOrEqual(final Price value)
	{
		return integer.greaterOrEqual(value.store());
	}

	@Override
	public Condition between(final Price lowerBound, final Price upperBound)
	{
		return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
	}

	public CompareFunctionCondition<?> equal(final PriceField right)
	{
		return integer.equal(right.integer);
	}

	public CompareFunctionCondition<?> notEqual(final PriceField right)
	{
		return integer.notEqual(right.integer);
	}

	public CompareFunctionCondition<?> less(final PriceField right)
	{
		return integer.less(right.integer);
	}

	public CompareFunctionCondition<?> lessOrEqual(final PriceField right)
	{
		return integer.lessOrEqual(right.integer);
	}

	public CompareFunctionCondition<?> greater(final PriceField right)
	{
		return integer.greater(right.integer);
	}

	public CompareFunctionCondition<?> greaterOrEqual(final PriceField right)
	{
		return integer.greaterOrEqual(right.integer);
	}
}
