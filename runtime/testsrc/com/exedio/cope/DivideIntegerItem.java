
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

public final class DivideIntegerItem extends Item
{
	public static final IntegerField numA = new IntegerField().optional();

	public static final IntegerField numB = new IntegerField().optional();

	public static final IntegerField numC = new IntegerField().optional();

	public static final DivideView<Integer> divideAB = numA.divide(numB);

	public static final DivideView<Integer> divideAC = numA.divide(numC);

	public static final DivideView<Integer> divideBC = numB.divide(numC);

	public DivideIntegerItem(final int initialNumA, final int initialNumB, final int initialNumC)
	{
		super(new SetValue<?>[]{
			numA.map(initialNumA),
			numB.map(initialNumB),
			numC.map(initialNumC),
		});
	}

	/**
	 * Creates a new DivideIntegerItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public DivideIntegerItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new DivideIntegerItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DivideIntegerItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #numA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Integer getNumA()
	{
		return DivideIntegerItem.numA.get(this);
	}

	/**
	 * Sets a new value for {@link #numA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumA(@javax.annotation.Nullable final java.lang.Integer numA)
	{
		DivideIntegerItem.numA.set(this,numA);
	}

	/**
	 * Returns the value of {@link #numB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Integer getNumB()
	{
		return DivideIntegerItem.numB.get(this);
	}

	/**
	 * Sets a new value for {@link #numB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumB(@javax.annotation.Nullable final java.lang.Integer numB)
	{
		DivideIntegerItem.numB.set(this,numB);
	}

	/**
	 * Returns the value of {@link #numC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Integer getNumC()
	{
		return DivideIntegerItem.numC.get(this);
	}

	/**
	 * Sets a new value for {@link #numC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumC(@javax.annotation.Nullable final java.lang.Integer numC)
	{
		DivideIntegerItem.numC.set(this,numC);
	}

	/**
	 * Returns the value of {@link #divideAB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getDivideAB()
	{
		return DivideIntegerItem.divideAB.get(this);
	}

	/**
	 * Returns the value of {@link #divideAC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getDivideAC()
	{
		return DivideIntegerItem.divideAC.get(this);
	}

	/**
	 * Returns the value of {@link #divideBC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Integer getDivideBC()
	{
		return DivideIntegerItem.divideBC.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for divideIntegerItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DivideIntegerItem> TYPE = com.exedio.cope.TypesBound.newType(DivideIntegerItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DivideIntegerItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
