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
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WrapFeature
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
			this.preventUrlGuessing = isMyAnnotationPresent(feature, PreventUrlGuessing.class);
			this.urlFingerPrinting  = isMyAnnotationPresent(feature, UrlFingerPrinting.class);
			if(preventUrlGuessing && feature.isAnnotationPresent(RedirectFrom.class))
				throw new RuntimeException(
						"not yet implemented: @" + PreventUrlGuessing.class.getSimpleName() +
						" at " + feature.getID() +
						" together with @" + RedirectFrom.class.getSimpleName());
		}
	}

	/**
	 * Must be consistent to {@link #getMyAnnotation(Pattern,Class)}.
	 */
	static boolean isMyAnnotationPresent(
			final Pattern feature,
			final Class<? extends Annotation> annotationClass)
	{
		assertMine(annotationClass);

		return
				feature.          isAnnotationPresent(annotationClass) ||
				feature.getType().isAnnotationPresent(annotationClass);
	}

	/**
	 * Must be consistent to {@link #isMyAnnotationPresent(Pattern,Class)}.
	 */
	static <T extends Annotation> T getMyAnnotation(
			final Pattern feature,
			final Class<T> annotationClass)
	{
		assertMine(annotationClass);

		final T byFeature = feature.getAnnotation(annotationClass);
		if(byFeature!=null)
			return byFeature;

		return feature.getType().getAnnotation(annotationClass);
	}

	private static void assertMine(
			final Class<? extends Annotation> annotationClass)
	{
		if(!(
				annotationClass==PreventUrlGuessing.class ||
				annotationClass==UrlFingerPrinting.class  ))
			throw new IllegalArgumentException("" + annotationClass);
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		this.mountIfMounted = new Mount(this);
	}

	private Mount mountPath()
	{
		return requireMounted(mountIfMounted);
	}

	final String getUrlPath()
	{
		return mountPath().urlPath;
	}

	public final boolean isUrlGuessingPrevented()
	{
		return mountPath().preventUrlGuessing;
	}

	public final boolean isUrlFingerPrinted()
	{
		return mountPath().urlFingerPrinting;
	}

	final String getMediaRootUrl()
	{
		return connectProperties().getMediaRootUrl();
	}

	final long fingerprintOffset(final long lastModified, final Item item)
	{
		return connectProperties().mediaFingerprintOffset().apply(lastModified, item);
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
	public final class Locator implements Serializable
	{
		private static final long serialVersionUID = 1l;

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
		 * Is equivalent to {@code bf.{@link StringBuilder#append(String) append}({@link #getPath()});}
		 */
		public void appendPath(final StringBuilder bf)
		{
			bf.append(getUrlPath());

			if(fingerprintLastModified!=Long.MIN_VALUE)
			{
				bf.append("." + SPECIAL_FINGERPRINT);
				MediaBase64.append(bf, fingerprintOffset(fingerprintLastModified, item));
				bf.append('/');
			}

			if(secret!=null)
			{
				bf.append("." + SPECIAL_TOKEN).
					append(secret).
					append('/');
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
		}

		/**
		 * Returns the same value as {@link MediaPath#getURL(Item)}.
		 * @see #appendURLByConnect(StringBuilder)
		 */
		@Nonnull public String getURLByConnect()
		{
			final StringBuilder bf = new StringBuilder();
			appendURLByConnect(bf);
			return bf.toString();
		}

		/**
		 * Is equivalent to {@code bf.{@link StringBuilder#append(String) append}({@link #getURLByConnect()});}
		 */
		public void appendURLByConnect(final StringBuilder bf)
		{
			bf.append(getMediaRootUrl());
			appendPath(bf);
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

	/**
	 * Returns a locator the content of this media path is available under,
	 * if a {@link MediaServlet} is properly installed.
	 * Returns null, if there is no such content.
	 */
	@Wrap(order=20, doc=Wrap.MEDIA_LOCATOR, nullability=NullableIfMediaPathOptional.class)
	public final Locator getLocator(@Nonnull final Item item)
	{
		final String contentType = getContentType(item);

		if(contentType==null)
			return null;

		return new Locator(
				item,
				mountPath().urlFingerPrinting ? getLastModified(item) : null,
				contentType,
				makeUrlToken(item));
	}

	/**
	 * Returns a URL the content of this media path is available under,
	 * if a {@link MediaServlet} is properly installed.
	 * Returns null, if there is no such content.
	 * @see Locator#getURLByConnect()
	 */
	@Wrap(order=10, doc=Wrap.MEDIA_URL, nullability=NullableIfMediaPathOptional.class)
	public final String getURL(@Nonnull final Item item)
	{
		final Locator locator = getLocator(item);
		return locator!=null ? locator.getURLByConnect() : null;
	}

	private String makeUrlToken(final Item item)
	{
		if(!mountPath().preventUrlGuessing)
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

	private String makeUrlToken(final String itemID)
	{
		if(!mountPath().preventUrlGuessing)
			return null;

		final String sss = connectProperties().getMediaUrlSecret();
		if(sss==null)
			return getID() + '-' + itemID;

		return makeUrlTokenDigest(getUrlPath() + itemID + '-' + sss);
	}

	private static String makeUrlTokenDigest(final String plainText)
	{
		final byte[] digest =
				MessageDigestUtil.getInstance("SHA-512").
						digest(plainText.getBytes(UTF_8));
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
	private final AtomicLong  redirectFrom = new AtomicLong();
	private final ErrorLog    exception = new ErrorLog();
	private final ErrorLog    invalidSpecial = new ErrorLog();
	private final ErrorLog    guessedUrl = new ErrorLog();
	private final ErrorLog    notAnItem = new ErrorLog();
	private final ErrorLog    noSuchItem = new ErrorLog();
	private final AtomicLong  moved = new AtomicLong();
	private final ErrorLog    isNull = new ErrorLog();
	private final ErrorLog    notComputable = new ErrorLog();
	private final AtomicLong  notModified = new AtomicLong();
	private final AtomicLong  delivered = new AtomicLong();

	final void incRedirectFrom()
	{
		redirectFrom.incrementAndGet();
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

		@SuppressWarnings("TransientFieldNotInitialized") // OK: class gets along without counter
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

			@SuppressWarnings("HardcodedLineSeparator") // OK unix newline in html
			final String body =
				"<html>\n" +
					"<head>\n" +
						"<title>Not Found</title>\n" +
						"<meta http-equiv=\"content-type\" content=\"text/html;charset=us-ascii\">\n" +
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

	private NotFound notFoundIsNullEarly()
	{
		return new NotFound("is null", isNull);
	}

	/**
	 * The result of this method should be thrown by
	 * {@link #doGetAndCommit(HttpServletRequest, HttpServletResponse, Item)}
	 * if and only if {@link #getContentType(Item)} would have returned {@code null}
	 * for the same parameter {@code item}.
	 * This case should happen only when
	 * <ul>
	 * <li>a rare race condition occurs or
	 * <li>{@code doGetAndCommit} and {@code getContentType} are implemented
	 *     inconsistently.
	 * </ul>
	 */
	protected final NotFound notFoundIsNull()
	{
		return new NotFound("is null late", isNull);
	}

	protected final NotFound notFoundNotComputable()
	{
		return new NotFound("not computable", notComputable);
	}

	public static final int getNoSuchPath()
	{
		return toIntMetrics(noSuchPath.get());
	}

	public final MediaInfo getInfo()
	{
		return new MediaInfo(
				this,
				toIntMetrics(redirectFrom.get()),
				toIntMetrics(exception.get()),
				toIntMetrics(invalidSpecial.get()),
				toIntMetrics(guessedUrl.get()),
				toIntMetrics(notAnItem.get()),
				toIntMetrics(noSuchItem.get()),
				toIntMetrics(moved.get()),
				toIntMetrics(isNull.get()),
				toIntMetrics(notComputable.get()),
				toIntMetrics(notModified.get()),
				toIntMetrics(delivered.get()));
	}

	private static int toIntMetrics(final long l)
	{
		if(l>Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		if(l<Integer.MIN_VALUE)
			return Integer.MIN_VALUE;
		return (int)l;
	}

	/**
	 * For testing only. TODO
	 */
	public final void incrementDelivered()
	{
		delivered.incrementAndGet();
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
		// NOTE
		// This code prevents a Denial of Service attack against the caching mechanism.
		// Query strings can be used to effectively disable the cache by using many urls
		// for one media value. Therefore they are forbidden completely.
		if(request.getQueryString()!=null)
			throw notFoundNotAnItem();

		String actualToken = null;
		int fromIndex = fromIndexWithSpecial;
		char lastKind = '\0';
		while(pathInfo.length()>fromIndex && pathInfo.charAt(fromIndex)=='.')
		{
			final int kindIndex = fromIndex+1;
			if(!(pathInfo.length()>kindIndex))
				throw notFoundInvalidSpecial();

			final int slash = pathInfo.indexOf('/', kindIndex);
			if(slash<0)
				throw notFoundInvalidSpecial();
			fromIndex = slash + 1;

			final char kind = pathInfo.charAt(kindIndex);

			if(kind<=lastKind)
				throw notFoundInvalidSpecial(); // duplicate tokens or wrong order
			lastKind = kind;

			switch(kind)
			{
				case SPECIAL_FINGERPRINT:
					break; // do nothing, redirects are implemented below

				case SPECIAL_TOKEN:
					actualToken = pathInfo.substring(kindIndex+1, slash);
					break;

				default:
					throw notFoundInvalidSpecial();
			}
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
			if(!token.equals(actualToken))
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
					locator.appendPath(expectedPathInfo);
					if(!expectedPathInfo.toString().equals(pathInfo))
					{
						final StringBuilder location = new StringBuilder();
						location.
							// There is no need for absolute url anymore: https://en.wikipedia.org/wiki/HTTP_location
							append(request.getContextPath()).
							append(request.getServletPath()).
							append('/');
						locator.appendPath(location);

						response.setStatus(SC_MOVED_PERMANENTLY);
						response.setHeader("Location", location.toString());
						moved.incrementAndGet();
						return;
					}
				}
				else
					throw notFoundIsNullEarly();
			}

			if(servlet.isAccessControlAllowOriginWildcard(this, item))
				response.setHeader("Access-Control-Allow-Origin", "*");

			doGetAndCommitWithCache(servlet, request, response, item);

			if(tx.hasCurrentTransaction())
				throw new RuntimeException("doGetAndCommit did not commit: " + pathInfo);
		}
		catch(final NoSuchIDException e)
		{
			throw e.notAnID() ? notFoundNotAnItem() : notFoundNoSuchItem();
		}
	}

	private static final char SPECIAL_FINGERPRINT = 'f';
	private static final char SPECIAL_TOKEN = 't';

	protected final void commit()
	{
		getType().getModel().commit();
	}

	/** A MediaPath is "mandatory" if {@link #getContentType(Item)} and {@link #getLocator(Item)} will
	 * not return {@code null} for any item. */
	public abstract boolean isMandatory();

	/**
	 * You must implement this method consistently with
	 * {@link #doGetAndCommit(HttpServletRequest, HttpServletResponse, Item)}.
	 * That means:
	 * <ul>
	 * <li>If and only if this method returns null
	 *     for any given {@code item},
	 *     {@code doGetAndCommit} for the same {@code item}
	 *     should throw a {@link #notFoundIsNull()}.
	 * <li>If and only if this method returns a non-null result {@code C}
	 *     for any given {@code item},
	 *     {@code doGetAndCommit} for the same {@code item}
	 *     should put a 200-OK into its {@code response} parameter
	 *     with the {@code Content-Type} header set to {@code C}.
	 * </ul>
	 * This method must not return null at all, if
	 * {@link #isMandatory()} returns {@code true}.
	 */
	@Wrap(order=30, doc=Wrap.MEDIA_CONTENT_TYPE, hide=ContentTypeGetter.class, nullability=NullableIfMediaPathOptional.class)
	public abstract String getContentType(@Nonnull Item item);

	// cache

	private void doGetAndCommitWithCache(
			final MediaServlet servlet,
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
		throws IOException, NotFound
	{
		final Date lastModifiedRaw = getLastModified(item);
		// if there is no LastModified, then there is no caching
		if(lastModifiedRaw==null)
		{
			setCacheControl(servlet, response, item, null);
			deliver(request, response, item);
			return;
		}

		// NOTE:
		// Last Modification Date must be rounded to full seconds,
		// otherwise comparison for SC_NOT_MODIFIED doesn't work.
		final long lastModified = roundLastModified(lastModifiedRaw);
		response.setDateHeader("Last-Modified", lastModified);

		final Duration cacheControlMaxAge;
		if(isUrlFingerPrinted())
		{
			// RFC 2616:
			// To mark a response as "never expires," an origin server sends an
			// Expires date approximately one year from the time the response is
			// sent. HTTP/1.1 servers SHOULD NOT send Expires dates more than one
			// year in the future.
			cacheControlMaxAge = Duration.ofDays(363);
		}
		else
		{
			cacheControlMaxAge = servlet.getMaximumAge(this, item);
		}

		setCacheControl(servlet, response, item, cacheControlMaxAge);

		final long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if(ifModifiedSince>=0 && ifModifiedSince>=lastModified)
		{
			final boolean flush =
					servlet.doFlushBufferOnNotModified(this, item);

			commit();

			response.setStatus(SC_NOT_MODIFIED);

			if(flush)
				response.flushBuffer();

			notModified.incrementAndGet();
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

	private void setCacheControl(
			final MediaServlet servlet,
			final HttpServletResponse response,
			final Item item,
			final Duration maxAge)
	{
		// RFC 2616
		// 4.2 Message Headers
		// Multiple message-header fields with the same field-name MAY be
		// present in a message if and only if the entire field-value for that
		// header field is defined as a comma-separated list [i.e., #(values)].
		// It MUST be possible to combine the multiple header fields into one
		// "field-name: field-value" pair, without changing the semantics of the
		// message, by appending each subsequent field-value to the first, each
		// separated by a comma.

		final StringBuilder bf = new StringBuilder();

		if(servlet.isCacheControlPrivate(this, item))
			bf.append("private");

		if(maxAge!=null)
		{
			if(bf.length()!=0)
				bf.append(',');

			bf.append("max-age=").
				append(maxAge.isNegative() ? 0 : maxAge.getSeconds());
		}

		if(bf.length()!=0)
			response.setHeader("Cache-Control", bf.toString());
	}

	private void deliver(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
		throws IOException, NotFound
	{
		doGetAndCommit(request, response, item);
		delivered.incrementAndGet();
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
	 * You must implement this method consistently with
	 * {@link #getContentType(Item)}. Refer to that method for details.
	 * <p>
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
	 * @throws UnsupportedOperationException if the condition is not supported by this path
	 */
	public Condition isNull() { throw unsupportedCondition(); }

	/**
	 * Returns a condition matching all items, for which {@link #getLocator(Item)} returns null.
	 * @param join the join the returned condition should be bound to
	 * @throws UnsupportedOperationException if the condition is not supported by this path
	 */
	public Condition isNull(final Join join) { throw unsupportedCondition(); }

	/**
	 * Returns a condition matching all items, for which {@link #getLocator(Item)} does not return null.
	 * @throws UnsupportedOperationException if the condition is not supported by this path
	 */
	public Condition isNotNull() { throw unsupportedCondition(); }

	/**
	 * Returns a condition matching all items, for which {@link #getLocator(Item)} does not return null.
	 * @param join the join the returned condition should be bound to
	 * @throws UnsupportedOperationException if the condition is not supported by this path
	 */
	public Condition isNotNull(final Join join) { throw unsupportedCondition(); }

	private  UnsupportedOperationException unsupportedCondition()
	{
		return new UnsupportedOperationException("condition not supported by " + getID() + " of " + getClass().getName());
	}
}
