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

import java.io.File;
import java.util.Properties;
import org.junit.After;

public abstract class ClusterNetworkTest
{
	static final ConnectProperties getProperties(final boolean multicast, final int sendPort, final int listenPort)
	{
		final Properties p = new Properties();
		p.setProperty("schema.primaryKeyGenerator", PrimaryKeyGenerator.sequence.name());
		p.setProperty("cluster", "true");
		p.setProperty("cluster.secret", "1234");
		p.setProperty("cluster.listenThreads", "2");
		if(!multicast)
		{
			p.setProperty("cluster.multicast", "false");
			p.setProperty("cluster.sendAddress"  , "127.0.0.1");
			p.setProperty("cluster.listenAddress", "127.0.0.1");
			p.setProperty("cluster.sendDestinationPort", String.valueOf(sendPort));
			p.setProperty("cluster.listenPort", String.valueOf(listenPort));
		}
		return getProperties(p,
				multicast
				? "Connect Properties Source (multicast)"
				: ("Connect Properties Source (" + sendPort + '>' + listenPort + ")"));
	}

	private static ConnectProperties getProperties(final Properties properties, final String description)
	{
		return ConnectProperties.factory().create(describe(
				cascade(
						view(properties, "ZACK"),
						load(new File("runtime/utiltest.properties"))
				),
				description
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

	static class TypeA extends Item
	{
		TypeA()
		{
			super(new SetValue<?>[]{});
		}

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
