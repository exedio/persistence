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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaPath;

final class MediaStatsCop extends ConsoleCop
{

	static final String MEDIA_TYPE = "mt";
	static final String MEDIA_NAME = "mn";
	
	final Media media;

	MediaStatsCop()
	{
		this(null);
	}
	
	private MediaStatsCop(final Media media)
	{
		super("media");
		this.media = media;
		
		addParameter(TAB, TAB_MEDIA_STATS);

		if(media!=null)
		{
			addParameter(MEDIA_TYPE, media.getType().getID());
			addParameter(MEDIA_NAME, media.getName());
		}
	}
	
	MediaStatsCop toMedia(final Media media)
	{
		return new MediaStatsCop(media);
	}
	
	static MediaStatsCop getMediaStatsCop(final Model model, final HttpServletRequest request)
	{
		final String typeID = request.getParameter(MEDIA_TYPE);
		return new MediaStatsCop((typeID==null) ? null : (Media)model.findTypeByID(typeID).getFeature(request.getParameter(MEDIA_NAME)));
	}

	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
	{
		if(media==null)
		{
			final ArrayList<MediaPath> medias = new ArrayList<MediaPath>();
	
			for(final Type<Item> type : model.getTypes())
			{
				for(final Feature feature : type.getDeclaredFeatures())
				{
					if(feature instanceof MediaPath)
						medias.add((MediaPath)feature);
				}
			}
	
			Console_Jspm.writeMediaStats(out, medias, this);
		}
		else
		{
			try
			{
				model.startTransaction("MediaStatsCop#media");
				final Query<? extends Item> q = media.getType().newQuery(media.getIsNull().isNotNull());
				q.setLimit(0, 50);
				final List<? extends Item> items = q.search();
				Console_Jspm.writeMedia(out, items, this);
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
	}
	
}
