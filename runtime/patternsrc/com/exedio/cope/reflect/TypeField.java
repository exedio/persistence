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
import com.exedio.cope.misc.ReflectionTypes;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class TypeField<E extends Item> extends Pattern implements Settable<Type<? extends E>>
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final Class<E> valueClass;
	private final StringField idField;
	private final boolean isfinal;
	private final boolean mandatory;

	public static TypeField<Item> create()
	{
		return create(Item.class);
	}

	public static <E extends Item> TypeField<E> create(final Class<E> valueClass)
	{
		return new TypeField<>(valueClass, new StringField());
	}

	private TypeField(final Class<E> valueClass, final StringField idField)
	{
		this.valueClass = requireNonNull(valueClass, "valueClass");
		this.idField = addSourceFeature(idField, "id", CustomAnnotatedElement.create(ComputedInstance.getAnnotation(), CopeSchemaNameElement.getEmpty()));
		this.isfinal = idField.isFinal();
		this.mandatory = idField.isMandatory();
	}

	public TypeField<E> toFinal()
	{
		return new TypeField<>(valueClass, idField.toFinal());
	}

	public TypeField<E> optional()
	{
		return new TypeField<>(valueClass, idField.optional());
	}

	public TypeField<E> unique()
	{
		return new TypeField<>(valueClass, idField.unique());
	}

	public TypeField<E> idLengthMax(final int maximumLength)
	{
		return new TypeField<>(valueClass, idField.lengthMax(maximumLength));
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
	public java.lang.reflect.Type getInitialType()
	{
		return ReflectionTypes.parameterized(Type.class, ReflectionTypes.sub(valueClass));
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

	@Wrap(order=10, doc=Wrap.GET_DOC, nullability=NullableIfOptional.class)
	public Type<? extends E> get(@Nonnull final Item item)
	{
		final String id = idField.get(item);
		if(id==null)
			return null;

		final Type<?> result = getType().getModel().getType(id);
		if(result==null)
			throw new NotFound(this, item, id, null);
		if(!isInstance(result))
			throw new NotFound(this, item, id, result);

		return cast(result);
	}

	public static final class NotFound extends IllegalStateException
	{
		private final TypeField<?> feature;
		private final Item item;
		private final String id;
		private final Type<?> value;

		NotFound(
				final TypeField<?> feature,
				final Item item,
				final String id,
				final Type<?> value)
		{
			this.feature = feature;
			this.item = item;
			this.id = id;
			this.value = value;
		}

		public TypeField<?> getFeature()
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
			final StringBuilder sb = new StringBuilder();
			sb.append("not found '").
				append(id).append("' on ").
				append(item.getCopeID()).
				append(" for ").append(feature).
				append(", ");

			if(value==null)
				sb.append("no such id in model");
			else
				sb.append("expected instance of ").append(feature.getValueClass().getName()).
					append(", but was ").append(value.getJavaClass().getName());

			sb.append('.');
			return sb.toString();
		}

		@Serial
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
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final Type<? extends E> value)
	{
		FinalViolationException.check(this, item);
		idField.set(item, executeValue(value, item));
	}

	@Override
	public SetValue<?>[] execute(final Type<? extends E> value, final Item exceptionItem)
	{
		return new SetValue<?>[]{ SetValue.map(idField, executeValue(value, exceptionItem)) };
	}

	private String executeValue(final Type<? extends E> value, final Item exceptionItem)
	{
		if(value==null && mandatory)
			throw MandatoryViolationException.create(this, exceptionItem);
		if(value!=null && !isInstance(value))
			throw new ClassCastException(
					"expected a Type<? extends " + valueClass.getName() + ">" +
					", but was " + value + " which is a Type<" + value.getJavaClass().getName() + ">" +
					" for " + this + '.');

		return value!=null ? value.getID() : null;
	}

	public List<Type<? extends E>> getValues()
	{
		final ArrayList<Type<? extends E>> result = new ArrayList<>();

		for(final Type<?> type : getType().getModel().getTypes())
			if(isInstance(type))
				result.add(cast(type));

		return Collections.unmodifiableList(result);
	}

	public Condition isInvalid()
	{
		final ArrayList<Condition> conditions = new ArrayList<>();

		for(final Type<?> type : getType().getModel().getTypes())
			if(isInstance(type))
				conditions.add(idField.isNot(type.getID()));

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
			hide=TypeNonUniqueGetter.class)
	@Nullable
	public <P extends Item> P searchUnique(
			@Nonnull final Class<P> typeClass,
			@Nonnull @Parameter(doc=Wrap.FOR_PARAM) final Type<? extends E> value)
	{
		return idField.searchUnique(typeClass, value.getID());
	}

	private boolean isInstance(final Type<?> type)
	{
		return valueClass.isAssignableFrom(type.getJavaClass());
	}

	private Type<? extends E> cast(final Type<?> type)
	{
		return type.asExtends(valueClass);
	}
}
