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
public final class HashItemHolder extends Item
{
	public static final ItemField<HashItem> hashItem = ItemField.create(HashItem.class);


	/**
	 * Creates a new HashItemHolder with all the fields initially needed.
	 * @param hashItem the initial value for field {@link #hashItem}.
	 * @throws com.exedio.cope.MandatoryViolationException if hashItem is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public HashItemHolder(
				@javax.annotation.Nonnull final HashItem hashItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			HashItemHolder.hashItem.map(hashItem),
		});
	}

	/**
	 * Creates a new HashItemHolder and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	HashItemHolder(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #hashItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public HashItem getHashItem()
	{
		return HashItemHolder.hashItem.get(this);
	}

	/**
	 * Sets a new value for {@link #hashItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setHashItem(@javax.annotation.Nonnull final HashItem hashItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		HashItemHolder.hashItem.set(this,hashItem);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hashItemHolder.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<HashItemHolder> TYPE = com.exedio.cope.TypesBound.newType(HashItemHolder.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private HashItemHolder(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
