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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ThreadSwarm
{
	private static final Logger logger = LoggerFactory.getLogger(ThreadSwarm.class);

	private final ThreadController[] threads;

	ThreadSwarm(
			final Runnable target,
			final String name,
			final int size)
	{
		assert target!=null;
		assert name!=null;
		assert size>0;

		this.threads = new ThreadController[size];
		for(int i = 0; i<threads.length; i++)
		{
			final ThreadController thread = new ThreadController(target,
				name + ' ' + (i+1) + '/' + size,
				true);
			threads[i] = thread;
		}
	}

	void start(final int number)
	{
		assert number<=threads.length;

		int toStart = number;
		for(final ThreadController thread : threads)
		{
			if((--toStart)<0)
				continue;

			thread.start();
			if(logger.isInfoEnabled())
				logger.info(MessageFormat.format("{0} ({1}) started.", thread.getName(), thread.getId()));
		}
	}

	void addThreadControllers(final ArrayList<ThreadController> list)
	{
		list.addAll(Arrays.asList(threads));
	}

	void setPriority(final int priority)
	{
		for(final ThreadController thread : threads)
			thread.setPriority(priority);
	}

	void interrupt()
	{
		for(final ThreadController thread : threads)
			thread.interrupt();
	}

	void join()
	{
		for(final ThreadController thread : threads)
		{
			try
			{
				thread.join();
			}
			catch(final InterruptedException e)
			{
				throw new RuntimeException(thread.getName() + '(' + thread.getId() + ')', e);
			}
		}
	}
}
