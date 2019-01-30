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

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.Item;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(type=NONE)
public final class DispatcherNoneItem extends Item
{
	@Wrapper(wrap="dispatch", visibility=NONE)
	static final Dispatcher wrong = new Dispatcher();

	@SuppressWarnings({"unchecked", "rawtypes"}) // because instrumentor fails on correct version
	static void newTypeAccessible(final Class javaClass)
	{
		TypesBound.newType(javaClass);
	}


	/**
	 * Creates a new DispatcherNoneItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public DispatcherNoneItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new DispatcherNoneItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DispatcherNoneItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns, whether this item is yet to be dispatched by {@link #wrong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isPending")
	boolean isWrongPending()
	{
		return DispatcherNoneItem.wrong.isPending(this);
	}

	/**
	 * Sets whether this item is yet to be dispatched by {@link #wrong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setPending")
	void setWrongPending(final boolean pending)
	{
		DispatcherNoneItem.wrong.setPending(this,pending);
	}

	/**
	 * Returns, whether this item is allowed to be purged by {@link #wrong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNoPurge")
	boolean isWrongNoPurge()
	{
		return DispatcherNoneItem.wrong.isNoPurge(this);
	}

	/**
	 * Sets whether this item is allowed to be purged by {@link #wrong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setNoPurge")
	void setWrongNoPurge(final boolean noPurge)
	{
		DispatcherNoneItem.wrong.setNoPurge(this,noPurge);
	}

	/**
	 * Returns the date, this item was last successfully dispatched by {@link #wrong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastSuccessDate")
	@javax.annotation.Nullable
	java.util.Date getWrongLastSuccessDate()
	{
		return DispatcherNoneItem.wrong.getLastSuccessDate(this);
	}

	/**
	 * Returns the milliseconds, this item needed to be last successfully dispatched by {@link #wrong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastSuccessElapsed")
	@javax.annotation.Nullable
	java.lang.Long getWrongLastSuccessElapsed()
	{
		return DispatcherNoneItem.wrong.getLastSuccessElapsed(this);
	}

	/**
	 * Returns the attempts to dispatch this item by {@link #wrong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getRuns")
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getWrongRuns()
	{
		return DispatcherNoneItem.wrong.getRuns(this);
	}

	/**
	 * Returns the failed attempts to dispatch this item by {@link #wrong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getFailures")
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getWrongFailures()
	{
		return DispatcherNoneItem.wrong.getFailures(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	static void purgeWrong(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherNoneItem.wrong.purge(properties,ctx);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	static void purgeWrong(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.Condition restriction,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherNoneItem.wrong.purge(properties,restriction,ctx);
	}

	/**
	 * Returns the parent field of the run type of {@link #wrong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="RunParent")
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<DispatcherNoneItem> wrongRunParent()
	{
		return DispatcherNoneItem.wrong.getRunParent(DispatcherNoneItem.class);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private DispatcherNoneItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
