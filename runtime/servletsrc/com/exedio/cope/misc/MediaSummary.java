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

package com.exedio.cope.misc;

import com.exedio.cope.pattern.MediaInfo;

public final class MediaSummary
{
	private final int redirectFrom;
	private final int exception;
	private final int invalidSpecial;
	private final int guessedUrl;
	private final int notAnItem;
	private final int noSuchItem;
	private final int moved;
	private final int isNull;
	private final int notModified;
	private final int delivered;

	public MediaSummary(final MediaInfo[] infos)
	{
		int redirectFrom = 0;
		int exception = 0;
		int invalidSpecial = 0;
		int guessedUrl = 0;
		int notAnItem = 0;
		int noSuchItem = 0;
		int moved = 0;
		int isNull = 0;
		int notModified = 0;
		int delivered = 0;

		for(final MediaInfo info : infos)
		{
			redirectFrom  += info.getRedirectFrom();
			exception     += info.getException();
			invalidSpecial+= info.getInvalidSpecial();
			guessedUrl    += info.getGuessedUrl();
			notAnItem     += info.getNotAnItem();
			noSuchItem    += info.getNoSuchItem();
			moved         += info.getMoved();
			isNull        += info.getIsNull();
			notModified   += info.getNotModified();
			delivered     += info.getDelivered();
		}

		this.redirectFrom = redirectFrom;
		this.exception = exception;
		this.invalidSpecial = invalidSpecial;
		this.guessedUrl = guessedUrl;
		this.notAnItem = notAnItem;
		this.noSuchItem = noSuchItem;
		this.moved = moved;
		this.isNull = isNull;
		this.notModified = notModified;
		this.delivered = delivered;
	}

	public int getRedirectFrom()
	{
		return redirectFrom;
	}

	public int getException()
	{
		return exception;
	}

	public int getInvalidSpecial()
	{
		return invalidSpecial;
	}

	public int getGuessedUrl()
	{
		return guessedUrl;
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
		return 0;
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
