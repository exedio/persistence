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

import com.exedio.cope.CompareFunctionCondition.Operator;
import java.util.Collection;

public interface Function<E> extends Selectable<E>
{
	E get(Item item);

	// convenience methods for conditions and views ---------------------------------

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is null.
	 */
	default IsNullCondition<E> isNull()
	{
		return new IsNullCondition<>(this, false);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not null.
	 */
	default IsNullCondition<E> isNotNull()
	{
		return new IsNullCondition<>(this, true);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is equal to the given parameter.
	 */
	default Condition equal(final E value)
	{
		return Cope.equal(this, value);
	}

	default Condition equal(final Join join, final E value)
	{
		return this.bind(join).equal(value);
	}

	@SuppressWarnings("unchecked") // Possible heap pollution from parameterized vararg type E
	default Condition in(final E... values)
	{
		return CompositeCondition.in(this, values);
	}

	default Condition in(final Collection<? extends E> values)
	{
		return CompositeCondition.in(this, values);
	}

	default Condition notEqual(final E value)
	{
		return Cope.notEqual(this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is less than the given parameter.
	 */
	default CompareCondition<E> less(final E value)
	{
		return new CompareCondition<>(Operator.Less, (Selectable<E>)this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not greater than the given parameter.
	 */
	default CompareCondition<E> lessOrEqual(final E value)
	{
		return new CompareCondition<>(Operator.LessEqual, (Selectable<E>)this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is greater than the given parameter.
	 */
	default CompareCondition<E> greater(final E value)
	{
		return new CompareCondition<>(Operator.Greater, (Selectable<E>)this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not less than the given parameter.
	 */
	default CompareCondition<E> greaterOrEqual(final E value)
	{
		return new CompareCondition<>(Operator.GreaterEqual, (Selectable<E>)this, value);
	}

	/**
	 * Returns a condition, that is equivalent to
	 * <code>{@link #greaterOrEqual(Object) greaterOrEqual}(lowerBound).{@link Condition#and(Condition) and}({@link #lessOrEqual(Object) lessOrEqual}(upperBound))</code>.
	 */
	default Condition between(final E lowerBound, final E upperBound)
	{
		return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is equal to the value of the <tt>right</tt> function.
	 */
	default CompareFunctionCondition<E> equal(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<>(Operator.Equal, this, right);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not equal to the value of the <tt>right</tt> function.
	 */
	default CompareFunctionCondition<E> notEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<>(Operator.NotEqual, this, right);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is less than the value of the <tt>right</tt> function.
	 */
	default CompareFunctionCondition<E> less(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<>(Operator.Less, this, right);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not greater than the value of the <tt>right</tt> function.
	 */
	default CompareFunctionCondition<E> lessOrEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<>(Operator.LessEqual, this, right);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is greater than the value of the <tt>right</tt> function.
	 */
	default CompareFunctionCondition<E> greater(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<>(Operator.Greater, this, right);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not less than the value of the <tt>right</tt> function.
	 */
	default CompareFunctionCondition<E> greaterOrEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<>(Operator.GreaterEqual, this, right);
	}

	default ExtremumAggregate<E> min()
	{
		return new ExtremumAggregate<>(this, true);
	}

	default ExtremumAggregate<E> max()
	{
		return new ExtremumAggregate<>(this, false);
	}

	default BindFunction<E> bind(final Join join)
	{
		return new BindFunction<>(this, join);
	}

	static final long serialVersionUID = 8575436913882709690L;
}
