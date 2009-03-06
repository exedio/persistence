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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cope.Revision;
import com.exedio.cops.Pageable;
import com.exedio.cops.Pager;
import com.exedio.dsmf.SQLRuntimeException;

final class RevisionCop extends ConsoleCop implements Pageable
{
	private static final Pager.Config PAGER_CONFIG = new Pager.Config(10, 20, 50, 100, 200, 500);
	
	final Pager pager;
	
	private RevisionCop(final Args args, final Pager pager)
	{
		super(TAB_REVISION, "revision", args);
		this.pager = pager;
		
		pager.addParameters(this);
	}
	
	RevisionCop(final Args args)
	{
		this(args, PAGER_CONFIG.newPager());
	}
	
	RevisionCop(final Args args, final HttpServletRequest request)
	{
		this(args, PAGER_CONFIG.newPager(request));
	}

	@Override
	protected RevisionCop newArgs(final Args args)
	{
		return new RevisionCop(args, pager);
	}
	
	public Pager getPager()
	{
		return pager;
	}
	
	public RevisionCop toPage(final Pager pager)
	{
		return new RevisionCop(args, pager);
	}
	
	@Override
	void writeHead(final PrintStream out)
	{
		Revision_Jspm.writeHead(out);
	}
	
	private static RevisionLine register(final TreeMap<Integer, RevisionLine> lines, final int revision)
	{
		RevisionLine result = lines.get(revision);
		if(result==null)
		{
			result = new RevisionLine(revision);
			lines.put(revision, result);
		}
		return result;
	}
	
	@Override
	final void writeBody(
			final PrintStream out,
			final Model model,
			final HttpServletRequest request,
			final History history)
	{
		if(!model.isRevisionEnabled())
		{
			Revision_Jspm.writeBodyDisabled(out);
			return;
		}
		
		final TreeMap<Integer, RevisionLine> lines = new TreeMap<Integer, RevisionLine>();
		
		register(lines, model.getRevisionNumber()).setCurrent();
		for(final Revision m : model.getRevisions())
			register(lines, m.getNumber()).setRevision(m);
		
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
			for(final Integer number : logsRaw.keySet())
				register(lines, number).setInfo(logsRaw.get(number));
		}
		
		final ArrayList<RevisionLine> lineList = new ArrayList<RevisionLine>(lines.values());
		Collections.reverse(lineList);
		
		final int offset = pager.getOffset();
		final List<RevisionLine> lineListLimited =
			lineList.subList(offset, Math.min(offset + pager.getLimit(), lineList.size()));
		
		pager.init(lineListLimited.size(), lineList.size());
		
		Revision_Jspm.writeBody(out, this, lineListLimited);
	}
}
