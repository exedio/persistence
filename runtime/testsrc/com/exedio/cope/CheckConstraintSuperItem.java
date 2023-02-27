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

import com.exedio.cope.instrument.WrapperInitial;

@CopeName("Super")
public class CheckConstraintSuperItem extends Item
{
	@WrapperInitial
	public static final IntegerField eins = new IntegerField().optional();

	@WrapperInitial
	public static final IntegerField zwei = new IntegerField().optional();

	@WrapperInitial
	public static final IntegerField drei = new IntegerField().optional();

	public static final CheckConstraint einsToZwei = new CheckConstraint(eins.greaterOrEqual(zwei));

	void setEinsZwei(final Integer eins, final Integer zwei)
	{
		set(
			CheckConstraintSuperItem.eins.map(eins),
			CheckConstraintSuperItem.zwei.map(zwei));
	}

	void setZweiDrei(final Integer zwei, final Integer drei)
	{
		set(
			CheckConstraintSuperItem.zwei.map(zwei),
			CheckConstraintSuperItem.drei.map(drei));
	}


	/**
	 * Creates a new CheckConstraintSuperItem with all the fields initially needed.
	 * @param eins the initial value for field {@link #eins}.
	 * @param zwei the initial value for field {@link #zwei}.
	 * @param drei the initial value for field {@link #drei}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public CheckConstraintSuperItem(
				@javax.annotation.Nullable final java.lang.Integer eins,
				@javax.annotation.Nullable final java.lang.Integer zwei,
				@javax.annotation.Nullable final java.lang.Integer drei)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(CheckConstraintSuperItem.eins,eins),
			com.exedio.cope.SetValue.map(CheckConstraintSuperItem.zwei,zwei),
			com.exedio.cope.SetValue.map(CheckConstraintSuperItem.drei,drei),
		});
	}

	/**
	 * Creates a new CheckConstraintSuperItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected CheckConstraintSuperItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #eins}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public final java.lang.Integer getEins()
	{
		return CheckConstraintSuperItem.eins.get(this);
	}

	/**
	 * Sets a new value for {@link #eins}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void setEins(@javax.annotation.Nullable final java.lang.Integer eins)
	{
		CheckConstraintSuperItem.eins.set(this,eins);
	}

	/**
	 * Returns the value of {@link #zwei}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public final java.lang.Integer getZwei()
	{
		return CheckConstraintSuperItem.zwei.get(this);
	}

	/**
	 * Sets a new value for {@link #zwei}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void setZwei(@javax.annotation.Nullable final java.lang.Integer zwei)
	{
		CheckConstraintSuperItem.zwei.set(this,zwei);
	}

	/**
	 * Returns the value of {@link #drei}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public final java.lang.Integer getDrei()
	{
		return CheckConstraintSuperItem.drei.get(this);
	}

	/**
	 * Sets a new value for {@link #drei}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void setDrei(@javax.annotation.Nullable final java.lang.Integer drei)
	{
		CheckConstraintSuperItem.drei.set(this,drei);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for checkConstraintSuperItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CheckConstraintSuperItem> TYPE = com.exedio.cope.TypesBound.newType(CheckConstraintSuperItem.class,CheckConstraintSuperItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected CheckConstraintSuperItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
