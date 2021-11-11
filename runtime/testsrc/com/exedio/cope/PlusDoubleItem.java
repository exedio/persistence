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
			SetValue.map(numA, initialNumA),
			SetValue.map(numB, initialNumB),
			SetValue.map(numC, initialNumC),
		});
	}

	/**
	 * Creates a new PlusDoubleItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public PlusDoubleItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new PlusDoubleItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private PlusDoubleItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #numA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getNumA()
	{
		return PlusDoubleItem.numA.get(this);
	}

	/**
	 * Sets a new value for {@link #numA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumA(@javax.annotation.Nullable final java.lang.Double numA)
	{
		PlusDoubleItem.numA.set(this,numA);
	}

	/**
	 * Returns the value of {@link #numB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getNumB()
	{
		return PlusDoubleItem.numB.get(this);
	}

	/**
	 * Sets a new value for {@link #numB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumB(@javax.annotation.Nullable final java.lang.Double numB)
	{
		PlusDoubleItem.numB.set(this,numB);
	}

	/**
	 * Returns the value of {@link #numC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getNumC()
	{
		return PlusDoubleItem.numC.get(this);
	}

	/**
	 * Sets a new value for {@link #numC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumC(@javax.annotation.Nullable final java.lang.Double numC)
	{
		PlusDoubleItem.numC.set(this,numC);
	}

	/**
	 * Returns the value of {@link #plusA9}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getPlusA9()
	{
		return PlusDoubleItem.plusA9.getSupported(this);
	}

	/**
	 * Returns the value of {@link #plusAB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getPlusAB()
	{
		return PlusDoubleItem.plusAB.getSupported(this);
	}

	/**
	 * Returns the value of {@link #plusAC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getPlusAC()
	{
		return PlusDoubleItem.plusAC.getSupported(this);
	}

	/**
	 * Returns the value of {@link #plusBC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getPlusBC()
	{
		return PlusDoubleItem.plusBC.getSupported(this);
	}

	/**
	 * Returns the value of {@link #plusABC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getPlusABC()
	{
		return PlusDoubleItem.plusABC.getSupported(this);
	}

	/**
	 * Returns the value of {@link #plusABaC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getPlusABaC()
	{
		return PlusDoubleItem.plusABaC.getSupported(this);
	}

	/**
	 * Returns the value of {@link #multiplyB9}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getMultiplyB9()
	{
		return PlusDoubleItem.multiplyB9.getSupported(this);
	}

	/**
	 * Returns the value of {@link #multiplyBC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getMultiplyBC()
	{
		return PlusDoubleItem.multiplyBC.getSupported(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for plusDoubleItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PlusDoubleItem> TYPE = com.exedio.cope.TypesBound.newType(PlusDoubleItem.class,PlusDoubleItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private PlusDoubleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
