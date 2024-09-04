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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	HiddenFeatureSubItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new HiddenFeatureSubItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private HiddenFeatureSubItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #nonHiddenSub}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getNonHiddenSub()
	{
		return HiddenFeatureSubItem.nonHiddenSub.get(this);
	}

	/**
	 * Sets a new value for {@link #nonHiddenSub}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNonHiddenSub(@javax.annotation.Nullable final java.lang.String nonHiddenSub)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HiddenFeatureSubItem.nonHiddenSub.set(this,nonHiddenSub);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hiddenFeatureSubItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HiddenFeatureSubItem> TYPE = com.exedio.cope.TypesBound.newType(HiddenFeatureSubItem.class,HiddenFeatureSubItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private HiddenFeatureSubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
