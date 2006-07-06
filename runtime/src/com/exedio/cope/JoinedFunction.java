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


public class JoinedFunction<E> implements Function<E> // TODO SOON rename to BindFunction
{
	final Function<E> function;
	final Join join;
	
	/**
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see Function#bind(Join)
	 */
	public JoinedFunction(final Function<E> function, final Join join)
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
		if(!(other instanceof JoinedFunction))
			return false;
		
		final JoinedFunction o = (JoinedFunction)other;
		
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

	public final EqualCondition<E> equal(final E value)
	{
		return new EqualCondition<E>(this, value);
	}
	
	public final EqualCondition<E> equal(final Join join, final E value)
	{
		return this.bind(join).equal(value);
	}
	
	public final CompositeCondition in(final Collection<E> values)
	{
		return CompositeCondition.in(this, values);
	}
	
	public final NotEqualCondition<E> notEqual(final E value)
	{
		return new NotEqualCondition<E>(this, value);
	}
	
	public final EqualFunctionCondition<E> equal(final Function<E> right)
	{
		return new EqualFunctionCondition<E>(this, right);
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
	 * It makes no sense wrapping a JoinedFunction into another JoinedFunction,
	 * because the inner JoinedFunction &quot;wins&quot;.
	 */
	public JoinedFunction<E> bind(final Join join)
	{
		return this;
	}
}
