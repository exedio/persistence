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

import com.exedio.cope.DayField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperInitial;

/**
 */
public abstract class Super extends Item
{
	public static final StringField superMandatory = new StringField().lengthExact(5);

	@WrapperInitial
	public static final IntegerField superInitial = new IntegerField().optional();

	public static final DayField superNonInitial = new DayField().optional();

	@SuppressWarnings("unused") // OK: just for testing instrumentor
	abstract java.lang.Long getOverride();

	/**
	 * Creates a new Super with all the fields initially needed.
	 * @param superMandatory the initial value for field {@link #superMandatory}.
	 * @param superInitial the initial value for field {@link #superInitial}.
	 * @throws com.exedio.cope.MandatoryViolationException if superMandatory is null.
	 * @throws com.exedio.cope.StringLengthViolationException if superMandatory violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	protected Super(
				@javax.annotation.Nonnull final java.lang.String superMandatory,
				@javax.annotation.Nullable final java.lang.Integer superInitial)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			Super.superMandatory.map(superMandatory),
			Super.superInitial.map(superInitial),
		});
	}

	/**
	 * Creates a new Super and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Super(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #superMandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public final java.lang.String getSuperMandatory()
	{
		return Super.superMandatory.get(this);
	}

	/**
	 * Sets a new value for {@link #superMandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void setSuperMandatory(@javax.annotation.Nonnull final java.lang.String superMandatory)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		Super.superMandatory.set(this,superMandatory);
	}

	/**
	 * Returns the value of {@link #superInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public final java.lang.Integer getSuperInitial()
	{
		return Super.superInitial.get(this);
	}

	/**
	 * Sets a new value for {@link #superInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void setSuperInitial(@javax.annotation.Nullable final java.lang.Integer superInitial)
	{
		Super.superInitial.set(this,superInitial);
	}

	/**
	 * Returns the value of {@link #superNonInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public final com.exedio.cope.util.Day getSuperNonInitial()
	{
		return Super.superNonInitial.get(this);
	}

	/**
	 * Sets a new value for {@link #superNonInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void setSuperNonInitial(@javax.annotation.Nullable final com.exedio.cope.util.Day superNonInitial)
	{
		Super.superNonInitial.set(this,superNonInitial);
	}

	/**
	 * Sets today for the date field {@link #superNonInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void touchSuperNonInitial(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		Super.superNonInitial.touch(this,zone);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 2l;

	/**
	 * The persistent type information for super.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<Super> TYPE = com.exedio.cope.TypesBound.newType(Super.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Super(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
