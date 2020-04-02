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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.util.Sources.cascade;
import static com.exedio.cope.util.Sources.view;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;

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
		MULTICAST_ADDRESS = System.getProperty(ClusterNetworkTest.class.getName() + ".multicast", "224.0.0.41");
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
						TestSources.minimal()
				)
			));
	}

	@AfterEach final void tearDownClusterNetworkTest()
	{
		modelB.disconnect();
		modelA.disconnect();
		modelA.removeAllChangeListeners();
		modelB.removeAllChangeListeners();
	}

	@WrapperType(indent=2, comments=false)
	static class TypeA extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		TypeA()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@com.exedio.cope.instrument.Generated
		protected TypeA(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<TypeA> TYPE = com.exedio.cope.TypesBound.newType(TypeA.class);

		@com.exedio.cope.instrument.Generated
		protected TypeA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class TypeB extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<TypeB> TYPE = com.exedio.cope.TypesBound.newType(TypeB.class);

		@com.exedio.cope.instrument.Generated
		private TypeB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model modelA = new Model(TypeA.TYPE);
	static final Model modelB = new Model(TypeB.TYPE);

	static
	{
		modelA.enableSerialization(ClusterNetworkTest.class, "modelA");
		modelB.enableSerialization(ClusterNetworkTest.class, "modelB");
	}
}
