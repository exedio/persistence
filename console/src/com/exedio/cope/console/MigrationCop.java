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

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Migration;
import com.exedio.cope.Model;


final class MigrationCop extends ConsoleCop
{
	MigrationCop()
	{
		super("migration");
		addParameter(TAB, TAB_MIGRATION);
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
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
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
			
			final Map<Integer, String> logs = model.getMigrationLogs();
			for(final Integer v : logs.keySet())
				register(v);
			
			final int current = model.getMigrationVersion();
			register(current);
			
			Console_Jspm.writeBody(this, out, oldest, latest, current, migrationMap, logs);
		}
		else
			Console_Jspm.writeBodyDisabled(this, out);
	}
}
