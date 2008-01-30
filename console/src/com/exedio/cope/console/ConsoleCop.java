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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
	private static final String NAME_POSTFIX = ".html";
	final String name;

	protected ConsoleCop(final String tab, final String name)
	{
		super(tab + NAME_POSTFIX);
		this.name = name;
	}
	
	long start = 0;
	private SimpleDateFormat fullDateFormat, todayDateFormat;
	DecimalFormat nf;
	String authentication;
	String hostname;
	
	/**
	 * @param model used in subclasses
	 */
	void initialize(final HttpServletRequest request, final Model model)
	{
		start = System.currentTimeMillis();
		fullDateFormat = new SimpleDateFormat("yyyy/MM/dd'&nbsp;'HH:mm:ss'<small>'.SSS'</small>'");
		todayDateFormat = new SimpleDateFormat("HH:mm:ss'<small>'.SSS'</small>'");
		final DecimalFormatSymbols nfs = new DecimalFormatSymbols();
		nfs.setDecimalSeparator(',');
		nfs.setGroupingSeparator('\'');
		nf = new DecimalFormat("", nfs);
		final Principal principal = request.getUserPrincipal();
		authentication = principal!=null ? principal.getName() : null;
		try
		{
			hostname = InetAddress.getLocalHost().getHostName();
		}
		catch(UnknownHostException e)
		{
			// leave hostname==null
		}
	}
	
	int getResponseStatus()
	{
		return HttpServletResponse.SC_OK;
	}
	
	final ConsoleCop[] getTabs()
	{
		return new ConsoleCop[]{
				new PropertiesCop(),
				new SchemaCop(),
				new TypeColumnCop(),
				new MigrationCop(),
				new DatabaseLogCop(),
				new ConnectionPoolCop(),
				new TransactionCop(),
				new ItemCacheCop(),
				new QueryCacheCop(),
				new PrimaryKeysCop(),
				new MediaStatsCop(),
				new VmCop(false, false),
				new EnvironmentCop(),
				new HiddenCop(),
				new ModificationListenerCop(),
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
	
	final String getAuthentication()
	{
		return authentication;
	}
	
	final String getHostname()
	{
		return hostname;
	}
	
	private static final long todayInterval = 6 * 60 * 60 * 1000; // 6 hours
	
	final String formatAndHide(final Date date)
	{
		return date!=null ? format(date) : "";
	}
	
	final String format(final Date date)
	{
		final long dateMillis = date.getTime();
		return (
			( (start-todayInterval) < dateMillis && dateMillis < (start+todayInterval) )
			? todayDateFormat : fullDateFormat).format(date);
	}
	
	final String formatDate(final long date)
	{
		return format(new Date(date));
	}
	
	final String format(final long number)
	{
		return /*"fm" +*/ nf.format(number);
	}
	
	final String formatAndHide(final long hidden, final long number)
	{
		return /*("["+hidden+']') +*/ (number!=hidden ? format(number) : "");
	}
	
	/**
	 * @param out used in subclasses
	 */
	void writeHead(PrintStream out)
	{
		// default implementation does nothing
	}
	
	abstract void writeBody(PrintStream out, Model model, HttpServletRequest request);
	
	static final String TAB_PROPERTIES = "properties";
	static final String TAB_SCHEMA = "schema";
	static final String TAB_TYPE_COLUMNS = "typecolumns";
	static final String TAB_MIGRATION = "migration";
	static final String TAB_DATBASE_LOG = "dblogs";
	static final String TAB_CONNECTION_POOL = "connections";
	static final String TAB_TRANSACTION = "transactions";
	static final String TAB_ITEM_CACHE = "itemcache";
	static final String TAB_QUERY_CACHE = "querycache";
	static final String TAB_PRIMARY_KEY = "primarykeys";
	static final String TAB_MEDIA_STATS = "mediastats";
	static final String TAB_VM = "vm";
	static final String TAB_ENVIRONMENT = "environment";
	static final String TAB_HIDDEN = "hidden";
	static final String TAB_MODIFICATION_LISTENER = "modificationlistener";
	
	static final ConsoleCop getCop(final Model model, final HttpServletRequest request)
	{
		final String pathInfo = request.getPathInfo();
		
		if("/".equals(pathInfo))
			return new PropertiesCop();
		
		if(pathInfo==null || !pathInfo.startsWith("/") || !pathInfo.endsWith(NAME_POSTFIX))
			return new NotFound(pathInfo);
		
		final String tab = pathInfo.substring(1, pathInfo.length()-NAME_POSTFIX.length());
		if(TAB_SCHEMA.equals(tab))
			return new SchemaCop();
		if(TAB_TYPE_COLUMNS.equals(tab))
			return new TypeColumnCop();
		if(TAB_PROPERTIES.equals(tab))
			return new PropertiesCop();
		if(TAB_MIGRATION.equals(tab))
			return new MigrationCop(request);
		if(TAB_CONNECTION_POOL.equals(tab))
			return new ConnectionPoolCop();
		if(TAB_TRANSACTION.equals(tab))
			return new TransactionCop();
		if(TAB_DATBASE_LOG.equals(tab))
			return new DatabaseLogCop();
		if(TAB_ITEM_CACHE.equals(tab))
			return new ItemCacheCop();
		if(TAB_QUERY_CACHE.equals(tab))
			return QueryCacheCop.getQueryCacheCop(request);
		if(TAB_PRIMARY_KEY.equals(tab))
			return new PrimaryKeysCop();
		if(TAB_MEDIA_STATS.equals(tab))
			return new MediaStatsCop();
		if(TAB_VM.equals(tab))
			return VmCop.getVmCop(request);
		if(TAB_ENVIRONMENT.equals(tab))
			return new EnvironmentCop();
		if(TAB_HIDDEN.equals(tab))
			return new HiddenCop();
		if(TAB_MODIFICATION_LISTENER.equals(tab))
			return new ModificationListenerCop();

		final MediaCop mediaCop = MediaCop.getMediaCop(model, request);
		if(mediaCop!=null)
			return mediaCop;

		return new NotFound(pathInfo);
	}
	
	private static class NotFound extends ConsoleCop
	{
		private final String pathInfo;
		
		protected NotFound(final String pathInfo)
		{
			super("Not Found", "Not Found");
			this.pathInfo = pathInfo;
		}
		
		@Override
		int getResponseStatus()
		{
			return HttpServletResponse.SC_NOT_FOUND;
		}

		@Override
		void writeBody(PrintStream out, Model model, HttpServletRequest request)
		{
			Console_Jspm.writeNotFound(out, pathInfo);
		}
	}
	
	static void writePager(final PrintStream out, final Pageable cop)
	{
		final Pager pager = cop.getPager();
		if(pager.isNeeded())
		{
			Media_Jspm.writePagerButton(out, cop, pager.first(),    "&lt;&lt;");
			Media_Jspm.writePagerButton(out, cop, pager.previous(), "&lt;");
			Media_Jspm.writePagerButton(out, cop, pager.next(),     "&gt;");
			Media_Jspm.writePagerButton(out, cop, pager.last(),     "&gt;&gt;");
			for(final Pager newLimit : pager.newLimits())
				Media_Jspm.writePagerButton(out, cop, newLimit, String.valueOf(newLimit.getLimit()));
			out.print(' ');
			out.print(pager.getFrom());
			out.print('-');
			out.print(pager.getTo());
			out.print('/');
			out.print(pager.getTotal());
		}
	}
}
