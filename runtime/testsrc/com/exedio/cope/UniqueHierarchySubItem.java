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

final class UniqueHierarchySubItem extends UniqueHierarchySuperItem
{
	static final StringField subField = new StringField().unique();

	@Override
	public String toString()
	{
		return getSubField();
	}

	/**
	 * Creates a new UniqueHierarchySubItem with all the fields initially needed.
	 * @param superField the initial value for field {@link #superField}.
	 * @param subField the initial value for field {@link #subField}.
	 * @throws com.exedio.cope.MandatoryViolationException if superField, subField is null.
	 * @throws com.exedio.cope.StringLengthViolationException if superField, subField violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if subField is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	UniqueHierarchySubItem(
				@javax.annotation.Nonnull final java.lang.String superField,
				@javax.annotation.Nonnull final java.lang.String subField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.UniqueHierarchySuperItem.superField.map(superField),
			UniqueHierarchySubItem.subField.map(subField),
		});
	}

	/**
	 * Creates a new UniqueHierarchySubItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private UniqueHierarchySubItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #subField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getSubField()
	{
		return UniqueHierarchySubItem.subField.get(this);
	}

	/**
	 * Sets a new value for {@link #subField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSubField(@javax.annotation.Nonnull final java.lang.String subField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		UniqueHierarchySubItem.subField.set(this,subField);
	}

	/**
	 * Finds a uniqueHierarchySubItem by its {@link #subField}.
	 * @param subField shall be equal to field {@link #subField}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static UniqueHierarchySubItem forSubField(@javax.annotation.Nonnull final java.lang.String subField)
	{
		return UniqueHierarchySubItem.subField.searchUnique(UniqueHierarchySubItem.class,subField);
	}

	/**
	 * Finds a uniqueHierarchySubItem by its {@link #subField}.
	 * @param subField shall be equal to field {@link #subField}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static UniqueHierarchySubItem forSubFieldStrict(@javax.annotation.Nonnull final java.lang.String subField)
			throws
				java.lang.IllegalArgumentException
	{
		return UniqueHierarchySubItem.subField.searchUniqueStrict(UniqueHierarchySubItem.class,subField);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for uniqueHierarchySubItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<UniqueHierarchySubItem> TYPE = com.exedio.cope.TypesBound.newType(UniqueHierarchySubItem.class,UniqueHierarchySubItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private UniqueHierarchySubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
