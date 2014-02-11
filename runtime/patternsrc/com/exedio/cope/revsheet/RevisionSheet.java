/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.revsheet;

import com.exedio.cope.Model;
import com.exedio.cope.RevisionInfo;
import com.exedio.cope.RevisionInfoRevise;
import com.exedio.cope.TypeSet;
import com.exedio.cope.util.JobContext;
import java.util.Map;
import java.util.TreeMap;

public final class RevisionSheet
{
	public static void write(final Model model, final JobContext ctx)
	{
		if(model.getRevisions()==null)
			return;

		final TreeMap<Integer, RevisionInfoRevise> revisions = new TreeMap<Integer, RevisionInfoRevise>();

		for(final Map.Entry<Integer, byte[]> e : model.getRevisionLogs().entrySet())
		{
			final RevisionInfo info = RevisionInfo.read(e.getValue());
			if(info!=null && info instanceof RevisionInfoRevise)
				revisions.put(info.getNumber(), (RevisionInfoRevise)info);
		}

		ctx.stopIfRequested();

		for(final Map.Entry<Integer, RevisionInfoRevise> entry : revisions.entrySet())
		{
			ctx.stopIfRequested();
			if(CopeRevisionSheet.write(model, entry.getKey(), entry.getValue()))
				ctx.incrementProgress();
		}
	}

	public static final TypeSet types = new TypeSet(CopeRevisionSheet.TYPE, CopeRevisionSheetBody.TYPE);

	private RevisionSheet()
	{
		// prevent instantiation
	}
}
