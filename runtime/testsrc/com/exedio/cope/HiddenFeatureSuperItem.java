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

class HiddenFeatureSuperItem extends Item
{
	static final StringField nonHiddenSuper = new StringField().optional();
	static final StringField hiddenSame = new StringField().optional();
	static final StringField hiddenOther = new StringField().optional();

	/**
	 * Creates a new HiddenFeatureSuperItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	HiddenFeatureSuperItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new HiddenFeatureSuperItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected HiddenFeatureSuperItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #nonHiddenSuper}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getNonHiddenSuper()
	{
		return HiddenFeatureSuperItem.nonHiddenSuper.get(this);
	}

	/**
	 * Sets a new value for {@link #nonHiddenSuper}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setNonHiddenSuper(@javax.annotation.Nullable final java.lang.String nonHiddenSuper)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HiddenFeatureSuperItem.nonHiddenSuper.set(this,nonHiddenSuper);
	}

	/**
	 * Returns the value of {@link #hiddenSame}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getHiddenSame()
	{
		return HiddenFeatureSuperItem.hiddenSame.get(this);
	}

	/**
	 * Sets a new value for {@link #hiddenSame}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setHiddenSame(@javax.annotation.Nullable final java.lang.String hiddenSame)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HiddenFeatureSuperItem.hiddenSame.set(this,hiddenSame);
	}

	/**
	 * Returns the value of {@link #hiddenOther}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getHiddenOther()
	{
		return HiddenFeatureSuperItem.hiddenOther.get(this);
	}

	/**
	 * Sets a new value for {@link #hiddenOther}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setHiddenOther(@javax.annotation.Nullable final java.lang.String hiddenOther)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HiddenFeatureSuperItem.hiddenOther.set(this,hiddenOther);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hiddenFeatureSuperItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HiddenFeatureSuperItem> TYPE = com.exedio.cope.TypesBound.newType(HiddenFeatureSuperItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected HiddenFeatureSuperItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
