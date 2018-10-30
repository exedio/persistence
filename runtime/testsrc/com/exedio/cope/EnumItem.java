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

final class EnumItem extends Item
{
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

	static final EnumField<Single> single = EnumField.create(Single.class).optional();

	/**
	 * Creates a new EnumItem with all the fields initially needed.
	 * @param status the initial value for field {@link #status}.
	 * @throws com.exedio.cope.MandatoryViolationException if status is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	EnumItem(
				@javax.annotation.Nonnull final Status status)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			EnumItem.status.map(status),
		});
	}

	/**
	 * Creates a new EnumItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private EnumItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #status}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	Status getStatus()
	{
		return EnumItem.status.get(this);
	}

	/**
	 * Sets a new value for {@link #status}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setStatus(@javax.annotation.Nonnull final Status status)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		EnumItem.status.set(this,status);
	}

	/**
	 * Returns the value of {@link #single}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	Single getSingle()
	{
		return EnumItem.single.get(this);
	}

	/**
	 * Sets a new value for {@link #single}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSingle(@javax.annotation.Nullable final Single single)
	{
		EnumItem.single.set(this,single);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for enumItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<EnumItem> TYPE = com.exedio.cope.TypesBound.newType(EnumItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private EnumItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
