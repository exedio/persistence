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

package com.exedio.cope.tojunit;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.opentest4j.AssertionFailedError;

public class AssertionFailedClock extends Clock
{
	@Override
	public ZoneId getZone()
	{
		throw new AssertionFailedError();
	}

	@Override
	public Clock withZone(final ZoneId zone)
	{
		throw new AssertionFailedError(zone.getId());
	}

	@Override
	public long millis()
	{
		throw new AssertionFailedError();
	}

	@Override
	public Instant instant()
	{
		throw new AssertionFailedError();
	}

	@Override
	public boolean equals(final Object obj)
	{
		throw new AssertionFailedError();
	}

	@Override
	public int hashCode()
	{
		throw new AssertionFailedError();
	}
}
