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

import java.net.DatagramPacket;
import java.util.NoSuchElementException;

final class ClusterIterator
{
	private final int length;
	private final int offset;
	private final int endOffset;
	private final byte[] buf;
	private int pos;

	ClusterIterator(final DatagramPacket packet)
	{
		this.offset = packet.getOffset();
		this.length = packet.getLength();
		this.endOffset = offset + length;
		this.buf = packet.getData();

		this.pos = offset;
	}

	boolean hasNext()
	{
		return pos<endOffset;
	}

	boolean checkBytes(final byte[] expected)
	{
		int pos = this.pos;
		for(int i = 0; i<expected.length; i++)
			if(expected[i]!=buf[pos++])
			{
				if(pos>endOffset)
					throw new NoSuchElementException(String.valueOf(length));
				return false;
			}

		if(pos>endOffset)
			throw new NoSuchElementException(String.valueOf(length));
		this.pos = pos;
		return true;
	}

	int next()
	{
		int pos = this.pos;
		final int result =
			((buf[pos++] & 0xff)    ) |
			((buf[pos++] & 0xff)<< 8) |
			((buf[pos++] & 0xff)<<16) |
			((buf[pos++] & 0xff)<<24) ;
		if(pos>endOffset)
			throw new NoSuchElementException(String.valueOf(length));
		this.pos = pos;
		return result;
	}

	void checkPingPayload(final ClusterProperties properties, final boolean ping)
	{
		properties.checkPingPayload(pos, buf, offset, length, ping);
	}
}
