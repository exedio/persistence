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

package com.exedio.cope.reflect;

import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.WrapperInitial;

public final class TypeFieldItem extends Item
{
	static final TypeField<Item> standard = TypeField.create();
	static final TypeField<Item> isFinal  = TypeField.create().toFinal();
	static final TypeField<Item> optional = TypeField.create().optional();
	static final TypeField<Item> unique   = TypeField.create().optional().unique();
	@CopeSchemaName("newname")
	static final TypeField<Item> renamed = TypeField.create().optional();
	@WrapperInitial
	static final TypeField<TypeFieldSubItem> restricted = TypeField.create(TypeFieldSubItem.class).optional();


	@SuppressWarnings({"unchecked", "UnusedReturnValue"}) // OK: test bad API usage
	static TypeFieldItem createRestrictedRaw(final Type<?> restricted)
	{
		return new TypeFieldItem(new SetValue<?>[]{
			TypeFieldItem.standard.map(TYPE),
			TypeFieldItem.isFinal.map(TYPE),
			TypeFieldItem.restricted.map((Type)restricted),
		});
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	void setRestrictedRaw(final Type<?> restricted)
	{
		TypeFieldItem.restricted.set(this, (Type)restricted);
	}


	/**
	 * Creates a new TypeFieldItem with all the fields initially needed.
	 * @param standard the initial value for field {@link #standard}.
	 * @param isFinal the initial value for field {@link #isFinal}.
	 * @param restricted the initial value for field {@link #restricted}.
	 * @throws com.exedio.cope.MandatoryViolationException if standard, isFinal is null.
	 * @throws com.exedio.cope.StringLengthViolationException if standard, isFinal, restricted violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	TypeFieldItem(
				@javax.annotation.Nonnull final com.exedio.cope.Type<? extends Item> standard,
				@javax.annotation.Nonnull final com.exedio.cope.Type<? extends Item> isFinal,
				@javax.annotation.Nullable final com.exedio.cope.Type<? extends TypeFieldSubItem> restricted)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			TypeFieldItem.standard.map(standard),
			TypeFieldItem.isFinal.map(isFinal),
			TypeFieldItem.restricted.map(restricted),
		});
	}

	/**
	 * Creates a new TypeFieldItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private TypeFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #standard}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	com.exedio.cope.Type<? extends Item> getStandard()
	{
		return TypeFieldItem.standard.get(this);
	}

	/**
	 * Sets a new value for {@link #standard}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setStandard(@javax.annotation.Nonnull final com.exedio.cope.Type<? extends Item> standard)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		TypeFieldItem.standard.set(this,standard);
	}

	/**
	 * Returns the value of {@link #isFinal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	com.exedio.cope.Type<? extends Item> getIsFinal()
	{
		return TypeFieldItem.isFinal.get(this);
	}

	/**
	 * Returns the value of {@link #optional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.Type<? extends Item> getOptional()
	{
		return TypeFieldItem.optional.get(this);
	}

	/**
	 * Sets a new value for {@link #optional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOptional(@javax.annotation.Nullable final com.exedio.cope.Type<? extends Item> optional)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		TypeFieldItem.optional.set(this,optional);
	}

	/**
	 * Returns the value of {@link #unique}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.Type<? extends Item> getUnique()
	{
		return TypeFieldItem.unique.get(this);
	}

	/**
	 * Sets a new value for {@link #unique}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setUnique(@javax.annotation.Nullable final com.exedio.cope.Type<? extends Item> unique)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		TypeFieldItem.unique.set(this,unique);
	}

	/**
	 * Finds a typeFieldItem by it's {@link #unique}.
	 * @param unique shall be equal to field {@link #unique}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	static TypeFieldItem forUnique(@javax.annotation.Nonnull final com.exedio.cope.Type<? extends Item> unique)
	{
		return TypeFieldItem.unique.searchUnique(TypeFieldItem.class,unique);
	}

	/**
	 * Returns the value of {@link #renamed}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.Type<? extends Item> getRenamed()
	{
		return TypeFieldItem.renamed.get(this);
	}

	/**
	 * Sets a new value for {@link #renamed}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setRenamed(@javax.annotation.Nullable final com.exedio.cope.Type<? extends Item> renamed)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		TypeFieldItem.renamed.set(this,renamed);
	}

	/**
	 * Returns the value of {@link #restricted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.Type<? extends TypeFieldSubItem> getRestricted()
	{
		return TypeFieldItem.restricted.get(this);
	}

	/**
	 * Sets a new value for {@link #restricted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setRestricted(@javax.annotation.Nullable final com.exedio.cope.Type<? extends TypeFieldSubItem> restricted)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		TypeFieldItem.restricted.set(this,restricted);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for typeFieldItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<TypeFieldItem> TYPE = com.exedio.cope.TypesBound.newType(TypeFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private TypeFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
