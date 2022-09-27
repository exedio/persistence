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

class HardJoinA1Item extends Item
{
	static final StringField code = new StringField().unique();

	@WrapperInitial
	static final IntegerField a1 = new IntegerField().optional();


	/**
	 * Creates a new HardJoinA1Item with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param a1 the initial value for field {@link #a1}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if code is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	HardJoinA1Item(
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nullable final java.lang.Integer a1)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			HardJoinA1Item.code.map(code),
			HardJoinA1Item.a1.map(a1),
		});
	}

	/**
	 * Creates a new HardJoinA1Item and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected HardJoinA1Item(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getCode()
	{
		return HardJoinA1Item.code.get(this);
	}

	/**
	 * Sets a new value for {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setCode(@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		HardJoinA1Item.code.set(this,code);
	}

	/**
	 * Finds a hardJoinA1Item by its {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final HardJoinA1Item forCode(@javax.annotation.Nonnull final java.lang.String code)
	{
		return HardJoinA1Item.code.searchUnique(HardJoinA1Item.class,code);
	}

	/**
	 * Finds a hardJoinA1Item by its {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final HardJoinA1Item forCodeStrict(@javax.annotation.Nonnull final java.lang.String code)
			throws
				java.lang.IllegalArgumentException
	{
		return HardJoinA1Item.code.searchUniqueStrict(HardJoinA1Item.class,code);
	}

	/**
	 * Returns the value of {@link #a1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.Integer getA1()
	{
		return HardJoinA1Item.a1.get(this);
	}

	/**
	 * Sets a new value for {@link #a1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setA1(@javax.annotation.Nullable final java.lang.Integer a1)
	{
		HardJoinA1Item.a1.set(this,a1);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hardJoinA1Item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HardJoinA1Item> TYPE = com.exedio.cope.TypesBound.newType(HardJoinA1Item.class,HardJoinA1Item::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected HardJoinA1Item(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
