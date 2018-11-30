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

@WrapperType(genericConstructor=PACKAGE)
@CopeSchemaName("DefaultToItem")
final class DateFieldDefaultToNowItem extends Item
{
	static final DateField dateNow = new DateField().defaultToNow();
	static final DateField dateNowOpt = new DateField().optional().defaultToNow();
	static final DateField dateNone = new DateField().optional();

	/**
	 * Creates a new DateFieldDefaultToNowItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DateFieldDefaultToNowItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new DateFieldDefaultToNowItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	DateFieldDefaultToNowItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #dateNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getDateNow()
	{
		return DateFieldDefaultToNowItem.dateNow.get(this);
	}

	/**
	 * Sets a new value for {@link #dateNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateNow(@javax.annotation.Nonnull final java.util.Date dateNow)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DateFieldDefaultToNowItem.dateNow.set(this,dateNow);
	}

	/**
	 * Sets the current date for the date field {@link #dateNow}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateNow()
	{
		DateFieldDefaultToNowItem.dateNow.touch(this);
	}

	/**
	 * Returns the value of {@link #dateNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.util.Date getDateNowOpt()
	{
		return DateFieldDefaultToNowItem.dateNowOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #dateNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateNowOpt(@javax.annotation.Nullable final java.util.Date dateNowOpt)
	{
		DateFieldDefaultToNowItem.dateNowOpt.set(this,dateNowOpt);
	}

	/**
	 * Sets the current date for the date field {@link #dateNowOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateNowOpt()
	{
		DateFieldDefaultToNowItem.dateNowOpt.touch(this);
	}

	/**
	 * Returns the value of {@link #dateNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.util.Date getDateNone()
	{
		return DateFieldDefaultToNowItem.dateNone.get(this);
	}

	/**
	 * Sets a new value for {@link #dateNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDateNone(@javax.annotation.Nullable final java.util.Date dateNone)
	{
		DateFieldDefaultToNowItem.dateNone.set(this,dateNone);
	}

	/**
	 * Sets the current date for the date field {@link #dateNone}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchDateNone()
	{
		DateFieldDefaultToNowItem.dateNone.touch(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dateFieldDefaultToNowItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DateFieldDefaultToNowItem> TYPE = com.exedio.cope.TypesBound.newType(DateFieldDefaultToNowItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private DateFieldDefaultToNowItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
