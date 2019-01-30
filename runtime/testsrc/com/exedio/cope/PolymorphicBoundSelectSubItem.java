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

package com.exedio.cope;

public final class PolymorphicBoundSelectSubItem extends PolymorphicBoundSelectSuperItem
{


	/**
	 * Creates a new PolymorphicBoundSelectSubItem with all the fields initially needed.
	 * @param parent the initial value for field {@link #parent}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public PolymorphicBoundSelectSubItem(
				@javax.annotation.Nullable final com.exedio.cope.PolymorphicBoundSelectSuperItem parent)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.PolymorphicBoundSelectSuperItem.parent.map(parent),
		});
	}

	/**
	 * Creates a new PolymorphicBoundSelectSubItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private PolymorphicBoundSelectSubItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for polymorphicBoundSelectSubItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PolymorphicBoundSelectSubItem> TYPE = com.exedio.cope.TypesBound.newType(PolymorphicBoundSelectSubItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private PolymorphicBoundSelectSubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
