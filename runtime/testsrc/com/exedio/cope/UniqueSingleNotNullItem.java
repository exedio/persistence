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
 * An item having a unique mandatory attribute.
 * @author Ralf Wiebicke
 */
public final class UniqueSingleNotNullItem extends Item
{
	/**
	 * An attribute that is unique and mandatory.
	 */
	public static final StringField uniqueNotNullString = new StringField().unique();

	/**
	 * Creates a new UniqueSingleNotNullItem with all the fields initially needed.
	 * @param uniqueNotNullString the initial value for field {@link #uniqueNotNullString}.
	 * @throws com.exedio.cope.MandatoryViolationException if uniqueNotNullString is null.
	 * @throws com.exedio.cope.StringLengthViolationException if uniqueNotNullString violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if uniqueNotNullString is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public UniqueSingleNotNullItem(
				@javax.annotation.Nonnull final java.lang.String uniqueNotNullString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(UniqueSingleNotNullItem.uniqueNotNullString,uniqueNotNullString),
		});
	}

	/**
	 * Creates a new UniqueSingleNotNullItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private UniqueSingleNotNullItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #uniqueNotNullString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public java.lang.String getUniqueNotNullString()
	{
		return UniqueSingleNotNullItem.uniqueNotNullString.get(this);
	}

	/**
	 * Sets a new value for {@link #uniqueNotNullString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setUniqueNotNullString(@javax.annotation.Nonnull final java.lang.String uniqueNotNullString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		UniqueSingleNotNullItem.uniqueNotNullString.set(this,uniqueNotNullString);
	}

	/**
	 * Finds a uniqueSingleNotNullItem by its {@link #uniqueNotNullString}.
	 * @param uniqueNotNullString shall be equal to field {@link #uniqueNotNullString}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public static UniqueSingleNotNullItem forUniqueNotNullString(@javax.annotation.Nonnull final java.lang.String uniqueNotNullString)
	{
		return UniqueSingleNotNullItem.uniqueNotNullString.searchUnique(UniqueSingleNotNullItem.class,uniqueNotNullString);
	}

	/**
	 * Finds a uniqueSingleNotNullItem by its {@link #uniqueNotNullString}.
	 * @param uniqueNotNullString shall be equal to field {@link #uniqueNotNullString}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public static UniqueSingleNotNullItem forUniqueNotNullStringStrict(@javax.annotation.Nonnull final java.lang.String uniqueNotNullString)
			throws
				java.lang.IllegalArgumentException
	{
		return UniqueSingleNotNullItem.uniqueNotNullString.searchUniqueStrict(UniqueSingleNotNullItem.class,uniqueNotNullString);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for uniqueSingleNotNullItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<UniqueSingleNotNullItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueSingleNotNullItem.class,UniqueSingleNotNullItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private UniqueSingleNotNullItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
