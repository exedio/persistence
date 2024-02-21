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

package com.exedio.cope.instrument.parameters;

import com.exedio.cope.Item;

@SuppressWarnings({"ConstantForZeroLengthArrayAllocation", "AbstractClassNeverImplemented"})
abstract class AbstractItem extends Item
{
	/**
	 * Creates a new AbstractItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"constructor-first","constructor-second"})
	AbstractItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new AbstractItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(genericConstructor=...)
	protected AbstractItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.GeneratedClass
	private static final long serialVersionUID = 2;

	/**
	 * The persistent type information for abstractItem.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(type=...)
	@java.lang.SuppressWarnings("type-single")
	static final com.exedio.cope.Type<AbstractItem> TYPE = com.exedio.cope.TypesBound.newType(AbstractItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.GeneratedClass
	protected AbstractItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
