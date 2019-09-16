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

package com.exedio.cope.misc;

import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MicrometerUtil
{
	private static final Logger logger = LoggerFactory.getLogger(MicrometerUtil.class);

	/**
	 * Never returns a negative value.
	 */
	public static long toMillies(final Timer timer, final Timer.Sample start)
	{
		final long diff = start.stop(timer);

		if(logger.isErrorEnabled() && diff<0)
			logger.error("backwards nanos {}: {}", timer.getId(), diff);

		if(diff<500000)
			return 0l;

		return (diff+500000) / 1000000l;
	}

	private MicrometerUtil()
	{
		// prevent instantiation
	}
}
