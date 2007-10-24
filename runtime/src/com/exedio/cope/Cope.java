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

import java.io.Serializable;
import java.util.List;

import com.exedio.cope.function.PlusView;

/**
 * Utility class for creating conditions.
 * May be subclassed to access methods without class qualifier.
 * <p>
 * Must be {@link Serializable} to avoid needing a public empty
 * constructor for deserialization, needed on the first
 * non-Serializable super class of {@link Item}.
 * See http://www.jguru.com/faq/view.jsp?EID=251942
 *
 * @author Ralf Wiebicke
 */
public abstract class Cope implements Serializable
{
	Cope()
	{/* do not allow class to be subclassed by public */}

	public static final <E> Condition equal(final Function<E> function, final E value)
	{
		return value!=null ? new CompareCondition<E>(CompareCondition.Operator.Equal, function, value) : new IsNullCondition<E>(function, false);
	}
	
	public static final <E> Condition notEqual(final Function<E> function, final E value)
	{
		return value!=null ? new CompareCondition<E>(CompareCondition.Operator.NotEqual, function, value) : new IsNullCondition<E>(function, true);
	}
	
	public static final Condition and(final List<? extends Condition> conditions)
	{
		return composite(CompositeCondition.Operator.AND, conditions);
	}
	
	public static final Condition and(final Condition... conditions)
	{
		return composite(CompositeCondition.Operator.AND, conditions);
	}
	
	public static final Condition or(final List<? extends Condition> conditions)
	{
		return composite(CompositeCondition.Operator.OR, conditions);
	}
	
	public static final Condition or(final Condition... conditions)
	{
		return composite(CompositeCondition.Operator.OR, conditions);
	}
	
	private static final Condition composite(final CompositeCondition.Operator operator, final List<? extends Condition> conditions)
	{
		if(conditions==null)
			throw new NullPointerException("conditions must not be null");

		switch(conditions.size())
		{
			case 0:
				return operator.identity;
			case 1:
				return conditions.get(0);
			default:
				return new CompositeCondition(operator, conditions);
		}
	}
	
