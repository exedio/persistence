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

@CopeName("Main")
public final class DeleteAfterUniqueViolationItem extends Item
{
	static final StringField uniqueString = new StringField().optional().unique();

	public static final StringField name = new StringField();

	@SuppressWarnings("unused")
	DeleteAfterUniqueViolationItem(final String name, final double dummy)
	{
		this(new com.exedio.cope.SetValue<?>[]{
				DeleteAfterUniqueViolationItem.uniqueString.map(name),
				DeleteAfterUniqueViolationItem.name.map(name),
		});
	}

	/**
	 * Creates a new DeleteAfterUniqueViolationItem with all the fields initially needed.
	 * @param name the initial value for field {@link #name}.
	 * @throws com.exedio.cope.MandatoryViolationException if name is null.
	 * @throws com.exedio.cope.StringLengthViolationException if name violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public DeleteAfterUniqueViolationItem(
				@javax.annotation.Nonnull final java.lang.String name)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DeleteAfterUniqueViolationItem.name.map(name),
		});
	}

	/**
	 * Creates a new DeleteAfterUniqueViolationItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DeleteAfterUniqueViolationItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #uniqueString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getUniqueString()
	{
		return DeleteAfterUniqueViolationItem.uniqueString.get(this);
	}

	/**
	 * Sets a new value for {@link #uniqueString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setUniqueString(@javax.annotation.Nullable final java.lang.String uniqueString)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		DeleteAfterUniqueViolationItem.uniqueString.set(this,uniqueString);
	}

	/**
	 * Finds a deleteAfterUniqueViolationItem by it's {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	static DeleteAfterUniqueViolationItem forUniqueString(@javax.annotation.Nonnull final java.lang.String uniqueString)
	{
		return DeleteAfterUniqueViolationItem.uniqueString.searchUnique(DeleteAfterUniqueViolationItem.class,uniqueString);
	}

	/**
	 * Finds a deleteAfterUniqueViolationItem by its {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forStrict")
	@javax.annotation.Nonnull
	static DeleteAfterUniqueViolationItem forUniqueStringStrict(@javax.annotation.Nonnull final java.lang.String uniqueString)
			throws
				java.lang.IllegalArgumentException
	{
		return DeleteAfterUniqueViolationItem.uniqueString.searchUniqueStrict(DeleteAfterUniqueViolationItem.class,uniqueString);
	}

	/**
	 * Returns the value of {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public java.lang.String getName()
	{
		return DeleteAfterUniqueViolationItem.name.get(this);
	}

	/**
	 * Sets a new value for {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setName(@javax.annotation.Nonnull final java.lang.String name)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		DeleteAfterUniqueViolationItem.name.set(this,name);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for deleteAfterUniqueViolationItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DeleteAfterUniqueViolationItem> TYPE = com.exedio.cope.TypesBound.newType(DeleteAfterUniqueViolationItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private DeleteAfterUniqueViolationItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
