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

import gnu.trove.TLongHashSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ChangeListenerDispatcher implements Runnable
{
	private static final Logger logger = LoggerFactory.getLogger(ChangeListenerDispatcher.class);

	private final Types types;
	private final ChangeListeners manager;
	private final LimitedQueue<ChangeEvent> queue;

	private final ThreadSwarm threads;
	private boolean threadRun = true;
	private final VolatileLong overflow = new VolatileLong();
	private final VolatileLong exception = new VolatileLong();

	ChangeListenerDispatcher(
			final Types types,
			final String modelName,
			final ChangeListeners manager,
			final ConnectProperties properties)
	{
		this.types = types;
		this.manager = manager;
		this.queue = new LimitedQueue<>(properties.changeListenersQueueCapacity);

		this.threads = new ThreadSwarm(
				this,
				"COPE Change Listener Dispatcher " + modelName,
				properties.chaListThreads
		);
		threads.start();
	}

	ChangeListenerDispatcherInfo getInfo()
	{
		return new ChangeListenerDispatcherInfo(overflow.get(), exception.get(), queue.size());
	}

	void invalidate(final TLongHashSet[] invalidations, final TransactionInfo transactionInfo)
	{
		if(!manager.isUsed())
			return;

		final ChangeEvent event =
			new ChangeEvent(types.activate(invalidations), transactionInfo);

		if(!queue.offer(event))
		{
			overflow.inc();
			if(logger.isErrorEnabled())
				logger.error("overflows {} {}", overflow.get(), transactionInfo);
		}
	}

	boolean requestedToStop()
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

				final ChangeEvent event = queue.take();
				if(event==null)
					throw new RuntimeException("null take");

				if(!threadRun)
				{
					logTerminate();
					return;
				}

				manager.dispatch(event, this);
			}
			catch(final InterruptedException e)
			{
				// do nothing, thread will be
				// terminated by threadRun flag
			}
			catch(final Exception | AssertionError e)
			{
				exception.inc();
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
