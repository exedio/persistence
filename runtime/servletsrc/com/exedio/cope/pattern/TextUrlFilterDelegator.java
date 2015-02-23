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

import static com.exedio.cope.misc.Check.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.pattern.TextUrlFilter.Paste;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TextUrlFilterDelegator extends MediaFilter implements TextUrlFilterCheckable
{
	private static final long serialVersionUID = 1l;

	private final Media raw;
	protected final TextUrlFilter delegate;
	private final String supportedContentType;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Charset charset;
	private final String pasteStart;
	private final String pasteStop;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final AnnotationProxy annotationProxy = new AnnotationProxy();

	public TextUrlFilterDelegator(
			final Media raw,
			final TextUrlFilter delegate,
			final String supportedContentType,
			final Charset charset,
			final String pasteStart,
			final String pasteStop)
	{
		super(raw);

		this.raw = raw;
		this.delegate = requireNonNull(delegate, "delegate");
		this.supportedContentType = requireNonEmpty(supportedContentType, "supportedContentType");
		this.charset    = requireNonNull (charset,    "charset");
		this.pasteStart = requireNonEmpty(pasteStart, "pasteStart");
		this.pasteStop  = requireNonEmpty(pasteStop,  "pasteStop");
		addSource(raw, "Raw", annotationProxy);
	}

	@Wrap(order=10, thrown=@Wrap.Thrown(IOException.class))
	public final void setRaw(
			final Item item,
			@Parameter("raw") final Media.Value raw )
	throws IOException
	{
		this.raw.set( item, raw );
	}

	private final Paste getPaste(final Item item, final String key)
	{
		return delegate.getPaste(item, key);
	}

	public final Locator getPasteLocator(final Item item, final String key)
	{
		return delegate.getPasteLocator(item, key);
	}

	public final String getPasteURL( final Item item, final String key )
	{
		return delegate.getPasteURL(item, key);
	}

	@Override
	public final String getContentType(final Item item)
	{
		final String contentType = raw.getContentType(item);
		return supportedContentType.equals(contentType) ? contentType : null;
	}

	@Override
	public final boolean isContentTypeWrapped()
	{
		return false; // since there is only one supportedContentType
	}

	@Override
	public final void doGetAndCommit(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
	throws IOException, NotFound
	{
		checkContentType( item );

		final byte[] sourceByte = raw.getBody().getArray(item);
		final String srcString = new String(sourceByte, charset);

		final StringBuilder bf = new StringBuilder( srcString.length() );
		final int nextStart = substitutePastes(bf, null, srcString, item, request);

		commit();

		if(nextStart>0)
		{
			bf.append(srcString.substring(nextStart));
			MediaUtil.send(supportedContentType, charset.name(), bf.toString(), response);
		}
		else
		{
			// short cut if there are no pastes at all
			MediaUtil.send(supportedContentType, sourceByte, response);
		}
	}

	@Wrap(order=20, thrown={@Wrap.Thrown(NotFound.class)})
	public final String getContent(
			final Item item,
			@Parameter("request") final HttpServletRequest request )
		throws NotFound
	{
		checkContentType( item );

		final byte[] sourceByte = raw.getBody().getArray(item);
		final String srcString = new String(sourceByte, charset);

		final StringBuilder bf = new StringBuilder( srcString.length() );
		final int nextStart = substitutePastes(bf, null, srcString, item, request);

		if(nextStart>0)
			return bf.append(srcString.substring(nextStart)).toString();
		else
			return srcString;
	}

	private void checkContentType( final Item item ) throws NotFound
	{
		final String sourceContentType = raw.getContentType( item );
		if(sourceContentType==null)
			throw notFoundIsNull();
		if(!supportedContentType.equals(sourceContentType) )
			throw notFoundNotComputable();
	}

	private int substitutePastes(
			final StringBuilder bf,
			final Set<String> brokenCodes,
			final String srcString,
			final Item item,
			final HttpServletRequest request)
	{
		final int pasteStartLen = pasteStart.length();
		final int pasteStopLen = pasteStop.length();
		int nextStart = 0;
		for( int start = srcString.indexOf( pasteStart ); start >= 0; start = srcString.indexOf( pasteStart, nextStart ) )
		{
			final int stop = srcString.indexOf( pasteStop, start );
			if( stop < 0 ) throw new IllegalArgumentException( pasteStart + ':' + start + '/' + pasteStop );

			final String key = srcString.substring(start + pasteStartLen, stop);
			if(bf!=null)
			{
				bf.append( srcString.substring( nextStart, start ) );
				appendKey(bf, item, key, request);
			}
			if(brokenCodes!=null)
			{
				try
				{
					getPaste(item, key);
				}
				catch(final IllegalArgumentException e)
				{
					brokenCodes.add(key);
				}
			}

			nextStart = stop + pasteStopLen;
		}
		return nextStart;
	}

	@Override
	@Wrap(order=30, thrown={@Wrap.Thrown(NotFound.class)})
	public Set<String> check( final Item item ) throws NotFound
	{
		checkContentType( item );
		final Set<String> brokenCodes = new HashSet<>();
		final byte[] sourceByte = getSource().getBody().getArray( item );
		final String srcString = new String( sourceByte, charset );
		substitutePastes(null, brokenCodes, srcString, item, null);
		return brokenCodes;
	}

	protected void appendKey(
			final StringBuilder bf,
			final Item item,
			final String key,
			final HttpServletRequest request)
	{
		appendURL(bf, getPasteLocator(item, key), request);
	}

	private static void appendURL(
			final StringBuilder bf,
			final Locator locator,
			final HttpServletRequest request)
	{
		bf.append(request.getContextPath());
		bf.append(request.getServletPath());
		bf.append('/');
		locator.appendPath(bf);
	}

	@Override
	public final Set<String> getSupportedSourceContentTypes()
	{
		return Collections.singleton(supportedContentType);
	}

	public final List<String> getPasteContentTypesAllowed()
	{
		return delegate.getPasteContentTypesAllowed();
	}

	private final class AnnotationProxy implements AnnotatedElement
	{
		AnnotationProxy()
		{
			// just to make non-private
		}

		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			return
				(PreventUrlGuessing.class==annotationClass || UrlFingerPrinting.class==annotationClass)
				? TextUrlFilterDelegator.this.isAnnotationPresent(annotationClass)
				: false;
		}

		public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
		{
			return
				(PreventUrlGuessing.class==annotationClass || UrlFingerPrinting.class==annotationClass)
				? TextUrlFilterDelegator.this.getAnnotation(annotationClass)
				: null;
		}

		public Annotation[] getAnnotations()
		{
			throw new RuntimeException(TextUrlFilterDelegator.this.toString());
		}

		public Annotation[] getDeclaredAnnotations()
		{
			throw new RuntimeException(TextUrlFilterDelegator.this.toString());
		}

		@Override
		public String toString()
		{
			return TextUrlFilterDelegator.this.toString() + "-annotations";
		}
	}
}
