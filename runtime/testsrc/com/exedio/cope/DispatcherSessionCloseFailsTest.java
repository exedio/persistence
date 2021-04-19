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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Dispatcher;
import com.exedio.cope.pattern.Dispatcher.Config;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobContexts;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DispatcherSessionCloseFailsTest
{
	@Test void test()
	{
		MyItem.dispatchToTarget(CONFIG, JobContexts.EMPTY);

		MODEL.startTransaction(DispatcherSessionCloseFailsTest.class.getName());
		final MyItem item = new MyItem();
		assertEquals(0, item.getDispatchCountCommitted());
		MODEL.commit();

		final RuntimeException e = assertFails(
				() -> MyItem.dispatchToTarget(CONFIG, JobContexts.EMPTY),
				RuntimeException.class,
				"MyItem.toTarget");

		final Throwable cause = e.getCause();
		assertNotNull(cause);
		assertEquals(IOException.class, cause.getClass());
		assertEquals("toTarget session close", cause.getMessage());

		MODEL.startTransaction(DispatcherSessionCloseFailsTest.class.getName());
		assertEquals(1, item.getDispatchCountCommitted());
		MODEL.commit();
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		static final IntegerField dispatchCountCommitted = new IntegerField().defaultTo(0).min(0);

		@Wrapper(wrap="dispatch", parameters={Config.class, Runnable.class, JobContext.class}, visibility=NONE)
		@SuppressWarnings("Convert2Lambda")
		static final Dispatcher toTarget = Dispatcher.createWithSession(
				() -> new AutoCloseable()
				{
					@Override
					public void close() throws IOException
					{
						throw new IOException("toTarget session close");
					}
				},
				MyItem::dispatch);

		@WrapInterim(methodBody=false)
		private void dispatch(final AutoCloseable session)
		{
			setDispatchCountCommitted(getDispatchCountCommitted()+1);
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getDispatchCountCommitted()
		{
			return MyItem.dispatchCountCommitted.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDispatchCountCommitted(final int dispatchCountCommitted)
				throws
					com.exedio.cope.IntegerRangeViolationException
		{
			MyItem.dispatchCountCommitted.set(this,dispatchCountCommitted);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		static void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
		{
			MyItem.toTarget.dispatch(MyItem.class,config,ctx);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean isToTargetPending()
		{
			return MyItem.toTarget.isPending(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setToTargetPending(final boolean pending)
		{
			MyItem.toTarget.setPending(this,pending);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean isToTargetNoPurge()
		{
			return MyItem.toTarget.isNoPurge(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setToTargetNoPurge(final boolean noPurge)
		{
			MyItem.toTarget.setNoPurge(this,noPurge);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.util.Date getToTargetLastSuccessDate()
		{
			return MyItem.toTarget.getLastSuccessDate(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Long getToTargetLastSuccessElapsed()
		{
			return MyItem.toTarget.getLastSuccessElapsed(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetRuns()
		{
			return MyItem.toTarget.getRuns(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.util.List<com.exedio.cope.pattern.Dispatcher.Run> getToTargetFailures()
		{
			return MyItem.toTarget.getFailures(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		static void purgeToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
		{
			MyItem.toTarget.purge(properties,ctx);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		static void purgeToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.DispatcherPurgeProperties properties,@javax.annotation.Nonnull final com.exedio.cope.Condition restriction,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
		{
			MyItem.toTarget.purge(properties,restriction,ctx);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static com.exedio.cope.ItemField<MyItem> toTargetRunParent()
		{
			return MyItem.toTarget.getRunParent(MyItem.class);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	@BeforeEach
	final void setUp()
	{
		MODEL.connect(ConnectProperties.create(TestSources.minimal()));
		setupSchemaMinimal(MODEL);
	}

	@AfterEach
	final void tearDown()
	{
		if(MODEL.isConnected())
		{
			MODEL.tearDownSchema();
			MODEL.disconnect();
		}
	}

	private static final Config CONFIG = new Config();
}
