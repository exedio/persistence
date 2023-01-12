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
	private final double overflow;
	private final double exception;
	private final int pending;

	ChangeListenerDispatcherInfo(
			final Counter overflow,
			final Counter exception,
			final int pending)
	{
		this.overflow = overflow.count();
		this.exception = exception.count();
		this.pending  = pending;
	}

	public long getOverflow()
	{
		return count(overflow);
	}

	public long getException()
	{
		return count(exception);
	}

	public int getPending()
	{
		return pending;
	}
}
