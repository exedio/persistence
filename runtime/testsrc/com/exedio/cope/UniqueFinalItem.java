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

/**
 * An item having a unique final attribute.
 * @author Ralf Wiebicke
 */
public final class UniqueFinalItem extends Item
{
	/**
	 * An attribute that is unique and final.
	 */
	public static final StringField uniqueFinalString = new StringField().toFinal().optional().unique();

	/**
	 * Creates a new UniqueFinalItem with all the fields initially needed.
	 * @param uniqueFinalString the initial value for field {@link #uniqueFinalString}.
	 * @throws com.exedio.cope.StringLengthViolationException if uniqueFinalString violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if uniqueFinalString is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public UniqueFinalItem(
				@javax.annotation.Nullable final java.lang.String uniqueFinalString)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			UniqueFinalItem.uniqueFinalString.map(uniqueFinalString),
		});
	}

	/**
	 * Creates a new UniqueFinalItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private UniqueFinalItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #uniqueFinalString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getUniqueFinalString()
	{
		return UniqueFinalItem.uniqueFinalString.get(this);
	}

	/**
	 * Finds a uniqueFinalItem by it's {@link #uniqueFinalString}.
	 * @param uniqueFinalString shall be equal to field {@link #uniqueFinalString}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public static UniqueFinalItem forUniqueFinalString(@javax.annotation.Nonnull final java.lang.String uniqueFinalString)
	{
		return UniqueFinalItem.uniqueFinalString.searchUnique(UniqueFinalItem.class,uniqueFinalString);
	}

	/**
	 * Finds a uniqueFinalItem by its {@link #uniqueFinalString}.
	 * @param uniqueFinalString shall be equal to field {@link #uniqueFinalString}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public static UniqueFinalItem forUniqueFinalStringStrict(@javax.annotation.Nonnull final java.lang.String uniqueFinalString)
			throws
				java.lang.IllegalArgumentException
	{
		return UniqueFinalItem.uniqueFinalString.searchUniqueStrict(UniqueFinalItem.class,uniqueFinalString);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for uniqueFinalItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<UniqueFinalItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueFinalItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private UniqueFinalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
