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

package com.exedio.filevault;

import com.exedio.cope.util.Properties;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.UnknownHostException;
import java.net.http.HttpClient;

/**
 * Instead of this class one can use
 * <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/doc-files/net-properties.html#Proxies">System Properties</a>
 * as well.
 */
// TODO move to copeutil and make public
final class HttpClientProxyProperties extends Properties
{
	private enum Mode
	{
		DEFAULT,
		DEFAULT_SPECIFIED,
		NONE,
		SPECIFIED
	}

	private final Mode mode = value("mode", Mode.DEFAULT);
	private final boolean specified = mode==Mode.SPECIFIED;

	private final InetAddress address = specified ? valAd("address") : null;
	private final int port = specified ? value("port", 3128, 1) : Integer.MIN_VALUE; // 3128 is squid default
	private final ProxySelector proxy =
			switch (mode)
			{
				case DEFAULT -> null;
				case DEFAULT_SPECIFIED -> ProxySelector.getDefault();
				case NONE -> HttpClient.Builder.NO_PROXY;
				case SPECIFIED -> ProxySelector.of(new InetSocketAddress(address, port));
			};

	private InetAddress valAd(final String key)
	{
		final String value = value(key, (String)null);
		try
		{
			return InetAddress.getByName(value);
		}
		catch(final UnknownHostException e)
		{
			throw newException(key, "must be a valid host name, but was '" + value + '\'', e);
		}
	}

	@SuppressWarnings("UnusedReturnValue") // OK: for future use
	public HttpClient.Builder set(final HttpClient.Builder builder)
	{
		return proxy!=null ? builder.proxy(proxy) : builder;
	}

	public static Factory<HttpClientProxyProperties> factory()
	{
		return HttpClientProxyProperties::new;
	}

	private HttpClientProxyProperties(final Source source)
	{
		super(source);
	}
}
