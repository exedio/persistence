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

final class ConnectItem2 extends ConnectItem
{
	static final ItemField<ConnectItem> itemField2 = ItemField.create(ConnectItem.class).optional();
	static final ItemField<ConnectItem2> itemFieldSelf2 = ItemField.create(ConnectItem2.class).optional();
	static final StringField stringField2 = new StringField().optional();

	/**
	 * Creates a new ConnectItem2 with all the fields initially needed.
	 * @param itemField the initial value for field {@link #itemField}.
	 * @param itemFieldSelf the initial value for field {@link #itemFieldSelf}.
	 * @param stringField the initial value for field {@link #stringField}.
	 * @throws com.exedio.cope.MandatoryViolationException if itemField, itemFieldSelf, stringField is null.
	 * @throws com.exedio.cope.StringLengthViolationException if stringField violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ConnectItem2(
				@javax.annotation.Nonnull final com.exedio.cope.ConnectItem2 itemField,
				@javax.annotation.Nonnull final com.exedio.cope.ConnectItem itemFieldSelf,
				@javax.annotation.Nonnull final java.lang.String stringField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.ConnectItem.itemField.map(itemField),
			com.exedio.cope.ConnectItem.itemFieldSelf.map(itemFieldSelf),
			com.exedio.cope.ConnectItem.stringField.map(stringField),
		});
	}

	/**
	 * Creates a new ConnectItem2 and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ConnectItem2(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #itemField2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	ConnectItem getItemField2()
	{
		return ConnectItem2.itemField2.get(this);
	}

	/**
	 * Sets a new value for {@link #itemField2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setItemField2(@javax.annotation.Nullable final ConnectItem itemField2)
	{
		ConnectItem2.itemField2.set(this,itemField2);
	}

	/**
	 * Returns the value of {@link #itemFieldSelf2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	ConnectItem2 getItemFieldSelf2()
	{
		return ConnectItem2.itemFieldSelf2.get(this);
	}

	/**
	 * Sets a new value for {@link #itemFieldSelf2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setItemFieldSelf2(@javax.annotation.Nullable final ConnectItem2 itemFieldSelf2)
	{
		ConnectItem2.itemFieldSelf2.set(this,itemFieldSelf2);
	}

	/**
	 * Returns the value of {@link #stringField2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getStringField2()
	{
		return ConnectItem2.stringField2.get(this);
	}

	/**
	 * Sets a new value for {@link #stringField2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setStringField2(@javax.annotation.Nullable final java.lang.String stringField2)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		ConnectItem2.stringField2.set(this,stringField2);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for connectItem2.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ConnectItem2> TYPE = com.exedio.cope.TypesBound.newType(ConnectItem2.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private ConnectItem2(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
