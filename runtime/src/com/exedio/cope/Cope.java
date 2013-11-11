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
import com.exedio.cope.misc.ModelByString;
import com.exedio.cope.util.Cast;
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
		return value!=null ? new CompareCondition<E>(Operator.Equal, function, value) : new IsNullCondition<E>(function, false);
	}

	public static <E> Condition notEqual(final Function<E> function, final E value)
	{
		return value!=null ? new CompareCondition<E>(Operator.NotEqual, function, value) : new IsNullCondition<E>(function, true);
	}

	public static Condition and(final List<? extends Condition> conditions)
	{
		return composite(CompositeCondition.Operator.AND, conditions);
	}

	public static Condition and(final Condition... conditions)
	{
		return composite(CompositeCondition.Operator.AND, conditions);
	}

	public static Condition or(final List<? extends Condition> conditions)
	{
		return composite(CompositeCondition.Operator.OR, conditions);
	}

	public static Condition or(final Condition... conditions)
	{
		return composite(CompositeCondition.Operator.OR, conditions);
	}

	private static Condition composite(final CompositeCondition.Operator operator, final List<? extends Condition> conditions)
	{
		if(conditions==null)
			throw new NullPointerException("conditions");

		return composite(operator, conditions.toArray(new Condition[conditions.size()]));
	}

	private static Condition composite(final CompositeCondition.Operator operator, final Condition[] conditions)
	{
		if(conditions==null)
			throw new NullPointerException("conditions");

		int filtered = 0;

		for(int i = 0; i<conditions.length; i++)
		{
			final Condition c = conditions[i];
			if(c==null)
				throw new NullPointerException("conditions" + '[' + i + ']');

			if(c instanceof Condition.Literal)
			{
				if(operator.absorber==c)
					return c;
				else
					filtered++;
			}
		}

		final Condition[] filteredConditions;
		if(filtered==0)
		{
			filteredConditions = conditions;
		}
		else
		{
			filteredConditions = new Condition[conditions.length-filtered];

			int j = 0;
			for(final Condition c : conditions)
				if(operator.identity!=c)
					filteredConditions[j++] = c;

			assert j==filteredConditions.length;
		}

		switch(filteredConditions.length)
		{
			case 0:
				return operator.identity;
			case 1:
				return filteredConditions[0];
			default:
				return new CompositeCondition(operator, filteredConditions);
		}
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

	public static <X> SetValue<X> mapAndCast(final Field<X> a, final Object o)
	{
		return SetValue.map(a, Cast.verboseCast(a.getValueClass(), o));
	}

	/**
	 * {@link Cast#verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>X</tt> before calling
	 * {@link Field#set(Item, Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>X</tt>
	 */
	public static <X> void setAndCast(final Field<X> field, final Item item, final Object value)
	{
		field.set(item, Cast.verboseCast(field.getValueClass(), value));
	}

	/**
	 * {@link Cast#verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>X</tt> before calling
	 * {@link Function#equal(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>X</tt>
	 */
	public static <X> Condition equalAndCast(final Function<X> function, final Object value)
	{
		return function.equal(Cast.verboseCast(function.getValueClass(), value));
	}

	/**
	 * {@link Cast#verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>X</tt> before calling
	 * {@link Function#notEqual(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>X</tt>
	 */
	public static <X> Condition notEqualAndCast(final Function<X> function, final Object value)
	{
		return function.notEqual(Cast.verboseCast(function.getValueClass(), value));
	}

	/**
	 * {@link Cast#verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>X</tt> before calling
	 * {@link Function#less(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>X</tt>
	 */
	public static <X> CompareCondition<X> lessAndCast(final Function<X> function, final Object value)
	{
		return function.less(Cast.verboseCast(function.getValueClass(), value));
	}

	/**
	 * {@link Cast#verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>X</tt> before calling
	 * {@link Function#lessOrEqual(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>X</tt>
	 */
	public static <X> CompareCondition<X> lessOrEqualAndCast(final Function<X> function, final Object value)
	{
		return function.lessOrEqual(Cast.verboseCast(function.getValueClass(), value));
	}

	/**
	 * {@link Cast#verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>X</tt> before calling
	 * {@link Function#greater(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>X</tt>
	 */
	public static <X> CompareCondition<X> greaterAndCast(final Function<X> function, final Object value)
	{
		return function.greater(Cast.verboseCast(function.getValueClass(), value));
	}

	/**
	 * {@link Cast#verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>X</tt> before calling
	 * {@link Function#greaterOrEqual(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>X</tt>
	 */
	public static <X> CompareCondition<X> greaterOrEqualAndCast(final Function<X> function, final Object value)
	{
		return function.greaterOrEqual(Cast.verboseCast(function.getValueClass(), value));
	}

	/**
	 * {@link Cast#verboseCast(Class, Object) Casts}
	 * <tt>values</tt> to <tt>X</tt> before calling
	 * {@link Function#between(Object, Object)}
	 * @throws ClassCastException if one of the <tt>values</tt> is not assignable to <tt>X</tt>
	 */
	public static <X> Condition betweenAndCast(final Function<X> function, final Object lowerBound, final Object upperBound)
	{
		final Class<X> valueClass = function.getValueClass();
		return function.between(
				  Cast.verboseCast(valueClass, lowerBound),
				  Cast.verboseCast(valueClass, upperBound));
	}


	@SuppressWarnings("deprecation") // OK: Selectable.check is for internal use within COPE only
	static void check(final Selectable<?> select, final TC tc, final Join join)
	{
		select.check(tc, join);
	}

	public static void main(final String[] args)
	{
		if(args.length!=2)
			throw new RuntimeException("must have two arguments, model and action");

		final Model model = ModelByString.get(args[0]);
		model.connect(new ConnectProperties(ConnectProperties.SYSTEM_PROPERTY_SOURCE));
		final String action = args[1];
		if("create".equals(action))
			model.createSchema();
		else if("drop".equals(action))
			model.dropSchema();
		else if("tearDown".equals(action))
			model.tearDownSchema();
		else
			throw new RuntimeException("illegal action, must be 'create', 'drop', or 'tearDown'");
		model.disconnect();
	}

	private Cope()
	{
		// prevent instantiation
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated renamed to {@link #plus(NumberFunction, NumberFunction)}.
	 */
	@Deprecated
	public static <E extends Number> PlusView<E> sum(final NumberFunction<E> addend1, final NumberFunction<E> addend2)
	{
		return plus(addend1, addend2);
	}

	/**
	 * @deprecated renamed to {@link #plus(NumberFunction, NumberFunction, NumberFunction)}.
	 */
	@Deprecated
	public static <E extends Number> PlusView<E> sum(final NumberFunction<E> addend1, final NumberFunction<E> addend2, final NumberFunction<E> addend3)
	{
		return plus(addend1, addend2, addend3);
	}

	/**
	 * @deprecated Use {@link Cast#verboseCast(Class, Object)} instead.
	 */
	@Deprecated
	public static <X> X verboseCast(final Class<X> clazz, final Object o)
	{
		return Cast.verboseCast(clazz, o);
	}

	/**
	 * @deprecated Use {@link ModelByString#get(String)} instead.
	 */
	@Deprecated
	public static Model getModel(final String name)
	{
		return ModelByString.get(name);
	}
}
