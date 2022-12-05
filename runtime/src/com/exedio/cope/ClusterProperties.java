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

import static com.exedio.cope.ClusterUtil.nextNode;
import static com.exedio.cope.ClusterUtil.pingString;
import static java.lang.Integer.MIN_VALUE;
import static java.net.StandardSocketOptions.IP_MULTICAST_LOOP;
import static java.util.Collections.list;

import com.exedio.cope.util.Properties;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("FieldCanBeLocal")
final class ClusterProperties extends Properties
{
	private static final Logger logger = LoggerFactory.getLogger(ClusterProperties.class);

	/**
	 * Multicast address for Local subnetwork (Not routable, 224.0.0.0 to 224.0.0.255).
	 * Not one of the "Notable addresses":
	 * <a href="https://en.wikipedia.org/wiki/Multicast_address">Multicast address</a>
	 */
	private static final String MULTICAST_ADDRESS = "224.0.0.50";
	private static final int PORT = 14446;

	        final int     secret              = value("secret", 0, MIN_VALUE);
	private final boolean nodeAuto            = value("nodeAuto" , true);
	private final int     nodeField           = value("node"     , 0, MIN_VALUE);
	private final boolean multicast           = value("multicast",           true);
	private final boolean sendSourcePortAuto  = value("sendSourcePortAuto" , true);
	private final int     sendSourcePort      = value("sendSourcePort"     , 14445, 1);
	private final InetAddress sendInterface   = valAd("sendInterface");
	private final List<Send> send             = valSd("sendAddress",         multicast?MULTICAST_ADDRESS:null);
	private final boolean sendBufferDefault   = value("sendBufferDefault"  , true);
	private final int     sendBuffer          = value("sendBuffer"         , 50000, 1);
	private final boolean sendTrafficDefault  = value("sendTrafficDefault" , true);
	private final int     sendTraffic         = value("sendTraffic"        , 0, 0);
	@SuppressWarnings("SimplifiableConditionalExpression")
	private final boolean sendLoopback        = multicast ? value("sendLoopback", true) : true;
	final InetSocketAddress listenAddress     = multicast ? new InetSocketAddress(valAd("listenAddress", MULTICAST_ADDRESS), 0)  : null;
	private final int     listenPort          = value("listenPort",          PORT, 1);
	final NetworkInterface listenInterface    = multicast ? valNI("listenInterface") : null;
	@SuppressWarnings("SimplifiableConditionalExpression")
	private final boolean listenLoopback      = multicast ? value("listenLoopback", true) : true;
	private final boolean listenBufferDefault = value("listenBufferDefault", true);
	private final int     listenBuffer        = value("listenBuffer"       , 50000, 1);
	final ThreadSwarmProperties listenThreads = valnp("listen.threads"     , ThreadSwarmProperties::new);
	        final int     listenSeqCheckCap   = value("listenSequenceCheckerCapacity", 200, 1);
	private final int     packetSizeField     = value("packetSize",          1400, 32);

	final int node;
	final int packetSize;
	private final byte[] pingPayload;

	private ClusterProperties(final Source source)
	{
		super(source);

		if(secret==0)
			throw newException("secret", "must not be zero");

		if(nodeAuto)
		{
			node = nextNode();
		}
		else
		{
			node = nodeField;
			if(node==0)
				throw newException("node", "must not be zero"); // must not be left at default value
		}
		if(logger.isInfoEnabled())
			logger.info("node id: {}", ClusterSenderInfo.toStringNodeID(node));

		if(multicast && send.size()>1)
			throw newException("sendAddress",
					"must must contain exactly one address for multicast, " +
					"but was " + send);

		packetSize = packetSizeField & (~3);
		{
			final Random r = new Random(secret);
			final byte[] pingPayload = new byte[packetSize];
			for(int pos = 32; pos<pingPayload.length; pos++)
				pingPayload[pos] = (byte)(r.nextInt()>>8);
			this.pingPayload = pingPayload;
		}
	}

