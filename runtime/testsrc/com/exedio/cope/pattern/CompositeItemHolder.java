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
public final class CompositeItemHolder extends Item
{
	public static final ItemField<CompositeOptionalItem> anItem = ItemField.create(CompositeOptionalItem.class);


	/**
	 * Creates a new CompositeItemHolder with all the fields initially needed.
	 * @param anItem the initial value for field {@link #anItem}.
	 * @throws com.exedio.cope.MandatoryViolationException if anItem is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public CompositeItemHolder(
				@javax.annotation.Nonnull final CompositeOptionalItem anItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CompositeItemHolder.anItem.map(anItem),
		});
	}

	/**
	 * Creates a new CompositeItemHolder and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	CompositeItemHolder(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #anItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public final CompositeOptionalItem getAnItem()
	{
		return CompositeItemHolder.anItem.get(this);
	}

	/**
	 * Sets a new value for {@link #anItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setAnItem(@javax.annotation.Nonnull final CompositeOptionalItem anItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CompositeItemHolder.anItem.set(this,anItem);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for compositeItemHolder.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CompositeItemHolder> TYPE = com.exedio.cope.TypesBound.newType(CompositeItemHolder.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private CompositeItemHolder(final com.exedio.cope.ActivationParameters ap){super(ap);}
}