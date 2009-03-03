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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Model;
import com.exedio.cops.Cop;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;

abstract class ConsoleCop extends Cop
{
	protected static class Args
	{
		static final String HISTORY_MODEL_SHOWN = "sh";
		private static final String AUTO_REFRESH = "ar";
		
		final boolean historyModelShown;
		final int autoRefresh;
		
		Args(final boolean historyModelShown, final int autoRefresh)
		{
			this.historyModelShown = historyModelShown;
			this.autoRefresh = autoRefresh;
		}
		
		Args(final HttpServletRequest request)
		{
			this.historyModelShown = getBooleanParameter(request, HISTORY_MODEL_SHOWN);
			this.autoRefresh = getIntParameter(request, AUTO_REFRESH, 0);
		}
		
		void addParameters(final ConsoleCop cop)
		{
			cop.addParameterAccessor(HISTORY_MODEL_SHOWN, historyModelShown);
			cop.addParameterAccessor(AUTO_REFRESH, autoRefresh, 0);
		}
	}
	
	private static final String NAME_POSTFIX = ".html";
	final String name;
	final Args args;
	static final int[] AUTO_REFRESHS = new int[]{0, 2, 5, 15, 60};

	protected ConsoleCop(final String tab, final String name, final Args args)
	{
		super(tab + NAME_POSTFIX);
		this.name = name;
		this.args = args;
		args.addParameters(this);
	}
	
	long start = 0;
	
	void addParameterAccessor(final String key, final boolean value)
	{
		addParameter(key, value);
	}
	
	void addParameterAccessor(final String key, final int value, final int defaultValue)
	{
		addParameter(key, value, defaultValue);
	}
	
	/**
	 * @param request used in subclasses
	 * @param model used in subclasses
	 */
	void initialize(final HttpServletRequest request, final Model model)
	{
		start = System.currentTimeMillis();
	}
	
	protected abstract ConsoleCop newArgs(final Args args);
	
	final ConsoleCop toHistoryModelShown(final boolean historyModelShown)
	{
		return newArgs(new Args(historyModelShown, args.autoRefresh));
	}
	
	final ConsoleCop toAutoRefresh(final int autoRefresh)
	{
		return newArgs(new Args(args.historyModelShown, autoRefresh));
	}
	
	int getResponseStatus()
	{
		return HttpServletResponse.SC_OK;
	}
	
	final ConsoleCop[] getTabs()
	{
		return
			new ConsoleCop[]{
					new PropertiesCop(args),
					new SchemaCop(args),
					new UnsupportedConstraintCop(args),
					new SequenceCop(args),
					new TypeColumnCop(args),
					new CopyConstraintCop(args),
					new RevisionCop(args),
					new DatabaseLogCop(args),
					new ConnectionPoolCop(args),
					new TransactionCop(args),
					new ItemCacheCop(args),
					new QueryCacheCop(args),
					new MediaStatsCop(args),
					new MediaTypeCop(args),
					new ClusterCop(args),
					new VmCop(args, false, false),
					new EnvironmentCop(args),
					new HiddenCop(args),
					new ModificationListenerCop(args),
					new HistoryCop(args),
				};
	}
	
	final String getStart()
	{
		if(start==0)
			throw new RuntimeException();
		
		return new SimpleDateFormat("yyyy/MM/dd'&nbsp;'HH:mm:ss.SSS Z (z)").format(new Date(start));
	}
	
	final long getDuration()
	{
		if(start==0)
			throw new RuntimeException();

		return System.currentTimeMillis() - start;
	}
	
	/**
	 * @param out used in subclasses
	 */
	void writeHead(PrintStream out)
	{
		// default implementation does nothing
	}
	
	abstract void writeBody(PrintStream out, Model model, HttpServletRequest request, History history);
	
	static final String TAB_PROPERTIES = "properties";
	static final String TAB_SCHEMA = "schema";
	static final String TAB_UNSUPPORTED_CONSTRAINTS = "unsupportedconstraints";
	static final String TAB_TYPE_COLUMNS = "typecolumns";
	static final String TAB_COPY_CONSTRAINTS = "copyconstraints";
	static final String TAB_REVISION = "revision";
	static final String TAB_DATBASE_LOG = "dblogs";
	static final String TAB_CONNECTION_POOL = "connections";
	static final String TAB_TRANSACTION = "transactions";
	static final String TAB_ITEM_CACHE = "itemcache";
	static final String TAB_QUERY_CACHE = "querycache";
	static final String TAB_HISTORY = "history";
	static final String TAB_SEQUENCE = "sequence";
	static final String TAB_MEDIA_STATS = "mediastats";
	static final String TAB_MEDIA_TYPE = "mediatype";
	static final String TAB_CLUSTER = "cluster";
	static final String TAB_VM = "vm";
	static final String TAB_ENVIRONMENT = "environment";
	static final String TAB_HIDDEN = "hidden";
	static final String TAB_MODIFICATION_LISTENER = "modificationlistener";
	