	private NetworkInterface valNI(final String key)
	{
		final String DEFAULT = "DEFAULT";
		final String value = value(key, DEFAULT);
		if(DEFAULT.equals(value))
			return null;

		final NetworkInterface result;
		try
		{
			result = NetworkInterface.getByName(value);
			if(result==null)
				throw newException(key,
						"must be " + DEFAULT + " or one of the network interfaces: (" +
						list(NetworkInterface.getNetworkInterfaces()).stream().
								map(NetworkInterface::getName).
								collect(Collectors.joining(", ")) + "), " +
								"but was '" + value + '\'');
		}
		catch(final SocketException e)
		{
			throw newException(key, "failed '" + value + "' ", e);
		}

		return result;
	}

	private InetAddress valAd(final String key, final String defaultValue)
	{
		return getInetAddressByName(key, value(key, defaultValue));
	}

	private InetAddress valAd(final String key)
	{
		final String DEFAULT = "DEFAULT";
		final String value = value(key, DEFAULT);
		if(DEFAULT.equals(value))
			return null;

		return getInetAddressByName(key, value);
	}

	static final class Send
	{
		final InetAddress address;
		final int port;

		private Send(final InetAddress address, final int port)
		{
			this.address = address;
			this.port = port;
		}

		@Override
		public String toString()
		{
			return address.toString() + ':' + port;
		}
	}

	private List<Send> valSd(final String key, final String defaultValue)
	{
		final String value = value(key, defaultValue);
		if(!value.trim().equals(value))
			throw newException(key, "must be trimmed, but was '" + value + '\'');
		final ArrayList<Send> result = new ArrayList<>();
		for(final StringTokenizer tn = new StringTokenizer(value, " "); tn.hasMoreTokens(); )
			result.add(valSdSingle(key, tn.nextToken()));
		if(result.isEmpty())
			throw newException(key, "must not be empty");
		return Collections.unmodifiableList(result);
	}

	private Send valSdSingle(final String key, final String value)
	{
		final String address;
		final int port;
		final int pos = value.indexOf(':');
		if(pos<0)
		{
			address = value;
			port = PORT;
		}
		else
		{
			address = value.substring(0, pos);
			final String portString = value.substring(pos + 1);
			try
			{
				port = Integer.parseInt(portString);
			}
			catch(final NumberFormatException e)
			{
				throw newException(key,
						"must have an integer between 1 and " + 0xffff + " as port, " +
						"but was '" + portString + "' " +
						"at position " + pos + " in '" + value + '\'', e);
			}
		}

		if(port<1 || port>0xffff)
			throw newException(key,
					"must have an integer between 1 and " + 0xffff + " as port, " +
					"but was " + port + " " +
					"at position " + pos + " in '" + value + '\'');

		return new Send(getInetAddressByName(key, address), port);
	}

	Send[] send()
	{
		return send.toArray(SEND_EMPTY);
	}

	private static final Send[] SEND_EMPTY = new Send[0];

	private InetAddress getInetAddressByName(final String key, final String value)
	{
		if(value.isEmpty())
			throw newException(key, "must not be empty");

		try
		{
			return InetAddress.getByName(value);
		}
		catch(final UnknownHostException e)
		{
			throw newException(key, "must be a valid host name, but was '" + value + '\'', e);
		}
	}

	int copyPingPayload(int pos, final byte[] destination)
	{
		for(; pos<packetSize; pos++)
			destination[pos] = pingPayload[pos];
		return pos;
	}

	void checkPingPayload(int pos, final byte[] buf, final int offset, final int length, final boolean ping)
	{
		if(length!=packetSize)
			throw new RuntimeException("invalid " + pingString(ping) + ", expected length " + packetSize + ", but was " + length);
		final int endPos = offset + length;
		final byte[] pingPayload = this.pingPayload;
		for(; pos<endPos; pos++)
			if(pingPayload[pos-offset]!=buf[pos])
				throw new RuntimeException("invalid " + pingString(ping) + ", at position " + (pos-offset) + " expected " + pingPayload[pos-offset] + ", but was " + buf[pos]);
	}

