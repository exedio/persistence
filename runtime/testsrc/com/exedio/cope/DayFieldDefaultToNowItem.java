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
import com.exedio.cope.util.TimeZoneStrict;

@WrapperType(genericConstructor=PACKAGE)
@CopeSchemaName("DefaultToItem")
final class DayFieldDefaultToNowItem extends Item
{
	static final DayField dayNow = new DayField().defaultToNow(TimeZoneStrict.getTimeZone("Europe/Berlin"));
	static final DayField dayNowOpt = new DayField().optional().defaultToNow(TimeZoneStrict.getTimeZone("Europe/Berlin"));
	static final DayField dayNone = new DayField().optional();

	/**
	 * Creates a new DayFieldDefaultToNowItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DayFieldDefaultToNowItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new DayFieldDefaultToNowItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	DayFieldDefaultToNowItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #dayNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	com.exedio.cope.util.Day getDayNow()
	{
		return DayFieldDefaultToNowItem.dayNow.get(this);
	}

	/**
	 * Sets a new value for {@link #dayNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDayNow(@javax.annotation.Nonnull final com.exedio.cope.util.Day dayNow)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DayFieldDefaultToNowItem.dayNow.set(this,dayNow);
	}

	/**
	 * Sets today for the date field {@link #dayNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDayNow(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DayFieldDefaultToNowItem.dayNow.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #dayNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getDayNowOpt()
	{
		return DayFieldDefaultToNowItem.dayNowOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #dayNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDayNowOpt(@javax.annotation.Nullable final com.exedio.cope.util.Day dayNowOpt)
	{
		DayFieldDefaultToNowItem.dayNowOpt.set(this,dayNowOpt);
	}

	/**
	 * Sets today for the date field {@link #dayNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDayNowOpt(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DayFieldDefaultToNowItem.dayNowOpt.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #dayNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getDayNone()
	{
		return DayFieldDefaultToNowItem.dayNone.get(this);
	}

	/**
	 * Sets a new value for {@link #dayNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDayNone(@javax.annotation.Nullable final com.exedio.cope.util.Day dayNone)
	{
		DayFieldDefaultToNowItem.dayNone.set(this,dayNone);
	}

	/**
	 * Sets today for the date field {@link #dayNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDayNone(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DayFieldDefaultToNowItem.dayNone.touch(this,zone);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dayFieldDefaultToNowItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DayFieldDefaultToNowItem> TYPE = com.exedio.cope.TypesBound.newType(DayFieldDefaultToNowItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private DayFieldDefaultToNowItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
