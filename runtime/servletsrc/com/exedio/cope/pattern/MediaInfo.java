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

public final class MediaInfo
{
	private final MediaPath path;
	
	private final int redirectFrom;
	private final int exception;
	private final int notAnItem;
	private final int noSuchItem;
	private final int moved;
	private final int isNull;
	private final int notComputable;
	private final int notModified;
	private final int delivered;
	
	MediaInfo(
			final MediaPath path,
			final int redirectFrom,
			final int exception,
			final int notAnItem,
			final int noSuchItem,
			final int moved,
			final int isNull,
			final int notComputable,
			final int notModified,
			final int delivered)
	{
		this.path = path;
		this.redirectFrom = redirectFrom;
		this.exception = exception;
		this.notAnItem = notAnItem;
		this.noSuchItem = noSuchItem;
		this.moved = moved;
		this.isNull = isNull;
		this.notComputable = notComputable;
		this.notModified = notModified;
		this.delivered = delivered;
	}

	public MediaPath getPath()
	{
		return path;
	}

	public int getRedirectFrom()
	{
		return redirectFrom;
	}

	public int getException()
	{
		return exception;
	}

	public int getNotAnItem()
	{
		return notAnItem;
	}

	public int getNoSuchItem()
	{
		return noSuchItem;
	}

	public int getMoved()
	{
		return moved;
	}

	public int getIsNull()
	{
		return isNull;
	}

	public int getNotComputable()
	{
		return notComputable;
	}

	public int getNotModified()
	{
		return notModified;
	}

	public int getDelivered()
	{
		return delivered;
	}
}
