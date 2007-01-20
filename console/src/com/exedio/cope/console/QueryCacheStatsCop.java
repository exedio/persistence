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

import com.exedio.cope.Model;
import com.exedio.cope.util.CacheQueryInfo;

final class QueryCacheStatsCop extends ConsoleCop
{

	QueryCacheStatsCop()
	{
		super("query cache");
		addParameter(TAB, TAB_QUERY_CACHE_STATS);
	}

	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request)
	{
		final CacheQueryInfo[] histogram = model.getCacheQueryHistogram();
		int sumLength = 0;
		int maxLength = 0;
		int minLength = Integer.MAX_VALUE;
		for(final CacheQueryInfo info : histogram)
		{
			final int length = info.getQuery().length();
			sumLength += length;
			if(length<minLength)
				minLength = length;
			if(length>maxLength)
				maxLength = length;
		}
		
		Console_Jspm.writeBody(this, out,
				model.getCacheQueryInfo(),
				histogram,
				sumLength, maxLength, minLength,
				model.getProperties().getCacheQueryHistogram());
	}
}
