/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Cope;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.misc.Computed;

public class TextUrlFilter extends MediaFilter
{
	private static final long serialVersionUID = 1l;

	private final Media raw;
	private final String supportedContentType;
	private final String encoding;
	private final String pasteStart;
	private final String pasteStop;

	private final StringField pasteKey;
	final Media pasteValue;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SE_BAD_FIELD") // Non-transient non-serializable instance field in serializable class
	private final PreventUrlGuessingProxy preventUrlGuessingProxy = new PreventUrlGuessingProxy();
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SE_BAD_FIELD") // Non-transient non-serializable instance field in serializable class
	private Mount mount = null;

	public TextUrlFilter(
			final Media raw,
			final String supportedContentType,
			final String encoding,
			final String pasteStart,
			final String pasteStop,
			final StringField pasteKey,
			final Media pasteValue)
	{
		super(raw);

		if(supportedContentType==null)
			throw new NullPointerException("supportedContentType");

		if(encoding==null)
			throw new NullPointerException("encoding");
		try
		{
			"zack".getBytes(encoding);
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new IllegalArgumentException(e);
		}

		if(pasteStart==null)
			throw new NullPointerException("pasteStart");
		if(pasteStart.isEmpty())
			throw new IllegalArgumentException("pasteStart");
		if(pasteStop==null)
			throw new NullPointerException("pasteStop");
		if(pasteStop.isEmpty())
			throw new IllegalArgumentException("pasteStop");

		if(pasteKey==null)
			throw new NullPointerException("pasteKey");
		if(pasteValue==null)
			throw new NullPointerException("pasteValue");

		this.raw = raw;
		this.supportedContentType = supportedContentType;
		this.encoding = encoding;
		this.pasteStart = pasteStart;
		this.pasteStop = pasteStop;
		this.pasteKey = pasteKey;
		this.pasteValue = pasteValue;
		addSource(raw, "Raw", preventUrlGuessingProxy);
	}

	@Wrap(order=10, thrown=@Wrap.Thrown(IOException.class))
	public final void setRaw(
			final Item item,
			@Parameter("raw") final Media.Value raw )
	throws IOException
	{
		this.raw.set( item, raw );
	}

	@Wrap(order=20)
	public final Paste addPaste(
			final Item item,
			@Parameter("key") final String key,
			@Parameter("value") final Media.Value value)
	{
		final Mount mount = mount();
		return mount.pasteType.newItem(
				this.pasteKey.map(key),
				this.pasteValue.map(value),
				Cope.mapAndCast(mount.pasteParent, item));
	}

	@Wrap(order=30, thrown=@Wrap.Thrown(IOException.class))
	public final void modifyPaste(
			final Item item,
			@Parameter("key") final String key,
			@Parameter("value") final Media.Value value )
	throws IOException
	{
		pasteValue.set(getPaste(item, key), value);
	}

	public final Locator getPasteLocator(final Item item, final String key)
	{
		return pasteValue.getLocator(getPaste(item, key));
	}

	public final String getPasteURL( final Item item, final String key )
	{
		return pasteValue.getURL(getPaste(item, key));
	}

