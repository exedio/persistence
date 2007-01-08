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

import java.io.IOException;
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
	private static final String INLINE_MEDIA = "m";
	private static final String INLINE_OTHER = "o";
	
	final MediaPath media;
	final Media other;
	final boolean inlineMedia;
	final boolean inlineOther;

	MediaCop(final MediaPath media, final boolean inlineMedia, final boolean inlineOther)
	{
		super("media - " + media.getID());
		
		this.media = media;
		
		if(media instanceof Media)
			other = null;
		else if(media instanceof MediaFilter)
			other = ((MediaFilter)media).getSource();
		else if(media instanceof MediaRedirect)
			other = ((MediaRedirect)media).getTarget();
		else
			other = null;

		this.inlineMedia = inlineMedia;
		this.inlineOther = inlineOther;
		
		addParameter(MEDIA, media.getID());
		if(inlineMedia)
			addParameter(INLINE, INLINE_MEDIA);
		if(inlineOther)
			addParameter(INLINE, INLINE_OTHER);
	}
	
	static MediaCop getMediaCop(final Model model, final HttpServletRequest request)
	{
		final String mediaID = request.getParameter(MEDIA);
		if(mediaID==null)
			return null;

		boolean inlineMedia = false;
		boolean inlineOther = false;
		final String[] inlineParameters = request.getParameterValues(INLINE);
		if(inlineParameters!=null)
		{
			for(final String p : inlineParameters)
			{
				if(INLINE_MEDIA.equals(p))
					inlineMedia = true;
				else if(INLINE_OTHER.equals(p))
					inlineOther = true;
			}
		}
		
		return new MediaCop((MediaPath)model.findFeatureByID(mediaID), inlineMedia, inlineOther);
	}
	
	MediaCop toggleInlineMedia()
	{
		return new MediaCop(media, !inlineMedia, inlineOther);
	}

	MediaCop toggleInlineOther()
	{
		return new MediaCop(media, inlineMedia, !inlineOther);
	}

	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
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
			Console_Jspm.writeBody(this, out, items, other);
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
