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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public HistoryItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new HistoryItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private HistoryItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #amount}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getAmount()
	{
		return HistoryItem.amount.get(this);
	}

	/**
	 * Sets a new value for {@link #amount}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAmount(@javax.annotation.Nullable final java.lang.Integer amount)
	{
		HistoryItem.amount.set(this,amount);
	}

	/**
	 * Returns the value of {@link #comment}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getComment()
	{
		return HistoryItem.comment.get(this);
	}

	/**
	 * Sets a new value for {@link #comment}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setComment(@javax.annotation.Nullable final java.lang.String comment)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HistoryItem.comment.set(this,comment);
	}

	/**
	 * Returns the events of the history {@link #audit}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getEvents")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.History.Event> getAuditEvents()
	{
		return HistoryItem.audit.getEvents(this);
	}

	/**
	 * Creates a new event for the history {@link #audit}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="createEvent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.History.Event createAuditEvent(@javax.annotation.Nonnull final java.lang.String author,final boolean isNew)
	{
		return HistoryItem.audit.createEvent(this,author,isNew);
	}

	/**
	 * Returns the parent field of the event type of {@link #audit}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="EventParent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<HistoryItem> auditEventParent()
	{
		return HistoryItem.audit.getEventParent(HistoryItem.class);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for historyItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<HistoryItem> TYPE = com.exedio.cope.TypesBound.newType(HistoryItem.class,HistoryItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private HistoryItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
