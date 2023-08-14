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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.CompareFunctionCondition.Operator;
import java.util.Collection;

public interface Function<E> extends Selectable<E>
{
	/**
	 * Must throw the same {@link UnsupportedGetException} under the same circumstances as
	 * {@link #get(Item)} and {@link #get(FieldValues)}.
	 */
	void requireSupportForGet() throws UnsupportedGetException;

	E get(Item item) throws UnsupportedGetException;

	default E get(final FieldValues item) throws UnsupportedGetException
	{
		// TODO may be we should throw UnsupportedGetException if item.getBackingItem()==null
		return get(requireNonNull(item.getBackingItem(), "backingItem"));
	}

	// convenience methods for conditions and views ---------------------------------

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is null.
	 */
	@SuppressWarnings("deprecation")
	default IsNullCondition<E> isNull()
	{
		return new IsNullCondition<>(this, false);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not null.
	 */
	@SuppressWarnings("deprecation")
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
		return bind(join).equal(value);
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
		return new CompareCondition<>(Operator.Less, this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not greater than the given parameter.
	 */
	default CompareCondition<E> lessOrEqual(final E value)
	{
		return new CompareCondition<>(Operator.LessEqual, this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is greater than the given parameter.
	 */
	default CompareCondition<E> greater(final E value)
	{
		return new CompareCondition<>(Operator.Greater, this, value);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not less than the given parameter.
	 */
	default CompareCondition<E> greaterOrEqual(final E value)
	{
		return new CompareCondition<>(Operator.GreaterEqual, this, value);
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
	 * is equal to the value of the {@code right} function.
	 */
	default CompareFunctionCondition<E> equal(final Function<? extends E> right)
	{
		return CompareFunctionCondition.create(Operator.Equal, this, right);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not equal to the value of the {@code right} function.
	 */
	default CompareFunctionCondition<E> notEqual(final Function<? extends E> right)
	{
		return CompareFunctionCondition.create(Operator.NotEqual, this, right);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is less than the value of the {@code right} function.
	 */
	default CompareFunctionCondition<E> less(final Function<? extends E> right)
	{
		return CompareFunctionCondition.create(Operator.Less, this, right);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not greater than the value of the {@code right} function.
	 */
	default CompareFunctionCondition<E> lessOrEqual(final Function<? extends E> right)
	{
		return CompareFunctionCondition.create(Operator.LessEqual, this, right);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is greater than the value of the {@code right} function.
	 */
	default CompareFunctionCondition<E> greater(final Function<? extends E> right)
	{
		return CompareFunctionCondition.create(Operator.Greater, this, right);
	}

	/**
	 * Returns a condition, that is true for all items,
	 * if and only if the value of this function for that item
	 * is not less than the value of the {@code right} function.
	 */
	default CompareFunctionCondition<E> greaterOrEqual(final Function<? extends E> right)
	{
		return CompareFunctionCondition.create(Operator.GreaterEqual, this, right);
	}

	@SuppressWarnings("deprecation")
	default ExtremumAggregate<E> min()
	{
		return new ExtremumAggregate<>(this, true);
	}

	@SuppressWarnings("deprecation")
	default ExtremumAggregate<E> max()
	{
		return new ExtremumAggregate<>(this, false);
	}

	/**
	 * <b>BEWARE:</b>
	 * This aggregate works on MySQL 5.7 or later only.
	 * See <a href="https://dev.mysql.com/doc/refman/5.7/en/miscellaneous-functions.html#function_any-value">ANY_VALUE</a>.
	 */
	default Aggregate<E,E> any()
	{
		return new AnyAggregate<>(this);
	}

	default Aggregate<E,E> distinct()
	{
		return new Distinct<>(this);
	}

	default Count count()
	{
		return new Count(this);
	}

	default Function<E> bind(final Join join)
	{
		return new BindFunction<>(this, join);
	}

	long serialVersionUID = 8575436913882709690L;
}
