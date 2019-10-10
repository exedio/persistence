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

@CopeName("ItemTop")
@SuppressWarnings("SuspiciousNameCombination")
public class CheckConstraintHierarchyItemTop extends Item
{
	static final IntegerField top1 = new IntegerField().defaultTo(100);
	static final IntegerField top2 = new IntegerField().defaultTo(101);
	static final CheckConstraint top = new CheckConstraint(top1.less(top2));

	static final IntegerField up1 = new IntegerField().defaultTo(200);
	static final IntegerField up2 = new IntegerField().defaultTo(201);

	static final IntegerField cross1 = new IntegerField().defaultTo(400);


	/**
	 * Creates a new CheckConstraintHierarchyItemTop with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public CheckConstraintHierarchyItemTop()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new CheckConstraintHierarchyItemTop and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected CheckConstraintHierarchyItemTop(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #top1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final int getTop1()
	{
		return CheckConstraintHierarchyItemTop.top1.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #top1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setTop1(final int top1)
	{
		CheckConstraintHierarchyItemTop.top1.set(this,top1);
	}

	/**
	 * Returns the value of {@link #top2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final int getTop2()
	{
		return CheckConstraintHierarchyItemTop.top2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #top2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setTop2(final int top2)
	{
		CheckConstraintHierarchyItemTop.top2.set(this,top2);
	}

	/**
	 * Returns the value of {@link #up1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final int getUp1()
	{
		return CheckConstraintHierarchyItemTop.up1.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #up1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setUp1(final int up1)
	{
		CheckConstraintHierarchyItemTop.up1.set(this,up1);
	}

	/**
	 * Returns the value of {@link #up2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final int getUp2()
	{
		return CheckConstraintHierarchyItemTop.up2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #up2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setUp2(final int up2)
	{
		CheckConstraintHierarchyItemTop.up2.set(this,up2);
	}

	/**
	 * Returns the value of {@link #cross1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final int getCross1()
	{
		return CheckConstraintHierarchyItemTop.cross1.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #cross1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setCross1(final int cross1)
	{
		CheckConstraintHierarchyItemTop.cross1.set(this,cross1);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for checkConstraintHierarchyItemTop.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CheckConstraintHierarchyItemTop> TYPE = com.exedio.cope.TypesBound.newType(CheckConstraintHierarchyItemTop.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected CheckConstraintHierarchyItemTop(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
