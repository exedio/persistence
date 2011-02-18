/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.util.Collection;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Properties.Source;

public class ClusterPropertiesTest extends CopeAssert
{
	public void testListenThreadsOk()
	{
		final Source s = new Source()
		{
			public String get(final String key)
			{
				if(key.equals("secret"))
					return String.valueOf("1234");
				else if(key.equals("log"))
					return "false";
				else if(key.equals("listenThreads"))
					return "5";
				else if(key.equals("listenThreadsMax"))
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

		final ClusterProperties p = ClusterProperties.get(s);
		assertEquals(5, p.getListenThreads());
		assertEquals(5, p.getListenThreadsMax());
	}

	public void testListenThreadsFail()
	{
		final Source s = new Source()
		{
			public String get(final String key)
			{
				if(key.equals("secret"))
					return String.valueOf("1234");
				else if(key.equals("log"))
					return "false";
				else if(key.equals("listenThreads"))
					return "5";
				else if(key.equals("listenThreadsMax"))
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

		try
		{
			ClusterProperties.get(s);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("listenThreads=5 must be less or equal listenThreadsMax=4", e.getMessage());
		}
	}
}