	DatagramSocket newSendSocket()
	{
		final boolean single = sendLoopback || atLeastJdk17(); // single==false enables workaround for bug in JDK 11
		try
		{
			final DatagramSocket result =
				sendSourcePortAuto
				? single ? new DatagramSocket() : new MulticastSocket()
				: (sendInterface==null
					? single ? new DatagramSocket(sendSourcePort) : new MulticastSocket(sendSourcePort)
					: single ? new DatagramSocket(sendSourcePort, sendInterface) : new MulticastSocket(new InetSocketAddress(sendInterface, sendSourcePort)));
			// TODO close socket if code below fails
			if(!sendBufferDefault)
			{
				result.setSendBufferSize(sendBuffer);
				final int actual = result.getSendBufferSize();
				if(actual!=sendBuffer)
					logger.error("sendBufferSize expected {}, but was {}", sendBuffer, actual);
			}
			if(!sendTrafficDefault)
				result.setTrafficClass(sendTraffic);
			if(!sendLoopback)
			{
				// The semantics of IP_MULTICAST_LOOP changes between jdk versions:
				// * on jdk 11 the default value of IP_MULTICAST_LOOP is false, which means loopback is enabled. To disable
				//   it, you have to set it to true. That contradicts the documentation.
				// * on jdk 17 the default value of IP_MULTICAST_LOOP is true, which means loopback is enabled. To disable
				//   it, you have to set it to false. That conforms to the documentation.
				// To be portable between jdk versions we will just negate the default value.
				//
				// Apart from that, get/setOption with IP_MULTICAST_LOOP requires the socket to be a MulticastSocket
				// on JDK 11, but not on JDK 17.
				final boolean value = !result.getOption(IP_MULTICAST_LOOP);
				result.setOption(IP_MULTICAST_LOOP, value);
				if(result.getOption(IP_MULTICAST_LOOP)!=value)
					logger.error("disabling send IP_MULTICAST_LOOP ({}) was ignored by MulticastSocket", value);
			}
			return result;
		}
		catch(final IOException e)
		{
			throw new RuntimeException(String.valueOf(sendSourcePort), e);
		}
	}

	private static boolean atLeastJdk17()
	{
		try
		{
			// https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/random/RandomGenerator.html
			Class.forName("java.util.random.RandomGenerator", // available since JDK 17
					false,
					ClusterProperties.class.getClassLoader());
			logger.info("JDK 17 or later detected");
			return true;
		}
		catch(final ClassNotFoundException e)
		{
			logger.info("JDK earlier than 17 or later detected");
			return false;
		}
	}

	DatagramSocket newListenSocket()
	{
		final int port = listenPort;
		try
		{
			final DatagramSocket result;
			if(multicast)
			{
				@SuppressWarnings({"resource", "IOResourceOpenedButNotSafelyClosed", "SocketOpenedButNotSafelyClosed"}) // OK: is closed outside this factory method
				final MulticastSocket resultMulti = new MulticastSocket(port);
				// TODO close socket if code below fails
				if(!listenLoopback)
				{
					// BEWARE:
					// The setOption(IP_MULTICAST_LOOP) below does not work. Although getOption reflects the change made,
					// the sender socket still receives datagrams sent by itself. I have no idea why.
					// https://tldp.org/HOWTO/Multicast-HOWTO-6.html
					// Moving setOption behind joinGroup did not help.
					// Calling setOption(IP_MULTICAST_LOOP, false) did not help.
					// Setting this option at the sender sockets (see sendLoopback) works.
					resultMulti.setOption(IP_MULTICAST_LOOP, true); // BEWARE of the negation introduced by IP_MULTICAST_LOOP
					if(!resultMulti.getOption(IP_MULTICAST_LOOP))
						logger.error("disabling listen IP_MULTICAST_LOOP was ignored by MulticastSocket");
				}
				resultMulti.joinGroup(listenAddress, listenInterface);
				result = resultMulti;
			}
			else
			{
				result = new DatagramSocket(port);
			}
			// TODO close socket if code below fails
			if(!listenBufferDefault)
			{
				result.setReceiveBufferSize(listenBuffer);
				final int actual = result.getReceiveBufferSize();
				if(actual!=listenBuffer)
					logger.error("receiveBufferSize expected {}, but was {}", listenBuffer, actual);
			}
			return result;
		}
		catch(final IOException e)
		{
			throw new RuntimeException(String.valueOf(port), e);
		}
	}

	static Factory<ClusterProperties> factory()
	{
		return ClusterProperties::new;
	}
}
