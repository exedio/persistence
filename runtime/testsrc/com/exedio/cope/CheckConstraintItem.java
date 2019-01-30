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

@CopeName("Main")
public final class CheckConstraintItem extends CheckConstraintSuperItem
{
	@WrapperInitial
	public static final IntegerField alpha = new IntegerField().optional();

	@WrapperInitial
	public static final IntegerField beta = new IntegerField().optional();

	@WrapperInitial
	public static final IntegerField gamma = new IntegerField().optional();

	@WrapperInitial
	public static final IntegerField delta = new IntegerField().optional();

	public static final CheckConstraint alphaToBeta = new CheckConstraint(alpha.less(beta));

	void setAlphaBeta(final Integer alpha, final Integer beta)
	{
		set(
			CheckConstraintItem.alpha.map(alpha),
			CheckConstraintItem.beta.map(beta));
	}

	void setBetaGamma(final Integer beta, final Integer gamma)
	{
		set(
			CheckConstraintItem.beta.map(beta),
			CheckConstraintItem.gamma.map(gamma));
	}

	/**
	 * Creates a new CheckConstraintItem with all the fields initially needed.
	 * @param eins the initial value for field {@link #eins}.
	 * @param zwei the initial value for field {@link #zwei}.
	 * @param drei the initial value for field {@link #drei}.
	 * @param alpha the initial value for field {@link #alpha}.
	 * @param beta the initial value for field {@link #beta}.
	 * @param gamma the initial value for field {@link #gamma}.
	 * @param delta the initial value for field {@link #delta}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public CheckConstraintItem(
				@javax.annotation.Nullable final java.lang.Integer eins,
				@javax.annotation.Nullable final java.lang.Integer zwei,
				@javax.annotation.Nullable final java.lang.Integer drei,
				@javax.annotation.Nullable final java.lang.Integer alpha,
				@javax.annotation.Nullable final java.lang.Integer beta,
				@javax.annotation.Nullable final java.lang.Integer gamma,
				@javax.annotation.Nullable final java.lang.Integer delta)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.CheckConstraintSuperItem.eins.map(eins),
			com.exedio.cope.CheckConstraintSuperItem.zwei.map(zwei),
			com.exedio.cope.CheckConstraintSuperItem.drei.map(drei),
			CheckConstraintItem.alpha.map(alpha),
			CheckConstraintItem.beta.map(beta),
			CheckConstraintItem.gamma.map(gamma),
			CheckConstraintItem.delta.map(delta),
		});
	}

	/**
	 * Creates a new CheckConstraintItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CheckConstraintItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #alpha}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Integer getAlpha()
	{
		return CheckConstraintItem.alpha.get(this);
	}

	/**
	 * Sets a new value for {@link #alpha}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setAlpha(@javax.annotation.Nullable final java.lang.Integer alpha)
	{
		CheckConstraintItem.alpha.set(this,alpha);
	}

	/**
	 * Returns the value of {@link #beta}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Integer getBeta()
	{
		return CheckConstraintItem.beta.get(this);
	}

	/**
	 * Sets a new value for {@link #beta}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setBeta(@javax.annotation.Nullable final java.lang.Integer beta)
	{
		CheckConstraintItem.beta.set(this,beta);
	}

	/**
	 * Returns the value of {@link #gamma}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Integer getGamma()
	{
		return CheckConstraintItem.gamma.get(this);
	}

	/**
	 * Sets a new value for {@link #gamma}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setGamma(@javax.annotation.Nullable final java.lang.Integer gamma)
	{
		CheckConstraintItem.gamma.set(this,gamma);
	}

	/**
	 * Returns the value of {@link #delta}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Integer getDelta()
	{
		return CheckConstraintItem.delta.get(this);
	}

	/**
	 * Sets a new value for {@link #delta}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setDelta(@javax.annotation.Nullable final java.lang.Integer delta)
	{
		CheckConstraintItem.delta.set(this,delta);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for checkConstraintItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<CheckConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(CheckConstraintItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private CheckConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
