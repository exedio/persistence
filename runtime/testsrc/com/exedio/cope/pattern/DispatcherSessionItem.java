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

import static com.exedio.cope.pattern.DispatcherSessionTest.MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.BooleanField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.JobContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public final class DispatcherSessionItem extends Item
{
	static final BooleanField fail = new BooleanField().toFinal();
	static final IntegerField dispatchCountCommitted = new IntegerField().defaultTo(0).min(0);

	@Wrapper(wrap="dispatch", parameters={Dispatcher.Config.class, Runnable.class, JobContext.class}, visibility=Visibility.NONE)
	@SuppressWarnings("resource")
	static final Dispatcher toTarget = Dispatcher.createWithSession(
			Session::new,
			DispatcherSessionItem::dispatch);

	@WrapInterim
	private static final class Session implements DispatcherSessionInterface
	{
		private final int count;
		boolean closed = false;

		@WrapInterim(methodBody=false)
		Session()
		{
			//noinspection AssignmentToStaticFieldFromInstanceMethod
			this.count = sessionCount++;
			historyAdd("session " + count + " new");
			//noinspection ThisEscapedInObjectConstruction
			sessions.add(this);
		}

		@Override
		@WrapInterim(methodBody=false)
		public void close()
		{
			historyAdd("session " + count + " close");
			closed = true;
		}
	}

	private static final ArrayList<Session> sessions = new ArrayList<>();
	private static int sessionCount = 0;

	static void assertSessionsClosed(final int expectedSize)
	{
		for(final Session session : sessions)
			assertTrue(session.closed);
		//noinspection MisorderedAssertEqualsArguments
		assertEquals(expectedSize, sessions.size());
	}

	@WrapInterim(methodBody=false)
	private void dispatch(final Session session) throws IOException
	{
		assertFalse(session.closed);
		//noinspection MisorderedAssertEqualsArguments
		assertSame(session, sessions.get(sessions.size()-1));
		assertTrue(MODEL.hasCurrentTransaction());
		assertEquals(toTarget.getID() + " dispatch " + getCopeID(), MODEL.currentTransaction().getName());
		setDispatchCountCommitted(getDispatchCountCommitted()+1);
		historyAdd("session " + session.count + " dispatch " + getCopeID());
		if(getFail())
			throw new IOException();
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
		sessions.clear();
		sessionCount = 0;
		actualHistory.clear();
	}


	/**
	 * Creates a new DispatcherSessionItem with all the fields initially needed.
	 * @param fail the initial value for field {@link #fail}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	DispatcherSessionItem(
				final boolean fail)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DispatcherSessionItem.fail.map(fail),
		});
	}

	/**
	 * Creates a new DispatcherSessionItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DispatcherSessionItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #fail}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean getFail()
	{
		return DispatcherSessionItem.fail.getMandatory(this);
	}

	/**
	 * Returns the value of {@link #dispatchCountCommitted}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getDispatchCountCommitted()
	{
		return DispatcherSessionItem.dispatchCountCommitted.getMandatory(this);
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
		DispatcherSessionItem.dispatchCountCommitted.set(this,dispatchCountCommitted);
	}

	/**
	 * Dispatch by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="dispatch")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherSessionItem.toTarget.dispatch(DispatcherSessionItem.class,config,ctx);
	}

	/**
	 * Returns, whether this item is yet to be dispatched by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isPending")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isToTargetPending()
	{
		return DispatcherSessionItem.toTarget.isPending(this);
	}

	/**
	 * Sets whether this item is yet to be dispatched by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setPending")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setToTargetPending(final boolean pending)
	{
		DispatcherSessionItem.toTarget.setPending(this,pending);
	}

	/**
	 * Returns, whether this item is allowed to be purged by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNoPurge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isToTargetNoPurge()
	{
		return DispatcherSessionItem.toTarget.isNoPurge(this);
	}

	/**
	 * Sets whether this item is allowed to be purged by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setNoPurge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setToTargetNoPurge(final boolean noPurge)
	{
		DispatcherSessionItem.toTarget.setNoPurge(this,noPurge);
	}

	/**
	 * Returns the date, this item was last successfully dispatched by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastSuccessDate")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.util.Date getToTargetLastSuccessDate()
	{
		return DispatcherSessionItem.toTarget.getLastSuccessDate(this);
	}

	/**
	 * Returns the milliseconds, this item needed to be last successfully dispatched by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastSuccessElapsed")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Long getToTargetLastSuccessElapsed()
	{
		return DispatcherSessionItem.toTarget.getLastSuccessElapsed(this);
	}

	/**
	 * Returns the attempts to dispatch this item by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getRuns")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetRuns()
	{
		return DispatcherSessionItem.toTarget.getRuns(this);
	}

	/**
	 * Returns the failed attempts to dispatch this item by {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getFailures")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetFailures()
	{
		return DispatcherSessionItem.toTarget.getFailures(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="purge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void purgeToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherSessionItem.toTarget.purge(properties,ctx);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="purge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void purgeToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.Condition restriction,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		DispatcherSessionItem.toTarget.purge(properties,restriction,ctx);
	}

	/**
	 * Returns the parent field of the run type of {@link #toTarget}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="RunParent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<DispatcherSessionItem> toTargetRunParent()
	{
		return DispatcherSessionItem.toTarget.getRunParent(DispatcherSessionItem.class);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dispatcherSessionItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DispatcherSessionItem> TYPE = com.exedio.cope.TypesBound.newType(DispatcherSessionItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DispatcherSessionItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
