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

package com.exedio.cope.pattern;

import com.exedio.cope.BooleanField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;

public final class SingletonItem extends Item
{
	static final IntegerField integer = new IntegerField().optional();
	static final IntegerField integer55 = new IntegerField().defaultTo(55);
	static final BooleanField booleanField = new BooleanField().defaultTo(true);

	static final Singleton einzigartig = new Singleton();


	/**
	 * Creates a new SingletonItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public SingletonItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new SingletonItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private SingletonItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getInteger()
	{
		return SingletonItem.integer.get(this);
	}

	/**
	 * Sets a new value for {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setInteger(@javax.annotation.Nullable final java.lang.Integer integer)
	{
		SingletonItem.integer.set(this,integer);
	}

	/**
	 * Returns the value of {@link #integer55}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getInteger55()
	{
		return SingletonItem.integer55.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integer55}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setInteger55(final int integer55)
	{
		SingletonItem.integer55.set(this,integer55);
	}

	/**
	 * Returns the value of {@link #booleanField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean getBooleanField()
	{
		return SingletonItem.booleanField.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #booleanField}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBooleanField(final boolean booleanField)
	{
		SingletonItem.booleanField.set(this,booleanField);
	}

	/**
	 * Gets the single instance of singletonItem.
	 * Creates an instance, if none exists.
	 * @return never returns null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="instance")
	@javax.annotation.Nonnull
	static SingletonItem instance()
	{
		return SingletonItem.einzigartig.instance(SingletonItem.class);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for singletonItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<SingletonItem> TYPE = com.exedio.cope.TypesBound.newType(SingletonItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private SingletonItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
