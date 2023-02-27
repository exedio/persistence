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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	HookStampItemSub(
				@javax.annotation.Nullable final java.lang.String field)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.hookstamp.HookStampItem.field,field),
		});
	}

	/**
	 * Creates a new HookStampItemSub and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private HookStampItemSub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #subField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getSubField()
	{
		return HookStampItemSub.subField.get(this);
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
		HookStampItemSub.subField.set(this,subField);
	}

	/**
	 * Returns the value of {@link #historySub}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getHistorySub()
	{
		return HookStampItemSub.historySub.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hookStampItemSub.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<HookStampItemSub> TYPE = com.exedio.cope.TypesBound.newType(HookStampItemSub.class,HookStampItemSub::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private HookStampItemSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
