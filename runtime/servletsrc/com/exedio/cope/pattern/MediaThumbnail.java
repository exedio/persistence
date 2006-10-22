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

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Item;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public final class MediaThumbnail extends CachedMedia
{
	private final Media media;
	private final int boundX;
	private final int boundY;
	
	public MediaThumbnail(final Media media, final int boundX, final int boundY)
	{
		this.media = media;
		this.boundX = boundX;
		this.boundY = boundY;
		
		if(media==null)
			throw new NullPointerException("media must not be null");
		if(boundX<5)
			throw new IllegalArgumentException("boundX must be 5 or greater, but was " + boundX);
		if(boundY<5)
			throw new IllegalArgumentException("boundX must be 5 or greater, but was " + boundY);
	}
	
	public Media getMedia()
	{
		return media;
	}
	
	/**
	 * Returns a URL the content of this thumbnail is available under,
	 * if a {@link MediaServlet} is properly installed.
	 * Returns null, if this thumbnail is null.
	 */
	public String getURL(final Item item)
	{
		final String contentType = media.getContentType(item);

		if(contentType==null)
			return null;

		final StringBuffer bf = new StringBuffer(getMediaRootUrl());

		bf.append(getUrlPath()).
			append(item.getCopeID()).
			append(".jpg");

		return bf.toString();
	}

	@Override
	public long getLastModified(final Item item)
	{
		return media.getLastModified(item);
	}
	
	@Override
	public Media.Log doGetIfModified(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item,
			final String extension)
	throws ServletException, IOException
	{
		final String contentType = media.getContentType(item);
		if(!"image/jpeg".equals(contentType))
			return media.doGetIfModified(request, response, item, extension);
		
		final byte[] srcBytes = media.getBody().get(item);
		final BufferedImage srcBuf = JPEGCodec.createJPEGDecoder(new ByteArrayInputStream(srcBytes)).decodeAsBufferedImage();
		
		final int srcX = srcBuf.getWidth();
		final int srcY = srcBuf.getHeight();
		final int[] tgtDim = boundingBox(srcX, srcY);
		if(tgtDim==null)
			return media.doGetIfModified(request, response, item, extension);

		final int tgtX = tgtDim[0];
		final int tgtY = tgtDim[1];
		final double scaleX = ((double)tgtX) / ((double)srcX);
		final double scaleY = ((double)tgtY) / ((double)srcY);
		
		final AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(scaleX, scaleY), AffineTransformOp.TYPE_BILINEAR);
		final BufferedImage scaledBuf = new BufferedImage(tgtX, tgtY, BufferedImage.TYPE_INT_RGB);
		op.filter(srcBuf, scaledBuf);

		response.setContentType("image/jpeg");
		ServletOutputStream out = null;
		try
		{
			out = response.getOutputStream();
			final JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			final JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(scaledBuf);
			param.setQuality(0.75f, false);
			encoder.encode(scaledBuf);
			return delivered;
		}
		finally
		{
			if(out!=null)
				out.close();
		}
	}
	
	int[] boundingBox(final int x, final int y)
	{
		if(x<=boundX && y<=boundY)
			return null;
		
		final int tgtHeight = (boundX * y) / x;
		if(tgtHeight<=boundY)
			return new int[]{boundX, tgtHeight};

		final int tgtWidth = (boundY * x) / y;
		return new int[]{tgtWidth, boundY};
	}
}
