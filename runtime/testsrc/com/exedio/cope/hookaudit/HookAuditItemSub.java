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

package com.exedio.cope.hookaudit;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(constructor=NONE)
final class HookAuditItemSub extends HookAuditItem
{
	@SuppressWarnings("MethodOverridesStaticMethodOfSuperclass")
	static HookAuditItemSub newWithField(final String field)
	{
		return new HookAuditItemSub(HookAuditItem.field.map(field));
	}


	@HookAuditWatched("S")
	static final StringField subField = new StringField().optional();

	static HookAuditItemSub newWithSubField(final String subField)
	{
		return new HookAuditItemSub(HookAuditItemSub.subField.map(subField));
	}


	/**
	 * Creates a new HookAuditItemSub and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private HookAuditItemSub(final com.exedio.cope.SetValue<?>... setValues)
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
		return HookAuditItemSub.subField.get(this);
	}

	/**
	 * Sets a new value for {@link #subField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSubField(@javax.annotation.Nullable final java.lang.String subField)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HookAuditItemSub.subField.set(this,subField);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hookAuditItemSub.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HookAuditItemSub> TYPE = com.exedio.cope.TypesBound.newType(HookAuditItemSub.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private HookAuditItemSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
