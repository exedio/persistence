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

package com.exedio.cope.pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;

import com.exedio.cope.Condition;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.Pattern;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class MediaPath extends Pattern
{
	private static final long serialVersionUID = 1l;

	private Mount mountIfMounted;

	private static final class Mount
	{
		final String urlPath;
		final boolean preventUrlGuessing;
		final boolean urlFingerPrinting;

		Mount(final MediaPath feature)
		{
			this.urlPath = feature.getType().getID() + '/' + feature.getName() + '/';
			this.preventUrlGuessing = feature.isAnnotationPresent(PreventUrlGuessing.class);
			this.urlFingerPrinting = !feature.isFinal() && feature.isAnnotationPresent(UrlFingerPrinting.class);
			if(preventUrlGuessing && feature.isAnnotationPresent(RedirectFrom.class))
				throw new RuntimeException(
						"not yet implemented: @" + PreventUrlGuessing.class.getSimpleName() +
						" at " + feature.getID() +
						" together with @" + RedirectFrom.class.getSimpleName());
			if(preventUrlGuessing && urlFingerPrinting)
				throw new RuntimeException(
						"not yet implemented: @" + PreventUrlGuessing.class.getSimpleName() +
						" at " + feature.getID() +
						" together with @" + UrlFingerPrinting.class.getSimpleName());
		}
	}

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

	public final boolean isUrlFingerPrinted()
	{
		return mount().urlFingerPrinting;
	}

	final String getMediaRootUrl()
	{
		return connectProperties().getMediaRootUrl();
	}

	final int getMediaOffsetFingerprint(final Item item)
	{
		return connectProperties().mediaFingerprintOffset().get(item);
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
		private final long fingerprintLastModified;
		private final String catchphrase;
		private final String contentType;
		private final String secret;

		Locator(
				final Item item,
				final Date fingerprintLastModified,
				final String contentType,
				final String secret)
		{
			this.item = item;
			this.fingerprintLastModified = fixFingerprintLastModified(fingerprintLastModified);
			this.catchphrase = makeUrlCatchphrase(item);
			this.contentType = contentType;
			this.secret = secret;
		}

		private long fixFingerprintLastModified(final Date fingerprintLastModified)
		{
			if(fingerprintLastModified==null)
				return Long.MIN_VALUE;

			final long fingerprintTime = fingerprintLastModified.getTime();
			return fingerprintTime!=Long.MIN_VALUE ? fingerprintTime : (Long.MIN_VALUE+1);
		}

		private String makeUrlCatchphrase(final Item item)
		{
			if(!(item instanceof MediaUrlCatchphraseProvider))
				return null;

			final String result = ((MediaUrlCatchphraseProvider)item).getMediaUrlCatchphrase(MediaPath.this);
			if(result==null || result.isEmpty())
				return null;

			final int l = result.length();
			for(int i = 0; i<l; i++)
			{
				final char c = result.charAt(i);
				if(! (('0'<=c&&c<='9')||('A'<=c&&c<='Z')||('a'<=c&&c<='z')||(c=='-')||(c=='_')) )
					throw new IllegalArgumentException(
							"illegal catchphrase" +
							" on " + item.getCopeID() +
							" for " + getID() +
							": >" + result + "< at position " + i);
			}

			return result;
		}

		public MediaPath getFeature()
		{
			return MediaPath.this;
		}

		public Item getItem()
		{
			return item;
		}

		public String getContentType()
		{
			return contentType;
		}

		/**
		 * @see #appendPath(StringBuilder)
		 */
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
			appendPath(bf, true);
		}

		void appendPath(
				final StringBuilder bf,
				final boolean withSecret)
		{
			bf.append(getUrlPath());

			if(fingerprintLastModified!=Long.MIN_VALUE)
			{
				bf.append(".f");
				MediaBase64.append(bf, fingerprintLastModified + getMediaOffsetFingerprint(item));
				bf.append('/');
			}

			item.appendCopeID(bf);

			if(catchphrase!=null)
				bf.append('/').append(catchphrase);

			{
				final MediaType mediaType =
					MediaType.forNameAndAliases(contentType);
				if(mediaType!=null)
				{
					final String extension = mediaType.getDefaultExtension();
					if(extension!=null)
						bf.append(extension);
				}
			}

			if(withSecret && secret!=null)
				bf.append("?" + URL_TOKEN + "=").
					append(secret);
		}

		/**
		 * Returns the same value as {@link MediaPath#getURL(Item)}.
		 * @see #appendURLByConnect(StringBuilder)
		 */
		public String getURLByConnect()
		{
			final StringBuilder bf = new StringBuilder();
			appendURLByConnect(bf);
			return bf.toString();
		}

		/**
		 * Is equivalent to <tt>bf.{@link StringBuilder#append(String) append}({@link #getURLByConnect()});</tt>
		 */
		public void appendURLByConnect(final StringBuilder bf)
		{
			bf.append(getMediaRootUrl());
			appendPath(bf, true);
		}

		@Override
		public boolean equals(final Object other)
		{
			if(!(other instanceof Locator))
				return false;

			final Locator o = (Locator)other;
			return
				MediaPath.this.equals(o.getFeature()) &&
				item.equals(o.item);
		}

		@Override
		public int hashCode()
		{
			return MediaPath.this.hashCode() ^ item.hashCode();
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

		return new Locator(
				item,
				mount().urlFingerPrinting ? getLastModified(item) : null,
				contentType,
				makeUrlToken(item));
	}

	/**
	 * Returns a URL the content of this media path is available under,
	 * if a {@link MediaServlet} is properly installed.
	 * Returns null, if there is no such content.
	 * @see Locator#getURLByConnect()
	 */
	@Wrap(order=10, doc="Returns a URL the content of {0} is available under.")
	public final String getURL(final Item item)
	{
		final Locator locator = getLocator(item);
		return locator!=null ? locator.getURLByConnect() : null;
	}

	static final String URL_TOKEN = "t";

	private final String makeUrlToken(final Item item)
	{
		if(!mount().preventUrlGuessing)
			return null;

		final String sss = connectProperties().getMediaUrlSecret();
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

		final String sss = connectProperties().getMediaUrlSecret();
		if(sss==null)
			return getID() + '-' + itemID;

		return makeUrlTokenDigest(getUrlPath() + itemID + '-' + sss);
	}

	private static final String makeUrlTokenDigest(final String plainText)
	{
		final MessageDigest messageDigest = MessageDigestUtil.getInstance("SHA-512");
		messageDigest.update(plainText.getBytes(UTF_8));
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

	public static final boolean isUrlGuessingPreventedSecurely(final ConnectProperties properties)
	{
		return properties.getMediaUrlSecret()!=null;
	}


	private static final ErrorLog noSuchPath = new ErrorLog();
	private final VolatileInt redirectFrom = new VolatileInt();
	private final ErrorLog    exception = new ErrorLog();
	private final ErrorLog    invalidSpecial = new ErrorLog();
	private final ErrorLog    guessedUrl = new ErrorLog();
	private final ErrorLog    notAnItem = new ErrorLog();
	private final ErrorLog    noSuchItem = new ErrorLog();
	private final VolatileInt moved = new VolatileInt();
	private final ErrorLog    isNull = new ErrorLog();
	private final ErrorLog    notComputable = new ErrorLog();
	private final VolatileInt notModified = new VolatileInt();
	private final VolatileInt delivered = new VolatileInt();

	final void incRedirectFrom()
	{
		redirectFrom.inc();
	}

	final void countException(
			final HttpServletRequest request,
			final Exception exception)
	{
		this.exception.count(request, exception);
	}

	public static final class NotFound extends Exception
	{
		private final String reason;
		private final transient ErrorLog counter;

		NotFound(final String reason, final ErrorLog counter)
		{
			this.reason = requireNonNull(reason);
			this.counter = requireNonNull(counter);
		}

		@Override
		public String getMessage()
		{
			return reason;
		}

		void serve(
				final HttpServletRequest request,
				final HttpServletResponse response)
		throws IOException
		{
			// counter may be null if exception had been deserialized
			if(counter!=null)
				counter.count(request, this);

			final String body =
				"<html>\n" +
					"<head>\n" +
						"<title>Not Found</title>\n" +
						"<meta http-equiv=\"content-type\" content=\"text/html;charset=us-ascii\">\n" +
						"<meta name=\"generator\" content=\"cope media servlet\">\n" +
					"</head>\n" +
					"<body>\n" +
						"<h1>Not Found</h1>\n" +
						"The requested URL was not found on this server (" + reason + ").\n" +
					"</body>\n" +
				"</html>\n";

			response.setStatus(SC_NOT_FOUND);
			MediaUtil.send("text/html", "us-ascii", body, response);
		}

		private static final long serialVersionUID = 1l;
	}

	static final NotFound notFoundNoSuchPath()
	{
		return new NotFound("no such path", noSuchPath);
	}

	private NotFound notFoundInvalidSpecial()
	{
		return new NotFound("invalid special", invalidSpecial);
	}

	private NotFound notFoundGuessedUrl()
	{
		return new NotFound("guessed url", guessedUrl);
	}

	final NotFound notFoundNotAnItem()
	{
		return new NotFound("not an item", notAnItem);
	}

	private NotFound notFoundNoSuchItem()
	{
		return new NotFound("no such item", noSuchItem);
	}

	protected final NotFound notFoundIsNull()
	{
		return new NotFound("is null", isNull);
	}

	protected final NotFound notFoundNotComputable()
	{
		return new NotFound("not computable", notComputable);
	}

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
				invalidSpecial.get(),
				guessedUrl.get(),
				notAnItem.get(),
				noSuchItem.get(),
				moved.get(),
				isNull.get(),
				notComputable.get(),
				notModified.get(),
				delivered.get());
	}

	/**
	 * For testing only. TODO
	 */
	public final void incrementDelivered()
	{
		delivered.inc();
	}

	public static final List<MediaRequestLog> getNoSuchPathLogs()
	{
		return noSuchPath.getLogs();
	}

	public final List<MediaRequestLog> getExceptionLogs()
	{
		return exception.getLogs();
	}

	public final List<MediaRequestLog> getInvalidSpecialLogs()
	{
		return invalidSpecial.getLogs();
	}

	public final List<MediaRequestLog> getGuessedUrlLogs()
	{
		return guessedUrl.getLogs();
	}

	public final List<MediaRequestLog> getNotAnItemLogs()
	{
		return notAnItem.getLogs();
	}

	public final List<MediaRequestLog> getNoSuchItemLogs()
	{
		return noSuchItem.getLogs();
	}

	public final List<MediaRequestLog> getIsNullLogs()
	{
		return isNull.getLogs();
	}

	public final List<MediaRequestLog> getNotComputableLogs()
	{
		return notComputable.getLogs();
	}


	final void doGet(
			final MediaServlet servlet,
			final HttpServletRequest request, final HttpServletResponse response,
			final String pathInfo, final int fromIndexWithSpecial)
		throws IOException, NotFound
	{
		final int fromIndex;
		if(pathInfo.length()>fromIndexWithSpecial && pathInfo.charAt(fromIndexWithSpecial)=='.')
		{
			final int kindIndex = fromIndexWithSpecial+1;
			if(!(pathInfo.length()>kindIndex))
				throw notFoundInvalidSpecial();

			switch(pathInfo.charAt(kindIndex))
			{
				case 'f':
					final int slash = pathInfo.indexOf('/', kindIndex);
					if(slash<0)
						throw notFoundInvalidSpecial();
					fromIndex = slash + 1;
					break;

				default:
					throw notFoundInvalidSpecial();
			}
		}
		else
		{
			fromIndex = fromIndexWithSpecial;
		}

		final int slash = pathInfo.indexOf('/', fromIndex);
		final String id;
		if(slash<0)
		{
			final int dot = pathInfo.indexOf('.', fromIndex);
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
				throw notFoundGuessedUrl();
		}

		final Model model = getType().getModel();
		try(TransactionTry tx = model.startTransactionTry("MediaPath#doGet " + pathInfo))
		{
			final Item item = tx.getItem(id);
			{
				final Locator locator = getLocator(item);
				if(locator!=null)
				{
					final StringBuilder expectedPathInfo = new StringBuilder();
					expectedPathInfo.append('/');
					locator.appendPath(expectedPathInfo, false);
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
						moved.inc();
						return;
					}
				}
			}

			if(servlet.isAccessControlAllowOriginWildcard(this, item))
				response.setHeader("Access-Control-Allow-Origin", "*");

			doGetAndCommitWithCache(request, response, item);

			if(tx.hasCurrentTransaction())
				throw new RuntimeException("doGetAndCommit did not commit: " + pathInfo);
		}
		catch(final NoSuchIDException e)
		{
			throw e.notAnID() ? notFoundNotAnItem() : notFoundNoSuchItem();
		}
	}

	protected final void commit()
	{
		getType().getModel().commit();
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

	// cache

	private final void doGetAndCommitWithCache(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
		throws IOException, NotFound
	{
		// NOTE
		// This code prevents a Denial of Service attack against the caching mechanism.
		// Query strings can be used to effectively disable the cache by using many urls
		// for one media value. Therefore they are forbidden completely.
		if(isUrlGuessingPrevented())
		{
			final String[] tokens = request.getParameterValues(URL_TOKEN);
			if(tokens!=null&&tokens.length>1)
				throw notFoundNotAnItem();
			for(final Enumeration<?> e = request.getParameterNames(); e.hasMoreElements(); )
				if(!URL_TOKEN.equals(e.nextElement()))
					throw notFoundNotAnItem();

			// Cache-Control forbids shared caches, such as company proxies to cache
			// such urls.
			// TODO make this customizable
			// See http://httpd.apache.org/docs/2.2/mod/mod_cache.html#cachestoreprivate
			// and RFC 2616 Section 14.9.1 What is Cacheable
			response.setHeader("Cache-Control", "private");
		}
		else
		{
			if(request.getQueryString()!=null)
				throw notFoundNotAnItem();
		}

		final Date lastModifiedRaw = getLastModified(item);
		// if there is no LastModified, then there is no caching
		if(lastModifiedRaw==null)
		{
			deliver(request, response, item);
			return;
		}

		// NOTE:
		// Last Modification Date must be rounded to full seconds,
		// otherwise comparison for SC_NOT_MODIFIED doesn't work.
		final long lastModified = roundLastModified(lastModifiedRaw);
		response.setDateHeader("Last-Modified", lastModified);

		if( isFinal() || isUrlFingerPrinted() )
		{
			// RFC 2616:
			// To mark a response as "never expires," an origin server sends an
			// Expires date approximately one year from the time the response is
			// sent. HTTP/1.1 servers SHOULD NOT send Expires dates more than one
			// year in the future.
			setExpiresHeader(response, 1000l*60*60*24*363); // 363 days
		}
		else
		{
			final int mediaOffsetExpires = connectProperties().getMediaOffsetExpires();
			if(mediaOffsetExpires>0)
				setExpiresHeader(response, mediaOffsetExpires);
		}

		final long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if(ifModifiedSince>=0 && ifModifiedSince>=lastModified)
		{
			commit();

			response.setStatus(SC_NOT_MODIFIED);
			notModified.inc();
		}
		else
		{
			deliver(request, response, item);
		}
	}

	private ConnectProperties connectProperties()
	{
		return getType().getModel().getConnectProperties();
	}

	/**
	 * Copied from com.exedio.cops.Resource.
	 */
	private static long roundLastModified(final Date lastModifiedDate)
	{
		final long lastModified = lastModifiedDate.getTime();
		final long remainder = lastModified%1000;
		return (remainder==0) ? lastModified : (lastModified-remainder+1000);
	}

	private static void setExpiresHeader(final HttpServletResponse response, final long offset)
	{
		response.setDateHeader("Expires", Clock.currentTimeMillis() + offset);
	}

	private void deliver(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
		throws IOException, NotFound
	{
		doGetAndCommit(request, response, item);
		delivered.inc();
	}

	/**
	 * Returns, whether the contents of this media path may change or not.
	 * The default implementation returns false.
	 */
	public boolean isFinal()
	{
		return false;
	}

	/**
	 * The default implementation returns null.
	 * @param item the item which has the LastModified information
	 */
	public Date getLastModified(final Item item)
	{
		return null;
	}

	/**
	 * The implementor MUST {@link #commit() commit} the transaction,
	 * if the method completes normally (without exception).
	 * Otherwise the implementor may or may not commit the transaction.
	 */
	public abstract void doGetAndCommit(
			HttpServletRequest request,
			HttpServletResponse response,
			Item item)
		throws IOException, NotFound;


	// convenience methods

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

	// ------------------- deprecated stuff -------------------

	@Deprecated
	public static final class Log
	{
		private Log()
		{
			// prevent instantiation
		}

		@SuppressWarnings("static-method")
		public int get()
		{
			throw new NoSuchMethodError();
		}
	}

	/**
	 * @param name is ignored
	 * @deprecated Use {@link #getURL(Item)} and {@link MediaUrlCatchphraseProvider#getMediaUrlCatchphrase(MediaPath)} instead.
	 */
	@Deprecated public final String getNamedURL(final Item item, final String name)
	{
		return getURL(item);
	}
}
