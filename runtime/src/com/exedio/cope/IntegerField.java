/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.util.Set;

import com.exedio.cope.search.SumAggregate;

/**
 * Represents a field within a {@link Type type},
 * that enables instances of that type to store a integer.
 *
 * @author Ralf Wiebicke
 */
public final class IntegerField extends FunctionField<Integer> implements IntegerFunction
{
	final Integer defaultNextStart;
	private boolean defaultNextValueComputed = false;
	private int defaultNextValue = Integer.MIN_VALUE;
	private final Object defaultNextLock;
	private final int minimum;
	private final int maximum;

	private IntegerField(
			final boolean isfinal, final boolean optional, final boolean unique,
			final Integer defaultConstant, final Integer defaultNextStart,
			final int minimum, final int maximum)
	{
		super(isfinal, optional, unique, Integer.class, defaultConstant);
		this.defaultNextStart = defaultNextStart;
		this.defaultNextLock = defaultNextStart!=null ? new Object() : null;
		this.minimum = minimum;
		this.maximum = maximum;

		if(defaultConstant!=null && defaultNextStart!=null)
			throw new IllegalStateException("cannot use defaultConstant and defaultNext together");
		if(minimum>=maximum)
			throw new IllegalArgumentException("maximum must be greater than mimimum, but was " + maximum + " and " + minimum + '.');

		checkDefaultValue();
		if(defaultNextStart!=null)
		{
			try
			{
				checkValue(defaultNextStart, null);
			}
			catch(ConstraintViolationException e)
			{
				// BEWARE
				// Must not make exception e available to public,
				// since it contains a reference to this function field,
				// which has not been constructed successfully.
				throw new IllegalArgumentException(
						"The start value for defaultToNext of the field " +
						"does not comply to one of it's own constraints, " +
						"caused a " + e.getClass().getSimpleName() +
						": " + e.getMessageWithoutFeature() +
						" Start value was '" + defaultNextStart + "'.");
			}
		}
	}
	
	/**
	 * Creates a new mandatory <tt>IntegerField</tt>.
	 */
	public IntegerField()
	{
		this(false, false, false, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	@Override
	public IntegerField copy()
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, defaultNextStart, minimum, maximum);
	}
	
	@Override
	public IntegerField toFinal()
	{
		return new IntegerField(true, optional, unique, defaultConstant, defaultNextStart, minimum, maximum);
	}
	
	@Override
	public IntegerField optional()
	{
		return new IntegerField(isfinal, true, unique, defaultConstant, defaultNextStart, minimum, maximum);
	}

	@Override
	public IntegerField unique()
	{
		return new IntegerField(isfinal, optional, true, defaultConstant, defaultNextStart, minimum, maximum);
	}
	
