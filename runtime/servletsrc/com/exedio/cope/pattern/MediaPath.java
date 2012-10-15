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

package com.exedio.cope.pattern;

import static javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Condition;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.Pattern;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import com.exedio.cope.util.Properties;

public abstract class MediaPath extends Pattern
{
	private static final long serialVersionUID = 1l;

	private Mount mountIfMounted;

	private static final class Mount
	{
		final String urlPath;
		final boolean preventUrlGuessing;

		Mount(final MediaPath feature)
		{
			this.urlPath = feature.getType().getID() + '/' + feature.getName() + '/';
			this.preventUrlGuessing = feature.isAnnotationPresent(PreventUrlGuessing.class);
			if(preventUrlGuessing && feature.isAnnotationPresent(RedirectFrom.class))
				throw new RuntimeException(
						"not yet implemented: @" + PreventUrlGuessing.class.getSimpleName() +
						" at " + feature.getID() +
						" together with @" + RedirectFrom.class.getSimpleName());
		}
	}

	private String mediaRootUrl = null;

	@Override
	protected void onMount()
	{
		super.onMount();
		this.mountIfMounted = new Mount(this);
	}

	private Mount mount()
	{
		final Mount result = this.mountIfMounted;
		if(result==null)
			throw new IllegalStateException("feature not mounted");
		return result;
	}

	final String getUrlPath()
	{
		return mount().urlPath;
	}

	public final boolean isUrlGuessingPrevented()
	{
		return mount().preventUrlGuessing;
	}

	private final String getMediaRootUrl()
	{
		if(mediaRootUrl==null)
			mediaRootUrl = getType().getModel().getConnectProperties().getMediaRootUrl();

		return mediaRootUrl;
	}

	public boolean isContentTypeWrapped()
	{
		return true;
	}

	/**
	 * Represents a resource to be delivered the media servlet.
	 * Provides methods for retrieving different types of urls.
	 * All methods of Locator do not require a connected model or a transaction to work.
	 */
	public final class Locator
	{
		private final Item item;
		private final String catchphrase;
		private final String extension;
		private final String secret;

		Locator(
				final Item item,
				final String catchphrase,
				final String extension,
				final String secret)
		{
			this.item = item;
			this.catchphrase = catchphrase;
			this.extension = extension;
			this.secret = secret;
		}

		public MediaPath getFeature()
		{
			return MediaPath.this;
		}

		public String getPath()
		{
			final StringBuilder bf = new StringBuilder();
			appendPath(bf);
			return bf.toString();
		}

		/**
		 * Is equivalent to <tt>bf.{@link StringBuilder#append(String) append}({@link #getPath()});</tt>
		 */
		public void appendPath(final StringBuilder bf)
		{
			bf.append(getUrlPath());
			item.appendCopeID(bf);

			if(catchphrase!=null)
				bf.append('/').append(catchphrase);

			if(extension!=null)
				bf.append(extension);

			if(secret!=null)
				bf.append("?" + URL_TOKEN + "=").
					append(secret);
		}

		void appendPathInfo(final StringBuilder bf)
		{
			bf.append(getUrlPath());
			item.appendCopeID(bf);

			if(catchphrase!=null)
				bf.append('/').append(catchphrase);

			if(extension!=null)
				bf.append(extension);
		}

		@Override
		public String toString()
		{
			return getPath();
		}
	}

	@Wrap(order=20, doc="Returns a Locator the content of {0} is available under.")
	public final Locator getLocator(final Item item)
	{
		final String contentType = getContentType(item);

		if(contentType==null)
			return null;

		final MediaType mediaType =
			MediaType.forNameAndAliases(contentType);
		return new Locator(
				item,
				makeUrlCatchphrase(item),
				mediaType!=null ? mediaType.getExtension() : null,
				makeUrlToken(item));
	}

	/**
	 * Returns a URL the content of this media path is available under,
	 * if a {@link MediaServlet} is properly installed.
	 * Returns null, if there is no such content.
	 */
	@Wrap(order=10, doc="Returns a URL the content of {0} is available under.")
	public final String getURL(final Item item)
	{
		final String contentType = getContentType(item);

		if(contentType==null)
			return null;

		final StringBuilder bf = new StringBuilder(getMediaRootUrl());

		bf.append(getUrlPath());
		item.appendCopeID(bf);

		final String catchphrase = makeUrlCatchphrase(item);
		if(catchphrase!=null)
			bf.append('/').append(catchphrase);

		final MediaType type = MediaType.forNameAndAliases(contentType);
		if(type!=null)
			bf.append(type.getExtension());

		final String secret = makeUrlToken(item);
		if(secret!=null)
			bf.append("?" + URL_TOKEN + "=").
				append(secret);

		return bf.toString();
	}

