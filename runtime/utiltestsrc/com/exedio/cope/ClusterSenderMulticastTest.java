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

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Properties;
import java.io.IOException;
import java.net.SocketException;
import java.util.Collection;

public final class ClusterSenderMulticastTest extends CopeAssert
{
	private ClusterSenderMulticast sender;

	private static final int SECRET = 0x88776655;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final ClusterProperties properties = ClusterProperties.get(
			new ConnectProperties(
				ConnectSource.get(),
				new Properties.Source()
				{
					public String get(final String key)
					{
						if(key.equals("cluster.packetSize"))
							return "47";
						else if(key.equals("cluster.secret"))
							return String.valueOf(SECRET);
						else if(key.equals("cluster.log"))
							return "false";
						else
							return null;
					}

					public String getDescription()
					{
						return "Cluster Properties";
					}

					public Collection<String> keySet()
					{
						return null;
					}
				}
			));
		sender = new ClusterSenderMulticast(properties);
	}

	@Override
	protected void tearDown() throws Exception
	{
		sender.close();
		super.tearDown();
	}

	public void testIt() throws IOException
	{
		final byte[] b = new byte[]{10, 10, 10, 10, 10, 10, 10};
		sender.send(b.length, b);
		sender.send(b.length, b);

		sender.close();
		try
		{
			sender.send(b.length, b);
			fail();
		}
		catch(final SocketException e)
		{
			// do not assert message of foreign library
		}
	}
}
