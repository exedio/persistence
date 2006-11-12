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

public final class MediaThumbnail extends MediaImageFilter
{
	private final int boundX;
	private final int boundY;
	
	private static final int MIN_BOUND = 5;
	
	public MediaThumbnail(final Media media, final int boundX, final int boundY)
	{
		super(media);
		this.boundX = boundX;
		this.boundY = boundY;
		
		if(boundX<MIN_BOUND)
			throw new IllegalArgumentException("boundX must be " + MIN_BOUND + " or greater, but was " + boundX);
		if(boundY<MIN_BOUND)
			throw new IllegalArgumentException("boundX must be " + MIN_BOUND + " or greater, but was " + boundY);
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
	public BufferedImage filter(final BufferedImage in)
	{
		final int srcX = in.getWidth();
		final int srcY = in.getHeight();
		final int[] tgtDim = boundingBox(srcX, srcY);

		final int tgtX = tgtDim[0];
		final int tgtY = tgtDim[1];
		final double scaleX = ((double)tgtX) / ((double)srcX);
		final double scaleY = ((double)tgtY) / ((double)srcY);
		
		final AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(scaleX, scaleY), AffineTransformOp.TYPE_BILINEAR);
		final BufferedImage result = new BufferedImage(tgtX, tgtY, BufferedImage.TYPE_INT_RGB);
		op.filter(in, result);
		
		return result;
	}
	
	int[] boundingBox(final int x, final int y)
	{
		final int boundX = this.boundX;
		final int boundY = this.boundY;
		
		final int resultY = (boundX * y) / x;
		if(resultY<=boundY)
			return new int[]{boundX, resultY};

		final int resultX = (boundY * x) / y;
		assert resultX<=boundX;
		return new int[]{resultX, boundY};
	}
}
