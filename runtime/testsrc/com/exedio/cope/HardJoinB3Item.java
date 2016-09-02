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

final class HardJoinB3Item extends HardJoinB2Item
{
	@WrapperInitial
	static final IntegerField b3 = new IntegerField().optional();


	/**

	 **
	 * Creates a new HardJoinB3Item with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param b1 the initial value for field {@link #b1}.
	 * @param b2 the initial value for field {@link #b2}.
	 * @param b3 the initial value for field {@link #b3}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if code is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	HardJoinB3Item(
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nullable final java.lang.Integer b1,
				@javax.annotation.Nullable final java.lang.Integer b2,
				@javax.annotation.Nullable final java.lang.Integer b3)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.HardJoinB1Item.code.map(code),
			com.exedio.cope.HardJoinB1Item.b1.map(b1),
			com.exedio.cope.HardJoinB2Item.b2.map(b2),
			HardJoinB3Item.b3.map(b3),
		});
	}/**

	 **
	 * Creates a new HardJoinB3Item and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private HardJoinB3Item(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #b3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.Integer getB3()
	{
		return HardJoinB3Item.b3.get(this);
	}/**

	 **
	 * Sets a new value for {@link #b3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setB3(@javax.annotation.Nullable final java.lang.Integer b3)
	{
		HardJoinB3Item.b3.set(this,b3);
	}/**

	 **
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for hardJoinB3Item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HardJoinB3Item> TYPE = com.exedio.cope.TypesBound.newType(HardJoinB3Item.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private HardJoinB3Item(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
