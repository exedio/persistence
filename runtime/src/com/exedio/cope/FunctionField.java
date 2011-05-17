/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.exedio.cope.CompareFunctionCondition.Operator;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperSuppressor;
import com.exedio.cope.instrument.WrapperThrown;
import com.exedio.cope.search.ExtremumAggregate;
import com.exedio.cope.util.Cast;

public abstract class FunctionField<E extends Object> extends Field<E>
	implements Function<E>
{
	private static final long serialVersionUID = 1l;

	final boolean unique;
	private final UniqueConstraint implicitUniqueConstraint;
	final E defaultConstant;
	private ArrayList<UniqueConstraint> uniqueConstraints;

	FunctionField(final boolean isfinal, final boolean optional, final boolean unique, final Class<E> valueClass, final E defaultConstant)
	{
		super(isfinal, optional, valueClass);
		this.unique = unique;
		this.implicitUniqueConstraint =
			unique ?
				new UniqueConstraint(this) :
				null;
		this.defaultConstant = defaultConstant;
	}

	final void checkDefaultConstant()
	{
		if(defaultConstant!=null)
		{
			try
			{
				check(defaultConstant, null);
			}
			catch(final ConstraintViolationException e)
			{
				// BEWARE
				// Must not make exception e available to public,
				// since it contains a reference to this function field,
				// which has not been constructed successfully.
				throw new IllegalArgumentException(
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
	 * <tt>({@link #isFinal() isFinal()} || {@link #isMandatory() isMandatory()}) && {@link #getDefaultConstant()}==null</tt>.
	 */
	@Override
	public boolean isInitial()
	{
		return (defaultConstant==null) && super.isInitial();
	}

	@Override
	void mount(final Type<? extends Item> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);

		if(unique)
			implicitUniqueConstraint.mount(type, name + UniqueConstraint.IMPLICIT_UNIQUE_SUFFIX, null);
	}

	final void checkValueClass(final Class<? extends Object> superClass)
	{
		if(!superClass.isAssignableFrom(valueClass))
			throw new RuntimeException("is not a subclass of " + superClass.getName() + ": "+valueClass.getName());
	}

	public abstract FunctionField<E> copy();

	/**
	 * @deprecated Renamed to {@link #copy()}.
	 */
	@Deprecated
	public final FunctionField<E> copyFunctionField()
	{
		return copy();
	}


	/**
	 * Returns a new FunctionField,
	 * that differs from this FunctionField
	 * by being unique.
	 * If this FunctionField is already unique,
	 * the the result is equal to this FunctionField.
	 * @see #getImplicitUniqueConstraint()
	 */
	public abstract FunctionField<E> unique();

	public abstract FunctionField<E> nonUnique();
	public abstract FunctionField<E> noDefault();
	public abstract FunctionField<E> defaultTo(E defaultConstant);

	abstract E get(final Row row);
	abstract void set(final Row row, final E surface);

	@Override
	public Class getInitialType()
	{
		return valueClass;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(FunctionField.class, this, super.getWrappers());
	}

	@Wrap(order=10, doc="Returns the value of {0}.", suppressor=PrimitiveSuppressor.class)
	@Override
	public final E get(final Item item)
	{
		item.type.assertBelongs(this);

		return Cast.verboseCast(valueClass, item.getEntity().get(this));
	}

	private static final class PrimitiveSuppressor implements WrapperSuppressor<FunctionField>
	{
		public boolean isSuppressed(final FunctionField feature)
		{
			return feature.getInitialType().isPrimitive();
		}
	}

	final E getMandatoryObject(final Item item)
	{
		if(optional)
			throw new IllegalArgumentException("field " + toString() + " is not mandatory");

		final E result = get(item);
		if(result==null)
			throw new NullPointerException("field " + toString() + " returns null in getMandatory");
		return result;
	}

	static final class OptionalSuppressor implements WrapperSuppressor<FunctionField>
	{
		public boolean isSuppressed(final FunctionField feature)
		{
			return !feature.isMandatory();
		}
	}

	static final class NonUniqueSuppressor implements WrapperSuppressor<FunctionField>
	{
		public boolean isSuppressed(final FunctionField feature)
		{
			return feature.getImplicitUniqueConstraint()==null;
		}
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			suppressor=FinalPrimitiveSuppressor.class,
			thrownx=ElementThrown.class)
	@Override
	public final void set(final Item item, final E value)
	{
		item.set(this, value);
	}

	private static final class FinalPrimitiveSuppressor implements WrapperSuppressor<FunctionField>
	{
		public boolean isSuppressed(final FunctionField feature)
		{
			return feature.isFinal() || feature.getInitialType().isPrimitive();
		}
	}

	static final class PrimitiveSetSuppressor implements WrapperSuppressor<FunctionField>
	{
		public boolean isSuppressed(final FunctionField feature)
		{
			return feature.isFinal() || !feature.isMandatory();
		}
	}

	static final class ElementThrown implements WrapperThrown<FunctionField<?>>
	{
		public Set<Class<? extends Throwable>> get(final FunctionField<?> feature)
		{
			final Set<Class<? extends Throwable>> result = feature.getInitialExceptions();
			if(feature.getInitialType().isPrimitive())
				result.remove(MandatoryViolationException.class);
			return result;
		}
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	public final void check(final TC tc, final Join join)
	{
		tc.check(this, join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		bf.append(getColumn(), join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	public void appendSelect(final Statement bf, final Join join)
	{
		final Column column = getColumn();
		bf.append(column, join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
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
	 * @see #unique()
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
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
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
	 * @throws NullPointerException if value is null.
	 */
	public final Item searchUnique(final E value)
	{
		if(value==null)
			throw new NullPointerException("cannot search uniquely for null on " + getID());
		// TODO: search nativly for unique constraints
		return getType().searchSingleton(equal(value));
	}

	/**
	 * Finds an item by it's unique fields.
	 * @return null if there is no matching item.
	 * @throws NullPointerException if value is null.
	 */
	@Wrap(order=100, name="for{0}",
			doc="Finds a {2} by it''s {0}.",
			docReturn="null if there is no matching item.",
			suppressor=NonUniquePrimitiveSuppressor.class)
	public final <P extends Item> P searchUnique(
			final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.") final E value)
	{
		if(value==null)
			throw new NullPointerException("cannot search uniquely for null on " + getID());
		// TODO: search nativly for unique constraints
		return getType().as(typeClass).searchSingleton(equal(value));
	}

	private static final class NonUniquePrimitiveSuppressor implements WrapperSuppressor<FunctionField>
	{
		public boolean isSuppressed(final FunctionField feature)
		{
			return
				(feature.getImplicitUniqueConstraint()==null) ||
				(feature.getInitialType().isPrimitive());
		}
	}

	static List<Wrapper> adjustOrderForPrimitiveOperations(final List<Wrapper> in)
	{
		// TODO

		final LinkedList<Wrapper> result = new LinkedList<Wrapper>(in);
		for(final Wrapper wrapper : result)
		{
			if(wrapper.matchesMethod("getMandatory", Item.class))
			{
				result.remove(wrapper);
				result.add(0, wrapper);
				break;
			}
		}
		for(final Wrapper wrapper : result)
		{
			if(
					wrapper.matchesMethod("set", Item.class, int    .class) ||
					wrapper.matchesMethod("set", Item.class, long   .class) ||
					wrapper.matchesMethod("set", Item.class, double .class) ||
					wrapper.matchesMethod("set", Item.class, boolean.class) )
			{
				result.remove(wrapper);
				result.add(1, wrapper);
				break;
			}
		}
		return result;
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

	public Condition equal(final E value)
	{
		return Cope.equal(this, value);
	}

	public final Condition equal(final Join join, final E value)
	{
		return this.bind(join).equal(value);
	}

	public final Condition in(final E... values)
	{
		return CompositeCondition.in(this, values);
	}

	public final Condition in(final Collection<E> values)
	{
		return CompositeCondition.in(this, values);
	}

	public Condition notEqual(final E value)
	{
		return Cope.notEqual(this, value);
	}

	public final CompareCondition<E> less(final E value)
	{
		return new CompareCondition<E>(Operator.Less, this, value);
	}

	public final CompareCondition<E> lessOrEqual(final E value)
	{
		return new CompareCondition<E>(Operator.LessEqual, this, value);
	}

	public final CompareCondition<E> greater(final E value)
	{
		return new CompareCondition<E>(Operator.Greater, this, value);
	}

	public final CompareCondition<E> greaterOrEqual(final E value)
	{
		return new CompareCondition<E>(Operator.GreaterEqual, this, value);
	}

	public Condition between(final E lowerBound, final E upperBound)
	{
		return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
	}

	public final CompareFunctionCondition<E> equal(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Equal, this, right);
	}

	public final CompareFunctionCondition<E> notEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.NotEqual, this, right);
	}

	public final CompareFunctionCondition<E> less(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Less, this, right);
	}

	public final CompareFunctionCondition<E> lessOrEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.LessEqual, this, right);
	}

	public final CompareFunctionCondition<E> greater(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Greater, this, right);
	}

	public final CompareFunctionCondition<E> greaterOrEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.GreaterEqual, this, right);
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