	private final String makeUrlCatchphrase(final Item item)
	{
		if(!(item instanceof MediaUrlCatchphraseProvider))
			return null;

		final String result = ((MediaUrlCatchphraseProvider)item).getMediaUrlCatchphrase(this);
		if(result==null || result.isEmpty())
			return null;

		final int l = result.length();
		for(int i = 0; i<l; i++)
		{
			final char c = result.charAt(i);
			if(! (('0'<=c&&c<='9')||('a'<=c&&c<='z')||('A'<=c&&c<='Z')||(c=='-')) )
				throw new IllegalArgumentException(result);
		}

		return result;
	}

	static final String URL_TOKEN = "t";

	private final String makeUrlToken(final Item item)
	{
		if(!mount().preventUrlGuessing)
			return null;

		final String sss = getNonGuessableUrlSecret();
		if(sss==null)
		{
			final StringBuilder bf = new StringBuilder();
			bf.append(getID()).
				append('-');
			item.appendCopeID(bf);
			return bf.toString();
		}

		final StringBuilder bf = new StringBuilder();
		bf.append(getUrlPath());
		item.appendCopeID(bf);
		bf.append('-').
			append(sss);
		return makeUrlTokenDigest(bf.toString());
	}

	private final String makeUrlToken(final String itemID)
	{
		if(!mount().preventUrlGuessing)
			return null;

		final String sss = getNonGuessableUrlSecret();
		if(sss==null)
			return getID() + '-' + itemID;

		return makeUrlTokenDigest(getUrlPath() + itemID + '-' + sss);
	}

