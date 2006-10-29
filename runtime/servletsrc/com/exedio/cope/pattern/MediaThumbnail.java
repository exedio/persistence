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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Item;
import com.sun.image.codec.jpeg.JPEGCodec;

public final class MediaThumbnail extends CachedMedia
{
	private final Media media;
	private final int boundX;
	private final int boundY;
	
	private static final int MIN_BOUND = 5;
	private static final HashSet<String> inputContentTypes = new HashSet<String>(Arrays.asList("image/jpeg", "image/png", "image/gif"));
	private static final String outputContentType = "image/jpeg";
	
	public MediaThumbnail(final Media media, final int boundX, final int boundY)
	{
		this.media = media;
		this.boundX = boundX;
		this.boundY = boundY;
		
		if(media==null)
			throw new NullPointerException("media must not be null");
		if(boundX<MIN_BOUND)
			throw new IllegalArgumentException("boundX must be " + MIN_BOUND + " or greater, but was " + boundX);
		if(boundY<MIN_BOUND)
			throw new IllegalArgumentException("boundX must be " + MIN_BOUND + " or greater, but was " + boundY);
	}
	
	public Media getMedia()
	{
		return media;
	}
	
	public int getBoundX()
	{
		return boundX;
	}
	
	public int getBoundY()
	{
		return boundY;
	}

	@Override
	public String getContentType(final Item item)
	{
		final String contentType = media.getContentType(item);

		return (contentType!=null && inputContentTypes.contains(contentType)) ? outputContentType : null;
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
		if(!inputContentTypes.contains(contentType))
			return notComputable;
		
		final byte[] srcBytes = media.getBody().get(item);
		final BufferedImage srcBuf;
		if("image/jpeg".equals(contentType)) // TODO don't know why this is needed
			srcBuf = JPEGCodec.createJPEGDecoder(new ByteArrayInputStream(srcBytes)).decodeAsBufferedImage();
		else
			srcBuf = ImageIO.read(new ByteArrayInputStream(srcBytes));
		
		final int srcX = srcBuf.getWidth();
		final int srcY = srcBuf.getHeight();
		final int[] tgtDim = boundingBox(srcX, srcY);

		final int tgtX = tgtDim[0];
		final int tgtY = tgtDim[1];
		final double scaleX = ((double)tgtX) / ((double)srcX);
		final double scaleY = ((double)tgtY) / ((double)srcY);
		
		final AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(scaleX, scaleY), AffineTransformOp.TYPE_BILINEAR);
		final BufferedImage scaledBuf = new BufferedImage(tgtX, tgtY, BufferedImage.TYPE_INT_RGB);
		op.filter(srcBuf, scaledBuf);
		
		ImageIO.setUseCache(false); // otherwise many small files are created and not deleted in tomcat/temp
      final ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
      final JPEGImageWriteParam imageWriteParam = new JPEGImageWriteParam(Locale.getDefault());
      imageWriteParam.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
      imageWriteParam.setCompressionQuality(0.75f);
      final IIOImage iioImage = new IIOImage(scaledBuf, null, null);

		response.setContentType(outputContentType);
		ServletOutputStream out = null;
		try
		{
			out = response.getOutputStream();
	      imageWriter.setOutput(ImageIO.createImageOutputStream(out));
	      imageWriter.write(null, iioImage, imageWriteParam);
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
		final int boundX = this.boundX;
		final int boundY = this.boundY;
		
		final int resultY = (boundX * y) / x;
		if(resultY<=boundY)
			return new int[]{boundX, resultY};

		final int resultX = (boundY * x) / y;
		return new int[]{resultX, boundY};
	}
}
