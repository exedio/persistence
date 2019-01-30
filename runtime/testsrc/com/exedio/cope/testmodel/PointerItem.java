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

package com.exedio.cope.testmodel;

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;

public final class PointerItem extends Item
{

	public static final StringField code = new StringField();

	public static final ItemField<PointerTargetItem> pointer = ItemField.create(PointerTargetItem.class);

	public static final ItemField<PointerTargetItem> pointer2 = ItemField.create(PointerTargetItem.class).optional();

	public static final ItemField<PointerItem> self = ItemField.create(PointerItem.class).optional();

	public static final ItemField<EmptyItem2> empty2 = ItemField.create(EmptyItem2.class).optional();

	/**
	 * Creates a new PointerItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param pointer the initial value for field {@link #pointer}.
	 * @throws com.exedio.cope.MandatoryViolationException if code, pointer is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public PointerItem(
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nonnull final PointerTargetItem pointer)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			PointerItem.code.map(code),
			PointerItem.pointer.map(pointer),
		});
	}

	/**
	 * Creates a new PointerItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private PointerItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public java.lang.String getCode()
	{
		return PointerItem.code.get(this);
	}

	/**
	 * Sets a new value for {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setCode(@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		PointerItem.code.set(this,code);
	}

	/**
	 * Returns the value of {@link #pointer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public PointerTargetItem getPointer()
	{
		return PointerItem.pointer.get(this);
	}

	/**
	 * Sets a new value for {@link #pointer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setPointer(@javax.annotation.Nonnull final PointerTargetItem pointer)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		PointerItem.pointer.set(this,pointer);
	}

	/**
	 * Returns the value of {@link #pointer2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public PointerTargetItem getPointer2()
	{
		return PointerItem.pointer2.get(this);
	}

	/**
	 * Sets a new value for {@link #pointer2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setPointer2(@javax.annotation.Nullable final PointerTargetItem pointer2)
	{
		PointerItem.pointer2.set(this,pointer2);
	}

	/**
	 * Returns the value of {@link #self}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public PointerItem getSelf()
	{
		return PointerItem.self.get(this);
	}

	/**
	 * Sets a new value for {@link #self}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSelf(@javax.annotation.Nullable final PointerItem self)
	{
		PointerItem.self.set(this,self);
	}

	/**
	 * Returns the value of {@link #empty2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public EmptyItem2 getEmpty2()
	{
		return PointerItem.empty2.get(this);
	}

	/**
	 * Sets a new value for {@link #empty2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setEmpty2(@javax.annotation.Nullable final EmptyItem2 empty2)
	{
		PointerItem.empty2.set(this,empty2);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for pointerItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PointerItem> TYPE = com.exedio.cope.TypesBound.newType(PointerItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private PointerItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
