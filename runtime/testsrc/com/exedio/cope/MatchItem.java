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


final class MatchItem extends Item
{

	static final StringField text = new StringField().optional();

	MatchItem(final String text)
	{
		this(new com.exedio.cope.SetValue<?>[]{
				MatchItem.text.map(text),
		});
	}

	/**
	 * Creates a new MatchItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	MatchItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new MatchItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private MatchItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #text}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getText()
	{
		return MatchItem.text.get(this);
	}

	/**
	 * Sets a new value for {@link #text}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setText(@javax.annotation.Nullable final java.lang.String text)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MatchItem.text.set(this,text);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for matchItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<MatchItem> TYPE = com.exedio.cope.TypesBound.newType(MatchItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private MatchItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}