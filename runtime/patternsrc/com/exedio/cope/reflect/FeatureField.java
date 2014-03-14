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

package com.exedio.cope.reflect;

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
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.util.Cast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class FeatureField<E extends Feature> extends Pattern implements Settable<E>
{
	private static final long serialVersionUID = 1l;

	private final Class<E> valueClass;
	private final StringField idField;
	private final boolean isfinal;
	private final boolean mandatory;

	public static FeatureField<Feature> create()
	{
		return create(Feature.class);
	}

	public static <E extends Feature> FeatureField<E> create(final Class<E> valueClass)
	{
		return new FeatureField<E>(valueClass, new StringField());
	}

	private FeatureField(final Class<E> valueClass, final StringField integer)
	{
		if(valueClass==null)
			throw new NullPointerException("valueClass");

		this.valueClass = valueClass;
		this.idField = integer;
		addSource(integer, "id", CustomAnnotatedElement.create(ComputedInstance.getAnnotation(), CopeSchemaNameEmpty.get()));
		this.isfinal = integer.isFinal();
		this.mandatory = integer.isMandatory();
	}

	public FeatureField<E> toFinal()
	{
		return new FeatureField<E>(valueClass, idField.toFinal());
	}

	public FeatureField<E> optional()
	{
		return new FeatureField<E>(valueClass, idField.optional());
	}

	public FeatureField<E> unique()
	{
		return new FeatureField<E>(valueClass, idField.unique());
	}

	public Class<E> getValueClass()
	{
		return valueClass;
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
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return idField.getInitialExceptions();
	}

	/**
	 * @see StringField#getImplicitUniqueConstraint()
	 */
	public UniqueConstraint getImplicitUniqueConstraint()
	{
		return idField.getImplicitUniqueConstraint();
	}

	@Wrap(order=10, doc="Returns the value of {0}.")
	public E get(final Item item)
	{
		final String id = idField.get(item);
		if(id==null)
			return null;

		final Feature f = getType().getModel().getFeature(id);
		if(f==null)
			throw new IllegalStateException(id);

		return Cast.verboseCast(valueClass, f);
	}

	public String getId(final Item item)
	{
		return idField.get(item);
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(final Item item, final E value)
	{
		if(isfinal)
			throw FinalViolationException.create(this, item);
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, item);

		idField.set(item, value!=null ? value.getID() : null);
	}

	@Override
	public SetValue<E> map(final E value)
	{
		return SetValue.map(this, value);
	}

	@Override
	public SetValue<?>[] execute(final E value, final Item exceptionItem)
	{
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, exceptionItem);

		return new SetValue<?>[]{ idField.map(value!=null ? value.getID() : null) };
	}

	public List<E> getValues()
	{
		final ArrayList<E> result = new ArrayList<E>();

		for(final Type<?> type : getType().getModel().getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(valueClass.isInstance(feature))
					result.add(valueClass.cast(feature));

		return Collections.unmodifiableList(result);
	}

	public Condition isInvalid()
	{
		final ArrayList<Condition> conditions = new ArrayList<Condition>();

		for(final Type<?> type : getType().getModel().getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(valueClass.isInstance(feature))
					conditions.add(idField.notEqual(feature.getID()));

		return Cope.and(conditions);
	}

	/**
	 * Finds an item by it's unique fields.
	 * @return null if there is no matching item.
	 * @throws NullPointerException if value is null.
	 */
	@Wrap(order=30, name="for{0}",
			doc="Finds a {2} by it''s {0}.",
			docReturn="null if there is no matching item.",
			hide=NonUniqueGetter.class)
	public final <P extends Item> P searchUnique(
			final Class<P> typeClass,
			@Parameter(doc="shall be equal to field {0}.") final E value)
	{
		return idField.searchUnique(typeClass, value.getID());
	}

	private static final class NonUniqueGetter implements BooleanGetter<FeatureField<?>>
	{
		@Override
		public boolean get(final FeatureField<?> feature)
		{
			return feature.getIdField().getImplicitUniqueConstraint()==null;
		}
	}

	// ------------------- deprecated stuff -------------------

	@Override
	@Deprecated
	public Class<?> getInitialType()
	{
		return valueClass;
	}

	/**
	 * @deprecated Use {@link #create()} instead
	 */
	@Deprecated
	public static FeatureField<Feature> newField()
	{
		return create();
	}

	/**
	 * @deprecated Use {@link #create(Class)} instead
	 */
	@Deprecated
	public static <E extends Feature> FeatureField<E> newField(final Class<E> valueClass)
	{
		return create(valueClass);
	}
}
