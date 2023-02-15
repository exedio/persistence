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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscribers;
import java.time.Duration;
import java.util.OptionalLong;
import java.util.function.Supplier;

@ServiceProperties(VaultHttpService.Props.class)
public final class VaultHttpService extends VaultNonWritableService
{
	private final String rootUri;
	private final VaultDirectory directory;
	private final Props properties;

	VaultHttpService(
			final VaultServiceParameters parameters,
			final Props properties)
	{
		super(parameters);
		this.rootUri = properties.root;
		this.directory = VaultDirectory.instance(properties.directory, parameters);
		this.properties = properties;
	}


	@Override
	public long getLength(final String hash) throws VaultNotFoundException
	{
		try
		{
			return getContentLength(getOk(hash, REQUEST_METHOD_HEAD, BodySubscribers::discarding), hash);
		}
		catch(final IOException e)
		{
			throw wrap(hash, e);
		}
	}

	private long getContentLength(
			final HttpResponse<?> response,
			final String hash)
	{
		return getContentLength(response).orElseThrow(
				() -> new RuntimeException(
						CONTENT_LENGTH + " header missing at " + rootUri + ':' + anonymiseHash(hash)));
	}

	private static OptionalLong getContentLength(
			final HttpResponse<?> response)
	{
		return response.headers().firstValueAsLong(CONTENT_LENGTH);
	}

	private static final String CONTENT_LENGTH = "Content-Length";


	private static final String REQUEST_METHOD_HEAD = "HEAD";

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		try
		{
			return getOk(hash, null, BodySubscribers::ofByteArray).body();
		}
		catch(final IOException e)
		{
			throw wrap(hash, e);
		}
	}

	@Override
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		final HttpResponse<InputStream> response = getOk(hash, null, BodySubscribers::ofInputStream);
		final byte[] result = new byte[toIntExact(Math.min(getContentLength(response, hash), 8*1024))];
		try(InputStream in = response.body())
		{
			for(int len = in.read(result); len>=0; len = in.read(result))
			{
				sink.write(result, 0, len);
			}
		}
	}

	private <T> HttpResponse<T> getOk(
			final String hash,
			final String requestMethod,
			final Supplier<HttpResponse.BodySubscriber<T>> bodySubscriberOk)
			throws VaultNotFoundException, IOException
	{
		if(hash==null)
			throw new NullPointerException();
		if(hash.isEmpty())
			throw new IllegalArgumentException();

		final HttpResponse<T> response;
		try
		{
			final URI uri = new URI(rootUri + '/' + directory.path(hash));
			response = properties.client.send(
					properties.newRequest(uri, requestMethod),
					info -> info.statusCode()==HTTP_OK
					? bodySubscriberOk.get()
					: BodySubscribers.replacing(null));
		}
		catch(URISyntaxException | InterruptedException e)
		{
			throw wrap(hash, e);
		}

		final int responseCode = response.statusCode();
		switch(responseCode)
		{
			case HTTP_OK:
				return response;
			case HTTP_NOT_FOUND:
				throw new VaultNotFoundException(hash);
			default:
				throw new RuntimeException(rootUri + ':' + responseCode + ':' + anonymiseHash(hash));
		}
	}

	private RuntimeException wrap(final String hash, final Exception exception)
	{
		throw new RuntimeException(rootUri + ':' + anonymiseHash(hash), exception);
	}


	@Override
	// Method signature shall NOT narrow down specification from VaultService to
	//   URI probeGenuineServiceKey(String serviceKey) throws IOException
	// so we are free to change signature in the future without breaking API compatibility.
	public Object probeGenuineServiceKey(final String serviceKey) throws Exception
	{
		final URI uri = new URI(rootUri + '/' + VAULT_GENUINE_SERVICE_KEY + '/' + serviceKey);
		final HttpResponse<Void> response = properties.client.send(
				properties.newRequest(uri, REQUEST_METHOD_HEAD),
				responseInfo -> BodySubscribers.discarding());
		final int responseCode = response.statusCode();
		if(responseCode!=HTTP_OK)
			throw new IllegalStateException(
					"response code " + responseCode + ':' + uri);
		final OptionalLong size = getContentLength(response);
		if(size.isPresent() && // Content-Length header may be absent for empty files
			size.getAsLong()!=0) // file must not have any content, because it is likely exposed to public
			throw new IllegalStateException(
					"is not empty, but has size " + size.getAsLong() + ':' + uri);

		return uri;
	}


	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ':' + rootUri;
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
			final URI uri;
			try
			{
				uri = new URI(root);
			}
			catch(final URISyntaxException e)
			{
				throw newException("root", "syntax exception: >" + root + '<', e);
			}
			final String scheme = uri.getScheme();
			if(!"http".equals(scheme) &&
				!"https".equals(scheme))
				throw newException("root",
						"must be a uri with scheme http(s), " +
						"but was >" + root + "< with scheme >" + scheme + '<');
		}

		private final Duration connectTimeout = valueTimeout("connectTimeout", ofSeconds(3));
		private final Duration requestTimeout = valueTimeout("requestTimeout", ofSeconds(3));
		private final Redirect followRedirects = value("followRedirects", Redirect.NEVER);

		private Duration valueTimeout(
				final String key,
				final Duration defaultValue)
		{
			return
					value(key, defaultValue, ofSeconds(1));
		}

		final HttpClient client = HttpClient.newBuilder().
					connectTimeout(connectTimeout).
					followRedirects(followRedirects).
					build();

		HttpRequest newRequest(final URI uri, final String method)
		{
			final HttpRequest.Builder result = HttpRequest.newBuilder(uri);
			if(method!=null)
				result.method(method, BodyPublishers.noBody());
			return result.timeout(requestTimeout).build();
		}

		@Probe(name="root.Exists")
		private URI probeRootExists() throws URISyntaxException, IOException, InterruptedException
		{
			final URI uri = new URI(root + '/');
			final HttpResponse<Void> response = client.send(
					newRequest(uri, REQUEST_METHOD_HEAD),
					info -> BodySubscribers.discarding());
			final int responseCode = response.statusCode();
			switch(responseCode)
			{
				case HTTP_OK:
				// The error codes below are ok if directory listing is forbidden and
				// there is no index.html as well. At least we check whether host is reachable.
				case HTTP_FORBIDDEN:
				case HTTP_NOT_FOUND:
					return uri;
				default:
					throw new IllegalArgumentException(
							"does respond with code other than " + HTTP_OK + ", " + HTTP_FORBIDDEN + " or " + HTTP_NOT_FOUND + ": " +
							responseCode + " >" + uri + '<');
			}
		}
	}
}
