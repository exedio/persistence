/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

public final class ClusterNetworkWithoutClusterSchemaFailureTest extends CopeAssert
{
	@SuppressWarnings("static-method")
	public void testConnect()
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

	@Override
	protected void tearDown() throws Exception
	{
		if(model.isConnected())
			model.disconnect();
		super.tearDown();
	}

	private static final class AType extends Item
	{
		private AType(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
	}

	private static final Model model = new Model(TypesBound.newType(AType.class));

	static
	{
		model.enableSerialization(ClusterNetworkWithoutClusterSchemaFailureTest.class, "model");
	}
}
