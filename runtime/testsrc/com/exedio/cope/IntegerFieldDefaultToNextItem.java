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

import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.instrument.WrapperType;

@WrapperType(genericConstructor=PACKAGE)
@CopeSchemaName("DefaultToItem")
final class IntegerFieldDefaultToNextItem extends Item
{
	static final IntegerField next = new IntegerField().optional().defaultToNext(10001);
	static final IntegerField none = new IntegerField().optional();

	/**
	 * Creates a new IntegerFieldDefaultToNextItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	IntegerFieldDefaultToNextItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new IntegerFieldDefaultToNextItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	IntegerFieldDefaultToNextItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #next}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getNext()
	{
		return IntegerFieldDefaultToNextItem.next.get(this);
	}

	/**
	 * Sets a new value for {@link #next}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNext(@javax.annotation.Nullable final java.lang.Integer next)
	{
		IntegerFieldDefaultToNextItem.next.set(this,next);
	}

	/**
	 * Returns the value of {@link #none}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getNone()
	{
		return IntegerFieldDefaultToNextItem.none.get(this);
	}

	/**
	 * Sets a new value for {@link #none}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNone(@javax.annotation.Nullable final java.lang.Integer none)
	{
		IntegerFieldDefaultToNextItem.none.set(this,none);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for integerFieldDefaultToNextItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<IntegerFieldDefaultToNextItem> TYPE = com.exedio.cope.TypesBound.newType(IntegerFieldDefaultToNextItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private IntegerFieldDefaultToNextItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}