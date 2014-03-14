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

import com.exedio.cope.CompareFunctionCondition.Operator;
import com.exedio.cope.search.ExtremumAggregate;
import java.util.Collection;

public class BindFunction<E> implements Function<E>
{
	private static final long serialVersionUID = 1l;

	final Function<E> function;
	final Join join;

	/**
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see Function#bind(Join)
	 */
	public BindFunction(final Function<E> function, final Join join)
	{
		assert function!=null;
		assert join!=null;

		this.function = function;
		this.join = join;
	}

	@Override
	public final E get(final Item item)
	{
		return function.get(item);
	}

	@Override
	public final Class<E> getValueClass()
	{
		return function.getValueClass();
	}

	@Override
	public SelectType<E> getValueType()
	{
		return function.getValueType();
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void check(final TC tc, final Join join)
	{
		function.check(tc, this.join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		function.append(bf, this.join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void appendSelect(final Statement bf, final Join join)
	{
		function.appendSelect(bf, this.join);
	}

	@Override
	public final Type<? extends Item> getType()
	{
		return function.getType();
	}

	@Override
	public final boolean equals(final Object other)
	{
		if(!(other instanceof BindFunction<?>))
			return false;

		final BindFunction<?> o = (BindFunction<?>)other;

		return function.equals(o.function) && join.index==o.join.index; // using Join#equals(Object) causes infinite recursion
	}

	@Override
	public final int hashCode()
	{
		return function.hashCode() ^ join.index; // using Join#hashCode() causes infinite recursion
	}

	@Override
	public final String toString()
	{
		return join.getToStringAlias() + '.' + function.toString();
	}

	@Override
	public final void toString(final StringBuilder bf, final Type<?> defaultType)
	{
		bf.append(join.getToStringAlias()).
			append('.');
		function.toString(bf, defaultType);
	}

	// convenience methods for conditions and views ---------------------------------

	@Override
	public final IsNullCondition<E> isNull()
	{
		return new IsNullCondition<E>(this, false);
	}

	@Override
	public final IsNullCondition<E> isNotNull()
	{
		return new IsNullCondition<E>(this, true);
	}

	@Override
	public final Condition equal(final E value)
	{
		return Cope.equal(this, value);
	}

	@Override
	public final Condition equal(final Join join, final E value)
	{
		return this.bind(join).equal(value);
	}

	@Override
	public final Condition in(final E... values)
	{
		return CompositeCondition.in(this, values);
	}

	@Override
	public final Condition in(final Collection<? extends E> values)
	{
		return CompositeCondition.in(this, values);
	}

	@Override
	public final Condition notEqual(final E value)
	{
		return Cope.notEqual(this, value);
	}

	@Override
	public final CompareCondition<E> less(final E value)
	{
		return new CompareCondition<E>(Operator.Less, this, value);
	}

	@Override
	public final CompareCondition<E> lessOrEqual(final E value)
	{
		return new CompareCondition<E>(Operator.LessEqual, this, value);
	}

	@Override
	public final CompareCondition<E> greater(final E value)
	{
		return new CompareCondition<E>(Operator.Greater, this, value);
	}

	@Override
	public final CompareCondition<E> greaterOrEqual(final E value)
	{
		return new CompareCondition<E>(Operator.GreaterEqual, this, value);
	}

	@Override
	public Condition between(final E lowerBound, final E upperBound)
	{
		return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
	}

	@Override
	public final CompareFunctionCondition<E> equal(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Equal, this, right);
	}

	@Override
	public final CompareFunctionCondition<E> notEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.NotEqual, this, right);
	}

	@Override
	public final CompareFunctionCondition<E> less(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Less, this, right);
	}

	@Override
	public final CompareFunctionCondition<E> lessOrEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.LessEqual, this, right);
	}

	@Override
	public final CompareFunctionCondition<E> greater(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Greater, this, right);
	}

	@Override
	public final CompareFunctionCondition<E> greaterOrEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.GreaterEqual, this, right);
	}

	@Override
	public final ExtremumAggregate<E> min()
	{
		return new ExtremumAggregate<E>(this, true);
	}

	@Override
	public final ExtremumAggregate<E> max()
	{
		return new ExtremumAggregate<E>(this, false);
	}

	/**
	 * Return this.
	 * It makes no sense wrapping a BindFunction into another BindFunction,
	 * because the inner BindFunction &quot;wins&quot;.
	 */
	@Override
	public BindFunction<E> bind(final Join join)
	{
		return this;
	}
}
