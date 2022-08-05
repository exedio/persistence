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

import static java.lang.System.arraycopy;

final class DataConsumer
{
	private final int startLimit;
	private final byte[] start;
	private int startLength;

	DataConsumer(final int startLimit)
	{
		this.startLimit = startLimit;
		this.start = new byte[startLimit];
	}

	void acceptBytes(final byte[] input, final int length)
	{
		if(startLimit<=startLength)
			return;

		final int toCopy = Math.min(length, startLimit - startLength);
		arraycopy(input, 0, start, startLength, toCopy);
		startLength += toCopy;
	}

	byte[] start()
	{
		if(!lengthSet)
			throw new IllegalStateException();

		final byte[] result = new byte[startLength];
		arraycopy(start, 0, result, 0, startLength);
		return result;
	}



	private long length;
	private boolean lengthSet = false;

	// TODO rename to length
	void acceptLength(final long value)
	{
		if(lengthSet)
			throw new IllegalStateException();
		this.length = value;
		this.lengthSet = true;
	}

	long length()
	{
		if(!lengthSet)
			throw new IllegalStateException();
		return length;
	}
}
