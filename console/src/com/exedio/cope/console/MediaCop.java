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

package com.exedio.cope.console;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.pattern.MediaThumbnail;

final class MediaCop extends ConsoleCop
{

	private static final String MEDIA = "m";
	private static final String INLINE = "il";
	
	final Media media;
	final MediaThumbnail thumbnail;
	final boolean inline;

	MediaCop(final MediaPath mediaOrThumbnail, final boolean inline)
	{
		super("media - " + mediaOrThumbnail.getID());
		
		if(mediaOrThumbnail instanceof Media)
		{
			this.media = (Media)mediaOrThumbnail;
			this.thumbnail = null;
		}
		else if(mediaOrThumbnail instanceof MediaThumbnail)
		{
			this.media = ((MediaThumbnail)mediaOrThumbnail).getMedia();
			this.thumbnail = (MediaThumbnail)mediaOrThumbnail;
		}
		else
			throw new RuntimeException(mediaOrThumbnail.toString());

		this.inline = inline;

		addParameter(MEDIA, mediaOrThumbnail.getID());
		if(inline)
			addParameter(INLINE, "t");
	}
	
	private MediaCop(final Media media, final MediaThumbnail thumbnail, final boolean inline)
	{
		this(thumbnail!=null ? thumbnail : media, inline);
	}
	
	static MediaCop getMediaCop(final Model model, final HttpServletRequest request)
	{
		final String mediaID = request.getParameter(MEDIA);
		return (mediaID==null) ? null : new MediaCop((MediaPath)model.findFeatureByID(mediaID), request.getParameter(INLINE)!=null);
	}
	
	MediaCop toggleInline()
	{
		return new MediaCop(media, thumbnail, !inline);
	}

	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
	{
		try
		{
			model.startTransaction(getClass().getName());
			final Query<? extends Item> q = media.getType().newQuery(media.getIsNull().isNotNull());
			q.setLimit(0, 50);
			final List<? extends Item> items = q.search();
			Console_Jspm.writeBody(this, out, items);
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}
	
}
