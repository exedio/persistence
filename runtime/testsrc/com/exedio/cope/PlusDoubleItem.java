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


public final class PlusDoubleItem extends Item
{
	public static final DoubleField numA = new DoubleField().optional();

	public static final DoubleField numB = new DoubleField().optional();

	public static final DoubleField numC = new DoubleField().optional();

	public static final PlusLiteralView<Double> plusA9 = PlusLiteralView.plus(numA, Double.valueOf(9.3));

	public static final PlusView<Double> plusAB = numA.plus(numB);

	public static final PlusView<Double> plusAC = numA.plus(numC);

	public static final PlusView<Double> plusBC = numB.plus(numC);

	public static final PlusView<Double> plusABC = Cope.plus(numA, numB, numC);

	public static final PlusView<Double> plusABaC = plusAB.plus(numC);

	public static final MultiplyLiteralView<Double> multiplyB9 = MultiplyLiteralView.multiply(numB, Double.valueOf(9.3));

	public static final MultiplyView<Double> multiplyBC = numB.multiply(numC);

	public PlusDoubleItem(final double initialNumA, final double initialNumB, final double initialNumC)
	{
		super(new SetValue<?>[]{
			numA.map(initialNumA),
			numB.map(initialNumB),
			numC.map(initialNumC),
		});
	}

	/**
	 * Creates a new PlusDoubleItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public PlusDoubleItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new PlusDoubleItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private PlusDoubleItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #numA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Double getNumA()
	{
		return PlusDoubleItem.numA.get(this);
	}

	/**
	 * Sets a new value for {@link #numA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumA(@javax.annotation.Nullable final java.lang.Double numA)
	{
		PlusDoubleItem.numA.set(this,numA);
	}

	/**
	 * Returns the value of {@link #numB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Double getNumB()
	{
		return PlusDoubleItem.numB.get(this);
	}

	/**
	 * Sets a new value for {@link #numB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumB(@javax.annotation.Nullable final java.lang.Double numB)
	{
		PlusDoubleItem.numB.set(this,numB);
	}

	/**
	 * Returns the value of {@link #numC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Double getNumC()
	{
		return PlusDoubleItem.numC.get(this);
	}

	/**
	 * Sets a new value for {@link #numC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumC(@javax.annotation.Nullable final java.lang.Double numC)
	{
		PlusDoubleItem.numC.set(this,numC);
	}

	/**
	 * Returns the value of {@link #plusA9}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getPlusA9()
	{
		return PlusDoubleItem.plusA9.get(this);
	}

	/**
	 * Returns the value of {@link #plusAB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getPlusAB()
	{
		return PlusDoubleItem.plusAB.get(this);
	}

	/**
	 * Returns the value of {@link #plusAC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getPlusAC()
	{
		return PlusDoubleItem.plusAC.get(this);
	}

	/**
	 * Returns the value of {@link #plusBC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getPlusBC()
	{
		return PlusDoubleItem.plusBC.get(this);
	}

	/**
	 * Returns the value of {@link #plusABC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getPlusABC()
	{
		return PlusDoubleItem.plusABC.get(this);
	}

	/**
	 * Returns the value of {@link #plusABaC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getPlusABaC()
	{
		return PlusDoubleItem.plusABaC.get(this);
	}

	/**
	 * Returns the value of {@link #multiplyB9}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getMultiplyB9()
	{
		return PlusDoubleItem.multiplyB9.get(this);
	}

	/**
	 * Returns the value of {@link #multiplyBC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Double getMultiplyBC()
	{
		return PlusDoubleItem.multiplyBC.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for plusDoubleItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PlusDoubleItem> TYPE = com.exedio.cope.TypesBound.newType(PlusDoubleItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private PlusDoubleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
