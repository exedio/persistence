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
import static com.exedio.cope.pattern.DispatcherWithoutRemainingModelTest.MODEL;
import static java.lang.System.nanoTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.JobContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class DispatcherWithoutRemainingItem extends Item
{
	static final StringField body = new StringField();
	static final IntegerField dispatchCountCommitted = new IntegerField().defaultTo(0).min(0);

	static final HashMap<DispatcherWithoutRemainingItem, Log> logs = new HashMap<>();
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

	@Wrapper(wrap="dispatch", parameters={Dispatcher.Config.class, Runnable.class, JobContext.class}, visibility=Visibility.NONE)
	static final Dispatcher toTarget = Dispatcher.create(
			DispatcherWithoutRemainingItem::dispatch,
			DispatcherWithoutRemainingItem::notifyFinalFailure).
			withoutRemainingLEGACY();

	@WrapInterim(methodBody=false)
	private void dispatch() throws IOException, InterruptedException
	{
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
		assertEquals(Arrays.asList(expected), actualHistory);
		actualHistory.clear();
	}

	static void historyClear()
	{
		actualHistory.clear();
	}

	@WrapInterim(methodBody=false)
	private void notifyFinalFailure(final Exception cause)
	{
		assertTrue(!MODEL.hasCurrentTransaction());
		assertEquals(IOException.class, cause.getClass());
		historyAdd("notifyFinalFailure " + getCopeID());
		logs.get(this).notifyFinalFailureCount++;
	}

	DispatcherWithoutRemainingItem(final String body, final boolean fail)
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
	 * Creates a new DispatcherWithoutRemainingItem with all the fields initially needed.
	 * @param body the initial value for field {@link #body}.
	 * @throws com.exedio.cope.MandatoryViolationException if body is null.
	 * @throws com.exedio.cope.StringLengthViolationException if body violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	DispatcherWithoutRemainingItem(
				@javax.annotation.Nonnull final java.lang.String body)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(DispatcherWithoutRemainingItem.body,body),
		});
	}

	/**
	 * Creates a new DispatcherWithoutRemainingItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DispatcherWithoutRemainingItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #body}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getBody()
	{
		return DispatcherWithoutRemainingItem.body.get(this);
	}

	/**
	 * Sets a new value for {@link #body}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setBody(@javax.annotation.Nonnull final java.lang.String body)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		DispatcherWithoutRemainingItem.body.set(this,body);
	}

	/**
	 * Returns the value of {@link #dispatchCountCommitted}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getDispatchCountCommitted()
	{
		return DispatcherWithoutRemainingItem.dispatchCountCommitted.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #dispatchCountCommitted}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDispatchCountCommitted(final int dispatchCountCommitted)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		DispatcherWithoutRemainingItem.dispatchCountCommitted.set(this,dispatchCountCommitted);
	}

	/**
	 * Dispatch by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="dispatch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherWithoutRemainingItem.toTarget.dispatch(DispatcherWithoutRemainingItem.class,config,ctx);
	}

	/**
	 * Returns, whether this item is yet to be dispatched by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isPending")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isToTargetPending()
	{
		return DispatcherWithoutRemainingItem.toTarget.isPending(this);
	}

	/**
	 * Sets whether this item is yet to be dispatched by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setPending")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setToTargetPending(final boolean pending)
	{
		DispatcherWithoutRemainingItem.toTarget.setPending(this,pending);
	}

	/**
	 * Returns, whether this item is allowed to be purged by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNoPurge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isToTargetNoPurge()
	{
		return DispatcherWithoutRemainingItem.toTarget.isNoPurge(this);
	}

	/**
	 * Sets whether this item is allowed to be purged by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setNoPurge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setToTargetNoPurge(final boolean noPurge)
	{
		DispatcherWithoutRemainingItem.toTarget.setNoPurge(this,noPurge);
	}

	/**
	 * Returns the date, this item was last successfully dispatched by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastSuccessDate")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getToTargetLastSuccessDate()
	{
		return DispatcherWithoutRemainingItem.toTarget.getLastSuccessDate(this);
	}

	/**
	 * Returns the milliseconds, this item needed to be last successfully dispatched by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastSuccessElapsed")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Long getToTargetLastSuccessElapsed()
	{
		return DispatcherWithoutRemainingItem.toTarget.getLastSuccessElapsed(this);
	}

	/**
	 * Returns the attempts to dispatch this item by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getRuns")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetRuns()
	{
		return DispatcherWithoutRemainingItem.toTarget.getRuns(this);
	}

	/**
	 * Returns the failed attempts to dispatch this item by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getFailures")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetFailures()
	{
		return DispatcherWithoutRemainingItem.toTarget.getFailures(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="purge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void purgeToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherWithoutRemainingItem.toTarget.purge(properties,ctx);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="purge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void purgeToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.Condition restriction,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherWithoutRemainingItem.toTarget.purge(properties,restriction,ctx);
	}

	/**
	 * Returns the parent field of the run type of {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="RunParent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<DispatcherWithoutRemainingItem> toTargetRunParent()
	{
		return DispatcherWithoutRemainingItem.toTarget.getRunParent(DispatcherWithoutRemainingItem.class);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dispatcherWithoutRemainingItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DispatcherWithoutRemainingItem> TYPE = com.exedio.cope.TypesBound.newType(DispatcherWithoutRemainingItem.class,DispatcherWithoutRemainingItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DispatcherWithoutRemainingItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
