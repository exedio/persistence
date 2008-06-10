/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
	private final Model model;
	private final Object historyLock = new Object();
	private HistoryThread historyThread = null;
	private boolean historyAvailable = false;
	
	History(final Model model)
	{
		this.model = model;
		assert model!=null;
		startHistory();
	}
	
	boolean isHistoryAvailable()
	{
		synchronized(historyLock)
		{
			return historyAvailable;
		}
	}
	
	boolean isHistoryRunning()
	{
		synchronized(historyLock)
		{
			return historyThread!=null && historyThread.isAlive();
		}
	}
	
	void startHistory()
	{
		synchronized(historyLock)
		{
			if(historyThread!=null && historyThread.isAlive())
				throw new RuntimeException("already running");
			
			Properties.Source context = null;
			try
			{
				context = model.getProperties().getContext();
			}
			catch(IllegalStateException e)
			{
				// ok, then no history
			}
			if(context!=null)
			{
				final String propertyFile = context.get(ConsoleServlet.HISTORY_PROPERTY_FILE);
				if(propertyFile!=null)
				{
					historyAvailable = true;
					historyThread = new HistoryThread(model, propertyFile);
					historyThread.start();
				}
			}
		}
	}

	void stopHistory()
	{
		synchronized(historyLock)
		{
			if(historyThread!=null)
			{
				historyThread.stopAndJoin();
				historyThread = null;
			}
		}
	}
}
