
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

public final class MinusDoubleItem extends Item
{
	public static final DoubleField numA = new DoubleField().optional();

	public static final DoubleField numB = new DoubleField().optional();

	public static final DoubleField numC = new DoubleField().optional();

	public static final MinusView<Double> viewAB = numA.minus(numB);

	public static final MinusView<Double> viewAC = numA.minus(numC);

	public static final MinusView<Double> viewBC = numB.minus(numC);

	public MinusDoubleItem(final double initialNumA, final double initialNumB, final double initialNumC)
	{
		super(new SetValue<?>[]{
			numA.map(initialNumA),
			numB.map(initialNumB),
			numC.map(initialNumC),
		});
	}

	/**
	 * Creates a new MinusDoubleItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public MinusDoubleItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new MinusDoubleItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private MinusDoubleItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #numA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getNumA()
	{
		return MinusDoubleItem.numA.get(this);
	}

	/**
	 * Sets a new value for {@link #numA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumA(@javax.annotation.Nullable final java.lang.Double numA)
	{
		MinusDoubleItem.numA.set(this,numA);
	}

	/**
	 * Returns the value of {@link #numB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getNumB()
	{
		return MinusDoubleItem.numB.get(this);
	}

	/**
	 * Sets a new value for {@link #numB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumB(@javax.annotation.Nullable final java.lang.Double numB)
	{
		MinusDoubleItem.numB.set(this,numB);
	}

	/**
	 * Returns the value of {@link #numC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getNumC()
	{
		return MinusDoubleItem.numC.get(this);
	}

	/**
	 * Sets a new value for {@link #numC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumC(@javax.annotation.Nullable final java.lang.Double numC)
	{
		MinusDoubleItem.numC.set(this,numC);
	}

	/**
	 * Returns the value of {@link #viewAB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getViewAB()
	{
		return MinusDoubleItem.viewAB.get(this);
	}

	/**
	 * Returns the value of {@link #viewAC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getViewAC()
	{
		return MinusDoubleItem.viewAC.get(this);
	}

	/**
	 * Returns the value of {@link #viewBC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Double getViewBC()
	{
		return MinusDoubleItem.viewBC.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for minusDoubleItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MinusDoubleItem> TYPE = com.exedio.cope.TypesBound.newType(MinusDoubleItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private MinusDoubleItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
