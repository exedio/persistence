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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.BooleanField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobContexts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DispatchableRependWithoutPurgeTest extends TestWithEnvironment
{
	@Test void testSuccess()
	{
		final AnItem item = new AnItem();
		assertIt(true, 0, 0, item);

		dispatch();
		assertIt(false, 1, 1, item);

		dispatch();
		assertIt(false, 1, 1, item);

		item.setToTargetPending(true);
		item.setDispatchFails(true);
		assertIt(true, 1, 1, item);

		dispatch();
		assertIt(true, 1, 2, item);

		dispatch();
		assertIt(false, 1, 3, item); // failureLimit would be reset without withoutPurge()

		dispatch();
		assertIt(false, 1, 3, item);
	}

	@Test void testFailure()
	{
		final AnItem item = new AnItem();
		item.setDispatchFails(true);
		assertIt(true, 0, 0, item);

		dispatch();
		assertIt(true, 0, 1, item);

		dispatch();
		assertIt(true, 0, 2, item);

		dispatch();
		assertIt(false, 0, 3, item);

		dispatch();
		assertIt(false, 0, 3, item);

		item.setToTargetPending(true);
		assertIt(true, 0, 3, item);

		dispatch();
		assertIt(false, 0, 4, item); // failureLimit would be reset without withoutPurge()

		dispatch();
		assertIt(false, 0, 4, item);
	}


	private static void assertIt(
			final boolean pending,
			final int dispatchCount,
			final int runCount,
			final AnItem item)
	{
		assertAll(
				() -> assertEquals(pending, item.isToTargetPending(), "pending"),
				() -> assertEquals(dispatchCount, item.getDispatchCount(), "dispatchCount"),
				() -> assertEquals(runCount, item.getToTargetRuns().size(), "runCount"));
	}

	private void dispatch()
	{
		model.commit();
		AnItem.dispatchToTarget(new Dispatcher.Config(3, 100), JobContexts.EMPTY);
		model.startTransaction(DispatchableDeferrableTest.class.getName());
	}


	@WrapperType(indent=2)
	private static final class AnItem extends Item
	{
		@Wrapper(wrap="dispatch", parameters={Dispatcher.Config.class, Runnable.class, JobContext.class}, visibility=Visibility.NONE)
		static final Dispatcher toTarget = Dispatcher.create(AnItem::dispatch).withoutPurgeLEGACY();
		static final BooleanField dispatchFails = new BooleanField().defaultTo(false);
		static final IntegerField dispatchCount = new IntegerField().defaultTo(0);

		@WrapInterim(methodBody=false)
		private void dispatch()
		{
			if(getDispatchFails())
				throw new RuntimeException("dispatch " + this);
			setDispatchCount(getDispatchCount()+1);
		}


		/**
		 * Creates a new AnItem with all the fields initially needed.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		/**
		 * Creates a new AnItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Dispatch by {@link #toTarget}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="dispatch")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		static void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
		{
			AnItem.toTarget.dispatch(AnItem.class,config,ctx);
		}

		/**
		 * Returns, whether this item is yet to be dispatched by {@link #toTarget}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isPending")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean isToTargetPending()
		{
			return AnItem.toTarget.isPending(this);
		}

		/**
		 * Sets whether this item is yet to be dispatched by {@link #toTarget}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setPending")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setToTargetPending(final boolean pending)
		{
			AnItem.toTarget.setPending(this,pending);
		}

		/**
		 * Returns the date, this item was last successfully dispatched by {@link #toTarget}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastSuccessDate")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.util.Date getToTargetLastSuccessDate()
		{
			return AnItem.toTarget.getLastSuccessDate(this);
		}

		/**
		 * Returns the milliseconds, this item needed to be last successfully dispatched by {@link #toTarget}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastSuccessElapsed")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Long getToTargetLastSuccessElapsed()
		{
			return AnItem.toTarget.getLastSuccessElapsed(this);
		}

		/**
		 * Returns the attempts to dispatch this item by {@link #toTarget}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getRuns")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetRuns()
		{
			return AnItem.toTarget.getRuns(this);
		}

		/**
		 * Returns the failed attempts to dispatch this item by {@link #toTarget}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getFailures")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetFailures()
		{
			return AnItem.toTarget.getFailures(this);
		}

		/**
		 * Returns the parent field of the run type of {@link #toTarget}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="RunParent")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static com.exedio.cope.ItemField<AnItem> toTargetRunParent()
		{
			return AnItem.toTarget.getRunParent(AnItem.class);
		}

		/**
		 * Returns the value of {@link #dispatchFails}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean getDispatchFails()
		{
			return AnItem.dispatchFails.getMandatory(this);
		}

		/**
		 * Sets a new value for {@link #dispatchFails}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDispatchFails(final boolean dispatchFails)
		{
			AnItem.dispatchFails.set(this,dispatchFails);
		}

		/**
		 * Returns the value of {@link #dispatchCount}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getDispatchCount()
		{
			return AnItem.dispatchCount.getMandatory(this);
		}

		/**
		 * Sets a new value for {@link #dispatchCount}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDispatchCount(final int dispatchCount)
		{
			AnItem.dispatchCount.set(this,dispatchCount);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for anItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(AnItem.TYPE);

	public DispatchableRependWithoutPurgeTest()
	{
		super(MODEL);
	}

	@AfterEach void afterEach()
	{
		AnItem.toTarget.reset();
	}
}
