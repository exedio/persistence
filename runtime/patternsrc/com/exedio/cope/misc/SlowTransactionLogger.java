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

import static com.exedio.cope.util.Check.requireAtLeast;
import static com.exedio.cope.util.Check.requireNonNegative;
import static java.lang.Math.subtractExact;
import static java.time.Duration.ofSeconds;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
import com.exedio.cope.util.Holder;
import java.time.Duration;
import java.util.Collection;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SlowTransactionLogger
{
	public static void run(
			final Model model,
			final Properties properties)
	{
		run(model, properties, tx -> false);
	}

	/**
	 * @param suppressed
	 * May be used to omit certain transactions from consideration for logging.
	 */
	public static void run(
			final Model model,
			final Properties properties,
			final Predicate<Transaction> suppressed)
	{
		requireNonNull(model, "model");
		requireNonNull(properties, "properties");
		requireNonNull(suppressed, "suppressed");

		if(!logger.isErrorEnabled())
			return;

		final Collection<Transaction> openTransactions = model.getOpenTransactions();
		if(openTransactions.isEmpty())
			return;

		final long date = now.get().getAsLong();
		final long thresholdWarn  = subtractExact(date, properties.thresholdWarn .toMillis());
		final long thresholdError = subtractExact(date, properties.thresholdError.toMillis());
		for(final Transaction tx : openTransactions)
		{
			final long start = tx.getStartDate().getTime();
			if(start<=thresholdWarn)
			{
				if(suppressed.test(tx))
					continue;

				final Thread t = tx.getBoundThread();
				final long age = date - start;
				final String format = t==null ? FORMAT_THREAD_NONE : FORMAT_THREAD;
				final Object[] args = t==null
						? new Object[] {
								tx.getID(), age, tx.getName(),
								tx.getInvalidationSize(),
								tx.getPreCommitHookCount (), tx.getPreCommitHookDuplicates (),
								tx.getPostCommitHookCount(), tx.getPostCommitHookDuplicates() }
						: new Object[] {
								tx.getID(), age, tx.getName(),
								tx.getInvalidationSize(),
								tx.getPreCommitHookCount (), tx.getPreCommitHookDuplicates (),
								tx.getPostCommitHookCount(), tx.getPostCommitHookDuplicates(),
								t.getId(), t.getPriority(), t.getState(), t.getName(), toString(t.getStackTrace()) };
				if(start<=thresholdError)
					logger.error(format, args);
				else
					logger.warn(format, args);
			}
		}
	}

	static final Holder<LongSupplier> now = new Holder<>(System::currentTimeMillis);

	// almost equal to code in SamplerThread
	static String toString(final StackTraceElement[] trace)
	{
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(final StackTraceElement e : trace)
		{
			if(first)
				first = false;
			else
				sb.append(' ');

			sb.
					append(e.getClassName()).
					append('.').
					append(e.getMethodName()).
					append('(').append(e.getFileName()).
					append(':').
					append(e.getLineNumber()).append(')');
		}
		return !sb.isEmpty() ? sb.toString() : null;
	}

	private static final String FORMAT = "exceeds threshold id={} age={}ms name=\"{}\" inv={} pre={} predup={} post={} postdup={} "; // https://www.cloudbees.com/blog/logfmt-a-log-format-thats-easy-to-read-and-write
	private static final String FORMAT_THREAD = FORMAT + "threadId={} prio={} state={} threadName=\"{}\" stackTrace=\"{}\"";
	private static final String FORMAT_THREAD_NONE = FORMAT + "unbound";

	public static final class Properties extends com.exedio.cope.util.Properties
	{
		final Duration thresholdWarn;
		final Duration thresholdError;

		private static Duration max(final Duration a, final Duration b)
		{
			return a.compareTo(b)>0 ? a : b;
		}

		public static Factory<Properties> factory()
		{
			return new F(ofSeconds(10), ofSeconds(30));
		}

		public static Factory<Properties> factory(
				final Duration thresholdWarnDefault,
				final Duration thresholdErrorDefault)
		{
			return new F(thresholdWarnDefault, thresholdErrorDefault);
		}

		private Properties(final Source source, final F factory)
		{
			super(source);
			thresholdWarn  = value("threshold.warn",  factory.thresholdWarnDefault, Duration.ZERO);
			thresholdError = value("threshold.error", max(factory.thresholdErrorDefault, thresholdWarn), thresholdWarn);
		}

		private record F(
				Duration thresholdWarnDefault,
				Duration thresholdErrorDefault)
				implements Factory<Properties>
		{
			private F
			{
				requireNonNegative(thresholdWarnDefault, "thresholdWarnDefault");
				requireAtLeast(thresholdErrorDefault, "thresholdErrorDefault", thresholdWarnDefault);
			}

			@Override
			public Properties create(final Source source)
			{
				return new Properties(source, this);
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(SlowTransactionLogger.class);


	private SlowTransactionLogger()
	{
		// prevent instantiation
	}
}
