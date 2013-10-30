/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;

public final class MediaImageMagickThumbnail extends MediaImageMagickFilter
{
	private static final long serialVersionUID = 1l;

	private final int boundX;
	private final int boundY;
	private final int density;
	private final String flattenColor;

	private static final int MIN_BOUND = 5;

	public MediaImageMagickThumbnail(final Media source, final int boundX, final int boundY)
	{
		this(source, boundX, boundY, 0, null, "image/jpeg");
	}

	private static String[] options(
			final int boundX, final int boundY,
			final int density,
			final String flattenColor)
	{
		final ArrayList<String> result = new ArrayList<String>(5);
		result.add("-resize");
		result.add(String.valueOf(boundX) + 'x' + String.valueOf(boundY) + '>');
		if(density>0)
		{
			result.add("-density");
			result.add(String.valueOf(density));
			result.add("-units");
			result.add("PixelsPerInch");
		}
		if(flattenColor!=null)
		{
			result.add("-flatten");
			result.add("-background");
			result.add(flattenColor);
		}
		return result.toArray(new String[result.size()]);
	}

	private MediaImageMagickThumbnail(
			final Media source,
			final int boundX, final int boundY,
			final int density,
			final String flattenColor,
			final String outputContentType)
	{
		super(
				source,
				new MediaThumbnail(source, boundX, boundY),
				outputContentType,
				options(boundX, boundY, density, flattenColor));
		this.boundX = boundX;
		this.boundY = boundY;
		this.density = density;
		this.flattenColor = flattenColor;

		if(boundX<MIN_BOUND)
			throw new IllegalArgumentException("boundX must be " + MIN_BOUND + " or greater, but was " + boundX);
		if(boundY<MIN_BOUND)
			throw new IllegalArgumentException("boundY must be " + MIN_BOUND + " or greater, but was " + boundY);
		if(density<0)
			throw new IllegalArgumentException("density must be 0 or greater, but was " + density);
	}

	public MediaImageMagickThumbnail outputContentType(final String contentType)
	{
		if(contentType==null)
			throw new NullPointerException("outputContentType");
		return new MediaImageMagickThumbnail(getSource(), this.boundX, this.boundY, this.density, this.flattenColor, contentType);
	}

	public MediaImageMagickThumbnail outputContentTypeSame()
	{
		return new MediaImageMagickThumbnail(getSource(), this.boundX, this.boundY, this.density, this.flattenColor, null);
	}

	public MediaImageMagickThumbnail density(final int density)
	{
		return new MediaImageMagickThumbnail(getSource(), this.boundX, this.boundY, density, this.flattenColor, this.getOutputContentType());
	}

	public MediaImageMagickThumbnail flatten(final String color)
	{
		return new MediaImageMagickThumbnail(getSource(), this.boundX, this.boundY, this.density, color, this.getOutputContentType());
	}

	public int getBoundX()
	{
		return boundX;
	}

	public int getBoundY()
	{
		return boundY;
	}
}
