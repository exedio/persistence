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

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.imageio.IIOImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class MediaImageioFilter extends MediaFilter
{
	private static final long serialVersionUID = 1l;

	private final Media source;
	private final HashMap<String, ImageReaderSpi> imageReaderSpi;
	private final ImageWriterSpi imageWriterSpi;

	private static final String outputContentType = "image/jpeg";

	protected MediaImageioFilter(final Media source)
	{
		super(source);
		this.source = source;

		final IIORegistry registry = IIORegistry.getDefaultInstance();
		final HashMap<String, ImageReaderSpi> imageReaderSpi = new HashMap<>();
		for(final Iterator<ImageReaderSpi> spiIt = registry.getServiceProviders(ImageReaderSpi.class, true); spiIt.hasNext(); )
		{
			final ImageReaderSpi spi = spiIt.next();
			for(final String spiMimeType : spi.getMIMETypes())
				imageReaderSpi.putIfAbsent(spiMimeType, spi); // first wins
		}

		// fix for MSIE behaviour
		final ImageReaderSpi jpegSpi = imageReaderSpi.get("image/jpeg");
		if(jpegSpi!=null && !imageReaderSpi.containsKey("image/pjpeg"))
			imageReaderSpi.put("image/pjpeg", jpegSpi);

		this.imageReaderSpi = imageReaderSpi;

		ImageWriterSpi imageWriterSpi = null;
		spiLoop:
		for(final Iterator<ImageWriterSpi> spiIt = registry.getServiceProviders(ImageWriterSpi.class, true); spiIt.hasNext(); )
		{
			final ImageWriterSpi spi = spiIt.next();
			for(final String spiMimeType : spi.getMIMETypes())
			{
				if(outputContentType.equals(spiMimeType)) // first wins
				{
					imageWriterSpi = spi;
					break spiLoop;
				}
			}
		}
		if(imageWriterSpi==null)
			throw new RuntimeException("no jpeg encoder found");

		this.imageWriterSpi = imageWriterSpi;
	}

	@Override
	public final Set<String> getSupportedSourceContentTypes()
	{
		return Collections.unmodifiableSet(imageReaderSpi.keySet());
	}

	@Override
	public final String getContentType(final Item item)
	{
		final String contentType = source.getContentType(item);

		return (contentType!=null && imageReaderSpi.containsKey(contentType)) ? outputContentType : null;
	}

	@Override
	public final boolean isContentTypeWrapped()
	{
		return false; // since there is only one outputContentType
	}

	public abstract BufferedImage filter(BufferedImage in);

	@Override
	public final void doGetAndCommit(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
	throws IOException, NotFound
	{
		final String contentType = source.getContentType(item);
		if(contentType==null)
			throw notFoundIsNull();
		final ImageReaderSpi spi = imageReaderSpi.get(contentType);
		if(spi==null)
			throw notFoundNotComputable();

		final ByteArrayOutputStream body = execute(item, spi, true);

		MediaUtil.send(outputContentType, body, response);
	}

	@SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
	@Wrap(order=10, doc="Returns the body of {0}.", thrown=@Wrap.Thrown(IOException.class), nullability=NullableIfMediaPathOptional.class)
	public final byte[] get(@Nonnull final Item item) throws IOException
	{
		final String contentType = source.getContentType(item);
		if(contentType==null)
			return null;
		final ImageReaderSpi spi = imageReaderSpi.get(contentType);
		if(spi==null)
			return null;

		return execute(item, spi, false).toByteArray();
	}

	private ByteArrayOutputStream execute(
			final Item item,
			final ImageReaderSpi spi,
			final boolean commit)
	throws IOException
	{
		final byte[] srcBytes = source.getBody().getArray(item);

		if(commit)
			commit();

		final BufferedImage srcBuf;

		{
			//noinspection ConstantConditions OK: is checked before (contentType==null)
			try(MemoryCacheImageInputStream input = new MemoryCacheImageInputStream(new ByteArrayInputStream(srcBytes)))
			{
				final ImageReader imageReader = spi.createReaderInstance();
				try
				{
					imageReader.setInput(input, true, true);
					srcBuf = imageReader.read(0);
				}
				finally
				{
					imageReader.dispose();
				}
			}
		}
		//System.out.println("----------"+item+'/'+srcBuf.getWidth()+'/'+srcBuf.getHeight()+"-----"+srcBuf.getColorModel());

		final BufferedImage filteredBuf = filter(srcBuf);

		final JPEGImageWriteParam imageWriteParam = getImageWriteParam();
		final IIOImage iioImage = new IIOImage(filteredBuf, null, null);

		// Dont let ImageWriter write directly to ServletOutputStream,
		// causes spurious hanging requests.
		final ByteArrayOutputStream body = new ByteArrayOutputStream();
		{
			try(MemoryCacheImageOutputStream output = new MemoryCacheImageOutputStream(body))
			{
				final ImageWriter imageWriter = imageWriterSpi.createWriterInstance();
				try
				{
					imageWriter.setOutput(output);
					imageWriter.write(null, iioImage, imageWriteParam);
				}
				finally
				{
					imageWriter.dispose();
				}
			}
		}
		return body;
	}

	public JPEGImageWriteParam getImageWriteParam()
	{
		final JPEGImageWriteParam result = new JPEGImageWriteParam(Locale.getDefault());
		result.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		result.setCompressionQuality(0.85f);
		return result;
	}
}
