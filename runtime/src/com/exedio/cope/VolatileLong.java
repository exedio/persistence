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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;

final class VolatileLong implements Serializable
{
	private static final long serialVersionUID = 1l;

	@SuppressWarnings("VolatileLongOrDoubleField")
	@SuppressFBWarnings("VO_VOLATILE_INCREMENT")
	private volatile long value = 0;

	@SuppressWarnings("NonAtomicOperationOnVolatileField")
	void inc()
	{
		value++;
	}

	@SuppressWarnings("NonAtomicOperationOnVolatileField")
	void inc(final long addend)
	{
		if(addend<0)
			throw new IllegalArgumentException();

		if(addend>0)
			value += addend;
	}

	long get()
	{
		return value;
	}
}