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

import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(genericConstructor=PACKAGE)
public final class LimitedListFieldItemFieldItem extends Item
{
	static final ItemField<LimitedListFieldItem> limitedListFieldItem = ItemField.create(LimitedListFieldItem.class);


	/**
	 * Creates a new LimitedListFieldItemFieldItem with all the fields initially needed.
	 * @param limitedListFieldItem the initial value for field {@link #limitedListFieldItem}.
	 * @throws com.exedio.cope.MandatoryViolationException if limitedListFieldItem is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	LimitedListFieldItemFieldItem(
				@javax.annotation.Nonnull final LimitedListFieldItem limitedListFieldItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			LimitedListFieldItemFieldItem.limitedListFieldItem.map(limitedListFieldItem),
		});
	}

	/**
	 * Creates a new LimitedListFieldItemFieldItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	LimitedListFieldItemFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #limitedListFieldItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	LimitedListFieldItem getLimitedListFieldItem()
	{
		return LimitedListFieldItemFieldItem.limitedListFieldItem.get(this);
	}

	/**
	 * Sets a new value for {@link #limitedListFieldItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setLimitedListFieldItem(@javax.annotation.Nonnull final LimitedListFieldItem limitedListFieldItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		LimitedListFieldItemFieldItem.limitedListFieldItem.set(this,limitedListFieldItem);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for limitedListFieldItemFieldItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<LimitedListFieldItemFieldItem> TYPE = com.exedio.cope.TypesBound.newType(LimitedListFieldItemFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private LimitedListFieldItemFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
