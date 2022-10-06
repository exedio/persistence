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

import static java.util.Objects.requireNonNull;

import gnu.trove.TLongHashSet;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.function.ToDoubleFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ChangeListenerDispatcher implements Runnable
{
	private static final Logger logger = LoggerFactory.getLogger(ChangeListenerDispatcher.class);

	private final Types types;
	private final ChangeListeners manager;
	private final LimitedQueue<ChangeEvent> queue;

	private final ThreadSwarm threads;
	private volatile boolean threadRun = true;
	private final Counter overflow;
	private final Counter exception;

	ChangeListenerDispatcher(
			final MetricsBuilder metricsTemplate,
			final Types types,
			final ChangeListeners manager,
			final ConnectProperties properties)
	{
		this.types = types;
		this.manager = manager;
		this.queue = new LimitedQueue<>(properties.changeListenersQueueCapacity);

		//noinspection ThisEscapedInObjectConstruction
		this.threads = new ThreadSwarm(
				this,
				"COPE Change Listener Dispatcher " + metricsTemplate.modelName,
				properties.chaListThreads
		);
		threads.start();

		final Metrics metrics = new Metrics(metricsTemplate);
		overflow  = metrics.counter("overflow",          "How often the queue overflows, because ChangeEvents coming in faster than they can be dispatched to ChangeListeners.", Tags.empty());
		exception = metrics.counter("dispatchEventFail", "How often dispatching a ChangeEvent to all ChangeListeners fails.", Tags.empty());
		metrics.gauge(d -> d.queue.capacity,"capacity",  "How many ChangeEvents the queue can hold.");
		metrics.gauge(d -> d.queue.size(), "pending",    "How many ChangeEvents are in the queue waiting to be dispatched.");
	}

	private static final class Metrics
	{
		final MetricsBuilder back;

		Metrics(final MetricsBuilder metricsTemplate)
		{
			this.back = metricsTemplate.name(ChangeListener.class);
		}

		Counter counter(
				final String nameSuffix,
				final String description,
				final Tags tags)
		{
			return back.counter(nameSuffix, description, tags);
		}

		void gauge(
				final ToDoubleFunction<ChangeListenerDispatcher> f,
				final String nameSuffix,
				final String description)
		{
			back.gaugeConnect(
					c -> f.applyAsDouble(c.changeListenerDispatcher),
					nameSuffix, description);
		}
	}

	ChangeListenerDispatcherInfo getInfo()
	{
		return new ChangeListenerDispatcherInfo(overflow, exception, queue.size());
	}

	void invalidate(final TLongHashSet[] invalidations, final TransactionInfo transactionInfo)
	{
		if(!manager.isUsed())
			return;

		final ChangeEvent event =
			new ChangeEvent(types.activate(invalidations), transactionInfo);

		if(!queue.offer(event))
		{
			overflow.increment();
			if(logger.isErrorEnabled())
				logger.error("overflows {} {}", overflow.count(), transactionInfo);
		}
	}

	boolean interrupts()
	{
		return !threadRun;
	}

	@Override
	public void run()
	{
		while(threadRun)
		{
			try
			{
				if(!threadRun)
				{
					logTerminate();
					return;
				}

				final ChangeEvent event = requireNonNull(queue.take());

				if(!threadRun)
				{
					logTerminate();
					return;
				}

				manager.dispatch(event, this);
			}
			catch(final InterruptedException ignored)
			{
				// do nothing, thread will be
				// terminated by threadRun flag
			}
			catch(final Exception | AssertionError e)
			{
				exception.increment();
				if(logger.isErrorEnabled())
					logger.error("ChangeListenerDispatcher", e);
			}
		}
		logTerminate();
	}

	private static void logTerminate()
	{
		if(logger.isInfoEnabled())
		{
			final Thread t = Thread.currentThread();
			logger.info(MessageFormat.format("{0} ({1}) terminates.", t.getName(), t.getId()));
		}
	}

	void addThreadControllers(final ArrayList<ThreadController> result)
	{
		threads.addThreadControllers(result);
	}

	void startClose()
	{
		threadRun = false;
		threads.interrupt();
	}

	void joinClose()
	{
		threads.join();
	}
}
