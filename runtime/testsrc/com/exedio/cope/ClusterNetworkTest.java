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

import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.util.Sources.cascade;
import static com.exedio.cope.util.Sources.load;
import static com.exedio.cope.util.Sources.view;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import java.io.File;
import java.util.Properties;
import org.junit.After;

public abstract class ClusterNetworkTest
{
	static final ConnectProperties getPropertiesMulticast()
	{
		final Properties p = new Properties();
		p.setProperty("cluster.sendAddress"  , MULTICAST_ADDRESS);
		p.setProperty("cluster.listenAddress", MULTICAST_ADDRESS);
		return getProperties(p, "Connect Properties Source (multicast)");
	}

	static final ConnectProperties getPropertiesSinglecast(final boolean forward)
	{
		final Properties p = new Properties();
		p.setProperty("cluster.multicast", "false");
		p.setProperty("cluster.sendAddress"  , "127.0.0.1");
		p.setProperty("cluster.listenAddress", "127.0.0.1");
		p.setProperty("cluster.sendDestinationPort", forward ? PORT_SEND : PORT_LISTEN);
		p.setProperty("cluster.listenPort",          forward ? PORT_LISTEN : PORT_SEND);
		return getProperties(p, "Connect Properties Source (singlecast " + (forward ? "forward" : "backward") + ")");
	}

	private static final String MULTICAST_ADDRESS;
	private static final String PORT_SEND;
	private static final String PORT_LISTEN;
	static
	{
		MULTICAST_ADDRESS = System.getProperty(ClusterNetworkTest.class.getName() + ".multicast", "230.0.0.1");
		final String prefix = ClusterNetworkTest.class.getName() + ".port";
		PORT_SEND   = System.getProperty(prefix + ".send",   "14446");
		PORT_LISTEN = System.getProperty(prefix + ".listen", "14447");
		System.out.println(ClusterNetworkTest.class.getName() + ' ' + MULTICAST_ADDRESS + ' ' + PORT_SEND + '>' + PORT_LISTEN);
	}

	private static ConnectProperties getProperties(final Properties properties, final String description)
	{
		properties.setProperty("schema.primaryKeyGenerator", PrimaryKeyGenerator.sequence.name());
		properties.setProperty("cluster", "true");
		properties.setProperty("cluster.secret", "1234");
		properties.setProperty("cluster.listenThreads", "2");
		return ConnectProperties.create(describe(description,
				cascade(
						view(properties, "ZACK"),
						load(new File("runtime/utiltest.properties"))
				)
			));
	}

	@SuppressWarnings("static-method")
	@After public final void tearDownClusterNetworkTest()
	{
		modelB.disconnect();
		modelA.disconnect();
		modelA.removeAllChangeListeners();
		modelB.removeAllChangeListeners();
	}

	@WrapperType(indent=2, comments=false)
	static class TypeA extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		TypeA()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected TypeA(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<TypeA> TYPE = com.exedio.cope.TypesBound.newType(TypeA.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected TypeA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperIgnore
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
