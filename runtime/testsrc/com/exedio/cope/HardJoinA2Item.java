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

class HardJoinA2Item extends HardJoinA1Item
{
	@WrapperInitial
	static final IntegerField a2 = new IntegerField().optional();


	/**
	 * Creates a new HardJoinA2Item with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param a1 the initial value for field {@link #a1}.
	 * @param a2 the initial value for field {@link #a2}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if code is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	HardJoinA2Item(
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nullable final java.lang.Integer a1,
				@javax.annotation.Nullable final java.lang.Integer a2)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.HardJoinA1Item.code,code),
			com.exedio.cope.SetValue.map(com.exedio.cope.HardJoinA1Item.a1,a1),
			com.exedio.cope.SetValue.map(HardJoinA2Item.a2,a2),
		});
	}

	/**
	 * Creates a new HardJoinA2Item and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected HardJoinA2Item(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #a2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.Integer getA2()
	{
		return HardJoinA2Item.a2.get(this);
	}

	/**
	 * Sets a new value for {@link #a2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setA2(@javax.annotation.Nullable final java.lang.Integer a2)
	{
		HardJoinA2Item.a2.set(this,a2);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hardJoinA2Item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HardJoinA2Item> TYPE = com.exedio.cope.TypesBound.newType(HardJoinA2Item.class,HardJoinA2Item::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected HardJoinA2Item(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
