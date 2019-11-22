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

import io.micrometer.core.instrument.Tags;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

final class CopeProbe
{
	final ConnectProperties properties;

	// probed on the initial connection
	final EnvironmentInfo environmentInfo;

	CopeProbe(
			final ConnectProperties properties,
			final EnvironmentInfo environmentInfo)
	{
		this.properties = properties;
		this.environmentInfo = environmentInfo;
	}

	Map<String, String> getRevisionEnvironment()
	{
		final HashMap<String, String> env = new HashMap<>();

		try
		{
			env.put("hostname", InetAddress.getLocalHost().getHostName());
		}
		catch(final UnknownHostException ignored)
		{
			// do not put in hostname
		}

		properties.putRevisionEnvironment(env);
		environmentInfo.putRevisionEnvironment(env);

		return env;
	}

	Tags tags()
	{
		return
				properties.tags().and(
				environmentInfo.tags());
	}
}
