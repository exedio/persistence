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
import java.util.Collection;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.MediaInfo;
import com.exedio.cope.pattern.MediaPath;

final class MediaStatsCop extends ConsoleCop
{
	MediaStatsCop(final Args args)
	{
		super(TAB_MEDIA_STATS, "media", args);
	}

	@Override
	protected MediaStatsCop newArgs(final Args args)
	{
		return new MediaStatsCop(args);
	}
	
	MediaCop toMedia(final MediaPath media)
	{
		return new MediaCop(args, media);
	}
	
	@Override
	final void writeBody(
			final Out out,
			final Model model,
			final HttpServletRequest request,
			final History history)
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

		final MediaInfo[] infos = new MediaInfo[medias.size()];
		int mediaIndex = 0;
		for(final MediaPath media : medias)
			infos[mediaIndex++] = media.getInfo();
		final MediaSummary summary = new MediaSummary(infos);
		
		Media_Jspm.writeBody(this, out, medias, names, shortNames, infos, summary);
	}
	
	private static final String[] names = {
			"Redirect From <small>(301)</small>",
			"Exception <small>(500)</small>",
			"Not An Item <small>(404)</small>",
			"No Such Item <small>(404)</small>",
			"Moved <small>(301)</small>",
			"Is Null <small>(404)</small>",
			"Not Computable <small>(404)</small>",
			"Not Modified <small>(304)</small>",
			"Delivered <small>(200/301)</small>",
			"log<sub>10</sub> Not Modified / Delivered",
			"Type",
			"Name",
			"Description",
		};
	private static final String[] shortNames = {
			"rf",
			"ex",
			"nai",
			"nsi",
			"mv",
			"in",
			"nc",
			"nm",
			"del",
			"ratio",
		};
	
	static final String[] format(final MediaSummary summary)
	{
		return format(new int[]{
				summary.getRedirectFrom(),
				summary.getException(),
				summary.getNotAnItem(),
				summary.getNoSuchItem(),
				summary.getMoved(),
				summary.getIsNull(),
				summary.getNotComputable(),
				summary.getNotModified(),
				summary.getDelivered()
		});
	}
	
	static final String[] format(final MediaInfo info)
	{
		return format(new int[]{
				info.getRedirectFrom(),
				info.getException(),
				info.getNotAnItem(),
				info.getNoSuchItem(),
				info.getMoved(),
				info.getIsNull(),
				info.getNotComputable(),
				info.getNotModified(),
				info.getDelivered()
		});
	}
	
	private static final String[] format(final int[] numbers)
	{
		final int length = numbers.length;
		final String[] result = new String[length];
		for(int i = 0; i<length; i++)
			result[i] = Format.formatAndHide(0, numbers[i]);
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
	
	static final void printContentTypes(final OutBasic out, final Collection<String> contentTypes)
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
				out.write(", ");
			
			if(prefix!=null && contentType.startsWith(prefix))
			{
				out.write('~');
				out.write(contentType.substring(prefix.length()-1));
			}
			else
			{
				out.write(contentType);
				int pos = contentType.indexOf('/');
				if(pos>1)
					prefix = contentType.substring(0, pos+1);
			}
		}
	}
}