	private static final Condition composite(final CompositeCondition.Operator operator, final Condition[] conditions)
	{
		if(conditions==null)
			throw new NullPointerException("conditions must not be null");
		
		int filtered = 0;
		
		for(int i = 0; i<conditions.length; i++)
		{
			final Condition c = conditions[i];
			if(c==null)
				throw new NullPointerException("condition " + i + " must not be null");

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
	
	public static final PlusView plus(final IntegerFunction addend1, final IntegerFunction addend2)
	{
		return new PlusView(new IntegerFunction[]{addend1, addend2});
	}

	public static final PlusView plus(final IntegerFunction addend1, final IntegerFunction addend2, final IntegerFunction addend3)
	{
		return new PlusView(new IntegerFunction[]{addend1, addend2, addend3});
	}

	/**
	 * @deprecated renamed to {@link #plus(IntegerFunction, IntegerFunction)}.
	 */
	@Deprecated
	public static final PlusView sum(final IntegerFunction addend1, final IntegerFunction addend2)
	{
		return plus(addend1, addend2);
	}

	/**
	 * @deprecated renamed to {@link #plus(IntegerFunction, IntegerFunction, IntegerFunction)}.
	 */
	@Deprecated
	public static final PlusView sum(final IntegerFunction addend1, final IntegerFunction addend2, final IntegerFunction addend3)
	{
		return plus(addend1, addend2, addend3);
	}

	/**
	 * Does the same as {@link Class#cast(Object)},
	 * but throws a ClassCastException
	 * with a more verbose message.
	 */
	public static final <X> X verboseCast(final Class<X> clazz, final Object o)
	{
		// NOTE:
		// This code is redundant to the following call to Class#cast(Object),
		// but creates an exception with a much more verbose message.
		if(o!=null && !clazz.isInstance(o))
			throw new ClassCastException("expected a " + clazz.getName() + ", but was a " + o.getClass().getName());
		
		return clazz.cast(o);
	}
	
	public static final <X> SetValue<X> mapAndCast(final Field<X> a, final Object o)
	{
		return new SetValue<X>(a, Cope.verboseCast(a.getValueClass(), o));
	}
	
	/**
	 * {@link #verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link Field#set(Item, Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	public static final <X> void setAndCast(final Field<X> field, final Item item, final Object value)
	{
		field.set(item, verboseCast(field.getValueClass(), value));
	}

	/**
	 * {@link #verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link Function#equal(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	public static final <X> Condition equalAndCast(final Function<X> function, final Object value)
	{
		return function.equal(verboseCast(function.getValueClass(), value));
	}
	
	/**
	 * {@link #verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link Function#notEqual(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	public static final <X> Condition notEqualAndCast(final Function<X> function, final Object value)
	{
		return function.notEqual(verboseCast(function.getValueClass(), value));
	}
	
	/**
	 * {@link #verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link Function#less(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	public static final <X> CompareCondition<X> lessAndCast(final Function<X> function, final Object value)
	{
		return function.less(verboseCast(function.getValueClass(), value));
	}

	/**
	 * {@link #verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link Function#lessOrEqual(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	public static final <X> CompareCondition<X> lessOrEqualAndCast(final Function<X> function, final Object value)
	{
		return function.lessOrEqual(verboseCast(function.getValueClass(), value));
	}
	
	/**
	 * {@link #verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link Function#greater(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	public static final <X> CompareCondition<X> greaterAndCast(final Function<X> function, final Object value)
	{
		return function.greater(verboseCast(function.getValueClass(), value));
	}
	
	/**
	 * {@link #verboseCast(Class, Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link Function#greaterOrEqual(Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	public static final <X> CompareCondition<X> greaterOrEqualAndCast(final Function<X> function, final Object value)
	{
		return function.greaterOrEqual(verboseCast(function.getValueClass(), value));
	}
	
	@SuppressWarnings("deprecation") // OK: Selectable.check is for internal use within COPE only
	static void check(final Selectable select, final TC tc, final Join join)
	{
		select.check(tc, join);
	}
	
	private static final char DIVIDER = '#';
	
	public static Model getModel(final String name)
	{
		final int pos = name.indexOf(DIVIDER);
		if(pos<=0)
			throw new IllegalArgumentException("does not contain '" + DIVIDER + "', but was " + name);
		final String className = name.substring(0, pos);
		final String fieldName = name.substring(pos+1);

		final Class clazz;
		try
		{
			clazz = Class.forName(className);
		}
		catch(ClassNotFoundException e)
		{
			throw new IllegalArgumentException("class " + className + " does not exist.", e);
		}

		final java.lang.reflect.Field field;
		try
		{
			field = clazz.getField(fieldName);
		}
		catch(NoSuchFieldException e)
		{
			throw new IllegalArgumentException("field " + fieldName + " in " + clazz.toString() + " does not exist or is not public.", e);
		}
		
		final Object result;
		try
		{
			result = field.get(null);
		}
		catch(IllegalAccessException e)
		{
			throw new IllegalArgumentException("accessing " + field.toString(), e);
		}
		
		if(result==null)
			throw new IllegalArgumentException("field " + clazz.getName() + '#' + field.getName() + " is null.");
		if(!(result instanceof Model))
			throw new IllegalArgumentException("field " + clazz.getName() + '#' + field.getName() + " is not a model, but a " + result.getClass().getName() + '.');
		
		return (Model)result;
	}
	
	public static final void main(final String[] args)
	{
		if(args.length!=2)
			throw new RuntimeException("must have two arguments, model and action");
		
		final Model model = getModel(args[0]);
		model.connect(new ConnectProperties(ConnectProperties.getSystemPropertyContext()));
		final String action = args[1];
		if("create".equals(action))
			model.createDatabase();
		else if("drop".equals(action))
			model.dropDatabase();
		else if("tearDown".equals(action))
			model.tearDownDatabase();
		else
			throw new RuntimeException("illegal action, must be 'create', 'drop', or 'tearDown'");
		model.disconnect();
	}
}
