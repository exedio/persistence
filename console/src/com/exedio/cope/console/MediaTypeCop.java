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

package com.exedio.cope.console;

import java.util.ArrayList;
import java.util.List;

import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Media;

final class MediaTypeCop extends TestCop<Media>
{
	MediaTypeCop(final Args args)
	{
		super(TAB_MEDIA_TYPE, "media types", args);
	}

	@Override
	protected MediaTypeCop newArgs(final Args args)
	{
		return new MediaTypeCop(args);
	}
	
	@Override
	List<Media> getItems(final Model model)
	{
		final ArrayList<Media> medias = new ArrayList<Media>();
		
		for(final Type<?> type : model.getTypes())
		{
			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof Media)
					medias.add((Media)feature);
			}
		}
		return medias;
	}
	
	@Override
	String getCaption()
	{
		return "Media Types";
	}
	
	@Override
	String[] getHeadings()
	{
		return new String[]{"Type", "Name", "Content Type", "Query"};
	}
	
	private final Query<? extends Item> query(final Media media)
	{
		return media.getType().newQuery(media.bodyMismatchesContentType());
	}
	
	@Override
	String[] getValues(final Media media)
	{
		return new String[]{
				media.getType().getID(),
				media.getName(),
				media.getContentTypeDescription().replaceAll(",", ", "),
				query(media).toString()};
	}
	
	@Override
	int test(final Media media)
	{
		final Query<? extends Item> q = query(media);
		final int result = q.total();
		if(result>0)
		{
			q.setLimit(0, 100);
			System.out.println("* COPE Media Content Type mismatched on " + media.getID());
			for(final Item item : q.search())
				System.out.println(' ' + item.getCopeID() + ' ' + media.getContentType(item));
			System.out.println("/ COPE Media Content Type mismatched on " + media.getID());
		}
		return result;
	}
}
