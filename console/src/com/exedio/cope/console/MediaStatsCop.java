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

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.pattern.MediaThumbnail;

final class MediaStatsCop extends ConsoleCop
{

	MediaStatsCop()
	{
		super("media");
		addParameter(TAB, TAB_MEDIA_STATS);
	}
	
	MediaCop toMedia(final Media media)
	{
		return new MediaCop(media);
	}
	
	MediaCop toThumbnail(final MediaThumbnail thumbnail)
	{
		return new MediaCop(thumbnail);
	}
	
	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
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

		Console_Jspm.writeBody(this, out, medias);
	}
	
}
