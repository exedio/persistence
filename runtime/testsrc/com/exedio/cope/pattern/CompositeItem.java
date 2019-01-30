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

public final class CompositeItem extends Item
{
	static final StringField code = new StringField().toFinal();

	static final CompositeField<CompositeValue> eins = CompositeField.create(CompositeValue.class);
	static final CompositeField<CompositeValue> zwei = CompositeField.create(CompositeValue.class);


	/**
	 * Creates a new CompositeItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param eins the initial value for field {@link #eins}.
	 * @param zwei the initial value for field {@link #zwei}.
	 * @throws com.exedio.cope.MandatoryViolationException if code, eins, zwei is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	CompositeItem(
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nonnull final CompositeValue eins,
				@javax.annotation.Nonnull final CompositeValue zwei)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CompositeItem.code.map(code),
			CompositeItem.eins.map(eins),
			CompositeItem.zwei.map(zwei),
		});
	}

	/**
	 * Creates a new CompositeItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CompositeItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getCode()
	{
		return CompositeItem.code.get(this);
	}

	/**
	 * Returns the value of {@link #eins}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	CompositeValue getEins()
	{
		return CompositeItem.eins.get(this);
	}

	/**
	 * Sets a new value for {@link #eins}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setEins(@javax.annotation.Nonnull final CompositeValue eins)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CompositeItem.eins.set(this,eins);
	}

	/**
	 * Returns the value of {@link #zwei}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	CompositeValue getZwei()
	{
		return CompositeItem.zwei.get(this);
	}

	/**
	 * Sets a new value for {@link #zwei}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setZwei(@javax.annotation.Nonnull final CompositeValue zwei)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CompositeItem.zwei.set(this,zwei);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for compositeItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CompositeItem> TYPE = com.exedio.cope.TypesBound.newType(CompositeItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private CompositeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
