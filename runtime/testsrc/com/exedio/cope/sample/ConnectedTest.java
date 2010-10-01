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

import junit.framework.TestCase;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.util.Properties;

public class ConnectedTest extends TestCase
{
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final Properties.Source s = new Properties.Source(){

			public String get(final String key)
			{
				if(key.equals("database.url"))
					return "jdbc:hsqldb:mem:copetest";
				else if(key.equals("database.user"))
					return "sa";
				else if(key.equals("database.password"))
					return "";
				else
					return null;
			}

			public String getDescription()
			{
				return "HistoryTest Properties.Source";
			}

			public Collection<String> keySet()
			{
				return null;
			}
		};
		final Properties.Source c = new Properties.Source(){

			public String get(final String key)
			{
				if(key.startsWith("sampler."))
				{
					if(key.equals("sampler.database.url"))
						return "jdbc:hsqldb:mem:sampler";
					else if(key.equals("sampler.database.user"))
						return "sa";
					else if(key.equals("sampler.database.password"))
						return "";
					else
						return null;
				}
				else
					throw new RuntimeException(key);
			}

			public String getDescription()
			{
				return "HistoryTest Properties.Source Context";
			}

			public Collection<String> keySet()
			{
				return null;
			}
		};
		MODEL.connect(new ConnectProperties(s, c));
		sampler.connect();
	}

	@Override
	protected void tearDown() throws Exception
	{
		MODEL.disconnect();
		sampler.getModel().dropSchema();
		sampler.disconnect();
		super.tearDown();
	}
}
