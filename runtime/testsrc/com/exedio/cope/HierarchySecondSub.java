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

public final class HierarchySecondSub extends HierarchySuper
{

	public static final StringField firstSubString = new StringField().optional();

	/**
	 * Creates a new HierarchySecondSub with all the fields initially needed.
	 * @param superInt the initial value for field {@link #superInt}.
	 * @throws com.exedio.cope.UniqueViolationException if superInt is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public HierarchySecondSub(
				final int superInt)
			throws
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.HierarchySuper.superInt,superInt),
		});
	}

	/**
	 * Creates a new HierarchySecondSub and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private HierarchySecondSub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #firstSubString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.String getFirstSubString()
	{
		return HierarchySecondSub.firstSubString.get(this);
	}

	/**
	 * Sets a new value for {@link #firstSubString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setFirstSubString(@javax.annotation.Nullable final java.lang.String firstSubString)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HierarchySecondSub.firstSubString.set(this,firstSubString);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hierarchySecondSub.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<HierarchySecondSub> TYPE = com.exedio.cope.TypesBound.newType(HierarchySecondSub.class,HierarchySecondSub::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private HierarchySecondSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
