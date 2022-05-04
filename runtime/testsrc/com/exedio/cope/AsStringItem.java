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

final class AsStringItem extends Item
{
	@WrapperInitial static final IntegerField intx = new IntegerField().optional();
	@WrapperInitial static final LongField longx = new LongField().optional();
	@WrapperInitial static final DoubleField doublex = new DoubleField().optional();


	/**
	 * Creates a new AsStringItem with all the fields initially needed.
	 * @param intx the initial value for field {@link #intx}.
	 * @param longx the initial value for field {@link #longx}.
	 * @param doublex the initial value for field {@link #doublex}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AsStringItem(
				@javax.annotation.Nullable final java.lang.Integer intx,
				@javax.annotation.Nullable final java.lang.Long longx,
				@javax.annotation.Nullable final java.lang.Double doublex)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(AsStringItem.intx,intx),
			com.exedio.cope.SetValue.map(AsStringItem.longx,longx),
			com.exedio.cope.SetValue.map(AsStringItem.doublex,doublex),
		});
	}

	/**
	 * Creates a new AsStringItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AsStringItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #intx}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getIntx()
	{
		return AsStringItem.intx.get(this);
	}

	/**
	 * Sets a new value for {@link #intx}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntx(@javax.annotation.Nullable final java.lang.Integer intx)
	{
		AsStringItem.intx.set(this,intx);
	}

	/**
	 * Returns the value of {@link #longx}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Long getLongx()
	{
		return AsStringItem.longx.get(this);
	}

	/**
	 * Sets a new value for {@link #longx}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setLongx(@javax.annotation.Nullable final java.lang.Long longx)
	{
		AsStringItem.longx.set(this,longx);
	}

	/**
	 * Returns the value of {@link #doublex}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Double getDoublex()
	{
		return AsStringItem.doublex.get(this);
	}

	/**
	 * Sets a new value for {@link #doublex}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDoublex(@javax.annotation.Nullable final java.lang.Double doublex)
	{
		AsStringItem.doublex.set(this,doublex);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for asStringItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AsStringItem> TYPE = com.exedio.cope.TypesBound.newType(AsStringItem.class,AsStringItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AsStringItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
