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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private Sub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #subMandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public boolean getSubMandatory()
	{
		return Sub.subMandatory.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #subMandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setSubMandatory(final boolean subMandatory)
	{
		Sub.subMandatory.set(this,subMandatory);
	}

	/**
	 * Returns the value of {@link #subInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Long getSubInitial()
	{
		return Sub.subInitial.get(this);
	}

	/**
	 * Sets a new value for {@link #subInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setSubInitial(@javax.annotation.Nullable final java.lang.Long subInitial)
	{
		Sub.subInitial.set(this,subInitial);
	}

	/**
	 * Returns the value of {@link #subInitialAnnotated}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Double getSubInitialAnnotated()
	{
		return Sub.subInitialAnnotated.get(this);
	}

	/**
	 * Sets a new value for {@link #subInitialAnnotated}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setSubInitialAnnotated(@javax.annotation.Nullable final java.lang.Double subInitialAnnotated)
	{
		Sub.subInitialAnnotated.set(this,subInitialAnnotated);
	}

	/**
	 * Returns the value of {@link #subNonInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.util.Date getSubNonInitial()
	{
		return Sub.subNonInitial.get(this);
	}

	/**
	 * Sets a new value for {@link #subNonInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setSubNonInitial(@javax.annotation.Nullable final java.util.Date subNonInitial)
	{
		Sub.subNonInitial.set(this,subNonInitial);
	}

	/**
	 * Sets the current date for the date field {@link #subNonInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void touchSubNonInitial()
	{
		Sub.subNonInitial.touch(this);
	}

	/**
	 * Returns the value of {@link #override}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	@java.lang.Override
	java.lang.Long getOverride()
	{
		return Sub.override.get(this);
	}

	/**
	 * Sets a new value for {@link #override}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOverride(@javax.annotation.Nullable final java.lang.Long override)
	{
		Sub.override.set(this,override);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for sub.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<Sub> TYPE = com.exedio.cope.TypesBound.newType(Sub.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private Sub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
