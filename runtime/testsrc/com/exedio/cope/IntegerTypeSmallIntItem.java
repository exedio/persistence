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

import com.exedio.cope.ItemField.DeletePolicy;

@CopeCreateLimit(32767)
public class IntegerTypeSmallIntItem extends Item
{
	static final ItemField<IntegerTypeSmallIntItem> itemReference = ItemField.create(IntegerTypeSmallIntItem.class, DeletePolicy.NULLIFY).optional();

	static final LongField value = new LongField().range(-32768L, 32767L).optional();


	/**
	 * Creates a new IntegerTypeSmallIntItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public IntegerTypeSmallIntItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new IntegerTypeSmallIntItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected IntegerTypeSmallIntItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #itemReference}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final IntegerTypeSmallIntItem getItemReference()
	{
		return IntegerTypeSmallIntItem.itemReference.get(this);
	}

	/**
	 * Sets a new value for {@link #itemReference}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setItemReference(@javax.annotation.Nullable final IntegerTypeSmallIntItem itemReference)
	{
		IntegerTypeSmallIntItem.itemReference.set(this,itemReference);
	}

	/**
	 * Returns the value of {@link #value}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.Long getValue()
	{
		return IntegerTypeSmallIntItem.value.get(this);
	}

	/**
	 * Sets a new value for {@link #value}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setValue(@javax.annotation.Nullable final java.lang.Long value)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		IntegerTypeSmallIntItem.value.set(this,value);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for integerTypeSmallIntItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<IntegerTypeSmallIntItem> TYPE = com.exedio.cope.TypesBound.newType(IntegerTypeSmallIntItem.class,IntegerTypeSmallIntItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected IntegerTypeSmallIntItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
