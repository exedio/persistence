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

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.MIN_PRIORITY;
import static java.util.Objects.requireNonNull;

import java.lang.Thread.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ThreadController
{
	@SuppressWarnings("LoggerInitializedWithForeignClass")
	private static final Logger logger = LoggerFactory.getLogger(ThreadSwarm.class);

	private static final int NO_PRIORITY = Integer.MIN_VALUE;

	private final Runnable target;
	private final String name;
	private final boolean daemon;
	private int priority = NO_PRIORITY;

	private Thread thread = null;

	ThreadController(
			final Runnable target,
			final String name,
			final boolean daemon)
	{
		this.target = requireNonNull(target, "target");
		this.name = requireNonNull(name, "name");
		this.daemon = daemon;
	}

	public String getName()
	{
		return name;
	}

	public boolean isDaemon()
	{
		return daemon;
	}

	public int getPriority()
	{
		final Thread thread = this.thread;
		return thread!=null ? thread.getPriority() : priority;
	}

	public void setPriority(final int priority)
	{
		if(priority>MAX_PRIORITY || priority<MIN_PRIORITY)
		    throw new IllegalArgumentException(String.valueOf(priority));

		this.priority = priority;
	}

	private Thread makeThread()
	{
		final Thread thread = new Thread(target);
		thread.setName(name);
		thread.setDaemon(daemon);
		if(priority!=NO_PRIORITY)
			//noinspection CallToThreadSetPriority
			thread.setPriority(priority);
		// thread.setUncaughtExceptionHandler(xxx); TODO save exception and make available by getter
		return thread;
	}

	void start()
	{
		if(thread!=null)
			throw new IllegalStateException();

		this.thread = makeThread();
		thread.start();
	}

	public void restart()
	{
		if(thread!=null && thread.isAlive())
			throw new IllegalStateException();

		this.thread = makeThread();
		thread.start();
	}

	public long getId()
	{
		final Thread thread = this.thread;
		return thread!=null ? thread.getId() : -1;
	}

	public boolean isAlive()
	{
		final Thread thread = this.thread;
		return thread!=null && thread.isAlive();
	}

	public State getState()
	{
		final Thread thread = this.thread;
		return thread!=null ? thread.getState() : null;
	}

	public StackTraceElement[] getStackTrace()
	{
		final Thread thread = this.thread;
		return thread!=null ? thread.getStackTrace() : null;
	}

	void interrupt()
	{
		final Thread thread = this.thread;
		if(thread!=null)
			thread.interrupt();
	}

	void join() throws InterruptedException
	{
		final Thread thread = this.thread;
		if(thread!=null)
		{
			thread.join();
			if(logger.isInfoEnabled())
				logger.info("{} ({}) done.", thread.getName(), thread.getId());
		}
	}

	@Override
	public String toString()
	{
		return name;
	}
}
