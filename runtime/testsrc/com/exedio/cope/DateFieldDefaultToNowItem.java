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
	static final DateField mandatory = new DateField().defaultToNow();
	static final DateField optional  = new DateField().optional().defaultToNow();
	static final DateField none      = new DateField().optional();

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
	DateFieldDefaultToNowItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #mandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.util.Date getMandatory()
	{
		return DateFieldDefaultToNowItem.mandatory.get(this);
	}

	/**
	 * Sets a new value for {@link #mandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setMandatory(@javax.annotation.Nonnull final java.util.Date mandatory)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DateFieldDefaultToNowItem.mandatory.set(this,mandatory);
	}

	/**
	 * Sets the current date for the date field {@link #mandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchMandatory()
	{
		DateFieldDefaultToNowItem.mandatory.touch(this);
	}

	/**
	 * Returns the value of {@link #optional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.util.Date getOptional()
	{
		return DateFieldDefaultToNowItem.optional.get(this);
	}

	/**
	 * Sets a new value for {@link #optional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOptional(@javax.annotation.Nullable final java.util.Date optional)
	{
		DateFieldDefaultToNowItem.optional.set(this,optional);
	}

	/**
	 * Sets the current date for the date field {@link #optional}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchOptional()
	{
		DateFieldDefaultToNowItem.optional.touch(this);
	}

	/**
	 * Returns the value of {@link #none}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.util.Date getNone()
	{
		return DateFieldDefaultToNowItem.none.get(this);
	}

	/**
	 * Sets a new value for {@link #none}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNone(@javax.annotation.Nullable final java.util.Date none)
	{
		DateFieldDefaultToNowItem.none.set(this,none);
	}

	/**
	 * Sets the current date for the date field {@link #none}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	void touchNone()
	{
		DateFieldDefaultToNowItem.none.touch(this);
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
	private DateFieldDefaultToNowItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
