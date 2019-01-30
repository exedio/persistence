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
import static com.exedio.cope.pattern.DispatcherWithoutPurgeModelTest.MODEL;
import static java.lang.System.nanoTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.util.JobContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DispatcherWithoutPurgeItem extends Item implements Dispatchable
{
	static final StringField body = new StringField();
	static final IntegerField dispatchCountCommitted = new IntegerField().defaultTo(0).min(0);

	static final HashMap<DispatcherWithoutPurgeItem, Log> logs = new HashMap<>();
	static class Log
	{
		boolean fail;
		int dispatchCount = 0;
		long dispatchLastSuccessElapsed = 0;
		final ArrayList<Long> dispatchFailureElapsed = new ArrayList<>();
		int notifyFinalFailureCount = 0;

		Log(final boolean fail)
		{
			this.fail = fail;
		}
	}

	static final Dispatcher toTarget = new Dispatcher().withoutPurge();

	@Override
	public void dispatch(final Dispatcher dispatcher) throws IOException, InterruptedException
	{
		assertSame(toTarget, dispatcher);
		assertTrue(MODEL.hasCurrentTransaction());
		assertEquals(toTarget.getID() + " dispatch " + getCopeID(), MODEL.currentTransaction().getName());
		setDispatchCountCommitted(getDispatchCountCommitted()+1);
		final Log log = logs.get(this);
		final long start = nanoTime();
		log.dispatchCount++;
		Thread.sleep(5);
		if(log.fail)
		{
			log.dispatchFailureElapsed.add(toMillies(nanoTime(), start));
			throw new IOException(getBody());
		}
		log.dispatchLastSuccessElapsed = toMillies(nanoTime(), start);
	}

	@Override
	public void notifyFinalFailure(final Dispatcher dispatcher, final Exception cause)
	{
		assertSame(toTarget, dispatcher);
		assertTrue(!MODEL.hasCurrentTransaction());
		assertEquals(IOException.class, cause.getClass());
		logs.get(this).notifyFinalFailureCount++;
	}

	DispatcherWithoutPurgeItem(final String body, final boolean fail)
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
	 * Is not instrumented, because of purge is disabled by {@link Dispatcher#withoutPurge()}.
	 */
	static void purgeToTarget(final DispatcherPurgeProperties properties, final JobContext ctx)
	{
		toTarget.purge(properties, ctx);
	}


	/**
	 * Creates a new DispatcherWithoutPurgeItem with all the fields initially needed.
	 * @param body the initial value for field {@link #body}.
	 * @throws com.exedio.cope.MandatoryViolationException if body is null.
	 * @throws com.exedio.cope.StringLengthViolationException if body violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DispatcherWithoutPurgeItem(
				@javax.annotation.Nonnull final java.lang.String body)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DispatcherWithoutPurgeItem.body.map(body),
		});
	}

	/**
	 * Creates a new DispatcherWithoutPurgeItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DispatcherWithoutPurgeItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #body}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getBody()
	{
		return DispatcherWithoutPurgeItem.body.get(this);
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
		DispatcherWithoutPurgeItem.body.set(this,body);
	}

	/**
	 * Returns the value of {@link #dispatchCountCommitted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getDispatchCountCommitted()
	{
		return DispatcherWithoutPurgeItem.dispatchCountCommitted.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #dispatchCountCommitted}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDispatchCountCommitted(final int dispatchCountCommitted)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		DispatcherWithoutPurgeItem.dispatchCountCommitted.set(this,dispatchCountCommitted);
	}

	/**
	 * Dispatch by {@link #toTarget}.
	 * @return the number of successfully dispatched items
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="dispatch")
	@java.lang.Deprecated
	static int dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nullable final com.exedio.cope.util.Interrupter interrupter)
	{
		return DispatcherWithoutPurgeItem.toTarget.dispatch(DispatcherWithoutPurgeItem.class,config,interrupter);
	}

	/**
	 * Dispatch by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="dispatch")
	static void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherWithoutPurgeItem.toTarget.dispatch(DispatcherWithoutPurgeItem.class,config,ctx);
	}

	/**
	 * Dispatch by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="dispatch")
	static void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final java.lang.Runnable probe,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherWithoutPurgeItem.toTarget.dispatch(DispatcherWithoutPurgeItem.class,config,probe,ctx);
	}

	/**
	 * Returns, whether this item is yet to be dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isPending")
	boolean isToTargetPending()
	{
		return DispatcherWithoutPurgeItem.toTarget.isPending(this);
	}

	/**
	 * Sets whether this item is yet to be dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setPending")
	void setToTargetPending(final boolean pending)
	{
		DispatcherWithoutPurgeItem.toTarget.setPending(this,pending);
	}

	/**
	 * Returns the date, this item was last successfully dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastSuccessDate")
	@javax.annotation.Nullable
	java.util.Date getToTargetLastSuccessDate()
	{
		return DispatcherWithoutPurgeItem.toTarget.getLastSuccessDate(this);
	}

	/**
	 * Returns the milliseconds, this item needed to be last successfully dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastSuccessElapsed")
	@javax.annotation.Nullable
	java.lang.Long getToTargetLastSuccessElapsed()
	{
		return DispatcherWithoutPurgeItem.toTarget.getLastSuccessElapsed(this);
	}

	/**
	 * Returns the attempts to dispatch this item by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getRuns")
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetRuns()
	{
		return DispatcherWithoutPurgeItem.toTarget.getRuns(this);
	}

	/**
	 * Returns the failed attempts to dispatch this item by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getFailures")
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetFailures()
	{
		return DispatcherWithoutPurgeItem.toTarget.getFailures(this);
	}

	/**
	 * Returns the parent field of the run type of {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="RunParent")
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<DispatcherWithoutPurgeItem> toTargetRunParent()
	{
		return DispatcherWithoutPurgeItem.toTarget.getRunParent(DispatcherWithoutPurgeItem.class);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dispatcherWithoutPurgeItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DispatcherWithoutPurgeItem> TYPE = com.exedio.cope.TypesBound.newType(DispatcherWithoutPurgeItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private DispatcherWithoutPurgeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
