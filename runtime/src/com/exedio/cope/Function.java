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

public interface Function<E> extends Selectable<E>
{
	E get(Item item);

	Class<E> getValueClass();
	
	/**
	 * For internal use within COPE only.
	 */
	void append(Statement bf, Join join);
	
	/**
	 * For internal use within COPE only.
	 */
	void appendParameter(Statement bf, E value);
	
	// convenience methods for conditions and views ---------------------------------
	
	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is equal to the given parameter.
	 */
	EqualCondition<E> equal(E value);
	
	EqualCondition<E> equal(Join join, E value);
	CompositeCondition in(Collection<E> value);
	NotEqualCondition<E> notEqual(E value);
	EqualFunctionCondition<E> equal(Function<E> right);

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

	ExtremumAggregate<E> min();
	ExtremumAggregate<E> max();
	BindFunction<E> bind(Join join);
}
