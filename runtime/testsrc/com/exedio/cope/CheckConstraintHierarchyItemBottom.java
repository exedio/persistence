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

@CopeName("ItemBottom")
@SuppressWarnings("SuspiciousNameCombination")
public final class CheckConstraintHierarchyItemBottom extends CheckConstraintHierarchyItemTop
{
	static final IntegerField bottom1 = new IntegerField().defaultTo(300);
	static final IntegerField bottom2 = new IntegerField().defaultTo(301);
	static final CheckConstraint bottom = new CheckConstraint(bottom1.less(bottom2));

	static final CheckConstraint up = new CheckConstraint(up1.less(up2));

	static final IntegerField cross2 = new IntegerField().defaultTo(401);
	static final CheckConstraint cross = new CheckConstraint(cross1.less(cross2));


	/**
	 * Creates a new CheckConstraintHierarchyItemBottom with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public CheckConstraintHierarchyItemBottom()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new CheckConstraintHierarchyItemBottom and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CheckConstraintHierarchyItemBottom(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #bottom1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getBottom1()
	{
		return CheckConstraintHierarchyItemBottom.bottom1.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #bottom1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBottom1(final int bottom1)
	{
		CheckConstraintHierarchyItemBottom.bottom1.set(this,bottom1);
	}

	/**
	 * Returns the value of {@link #bottom2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getBottom2()
	{
		return CheckConstraintHierarchyItemBottom.bottom2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #bottom2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBottom2(final int bottom2)
	{
		CheckConstraintHierarchyItemBottom.bottom2.set(this,bottom2);
	}

	/**
	 * Returns the value of {@link #cross2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getCross2()
	{
		return CheckConstraintHierarchyItemBottom.cross2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #cross2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCross2(final int cross2)
	{
		CheckConstraintHierarchyItemBottom.cross2.set(this,cross2);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for checkConstraintHierarchyItemBottom.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CheckConstraintHierarchyItemBottom> TYPE = com.exedio.cope.TypesBound.newType(CheckConstraintHierarchyItemBottom.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private CheckConstraintHierarchyItemBottom(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
