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

import static com.exedio.cope.InfoRegistry.count;

import io.micrometer.core.instrument.Counter;

public final class ChangeListenerDispatcherInfo
{
	private final long overflow;
	private final long exception;
	private final int pending;

	ChangeListenerDispatcherInfo(
			final Counter overflow,
			final Counter exception,
			final int pending)
	{
		this.overflow = count(overflow);
		this.exception = count(exception);
		this.pending  = pending;
	}

	public long getOverflow()
	{
		return overflow;
	}

	public long getException()
	{
		return exception;
	}

	public int getPending()
	{
		return pending;
	}
}
