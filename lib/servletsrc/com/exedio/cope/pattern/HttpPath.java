/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import com.exedio.cope.Pattern;

class HttpPath extends Pattern
{
	private String urlPath = null;
	private String datadirURL = null;

	public void initialize()
	{
		final String name = getName();
		
		urlPath = getType().getID() + '/' + name + '/';
	}
	
	final String getUrlPath()
	{
		if(urlPath==null)
			throw new RuntimeException("http entity not yet initialized");
		
		return urlPath;
	}
	
	final String getDatadirURL()
	{
		if(datadirURL==null)
			datadirURL = getType().getModel().getProperties().getDatadirUrl();
		
		return datadirURL;
	}
	
	public final static class Log
	{
		private int counter = 0;
		private final Object lock = new Object();
		
		public final void increment()
		{
			synchronized(lock)
			{
				counter++;
			}
		}

		public final int get()
		{
			synchronized(lock)
			{
				return counter;
			}
		}

		final void reset()
		{
			synchronized(lock)
			{
				counter = 0;
			}
		}
	}

}
