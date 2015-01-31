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

import com.exedio.cope.util.Properties.Source;
import java.util.Collection;

public class ConnectSource
{
	static Source get()
	{
		return new Source()
		{
			public String get(final String key)
			{
				if(key.equals("connection.url"))
					return "jdbc:hsqldb:mem:testUrl";
				else if(key.equals("connection.username"))
					return "testUser";
				else if(key.equals("connection.password"))
					return "testPassword";
				else if(key.equals("schema.primaryKeyGenerator"))
					return PrimaryKeyGenerator.sequence.name();
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
