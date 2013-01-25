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

import java.util.Collection;

import com.exedio.cope.util.Properties.Source;

public class ConnectSource
{
	static Source get()
	{
		return new Source()
		{
			public String get(final String key)
			{
				if(key.equals("connection.url"))
					return "jdbc:hsqldb:testUrl";
				else if(key.equals("connection.username"))
					return "testUser";
				else if(key.equals("connection.password"))
					return "testPassword";
				else if(key.equals("cluster"))
					return "true";
				else
					return null;
			}

			public String getDescription()
			{
				return "Minimal Connect Properties Source";
			}

			public Collection<String> keySet()
			{
				return null;
			}
		};
	}
}
