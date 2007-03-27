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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Migration;
import com.exedio.cope.Model;


final class MigrationCop extends ConsoleCop
{
	private static final String SQL = "sql";
	
	final boolean sql;

	MigrationCop(final boolean sql)
	{
		super("migration");
		this.sql = sql;
		
		addParameter(TAB, TAB_MIGRATION);
		if(sql)
			addParameter(SQL, "t");
	}
	
	static final MigrationCop getMigrationCop(final HttpServletRequest request)
	{
		return new MigrationCop(request.getParameter(SQL)!=null);
	}
	
	MigrationCop toToggleSql()
	{
		return new MigrationCop(!sql);
	}
	
	int oldest = Integer.MAX_VALUE;
	int latest = Integer.MIN_VALUE;
	
	private void register(final int version)
	{
		if(oldest>version)
			oldest = version;
		if(latest<version)
			latest = version;
	}

	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request)
	{
		if(model.isMigrationSupported())
		{
			final List<Migration> migrations = model.getMigrations();
			final HashMap<Integer, Migration> migrationMap = new HashMap<Integer, Migration>();
			for(final Migration m : migrations)
			{
				register(m.getVersion());
				migrationMap.put(m.getVersion(), m);
			}
			
			final Map<Integer, byte[]> logsRaw = model.getMigrationLogs();
			final HashMap<Integer, String> logs = new HashMap<Integer, String>();
			try
			{
				for(final Integer v : logsRaw.keySet())
				{
					register(v);
					logs.put(v, new String(logsRaw.get(v), "latin1"));
				}
			}
			catch(UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
			
			final int current = model.getMigrationVersion();
			register(current);
			
			Migration_Jspm.writeBody(this, request, out, oldest, latest, current, migrationMap, logs);
		}
		else
			Migration_Jspm.writeBody(this, request, out, 0, 0, 0, null, null);
	}
}
