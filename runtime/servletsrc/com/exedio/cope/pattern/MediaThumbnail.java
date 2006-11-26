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
import java.awt.image.DirectColorModel;

public final class MediaThumbnail extends MediaImageioFilter
{
	private final int boundX;
	private final int boundY;
	
	private static final int MIN_BOUND = 5;
	
	public MediaThumbnail(final Media source, final int boundX, final int boundY)
	{
		super(source);
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
	public BufferedImage filter(BufferedImage in)
	{
		// dont know why this is needed,
		// without jpeg cannot be scaled below
		// and palette images get a nasty black bar
		// on the right side
		if(!(in.getColorModel() instanceof DirectColorModel))
		{
			final AffineTransformOp rgbOp = new AffineTransformOp(new AffineTransform(), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			final BufferedImage inDirect = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
			rgbOp.filter(in, inDirect);
			in = inDirect;
			assert in.getColorModel() instanceof DirectColorModel;
		}

		final int inX = in.getWidth();
		final int inY = in.getHeight();
		final int[] resultDim = boundingBox(inX, inY);

		final int resultX = resultDim[0];
		final int resultY = resultDim[1];
		final double scaleX = ((double)resultX) / ((double)inX);
		final double scaleY = ((double)resultY) / ((double)inY);
		
		final AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(scaleX, scaleY), AffineTransformOp.TYPE_BILINEAR);
		final BufferedImage result = new BufferedImage(resultX, resultY, BufferedImage.TYPE_INT_RGB);
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
