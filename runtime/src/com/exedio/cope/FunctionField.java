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

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.PrimitiveUtil;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import java.lang.reflect.AnnotatedElement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class FunctionField<E> extends Field<E>
	implements Function<E>, Copyable
{
	private static final long serialVersionUID = 1l;

	final boolean unique;
	private final UniqueConstraint implicitUniqueConstraint;
	final ItemField<?>[] copyFrom;
	private final CopyConstraint[] implicitCopyConstraintsFrom;
	final DefaultSupplier<E> defaultS;
	private ArrayList<UniqueConstraint> uniqueConstraints;
	private boolean isRedundantByCopyConstraint;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	FunctionField(
			final boolean isfinal,
			final boolean optional,
			final Class<E> valueClass,
			final boolean unique,
			final ItemField<?>[] copyFrom,
			final DefaultSupplier<E> defaultS)
	{
		super(isfinal, optional, valueClass);
		this.unique = unique;
		this.copyFrom = copyFrom;
		//noinspection ThisEscapedInObjectConstruction
		this.implicitUniqueConstraint = unique ? new UniqueConstraint(this) : null;
		this.implicitCopyConstraintsFrom = (copyFrom!=null) ? newCopyConstraintsFrom(copyFrom) : null;

		this.defaultS = defaultS!=null ? defaultS.forNewField() : null;
	}

	private CopyConstraint[] newCopyConstraintsFrom(final ItemField<?>[] copyFrom)
	{
		assert copyFrom.length>0;
		final CopyConstraint[] result = new CopyConstraint[copyFrom.length];
		for(int i = 0; i<copyFrom.length; i++)
			result[i] = new CopyConstraint(copyFrom[i], this);
		return result;
	}

	final void mountDefault()
	{
		if(defaultS!=null)
			defaultS.mount(this);
	}

	public final boolean hasDefault()
	{
		return defaultS!=null;
	}

	public final E getDefaultConstant()
	{
		return (defaultS instanceof DefaultConstant) ? ((DefaultConstant<E>)defaultS).value : null;
	}

	final DefaultConstant<E> defaultConstant(final E value)
	{
		return value!=null ? new DefaultConstant<>(value, null) : null;
	}

	final DefaultConstant<E> defaultConstantWithCreatedTime(final E value)
	{
		return value!=null ? new DefaultConstant<>(value, Instant.now()) : null;
	}

	private static final class DefaultConstant<E> extends DefaultSupplier<E>
	{
		final E value;
		private final Instant createdInstant;

		DefaultConstant(final E value, final Instant createdInstant)
		{
			this.value = value;
			this.createdInstant = createdInstant;

			assert value!=null;
		}

		@Override
		E generate(final Context ctx)
		{
			return value;
		}

		Instant createdInstant()
		{
			assert createdInstant!=null;
			return createdInstant;
		}

		@Override
		DefaultSupplier<E> forNewField()
		{
			return this;
		}

		@Override
		void mount(final FunctionField<E> field)
		{
			try
			{
				field.check(value, null);
			}
			catch(final ConstraintViolationException e)
			{
				// BEWARE
				// Must not make exception available to public,
				// since it contains a reference to this function field,
				// which has not been constructed successfully.
				throw new IllegalArgumentException(
						"The default constant of the field " +
						"does not comply to one of it's own constraints, " +
						"caused a " + e.getClass().getSimpleName() +
						": " + e.getMessageWithoutFeature() +
						" Default constant was '" + value + "'.");
			}
		}
	}

	final Instant getDefaultConstantCreatedInstant()
	{
		return ((DefaultConstant<E>)defaultS).createdInstant();
	}

	/**
	 * Returns true, if a value for the field should be specified
	 * on the creation of an item.
	 * This implementation returns
	 * {@code ({@link #isFinal() isFinal()} || {@link #isMandatory() isMandatory()}) &amp;&amp; !{@link #hasDefault()}}.
	 */
	@Override
	public final boolean isInitial()
	{
		return !hasDefault() && !isRedundantByCopyConstraint && super.isInitial();
	}

	protected final ItemField<?>[] addCopyFrom(final ItemField<?> copyFrom)
	{
		requireNonNull(copyFrom, "copyFrom");
		if(this.copyFrom==null)
			return new ItemField<?>[]{copyFrom};

		final int length = this.copyFrom.length;
		final ItemField<?>[] result = new ItemField<?>[length+1];
		System.arraycopy(this.copyFrom, 0, result, 0, length);
		result[length] = copyFrom;
		return result;
	}

	@Override
	void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);

		if(unique)
			implicitUniqueConstraint.mount(type, name + UniqueConstraint.IMPLICIT_UNIQUE_SUFFIX, null);
		if(implicitCopyConstraintsFrom!=null)
			for(final CopyConstraint constraint : implicitCopyConstraintsFrom)
				constraint.mount(type, name + "CopyFrom" + constraint.getTarget().getName(), null);
		if(defaultS!=null)
			defaultS.mount(type, name, annotationSource);
	}

	final void checkValueClass(final Class<?> superClass)
	{
		if(!superClass.isAssignableFrom(valueClass))
			throw new RuntimeException("is not a subclass of " + superClass.getName() + ": "+valueClass.getName());
	}

	// just to make sure the result is a FunctionField
	@Override public abstract FunctionField<E> toFinal();
	@Override public abstract FunctionField<E> optional();

	public abstract FunctionField<E> copy();

	@Override
	public final FunctionField<E> copy(final CopyMapper mapper)
	{
		return mapper.copy(this);
	}

	/**
	 * Returns a new FunctionField,
	 * that differs from this FunctionField
	 * by being unique.
	 * If this FunctionField is already unique,
	 * the the result is equal to this FunctionField.
	 * @see #getImplicitUniqueConstraint()
	 */
	@SuppressWarnings("unused") // OK: Methods are tested on implementation classes, but never used as a member of this interface.
	public abstract FunctionField<E> unique();

	public abstract FunctionField<E> nonUnique();

	/**
	 * @see ItemField#copyTo(FunctionField)
	 */
	public abstract FunctionField<E> copyFrom(ItemField<?> copyFrom);

	/**
	 * Returns a new FunctionField that differs from this FunctionField
	 * by having no {@link #copyFrom(ItemField) copyFrom} fields set.
	 */
	public abstract FunctionField<E> noCopyFrom();

	public abstract FunctionField<E> noDefault();
	public abstract FunctionField<E> defaultTo(E defaultConstant);

	boolean overlaps(final FunctionField<?> other)
	{
		return getClass().equals(other.getClass());
	}

	abstract E get(final Row row);
	abstract void set(final Row row, final E surface);

	@Wrap(order=10, doc=Wrap.GET_DOC, hide=PrimitiveGetter.class, nullability=NullableIfOptional.class)
	@Override
	public final E get(@Nonnull final Item item)
	{
		item.type.assertBelongs(this);

		return valueClass.cast(item.getEntity().get(this));
	}

	final E getMandatoryObject(final Item item)
	{
		if(optional)
			throw new IllegalArgumentException("field " + this + " is not mandatory");

		final E result = get(item);
		if(result==null)
			throw new NullPointerException("field " + this + " returns null in getMandatory");
		return result;
	}

	@Override
	public final E get(final FieldValues item)
	{
		return item.get(this);
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			hide={FinalSettableGetter.class, RedundantByCopyConstraintGetter.class, PrimitiveGetter.class},
			thrownGetter=InitialThrown.class)
	@Override
	public final void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final E value)
	{
		item.set(this, value);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void check(final TC tc, final Join join)
	{
		tc.check(this, join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		bf.append(getColumn(), join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public void appendSelect(final Statement bf, final Join join)
	{
		bf.append(getColumn(), join);
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
		return uniqueConstraints!=null ? Collections.unmodifiableList(uniqueConstraints) : Collections.emptyList();
	}

	/**
	 * Returns the copy constraint of this field,
	 * that has been created implicitly when creating this field.
	 * Does return null, if there is no such copy constraint.
	 * @see StringField#copyFrom(ItemField)
	 * @see ItemField#copyFrom(ItemField)
	 * @deprecated
	 * Use {@link #getImplicitCopyConstraints()} instead.
	 * Throws a RuntimeException, if {@link #getImplicitCopyConstraints()} returns more than one constraint.
	 */
	@Deprecated
	public CopyConstraint getImplicitCopyConstraint()
	{
		final List<CopyConstraint> constraints = getImplicitCopyConstraints();
		switch(constraints.size())
		{
			case 0: return null;
			case 1: return constraints.get(0);
			default: throw new RuntimeException(constraints.toString());
		}
	}

	/**
	 * Returns the copy constraints of this field,
	 * that has been created implicitly when creating this field.
	 * Does return an empty list, if there is no such copy constraint.
	 * @see StringField#copyFrom(ItemField)
	 * @see ItemField#copyFrom(ItemField)
	 * @deprecated
	 * This method makes no sense anymore, since there is a
	 * {@link ItemField#copyTo(FunctionField)} as well.
	 */
	@Deprecated
	public List<CopyConstraint> getImplicitCopyConstraints()
	{
		return
				implicitCopyConstraintsFrom!=null
				? Collections.unmodifiableList(Arrays.asList(implicitCopyConstraintsFrom))
				: Collections.emptyList();
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
			uniqueConstraints = new ArrayList<>();
		}
		else
		{
			if(uniqueConstraints.contains(constraint))
				throw new RuntimeException(constraint.toString());
		}

		uniqueConstraints.add(constraint);
	}

	final void setRedundantByCopyConstraint()
	{
		isRedundantByCopyConstraint = true;
	}

	public final boolean isRedundantByCopyConstraint()
	{
		return isRedundantByCopyConstraint;
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
	@Wrap(order=100, name=Wrap.FOR_NAME,
			doc=Wrap.FOR_DOC_BROKEN,
			docReturn=Wrap.FOR_RETURN,
			hide={NonUniqueGetter.class, PrimitiveGetter.class})
	@Nullable
	public final <P extends Item> P searchUnique(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc=Wrap.FOR_PARAM) @Nonnull final E value)
	{
		final Type<P> type =
				requireParentClass(typeClass, "typeClass");
		requireNonNull(value, () -> "cannot search uniquely for null on " + getID());
		// TODO: search nativly for unique constraints
		return type.searchSingleton(equal(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws NullPointerException if value is null.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	@Wrap(order=110, name=Wrap.FOR_STRICT_NAME,
			doc=Wrap.FOR_DOC,
			hide={NonUniqueGetter.class, PrimitiveGetter.class},
			thrown=@Wrap.Thrown(value=IllegalArgumentException.class, doc=Wrap.FOR_STRICT_THROWN))
	@Nonnull
	public final <P extends Item> P searchUniqueStrict(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc=Wrap.FOR_PARAM) @Nonnull final E value)
		throws IllegalArgumentException
	{
		final Type<P> type =
				requireParentClass(typeClass, "typeClass");
		requireNonNull(value, () -> "cannot search uniquely for null on " + getID());
		// TODO: search nativly for unique constraints
		return type.searchSingletonStrict(equal(value));
	}

	boolean isPrimitive()
	{
		return isMandatory() && (PrimitiveUtil.toPrimitive(getValueClass())!=null);
	}


	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Renamed to {@link #copy()}.
	 */
	@Deprecated
	public final FunctionField<E> copyFunctionField()
	{
		return copy();
	}
}
