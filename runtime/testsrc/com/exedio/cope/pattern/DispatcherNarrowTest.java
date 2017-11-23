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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class DispatcherNarrowTest extends TestWithEnvironment
{
	public DispatcherNarrowTest()
	{
		super(MODEL);
	}

	MyItem item1, item2, item3, item4;

	@Before public final void setUp()
	{
		item1 = new MyItem(false);
		item2 = new MyItem(true);
		item3 = new MyItem(false);
		item4 = new MyItem(true);
	}

	@Test public void test()
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

	@After public void afterEach()
	{
		MyItem.toTarget.reset();
	}

	@WrapperType(indent=2, comments=false)
	static final class MyItem extends Item implements Dispatchable
	{
		@Wrapper(visibility=Visibility.NONE, wrap="get")
		static final BooleanField dropByNarrowCondition = new BooleanField();

		@Wrapper(visibility=Visibility.NONE, wrap="*")
		@Wrapper(visibility=Visibility.DEFAULT, wrap="dispatch", parameters={Config.class, JobContext.class})
		@Wrapper(visibility=Visibility.DEFAULT, wrap="isPending")
		@Wrapper(visibility=Visibility.DEFAULT, wrap="getLastSuccessDate")
		static final Dispatcher toTarget = new Dispatcher();

		@Override
		public void dispatch(final Dispatcher dispatcher)
		{
			assertSame(toTarget, dispatcher);
		}


		@javax.annotation.Generated("com.exedio.cope.instrument")
		MyItem(
					final boolean dropByNarrowCondition)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.dropByNarrowCondition.map(dropByNarrowCondition),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setDropByNarrowCondition(final boolean dropByNarrowCondition)
		{
			MyItem.dropByNarrowCondition.set(this,dropByNarrowCondition);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final void dispatchToTarget(@javax.annotation.Nonnull final com.exedio.cope.pattern.Dispatcher.Config config,@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
		{
			MyItem.toTarget.dispatch(MyItem.class,config,ctx);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final boolean isToTargetPending()
		{
			return MyItem.toTarget.isPending(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		final java.util.Date getToTargetLastSuccessDate()
		{
			return MyItem.toTarget.getLastSuccessDate(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}
