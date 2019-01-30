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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DateField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.LongField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;

public final class Sub extends Super
{
	public static final BooleanField subMandatory = new BooleanField();

	@WrapperInitial
	public static final LongField subInitial = new LongField().optional();

	@WrapperInitial
	public static final DoubleField subInitialAnnotated = new DoubleField().optional();

	public static final DateField subNonInitial = new DateField().optional();

	@Wrapper(wrap="get", override=true)
	static final LongField override = new LongField().optional();

	/**
	 * Creates a new Sub with all the fields initially needed.
	 * @param superMandatory the initial value for field {@link #superMandatory}.
	 * @param superInitial the initial value for field {@link #superInitial}.
	 * @param subMandatory the initial value for field {@link #subMandatory}.
	 * @param subInitial the initial value for field {@link #subInitial}.
	 * @param subInitialAnnotated the initial value for field {@link #subInitialAnnotated}.
	 * @throws com.exedio.cope.MandatoryViolationException if superMandatory is null.
	 * @throws com.exedio.cope.StringLengthViolationException if superMandatory violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public Sub(
				@javax.annotation.Nonnull final java.lang.String superMandatory,
				@javax.annotation.Nullable final java.lang.Integer superInitial,
				final boolean subMandatory,
				@javax.annotation.Nullable final java.lang.Long subInitial,
				@javax.annotation.Nullable final java.lang.Double subInitialAnnotated)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.instrument.testmodel.Super.superMandatory.map(superMandatory),
			com.exedio.cope.instrument.testmodel.Super.superInitial.map(superInitial),
			Sub.subMandatory.map(subMandatory),
			Sub.subInitial.map(subInitial),
			Sub.subInitialAnnotated.map(subInitialAnnotated),
		});
	}

	/**
	 * Creates a new Sub and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private Sub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #subMandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	public boolean getSubMandatory()
	{
		return Sub.subMandatory.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #subMandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSubMandatory(final boolean subMandatory)
	{
		Sub.subMandatory.set(this,subMandatory);
	}

	/**
	 * Returns the value of {@link #subInitial}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Long getSubInitial()
	{
		return Sub.subInitial.get(this);
	}

	/**
	 * Sets a new value for {@link #subInitial}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSubInitial(@javax.annotation.Nullable final java.lang.Long subInitial)
	{
		Sub.subInitial.set(this,subInitial);
	}

	/**
	 * Returns the value of {@link #subInitialAnnotated}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.lang.Double getSubInitialAnnotated()
	{
		return Sub.subInitialAnnotated.get(this);
	}

	/**
	 * Sets a new value for {@link #subInitialAnnotated}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSubInitialAnnotated(@javax.annotation.Nullable final java.lang.Double subInitialAnnotated)
	{
		Sub.subInitialAnnotated.set(this,subInitialAnnotated);
	}

	/**
	 * Returns the value of {@link #subNonInitial}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	public java.util.Date getSubNonInitial()
	{
		return Sub.subNonInitial.get(this);
	}

	/**
	 * Sets a new value for {@link #subNonInitial}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	public void setSubNonInitial(@javax.annotation.Nullable final java.util.Date subNonInitial)
	{
		Sub.subNonInitial.set(this,subNonInitial);
	}

	/**
	 * Sets the current date for the date field {@link #subNonInitial}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	public void touchSubNonInitial()
	{
		Sub.subNonInitial.touch(this);
	}

	/**
	 * Returns the value of {@link #override}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	@java.lang.Override
	java.lang.Long getOverride()
	{
		return Sub.override.get(this);
	}

	/**
	 * Sets a new value for {@link #override}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOverride(@javax.annotation.Nullable final java.lang.Long override)
	{
		Sub.override.set(this,override);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for sub.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	@SuppressWarnings("hiding")
	public static final com.exedio.cope.Type<Sub> TYPE = com.exedio.cope.TypesBound.newType(Sub.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private Sub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
