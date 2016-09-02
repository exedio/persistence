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

public final class CompositeOptionalItem extends Item
{
	public static final StringField code = new StringField().toFinal();

	public static final CompositeField<CompositeValue> uno = CompositeField.create(CompositeValue.class).optional();
	public static final CompositeField<CompositeValue> duo = CompositeField.create(CompositeValue.class).optional();


	/**
	 * Creates a new CompositeOptionalItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public CompositeOptionalItem(
				@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CompositeOptionalItem.code.map(code),
		});
	}

	/**
	 * Creates a new CompositeOptionalItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CompositeOptionalItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public final java.lang.String getCode()
	{
		return CompositeOptionalItem.code.get(this);
	}

	/**
	 * Returns the value of {@link #uno}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public final CompositeValue getUno()
	{
		return CompositeOptionalItem.uno.get(this);
	}

	/**
	 * Sets a new value for {@link #uno}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setUno(@javax.annotation.Nullable final CompositeValue uno)
	{
		CompositeOptionalItem.uno.set(this,uno);
	}

	/**
	 * Returns the value of {@link #duo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public final CompositeValue getDuo()
	{
		return CompositeOptionalItem.duo.get(this);
	}

	/**
	 * Sets a new value for {@link #duo}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setDuo(@javax.annotation.Nullable final CompositeValue duo)
	{
		CompositeOptionalItem.duo.set(this,duo);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for compositeOptionalItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CompositeOptionalItem> TYPE = com.exedio.cope.TypesBound.newType(CompositeOptionalItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private CompositeOptionalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
