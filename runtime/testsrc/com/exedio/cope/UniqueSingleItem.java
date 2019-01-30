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

public final class UniqueSingleItem extends Item
{
	public static final StringField uniqueString = new StringField().optional().unique();

	public static final StringField otherString = new StringField().optional();

	public UniqueSingleItem(final String uniqueString)
	{
		this(
			UniqueSingleItem.uniqueString.map(uniqueString)
		);
	}

	public UniqueSingleItem(final String uniqueString, final String otherString)
	{
		this(
			UniqueSingleItem.uniqueString.map(uniqueString),
			UniqueSingleItem.otherString.map(otherString)
		);
	}

	/**
	 * Creates a new UniqueSingleItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public UniqueSingleItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new UniqueSingleItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private UniqueSingleItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #uniqueString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.String getUniqueString()
	{
		return UniqueSingleItem.uniqueString.get(this);
	}

	/**
	 * Sets a new value for {@link #uniqueString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setUniqueString(@javax.annotation.Nullable final java.lang.String uniqueString)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		UniqueSingleItem.uniqueString.set(this,uniqueString);
	}

	/**
	 * Finds a uniqueSingleItem by it's {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	public static UniqueSingleItem forUniqueString(@javax.annotation.Nonnull final java.lang.String uniqueString)
	{
		return UniqueSingleItem.uniqueString.searchUnique(UniqueSingleItem.class,uniqueString);
	}

	/**
	 * Finds a uniqueSingleItem by its {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forStrict")
	@javax.annotation.Nonnull
	public static UniqueSingleItem forUniqueStringStrict(@javax.annotation.Nonnull final java.lang.String uniqueString)
			throws
				java.lang.IllegalArgumentException
	{
		return UniqueSingleItem.uniqueString.searchUniqueStrict(UniqueSingleItem.class,uniqueString);
	}

	/**
	 * Returns the value of {@link #otherString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.String getOtherString()
	{
		return UniqueSingleItem.otherString.get(this);
	}

	/**
	 * Sets a new value for {@link #otherString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setOtherString(@javax.annotation.Nullable final java.lang.String otherString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		UniqueSingleItem.otherString.set(this,otherString);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for uniqueSingleItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<UniqueSingleItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueSingleItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private UniqueSingleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
