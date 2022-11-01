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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * This is a characterization test for the loopback functionality of the MulticastSocket.
 */
public class LoopbackTest
{
	private MulticastSocket socket;

	@AfterEach void afterEach()
	{
		if(socket!=null)
			socket.close();
	}

	@Test void testDefault() throws IOException
	{
		socket = new MulticastSocket(14446);
		socket.joinGroup(new InetSocketAddress(InetAddress.getByName("224.0.0.50"), 0), null);
		assertEquals(false, socket.getLoopbackMode());
	}

	@Test void testOldDefaultSet() throws IOException
	{
		socket = new MulticastSocket(14446);
		socket.setLoopbackMode(false);
		socket.joinGroup(new InetSocketAddress(InetAddress.getByName("224.0.0.50"), 0), null);
		assertEquals(false, socket.getLoopbackMode());
	}

	@Test void testOldSet() throws IOException
	{
		socket = new MulticastSocket(14446);
		socket.setLoopbackMode(true);
		socket.joinGroup(new InetSocketAddress(InetAddress.getByName("224.0.0.50"), 0), null);
		assertEquals(true, socket.getLoopbackMode());
	}
}
