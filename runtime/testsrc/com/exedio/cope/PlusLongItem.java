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


public final class PlusLongItem extends Item
{
	public static final LongField numA = new LongField().optional();

	public static final LongField numB = new LongField().optional();

	public static final LongField numC = new LongField().optional();

	public static final PlusLiteralView<Long> plusA9 = PlusLiteralView.plus(numA, Long.valueOf(9l));

	public static final PlusView<Long> plusAB = numA.plus(numB);

	public static final PlusView<Long> plusAC = numA.plus(numC);

	public static final PlusView<Long> plusBC = numB.plus(numC);

	public static final PlusView<Long> plusABC = Cope.plus(numA, numB, numC);

	public static final PlusView<Long> plusABaC = plusAB.plus(numC);

	public static final MultiplyLiteralView<Long> multiplyB9 = MultiplyLiteralView.multiply(numB, Long.valueOf(9l));

	public static final MultiplyView<Long> multiplyBC = numB.multiply(numC);

	public PlusLongItem(final long initialNumA, final long initialNumB, final long initialNumC)
	{
		super(new SetValue<?>[]{
			numA.map(initialNumA),
			numB.map(initialNumB),
			numC.map(initialNumC),
		});
	}

	/**
	 * Creates a new PlusLongItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public PlusLongItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new PlusLongItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private PlusLongItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #numA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public final java.lang.Long getNumA()
	{
		return PlusLongItem.numA.get(this);
	}

	/**
	 * Sets a new value for {@link #numA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setNumA(@javax.annotation.Nullable final java.lang.Long numA)
	{
		PlusLongItem.numA.set(this,numA);
	}

	/**
	 * Returns the value of {@link #numB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public final java.lang.Long getNumB()
	{
		return PlusLongItem.numB.get(this);
	}

	/**
	 * Sets a new value for {@link #numB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setNumB(@javax.annotation.Nullable final java.lang.Long numB)
	{
		PlusLongItem.numB.set(this,numB);
	}

	/**
	 * Returns the value of {@link #numC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public final java.lang.Long getNumC()
	{
		return PlusLongItem.numC.get(this);
	}

	/**
	 * Sets a new value for {@link #numC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public final void setNumC(@javax.annotation.Nullable final java.lang.Long numC)
	{
		PlusLongItem.numC.set(this,numC);
	}

	/**
	 * Returns the value of {@link #plusA9}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public final Long getPlusA9()
	{
		return PlusLongItem.plusA9.get(this);
	}

	/**
	 * Returns the value of {@link #plusAB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public final Long getPlusAB()
	{
		return PlusLongItem.plusAB.get(this);
	}

	/**
	 * Returns the value of {@link #plusAC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public final Long getPlusAC()
	{
		return PlusLongItem.plusAC.get(this);
	}

	/**
	 * Returns the value of {@link #plusBC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public final Long getPlusBC()
	{
		return PlusLongItem.plusBC.get(this);
	}

	/**
	 * Returns the value of {@link #plusABC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public final Long getPlusABC()
	{
		return PlusLongItem.plusABC.get(this);
	}

	/**
	 * Returns the value of {@link #plusABaC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public final Long getPlusABaC()
	{
		return PlusLongItem.plusABaC.get(this);
	}

	/**
	 * Returns the value of {@link #multiplyB9}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public final Long getMultiplyB9()
	{
		return PlusLongItem.multiplyB9.get(this);
	}

	/**
	 * Returns the value of {@link #multiplyBC}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public final Long getMultiplyBC()
	{
		return PlusLongItem.multiplyBC.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for plusLongItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PlusLongItem> TYPE = com.exedio.cope.TypesBound.newType(PlusLongItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private PlusLongItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}