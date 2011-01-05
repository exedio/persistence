/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import java.util.ArrayList;
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

	private ItemField<? extends Item> pasteParent = null;
	private final StringField pasteKey;
	private UniqueConstraint pasteParentAndKey = null;
	private final Media pasteValue;
	private Type<Paste> pasteType = null;

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
		if(pasteStart.length()==0)
			throw new IllegalArgumentException("pasteStart");
		if(pasteStop==null)
			throw new NullPointerException("pasteStop");
		if(pasteStop.length()==0)
			throw new IllegalArgumentException("pasteStop");
		if(pasteStop.length()>1)
			throw new RuntimeException("not yet implemented, is buggy " + pasteStop);

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
		addSource( raw, "Raw" );
	}

	public final void setRaw( final Item item, final Media.Value raw ) throws IOException
	{
		this.raw.set( item, raw );
	}

	public final void addPaste(final Item item, final String key, final Media.Value value)
	{
		pasteType.newItem(
				this.pasteKey.map(key),
				this.pasteValue.map(value),
				Cope.mapAndCast(this.pasteParent, item));
	}

	public final void modifyPaste( final Item item, final String key, final Media.Value value ) throws IOException
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
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());

		result.add(
			new Wrapper( "setRaw" ).addParameter( Media.Value.class, "raw" ).
			addThrows( IOException.class ) );

		result.add(
				new Wrapper("addPaste").
				addParameter(String.class, "key").
				addParameter(Media.Value.class, "value"));

		result.add(
			new Wrapper( "modifyPaste" ).addParameter( String.class, "key" ).
			addParameter( Media.Value.class, "value" ).
			addThrows( IOException.class ) );

		return Collections.unmodifiableList(result);
	}

	@Override
	protected final void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		pasteParent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		pasteParentAndKey = new UniqueConstraint(pasteParent, pasteKey);
		final Features features = new Features();
		features.put("parent", pasteParent);
		features.put("key", pasteKey);
		features.put("parentAndKey", pasteParentAndKey);
		features.put("value", pasteValue);
		features.put("pastes", PartOf.newPartOf(pasteParent, pasteKey));
		this.pasteType = newSourceType(Paste.class, features);
	}

	@Override
	public final String getContentType(final Item item)
	{
		final String contentType = raw.getContentType(item);
		return supportedContentType.equals(contentType) ? contentType : null;
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

		String tempString = srcString;
		while(tempString.indexOf(pasteStart) > -1)
		{
			final int startPos = tempString.indexOf(pasteStart);
			final StringBuilder sb = new StringBuilder();
			sb.append(tempString.substring(0, startPos));
			String image = tempString.substring(startPos + pasteStart.length());
			image = image.substring(0, image.indexOf(pasteStop));
			final String rest = tempString.substring(startPos);
			sb.append(request.getContextPath());
			sb.append(request.getServletPath());
			sb.append('/');
			pasteValue.getLocator(getPaste(item, image)).appendPath(sb);
			sb.append(rest.substring(rest.indexOf(pasteStop) + 1));
			tempString = sb.toString();
		}

		response.setContentType(supportedContentType);

		final byte[] body = tempString.getBytes(encoding);
		response.setContentLength(body.length);

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
		return pasteType.searchSingletonStrict(Cope.and(
				Cope.equalAndCast(pasteParent, item),
				pasteKey.equal(key)
		));
	}

	@Override
	public final Set<String> getSupportedSourceContentTypes()
	{
		return Collections.singleton(supportedContentType);
	}

	@Computed
	private static final class Paste extends Item
	{
		private static final long serialVersionUID = 1l;

		private Paste(final ActivationParameters ap)
		{
			super(ap);
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
