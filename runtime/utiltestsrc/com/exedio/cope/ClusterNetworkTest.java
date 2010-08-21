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

import java.io.File;
import java.util.Collection;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Properties;

public abstract class ClusterNetworkTest extends CopeAssert
{
	ConnectProperties getProperties(final boolean multicast, final int sendPort, final int listenPort)
	{
		final ConnectProperties defaultProperties = new ConnectProperties(new File("runtime/utiltest.properties"));
		final Properties.Source source = defaultProperties.getSourceObject();
		return new ConnectProperties(
				new Properties.Source()
				{
					public String get(final String key)
					{
						if(key.equals("cluster"))
							return "true";
						else
							return source.get(key);
					}

					public String getDescription()
					{
						return source.getDescription();
					}

					public Collection<String> keySet()
					{
						return source.keySet();
					}
				},
				new Properties.Source()
				{
					public String get(final String key)
					{
						if(key.equals("cluster.secret"))
							return "1234";
						else if(key.equals("cluster.log"))
							return "false";
						else if(key.equals("cluster.listenThreads"))
							return "2";
						else if(!multicast && key.equals("cluster.multicast"))
							return "false";
						else if(!multicast && (key.equals("cluster.sendAddress")||key.equals("cluster.listenAddress")))
							return "127.0.0.1";
						else if(!multicast && key.equals("cluster.sendDestinationPort"))
							return String.valueOf(sendPort);
						else if(!multicast && key.equals("cluster.listenPort"))
							return String.valueOf(listenPort);
						else
							return null;
					}

					public String getDescription()
					{
						return
							multicast
							? "Connect Properties Context (multicast)"
							: ("Connect Properties Context (" + sendPort + '>' + listenPort + ")");
					}

					public Collection<String> keySet()
					{
						return null;
					}
				}
			);
	}

	@Override
	protected void tearDown() throws Exception
	{
		modelB.disconnect();
		modelA.disconnect();
		super.tearDown();
	}

	static class TypeA extends Item
	{
		private TypeA(final ActivationParameters ap)
		{
			super(ap);
		}

		private static final long serialVersionUID = 1l;

		static final Type<TypeA> TYPE = TypesBound.newType(TypeA.class);
	}

	static class TypeB extends Item
	{
		private TypeB(final ActivationParameters ap)
		{
			super(ap);
		}

		private static final long serialVersionUID = 1l;

		static final Type<TypeB> TYPE = TypesBound.newType(TypeB.class);
	}

	static final Model modelA = new Model(TypeA.TYPE);
	static final Model modelB = new Model(TypeB.TYPE);

	static
	{
		modelA.enableSerialization(ClusterNetworkTest.class, "modelA");
		modelB.enableSerialization(ClusterNetworkTest.class, "modelB");
	}
}
