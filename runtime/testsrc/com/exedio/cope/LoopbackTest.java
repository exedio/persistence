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

import static java.net.StandardSocketOptions.IP_MULTICAST_LOOP;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * This is a characterization test for the loopback functionality of the MulticastSocket.
 * It shows, that documentation of {@link java.net.StandardSocketOptions#IP_MULTICAST_LOOP} is wrong:
 * <blockquote>
 *    The initial/default value of this socket option is TRUE.
 * </blockquote>
 * This option is {@code FALSE} by default, thus it is affected by the same negation semantics as
 * {@link MulticastSocket#setLoopbackMode(boolean)} and {@link MulticastSocket#getLoopbackMode()}.
 */
@SuppressWarnings("deprecation") // for set/getLoopbackMode
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
		socket.joinGroup(new InetSocketAddress(InetAddress.getByName("224.0.0.50"), 0), networkInterface());
		assertEquals(false, socket.getLoopbackMode());
		assertEquals(true, socket.getOption(IP_MULTICAST_LOOP));
	}

	@Test void testOldDefaultSet() throws IOException
	{
		socket = new MulticastSocket(14446);
		socket.setLoopbackMode(false);
		socket.joinGroup(new InetSocketAddress(InetAddress.getByName("224.0.0.50"), 0), networkInterface());
		assertEquals(false, socket.getLoopbackMode());
		assertEquals(true, socket.getOption(IP_MULTICAST_LOOP));
	}

	@Test void testOldSet() throws IOException
	{
		socket = new MulticastSocket(14446);
		socket.setLoopbackMode(true);
		socket.joinGroup(new InetSocketAddress(InetAddress.getByName("224.0.0.50"), 0), networkInterface());
		assertEquals(true, socket.getLoopbackMode());
		assertEquals(false, socket.getOption(IP_MULTICAST_LOOP));
	}

	@Test void testNewDefaultSet() throws IOException
	{
		socket = new MulticastSocket(14446);
		socket.setOption(IP_MULTICAST_LOOP, false);
		socket.joinGroup(new InetSocketAddress(InetAddress.getByName("224.0.0.50"), 0), networkInterface());
		assertEquals(true, socket.getLoopbackMode());
		assertEquals(false, socket.getOption(IP_MULTICAST_LOOP));
	}

	@Test void testNewSet() throws IOException
	{
		socket = new MulticastSocket(14446);
		socket.setOption(IP_MULTICAST_LOOP, true);
		socket.joinGroup(new InetSocketAddress(InetAddress.getByName("224.0.0.50"), 0), networkInterface());
		assertEquals(false, socket.getLoopbackMode());
		assertEquals(true, socket.getOption(IP_MULTICAST_LOOP));
	}

	private static NetworkInterface networkInterface() throws SocketException
	{
		final String name = ClusterNetworkTest.listenInterfaceIfSet();
		return name!=null ? NetworkInterface.getByName(name) : null;
	}
}
