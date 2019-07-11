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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.BooleanField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.util.JobContexts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DispatchableDeferrableTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE);

	public DispatchableDeferrableTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final AnItem item1 = new AnItem();
		final AnItem item2 = new AnItem();
		final AnItem item3 = new AnItem();
		assertIt(0, 0, 0, item1);
		assertIt(0, 0, 0, item2);
		assertIt(0, 0, 0, item3);

		dispatch();
		assertIt(1, 0, 0, item1);
		assertIt(1, 0, 0, item2);
		assertIt(1, 0, 0, item3);

		item1.setDeferred(false);
		dispatch();
		assertIt(2, 1, 1, item1);
		assertIt(2, 0, 0, item2);
		assertIt(2, 0, 0, item3);

		dispatch();
		assertIt(2, 1, 1, item1);
		assertIt(3, 0, 0, item2);
		assertIt(3, 0, 0, item3);

		item2.setDeferred(false);
		item3.setDeferred(false);
		dispatch();
		assertIt(2, 1, 1, item1);
		assertIt(4, 1, 1, item2);
		assertIt(4, 1, 1, item3);
	}

	private static void assertIt(
			final int deferredCount,
			final int dispatchCount,
			final int runCount,
			final AnItem item)
	{
		assertEquals(deferredCount, item.getDeferredCount(), "deferredCount");
		assertEquals(dispatchCount, item.getDispatchCount(), "dispatchCount");
		assertEquals(runCount, item.getToTargetRuns().size(), "runCount");
	}

	private void dispatch()
	{
		model.commit();
		AnItem.dispatchToTarget(new Dispatcher.Config(), JobContexts.EMPTY);
		model.startTransaction(DispatchableDeferrableTest.class.getName());
	}


	static final class AnItem extends Item
	{
		static final Dispatcher toTarget = Dispatcher.create(
				AnItem::dispatch,
				AnItem::isDeferred,
				(i, cause) -> { throw new RuntimeException(cause); });
		static final BooleanField deferred = new BooleanField().defaultTo(true);
		static final IntegerField deferredCount = new IntegerField().defaultTo(0);
		static final IntegerField dispatchCount = new IntegerField().defaultTo(0);

		@WrapInterim(methodBody=false)
		private boolean isDeferred()
		{
			assertIt();
			setDeferredCount(getDeferredCount()+1);
			return getDeferred();
		}

		@WrapInterim(methodBody=false)
		private void dispatch()
		{
			assertIt();
			setDispatchCount(getDispatchCount()+1);
		}

		private void assertIt()
		{
			assertTrue(MODEL.hasCurrentTransaction());
			assertEquals(toTarget.getID() + " dispatch " + getCopeID(), MODEL.currentTransaction().getName());
		}


	/**
	 * Creates a new AnItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AnItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Dispatch by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="dispatch")
	static void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		AnItem.toTarget.dispatch(AnItem.class,config,ctx);
	}

	/**
	 * Dispatch by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="dispatch")
	static void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final java.lang.Runnable probe,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		AnItem.toTarget.dispatch(AnItem.class,config,probe,ctx);
	}

	/**
	 * Returns, whether this item is yet to be dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isPending")
	boolean isToTargetPending()
	{
		return AnItem.toTarget.isPending(this);
	}

	/**
	 * Sets whether this item is yet to be dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setPending")
	void setToTargetPending(final boolean pending)
	{
		AnItem.toTarget.setPending(this,pending);
	}

	/**
	 * Returns, whether this item is allowed to be purged by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNoPurge")
	boolean isToTargetNoPurge()
	{
		return AnItem.toTarget.isNoPurge(this);
	}

	/**
	 * Sets whether this item is allowed to be purged by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setNoPurge")
	void setToTargetNoPurge(final boolean noPurge)
	{
		AnItem.toTarget.setNoPurge(this,noPurge);
	}

	/**
	 * Returns the date, this item was last successfully dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastSuccessDate")
	@javax.annotation.Nullable
	java.util.Date getToTargetLastSuccessDate()
	{
		return AnItem.toTarget.getLastSuccessDate(this);
	}

	/**
	 * Returns the milliseconds, this item needed to be last successfully dispatched by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastSuccessElapsed")
	@javax.annotation.Nullable
	java.lang.Long getToTargetLastSuccessElapsed()
	{
		return AnItem.toTarget.getLastSuccessElapsed(this);
	}

	/**
	 * Returns the attempts to dispatch this item by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getRuns")
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetRuns()
	{
		return AnItem.toTarget.getRuns(this);
	}

	/**
	 * Returns the failed attempts to dispatch this item by {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getFailures")
	@javax.annotation.Nonnull
	java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetFailures()
	{
		return AnItem.toTarget.getFailures(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	static void purgeToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		AnItem.toTarget.purge(properties,ctx);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	static void purgeToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.Condition restriction,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		AnItem.toTarget.purge(properties,restriction,ctx);
	}

	/**
	 * Returns the parent field of the run type of {@link #toTarget}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="RunParent")
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<AnItem> toTargetRunParent()
	{
		return AnItem.toTarget.getRunParent(AnItem.class);
	}

	/**
	 * Returns the value of {@link #deferred}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean getDeferred()
	{
		return AnItem.deferred.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #deferred}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDeferred(final boolean deferred)
	{
		AnItem.deferred.set(this,deferred);
	}

	/**
	 * Returns the value of {@link #deferredCount}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getDeferredCount()
	{
		return AnItem.deferredCount.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #deferredCount}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDeferredCount(final int deferredCount)
	{
		AnItem.deferredCount.set(this,deferredCount);
	}

	/**
	 * Returns the value of {@link #dispatchCount}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getDispatchCount()
	{
		return AnItem.dispatchCount.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #dispatchCount}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDispatchCount(final int dispatchCount)
	{
		AnItem.dispatchCount.set(this,dispatchCount);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	@AfterEach void afterEach()
	{
		AnItem.toTarget.reset();
	}
}
