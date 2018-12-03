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
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;

import com.exedio.cope.instrument.WrapperType;

@WrapperType(genericConstructor=PACKAGE)
@CopeSchemaName("DefaultToItem")
final class DayFieldDefaultToNowItem extends Item
{
	static final DayField mandatory = new DayField().defaultToNow(getTimeZone("Europe/Berlin"));
	static final DayField optional  = new DayField().optional().defaultToNow(getTimeZone("Europe/Berlin"));
	static final DayField none      = new DayField().optional();

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
	 * Returns the value of {@link #mandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	com.exedio.cope.util.Day getMandatory()
	{
		return DayFieldDefaultToNowItem.mandatory.get(this);
	}

	/**
	 * Sets a new value for {@link #mandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setMandatory(@javax.annotation.Nonnull final com.exedio.cope.util.Day mandatory)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DayFieldDefaultToNowItem.mandatory.set(this,mandatory);
	}

	/**
	 * Sets today for the date field {@link #mandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchMandatory(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DayFieldDefaultToNowItem.mandatory.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #optional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getOptional()
	{
		return DayFieldDefaultToNowItem.optional.get(this);
	}

	/**
	 * Sets a new value for {@link #optional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOptional(@javax.annotation.Nullable final com.exedio.cope.util.Day optional)
	{
		DayFieldDefaultToNowItem.optional.set(this,optional);
	}

	/**
	 * Sets today for the date field {@link #optional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchOptional(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DayFieldDefaultToNowItem.optional.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #none}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	com.exedio.cope.util.Day getNone()
	{
		return DayFieldDefaultToNowItem.none.get(this);
	}

	/**
	 * Sets a new value for {@link #none}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNone(@javax.annotation.Nullable final com.exedio.cope.util.Day none)
	{
		DayFieldDefaultToNowItem.none.set(this,none);
	}

	/**
	 * Sets today for the date field {@link #none}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchNone(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		DayFieldDefaultToNowItem.none.touch(this,zone);
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
