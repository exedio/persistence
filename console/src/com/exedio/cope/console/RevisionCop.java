/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Revision;
import com.exedio.cope.Model;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;
import com.exedio.dsmf.SQLRuntimeException;

final class RevisionCop extends ConsoleCop implements Pageable
{
	private static final int LIMIT_DEFAULT = 10;
	
	final Pager pager;
	
	private RevisionCop(final Pager pager)
	{
		super(TAB_REVISION, "revision");
		this.pager = pager;
		
		pager.addParameters(this);
	}
	
	RevisionCop()
	{
		this(new Pager(LIMIT_DEFAULT));
	}
	
	RevisionCop(final HttpServletRequest request)
	{
		this(Pager.newPager(request, LIMIT_DEFAULT));
	}
	
	public Pager getPager()
	{
		return pager;
	}
	
	public RevisionCop toPage(final Pager pager)
	{
		return new RevisionCop(pager);
	}
	
	@Override
	void writeHead(final PrintStream out)
	{
		Revision_Jspm.writeHead(out);
	}
	
	private Line register(final TreeMap<Integer, Line> lines, final int revision)
	{
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
		final int number;
		Revision revision = null;
		byte[] logRaw = null;
		String logString = null;
		TreeMap<String, String> logProperties = null;
		boolean current = false;
		
		Line(final int number)
		{
			this.number = number;
		}
	}

	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request, final boolean historyAvailable, final boolean historyModelShown, final boolean historyRunning)
	{
		if(model.isRevisionEnabled())
		{
			final TreeMap<Integer, Line> lines = new TreeMap<Integer, Line>();
			
			for(final Revision m : model.getRevisions())
				register(lines, m.getNumber()).revision = m;
			
			Map<Integer, byte[]> logsRaw = null;
			try
			{
				logsRaw = model.getRevisionLogs();
			}
			catch(SQLRuntimeException e)
			{
				e.printStackTrace(); // TODO show error in page together with declared revisions
			}

			if(logsRaw!=null)
			{
				try
				{
					for(final Integer revision : logsRaw.keySet())
					{
						final Line line = register(lines, revision);
						line.logRaw = logsRaw.get(revision);
						final byte[] infoBytes = logsRaw.get(revision);
						line.logString = new String(infoBytes, "latin1");
						final Properties infoProperties = Revision.parse(infoBytes);
						if(infoProperties!=null)
						{
							final TreeMap<String, String> map = new TreeMap<String, String>();
							for(final Map.Entry<Object, Object> entry : infoProperties.entrySet())
								map.put((String)entry.getKey(), (String)entry.getValue());
							line.logProperties = map;
						}
					}
				}
				catch(UnsupportedEncodingException e)
				{
					throw new RuntimeException(e);
				}
			}
			
			register(lines, model.getRevisionNumber()).current = true;
			
			final ArrayList<Line> lineList = new ArrayList<Line>(lines.values());
			Collections.reverse(lineList);
			
			final int offset = pager.getOffset();
			final List<Line> lineListLimited =
				lineList.subList(offset, Math.min(offset + pager.getLimit(), lineList.size()));
			
			pager.init(lineListLimited.size(), lineList.size());
			
			Revision_Jspm.writeBody(out, this, lineListLimited);
		}
		else
		{
			Revision_Jspm.writeBodyDisabled(out);
		}
	}
}
