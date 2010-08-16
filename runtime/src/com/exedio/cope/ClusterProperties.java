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

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.MIN_PRIORITY;

import java.net.DatagramSocket;
import java.net.SocketException;

import com.exedio.cope.util.Properties;

final class ClusterProperties extends Properties
{
	        final BooleanField log                 = new BooleanField("cluster.log", true);
	private final BooleanField sendSourcePortAuto  = new BooleanField("cluster.sendSourcePortAuto" , true);
	private final IntField     sendSourcePort      = new     IntField("cluster.sendSourcePort"     , 14445, 1);
	        final IntField     sendDestinationPort = new     IntField("cluster.sendDestinationPort", 14446, 1);
	        final IntField     listenPort          = new     IntField("cluster.listenPort",          14446, 1);
	private final BooleanField listenPrioritySet   = new BooleanField("cluster.listenPrioritySet",   false);
	private final IntField     listenPriority      = new     IntField("cluster.listenPriority",      MAX_PRIORITY, MIN_PRIORITY);
	        final StringField  group               = new  StringField("cluster.group",               "230.0.0.1");
	        final IntField     packetSize          = new     IntField("cluster.packetSize",          1400, 32);


	ClusterProperties(final Source source)
	{
		super(source, null);
	}

	DatagramSocket getSendSocket()
	{
		try
		{
			return
				sendSourcePortAuto.booleanValue()
				? new DatagramSocket()
				: new DatagramSocket(sendSourcePort.intValue());
		}
		catch(final SocketException e)
		{
			throw new RuntimeException(
					String.valueOf(sendSourcePort.intValue()) + '/' +
					String.valueOf(sendSourcePort.intValue()), e);
		}
	}

	void setListenPriority(final Thread thread)
	{
		if(listenPrioritySet.booleanValue())
			thread.setPriority(listenPriority.intValue());
	}
}
