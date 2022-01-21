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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public PlusIntegerItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new PlusIntegerItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private PlusIntegerItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #numA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Integer getNumA()
	{
		return PlusIntegerItem.numA.get(this);
	}

	/**
	 * Sets a new value for {@link #numA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumA(@javax.annotation.Nullable final java.lang.Integer numA)
	{
		PlusIntegerItem.numA.set(this,numA);
	}

	/**
	 * Returns the value of {@link #numB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Integer getNumB()
	{
		return PlusIntegerItem.numB.get(this);
	}

	/**
	 * Sets a new value for {@link #numB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumB(@javax.annotation.Nullable final java.lang.Integer numB)
	{
		PlusIntegerItem.numB.set(this,numB);
	}

	/**
	 * Returns the value of {@link #numC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Integer getNumC()
	{
		return PlusIntegerItem.numC.get(this);
	}

	/**
	 * Sets a new value for {@link #numC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumC(@javax.annotation.Nullable final java.lang.Integer numC)
	{
		PlusIntegerItem.numC.set(this,numC);
	}

	/**
	 * Returns the value of {@link #plusA9}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getPlusA9()
	{
		return PlusIntegerItem.plusA9.get(this);
	}

	/**
	 * Returns the value of {@link #plusAB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getPlusAB()
	{
		return PlusIntegerItem.plusAB.get(this);
	}

	/**
	 * Returns the value of {@link #plusAC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getPlusAC()
	{
		return PlusIntegerItem.plusAC.get(this);
	}

	/**
	 * Returns the value of {@link #plusBC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getPlusBC()
	{
		return PlusIntegerItem.plusBC.get(this);
	}

	/**
	 * Returns the value of {@link #plusABC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getPlusABC()
	{
		return PlusIntegerItem.plusABC.get(this);
	}

	/**
	 * Returns the value of {@link #plusABaC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getPlusABaC()
	{
		return PlusIntegerItem.plusABaC.get(this);
	}

	/**
	 * Returns the value of {@link #multiplyB9}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getMultiplyB9()
	{
		return PlusIntegerItem.multiplyB9.get(this);
	}

	/**
	 * Returns the value of {@link #multiplyBC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getMultiplyBC()
	{
		return PlusIntegerItem.multiplyBC.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for plusIntegerItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PlusIntegerItem> TYPE = com.exedio.cope.TypesBound.newType(PlusIntegerItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private PlusIntegerItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
