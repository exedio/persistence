/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import java.util.Map;

import com.exedio.cope.search.ExtremumAggregate;

public interface Function<E> extends Selectable<E>
{
	E get(Item item);
	E get(Item item, Map<FunctionField, Object> delta);

	Class<E> getValueClass();
	
	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	void appendParameter(Statement bf, E value);
	
	// convenience methods for conditions and views ---------------------------------
	
	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is null.
	 */
	IsNullCondition<E> isNull();
	
	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not null.
	 */
	IsNullCondition<E> isNotNull();
	
	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is equal to the given parameter.
	 */
	Condition equal(E value);
	
	Condition equal(Join join, E value);
	CompositeCondition in(Collection<E> value);
	Condition notEqual(E value);

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is less than the given parameter.
	 */
	CompareCondition<E> less(E value);

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not greater than the given parameter.
	 */
	CompareCondition<E> lessOrEqual(E value);

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is greater than the given parameter.
	 */
	CompareCondition<E> greater(E value);

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not less than the given parameter.
	 */
	CompareCondition<E> greaterOrEqual(E value);

	/**
	 * Returns a condition, that is equivalent to
	 * <code>{@link #greaterOrEqual(Object) greaterOrEqual}(lowerBound).{@link Condition#and(Condition) and}({@link #lessOrEqual(Object) lessOrEqual}(upperBound))</code>. 
	 */
	CompositeCondition between(E lowerBound, E upperBound);

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is equal to the value of the <tt>right</tt> function.
	 */
	CompareFunctionCondition<E> equal(Function<E> right);

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not equal to the value of the <tt>right</tt> function.
	 */
	CompareFunctionCondition<E> notEqual(Function<E> right);

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is less than the value of the <tt>right</tt> function.
	 */
	CompareFunctionCondition<E> less(Function<E> right);

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not greater than the value of the <tt>right</tt> function.
	 */
	CompareFunctionCondition<E> lessOrEqual(Function<E> right);

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is greater than the value of the <tt>right</tt> function.
	 */
	CompareFunctionCondition<E> greater(Function<E> right);

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not less than the value of the <tt>right</tt> function.
	 */
	CompareFunctionCondition<E> greaterOrEqual(Function<E> right);

	ExtremumAggregate<E> min();
	ExtremumAggregate<E> max();
	BindFunction<E> bind(Join join);
}
