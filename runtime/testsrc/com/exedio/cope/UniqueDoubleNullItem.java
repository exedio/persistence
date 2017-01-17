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

import com.exedio.cope.instrument.WrapperInitial;

final class UniqueDoubleNullItem extends Item
{
	@WrapperInitial static final StringField string = new StringField().optional();

	@WrapperInitial static final IntegerField integer = new IntegerField().optional();

	static final UniqueConstraint constraint = new UniqueConstraint(string, integer);

	/**
	 * Creates a new UniqueDoubleNullItem with all the fields initially needed.
	 * @param string the initial value for field {@link #string}.
	 * @param integer the initial value for field {@link #integer}.
	 * @throws com.exedio.cope.StringLengthViolationException if string violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if string, integer is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	UniqueDoubleNullItem(
				@javax.annotation.Nullable final java.lang.String string,
				@javax.annotation.Nullable final java.lang.Integer integer)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			UniqueDoubleNullItem.string.map(string),
			UniqueDoubleNullItem.integer.map(integer),
		});
	}

	/**
	 * Creates a new UniqueDoubleNullItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private UniqueDoubleNullItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getString()
	{
		return UniqueDoubleNullItem.string.get(this);
	}

	/**
	 * Sets a new value for {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setString(@javax.annotation.Nullable final java.lang.String string)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		UniqueDoubleNullItem.string.set(this,string);
	}

	/**
	 * Returns the value of {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.Integer getInteger()
	{
		return UniqueDoubleNullItem.integer.get(this);
	}

	/**
	 * Sets a new value for {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setInteger(@javax.annotation.Nullable final java.lang.Integer integer)
			throws
				com.exedio.cope.UniqueViolationException
	{
		UniqueDoubleNullItem.integer.set(this,integer);
	}

	/**
	 * Finds a uniqueDoubleNullItem by it's unique fields.
	 * @param string shall be equal to field {@link #string}.
	 * @param integer shall be equal to field {@link #integer}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
	@javax.annotation.Nullable
	static final UniqueDoubleNullItem forConstraint(@javax.annotation.Nonnull final java.lang.String string,@javax.annotation.Nonnull final java.lang.Integer integer)
	{
		return UniqueDoubleNullItem.constraint.search(UniqueDoubleNullItem.class,string,integer);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for uniqueDoubleNullItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<UniqueDoubleNullItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueDoubleNullItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private UniqueDoubleNullItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
