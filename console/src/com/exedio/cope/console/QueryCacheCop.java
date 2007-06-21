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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cope.util.CacheQueryInfo;

final class QueryCacheCop extends ConsoleCop
{
	static final String HISTOGRAM_LIMIT = "hl";
	private static final int HISTOGRAM_LIMIT_DEFAULT = 100;
	private static final String CONDENSE = "condense";
	
	final int histogramLimit;
	final boolean condense;

	QueryCacheCop()
	{
		this(HISTOGRAM_LIMIT_DEFAULT, true);
	}
	
	private QueryCacheCop(final int histogramLimit, final boolean condense)
	{
		super("query cache");
		addParameter(TAB, TAB_QUERY_CACHE);
		if(histogramLimit!=HISTOGRAM_LIMIT_DEFAULT)
			addParameter(HISTOGRAM_LIMIT, String.valueOf(histogramLimit));
		if(!condense)
			addParameter(CONDENSE, "f");
		
		this.histogramLimit = histogramLimit;
		this.condense = condense;
	}

	static QueryCacheCop getQueryCacheCop(final HttpServletRequest request)
	{
		final String hl = request.getParameter(HISTOGRAM_LIMIT);
		return new QueryCacheCop(hl!=null ? Integer.valueOf(hl) : HISTOGRAM_LIMIT_DEFAULT, request.getParameter(CONDENSE)==null);
	}
	
	QueryCacheCop toToggleCondense()
	{
		return new QueryCacheCop(histogramLimit, !condense);
	}
	
	static final class Content
	{
		final CacheQueryInfo[] histogram;
		final Condense[] histogramCondensed;
		
		final int avgKeyLength;
		final int maxKeyLength;
		final int minKeyLength;
		
		final int avgResultSize;
		final int maxResultSize;
		final int minResultSize;
		final int[] resultSizes;
		
		final int avgHits;
		final int maxHits;
		final int minHits;

		Content(final CacheQueryInfo[] histogram, final boolean condense)
		{
			if(histogram.length>0)
			{
				this.histogram = histogram;
				
				final HashMap<String, Condense> histogramCondensed = condense ? new HashMap<String, Condense>() : null;
				
				int sumKeyLength = 0;
				int maxKeyLength = 0;
				int minKeyLength = Integer.MAX_VALUE;
				
				int sumResultSize = 0;
				int maxResultSize = 0;
				int minResultSize = Integer.MAX_VALUE;
				int[] resultSizes = new int[5];
				
				int sumHits = 0;
				int maxHits = 0;
				int minHits = Integer.MAX_VALUE;
				
				int recentUsage = 0;
				for(final CacheQueryInfo info : histogram)
				{
					final String q = info.getQuery();
					
					if(condense)
					{
						StringBuffer qxbuf = null;
						int lastpos = 0;
						for(int pos = q.indexOf('\''); pos>=0; pos = q.indexOf('\'', pos+1))
						{
							if(qxbuf==null)
								qxbuf = new StringBuffer(q.substring(0, pos));
							else
								qxbuf.append(q.substring(lastpos+1, pos));
							
							qxbuf.append('?');
							
							pos = q.indexOf('\'', pos+1);
							if(pos<0)
							{
								qxbuf = null;
								break;
							}
							
							lastpos = pos;
						}
						final String qx;
						if(qxbuf!=null)
						{
							qxbuf.append(q.substring(lastpos+1));
							qx = qxbuf.toString();
						}
						else
							qx = q;
	
						final Condense dongs = histogramCondensed.get(qx);
						if(dongs==null)
							histogramCondensed.put(qx, new Condense(qx, recentUsage, info));
						else
							dongs.accumulate(qx, recentUsage, info);
					}
					
					final int keyLength = q.length();
					sumKeyLength += keyLength;
					if(keyLength<minKeyLength)
						minKeyLength = keyLength;
					if(keyLength>maxKeyLength)
						maxKeyLength = keyLength;
		
					final int resultSize = info.getResultSize();
					sumResultSize += resultSize;
					if(resultSize<minResultSize)
						minResultSize = resultSize;
					if(resultSize>maxResultSize)
						maxResultSize = resultSize;
					if(resultSize<resultSizes.length)
						resultSizes[resultSize]++;
					
					final int hits = info.getHits();
					sumHits += hits;
					if(hits<minHits)
						minHits = hits;
					if(hits>maxHits)
						maxHits = hits;
					
					recentUsage++;
				}
				
				if(histogramCondensed!=null)
				{
					this.histogramCondensed = histogramCondensed.values().toArray(new Condense[histogramCondensed.size()]);
					Arrays.sort(this.histogramCondensed, new Comparator<Condense>(){

						public int compare(final Condense c1, final Condense c2)
						{
							if(c1==c2)
								return 0;

							{
								final int r1 = c1.getRecentUsage();
								final int r2 = c2.getRecentUsage();
								if(r1<r2)
									return -1;
								else if(r1>r2)
									return 1;
							}

							return c1.query.compareTo(c2.query);
						}
					});
				}
				else
				{
					this.histogramCondensed = null;
				}
				
				this.avgKeyLength = sumKeyLength / histogram.length;
				this.maxKeyLength = maxKeyLength;
				this.minKeyLength = minKeyLength;
				
				this.avgResultSize = sumResultSize / histogram.length;
				this.maxResultSize = maxResultSize;
				this.minResultSize = minResultSize;
				this.resultSizes = resultSizes;
				
				this.avgHits = sumHits / histogram.length;
				this.maxHits = maxHits;
				this.minHits = minHits;
			}
			else
			{
				this.histogram = histogram;
				this.histogramCondensed = condense ? new Condense[0] : null;
				
				this.avgKeyLength = -1;
				this.maxKeyLength = -1;
				this.minKeyLength = -1;
				
				this.avgResultSize = -1;
				this.maxResultSize = -1;
				this.minResultSize = -1;
				this.resultSizes = new int[0];
				
				this.avgHits = -1;
				this.maxHits = -1;
				this.minHits = -1;
			}
		}
	}
	
	static final class Condense
	{
		final String query;
		private int count;
		private int recentUsage;
		private int resultSize;
		private int hits;
		
		Condense(final String query, final int recentUsage, final CacheQueryInfo info)
		{
			this.query = query;
			this.count = 1;
			this.recentUsage = recentUsage;
			this.resultSize  = info.getResultSize();
			this.hits        = info.getHits();
		}
		
		void accumulate(final String query, final int recentUsage, final CacheQueryInfo info)
		{
			assert this.query.equals(query);
			this.count++;
			this.recentUsage += recentUsage;
			this.resultSize  += info.getResultSize();
			this.hits        += info.getHits();
		}
		
		int getCount()
		{
			return count;
		}
		
		int getRecentUsage()
		{
			return recentUsage / count;
		}
		
		int getResultSize()
		{
			return resultSize;
		}
		
		int getHits()
		{
			return hits;
		}
	}
		
	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request)
	{
		final CacheQueryInfo[] histogram = model.getQueryCacheHistogram();
		QueryCache_Jspm.writeBody(this, out, request,
				model.getProperties().getQueryCacheLimit(),
				model.getQueryCacheInfo(),
				new Content(histogram, condense));
	}
}
