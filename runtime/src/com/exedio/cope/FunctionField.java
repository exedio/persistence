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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import com.exedio.cope.search.ExtremumAggregate;

public abstract class FunctionField<E extends Object>
	extends Field<E>
	implements Function<E>
{
	final UniqueConstraint implicitUniqueConstraint;
	final E defaultConstant;
	private ArrayList<UniqueConstraint> uniqueConstraints;
	
	FunctionField(final boolean isfinal, final boolean optional, final boolean unique, final Class<E> valueClass, final E defaultConstant)
	{
		super(isfinal, optional, valueClass);
		this.defaultConstant = defaultConstant;
		this.implicitUniqueConstraint =
			unique ?
				new UniqueConstraint(this) :
				null;
	}
	
	final void checkDefaultValue()
	{
		if(defaultConstant!=null)
		{
			try
			{
				checkValue(defaultConstant, null);
			}
			catch(ConstraintViolationException e)
			{
				// BEWARE
				// Must not make exception e available to public,
				// since it contains a reference to this function field,
				// which has not been constructed successfully.
				throw new RuntimeException(
						"The default constant of the field " +
						"does not comply to one of it's own constraints, " +
						"caused a " + e.getClass().getSimpleName() +
						": " + e.getMessageWithoutFeature() +
						" Default constant was '" + defaultConstant + "'.");
			}
		}
	}
	
	public final E getDefaultConstant()
	{
		return defaultConstant;
	}
	
	/**
	 * Returns true, if a value for the field should be specified
	 * on the creation of an item.
	 * This implementation returns
	 * <tt>{@link #isFinal() isFinal()} || ({@link #isMandatory() isMandatory()} && {@link #getDefaultConstant()}==null)</tt>.
	 */
	@Override
	public boolean isInitial()
	{
		return isfinal || (!optional && defaultConstant==null);
	}
	
	@Override
	final void initialize(final Type<? extends Item> type, final String name)
	{
		super.initialize(type, name);
		
		if(implicitUniqueConstraint!=null)
			implicitUniqueConstraint.initialize(type, name + UniqueConstraint.IMPLICIT_UNIQUE_SUFFIX);
	}
	
	final void checkValueClass(final Class<? extends Object> superClass)
	{
		if(!superClass.isAssignableFrom(valueClass))
			throw new RuntimeException("is not a subclass of " + superClass.getName() + ": "+valueClass.getName());
	}
	
	public abstract FunctionField<E> copyFunctionField();

	abstract E get(final Row row);
	abstract void set(final Row row, final E surface);
	
	/**
	 * Checks field values set by
	 * {@link Item#set(FunctionField,Object)} (for <tt>initial==false</tt>)
	 * and {@link Item#Item(SetValue[])} (for <tt>initial==true</tt>)
	 * and throws the exception specified there.
	 */
	@Override
	final void checkValue(final Object value, final Item exceptionItem)
		throws
			MandatoryViolationException,
			LengthViolationException
	{
		if(value == null)
		{
			if(!optional)
				throw new MandatoryViolationException(this, exceptionItem);
		}
		else
		{
			checkNotNullValue(Cope.verboseCast(valueClass, value), exceptionItem);
		}
	}

	/**
	 * Further checks non-null field values already checked by
	 * {@link #checkValue(Object, Item)}.
	 * To be overidden by subclasses,
	 * the default implementation does nothing.
	 */
	void checkNotNullValue(final E value, final Item exceptionItem)
		throws
			LengthViolationException
	{
		// empty default implementation
	}
	
	private static final Entity getEntity(final Item item)
	{
		return getEntity(item, true);
	}

	private static final Entity getEntity(final Item item, final boolean present)
	{
		return item.type.getModel().getCurrentTransaction().getEntity(item, present);
	}

	@Override
	public final E get(final Item item)
	{
		if(!getType().isAssignableFrom(item.type))
			throw new RuntimeException("field " + toString() + " does not belong to type " + item.type.toString());
		
		return Cope.verboseCast(valueClass, getEntity(item).get(this));
	}
	
	@Override
	public final void set(final Item item, final E value)
	{
		item.set(this, value);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated
	public final void check(final TC tc, final Join join)
	{
		tc.check(this, join);
	}
	
	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated
	public final void append(final Statement bf, final Join join)
	{
		bf.append(getColumn(), join);
	}
	
	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated
	public final int getTypeForDefiningColumn()
	{
		return getColumn().typeForDefiningColumn;
	}
	
	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated
	public final void appendParameter(final Statement bf, final E value)
	{
		final Row dummyRow = new Row();
		set(dummyRow, value);
		final Column column = getColumn();
		bf.appendParameter(column, dummyRow.get(column));
	}
	
	/**
	 * Returns the unique constraint of this field,
	 * that has been created implicitly when creating this field.
	 * Does return null, if there is no such unique constraint.
	 * @see #getUniqueConstraints()
	 */
	public UniqueConstraint getImplicitUniqueConstraint()
	{
		return implicitUniqueConstraint;
	}

	/**
	 * Returns a list of unique constraints this field is part of.
	 * This includes an
	 * {@link #getImplicitUniqueConstraint() implicit unique constraint},
	 * if there is one for this field.
	 */
	public List<UniqueConstraint> getUniqueConstraints()
	{
		return uniqueConstraints!=null ? Collections.unmodifiableList(uniqueConstraints) : Collections.<UniqueConstraint>emptyList();
	}
	
	@Override
	public SortedSet<Class> getSetterExceptions()
	{
		final SortedSet<Class> result = super.getSetterExceptions();
		if(uniqueConstraints!=null)
			result.add(UniqueViolationException.class);
		return result;
	}
	
	final void registerUniqueConstraint(final UniqueConstraint constraint)
	{
		if(constraint==null)
			throw new NullPointerException();
		
		if(uniqueConstraints==null)
		{
			uniqueConstraints = new ArrayList<UniqueConstraint>();
		}
		else
		{
			if(uniqueConstraints.contains(constraint))
				throw new RuntimeException(constraint.toString());
		}
		
		uniqueConstraints.add(constraint);
	}

	/**
	 * Finds an item by it's unique fields.
	 * @return null if there is no matching item.
	 */
	public final Item searchUnique(final E value)
	{
		// TODO: search nativly for unique constraints
		return getType().searchSingleton(equal(value));
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
		return new CompareFunctionCondition<E>(CompareCondition.Operator.Equal, this, right);
	}
	
	public final CompareFunctionCondition<E> notEqual(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareCondition.Operator.NotEqual, this, right);
	}
	
	public final CompareFunctionCondition<E> less(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareCondition.Operator.Less, this, right);
	}
	
	public final CompareFunctionCondition<E> lessOrEqual(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareCondition.Operator.LessEqual, this, right);
	}
	
	public final CompareFunctionCondition<E> greater(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareCondition.Operator.Greater, this, right);
	}
	
	public final CompareFunctionCondition<E> greaterOrEqual(final Function<E> right)
	{
		return new CompareFunctionCondition<E>(CompareCondition.Operator.GreaterEqual, this, right);
	}
	
	public final ExtremumAggregate<E> min()
	{
		return new ExtremumAggregate<E>(this, true);
	}
	
	public final ExtremumAggregate<E> max()
	{
		return new ExtremumAggregate<E>(this, false);
	}

	public BindFunction<E> bind(final Join join)
	{
		return new BindFunction<E>(this, join);
	}
}
