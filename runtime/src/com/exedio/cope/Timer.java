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

import static com.exedio.cope.misc.TimeUtil.toMillies;

import org.slf4j.Logger;

final class Timer
{
	private final Logger logger;
	private final String msg;
	private final Interval emptyInterval = new Interval();
	private volatile long totalElapsed = 0;

	Timer(final Logger logger, final String msg)
	{
		if(logger==null)
			throw new NullPointerException("logger");

		this.logger = logger;
		this.msg = (msg!=null) ? (" " + msg) : "";
	}

	Interval start()
	{
		return
				logger.isInfoEnabled()
				? new Interval(System.nanoTime())
				: emptyInterval;
	}

	void finish(final String msg, final long elapsed)
	{
		final long currentAccu = totalElapsed += elapsed;
		logger.info(msg  + " " + elapsed + "ms total" + this.msg + " " + currentAccu + "ms");
	}

	final class Interval
	{
		private final boolean dummy;
		private final long start;
		private boolean exhausted = false;

		Interval()
		{
			this.dummy = true;
			this.start = 0;
		}

		Interval(final long start)
		{
			this.dummy = false;
			this.start = start;
		}

		void finish(final String msg)
		{
			if(dummy)
				return;

			final long stop = System.nanoTime();

			if(exhausted)
				throw new IllegalStateException("exhausted");
			exhausted = true;

			Timer.this.finish(msg, toMillies(stop, start));
		}
	}
}
