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

package com.exedio.cope.sample;

import static com.exedio.cope.sample.Stuff.MODEL;
import static com.exedio.cope.sample.Stuff.sampler;

import java.util.Collection;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.util.Properties;

public class ConnectedTest extends AbstractRuntimeTest
{
	ConnectedTest()
	{
		super(MODEL);
	}

	@Override
	public ConnectProperties getConnectProperties()
	{
		final ConnectProperties superResult = super.getConnectProperties();
		final Properties.Source superSource  = superResult.getSourceObject();
		final Properties.Source superContext = superResult.getContext();

		final Properties.Source c = new Properties.Source(){

			public String get(final String key)
			{
				if(key.startsWith("sampler."))
				{
					if(key.equals("sampler.database.url"))
						return superSource.get("database.url");
					else if(key.equals("sampler.database.user"))
						return superSource.get("database.user");
					else if(key.equals("sampler.database.password"))
						return superSource.get("database.password");
					else
						return null;
				}
				else
					return superContext.get(key);
			}

			public String getDescription()
			{
				return superContext.getDescription() + " (sampler)";
			}

			public Collection<String> keySet()
			{
				return superContext.keySet();
			}
		};
		return new ConnectProperties(superSource, c);
	}

	boolean c;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		c = model.getConnectProperties().getItemCacheLimit()>0;
		sampler.connect();
	}

	@Override
	protected void tearDown() throws Exception
	{
		sampler.getModel().dropSchema();
		sampler.disconnect();
		super.tearDown();
	}
}
