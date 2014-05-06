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

package com.exedio.cope.pattern;

import com.exedio.cope.CompareCondition;
import com.exedio.cope.CompareFunctionCondition;
import com.exedio.cope.Condition;
import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegerField;
import com.exedio.cope.IsNullCondition;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import java.util.Set;

public final class PriceField extends Pattern implements Settable<Price>, Copyable
{
	private static final long serialVersionUID = 1l;

	private final IntegerField integer;
	private final boolean isfinal;
	private final boolean mandatory;

	public PriceField()
	{
		this(new IntegerField());
	}

	private PriceField(final IntegerField integer)
	{
		this.integer = integer;
		addSource(integer, "int", ComputedElement.get());
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

	public IntegerField getInt()
	{
		return integer;
	}

	public boolean isInitial()
	{
		return integer.isInitial();
	}

	public boolean isFinal()
	{
		return isfinal;
	}

	public boolean isMandatory()
	{
		return mandatory;
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

	public Class<?> getInitialType()
	{
		return Price.class;
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return integer.getInitialExceptions();
	}

	@Wrap(order=10, doc="Returns the value of {0}.")
	public Price get(final Item item)
	{
		return
			mandatory
			? Price.storeOf(integer.getMandatory(item))
			: Price.storeOf(integer.get(item));
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(final Item item, final Price value)
	{
		if(isfinal)
			throw FinalViolationException.create(this, item);
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, item);

		integer.set(item, value!=null ? value.store() : null);
	}

	public SetValue<Price> map(final Price value)
	{
		return SetValue.map(this, value);
	}

	public SetValue<?>[] execute(final Price value, final Item exceptionItem)
	{
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, exceptionItem);

		return new SetValue<?>[]{ integer.map(value!=null ? value.store() : null) };
	}

	// convenience methods for conditions and views ---------------------------------

	public final IsNullCondition<?> isNull()
	{
		return integer.isNull();
	}

	public final IsNullCondition<?> isNotNull()
	{
		return integer.isNotNull();
	}

	public Condition equal(final Price value)
	{
		return value!=null ? integer.equal(value.store()) : integer.isNull();
	}

	public Condition notEqual(final Price value)
	{
		return value!=null ? integer.notEqual(value.store()) : integer.isNotNull();
	}

	public final CompareCondition<?> less(final Price value)
	{
		return integer.less(value.store());
	}

	public final CompareCondition<?> lessOrEqual(final Price value)
	{
		return integer.lessOrEqual(value.store());
	}

	public final CompareCondition<?> greater(final Price value)
	{
		return integer.greater(value.store());
	}

	public final CompareCondition<?> greaterOrEqual(final Price value)
	{
		return integer.greaterOrEqual(value.store());
	}

	public Condition between(final Price lowerBound, final Price upperBound)
	{
		return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
	}

	public final CompareFunctionCondition<?> equal(final PriceField right)
	{
		return integer.equal(right.integer);
	}

	public final CompareFunctionCondition<?> notEqual(final PriceField right)
	{
		return integer.notEqual(right.integer);
	}

	public final CompareFunctionCondition<?> less(final PriceField right)
	{
		return integer.less(right.integer);
	}

	public final CompareFunctionCondition<?> lessOrEqual(final PriceField right)
	{
		return integer.lessOrEqual(right.integer);
	}

	public final CompareFunctionCondition<?> greater(final PriceField right)
	{
		return integer.greater(right.integer);
	}

	public final CompareFunctionCondition<?> greaterOrEqual(final PriceField right)
	{
		return integer.greaterOrEqual(right.integer);
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #min(Price)} instead.
	 */
	@Deprecated
	public PriceField min(final int minimum)
	{
		return new PriceField(integer.min(minimum));
	}
}
