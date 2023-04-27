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

package com.exedio.cope.misc;

import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.junit.HolderExtension;
import com.exedio.cope.misc.SlowTransactionLogger.Properties;
import com.exedio.cope.tojunit.AssertionFailedClock;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.Sources;
import java.time.Clock;
import java.time.Duration;
import java.util.LinkedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MainRule.Tag
@ExtendWith(SlowTransactionLoggerTest.NowClock.class)
public class SlowTransactionLoggerTest
{
	private final LogRule log = new LogRule(SlowTransactionLogger.class);

	@Test void testError(final NowClock now)
	{
		final Thread t = Thread.currentThread();
		final Transaction tx = MODEL.startTransaction("tx1Name");
		final long txStart = tx.getStartDate().getTime();
		now.override(EMPTY_PROPS.thresholdError);
		final StackTraceElement[] stackTrace = run();
		log.assertError(
				"exceeds threshold " +
				"id=" + tx.getID() + " age=" + (now.lastResult - txStart) + "ms name=\"tx1Name\" inv=0 pre=0 predup=0 post=0 postdup=0 " +
				"threadId=" + t.getId() + " prio=" + t.getPriority() + " state=RUNNABLE threadName=\"" + t.getName() + "\" " +
				"stackTrace=\"" + SlowTransactionLogger.toString(stackTrace) + "\"");

		MODEL.commit();
		run();
		log.assertEmpty();
	}

	@Test void testWarn(final NowClock now)
	{
		final Thread t = Thread.currentThread();
		final Transaction tx = MODEL.startTransaction("tx2Name");
		final long txStart = tx.getStartDate().getTime();
		now.override(EMPTY_PROPS.thresholdWarn);
		final StackTraceElement[] stackTrace = run();
		log.assertWarn(
				"exceeds threshold " +
				"id=" + tx.getID() + " age=" + (now.lastResult - txStart) + "ms name=\"tx2Name\" inv=0 pre=0 predup=0 post=0 postdup=0 " +
				"threadId=" + t.getId() + " prio=" + t.getPriority() + " state=RUNNABLE threadName=\"" + t.getName() + "\" " +
				"stackTrace=\"" + SlowTransactionLogger.toString(stackTrace) + "\"");

		assertSame(tx, MODEL.leaveTransaction());
		run();
		log.assertWarn(
				"exceeds threshold " +
				"id=" + tx.getID() + " age=" + (now.lastResult - txStart) + "ms name=\"tx2Name\" inv=0 pre=0 predup=0 post=0 postdup=0 " +
				"unbound");

		MODEL.joinTransaction(tx);
		MODEL.commit();
		run();
		log.assertEmpty();
	}

	@Test void testSuppress(final NowClock now)
	{
		final Thread t = Thread.currentThread();
		final Transaction tx = MODEL.startTransaction("tx1Name");
		final long txStart = tx.getStartDate().getTime();
		now.override(EMPTY_PROPS.thresholdError);
		final StackTraceElement[] stackTrace = run();
		log.assertError(
				"exceeds threshold " +
				"id=" + tx.getID() + " age=" + (now.lastResult - txStart) + "ms name=\"tx1Name\" inv=0 pre=0 predup=0 post=0 postdup=0 " +
				"threadId=" + t.getId() + " prio=" + t.getPriority() + " state=RUNNABLE threadName=\"" + t.getName() + "\" " +
				"stackTrace=\"" + SlowTransactionLogger.toString(stackTrace) + "\"");
		SlowTransactionLogger.run(MODEL, EMPTY_PROPS, currentTx ->
		{
			assertSame(tx, currentTx);
			return true;
		});
		log.assertEmpty();
	}

	private static final Properties EMPTY_PROPS = Properties.factory().create(Sources.EMPTY);

	public static final class NowClock extends HolderExtension<Clock>
	{
		long lastResult = -1;

		public NowClock()
		{
			super(SlowTransactionLogger.now);
		}

		void override(final Duration offset)
		{
			override(new AssertionFailedClock()
			{
				@Override public long millis()
				{
					lastResult = System.currentTimeMillis() + offset.toMillis();
					return lastResult;
				}
			});
		}
	}

	private static StackTraceElement[] run()
	{
		SlowTransactionLogger.run(MODEL, EMPTY_PROPS); final StackTraceElement[] original = Thread.currentThread().getStackTrace(); // MUST be on the same line, otherwise stack trace will not match

		final LinkedList<StackTraceElement> result = new LinkedList<>(asList(original));
		result.add(1, new StackTraceElement( // position 0 contains java.lang.Thread.getStackTrace(Thread.java:1602)
				SlowTransactionLogger.class.getName(), "run",
				SlowTransactionLogger.class.getSimpleName() + ".java", 90));
		result.add(2, new StackTraceElement(
				SlowTransactionLogger.class.getName(), "run",
				SlowTransactionLogger.class.getSimpleName() + ".java", 42));
		return result.toArray(new StackTraceElement[]{});
	}


	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static class MyItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@BeforeEach void setUp()
	{
		MODEL.connect(ConnectProperties.create(TestSources.minimal()));
	}

	@AfterEach void tearDown()
	{
		MODEL.rollbackIfNotCommitted();
		MODEL.disconnect();
	}

	static final Model MODEL = new Model(MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(SlowTransactionLoggerTest.class, "MODEL");
	}
}
