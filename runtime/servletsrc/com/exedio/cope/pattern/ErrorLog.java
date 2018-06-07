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

package com.exedio.cope.pattern;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServletRequest;

final class ErrorLog
{
	private final int capacity;
	private final AtomicInteger value = new AtomicInteger();
	private final ArrayDeque<MediaRequestLog> logs;

	ErrorLog()
	{
		this(100);
	}

	ErrorLog(final int capacity)
	{
		this.capacity = capacity;
		this.logs = new ArrayDeque<>(capacity);
	}

	void count(final HttpServletRequest request, final Exception exception)
	{
		final MediaRequestLog log = new MediaRequestLog(
				System.currentTimeMillis(),
				exception,
				request);

		value.incrementAndGet();

		synchronized(logs)
		{
			if(logs.size()>=capacity)
				logs.removeFirst();
			logs.addLast(log);
		}
	}

	int get()
	{
		return value.get();
	}

	List<MediaRequestLog> getLogs()
	{
		final ArrayList<MediaRequestLog> result = new ArrayList<>(capacity);
		synchronized(logs)
		{
			result.addAll(logs);
		}
		return Collections.unmodifiableList(result);
	}
}
