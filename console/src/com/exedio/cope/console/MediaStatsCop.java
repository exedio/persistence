/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
		return new MediaCop(media);
	}
	
	@Override
	final void writeBody(
			final PrintStream out,
			final Model model,
			final HttpServletRequest request,
			final History history,
			final boolean historyModelShown)
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

		final String[] names = {
				"Exception <small>(500)</small>",
				"Not An Item <small>(404)</small>",
				"No Such Item <small>(404)</small>",
				"Is Null <small>(404)</small>",
				"Not Computable <small>(404)</small>",
				"Not Modified <small>(304)</small>",
				"Delivered <small>(200/301)</small>",
				"log<sub>10</sub> Not Modified / Delivered",
				"Type",
				"Name",
				"Description",
			};
		final String[] shortNames = {
				"ex",
				"nai",
				"nsi",
				"in",
				"nc",
				"nm",
				"del",
				"ratio",
			};
		final int[] total = new int[7];
		final int[][] logs = new int[medias.size()][];

		int mediaIndex = 0;
		for(final MediaPath media : medias)
		{
			final int[] log = {
				media.exception.get(),
				media.notAnItem.get(),
				media.noSuchItem.get(),
				media.isNull.get(),
				media.notComputable.get(),
				media.notModified.get(),
				media.delivered.get(),
			};
			for(int i = 0; i<log.length; i++)
				total[i] += log[i];

			logs[mediaIndex++] = log;
		}
		
		Media_Jspm.writeBody(this, out, medias, names, shortNames, logs, total);
	}
	
	final String[] format(final int[] numbers)
	{
		final int length = numbers.length;
		final String[] result = new String[length];
		for(int i = 0; i<length; i++)
			result[i] = formatAndHide(0, numbers[i]);
		return result;
	}
	
	private static final void collapse(final TreeSet<String> contentTypes, final String r, final String a, final String b)
	{
		if(contentTypes.contains(a) && contentTypes.contains(b))
		{
			contentTypes.remove(a);
			contentTypes.remove(b);
			contentTypes.add(r);
		}
	}
	
	static final void printContentTypes(final PrintStream out, final Collection<String> contentTypes)
	{
		final TreeSet<String> sorted = new TreeSet<String>(contentTypes);
		collapse(sorted, "image/[p]jpeg", "image/jpeg", "image/pjpeg");
		collapse(sorted, "image/[x-]png", "image/png", "image/x-png");
		
		String prefix = null;
		boolean first = true;
		for(final String contentType : sorted)
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
