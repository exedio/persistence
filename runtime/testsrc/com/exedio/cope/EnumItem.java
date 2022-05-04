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

import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.instrument.WrapperType;

@WrapperType(activationConstructor=PACKAGE)
final class EnumItem extends Item
{
	@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
	enum Status
	{
		status1,
		status2,
		status3
	}

	static final EnumField<Status> status = EnumField.create(Status.class);

	enum Single
	{
		single
	}

	static final EnumField<Single> single = EnumField.createEvenIfRedundant(Single.class).optional();

	/**
	 * Creates a new EnumItem with all the fields initially needed.
	 * @param status the initial value for field {@link #status}.
	 * @throws com.exedio.cope.MandatoryViolationException if status is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	EnumItem(
				@javax.annotation.Nonnull final Status status)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(EnumItem.status,status),
		});
	}

	/**
	 * Creates a new EnumItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private EnumItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #status}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	Status getStatus()
	{
		return EnumItem.status.get(this);
	}

	/**
	 * Sets a new value for {@link #status}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setStatus(@javax.annotation.Nonnull final Status status)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		EnumItem.status.set(this,status);
	}

	/**
	 * Returns the value of {@link #single}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Single getSingle()
	{
		return EnumItem.single.get(this);
	}

	/**
	 * Sets a new value for {@link #single}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSingle(@javax.annotation.Nullable final Single single)
	{
		EnumItem.single.set(this,single);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for enumItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<EnumItem> TYPE = com.exedio.cope.TypesBound.newType(EnumItem.class,EnumItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	EnumItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
