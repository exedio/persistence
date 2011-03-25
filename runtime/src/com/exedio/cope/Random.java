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

import java.util.Collection;

import com.exedio.cope.CompareFunctionCondition.Operator;
import com.exedio.cope.search.AverageAggregate;
import com.exedio.cope.search.ExtremumAggregate;
import com.exedio.cope.search.SumAggregate;

public class Random implements NumberFunction<Double>
{
	private final Type<?> type;
	private final int seed;

	Random(final Type<?> type, final int seed)
	{
		if(type==null)
			throw new NullPointerException("type");

		this.type = type;
		this.seed = seed;
	}

	public Double get(final Item item)
	{
		throw new RuntimeException();
	}

	public Class<Double> getValueClass()
	{
		return Double.class;
	}

	public Type<? extends Item> getType()
	{
		return type;
	}

	@Override
	public final boolean equals(final Object other)
	{
		if(!(other instanceof Random))
			return false;

		final Random o = (Random)other;
		return type.equals(o.type) && seed==o.seed;
	}

	@Override
	public final int hashCode()
	{
		return type.hashCode() ^ seed;
	}

	public void toString(final StringBuilder bf, final Type defaultType)
	{
		if(defaultType!=type)
			bf.append(type.id).
				append('.');

		bf.append("rand(").
			append(seed).
			append(')');
	}

	@Override
	public final String toString()
	{
		final StringBuilder bf = new StringBuilder();
		toString(bf, null);
		return bf.toString();
	}

	@Deprecated // OK: for internal use within COPE only
	public void append(final Statement bf, final Join join)
	{
		if(!type.getModel().supportsRandom())
			throw new IllegalArgumentException("random not supported by this dialect");

		bf.append("rand(").
			appendParameter(seed).
			append(')');
	}

	@Deprecated // OK: for internal use within COPE only
	public void appendSelect(final Statement bf, final Join join)
	{
		append(bf, join);
	}

	@Deprecated // OK: for internal use within COPE only
	public void check(final TC tc, final Join join)
	{
		// nothing to do here, since there are no sources
	}

	// convenience methods for conditions and views ---------------------------------

	public final BindNumberFunction<Double> bind(final Join join)
	{
		return new BindNumberFunction<Double>(this, join);
	}

	public final IsNullCondition<Double> isNull()
	{
		return new IsNullCondition<Double>(this, false);
	}

	public final IsNullCondition<Double> isNotNull()
	{
		return new IsNullCondition<Double>(this, true);
	}

	public final Condition equal(final Double value)
	{
		return Cope.equal(this, value);
	}

	public final Condition equal(final Join join, final Double value)
	{
		return this.bind(join).equal(value);
	}

	public final Condition in(final Double... values)
	{
		return CompositeCondition.in(this, values);
	}

	public final Condition in(final Collection<Double> values)
	{
		return CompositeCondition.in(this, values);
	}

	public final Condition notEqual(final Double value)
	{
		return Cope.notEqual(this, value);
	}

	public final CompareCondition<Double> less(final Double value)
	{
		return new CompareCondition<Double>(Operator.Less, this, value);
	}

	public final CompareCondition<Double> lessOrEqual(final Double value)
	{
		return new CompareCondition<Double>(Operator.LessEqual, this, value);
	}

	public final CompareCondition<Double> greater(final Double value)
	{
		return new CompareCondition<Double>(Operator.Greater, this, value);
	}

	public final CompareCondition<Double> greaterOrEqual(final Double value)
	{
		return new CompareCondition<Double>(Operator.GreaterEqual, this, value);
	}

	public Condition between(final Double lowerBound, final Double upperBound)
	{
		return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
	}

	public final CompareFunctionCondition<Double> equal(final Function<? extends Double> right)
	{
		return new CompareFunctionCondition<Double>(Operator.Equal, this, right);
	}

	public final CompareFunctionCondition<Double> notEqual(final Function<? extends Double> right)
	{
		return new CompareFunctionCondition<Double>(Operator.NotEqual, this, right);
	}

	public final CompareFunctionCondition<Double> less(final Function<? extends Double> right)
	{
		return new CompareFunctionCondition<Double>(Operator.Less, this, right);
	}

	public final CompareFunctionCondition<Double> lessOrEqual(final Function<? extends Double> right)
	{
		return new CompareFunctionCondition<Double>(Operator.LessEqual, this, right);
	}

	public final CompareFunctionCondition<Double> greater(final Function<? extends Double> right)
	{
		return new CompareFunctionCondition<Double>(Operator.Greater, this, right);
	}

	public final CompareFunctionCondition<Double> greaterOrEqual(final Function<? extends Double> right)
	{
		return new CompareFunctionCondition<Double>(Operator.GreaterEqual, this, right);
	}

	public final ExtremumAggregate<Double> min()
	{
		return new ExtremumAggregate<Double>(this, true);
	}

	public final ExtremumAggregate<Double> max()
	{
		return new ExtremumAggregate<Double>(this, false);
	}

	public final AsStringView asString()
	{
		return new AsStringView(this);
	}

	public final PlusLiteralView<Double> plus(final Double value)
	{
		return new PlusLiteralView<Double>(this, value);
	}

	public final MultiplyLiteralView<Double> multiply(final Double value)
	{
		return new MultiplyLiteralView<Double>(this, value);
	}

	public final PlusView<Double> plus(final NumberFunction<Double> other)
	{
		return new PlusView<Double>(new NumberFunction[]{this, other});
	}

	public final MultiplyView<Double> multiply(final NumberFunction<Double> other)
	{
		return new MultiplyView<Double>(new NumberFunction[]{this, other});
	}

	public final DivideView<Double> divide(final NumberFunction<Double> other)
	{
		return new DivideView<Double>(this, other);
	}

	public final SumAggregate<Double> sum()
	{
		return new SumAggregate<Double>(this);
	}

	public final AverageAggregate<Double> average()
	{
		return new AverageAggregate<Double>(this);
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated renamed to {@link #plus(NumberFunction)}.
	 */
	@Deprecated
	public final PlusView<Double> sum(final NumberFunction<Double> other)
	{
		return plus(other);
	}
}
