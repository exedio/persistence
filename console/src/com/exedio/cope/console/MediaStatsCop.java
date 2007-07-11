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
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.MediaPath;

final class MediaStatsCop extends ConsoleCop
{
	MediaStatsCop()
	{
		super(TAB_MEDIA_STATS, "media");
	}
	
	MediaCop toMedia(final MediaPath media)
	{
		return new MediaCop(media, false, false);
	}
	
	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request)
	{
		final ArrayList<MediaPath> medias = new ArrayList<MediaPath>();

		for(final Type<?> type : model.getTypes())
		{
			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof MediaPath)
					medias.add((MediaPath)feature);
			}
		}

		Media_Jspm.writeBody(this, out, medias);
	}
	
	static final void printContentTypes(final PrintStream out, final Collection<String> contentTypes)
	{
		String prefix = null;
		boolean first = true;
		for(final String contentType : new TreeSet<String>(contentTypes))
		{
			if(first)
				first = false;
			else
				out.print(", ");
			
			if(prefix!=null && contentType.startsWith(prefix))
			{
				out.print('~');
				out.print(contentType.substring(prefix.length()-1));
			}
			else
			{
				out.print(contentType);
				int pos = contentType.indexOf('/');
				if(pos>1)
					prefix = contentType.substring(0, pos+1);
			}
		}
	}
}
