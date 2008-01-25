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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Migration;
import com.exedio.cope.Model;
import com.exedio.dsmf.SQLRuntimeException;

final class MigrationCop extends ConsoleCop
{
	MigrationCop()
	{
		super(TAB_MIGRATION, "migration");
	}
	
	@Override
	void writeHead(final PrintStream out)
	{
		Migration_Jspm.writeHead(out);
	}
	
	private TreeMap<Integer, Line> lines = null;
	
	private Line register(final int revision)
	{
		if(lines==null)
			lines = new TreeMap<Integer, Line>();
		Line result = lines.get(revision);
		if(result==null)
		{
			result = new Line(revision);
			lines.put(revision, result);
		}
		return result;
	}
	
	static class Line
	{
		final int revision;
		Migration migration = null;
		byte[] logRaw = null;
		String logString = null;
		TreeMap<String, String> logProperties = null;
		boolean current = false;
		
		Line(final int revision)
		{
			this.revision = revision;
		}
	}

	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request)
	{
		if(model.isMigrationSupported())
		{
			for(final Migration m : model.getMigrations())
				register(m.getRevision()).migration = m;
			
			Map<Integer, byte[]> logsRaw = null;
			try
			{
				logsRaw = model.getMigrationLogs();
			}
			catch(SQLRuntimeException e)
			{
				e.printStackTrace(); // TODO show error in page together with declared migrations
			}

			if(logsRaw!=null)
			{
				try
				{
					for(final Integer revision : logsRaw.keySet())
					{
						final Line line = register(revision);
						line.logRaw = logsRaw.get(revision);
						final byte[] infoBytes = logsRaw.get(revision);
						final Properties infoProperties = Migration.parse(infoBytes);
						if(infoProperties!=null)
						{
							final TreeMap<String, String> map = new TreeMap<String, String>();
							for(final Map.Entry<Object, Object> entry : infoProperties.entrySet())
								map.put((String)entry.getKey(), (String)entry.getValue());
							line.logProperties = map;
							continue;
						}
						line.logString = new String(infoBytes, "latin1");
					}
				}
				catch(UnsupportedEncodingException e)
				{
					throw new RuntimeException(e);
				}
			}
			
			register(model.getMigrationRevision()).current = true;
			
			final ArrayList<Line> lineList = new ArrayList<Line>(lines.values());
			Collections.reverse(lineList);
			Migration_Jspm.writeBody(out, lineList);
		}
		else
		{
			Migration_Jspm.writeBodyDisabled(out);
		}
	}
}
