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

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;

/**
 * An item having a final attribute.
 * @author Ralf Wiebicke
 */
public final class FinalItem extends Item
{
	public static final StringField finalString = new StringField().toFinal();
	public static final IntegerField nonFinalInteger = new IntegerField();

	/**
	 * Creates a new FinalItem with all the fields initially needed.
	 * @param finalString the initial value for field {@link #finalString}.
	 * @param nonFinalInteger the initial value for field {@link #nonFinalInteger}.
	 * @throws com.exedio.cope.MandatoryViolationException if finalString is null.
	 * @throws com.exedio.cope.StringLengthViolationException if finalString violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public FinalItem(
				@javax.annotation.Nonnull final java.lang.String finalString,
				final int nonFinalInteger)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			FinalItem.finalString.map(finalString),
			FinalItem.nonFinalInteger.map(nonFinalInteger),
		});
	}

	/**
	 * Creates a new FinalItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private FinalItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #finalString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	public final java.lang.String getFinalString()
	{
		return FinalItem.finalString.get(this);
	}

	/**
	 * Returns the value of {@link #nonFinalInteger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public final int getNonFinalInteger()
	{
		return FinalItem.nonFinalInteger.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #nonFinalInteger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setNonFinalInteger(final int nonFinalInteger)
	{
		FinalItem.nonFinalInteger.set(this,nonFinalInteger);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for finalItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<FinalItem> TYPE = com.exedio.cope.TypesBound.newType(FinalItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private FinalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}