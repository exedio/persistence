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

public final class FinalSubNoneItem extends FinalSuperItem
{
	public static final IntegerField subIntNone = new IntegerField();

	/**
	 * Creates a new FinalSubNoneItem with all the fields initially needed.
	 * @param superInt the initial value for field {@link #superInt}.
	 * @param subIntNone the initial value for field {@link #subIntNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public FinalSubNoneItem(
				final int superInt,
				final int subIntNone)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.FinalSuperItem.superInt,superInt),
			com.exedio.cope.SetValue.map(FinalSubNoneItem.subIntNone,subIntNone),
		});
	}

	/**
	 * Creates a new FinalSubNoneItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private FinalSubNoneItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #subIntNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public int getSubIntNone()
	{
		return FinalSubNoneItem.subIntNone.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #subIntNone}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setSubIntNone(final int subIntNone)
	{
		FinalSubNoneItem.subIntNone.set(this,subIntNone);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for finalSubNoneItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<FinalSubNoneItem> TYPE = com.exedio.cope.TypesBound.newType(FinalSubNoneItem.class,FinalSubNoneItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private FinalSubNoneItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
