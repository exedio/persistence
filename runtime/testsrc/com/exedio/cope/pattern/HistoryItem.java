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

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;

public final class HistoryItem extends Item
{
	static final IntegerField amount = new IntegerField().optional();
	static final StringField comment = new StringField().optional();

	static final History audit = new History();

	/**
	 * Creates a new HistoryItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public HistoryItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new HistoryItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private HistoryItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #amount}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Integer getAmount()
	{
		return HistoryItem.amount.get(this);
	}

	/**
	 * Sets a new value for {@link #amount}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAmount(@javax.annotation.Nullable final java.lang.Integer amount)
	{
		HistoryItem.amount.set(this,amount);
	}

	/**
	 * Returns the value of {@link #comment}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getComment()
	{
		return HistoryItem.comment.get(this);
	}

	/**
	 * Sets a new value for {@link #comment}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setComment(@javax.annotation.Nullable final java.lang.String comment)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HistoryItem.comment.set(this,comment);
	}

	/**
	 * Returns the events of the history {@link #audit}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getEvents")
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.History.Event> getAuditEvents()
	{
		return HistoryItem.audit.getEvents(this);
	}

	/**
	 * Creates a new event for the history {@link #audit}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="createEvent")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.History.Event createAuditEvent(@javax.annotation.Nonnull final java.lang.String author,final boolean isNew)
	{
		return HistoryItem.audit.createEvent(this,author,isNew);
	}

	/**
	 * Returns the parent field of the event type of {@link #audit}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="EventParent")
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<HistoryItem> auditEventParent()
	{
		return HistoryItem.audit.getEventParent(HistoryItem.class);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for historyItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<HistoryItem> TYPE = com.exedio.cope.TypesBound.newType(HistoryItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private HistoryItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