	public IntegerField defaultTo(final Integer defaultConstant)
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, defaultNextStart, minimum, maximum);
	}
	
	public IntegerField defaultToNext(final int start)
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, start, minimum, maximum);
	}
	
	public IntegerField range(final int minimum, final int maximum)
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, defaultNextStart, minimum, maximum);
	}
	
	public IntegerField min(final int minimum)
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, defaultNextStart, minimum, Integer.MAX_VALUE);
	}
	
	public IntegerField max(final int maximum)
	{
		return new IntegerField(isfinal, optional, unique, defaultConstant, defaultNextStart, Integer.MIN_VALUE, maximum);
	}
	
	public boolean isDefaultNext()
	{
		return defaultNextStart!=null;
	}
	
	public Integer getDefaultNextStart()
	{
		return defaultNextStart;
	}
	
	public int getMinimum()
	{
		return minimum;
	}
	
	public int getMaximum()
	{
		return maximum;
	}
	
	/**
	 * Returns true, if a value for the field should be specified
	 * on the creation of an item.
	 * This implementation returns
	 * <tt>({@link #isFinal() isFinal()} || {@link #isMandatory() isMandatory()}) && !{@link #isDefaultNext()}</tt>.
	 */
	@Override
	public boolean isInitial()
	{
		return (defaultNextStart==null) && super.isInitial();
	}
	
	@Override
	public Set<Class<? extends Throwable>> getSetterExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getSetterExceptions();
		if(minimum!=Integer.MIN_VALUE || maximum!=Integer.MAX_VALUE)
			result.add(RangeViolationException.class);
		return result;
	}
	
	@Override
	public Class getWrapperSetterType()
	{
		return optional ? Integer.class : int.class;
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		defaultNextValueComputed = false;
		return new IntegerColumn(table, this, name, optional, minimum, maximum, false);
	}
	
	@Override
	Integer get(final Row row)
	{
		return (Integer)row.get(getColumn());
	}
	
	@Override
	void set(final Row row, final Integer surface)
	{
		row.put(getColumn(), surface);
	}
	
	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	public int getMandatory(final Item item)
	{
		if(optional)
			throw new IllegalArgumentException("field " + toString() + " is not mandatory");
		
		return get(item).intValue();
	}
	
	public void set(final Item item, final int value)
		throws
			UniqueViolationException,
			FinalViolationException,
			RangeViolationException
	{
		set(item, Integer.valueOf(value));
	}

	@Override
	void checkNotNullValue(final Integer value, final Item exceptionItem) throws RangeViolationException
	{
		final int valuePrimitive = value.intValue();
		if(valuePrimitive<minimum)
			throw new RangeViolationException(this, exceptionItem, value, true, minimum);
		if(valuePrimitive>maximum)
			throw new RangeViolationException(this, exceptionItem, value, false, maximum);
	}
	
	int nextDefaultNext()
	{
		synchronized(defaultNextLock)
		{
			final int result;
			if(defaultNextValueComputed)
			{
				result = defaultNextValue;
			}
			else
			{
				final Integer current = new Query<Integer>(max()).searchSingleton();
				result = current!=null ? (current.intValue() + 1) : defaultNextStart.intValue();
				defaultNextValueComputed = true;
			}
			
			defaultNextValue = result + 1;
			return result;
		}
	}
	
	public static final void flushDefaultNextCache(final Model model)
	{
		for(final Type<?> t : model.getTypes())
			for(final Field f : t.getFields())
				if(f instanceof IntegerField)
				{
					final IntegerField fi = (IntegerField)f;
					
					if(fi.defaultNextLock==null)
						continue;
					
					synchronized(fi.defaultNextLock)
					{
						fi.defaultNextValueComputed = false;
					}
				}
	}
	
	@Override
	public Condition equal(final Integer value)
	{
		if(value!=null)
		{
			final int valuePrimitive = value.intValue();
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.FALSE;
			else
				return super.equal(value);
		}
		else
			return super.equal(value);
	}
	
	@Override
	public Condition notEqual(final Integer value)
	{
		if(value!=null)
		{
			final int valuePrimitive = value.intValue();
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.TRUE;
			else
				return super.notEqual(value);
		}
		else
			return super.notEqual(value);
	}
	// TODO the same for less, lessEqual, greater, greaterEqual
	
	// convenience methods for conditions and views ---------------------------------

	@Override
	public BindIntegerFunction bind(final Join join)
	{
		return new BindIntegerFunction(this, join);
	}
	
	public PlusView plus(final IntegerFunction other)
	{
		return new PlusView(new IntegerFunction[]{this, other});
	}
	
	public SumAggregate<Integer> sum()
	{
		return new SumAggregate<Integer>(this);
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated use {@link #toFinal()}, {@link #unique()} and {@link #optional()} instead.
	 */
	@Deprecated
	public IntegerField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null, null, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * @deprecated renamed to {@link #plus(IntegerFunction)}.
	 */
	@Deprecated
	public PlusView sum(final IntegerFunction other)
	{
		return plus(other);
	}
}