	static final ConsoleCop getCop(final Model model, final HttpServletRequest request)
	{
		final Args args = new Args(request);
		final String pathInfo = request.getPathInfo();
		
		if("/".equals(pathInfo))
			return new PropertiesCop(args);
		
		if(pathInfo==null || !pathInfo.startsWith("/") || !pathInfo.endsWith(NAME_POSTFIX))
			return new NotFound(args, pathInfo);
		
		final String tab = pathInfo.substring(1, pathInfo.length()-NAME_POSTFIX.length());
		if(TAB_SCHEMA.equals(tab))
			return new SchemaCop(args);
		if(TAB_UNSUPPORTED_CONSTRAINTS.equals(tab))
			return new UnsupportedConstraintCop(args);
		if(TAB_TYPE_COLUMNS.equals(tab))
			return new TypeColumnCop(args);
		if(TAB_COPY_CONSTRAINTS.equals(tab))
			return new CopyConstraintCop(args);
		if(TAB_PROPERTIES.equals(tab))
			return new PropertiesCop(args);
		if(TAB_REVISION.equals(tab))
			return new RevisionCop(args, request);
		if(TAB_CONNECTION_POOL.equals(tab))
			return new ConnectionPoolCop(args);
		if(TAB_TRANSACTION.equals(tab))
			return new TransactionCop(args);
		if(TAB_DATBASE_LOG.equals(tab))
			return new DatabaseLogCop(args);
		if(TAB_ITEM_CACHE.equals(tab))
			return new ItemCacheCop(args);
		if(TAB_QUERY_CACHE.equals(tab))
			return QueryCacheCop.getQueryCacheCop(args, request);
		if(TAB_HISTORY.equals(tab))
			return new HistoryCop(args);
		if(TAB_SEQUENCE.equals(tab))
			return new SequenceCop(args);
		if(TAB_MEDIA_STATS.equals(tab))
			return new MediaStatsCop(args);
		if(TAB_MEDIA_TYPE.equals(tab))
			return new MediaTypeCop(args);
		if(TAB_CLUSTER.equals(tab))
			return new ClusterCop(args);
		if(TAB_VM.equals(tab))
			return VmCop.getVmCop(args, request);
		if(TAB_ENVIRONMENT.equals(tab))
			return new EnvironmentCop(args);
		if(TAB_HIDDEN.equals(tab))
			return new HiddenCop(args);
		if(TAB_MODIFICATION_LISTENER.equals(tab))
			return new ModificationListenerCop(args);

		final MediaCop mediaCop = MediaCop.getMediaCop(model, args, request);
		if(mediaCop!=null)
			return mediaCop;

		return new NotFound(args, pathInfo);
	}
	
	private final static class NotFound extends ConsoleCop
	{
		private final String pathInfo;
		
		protected NotFound(final Args args, final String pathInfo)
		{
			super("Not Found", "Not Found", args);
			this.pathInfo = pathInfo;
		}

		@Override
		protected ConsoleCop newArgs(final Args args)
		{
			return new NotFound(args, pathInfo);
		}
		
		@Override
		int getResponseStatus()
		{
			return HttpServletResponse.SC_NOT_FOUND;
		}

		@Override
		final void writeBody(
				final PrintStream out,
				final Model model,
				final HttpServletRequest request,
				final History history)
		{
			Console_Jspm.writeNotFound(out, pathInfo);
		}
	}
	
	static void writePager(final PrintStream out, final Pageable cop)
	{
		final Pager pager = cop.getPager();
		if(pager.isNeeded())
		{
			out.print("<span class=\"pager\">");
			Console_Jspm.writePagerButton(out, cop, pager.first(),    "&lt;&lt;", "disabled");
			Console_Jspm.writePagerButton(out, cop, pager.previous(), "&lt;",     "disabled");
			Console_Jspm.writePagerButton(out, cop, pager.next(),     "&gt;",     "disabled");
			Console_Jspm.writePagerButton(out, cop, pager.last(),     "&gt;&gt;", "disabled");
			for(final Pager newLimit : pager.newLimits())
				Console_Jspm.writePagerButton(out, cop, newLimit, String.valueOf(newLimit.getLimit()), "selected");
			out.print(' ');
			out.print(pager.getFrom());
			out.print('-');
			out.print(pager.getTo());
			out.print('/');
			out.print(pager.getTotal());
			out.print("</span>");
		}
	}
}
