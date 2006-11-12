/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.imageio.IIOImage;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Item;
import com.sun.image.codec.jpeg.JPEGCodec;

public abstract class MediaImageFilter extends CachedMedia
{
	private final Media media;
	private final HashMap<String, ImageReaderSpi> imageReaderSpi;
	private final ImageWriterSpi imageWriterSpi;

	private static final String outputContentType = "image/jpeg";

	public MediaImageFilter(final Media media)
	{
		this.media = media;
		if(media==null)
			throw new NullPointerException("media must not be null");
		
		final IIORegistry registry = IIORegistry.getDefaultInstance();
		final HashMap<String, ImageReaderSpi> imageReaderSpi = new HashMap<String, ImageReaderSpi>();
		for(final Iterator<ImageReaderSpi> spiIt = registry.getServiceProviders(ImageReaderSpi.class, true); spiIt.hasNext(); )
		{
      	final ImageReaderSpi spi = spiIt.next();
      	for(final String spiMimeType : spi.getMIMETypes())
      	{
      		if(!imageReaderSpi.containsKey(spiMimeType)) // first wins
      			imageReaderSpi.put(spiMimeType, spi);
      	}
		}
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

	public final Media getMedia()
	{
		return media;
	}

	public final Set<String> getSupportedMediaContentTypes()
	{
		return Collections.unmodifiableSet(imageReaderSpi.keySet());
	}

	@Override
	public final String getContentType(final Item item)
	{
		final String contentType = media.getContentType(item);

		return (contentType!=null && imageReaderSpi.containsKey(contentType)) ? outputContentType : null;
	}

	@Override
	public final long getLastModified(final Item item)
	{
		return media.getLastModified(item);
	}
	
	public abstract BufferedImage filter(BufferedImage in);
	
	@Override
	public final Media.Log doGetIfModified(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item,
			final String extension)
	throws ServletException, IOException
	{
		final String contentType = media.getContentType(item);
		if(contentType==null)
			return isNull;
		final ImageReaderSpi spi = imageReaderSpi.get(contentType);
		if(spi==null)
			return notComputable;
		
		final byte[] srcBytes = media.getBody().get(item);
		final BufferedImage srcBuf;
		if("image/jpeg".equals(contentType)) // TODO don't know why else branch does not work for jpeg
			srcBuf = JPEGCodec.createJPEGDecoder(new ByteArrayInputStream(srcBytes)).decodeAsBufferedImage();
		else
		{
			ImageReader reader = null;
			try
			{
				reader = spi.createReaderInstance();
				final ImageReadParam param = reader.getDefaultReadParam();
				reader.setInput(new MemoryCacheImageInputStream(new ByteArrayInputStream(srcBytes)), true, true);
				srcBuf = reader.read(0, param);
				reader.dispose();
			}
			finally
			{
				if(reader!=null)
					reader.dispose();
			}
		}
		
		final BufferedImage filteredBuf = filter(srcBuf);
		
		final JPEGImageWriteParam imageWriteParam = new JPEGImageWriteParam(Locale.getDefault());
		imageWriteParam.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
		imageWriteParam.setCompressionQuality(0.75f);
		final IIOImage iioImage = new IIOImage(filteredBuf, null, null);
		
		response.setContentType(outputContentType);
		ImageWriter imageWriter = null;
		ServletOutputStream out = null;
		try
		{
			imageWriter = imageWriterSpi.createWriterInstance();
			out = response.getOutputStream();
			imageWriter.setOutput(new MemoryCacheImageOutputStream(out));
			imageWriter.write(null, iioImage, imageWriteParam);
			return delivered;
		}
		finally
		{
			if(imageWriter!=null)
				imageWriter.dispose();
			if(out!=null)
				out.close();
		}
	}
}
