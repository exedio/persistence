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

import static com.exedio.cope.misc.TimeUtil.toMillies;
import static com.exedio.cope.pattern.DispatcherModelTest.MODEL;
import static java.lang.System.nanoTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public final class DispatcherItem extends Item implements Dispatchable
{
	static final StringField body = new StringField();
	static final IntegerField dispatchCountCommitted = new IntegerField().defaultTo(0).min(0);

	static final HashMap<DispatcherItem, Log> logs = new HashMap<>();
	static class Log
	{
		boolean fail;
		int dispatchCount = 0;
		final ArrayList<Long> dispatchRunElapsed = new ArrayList<>();
		int notifyFinalFailureCount = 0;

		Log(final boolean fail)
		{
			this.fail = fail;
		}
	}

	static final Dispatcher toTarget = new Dispatcher();

	Boolean getToTargetUnpendSuccess()
	{
		return toTarget.getUnpendSuccess().get(this);
	}

	Date getToTargetUnpendDate()
	{
		return toTarget.getUnpendDate().get(this);
	}

	Long getToTargetUnpendDateMillis()
	{
		final Date date = getToTargetUnpendDate();
		return date!=null ? Long.valueOf(date.getTime()) : null;
	}

	@Override
	public void dispatch(final Dispatcher dispatcher) throws IOException, InterruptedException
	{
		assertSame(toTarget, dispatcher);
		assertTrue(MODEL.hasCurrentTransaction());
		assertEquals(toTarget.getID() + " dispatch " + getCopeID(), MODEL.currentTransaction().getName());
		setDispatchCountCommitted(getDispatchCountCommitted()+1);
		historyAdd("dispatch " + getCopeID());
		final Log log = logs.get(this);
		final long start = nanoTime();
		log.dispatchCount++;
		Thread.sleep(5);
		log.dispatchRunElapsed.add(toMillies(nanoTime(), start));
		if(log.fail)
			throw new IOException(getBody());
	}

	private static final ArrayList<String> actualHistory = new ArrayList<>();

	static void historyAdd(final String message)
	{
		actualHistory.add(message);
	}

	static void historyAssert(final String... expected)
	{
		//noinspection MisorderedAssertEqualsArguments
		assertEquals(Arrays.asList(expected), actualHistory);
		actualHistory.clear();
	}

	static void historyClear()
	{
		actualHistory.clear();
	}

	@Override
	public void notifyFinalFailure(final Dispatcher dispatcher, final Exception cause)
	{
		assertSame(toTarget, dispatcher);
		assertTrue(!MODEL.hasCurrentTransaction());
		assertEquals(IOException.class, cause.getClass());
		historyAdd("notifyFinalFailure " + getCopeID());
		logs.get(this).notifyFinalFailureCount++;
	}

	DispatcherItem(final String body, final boolean fail)
	{
		this(body);
		//noinspection ThisEscapedInObjectConstruction
		logs.put(this, new Log(fail));
	}

	long lastElapsed()
	{
		final List<Dispatcher.Run> runs = getToTargetRuns();
		return runs.get(runs.size()-1).getElapsed();
	}


	/**
	 * Creates a new DispatcherItem with all the fields initially needed.
	 * @param body the initial value for field {@link #body}.
	 * @throws com.exedio.cope.MandatoryViolationException if body is null.
	 * @throws com.exedio.cope.StringLengthViolationException if body violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DispatcherItem(
				@javax.annotation.Nonnull final java.lang.String body)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DispatcherItem.body.map(body),
		});
	}

	/**
	 * Creates a new DispatcherItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DispatcherItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #body}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getBody()
	{
		return DispatcherItem.body.get(this);
	}

	/**
	 * Sets a new value for {@link #body}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBody(@javax.annotation.Nonnull final java.lang.String body)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		DispatcherItem.body.set(this,body);
	}

	/**
	 * Returns the value of {@link #dispatchCountCommitted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getDispatchCountCommitted()
	{
		return DispatcherItem.dispatchCountCommitted.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #dispatchCountCommitted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDispatchCountCommitted(final int dispatchCountCommitted)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		DispatcherItem.dispatchCountCommitted.set(this,dispatchCountCommitted);
	}

	/**
	 * Dispatch by {@link #toTarget}.
	 * @return the number of successfully dispatched items
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="dispatch")
	@java.lang.Deprecated
	static int dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nullable final com.exedio.cope.util.Interrupter interrupter)
	{
		return DispatcherItem.toTarget.dispatch(DispatcherItem.class,config,interrupter);
	}

	/**
	 * Dispatch by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="dispatch")
	static void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherItem.toTarget.dispatch(DispatcherItem.class,config,ctx);
	}

	/**
	 * Dispatch by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="dispatch")
	static void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final java.lang.Runnable probe,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherItem.toTarget.dispatch(DispatcherItem.class,config,probe,ctx);
	}

	/**
	 * Returns, whether this item is yet to be dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isPending")
	boolean isToTargetPending()
	{
		return DispatcherItem.toTarget.isPending(this);
	}

	/**
	 * Sets whether this item is yet to be dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setPending")
	void setToTargetPending(final boolean pending)
	{
		DispatcherItem.toTarget.setPending(this,pending);
	}

	/**
	 * Returns, whether this item is allowed to be purged by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNoPurge")
	boolean isToTargetNoPurge()
	{
		return DispatcherItem.toTarget.isNoPurge(this);
	}

	/**
	 * Sets whether this item is allowed to be purged by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setNoPurge")
	void setToTargetNoPurge(final boolean noPurge)
	{
		DispatcherItem.toTarget.setNoPurge(this,noPurge);
	}

	/**
	 * Returns the date, this item was last successfully dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastSuccessDate")
	@javax.annotation.Nullable
	java.util.Date getToTargetLastSuccessDate()
	{
		return DispatcherItem.toTarget.getLastSuccessDate(this);
	}

	/**
	 * Returns the milliseconds, this item needed to be last successfully dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastSuccessElapsed")
	@javax.annotation.Nullable
	java.lang.Long getToTargetLastSuccessElapsed()
	{
		return DispatcherItem.toTarget.getLastSuccessElapsed(this);
	}

	/**
	 * Returns the attempts to dispatch this item by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getRuns")
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetRuns()
	{
		return DispatcherItem.toTarget.getRuns(this);
	}

	/**
	 * Returns the failed attempts to dispatch this item by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getFailures")
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetFailures()
	{
		return DispatcherItem.toTarget.getFailures(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	static void purgeToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherItem.toTarget.purge(properties,ctx);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	static void purgeToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.Condition restriction,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherItem.toTarget.purge(properties,restriction,ctx);
	}

	/**
	 * Returns the parent field of the run type of {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="RunParent")
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<DispatcherItem> toTargetRunParent()
	{
		return DispatcherItem.toTarget.getRunParent(DispatcherItem.class);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dispatcherItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DispatcherItem> TYPE = com.exedio.cope.TypesBound.newType(DispatcherItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private DispatcherItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
