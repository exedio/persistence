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

import com.exedio.cope.Model;
import com.exedio.cops.Cop;

abstract class ConsoleCop extends Cop
{
	final String name;

	protected ConsoleCop(final String name)
	{
		this.name = name;
	}
	
	long start = 0;
	SimpleDateFormat df;
	DecimalFormat nf;
	String authentication;
	String hostname;
	
	void initialize(final HttpServletRequest request, final Model model)
	{
		start = System.currentTimeMillis();
		df = new SimpleDateFormat("dd.MM.yyyy'&nbsp;'HH:mm:ss.SSS");
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
	
	final ConsoleCop[] getTabs()
	{
		return new ConsoleCop[]{
				new PropertiesCop(),
				new SchemaCop(),
				new TypeColumnCop(),
				new MigrationCop(false),
				new DatabaseLogCop(),
				new ConnectionPoolCop(),
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
		
		return df.format(new Date(start));
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
	
	final String format(final Date date)
	{
		return df.format(date);
	}
	
	final String formatDate(final long date)
	{
		return df.format(new Date(date));
	}
	
	final String format(final long number)
	{
		return /*"fm" +*/ nf.format(number);
	}
	
	final String formatAndHide(final long hidden, final long number)
	{
		return /*("["+hidden+']') +*/ (number!=hidden ? format(number) : "");
	}
	
	void writeHead(HttpServletRequest request, PrintStream out)
	{
		// default implementation does nothing
	}
	
	abstract void writeBody(PrintStream out, Model model, HttpServletRequest request);
	
	static final String TAB = "t";
	static final String TAB_SCHEMA = "schema";
	static final String TAB_TYPE_COLUMNS = "tc";
	static final String TAB_MIGRATION = "mig";
	static final String TAB_DATBASE_LOG = "dblog";
	static final String TAB_CONNECTION_POOL = "cp";
	static final String TAB_ITEM_CACHE = "ca";
	static final String TAB_QUERY_CACHE = "qca";
	static final String TAB_PRIMARY_KEY = "pk";
	static final String TAB_MEDIA_STATS = "m";
	static final String TAB_VM = "vm";
	static final String TAB_ENVIRONMENT = "env";
	static final String TAB_HIDDEN = "hidden";
	static final String TAB_MODIFICATION_LISTENER = "ml";
	
	static final ConsoleCop getCop(final Model model, final HttpServletRequest request)
	{
		final String tab = request.getParameter(TAB);
		if(TAB_SCHEMA.equals(tab))
			return new SchemaCop();
		if(TAB_TYPE_COLUMNS.equals(tab))
			return new TypeColumnCop();
		if(TAB_MIGRATION.equals(tab))
			return MigrationCop.getMigrationCop(request);
		if(TAB_CONNECTION_POOL.equals(tab))
			return new ConnectionPoolCop();
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

		return new PropertiesCop();
	}
}
