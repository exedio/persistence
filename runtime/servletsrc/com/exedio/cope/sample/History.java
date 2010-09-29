/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.console;

import com.exedio.cope.Model;
import com.exedio.cope.util.Properties;

final class History
{
	private static final String AUTO_PURGE_DAYS = "com.exedio.cope.console.history.autoPurgeDays";

	private final Model model;
	private final Object lock = new Object();
	private HistoryThread thread = null;
	private boolean available = false;

	History(final Model model)
	{
		this.model = model;
		assert model!=null;
		start();
	}

	boolean isAvailable()
	{
		synchronized(lock)
		{
			return available;
		}
	}

	boolean isRunning()
	{
		synchronized(lock)
		{
			return thread!=null && thread.isAlive();
		}
	}

	void start()
	{
		synchronized(lock)
		{
			if(thread!=null && thread.isAlive())
				throw new RuntimeException("already running");

			Properties.Source context = null;
			try
			{
				context = model.getConnectProperties().getContext();
			}
			catch(final IllegalStateException e)
			{
				// ok, then no history
			}
			if(context!=null)
			{
				final String propertyFile = context.get(ConsoleServlet.HISTORY_PROPERTY_FILE);
				if(propertyFile!=null)
				{
					available = true;

					int autoPurgeDays = 0;
					final String autoPurgeDaysString = context.get(AUTO_PURGE_DAYS);
					if(autoPurgeDaysString!=null)
						autoPurgeDays = Integer.parseInt(autoPurgeDaysString);

					thread = new HistoryThread(model, propertyFile, autoPurgeDays);
					thread.start();
				}
			}
		}
	}

	void stop()
	{
		synchronized(lock)
		{
			if(thread!=null)
			{
				thread.stopAndJoin();
				thread = null;
			}
		}
	}

	String getThreadID()
	{
		return
			thread!=null
			? thread.toString()
			: null;
	}

	int getAutoPurgeDays()
	{
		final HistoryThread thread = this.thread;
		return thread!=null ? thread.autoPurgeDays : 0;
	}
}
