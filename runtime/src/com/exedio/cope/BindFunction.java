/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.search.ExtremumAggregate;


public class BindFunction<E> implements Function<E>
{
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
	
	public final E get(final Item item)
	{
		return function.get(item);
	}

	public final Class<E> getValueClass()
	{
		return function.getValueClass();
	}

	public final void append(final Statement bf, final Join join)
	{
		function.append(bf, this.join);
	}
	
	public final int getTypeForDefiningColumn()
	{
		return function.getTypeForDefiningColumn();
	}
	
	public final void appendParameter(final Statement bf, final E value)
	{
		bf.appendParameter(function, value);
	}
	
	public final Type<? extends Item> getType()
	{
		return function.getType();
	}
	
	@Override
	public final boolean equals(final Object other)
	{
		if(!(other instanceof BindFunction))
			return false;
		
		final BindFunction o = (BindFunction)other;
		
		return function.equals(o.function) && join.equals(o.join);
	}
	
	@Override
	public final int hashCode()
	{
		return function.hashCode() ^ join.hashCode();
	}

	@Override
	public final String toString()
	{
		return function.toString();
	}
	
	// convenience methods for conditions and views ---------------------------------

	public final IsNullCondition<E> isNull()
	{
		return new IsNullCondition<E>(this, false);
	}
	
	public final IsNullCondition<E> isNotNull()
	{
		return new IsNullCondition<E>(this, true);
	}
	
	public final Condition equal(final E value)
	{
		return Cope.equal(this, value);
	}
	
	public final Condition equal(final Join join, final E value)
	{
		return this.bind(join).equal(value);
	}
	
	public final CompositeCondition in(final Collection<E> values)
	{
		return CompositeCondition.in(this, values);
	}
	
	public final Condition notEqual(final E value)
	{
		return Cope.notEqual(this, value);
	}
	
	public final CompareCondition<E> less(final E value)
	{
		return new CompareCondition<E>(CompareCondition.Operator.Less, this, value);
	}
	
	public final CompareCondition<E> lessOrEqual(final E value)
	{
		return new CompareCondition<E>(CompareCondition.Operator.LessEqual, this, value);
	}
	
	public final CompareCondition<E> greater(final E value)
	{
		return new CompareCondition<E>(CompareCondition.Operator.Greater, this, value);
	}
	
	public final CompareCondition<E> greaterOrEqual(final E value)
	{
		return new CompareCondition<E>(CompareCondition.Operator.GreaterEqual, this, value);
	}
	
	public final CompareFunctionCondition<E> equal(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.Equal, this, right);
	}
	
	public final CompareFunctionCondition<E> notEqual(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.NotEqual, this, right);
	}
	
	public final CompareFunctionCondition<E> less(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.Less, this, right);
	}
	
	public final CompareFunctionCondition<E> lessOrEqual(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.LessEqual, this, right);
	}
	
	public final CompareFunctionCondition<E> greater(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.Greater, this, right);
	}
	
	public final CompareFunctionCondition<E> greaterOrEqual(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareFunctionCondition.Operator.GreaterEqual, this, right);
	}

	public final ExtremumAggregate<E> min()
	{
		return new ExtremumAggregate<E>(this, true);
	}
	
	public final ExtremumAggregate<E> max()
	{
		return new ExtremumAggregate<E>(this, false);
	}

	/**
	 * Return this.
	 * It makes no sense wrapping a BindFunction into another BindFunction,
	 * because the inner BindFunction &quot;wins&quot;.
	 */
	public BindFunction<E> bind(final Join join)
	{
		return this;
	}
}
