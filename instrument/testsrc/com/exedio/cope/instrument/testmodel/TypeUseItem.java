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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.TypeUseAnnotation;
import com.exedio.cope.instrument.testfeature.SettableOpen;
import com.exedio.cope.misc.ReflectionTypes;
import java.util.List;

public class TypeUseItem extends Item
{
	static final EnumField<@TypeUseAnnotation Enum2> enumField = EnumField.createEvenIfRedundant(Enum2.class);
	static final ItemField<@TypeUseAnnotation NonFinal> itemField = ItemField.create(NonFinal.class);
	static final SettableOpen<@TypeUseAnnotation List<@TypeUseAnnotation String>> withParameterizedType = new SettableOpen<>(
			ReflectionTypes.parameterized(List.class, String.class)
	);

	/**
	 * Creates a new TypeUseItem with all the fields initially needed.
	 * @param enumField the initial value for field {@link #enumField}.
	 * @param itemField the initial value for field {@link #itemField}.
	 * @param withParameterizedType the initial value for field {@link #withParameterizedType}.
	 * @throws com.exedio.cope.MandatoryViolationException if enumField, itemField is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	TypeUseItem(
				@javax.annotation.Nonnull final Enum2 enumField,
				@javax.annotation.Nonnull final NonFinal itemField,
				@javax.annotation.Nonnull final List<String> withParameterizedType)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(TypeUseItem.enumField,enumField),
			com.exedio.cope.SetValue.map(TypeUseItem.itemField,itemField),
			com.exedio.cope.SetValue.map(TypeUseItem.withParameterizedType,withParameterizedType),
		});
	}

	/**
	 * Creates a new TypeUseItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected TypeUseItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #enumField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final Enum2 getEnumField()
	{
		return TypeUseItem.enumField.get(this);
	}

	/**
	 * Sets a new value for {@link #enumField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setEnumField(@javax.annotation.Nonnull final Enum2 enumField)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		TypeUseItem.enumField.set(this,enumField);
	}

	/**
	 * Returns the value of {@link #itemField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final NonFinal getItemField()
	{
		return TypeUseItem.itemField.get(this);
	}

	/**
	 * Sets a new value for {@link #itemField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setItemField(@javax.annotation.Nonnull final NonFinal itemField)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		TypeUseItem.itemField.set(this,itemField);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final List<String> methodWithParameterizedType(final List<String> withParameterizedType)
	{
		return TypeUseItem.withParameterizedType.method(withParameterizedType);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for typeUseItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<TypeUseItem> TYPE = com.exedio.cope.TypesBound.newType(TypeUseItem.class,TypeUseItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected TypeUseItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
