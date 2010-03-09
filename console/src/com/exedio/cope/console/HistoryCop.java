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

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;

final class HistoryCop extends ConsoleCop<HashMap<Type<?>, HistoryCop.Info>> implements Pageable
{
	private static final Pager.Config PAGER_CONFIG = new Pager.Config(10, 20, 50, 100, 200, 500);
	static final String ANALYZE = "analyze";
	static final String PURGE = "purge";
	
	private final Pager pager;
	
	HistoryCop(final Args args)
	{
		this(args, PAGER_CONFIG.newPager());
	}
	
	private HistoryCop(final Args args, final Pager pager)
	{
		super(TAB_HISTORY, "history", args);
		this.pager = pager;
		pager.addParameters(this);
	}
	
	static final HistoryCop getHistoryCop(final Args args, final HttpServletRequest request)
	{
		return new HistoryCop(args, PAGER_CONFIG.newPager(request));
	}

	@Override
	protected HistoryCop newArgs(final Args args)
	{
		return new HistoryCop(args);
	}
	
	@Override
	final void writeBody(
			final Out out,
			final Model model,
			final HttpServletRequest request,
			final History history)
	{
		if(history.isAvailable())
		{
			History_Jspm.writeBody(
					this,
					out,
					args.historyModelShown,
					HistoryThread.HISTORY_MODEL,
					history.getThreadID(),
					history.isRunning(),
					(isPost(request) && request.getParameter(ANALYZE)!=null));
			
			try
			{
				int limitDays = 100;
				if(isPost(request))
				{
					final String purgeString = request.getParameter(PURGE);
					if(purgeString!=null)
					{
						limitDays = Integer.parseInt(purgeString);
						HistoryPurge.purge(limitDays);
					}
				}
				HistoryThread.HISTORY_MODEL.startTransaction("browse HistoryPurge");
				final Query<HistoryPurge> purgeQuery = HistoryPurge.newQuery();
				purgeQuery.setLimit(pager.getOffset(), pager.getLimit());
				final Query.Result<HistoryPurge> purgeResult = purgeQuery.searchAndTotal();
				pager.init(purgeResult.getData().size(), purgeResult.getTotal());
				History_Jspm.writePurges(this, out, history.getAutoPurgeDays(), limitDays, purgeResult.getData());
				HistoryThread.HISTORY_MODEL.commit();
			}
			finally
			{
				HistoryThread.HISTORY_MODEL.rollbackIfNotCommitted();
			}
		}
		else
			History_Jspm.writeBodyNotAvailable(out);
	}
	
	static class Info
	{
		final int count;
		final Date from;
		final Date until;
		
		Info(final int count, final Date from, final Date until)
		{
			this.count = count;
			this.from  = from;
			this.until = until;
		}
	}
	
	public Pager getPager()
	{
		return pager;
	}
	
	public HistoryCop toPage(final Pager pager)
	{
		return new HistoryCop(args, pager);
	}
}
