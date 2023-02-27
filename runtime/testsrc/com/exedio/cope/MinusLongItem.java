
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
			SetValue.map(numA, initialNumA),
			SetValue.map(numB, initialNumB),
			SetValue.map(numC, initialNumC),
		});
	}

	/**
	 * Creates a new MinusLongItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public MinusLongItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new MinusLongItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private MinusLongItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #numA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Long getNumA()
	{
		return MinusLongItem.numA.get(this);
	}

	/**
	 * Sets a new value for {@link #numA}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumA(@javax.annotation.Nullable final java.lang.Long numA)
	{
		MinusLongItem.numA.set(this,numA);
	}

	/**
	 * Returns the value of {@link #numB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Long getNumB()
	{
		return MinusLongItem.numB.get(this);
	}

	/**
	 * Sets a new value for {@link #numB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumB(@javax.annotation.Nullable final java.lang.Long numB)
	{
		MinusLongItem.numB.set(this,numB);
	}

	/**
	 * Returns the value of {@link #numC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Long getNumC()
	{
		return MinusLongItem.numC.get(this);
	}

	/**
	 * Sets a new value for {@link #numC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setNumC(@javax.annotation.Nullable final java.lang.Long numC)
	{
		MinusLongItem.numC.set(this,numC);
	}

	/**
	 * Returns the value of {@link #viewAB}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Long getViewAB()
	{
		return MinusLongItem.viewAB.getSupported(this);
	}

	/**
	 * Returns the value of {@link #viewAC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Long getViewAC()
	{
		return MinusLongItem.viewAC.getSupported(this);
	}

	/**
	 * Returns the value of {@link #viewBC}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public Long getViewBC()
	{
		return MinusLongItem.viewBC.getSupported(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for minusLongItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MinusLongItem> TYPE = com.exedio.cope.TypesBound.newType(MinusLongItem.class,MinusLongItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private MinusLongItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
