/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import junit.framework.TestCase;

public class ClusterIterTest extends TestCase
{
	public void testInt()
	{
		final ClusterListener.Iter iter = new ClusterListener.Iter(new DatagramPacket(
				new byte[]{(byte)0xab, (byte)0x89, (byte)0x67, (byte)0x45}, 4));
		assertEquals(0x456789ab, iter.nextInt());
	}

	public void testIntNegative()
	{
		final ClusterListener.Iter iter = new ClusterListener.Iter(new DatagramPacket(
				new byte[]{(byte)0x45, (byte)0x67, (byte)0x89, (byte)0xab}, 4));
		assertEquals(0xab896745, iter.nextInt());
	}
}
