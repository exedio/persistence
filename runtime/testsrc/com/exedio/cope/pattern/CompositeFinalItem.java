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

package com.exedio.cope.pattern;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;

public final class CompositeFinalItem extends Item
{
	public static final StringField code = new StringField().toFinal();

	public static final CompositeField<CompositeValue> first = CompositeField.create(CompositeValue.class).toFinal();
	public static final CompositeField<CompositeValue> second = CompositeField.create(CompositeValue.class).toFinal();


	/**
	 * Creates a new CompositeFinalItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param first the initial value for field {@link #first}.
	 * @param second the initial value for field {@link #second}.
	 * @throws com.exedio.cope.MandatoryViolationException if code, first, second is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public CompositeFinalItem(
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nonnull final CompositeValue first,
				@javax.annotation.Nonnull final CompositeValue second)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(CompositeFinalItem.code,code),
			com.exedio.cope.SetValue.map(CompositeFinalItem.first,first),
			com.exedio.cope.SetValue.map(CompositeFinalItem.second,second),
		});
	}

	/**
	 * Creates a new CompositeFinalItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CompositeFinalItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.lang.String getCode()
	{
		return CompositeFinalItem.code.get(this);
	}

	/**
	 * Returns the value of {@link #first}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public CompositeValue getFirst()
	{
		return CompositeFinalItem.first.get(this);
	}

	/**
	 * Returns the value of {@link #second}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public CompositeValue getSecond()
	{
		return CompositeFinalItem.second.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for compositeFinalItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CompositeFinalItem> TYPE = com.exedio.cope.TypesBound.newType(CompositeFinalItem.class,CompositeFinalItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CompositeFinalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
