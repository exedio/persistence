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
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AsStringItem(
				@javax.annotation.Nullable final java.lang.Integer intx,
				@javax.annotation.Nullable final java.lang.Long longx,
				@javax.annotation.Nullable final java.lang.Double doublex)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AsStringItem.intx.map(intx),
			AsStringItem.longx.map(longx),
			AsStringItem.doublex.map(doublex),
		});
	}

	/**
	 * Creates a new AsStringItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AsStringItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #intx}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getIntx()
	{
		return AsStringItem.intx.get(this);
	}

	/**
	 * Sets a new value for {@link #intx}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setIntx(@javax.annotation.Nullable final java.lang.Integer intx)
	{
		AsStringItem.intx.set(this,intx);
	}

	/**
	 * Returns the value of {@link #longx}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Long getLongx()
	{
		return AsStringItem.longx.get(this);
	}

	/**
	 * Sets a new value for {@link #longx}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setLongx(@javax.annotation.Nullable final java.lang.Long longx)
	{
		AsStringItem.longx.set(this,longx);
	}

	/**
	 * Returns the value of {@link #doublex}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Double getDoublex()
	{
		return AsStringItem.doublex.get(this);
	}

	/**
	 * Sets a new value for {@link #doublex}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDoublex(@javax.annotation.Nullable final java.lang.Double doublex)
	{
		AsStringItem.doublex.set(this,doublex);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for asStringItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AsStringItem> TYPE = com.exedio.cope.TypesBound.newType(AsStringItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AsStringItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
