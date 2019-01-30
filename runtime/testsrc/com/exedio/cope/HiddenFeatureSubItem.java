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

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.instrument.Wrapper;

final class HiddenFeatureSubItem extends HiddenFeatureSuperItem
{
	static final StringField nonHiddenSub = new StringField().optional();

	@Wrapper(wrap="get", visibility=NONE)
	@Wrapper(wrap="set", visibility=NONE)
	static final StringField hiddenSame = new StringField().optional();

	@Wrapper(wrap="get", visibility=NONE)
	@Wrapper(wrap="set", visibility=NONE)
	static final IntegerField hiddenOther = new IntegerField().optional();

	/**
	 * Creates a new HiddenFeatureSubItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	HiddenFeatureSubItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new HiddenFeatureSubItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private HiddenFeatureSubItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #nonHiddenSub}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getNonHiddenSub()
	{
		return HiddenFeatureSubItem.nonHiddenSub.get(this);
	}

	/**
	 * Sets a new value for {@link #nonHiddenSub}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNonHiddenSub(@javax.annotation.Nullable final java.lang.String nonHiddenSub)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HiddenFeatureSubItem.nonHiddenSub.set(this,nonHiddenSub);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hiddenFeatureSubItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HiddenFeatureSubItem> TYPE = com.exedio.cope.TypesBound.newType(HiddenFeatureSubItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private HiddenFeatureSubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