	@Override
	public final List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(TextUrlFilter.class, this, super.getWrappers());
	}

	@Override
	protected final void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		final ItemField<? extends Item> pasteParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		final UniqueConstraint pasteParentAndKey = new UniqueConstraint(pasteParent, pasteKey);
		final Features features = new Features();
		features.put("parent", pasteParent);
		features.put("key", pasteKey);
		features.put("parentAndKey", pasteParentAndKey);
		features.put("value", pasteValue, preventUrlGuessingProxy);
		features.put("pastes", PartOf.create(pasteParent, pasteKey));
		final Type<Paste> pasteType = newSourceType(Paste.class, features);
		this.mount = new Mount(pasteParent, pasteType);
	}

	private static final class Mount
	{
		final ItemField<? extends Item> pasteParent;
		final Type<Paste> pasteType;

		Mount(
				final ItemField<? extends Item> pasteParent,
				final Type<Paste> pasteType)
		{
			assert pasteParent!=null;
			assert pasteType!=null;

			this.pasteParent = pasteParent;
			this.pasteType = pasteType;
		}
	}

	private final Mount mount()
	{
		final Mount mount = this.mount;
		if(mount==null)
			throw new IllegalStateException("feature not mounted");
		return mount;
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
	public final Log doGetIfModified(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
	throws IOException
	{
		final String sourceContentType = raw.getContentType(item);
		if(sourceContentType==null || !supportedContentType.equals(sourceContentType))
			return isNull;

		final byte[] sourceByte = raw.getBody().getArray(item);
		final String srcString = new String(sourceByte, encoding);

		final int pasteStartLen = pasteStart.length();
		final int pasteStopLen  = pasteStop .length();
		final StringBuilder bf = new StringBuilder(srcString.length());
		int nextStart = 0;
		for(int start = srcString.indexOf(pasteStart); start>=0; start = srcString.indexOf(pasteStart, nextStart))
		{
			final int stop = srcString.indexOf(pasteStop, start);
			if(stop<0)
				throw new IllegalArgumentException(pasteStart + ':' + start + '/' + pasteStop);

			bf.append(srcString.substring(nextStart, start));
			appendURL(bf, getPaste(item, srcString.substring(start + pasteStartLen, stop)), request);

			nextStart = stop + pasteStopLen;
		}

		final byte[] body;
		if(nextStart>0)
		{
			bf.append(srcString.substring(nextStart));
			body = bf.toString().getBytes(encoding);
		}
		else
		{
			// short cut if there are no pastes at all
			body = sourceByte;
		}

		response.setContentLength(body.length);
		response.setContentType(supportedContentType);

		final ServletOutputStream out = response.getOutputStream();
		try
		{
			out.write(body);
			return delivered;
		}
		finally
		{
			out.close();
		}
	}

	private final Paste getPaste(final Item item, final String key)
	{
		final Mount mount = mount();
		return mount.pasteType.searchSingletonStrict(Cope.and(
				Cope.equalAndCast(mount.pasteParent, item),
				pasteKey.equal(key)
		));
	}

	protected void appendURL(
			final StringBuilder bf,
			final Paste paste,
			final HttpServletRequest request)
	{
		bf.append(request.getContextPath());
		bf.append(request.getServletPath());
		bf.append('/');
		pasteValue.getLocator(paste).appendPath(bf);
	}

	@Override
	public final Set<String> getSupportedSourceContentTypes()
	{
		return Collections.singleton(supportedContentType);
	}

	@Computed
	protected static final class Paste extends Item
	{
		private static final long serialVersionUID = 1l;

		private Paste(final ActivationParameters ap)
		{
			super(ap);
		}

		public MediaPath.Locator getLocator()
		{
			return getPattern().pasteValue.getLocator(this);
		}

		public String getURL()
		{
			return getPattern().pasteValue.getURL(this);
		}

		private TextUrlFilter getPattern()
		{
			return (TextUrlFilter)getCopeType().getPattern();
		}
	}

	private final class PreventUrlGuessingProxy implements AnnotatedElement
	{
		PreventUrlGuessingProxy()
		{
			// just to make non-private
		}

		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			return
				(PreventUrlGuessing.class==annotationClass)
				? TextUrlFilter.this.isAnnotationPresent(annotationClass)
				: false;
		}

		public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
		{
			return
				(PreventUrlGuessing.class==annotationClass)
				? TextUrlFilter.this.getAnnotation(annotationClass)
				: null;
		}

		public Annotation[] getAnnotations()
		{
			throw new RuntimeException(TextUrlFilter.this.toString());
		}

		public Annotation[] getDeclaredAnnotations()
		{
			throw new RuntimeException(TextUrlFilter.this.toString());
		}

		@Override
		public String toString()
		{
			return TextUrlFilter.this.toString() + "-preventUrlGuessingAnnotations";
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getPasteURL(Item,String)} instead
	 */
	@Deprecated
	public final String getPasteUrl( final Item item, final String key )
	{
		return getPasteURL(item, key);
	}
}
