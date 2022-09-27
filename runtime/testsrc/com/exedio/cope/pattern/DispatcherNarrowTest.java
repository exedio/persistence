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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Dispatcher.Config;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobContexts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DispatcherNarrowTest extends TestWithEnvironment
{
	public DispatcherNarrowTest()
	{
		super(MODEL);
	}

	MyItem item1, item2, item3, item4;

	@BeforeEach final void setUp()
	{
		item1 = new MyItem(false);
		item2 = new MyItem(true);
		item3 = new MyItem(false);
		item4 = new MyItem(true);
	}

	@Test void test()
	{
		assertPending(item1);
		assertPending(item2);
		assertPending(item3);
		assertPending(item4);

		dispatch();
		assertSuccess(item1);
		assertPending(item2);
		assertSuccess(item3);
		assertPending(item4);

		dispatch();
		assertSuccess(item1);
		assertPending(item2);
		assertSuccess(item3);
		assertPending(item4);

		item2.setDropByNarrowCondition(false);
		dispatch();
		assertSuccess(item1);
		assertSuccess(item2);
		assertSuccess(item3);
		assertPending(item4);

		dispatch();
		assertSuccess(item1);
		assertSuccess(item2);
		assertSuccess(item3);
		assertPending(item4);
	}

	private void dispatch()
	{
		model.commit();
		MyItem.dispatchToTarget(config, JobContexts.EMPTY);
		model.startTransaction("DispatcherNarrowTest");
	}

	private static final Config config = new Config().narrow(
			MyItem.dropByNarrowCondition.equal(false));

	private static void assertPending(final MyItem item)
	{
		assertTrue(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
	}

	private static void assertSuccess(final MyItem item)
	{
		assertFalse(item.isToTargetPending());
		assertNotNull(item.getToTargetLastSuccessDate());
	}

	@AfterEach void afterEach()
	{
		MyItem.toTarget.reset();
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@Wrapper(visibility=Visibility.NONE, wrap="get")
		static final BooleanField dropByNarrowCondition = new BooleanField();

		@Wrapper(visibility=Visibility.NONE, wrap="*")
		@Wrapper(visibility=Visibility.DEFAULT, wrap="dispatch", parameters={Config.class, JobContext.class})
		@Wrapper(visibility=Visibility.DEFAULT, wrap="isPending")
		@Wrapper(visibility=Visibility.DEFAULT, wrap="getLastSuccessDate")
		static final Dispatcher toTarget = Dispatcher.create(i -> {});


		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					final boolean dropByNarrowCondition)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.dropByNarrowCondition.map(dropByNarrowCondition),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDropByNarrowCondition(final boolean dropByNarrowCondition)
		{
			MyItem.dropByNarrowCondition.set(this,dropByNarrowCondition);
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
		@javax.annotation.Nullable
		java.util.Date getToTargetLastSuccessDate()
		{
			return MyItem.toTarget.getLastSuccessDate(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}
