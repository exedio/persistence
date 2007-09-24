/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.io.PrintStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.pattern.MediaRedirect;

final class MediaCop extends ConsoleCop
{
	private static final String MEDIA = "m";
	private static final String INLINE = "il";
	private static final String MEDIA_INLINE = "m";
	private static final String OTHER_INLINE = "o";
	
	final MediaPath media;
	final Media other;
	final boolean mediaInline;
	final boolean otherInline;

	MediaCop(final MediaPath media, final boolean mediaInline, final boolean otherInline)
	{
		super("media", "media - " + media.getID());
		
		this.media = media;
		
		if(media instanceof Media)
			other = null;
		else if(media instanceof MediaFilter)
			other = ((MediaFilter)media).getSource();
		else if(media instanceof MediaRedirect)
			other = ((MediaRedirect)media).getTarget();
		else
			other = null;

		this.mediaInline = mediaInline;
		this.otherInline = otherInline;
		
		addParameter(MEDIA, media.getID());
		if(mediaInline)
			addParameter(INLINE, MEDIA_INLINE);
		if(otherInline)
			addParameter(INLINE, OTHER_INLINE);
	}
	
	static MediaCop getMediaCop(final Model model, final HttpServletRequest request)
	{
		final String mediaID = request.getParameter(MEDIA);
		if(mediaID==null)
			return null;

		boolean mediaInline = false;
		boolean otherInline = false;
		final String[] inlineParameters = request.getParameterValues(INLINE);
		if(inlineParameters!=null)
		{
			for(final String p : inlineParameters)
			{
				if(MEDIA_INLINE.equals(p))
					mediaInline = true;
				else if(OTHER_INLINE.equals(p))
					otherInline = true;
			}
		}
		
		return new MediaCop((MediaPath)model.findFeatureByID(mediaID), mediaInline, otherInline);
	}
	
	MediaCop toggleInlineMedia()
	{
		return new MediaCop(media, !mediaInline, otherInline);
	}

	MediaCop toggleInlineOther()
	{
		return new MediaCop(media, mediaInline, !otherInline);
	}

	@Override
	void writeHead(final PrintStream out)
	{
		if(mediaInline || otherInline)
			Media_Jspm.writeHead(this, out);
	}
	
	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request)
	{
		try
		{
			model.startTransaction(getClass().getName());
			
			final Media other;
			final Condition c;
			
			if(media instanceof Media)
			{
				other = null;
				c = ((Media)media).getIsNull().isNotNull();
			}
			else if(media instanceof MediaFilter)
			{
				other = ((MediaFilter)media).getSource();
				c = other.getIsNull().isNotNull();
			}
			else if(media instanceof MediaRedirect)
			{
				other = ((MediaRedirect)media).getTarget();
				c = other.getIsNull().isNotNull();
			}
			else
			{
				other = null;
				c = null;
			}
			
			final Query<? extends Item> q = media.getType().newQuery(c);
			q.setLimit(0, 50);
			final List<? extends Item> items = q.search();
			Media_Jspm.writeBody(this, out, items, other);
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}
	
	static final String fn(final String url)
	{
		final int pos = url.lastIndexOf('/');
		return (pos>0) ? url.substring(pos+1) : url;
	}
}
