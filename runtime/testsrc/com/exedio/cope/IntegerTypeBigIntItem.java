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

@CopeCreateLimit(9223372036854775807L)
public class IntegerTypeBigIntItem extends Item
{
	static final ItemField<IntegerTypeBigIntItem> itemReference = ItemField.create(IntegerTypeBigIntItem.class, DeletePolicy.NULLIFY).optional();

	static final LongField value = new LongField().optional();


	/**
	 * Creates a new IntegerTypeBigIntItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public IntegerTypeBigIntItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new IntegerTypeBigIntItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected IntegerTypeBigIntItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #itemReference}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final IntegerTypeBigIntItem getItemReference()
	{
		return IntegerTypeBigIntItem.itemReference.get(this);
	}

	/**
	 * Sets a new value for {@link #itemReference}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setItemReference(@javax.annotation.Nullable final IntegerTypeBigIntItem itemReference)
	{
		IntegerTypeBigIntItem.itemReference.set(this,itemReference);
	}

	/**
	 * Returns the value of {@link #value}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.Long getValue()
	{
		return IntegerTypeBigIntItem.value.get(this);
	}

	/**
	 * Sets a new value for {@link #value}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setValue(@javax.annotation.Nullable final java.lang.Long value)
	{
		IntegerTypeBigIntItem.value.set(this,value);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for integerTypeBigIntItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<IntegerTypeBigIntItem> TYPE = com.exedio.cope.TypesBound.newType(IntegerTypeBigIntItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected IntegerTypeBigIntItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
