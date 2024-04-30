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

import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.SetValue.map;
import static com.exedio.cope.util.Check.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.Computed;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TextUrlFilter extends MediaFilter implements TextUrlFilterCheckable
{
	private static final long serialVersionUID = 1l;

	private final Media raw;
	private final String supportedContentType;
	private final Charset charset;
	private final String pasteStart;
	private final String pasteStop;

	final StringField pasteKey;
	final Media pasteValue;
	private Mount mountIfMounted = null;

	public TextUrlFilter(
			final Media raw,
			final String supportedContentType,
			final Charset charset,
			final String pasteStart,
			final String pasteStop,
			final StringField pasteKey,
			final Media pasteValue)
	{
		super(raw);

		//noinspection ThisEscapedInObjectConstruction
		this.raw = addSourceFeature(raw, "Raw", new MediaPathFeatureAnnotationProxy(this, false));
		this.supportedContentType = requireNonEmpty(supportedContentType, "supportedContentType");
		this.charset    = requireNonNull (charset,    "charset");
		this.pasteStart = requireNonEmpty(pasteStart, "pasteStart");
		this.pasteStop  = requireNonEmpty(pasteStop,  "pasteStop");
		this.pasteKey   = requireNonNull (pasteKey,   "pasteKey");
		this.pasteValue = requireNonNull (pasteValue, "pasteValue");

		if(!pasteKey.isMandatory())
			throw new IllegalArgumentException("pasteKey must be mandatory");
		if(pasteKey.hasDefault())
			throw new IllegalArgumentException("pasteKey must not have any default");
		if(pasteKey.getImplicitUniqueConstraint()!=null)
			throw new IllegalArgumentException("pasteKey must not be unique");

		if(pasteValue.isFinal())
			throw new IllegalArgumentException("pasteValue must not be final");
		if(!pasteValue.isMandatory())
			throw new IllegalArgumentException("pasteValue must be mandatory");
	}

	Type<Paste> getPasteType()
	{
		return mount().pasteType;
	}

	@Wrap(order=10, thrown=@Wrap.Thrown(IOException.class))
	public final void setRaw(
			@Nonnull final Item item,
			@Parameter(value="raw", nullability=NullableIfSourceOptional.class) final Media.Value raw )
	throws IOException
	{
		this.raw.set( item, raw );
	}

	@Wrap(order=20)
	@Nonnull
	public final Paste addPaste(
			@Nonnull final Item item,
			@Nonnull @Parameter("key") final String key,
			@Nonnull @Parameter("value") final Media.Value value)
	{
		final Mount mount = mount();
		return mount.pasteType.newItem(
				map(pasteKey, key),
				map(pasteValue, value),
				Cope.mapAndCast(mount.pasteParent, item));
	}

	@Wrap(order=30, thrown=@Wrap.Thrown(IOException.class))
	public final void modifyPaste(
			@Nonnull final Item item,
			@Nonnull @Parameter("key") final String key,
			@Nonnull @Parameter("value") final Media.Value value )
	throws IOException
	{
		pasteValue.set(getPaste(item, key), value);
	}

	@Wrap(order=50, thrown=@Wrap.Thrown(IOException.class))
	@Nonnull
	public final Paste putPaste(
			@Nonnull final Item item,
			@Nonnull @Parameter("key") final String key,
			@Nonnull @Parameter("value") final Media.Value value)
	throws IOException
	{
		final Mount mount = mount();
		final Paste existing =
			mount.pasteType.searchSingleton(Cope.and(
				Cope.equalAndCast(mount.pasteParent, item),
				pasteKey.equal(key)));

		if(existing==null)
			return mount.pasteType.newItem(
					map(pasteKey, key),
					map(pasteValue, value),
					Cope.mapAndCast(mount.pasteParent, item));
		else
		{
			pasteValue.set(existing, value);
			return existing;
		}
	}

	public final Locator getPasteLocator(final Item item, final String key)
	{
		return pasteValue.getLocator(getPaste(item, key));
	}

	public final String getPasteURL(final Item item, final String key)
	{
		return pasteValue.getURL(getPaste(item, key));
	}

	@Override
	protected final void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		final ItemField<?> pasteParent = type.newItemField(CASCADE).toFinal();
		final UniqueConstraint pasteParentAndKey = UniqueConstraint.create(pasteParent, pasteKey);
		final Features features = new Features();
		features.put("parent", pasteParent);
		features.put("key", pasteKey);
		features.put("parentAndKey", pasteParentAndKey);
		features.put("value", pasteValue, new MediaPathFeatureOfTypeAnnotationProxy(this));
		features.put("pastes", PartOf.create(pasteParent, pasteKey));
		final Type<Paste> pasteType = newSourceType(Paste.class, Paste::new, features);
		this.mountIfMounted = new Mount(pasteParent, pasteType);
	}

	private static final class Mount
	{
		final ItemField<?> pasteParent;
		final Type<Paste> pasteType;

		Mount(
				final ItemField<?> pasteParent,
				final Type<Paste> pasteType)
		{
			assert pasteParent!=null;
			assert pasteType!=null;

			this.pasteParent = pasteParent;
			this.pasteType = pasteType;
		}
	}

	private Mount mount()
	{
		return requireMounted(mountIfMounted);
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
		//noinspection DataFlowIssue OK: is checked before (contentType==null)
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

	@Wrap(order=80, thrown=@Wrap.Thrown(NotFound.class))
	@Nonnull
	public final String getContent(
			@Nonnull final Item item,
			@Nonnull @Parameter("request") final HttpServletRequest request )
		throws NotFound
	{
		checkContentType( item );

		final byte[] sourceByte = raw.getBody().getArray(item);
		//noinspection DataFlowIssue OK: is checked before (contentType==null)
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
			throw notFoundIsNull();
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
				bf.append(srcString, nextStart, start);
				appendKey(bf, item, key, request);
			}
			if(brokenCodes!=null)
			{
				try
				{
					getPaste(item, key);
				}
				catch(final IllegalArgumentException ignored)
				{
					brokenCodes.add(key);
				}
			}

			nextStart = stop + pasteStopLen;
		}
		return nextStart;
	}

	@Override
	@Wrap(order=90, thrown=@Wrap.Thrown(NotFound.class))
	@Nonnull
	public Set<String> check( @Nonnull final Item item ) throws NotFound
	{
		checkContentType( item );
		final Set<String> brokenCodes = new HashSet<>();
		final byte[] sourceByte = getSource().getBody().getArray( item );
		//noinspection DataFlowIssue OK: is checked before (contentType==null)
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
		appendURL(bf, getPaste(item, key), request);
	}

	final Paste getPaste(final Item item, final String key)
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
		//noinspection DataFlowIssue OK: pasteValue is mandatory
		pasteValue.getLocator(paste).appendPath(bf);
	}

	@Override
	public final Set<String> getSupportedSourceContentTypes()
	{
		return Set.of(supportedContentType);
	}

	public final List<String> getPasteContentTypesAllowed()
	{
		return pasteValue.getContentTypesAllowed();
	}

	@Computed
	public static final class Paste extends Item
	{
		private static final long serialVersionUID = 1l;

		private Paste(final ActivationParameters ap)
		{
			super(ap);
		}

		String getKey()
		{
			return getPattern().pasteKey.get(this);
		}

		public MediaPath.Locator getLocator()
		{
			return getPattern().pasteValue.getLocator(this);
		}

		public String getURL()
		{
			return getPattern().pasteValue.getURL(this);
		}

		String getContentType()
		{
			return getPattern().pasteValue.getContentType(this);
		}

		byte[] getBody()
		{
			return getPattern().pasteValue.getBody(this);
		}

		private TextUrlFilter getPattern()
		{
			return (TextUrlFilter)getCopeType().getPattern();
		}
	}

	@Wrap(order=100, thrown=@Wrap.Thrown(IOException.class))
	public final void putPastesFromZip(
			@Nonnull final Item item,
			@Nonnull @Parameter("file") final File file)
		throws IOException
	{
		try(ZipFile zipFile = new ZipFile(file))
		{
			for(final Enumeration<? extends ZipEntry> entries = zipFile.entries();
					entries.hasMoreElements(); )
			{
				final ZipEntry entry = entries.nextElement();
				final String name = entry.getName();
				try
				{
					final MediaType contentType = MediaType.forFileName(name);
					if(contentType==null)
						throw new IllegalArgumentException("unknown content type for entry " + name);

					putPaste(item, name, Media.toValue(DataField.toValue(zipFile, entry), contentType.getName()));
				}
				catch(final ConstraintViolationException e)
				{
					throw new IllegalArgumentException(name, e);
				}
			}
		}
	}
}
