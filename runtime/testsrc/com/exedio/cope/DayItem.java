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
	static final DayField day = new DayField();
	static final DayField optionalDay = new DayField().optional();


	/**
	 * Creates a new DayItem with all the fields initially needed.
	 * @param day the initial value for field {@link #day}.
	 * @throws com.exedio.cope.MandatoryViolationException if day is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DayItem(
				@javax.annotation.Nonnull final com.exedio.cope.util.Day day)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DayItem.day.map(day),
		});
	}

	/**
	 * Creates a new DayItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DayItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #day}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final com.exedio.cope.util.Day getDay()
	{
		return DayItem.day.get(this);
	}

	/**
	 * Sets a new value for {@link #day}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDay(@javax.annotation.Nonnull final com.exedio.cope.util.Day day)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DayItem.day.set(this,day);
	}

	/**
	 * Sets today for the date field {@link #day}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDay(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DayItem.day.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #optionalDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final com.exedio.cope.util.Day getOptionalDay()
	{
		return DayItem.optionalDay.get(this);
	}

	/**
	 * Sets a new value for {@link #optionalDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setOptionalDay(@javax.annotation.Nullable final com.exedio.cope.util.Day optionalDay)
	{
		DayItem.optionalDay.set(this,optionalDay);
	}

	/**
	 * Sets today for the date field {@link #optionalDay}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchOptionalDay(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DayItem.optionalDay.touch(this,zone);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dayItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DayItem> TYPE = com.exedio.cope.TypesBound.newType(DayItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private DayItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}