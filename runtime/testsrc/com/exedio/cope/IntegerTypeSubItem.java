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

import com.exedio.cope.ItemField.DeletePolicy;

public class IntegerTypeSubItem extends IntegerTypeBigIntItem
{
	static final ItemField<IntegerTypeSubItem> itemReference2 = ItemField.create(IntegerTypeSubItem.class, DeletePolicy.NULLIFY).optional();

	static final LongField value2 = new LongField().optional();


	/**
	 * Creates a new IntegerTypeSubItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public IntegerTypeSubItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new IntegerTypeSubItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected IntegerTypeSubItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #itemReference2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final IntegerTypeSubItem getItemReference2()
	{
		return IntegerTypeSubItem.itemReference2.get(this);
	}

	/**
	 * Sets a new value for {@link #itemReference2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setItemReference2(@javax.annotation.Nullable final IntegerTypeSubItem itemReference2)
	{
		IntegerTypeSubItem.itemReference2.set(this,itemReference2);
	}

	/**
	 * Returns the value of {@link #value2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.Long getValue2()
	{
		return IntegerTypeSubItem.value2.get(this);
	}

	/**
	 * Sets a new value for {@link #value2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setValue2(@javax.annotation.Nullable final java.lang.Long value2)
	{
		IntegerTypeSubItem.value2.set(this,value2);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for integerTypeSubItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<IntegerTypeSubItem> TYPE = com.exedio.cope.TypesBound.newType(IntegerTypeSubItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected IntegerTypeSubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