	private final static String makeUrlTokenDigest(final String plainText)
	{
		try
		{
			final MessageDigest messageDigest = MessageDigestUtil.getInstance("SHA-512");
			messageDigest.update(plainText.getBytes("utf8"));
			final byte[] digest = messageDigest.digest();
			final byte[] digestShrink = new byte[10];
			int j = 0;
			for(final byte b : digest)
			{
				digestShrink[j++] ^= b;
				if(j>=digestShrink.length)
					j = 0;
			}
			return Hex.encodeLower(digestShrink);
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static final boolean isUrlGuessingPreventedSecurely(final ConnectProperties properties)
	{
		return getNonGuessableUrlSecret(properties)!=null;
	}

	private final String getNonGuessableUrlSecret()
	{
		return getNonGuessableUrlSecret(getType().getModel().getConnectProperties());
	}

	private static final String getNonGuessableUrlSecret(final ConnectProperties properties)
	{
		final Properties.Source context = properties.getContext();
		if(context==null)
			return null;

		final String result = context.get("media.url.secret");
		if(result==null || result.length()<10)
			return null;

		return result;
	}


	static final Log noSuchPath = new Log("no such path"  , HttpServletResponse.SC_NOT_FOUND);
	final Log redirectFrom      = new Log("redirectFrom"  , HttpServletResponse.SC_MOVED_PERMANENTLY);
	final Log exception         = new Log("exception"     , HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	private final Log guessedUrl = new Log("guessed url"  , HttpServletResponse.SC_NOT_FOUND);
	final Log notAnItem         = new Log("not an item"   , HttpServletResponse.SC_NOT_FOUND);
	final Log noSuchItem        = new Log("no such item"  , HttpServletResponse.SC_NOT_FOUND);
	final Log moved             = new Log("moved"         , HttpServletResponse.SC_OK);
	public final Log isNull     = new Log("is null"       , HttpServletResponse.SC_NOT_FOUND);
	final Log notComputable     = new Log("not computable", HttpServletResponse.SC_NOT_FOUND);
	final Log notModified       = new Log("not modified"  , HttpServletResponse.SC_OK);
	public final Log delivered  = new Log("delivered"     , HttpServletResponse.SC_OK);

	public static final int getNoSuchPath()
	{
		return noSuchPath.get();
	}

	public final MediaInfo getInfo()
	{
		return new MediaInfo(
				this,
				redirectFrom.get(),
				exception.get(),
				guessedUrl.get(),
				notAnItem.get(),
				noSuchItem.get(),
				moved.get(),
				isNull.get(),
				notComputable.get(),
				notModified.get(),
				delivered.get());
	}


	final Media.Log doGet(
			final HttpServletRequest request, final HttpServletResponse response,
			final String pathInfo, final int fromIndex)
		throws IOException
	{
		//final long start = System.currentTimeMillis();

		final int slash = pathInfo.indexOf('/', fromIndex);
		final String id;
		if(slash<0)
		{
			final int dot = pathInfo.indexOf('.', fromIndex);
			//System.out.println("trailingDot="+trailingDot);

			if(dot>=0)
				id = pathInfo.substring(fromIndex, dot);
			else
				id = pathInfo.substring(fromIndex);
		}
		else
		{
			id = pathInfo.substring(fromIndex, slash);
		}

		final String token = makeUrlToken(id);
		if(token!=null)
		{
			final String x = request.getParameter(URL_TOKEN);
			if(!token.equals(x))
				return guessedUrl;
		}

		//System.out.println("ID="+id);
		final Model model = getType().getModel();
		try
		{
			model.startTransaction("MediaPath#doGet " + pathInfo);
			final Item item = model.getItem(id);
			//System.out.println("item="+item);
			{
				final Locator locator = getLocator(item);
				if(locator!=null)
				{
					final StringBuilder expectedPathInfo = new StringBuilder();
					expectedPathInfo.append('/');
					locator.appendPathInfo(expectedPathInfo);
					if(!expectedPathInfo.toString().equals(pathInfo))
					{
						final StringBuilder location = new StringBuilder();
						location.
							append(request.getScheme()).
							append("://").
							append(request.getHeader("Host")).
							append(request.getContextPath()).
							append(request.getServletPath()).
							append('/');
						locator.appendPath(location);

						response.setStatus(SC_MOVED_PERMANENTLY);
						response.setHeader("Location", location.toString());
						return moved;
					}
				}
			}

			final Media.Log result = doGet(request, response, item);
			model.commit();

			//System.out.println("request for " + toString() + " took " + (System.currentTimeMillis() - start) + " ms, " + result.name + ", " + id);
			return result;
		}
		catch(final NoSuchIDException e)
		{
			return e.notAnID() ? notAnItem : noSuchItem;
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}

	@Wrap(order=30, doc="Returns the content type of the media {0}.", hide=ContentTypeGetter.class)
	public abstract String getContentType(Item item);

	private static final class ContentTypeGetter implements BooleanGetter<MediaPath>
	{
		public boolean get(final MediaPath feature)
		{
			return !feature.isContentTypeWrapped();
		}
	}

	public abstract Media.Log doGet(HttpServletRequest request, HttpServletResponse response, Item item)
		throws IOException;

	/**
	 * Returns a condition matching all items, for which {@link #getLocator(Item)} returns null.
	 */
	public abstract Condition isNull();

	/**
	 * Returns a condition matching all items, for which {@link #getLocator(Item)} returns null.
	 */
	public abstract Condition isNull(final Join join);

	/**
	 * Returns a condition matching all items, for which {@link #getLocator(Item)} does not return null.
	 */
	public abstract Condition isNotNull();

	/**
	 * Returns a condition matching all items, for which {@link #getLocator(Item)} does not return null.
	 */
	public abstract Condition isNotNull(final Join join);


	public final static class Log
	{
		private volatile int counter = 0;
		final String name;
		public final int responseStatus;

		Log(final String name, final int responseStatus)
		{
			if(name==null)
				throw new NullPointerException();
			switch(responseStatus)
			{
				case HttpServletResponse.SC_OK:
				case HttpServletResponse.SC_MOVED_PERMANENTLY:
				case HttpServletResponse.SC_NOT_FOUND:
				case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
					break;
				default:
					throw new RuntimeException(String.valueOf(responseStatus));
			}

			this.name = name;
			this.responseStatus = responseStatus;
		}

		void increment()
		{
			counter++; // may loose a few counts due to concurrency, but this is ok
		}

		public int get()
		{
			return counter;
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @param name is ignored
	 * @deprecated Use {@link #getURL(Item)} and {@link MediaUrlCatchphraseProvider#getMediaUrlCatchphrase(MediaPath)} instead.
	 */
	@Deprecated public final String getNamedURL(final Item item, final String name)
	{
		return getURL(item);
	}
}
