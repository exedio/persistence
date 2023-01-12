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

import io.micrometer.core.instrument.Counter;

final class InfoRegistry
{
	private static long count(final Counter counter) // TODO inline
	{
		return count(counter.count());
	}

	static long count(final double d)
	{
		final long l = Math.round(d);
		//noinspection FloatingPointEquality OK: tests backward conversion
		if(l!=d)
			throw new IllegalStateException("" + d);
		return l;
	}

	static int countInt(final Counter counter)
	{
		return Math.toIntExact(count(counter));
	}

	static int countInt(final double d) // TODO inline
	{
		return Math.toIntExact(count(d));
	}


	private InfoRegistry()
	{
		// prevent instantiation
	}
}
