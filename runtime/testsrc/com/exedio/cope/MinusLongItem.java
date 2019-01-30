
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

public final class MinusLongItem extends Item
{
	public static final LongField numA = new LongField().optional();

	public static final LongField numB = new LongField().optional();

	public static final LongField numC = new LongField().optional();

	public static final MinusView<Long> viewAB = numA.minus(numB);

	public static final MinusView<Long> viewAC = numA.minus(numC);

	public static final MinusView<Long> viewBC = numB.minus(numC);

	public MinusLongItem(final long initialNumA, final long initialNumB, final long initialNumC)
	{
		super(new SetValue<?>[]{
			numA.map(initialNumA),
			numB.map(initialNumB),
			numC.map(initialNumC),
		});
	}

	/**
	 * Creates a new MinusLongItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public MinusLongItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new MinusLongItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private MinusLongItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #numA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Long getNumA()
	{
		return MinusLongItem.numA.get(this);
	}

	/**
	 * Sets a new value for {@link #numA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumA(@javax.annotation.Nullable final java.lang.Long numA)
	{
		MinusLongItem.numA.set(this,numA);
	}

	/**
	 * Returns the value of {@link #numB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Long getNumB()
	{
		return MinusLongItem.numB.get(this);
	}

	/**
	 * Sets a new value for {@link #numB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumB(@javax.annotation.Nullable final java.lang.Long numB)
	{
		MinusLongItem.numB.set(this,numB);
	}

	/**
	 * Returns the value of {@link #numC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Long getNumC()
	{
		return MinusLongItem.numC.get(this);
	}

	/**
	 * Sets a new value for {@link #numC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumC(@javax.annotation.Nullable final java.lang.Long numC)
	{
		MinusLongItem.numC.set(this,numC);
	}

	/**
	 * Returns the value of {@link #viewAB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Long getViewAB()
	{
		return MinusLongItem.viewAB.get(this);
	}

	/**
	 * Returns the value of {@link #viewAC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Long getViewAC()
	{
		return MinusLongItem.viewAC.get(this);
	}

	/**
	 * Returns the value of {@link #viewBC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Long getViewBC()
	{
		return MinusLongItem.viewBC.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for minusLongItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MinusLongItem> TYPE = com.exedio.cope.TypesBound.newType(MinusLongItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private MinusLongItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
