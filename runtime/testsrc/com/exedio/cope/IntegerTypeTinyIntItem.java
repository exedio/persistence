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

@CopeCreateLimit(127)
public class IntegerTypeTinyIntItem extends Item
{
	static final ItemField<IntegerTypeTinyIntItem> itemReference = ItemField.create(IntegerTypeTinyIntItem.class, DeletePolicy.NULLIFY).optional();

	static final LongField value = new LongField().range(-128L, 127L).optional();


	/**
	 * Creates a new IntegerTypeTinyIntItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public IntegerTypeTinyIntItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new IntegerTypeTinyIntItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected IntegerTypeTinyIntItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #itemReference}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final IntegerTypeTinyIntItem getItemReference()
	{
		return IntegerTypeTinyIntItem.itemReference.get(this);
	}

	/**
	 * Sets a new value for {@link #itemReference}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setItemReference(@javax.annotation.Nullable final IntegerTypeTinyIntItem itemReference)
	{
		IntegerTypeTinyIntItem.itemReference.set(this,itemReference);
	}

	/**
	 * Returns the value of {@link #value}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.Long getValue()
	{
		return IntegerTypeTinyIntItem.value.get(this);
	}

	/**
	 * Sets a new value for {@link #value}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setValue(@javax.annotation.Nullable final java.lang.Long value)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		IntegerTypeTinyIntItem.value.set(this,value);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for integerTypeTinyIntItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<IntegerTypeTinyIntItem> TYPE = com.exedio.cope.TypesBound.newType(IntegerTypeTinyIntItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected IntegerTypeTinyIntItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}