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

final class DayItem extends Item
{
	static final DayField mandatory = new DayField();
	static final DayField optional = new DayField().optional();


	/**
	 * Creates a new DayItem with all the fields initially needed.
	 * @param mandatory the initial value for field {@link #mandatory}.
	 * @throws com.exedio.cope.MandatoryViolationException if mandatory is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	DayItem(
				@javax.annotation.Nonnull final com.exedio.cope.util.Day mandatory)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(DayItem.mandatory,mandatory),
		});
	}

	/**
	 * Creates a new DayItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DayItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.util.Day getMandatory()
	{
		return DayItem.mandatory.get(this);
	}

	/**
	 * Sets a new value for {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMandatory(@javax.annotation.Nonnull final com.exedio.cope.util.Day mandatory)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DayItem.mandatory.set(this,mandatory);
	}

	/**
	 * Sets today for the date field {@link #mandatory}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchMandatory(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DayItem.mandatory.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #optional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getOptional()
	{
		return DayItem.optional.get(this);
	}

	/**
	 * Sets a new value for {@link #optional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOptional(@javax.annotation.Nullable final com.exedio.cope.util.Day optional)
	{
		DayItem.optional.set(this,optional);
	}

	/**
	 * Sets today for the date field {@link #optional}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="touch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void touchOptional(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DayItem.optional.touch(this,zone);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dayItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DayItem> TYPE = com.exedio.cope.TypesBound.newType(DayItem.class,DayItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DayItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
