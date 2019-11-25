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

import static java.lang.Math.subtractExact;
import static java.time.Duration.ofSeconds;
import static java.time.ZoneOffset.UTC;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Model;
import com.exedio.cope.Transaction;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SlowTransactionLogger
{
	public static void run(
			final Model model,
			final Properties properties)
	{
		requireNonNull(model, "model");
		requireNonNull(properties, "properties");

		if(!logger.isErrorEnabled())
			return;

		final Collection<Transaction> openTransactions = model.getOpenTransactions();
		if(openTransactions.isEmpty())
			return;

		final long date = now.get().millis();
		final long thresholdWarn  = subtractExact(date, properties.thresholdWarn .toMillis());
		final long thresholdError = subtractExact(date, properties.thresholdError.toMillis());
		for(final Transaction tx : openTransactions)
		{
			final long start = tx.getStartDate().getTime();
			if(start<=thresholdWarn)
			{
				final Thread t = tx.getBoundThread();
				final String startDate = DATE_FORMATTER.format(LocalDateTime.ofInstant(tx.getStartDate().toInstant(), UTC));
				final String format = t==null ? FORMAT_THREAD_NONE : FORMAT_THREAD;
				final Object[] args = t==null
						? new Object[] {
								tx.getID(), startDate, tx.getName(),
								tx.getInvalidationSize(),
								tx.getPreCommitHookCount (), tx.getPreCommitHookDuplicates (),
								tx.getPostCommitHookCount(), tx.getPostCommitHookDuplicates() }
						: new Object[] {
								tx.getID(), startDate, tx.getName(),
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

	static final Holder<Clock> now = new Holder<>(Clock.systemUTC());

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	// almost equal to code in SamplerThread
	static String toString(final StackTraceElement[] trace)
	{
		final StringBuilder bf = new StringBuilder();
		for(final StackTraceElement e : trace)
		{
			bf.
					append(' ').
					append(e.getClassName()).
					append('.').
					append(e.getMethodName()).
					append('(').append(e.getFileName()).
					append(':').
					append(e.getLineNumber()).append(')');
		}
		return bf.length()!=0 ? bf.toString() : null;
	}

	private static final String FORMAT = "exceeds threshold id={} started={} {} inv={} pre={}({}) post={}({}) thread ";
	private static final String FORMAT_THREAD = FORMAT + "id={} prio={} state={} {} stackTrace{}";
	private static final String FORMAT_THREAD_NONE = FORMAT + "none";

	public static final class Properties extends com.exedio.cope.util.Properties
	{
		final Duration thresholdWarn  = value("threshold.warn",  ofSeconds(10), Duration.ZERO);
		final Duration thresholdError = value("threshold.error", max(ofSeconds(30), thresholdWarn), thresholdWarn);

		private static Duration max(final Duration a, final Duration b)
		{
			return a.compareTo(b)>0 ? a : b;
		}

		public static Factory<Properties> factory()
		{
			return Properties::new;
		}

		private Properties(final Source source)
		{
			super(source);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(SlowTransactionLogger.class);


	private SlowTransactionLogger()
	{
		// prevent instantiation
	}
}
