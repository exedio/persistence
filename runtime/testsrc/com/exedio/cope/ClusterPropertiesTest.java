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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Properties.Source;
import java.io.File;
import java.util.Collection;
import org.junit.After;
import org.junit.Test;

public class ClusterPropertiesTest
{
	@Test public void testOk()
	{
		final Source s = new Source()
		{
			public String get(final String key)
			{
				if(key.equals("cluster.secret"))
					return String.valueOf("1234");
				else if(key.equals("cluster.log"))
					return "false";
				else if(key.equals("cluster.listenThreads"))
					return "5";
				else if(key.equals("cluster.listenThreadsMax"))
					return "5";
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
		};

		model.connect(new ConnectProperties(ConnectSource.get(), s));
		assertEquals(true, model.isClusterNetworkEnabled());
		final ClusterProperties p = (ClusterProperties)model.getClusterProperties();
		assertEquals(5, p.listenThreads);
		assertEquals(5, p.listenThreadsMax);
	}

	@Test public void testFailListenThreads()
	{
		final Source s = new Source()
		{
			public String get(final String key)
			{
				if(key.equals("cluster.secret"))
					return String.valueOf("1234");
				else if(key.equals("cluster.log"))
					return "false";
				else if(key.equals("cluster.listenThreads"))
					return "5";
				else if(key.equals("cluster.listenThreadsMax"))
					return "4";
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
		};

		final ConnectProperties properties = new ConnectProperties(ConnectSource.get(), s);
		try
		{
			model.connect(properties);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property listenThreads in Cluster Properties (prefix cluster.) " +
					"must be less or equal listenThreadsMax=4, " +
					"but was 5",
					e.getMessage());
		}
	}

	@Test public void testFailPrimaryKeyGeneratorMemory()
	{
		final Properties.Source defaultSource =
				new ConnectProperties(new File("runtime/utiltest.properties")).getSourceObject();
		final Properties.Source source = new Properties.Source()
		{
			public String get(final String key)
			{
				if(key.equals("cluster"))
					return "false";
				else
					return defaultSource.get(key);
			}

			public String getDescription()
			{
				return defaultSource.getDescription();
			}

			public Collection<String> keySet()
			{
				return defaultSource.keySet();
			}
		};
		final Properties.Source context = new Properties.Source()
		{
			public String get(final String key)
			{
				if(key.equals("cluster.secret"))
					return "1234";
				else
					return null;
			}

			public String getDescription()
			{
				return "Connect Properties Context";
			}

			public Collection<String> keySet()
			{
				return null;
			}
		};

		final ConnectProperties props = new ConnectProperties(source, context);
		// TODO throw exception below already above
		try
		{
			model.connect(props);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cluster network not supported together with schema.primaryKeyGenerator=memory", e.getMessage());
		}
	}

	@SuppressWarnings("static-method")
	@After public final void tearDown()
	{
		if(model.isConnected())
			model.disconnect();
	}

	private static final class AType extends Item
	{
		private AType(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
	}

	private static final Model model = new Model(TypesBound.newType(AType.class));

	static
	{
		model.enableSerialization(ClusterPropertiesTest.class, "model");
	}
}
