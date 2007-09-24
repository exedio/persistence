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

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.pattern.MediaRedirect;
import com.exedio.cops.Pager;

final class MediaCop extends ConsoleCop
{
	private static final String MEDIA = "m";
	private static final String INLINE = "il";
	private static final String MEDIA_INLINE = "m";
	private static final String OTHER_INLINE = "o";
	
	private static final int LIMIT_DEFAULT = 10;
	
	final MediaPath media;
	final Media other;
	final boolean mediaInline;
	final boolean otherInline;
	final Pager pager;

	MediaCop(final MediaPath media)
	{
		this(media, false);
	}

	MediaCop(final MediaPath media, final boolean inline)
	{
		this(media, inline, false, new Pager(LIMIT_DEFAULT));
	}
	
	private MediaCop(final MediaPath media, final boolean mediaInline, final boolean otherInline, final Pager pager)
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
		this.pager = pager;
		
		addParameter(MEDIA, media.getID());
		if(mediaInline)
			addParameter(INLINE, MEDIA_INLINE);
		if(otherInline)
			addParameter(INLINE, OTHER_INLINE);
		pager.addParameters(this);
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
		
		return new MediaCop(
				(MediaPath)model.findFeatureByID(mediaID),
				mediaInline, otherInline,
				Pager.newPager(request, LIMIT_DEFAULT));
	}
	
	MediaCop toggleInlineMedia()
	{
		return new MediaCop(media, !mediaInline, otherInline, pager);
	}

	MediaCop toggleInlineOther()
	{
		return new MediaCop(media, mediaInline, !otherInline, pager);
	}

	MediaCop toPage(final Pager pager)
	{
		return new MediaCop(media, mediaInline, otherInline, pager);
	}
	
	@Override
	void writeHead(final PrintStream out)
	{
		if(mediaInline || otherInline)
			Media_Jspm.writeHead(this, out);
	}
	
	@Override
	final void writeBody(
			final PrintStream out,
			final Model model,
			final HttpServletRequest request)
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
			q.setLimit(pager.getOffset(), pager.getLimit());
			q.setOrderBy(media.getType().getThis(), true);
			final Query.Result<? extends Item> items = q.searchAndTotal();
			pager.init(items.getData().size(), items.getTotal());
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
	
	void writePager(final PrintStream out)
	{
		if(pager.isNeeded())
		{
			out.print(' ');
			out.print(pager.getFrom());
			out.print('-');
			out.print(pager.getTo());
			out.print('/');
			out.print(pager.getTotal());
			Media_Jspm.writePagerButton(out, this, pager.first(),    "&lt;&lt;");
			Media_Jspm.writePagerButton(out, this, pager.previous(), "&lt;");
			Media_Jspm.writePagerButton(out, this, pager.next(),     "&gt;");
			Media_Jspm.writePagerButton(out, this, pager.last(),     "&gt;&gt;");
			for(final Pager newLimit : pager.newLimits())
				Media_Jspm.writePagerButton(out, this, newLimit, String.valueOf(newLimit.getLimit()));
		}
	}
}
