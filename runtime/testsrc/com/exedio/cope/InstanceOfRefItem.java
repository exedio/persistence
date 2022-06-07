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

import static com.exedio.cope.instrument.Visibility.PRIVATE;

import com.exedio.cope.instrument.WrapperType;

@WrapperType(constructor=PRIVATE)
final class InstanceOfRefItem extends Item
{
	static final ItemField<InstanceOfAItem> ref = ItemField.create(InstanceOfAItem.class).toFinal().optional();
	static final StringField code = new StringField().toFinal().unique();
	static final ItemField<InstanceOfB2Item> refb2 = ItemField.create(InstanceOfB2Item.class).optional();

	@Override
	public String toString()
	{
		return getCode();
	}

	InstanceOfRefItem(final InstanceOfAItem ref)
	{
		this(ref, "->"+(ref!=null ? ref.getCode() : "NULL"));
	}

	/**
	 * Creates a new InstanceOfRefItem with all the fields initially needed.
	 * @param ref the initial value for field {@link #ref}.
	 * @param code the initial value for field {@link #code}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if code is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private InstanceOfRefItem(
				@javax.annotation.Nullable final InstanceOfAItem ref,
				@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			InstanceOfRefItem.ref.map(ref),
			InstanceOfRefItem.code.map(code),
		});
	}

	/**
	 * Creates a new InstanceOfRefItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private InstanceOfRefItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #ref}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	InstanceOfAItem getRef()
	{
		return InstanceOfRefItem.ref.get(this);
	}

	/**
	 * Returns the value of {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getCode()
	{
		return InstanceOfRefItem.code.get(this);
	}

	/**
	 * Finds a instanceOfRefItem by its {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static InstanceOfRefItem forCode(@javax.annotation.Nonnull final java.lang.String code)
	{
		return InstanceOfRefItem.code.searchUnique(InstanceOfRefItem.class,code);
	}

	/**
	 * Finds a instanceOfRefItem by its {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static InstanceOfRefItem forCodeStrict(@javax.annotation.Nonnull final java.lang.String code)
			throws
				java.lang.IllegalArgumentException
	{
		return InstanceOfRefItem.code.searchUniqueStrict(InstanceOfRefItem.class,code);
	}

	/**
	 * Returns the value of {@link #refb2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	InstanceOfB2Item getRefb2()
	{
		return InstanceOfRefItem.refb2.get(this);
	}

	/**
	 * Sets a new value for {@link #refb2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setRefb2(@javax.annotation.Nullable final InstanceOfB2Item refb2)
	{
		InstanceOfRefItem.refb2.set(this,refb2);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for instanceOfRefItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<InstanceOfRefItem> TYPE = com.exedio.cope.TypesBound.newType(InstanceOfRefItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private InstanceOfRefItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
