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

import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(constructor=NONE)
final class HookAuditItemSub extends HookAuditItem
{
	@SuppressWarnings("MethodOverridesStaticMethodOfSuperclass")
	static HookAuditItemSub newWithField(final String field)
	{
		return new HookAuditItemSub(SetValue.map(HookAuditItem.field, field));
	}


	@HookAuditWatched("S")
	static final StringField subField = new StringField().optional();

	static HookAuditItemSub newWithSubField(final String subField)
	{
		return new HookAuditItemSub(SetValue.map(HookAuditItemSub.subField, subField));
	}


	/**
	 * Creates a new HookAuditItemSub and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private HookAuditItemSub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #subField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getSubField()
	{
		return HookAuditItemSub.subField.get(this);
	}

	/**
	 * Sets a new value for {@link #subField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSubField(@javax.annotation.Nullable final java.lang.String subField)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HookAuditItemSub.subField.set(this,subField);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hookAuditItemSub.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HookAuditItemSub> TYPE = com.exedio.cope.TypesBound.newType(HookAuditItemSub.class,HookAuditItemSub::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private HookAuditItemSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
