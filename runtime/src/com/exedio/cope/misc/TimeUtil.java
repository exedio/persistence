/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.misc;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class TimeUtil
{
	static final Logger logger = Logger.getLogger(TimeUtil.class.getName());

	/**
	 *	Never returns a negative value.
	 */
	public static long toMillies(final long endNanos, final long startNanos)
	{
		if(endNanos<startNanos && logger.isLoggable(Level.SEVERE))
		{
			final LogRecord record = new LogRecord(Level.SEVERE, "backwards nanos {0} {1}");
			record.setParameters(new Object[]{Long.valueOf(startNanos), Long.valueOf(endNanos)});
			record.setSourceClassName(TimeUtil.class.getName());
			record.setSourceMethodName("toMillies");
			logger.log(record);
		}

		final long diff = endNanos - startNanos;
		if(diff<500000)
			return 0l;

		return (diff+500000) / 1000000l;
	}

	private TimeUtil()
	{
		// prevent instantiation
	}
}
