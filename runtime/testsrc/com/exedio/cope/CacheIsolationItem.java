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

public final class CacheIsolationItem extends Item
{
	static final StringField uniqueString = new StringField().optional().unique();

	public static final StringField name = new StringField();

	/**
	 * Creates a new CacheIsolationItem with all the fields initially needed.
	 * @param name the initial value for field {@link #name}.
	 * @throws com.exedio.cope.MandatoryViolationException if name is null.
	 * @throws com.exedio.cope.StringLengthViolationException if name violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public CacheIsolationItem(
				@javax.annotation.Nonnull final java.lang.String name)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CacheIsolationItem.name.map(name),
		});
	}

	/**
	 * Creates a new CacheIsolationItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CacheIsolationItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #uniqueString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getUniqueString()
	{
		return CacheIsolationItem.uniqueString.get(this);
	}

	/**
	 * Sets a new value for {@link #uniqueString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setUniqueString(@javax.annotation.Nullable final java.lang.String uniqueString)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		CacheIsolationItem.uniqueString.set(this,uniqueString);
	}

	/**
	 * Finds a cacheIsolationItem by it's {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	static final CacheIsolationItem forUniqueString(@javax.annotation.Nonnull final java.lang.String uniqueString)
	{
		return CacheIsolationItem.uniqueString.searchUnique(CacheIsolationItem.class,uniqueString);
	}

	/**
	 * Finds a cacheIsolationItem by its {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forStrict")
	@javax.annotation.Nonnull
	static final CacheIsolationItem forUniqueStringStrict(@javax.annotation.Nonnull final java.lang.String uniqueString)
			throws
				java.lang.IllegalArgumentException
	{
		return CacheIsolationItem.uniqueString.searchUniqueStrict(CacheIsolationItem.class,uniqueString);
	}

	/**
	 * Returns the value of {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public final java.lang.String getName()
	{
		return CacheIsolationItem.name.get(this);
	}

	/**
	 * Sets a new value for {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setName(@javax.annotation.Nonnull final java.lang.String name)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		CacheIsolationItem.name.set(this,name);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for cacheIsolationItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CacheIsolationItem> TYPE = com.exedio.cope.TypesBound.newType(CacheIsolationItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private CacheIsolationItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}