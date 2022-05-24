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

package com.exedio.cope.vault;

import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;
import static java.lang.Math.toIntExact;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.time.Duration.ofSeconds;

import com.exedio.cope.util.Properties;
import com.exedio.cope.util.ServiceProperties;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@ServiceProperties(VaultHttpService.Props.class)
public final class VaultHttpService extends VaultNonWritableService
{
	private final String rootUrl;
	private final VaultDirectory directory;
	private final Props properties;

	VaultHttpService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		super(parameters);
		this.rootUrl = properties.root;
		this.directory = VaultDirectory.instance(properties.directory, parameters);
		this.properties = properties;
	}


	@Override
	public long getLength(final String hash) throws VaultNotFoundException
	{
		try
		{
			return connectForGet(hash, REQUEST_METHOD_HEAD).getContentLength();
		}
		catch(final IOException e)
		{
			throw wrap(hash, e);
		}
	}

	private static final String REQUEST_METHOD_HEAD = "HEAD";

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		try
		{
			final HttpURLConnection connection = connectForGet(hash, null);
			final int contentLength = connection.getContentLength();
			final byte[] result = new byte[contentLength];
			try(InputStream in = connection.getInputStream())
			{
				int contentRead = 0;
				while(contentRead<contentLength)
				{
					final int len = in.read(result, contentRead, contentLength-contentRead);
					if(len<0)
						break;
					contentRead += len;
				}
			}
			return result;
		}
		catch(final IOException e)
		{
			throw wrap(hash, e);
		}
	}

	@Override
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		final HttpURLConnection connection = connectForGet(hash, null);
		final byte[] result = new byte[Math.min(connection.getContentLength(), 8*1024)];
		try(InputStream in = connection.getInputStream())
		{
			for(int len = in.read(result); len>=0; len = in.read(result))
			{
				sink.write(result, 0, len);
			}
		}
	}

	private HttpURLConnection connectForGet(
			final String hash,
			final String requestMethod)
			throws VaultNotFoundException, IOException
	{
		if(hash==null)
			throw new NullPointerException();
		if(hash.isEmpty())
			throw new IllegalArgumentException();

		final URL url = new URL(rootUrl + '/' + directory.path(hash));
		final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		properties.setConnection(connection, true);
		if(requestMethod!=null)
			connection.setRequestMethod(requestMethod);
		final int responseCode = connection.getResponseCode();
		switch(responseCode)
		{
			case HTTP_OK:
				return connection;
			case HTTP_NOT_FOUND:
				throw new VaultNotFoundException(hash);
			default:
				throw new RuntimeException(rootUrl + ':' + responseCode + ':' + anonymiseHash(hash));
		}
	}

	private RuntimeException wrap(final String hash, final IOException exception)
	{
		throw new RuntimeException(rootUrl + ':' + anonymiseHash(hash), exception);
	}


	@Override
	// Method signature shall NOT narrow down specification from VaultService to
	//   URL probeGenuineServiceKey(String serviceKey) throws IOException
	// so we are free to change signature in the future without breaking API compatibility.
	public Object probeGenuineServiceKey(final String serviceKey) throws Exception
	{
		final URL url = new URL(rootUrl + '/' + VAULT_GENUINE_SERVICE_KEY + '/' + serviceKey);
		final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		properties.setConnection(connection, false);
		connection.setRequestMethod(REQUEST_METHOD_HEAD);
		final int responseCode = connection.getResponseCode();
		if(responseCode!=HTTP_OK)
			throw new IllegalStateException(
					"response code " + responseCode + ':' + url);
		final long size = connection.getContentLength();
		if(size!=0 && // file must not have any content, because it is likely exposed to public
			size!=-1) // result -1 is returned if there is no Content-Length header, which happens for empty files as well
			throw new IllegalStateException(
					"is not empty, but has size " + size + ':' + url);

		return url;
	}


	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ':' + rootUrl;
	}


	static final class Props extends Properties
	{
		final String root = value("root", (String)null);

		final VaultDirectory.Properties directory = value("directory", true, s -> new VaultDirectory.Properties(s, false));

		Props(final Source source)
		{
			super(source);

			if(root.endsWith("/"))
				throw newException("root", "must not end with slash, but was >" + root + '<');
			try
			{
				//noinspection ResultOfObjectAllocationIgnored OK: just for checking that url is valid
				new URL(root);
			}
			catch(final MalformedURLException e)
			{
				throw newException("root", "is malformed: >" + root + '<', e);
			}
		}

		private final int connectTimeout = toIntExact(value("connectTimeout", ofSeconds(3), Duration.ZERO).toMillis());
		private final int readTimeout    = toIntExact(value("readTimeout",    ofSeconds(3), Duration.ZERO).toMillis());
		private final boolean useCaches       = value("useCaches", false);
		private final boolean followRedirects = value("followRedirects", false);

		void setConnection(final HttpURLConnection connection, final boolean useCaches)
		{
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.setUseCaches(useCaches && this.useCaches);
			connection.setInstanceFollowRedirects(followRedirects);
		}

		@Probe(name="root.Exists")
		private URL probeRootExists() throws IOException
		{
			final URL url = new URL(root + '/');
			final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			setConnection(connection, false);
			connection.setRequestMethod(REQUEST_METHOD_HEAD);
			final int responseCode = connection.getResponseCode();
			switch(responseCode)
			{
				case HTTP_OK:
				// The error codes below are ok if directory listing is forbidden and
				// there is no index.html as well. At least we check whether host is reachable.
				case HTTP_FORBIDDEN:
				case HTTP_NOT_FOUND:
					return url;
				default:
					throw new IllegalArgumentException(
							"does respond with code other than " + HTTP_OK + ", " + HTTP_FORBIDDEN + " or " + HTTP_NOT_FOUND + ": " +
							responseCode + " >" + url + '<');
			}
		}
	}
}
