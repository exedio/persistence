
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

public final class DivideDoubleItem extends Item
{
	public static final DoubleField numA = new DoubleField().optional();

	public static final DoubleField numB = new DoubleField().optional();

	public static final DoubleField numC = new DoubleField().optional();

	public static final DivideView<Double> divideAB = numA.divide(numB);

	public static final DivideView<Double> divideAC = numA.divide(numC);

	public static final DivideView<Double> divideBC = numB.divide(numC);

	public DivideDoubleItem(final double initialNumA, final double initialNumB, final double initialNumC)
	{
		super(new SetValue<?>[]{
			numA.map(initialNumA),
			numB.map(initialNumB),
			numC.map(initialNumC),
		});
	}

	/**
	 * Creates a new DivideDoubleItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public DivideDoubleItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new DivideDoubleItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DivideDoubleItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #numA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Double getNumA()
	{
		return DivideDoubleItem.numA.get(this);
	}

	/**
	 * Sets a new value for {@link #numA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumA(@javax.annotation.Nullable final java.lang.Double numA)
	{
		DivideDoubleItem.numA.set(this,numA);
	}

	/**
	 * Returns the value of {@link #numB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Double getNumB()
	{
		return DivideDoubleItem.numB.get(this);
	}

	/**
	 * Sets a new value for {@link #numB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumB(@javax.annotation.Nullable final java.lang.Double numB)
	{
		DivideDoubleItem.numB.set(this,numB);
	}

	/**
	 * Returns the value of {@link #numC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Double getNumC()
	{
		return DivideDoubleItem.numC.get(this);
	}

	/**
	 * Sets a new value for {@link #numC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumC(@javax.annotation.Nullable final java.lang.Double numC)
	{
		DivideDoubleItem.numC.set(this,numC);
	}

	/**
	 * Returns the value of {@link #divideAB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getDivideAB()
	{
		return DivideDoubleItem.divideAB.get(this);
	}

	/**
	 * Returns the value of {@link #divideAC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getDivideAC()
	{
		return DivideDoubleItem.divideAC.get(this);
	}

	/**
	 * Returns the value of {@link #divideBC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getDivideBC()
	{
		return DivideDoubleItem.divideBC.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for divideDoubleItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DivideDoubleItem> TYPE = com.exedio.cope.TypesBound.newType(DivideDoubleItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private DivideDoubleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
