/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import gnu.trove.TIntHashSet;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.apache.log4j.Level;

final class ChangeListenerDispatcher implements Runnable
{
	private final Types types;
	private final ChangeListeners manager;
	private final LimitedQueue<ChangeEvent> queue;

	private final ThreadSwarm threads;
	private boolean threadRun = true;
	private volatile long overflow = 0;
	private volatile long exception = 0;

	ChangeListenerDispatcher(
			final Types types,
			final String name,
			final ChangeListeners manager,
			final ConnectProperties properties)
	{
		this.types = types;
		this.manager = manager;
		this.queue = new LimitedQueue<ChangeEvent>(properties.changeListenersQueueCapacity.intValue());

		this.threads = new ThreadSwarm(
				this,
				"COPE Change Listener Dispatcher " + name,
				properties.changeListenersThreadsMax.intValue()
		);
		if(properties.changeListenersPrioritySet.booleanValue())
			threads.setPriority(properties.changeListenersPriority.intValue());
		threads.start(properties.changeListenersThreads.intValue());
	}

	ChangeListenerDispatcherInfo getInfo()
	{
		return new ChangeListenerDispatcherInfo(overflow, exception, queue.size());
	}

	void invalidate(final TIntHashSet[] invalidations, final TransactionInfo transactionInfo)
	{
		if(!manager.isUsed())
			return;

		final ChangeEvent event =
			new ChangeEvent(types.activate(invalidations), transactionInfo);

		if(!queue.offer(event))
		{
			overflow++;
			if(ChangeListeners.logger.isEnabledFor(Level.ERROR))
				ChangeListeners.logger.log(Level.ERROR, "COPE Change Listener Dispatcher overflows");
		}
	}

	boolean requestedToStop()
	{
		return !threadRun;
	}

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
			catch(final Exception e)
			{
				handleException(e);
			}
			catch(final AssertionError e)
			{
				handleException(e);
			}
		}
		logTerminate();
	}

	private void handleException(final Throwable e)
	{
		exception++;
		if(ChangeListeners.logger.isEnabledFor(Level.ERROR))
			ChangeListeners.logger.error( "ChangeListenerDispatcher", e );
	}

	private void logTerminate()
	{
		if(ThreadSwarm.logger.isInfoEnabled())
		{
			final Thread t = Thread.currentThread();
			ThreadSwarm.logger.info(MessageFormat.format("{0} ({1}) terminates.", t.getName(), t.getId()));
		}
	}

	void addThreadControllers(final ArrayList<ThreadController> list)
	{
		threads.addThreadControllers(list);
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
