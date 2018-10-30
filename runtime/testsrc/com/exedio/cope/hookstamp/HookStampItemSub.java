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

package com.exedio.cope.hookstamp;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;

final class HookStampItemSub extends HookStampItem
{
	@SuppressWarnings("MethodOverridesStaticMethodOfSuperclass")
	static HookStampItemSub newWithField(final String field)
	{
		return new HookStampItemSub(HookStampItem.field.map(field));
	}


	@HookStampWatched("S")
	static final StringField subField = new StringField().optional();

	static HookStampItemSub newWithSubField(final String subField)
	{
		return new HookStampItemSub(HookStampItemSub.subField.map(subField));
	}

	@HookStampWatcher
	@Wrapper(wrap="set", visibility=NONE)
	static final StringField historySub = new StringField().defaultTo("{S}");


	/**
	 * Creates a new HookStampItemSub with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	HookStampItemSub(
				@javax.annotation.Nullable final java.lang.String field)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.hookstamp.HookStampItem.field.map(field),
		});
	}

	/**
	 * Creates a new HookStampItemSub and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private HookStampItemSub(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #subField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getSubField()
	{
		return HookStampItemSub.subField.get(this);
	}

	/**
	 * Sets a new value for {@link #subField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSubField(@javax.annotation.Nullable final java.lang.String subField)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HookStampItemSub.subField.set(this,subField);
	}

	/**
	 * Returns the value of {@link #historySub}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getHistorySub()
	{
		return HookStampItemSub.historySub.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hookStampItemSub.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HookStampItemSub> TYPE = com.exedio.cope.TypesBound.newType(HookStampItemSub.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private HookStampItemSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
