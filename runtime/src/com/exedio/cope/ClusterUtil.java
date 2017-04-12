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

import java.util.Random;

final class ClusterUtil
{
	static final byte MAGIC0 = (byte)0xc0;
	static final byte MAGIC1 = (byte)0xbe;
	static final byte MAGIC2 = 0x11;
	/**
	 * History
	 * <ul>
	 * <li>0x11: initial</li>
	 * <li>0x12: change {@link Item#pk} from int to long</li>
	 * </ul>
	 */
	static final byte MAGIC3 = 0x12;

	static final int KIND_PING       = 0x00110001;
	static final int KIND_PONG       = 0x00110002;
	static final int KIND_INVALIDATE = 0x00120001;

	static int marshal(int pos, final byte[] buf, final int i)
	{
		buf[pos++] = (byte)( i       & 0xff);
		buf[pos++] = (byte)((i>>> 8) & 0xff);
		buf[pos++] = (byte)((i>>>16) & 0xff);
		buf[pos++] = (byte)((i>>>24) & 0xff);
		return pos;
	}

	static int marshal(int pos, final byte[] buf, final long i)
	{
		buf[pos++] = (byte)( i       & 0xff);
		buf[pos++] = (byte)((i>>> 8) & 0xff);
		buf[pos++] = (byte)((i>>>16) & 0xff);
		buf[pos++] = (byte)((i>>>24) & 0xff);
		buf[pos++] = (byte)((i>>>32) & 0xff);
		buf[pos++] = (byte)((i>>>40) & 0xff);
		buf[pos++] = (byte)((i>>>48) & 0xff);
		buf[pos++] = (byte)((i>>>56) & 0xff);
		return pos;
	}


	static int nextNode()
	{
		return nodeSource.nextInt();
	}

	private static final Random nodeSource = new Random();


	static String pingString(final boolean ping)
	{
		return ping ? "ping" : "pong";
	}
}
