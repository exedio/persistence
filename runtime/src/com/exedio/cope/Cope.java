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
import java.util.List;

/**
 * Utility class for creating conditions.
 *
 * @author Ralf Wiebicke
 */
public final class Cope
{
	public static <E> Condition equal(final Function<E> function, final E value)
	{
		return value!=null ? new CompareCondition<>(Operator.Equal, function, value) : function.isNull();
	}

	public static <E> Condition notEqual(final Function<E> function, final E value)
	{
		return value!=null ? new CompareCondition<>(Operator.NotEqual, function, value) : function.isNotNull();
	}

	public static Condition and(final List<? extends Condition> conditions)
	{
		return CompositeCondition.composite(CompositeCondition.Operator.AND, conditions);
	}

	public static Condition and(final Condition... conditions)
	{
		return CompositeCondition.composite(CompositeCondition.Operator.AND, conditions);
	}

	public static Condition or(final List<? extends Condition> conditions)
	{
		return CompositeCondition.composite(CompositeCondition.Operator.OR, conditions);
	}

	public static Condition or(final Condition... conditions)
	{
		return CompositeCondition.composite(CompositeCondition.Operator.OR, conditions);
	}

	/**
	 * You may want to use {@link PlusView#plus(Function, Function)} instead, if you do not have {@link NumberFunction}s available.
	 */
	public static <E extends Number> PlusView<E> plus(final NumberFunction<E> addend1, final NumberFunction<E> addend2)
	{
		return PlusView.plus(addend1, addend2);
	}

	/**
	 * You may want to use {@link PlusView#plus(Function, Function, Function)} instead, if you do not have {@link NumberFunction}s available.
	 */
	public static <E extends Number> PlusView<E> plus(final NumberFunction<E> addend1, final NumberFunction<E> addend2, final NumberFunction<E> addend3)
	{
		return PlusView.plus(addend1, addend2, addend3);
	}

	/**
	 * You may want to use {@link MultiplyView#multiply(Function, Function)} instead, if you do not have {@link NumberFunction}s available.
	 */
	public static <E extends Number> MultiplyView<E> multiply(final NumberFunction<E> multiplier1, final NumberFunction<E> multiplier2)
	{
		return MultiplyView.multiply(multiplier1, multiplier2);
	}

	/**
	 * You may want to use {@link MultiplyView#multiply(Function, Function, Function)} instead, if you do not have {@link NumberFunction}s available.
	 */
	public static <E extends Number> MultiplyView<E> multiply(final NumberFunction<E> multiplier1, final NumberFunction<E> multiplier2, final NumberFunction<E> multiplier3)
	{
		return MultiplyView.multiply(multiplier1, multiplier2, multiplier3);
	}

	/**
	 * @deprecated Use {@link SetValue#mapCasted(Field, Object)} instead.
	 */
	@Deprecated
	public static <X> SetValue<X> mapAndCast(final Field<X> a, final Object o)
	{
		return SetValue.mapCasted(a, o);
	}

	/**
	 * {@link Class#cast(Object) Casts}
	 * {@code value} to {@code X} before calling
	 * {@link Field#set(Item, Object)}
	 * @throws ClassCastException if {@code value} is not assignable to {@code X}
	 */
	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	public static <X> void setAndCast(final Field<X> field, final Item item, final Object value)
	{
		field.set(item, field.getValueClass().cast(value));
	}

	/**
	 * {@link Class#cast(Object) Casts}
	 * {@code value} to {@code X} before calling
	 * {@link Function#is(Object)}
	 * @throws ClassCastException if {@code value} is not assignable to {@code X}
	 * @deprecated Use {@link Function#isCasted(Object)} instead.
	 */
	@Deprecated
	public static <X> Condition equalAndCast(final Function<X> function, final Object value)
	{
		return function.isCasted(value);
	}

	/**
	 * {@link Class#cast(Object) Casts}
	 * {@code value} to {@code X} before calling
	 * {@link Function#isNot(Object)}
	 * @throws ClassCastException if {@code value} is not assignable to {@code X}
	 * @deprecated Use {@link Function#isNotCasted(Object)} instead.
	 */
	@Deprecated
	public static <X> Condition notEqualAndCast(final Function<X> function, final Object value)
	{
		return function.isNotCasted(value);
	}

	/**
	 * {@link Class#cast(Object) Casts}
	 * {@code value} to {@code X} before calling
	 * {@link Function#less(Object)}
	 * @throws ClassCastException if {@code value} is not assignable to {@code X}
	 */
	public static <X> CompareCondition<X> lessAndCast(final Function<X> function, final Object value)
	{
		return function.less(function.getValueClass().cast(value));
	}

	/**
	 * {@link Class#cast(Object) Casts}
	 * {@code value} to {@code X} before calling
	 * {@link Function#lessOrEqual(Object)}
	 * @throws ClassCastException if {@code value} is not assignable to {@code X}
	 */
	public static <X> CompareCondition<X> lessOrEqualAndCast(final Function<X> function, final Object value)
	{
		return function.lessOrEqual(function.getValueClass().cast(value));
	}

	/**
	 * {@link Class#cast(Object) Casts}
	 * {@code value} to {@code X} before calling
	 * {@link Function#greater(Object)}
	 * @throws ClassCastException if {@code value} is not assignable to {@code X}
	 */
	public static <X> CompareCondition<X> greaterAndCast(final Function<X> function, final Object value)
	{
		return function.greater(function.getValueClass().cast(value));
	}

	/**
	 * {@link Class#cast(Object) Casts}
	 * {@code value} to {@code X} before calling
	 * {@link Function#greaterOrEqual(Object)}
	 * @throws ClassCastException if {@code value} is not assignable to {@code X}
	 */
	public static <X> CompareCondition<X> greaterOrEqualAndCast(final Function<X> function, final Object value)
	{
		return function.greaterOrEqual(function.getValueClass().cast(value));
	}

	/**
	 * {@link Class#cast(Object) Casts}
	 * {@code values} to {@code X} before calling
	 * {@link Function#between(Object, Object)}
	 * @throws ClassCastException if one of the {@code values} is not assignable to {@code X}
	 */
	public static <X> Condition betweenAndCast(final Function<X> function, final Object lowerBound, final Object upperBound)
	{
		final Class<X> valueClass = function.getValueClass();
		return function.between(
				  valueClass.cast(lowerBound),
				  valueClass.cast(upperBound));
	}


	@SuppressWarnings("deprecation") // OK: Selectable.check is for internal use within COPE only
	static void check(final Selectable<?> select, final TC tc, final Join join)
	{
		select.check(tc, join);
	}

	private Cope()
	{
		// prevent instantiation
	}
}
