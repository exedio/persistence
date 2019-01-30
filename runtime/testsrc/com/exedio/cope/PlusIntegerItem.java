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


public final class PlusIntegerItem extends Item
{
	public static final IntegerField numA = new IntegerField().optional();

	public static final IntegerField numB = new IntegerField().optional();

	public static final IntegerField numC = new IntegerField().optional();

	public static final PlusLiteralView<Integer> plusA9 = PlusLiteralView.plus(numA, Integer.valueOf(9));

	public static final PlusView<Integer> plusAB = numA.plus(numB);

	public static final PlusView<Integer> plusAC = numA.plus(numC);

	public static final PlusView<Integer> plusBC = numB.plus(numC);

	public static final PlusView<Integer> plusABC = Cope.plus(numA, numB, numC);

	public static final PlusView<Integer> plusABaC = plusAB.plus(numC);

	public static final MultiplyLiteralView<Integer> multiplyB9 = MultiplyLiteralView.multiply(numB, Integer.valueOf(9));

	public static final MultiplyView<Integer> multiplyBC = numB.multiply(numC);

	public PlusIntegerItem(final int initialNumA, final int initialNumB, final int initialNumC)
	{
		super(new SetValue<?>[]{
			numA.map(initialNumA),
			numB.map(initialNumB),
			numC.map(initialNumC),
		});
	}

	public PlusIntegerItem(final Integer initialNumA, final Integer initialNumB, final Integer initialNumC)
	{
		super(new SetValue<?>[]{
			numA.map(initialNumA),
			numB.map(initialNumB),
			numC.map(initialNumC),
		});
	}

	/**
	 * Creates a new PlusIntegerItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public PlusIntegerItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new PlusIntegerItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private PlusIntegerItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #numA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Integer getNumA()
	{
		return PlusIntegerItem.numA.get(this);
	}

	/**
	 * Sets a new value for {@link #numA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumA(@javax.annotation.Nullable final java.lang.Integer numA)
	{
		PlusIntegerItem.numA.set(this,numA);
	}

	/**
	 * Returns the value of {@link #numB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Integer getNumB()
	{
		return PlusIntegerItem.numB.get(this);
	}

	/**
	 * Sets a new value for {@link #numB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumB(@javax.annotation.Nullable final java.lang.Integer numB)
	{
		PlusIntegerItem.numB.set(this,numB);
	}

	/**
	 * Returns the value of {@link #numC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Integer getNumC()
	{
		return PlusIntegerItem.numC.get(this);
	}

	/**
	 * Sets a new value for {@link #numC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setNumC(@javax.annotation.Nullable final java.lang.Integer numC)
	{
		PlusIntegerItem.numC.set(this,numC);
	}

	/**
	 * Returns the value of {@link #plusA9}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Integer getPlusA9()
	{
		return PlusIntegerItem.plusA9.get(this);
	}

	/**
	 * Returns the value of {@link #plusAB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Integer getPlusAB()
	{
		return PlusIntegerItem.plusAB.get(this);
	}

	/**
	 * Returns the value of {@link #plusAC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Integer getPlusAC()
	{
		return PlusIntegerItem.plusAC.get(this);
	}

	/**
	 * Returns the value of {@link #plusBC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Integer getPlusBC()
	{
		return PlusIntegerItem.plusBC.get(this);
	}

	/**
	 * Returns the value of {@link #plusABC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Integer getPlusABC()
	{
		return PlusIntegerItem.plusABC.get(this);
	}

	/**
	 * Returns the value of {@link #plusABaC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Integer getPlusABaC()
	{
		return PlusIntegerItem.plusABaC.get(this);
	}

	/**
	 * Returns the value of {@link #multiplyB9}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Integer getMultiplyB9()
	{
		return PlusIntegerItem.multiplyB9.get(this);
	}

	/**
	 * Returns the value of {@link #multiplyBC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public Integer getMultiplyBC()
	{
		return PlusIntegerItem.multiplyBC.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for plusIntegerItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PlusIntegerItem> TYPE = com.exedio.cope.TypesBound.newType(PlusIntegerItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private PlusIntegerItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
