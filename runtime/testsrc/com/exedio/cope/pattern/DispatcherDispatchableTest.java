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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfoAssert.assertNoUpdateCounterColumn;
import static com.exedio.cope.misc.TimeUtil.toMillies;
import static java.lang.System.nanoTime;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Dispatcher.Result;
import com.exedio.cope.pattern.Dispatcher.Run;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobContexts;
import com.exedio.cope.util.JobStop;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DispatcherDispatchableTest extends TestWithEnvironment
{
	private static final Dispatcher.Config config = new Dispatcher.Config(3, 2);

	public DispatcherDispatchableTest()
	{
		super(MODEL);
	}

	private final ClockRule clockRule = new ClockRule();

	MyItem item1;
	MyItem item2;
	MyItem item3;
	MyItem item4;

	@BeforeEach final void setUp()
	{
		item1 = new MyItem("item1", false);
		item2 = new MyItem("item2", true);
		item3 = new MyItem("item3", false);
		item4 = new MyItem("item4", true);
	}

	@Test void testIt()
	{
		assertNoUpdateCounterColumn(MyItem.toTarget.getRunType());
		assertEquals("success", getColumnName(MyItem.toTarget.getRunResult()));

		assertPending(item1);
		assertPending(item2);
		assertPending(item3);
		assertPending(item4);

		final Date[] d1 = dispatch();
		assertSuccess(item1, 1, d1[0], success(d1[0]));
		assertPending(item2, failure(d1[1]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertPending(item4, failure(d1[3]));

		final Date[] d2 = dispatch();
		assertSuccess(item1, 1, d1[0], success(d1[0]));
		assertPending(item2, failure(d1[1]), failure(d2[0], 1));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertPending(item4, failure(d1[3]), failure(d2[1], 1));

		MyItem.logs.get(item2).fail = false;
		final Date[] d3 = dispatch();
		assertSuccess(item1, 1, d1[0], success(d1[0]));
		assertSuccess(item2, 1, d3[0], failure(d1[1]), failure(d2[0], 1), success(d3[0]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertFailed (item4, failure(d1[3]), failure(d2[1], 1), finalFailure(d3[1]));

		dispatch();
		assertSuccess(item1, 1, d1[0], success(d1[0]));
		assertSuccess(item2, 1, d3[0], failure(d1[1]), failure(d2[0], 1), success(d3[0]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertFailed (item4, failure(d1[3]), failure(d2[1], 1), finalFailure(d3[1]));

		item1.setToTargetPending(true);
		final Date[] d4 = dispatch();
		assertSuccess(item1, 2, d4[0], success(d1[0]), success(d4[0]));
		assertSuccess(item2, 1, d3[0], failure(d1[1]), failure(d2[0], 1), success(d3[0]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertFailed (item4, failure(d1[3]), failure(d2[1], 1), finalFailure(d3[1]));

		dispatch();
		assertSuccess(item1, 2, d4[0], success(d1[0]), success(d4[0]));
		assertSuccess(item2, 1, d3[0], failure(d1[1]), failure(d2[0], 1), success(d3[0]));
		assertSuccess(item3, 1, d1[2], success(d1[2]));
		assertFailed (item4, failure(d1[3]), failure(d2[1], 1), finalFailure(d3[1]));

	}

	private Date[] dispatch()
	{
		model.commit();
		final CS clock = new CS();
		clockRule.override(clock);
		try
		{
			MyItem.dispatchToTarget(config, JobContexts.EMPTY);
		}
		catch(final JobStop js)
		{
			assertEquals("JC", js.getMessage());
		}
		clockRule.clear();
		model.startTransaction("DispatcherTest");
		return clock.close();
	}

	private static class CS implements Clock.Strategy
	{
		private long date = 1000l*60*60*24*1000;
		private ArrayList<Date> history = new ArrayList<>();

		@Override
		public long currentTimeMillis()
		{
			date += 1000l*60;
			history.add(new Date(date));
			return date;
		}

		Date[] close()
		{
			final Date[] result = history.toArray(new Date[0]);
			history = null;
			return result;
		}
	}

	private static void assertSuccess(
			final MyItem item,
			final int dispatchCountCommitted,
			final Date date,
			final ExpectedRun... expectedRuns)
	{
		assertEquals(false, item.isToTargetPending());
		assertEquals(date, item.getToTargetLastSuccessDate());
		assertIt(dispatchCountCommitted, expectedRuns.length, asList(expectedRuns), item, 0);
	}

	private static void assertPending(
			final MyItem item,
			final ExpectedRun... expectedRuns)
	{
		assertTrue(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
		assertNull(item.getToTargetLastSuccessElapsed());
		assertIt(0, expectedRuns.length, asList(expectedRuns), item, 0);
	}

	private static void assertFailed(
			final MyItem item,
			final ExpectedRun... expectedRuns)
	{
		assertFalse(item.isToTargetPending());
		assertNull(item.getToTargetLastSuccessDate());
		assertNull(item.getToTargetLastSuccessElapsed());
		assertIt(0, expectedRuns.length, asList(expectedRuns), item, 1);
	}

	private static void assertIt(
			final int dispatchCountCommitted,
			final int dispatchCount,
			final List<ExpectedRun> expectedRuns,
			final MyItem item,
			final int notifyFinalFailureCount)
	{
		assertEquals(dispatchCountCommitted, item.getDispatchCountCommitted());
		assertEquals(dispatchCount, MyItem.logs.get(item).dispatchCount);

		final List<Run> actualRuns = item.getToTargetRuns();
		assertTrue(actualRuns.size()<=3);
		assertEquals(expectedRuns.size(), actualRuns.size());

		final List<Long> runsElapsed = MyItem.logs.get(item).dispatchRunElapsed;
		assertEquals(expectedRuns.size(), runsElapsed.size());
		final Iterator<Long> runElapsedIter = runsElapsed.iterator();

		final Iterator<ExpectedRun> expectedRunIter = expectedRuns.iterator();
		final ArrayList<Run> expectedFailures = new ArrayList<>();
		for(final Run actual : actualRuns)
		{
			final Long runElapsed = runElapsedIter.next();
			final ExpectedRun expected = expectedRunIter.next();
			assertSame(MyItem.toTarget, actual.getPattern());
			assertEquals(item, actual.getParent());
			expected.assertIt(actual);
			assertTrue(actual.getElapsed()>=runElapsed.intValue(), actual.getElapsed() + ">=" + runElapsed);
			if(expected.success)
			{
				assertEquals(null, actual.getFailure());
			}
			else
			{
				expectedFailures.add(actual);
				assertTrue(actual.getFailure().startsWith(IOException.class.getName()+": "+item.getBody()), actual.getFailure());
			}
		}
		assertEquals(expectedFailures, item.getToTargetFailures());
		assertEquals(notifyFinalFailureCount, MyItem.logs.get(item).notifyFinalFailureCount);
	}


	private static ExpectedRun success(final Date date)
	{
		return new ExpectedRun(date, 0, Result.success, true);
	}

	private static ExpectedRun failure(final Date date)
	{
		return failure(date, 2);
	}

	private static ExpectedRun failure(final Date date, final int remaining)
	{
		return new ExpectedRun(date, remaining, Result.transientFailure, false);
	}

	private static ExpectedRun finalFailure(final Date date)
	{
		return new ExpectedRun(date, 0, Result.finalFailure, false);
	}

	private record ExpectedRun(
			Date date,
			int remaining,
			Result result,
			boolean success)
	{
		ExpectedRun(
				final Date date,
				final int remaining,
				final Result result,
				final boolean success)
		{
			this.date = new Date(date.getTime()); // Date is not immutable
			this.remaining = remaining;
			this.result = result;
			this.success = success;
			assertNotNull(date);
		}

		void assertIt(final Run actual)
		{
			assertAll(
					() -> assertEquals(date, actual.getDate(), "date"),
					() -> assertEquals(remaining, actual.getRemaining(), "remaining"),
					() -> assertEquals(3, actual.getLimit(), "limit"),
					() -> assertEquals(result, actual.getResult(), "result"),
					() -> assertEquals(success, actual.isSuccess(), "success"));
		}
	}

	@AfterEach void afterEach()
	{
		MyItem.toTarget.reset();
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		static final StringField body = new StringField();
		static final IntegerField dispatchCountCommitted = new IntegerField().defaultTo(0).min(0);

		static final HashMap<MyItem, Log> logs = new HashMap<>();
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
		static final Dispatcher toTarget = Dispatcher.create(MyItem::dispatch, MyItem::notifyFinalFailure);

		@WrapInterim(methodBody=false)
		private void dispatch() throws IOException, InterruptedException
		{
			assertTrue(MODEL.hasCurrentTransaction());
			assertEquals(toTarget.getID() + " dispatch " + getCopeID(), MODEL.currentTransaction().getName());
			setDispatchCountCommitted(getDispatchCountCommitted()+1);
			final Log log = logs.get(this);
			final long start = nanoTime();
			log.dispatchCount++;
			Thread.sleep(5);
			log.dispatchRunElapsed.add(toMillies(nanoTime(), start));
			if(log.fail)
				throw new IOException(getBody());
		}

		@WrapInterim(methodBody=false)
		private void notifyFinalFailure(final Exception cause)
		{
			assertTrue(!MODEL.hasCurrentTransaction());
			assertEquals(IOException.class, cause.getClass());
			logs.get(this).notifyFinalFailureCount++;
		}

		MyItem(final String body, final boolean fail)
		{
			this(body);
			//noinspection ThisEscapedInObjectConstruction
			logs.put(this, new Log(fail));
		}


		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nonnull final java.lang.String body)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.body,body),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getBody()
		{
			return MyItem.body.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setBody(@javax.annotation.Nonnull final java.lang.String body)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.body.set(this,body);
		}

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
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);
}
