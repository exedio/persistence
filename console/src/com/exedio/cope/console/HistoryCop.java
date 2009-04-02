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

import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cope.Type;

final class HistoryCop extends ConsoleCop<HashMap<Type<?>, HistoryCop.Info>>
{
	static final String ANALYZE = "analyze";
	
	HistoryCop(final Args args)
	{
		super(TAB_HISTORY, "history", args);
	}

	@Override
	protected HistoryCop newArgs(final Args args)
	{
		return new HistoryCop(args);
	}
	
	@Override
	final void writeBody(
			final PrintStream out,
			final Model model,
			final HttpServletRequest request,
			final History history)
	{
		if(history.isAvailable())
			History_Jspm.writeBody(
					this,
					out,
					args.historyModelShown,
					HistoryThread.HISTORY_MODEL,
					history.getThreadID(),
					history.isRunning(),
					(isPost(request) && request.getParameter(ANALYZE)!=null));
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
}
