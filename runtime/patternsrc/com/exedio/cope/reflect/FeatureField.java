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

package com.exedio.cope.reflect;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.CopeSchemaNameElement;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class FeatureField<E extends Feature> extends Pattern implements Settable<E>
{
	private static final long serialVersionUID = 1l;

	private final Class<E> valueClass;
	private final SortedSet<Class<? extends E>> forbiddenValueClasses;
	private final StringField idField;
	private final boolean isfinal;
	private final boolean mandatory;

	public static FeatureField<Feature> create()
	{
		return create(Feature.class);
	}

	public static <E extends Feature> FeatureField<E> create(final Class<E> valueClass)
	{
		return new FeatureField<>(valueClass, Collections.emptySortedSet(), new StringField());
	}

	private FeatureField(
			final Class<E> valueClass,
			final SortedSet<Class<? extends E>> forbiddenValueClasses,
			final StringField idField)
	{
		this.valueClass = requireNonNull(valueClass, "valueClass");
		this.forbiddenValueClasses = forbiddenValueClasses;
		this.idField = addSourceFeature(idField, "id", CustomAnnotatedElement.create(ComputedInstance.getAnnotation(), CopeSchemaNameElement.getEmpty()));
		this.isfinal = idField.isFinal();
		this.mandatory = idField.isMandatory();
	}

	public FeatureField<E> forbid(final Class<? extends E> forbiddenValueClass)
	{
		requireNonNull(forbiddenValueClass, "valueClass");
		if(valueClass.equals(forbiddenValueClass))
			throw new IllegalArgumentException(
					"expected a subclass of " + valueClass.getName() + ", " +
					"but was that class itself");
		if(!valueClass.isAssignableFrom(forbiddenValueClass))
			throw new ClassCastException(
					"expected a subclass of " + valueClass.getName() + ", " +
					"but was " + forbiddenValueClass.getName());
		for(final Class<? extends E> f : forbiddenValueClasses)
		{
			if(forbiddenValueClass.isAssignableFrom(f))
				throw new IllegalArgumentException(
						forbiddenValueClass.getName() + " must not be a super class " +
						"of the already forbidden class " + f.getName());
			if(f.isAssignableFrom(forbiddenValueClass))
				throw new IllegalArgumentException(
						forbiddenValueClass.getName() + " must not be a subclass " +
						"of the already forbidden class " + f.getName());
		}

		final TreeSet<Class<? extends E>> newForbidden = new TreeSet<>(Comparator.comparing(Class::getName));
		newForbidden.addAll(forbiddenValueClasses);
		newForbidden.add(forbiddenValueClass);
		return new FeatureField<>(valueClass, newForbidden, idField.copy());
	}

	public FeatureField<E> toFinal()
	{
		return new FeatureField<>(valueClass, forbiddenValueClasses, idField.toFinal());
	}

	public FeatureField<E> optional()
	{
		return new FeatureField<>(valueClass, forbiddenValueClasses, idField.optional());
	}

	public FeatureField<E> unique()
	{
		return new FeatureField<>(valueClass, forbiddenValueClasses, idField.unique());
	}

	public FeatureField<E> idLengthMax(final int maximumLength)
	{
		return new FeatureField<>(valueClass, forbiddenValueClasses, idField.lengthMax(maximumLength));
	}

	public Class<E> getValueClass()
	{
		return valueClass;
	}

	public SortedSet<Class<? extends E>> getForbiddenValueClasses()
	{
		return Collections.unmodifiableSortedSet(forbiddenValueClasses);
	}

	public StringField getIdField()
	{
		return idField;
	}

	@Override
	public boolean isInitial()
	{
		return idField.isInitial();
	}

	@Override
	public boolean isFinal()
	{
		return isfinal;
	}

	@Override
	public boolean isMandatory()
	{
		return mandatory;
	}

	@Override
	public Class<?> getInitialType()
	{
		return valueClass;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = idField.getInitialExceptions();
		if(!forbiddenValueClasses.isEmpty())
			result.add(ForbiddenFeatureException.class);
		return result;
	}

	/**
	 * @see StringField#getImplicitUniqueConstraint()
	 */
	public UniqueConstraint getImplicitUniqueConstraint()
	{
		return idField.getImplicitUniqueConstraint();
	}

	@Wrap(order=10, doc=Wrap.GET_DOC, nullability=NullableIfOptional.class)
	public E get(@Nonnull final Item item)
	{
		final String id = idField.get(item);
		if(id==null)
			return null;

		final Feature result = getType().getModel().getFeature(id);
		if(result==null)
			throw new NotFound(this, item, id, null);
		if(!isInstance(result))
			throw new NotFound(this, item, id, result);

		return cast(result);
	}

	public static final class NotFound extends IllegalStateException
	{
		private final FeatureField<?> feature;
		private final Item item;
		private final String id;
		private final Feature value;

		NotFound(
				final FeatureField<?> feature,
				final Item item,
				final String id,
				final Feature value)
		{
			this.feature = feature;
			this.item = item;
			this.id = id;
			this.value = value;
		}

		public FeatureField<?> getFeature()
		{
			return feature;
		}

		public Item getItem()
		{
			return item;
		}

		public String getID()
		{
			return id;
		}

		@Override
		public String getMessage()
		{
			final StringBuilder bf = new StringBuilder();
			bf.append("not found '").
				append(id).append("' on ").
				append(item.getCopeID()).
				append(" for ").append(feature).
				append(", ");

			if(value==null)
				bf.append("no such id in model");
			else
				bf.append("expected instance of ").append(feature.getValueClass().getName()).
					append(", but was ").append(value.getClass().getName());

			bf.append('.');
			return bf.toString();
		}

		private static final long serialVersionUID = 1l;
	}

	public String getId(final Item item)
	{
		return idField.get(item);
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final E value)
	{
		FinalViolationException.check(this, item);
		idField.set(item, executeValue(value, item));
	}

	@Override
	public SetValue<?>[] execute(final E value, final Item exceptionItem)
	{
		return new SetValue<?>[]{ SetValue.map(idField, executeValue(value, exceptionItem)) };
	}

	private String executeValue(final E value, final Item exceptionItem)
	{
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, exceptionItem);
		if(value!=null && !isInstance(value))
			throw new ClassCastException(
					"expected a " + valueClass.getName() +
					", but was " + value + " which is a " + value.getClass().getName() +
					" for " + this + '.');
		if(value!=null)
		{
			final Class<? extends E> forbidden = getForbidden(value);
			if(forbidden!=null)
				throw new ForbiddenFeatureException(this, exceptionItem, value, forbidden);
		}

		return value!=null ? value.getID() : null;
	}

	public List<E> getValues()
	{
		final ArrayList<E> result = new ArrayList<>();

		for(final Type<?> type : getType().getModel().getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(isInstance(feature) && getForbidden(feature)==null)
					result.add(cast(feature));

		return Collections.unmodifiableList(result);
	}

	public Condition isInvalid()
	{
		final ArrayList<Condition> conditions = new ArrayList<>();

		for(final Type<?> type : getType().getModel().getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(isInstance(feature) && getForbidden(feature)==null)
					conditions.add(idField.notEqual(feature.getID()));

		return Cope.and(conditions);
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 * @throws NullPointerException if value is null.
	 */
	@Wrap(order=30, name=Wrap.FOR_NAME,
			doc=Wrap.FOR_DOC,
			docReturn=Wrap.FOR_RETURN,
			hide=FeatureNonUniqueGetter.class)
	@Nullable
	public <P extends Item> P searchUnique(
			@Nonnull final Class<P> typeClass,
			@Nonnull @Parameter(doc=Wrap.FOR_PARAM) final E value)
	{
		return idField.searchUnique(typeClass, value.getID());
	}

	private boolean isInstance(final Feature feature)
	{
		return valueClass.isInstance(feature);
	}

	private Class<? extends E> getForbidden(final Feature feature)
	{
		for(final Class<? extends E> result : forbiddenValueClasses)
			if(result.isInstance(feature))
				return result;

		return null;
	}

	private E cast(final Feature feature)
	{
		return valueClass.cast(feature);
	}
}
