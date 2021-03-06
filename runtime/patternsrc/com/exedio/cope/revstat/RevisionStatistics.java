/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.revstat;

import static com.exedio.cope.util.JobContext.deferOrStopIfRequested;

import com.exedio.cope.Model;
import com.exedio.cope.RevisionInfo;
import com.exedio.cope.RevisionInfoRevise;
import com.exedio.cope.Type;
import com.exedio.cope.TypeSet;
import com.exedio.cope.util.JobContext;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class RevisionStatistics
{
	public static void write(final Model model, final JobContext ctx)
	{
		final TreeMap<Integer, RevisionInfoRevise> revisions = new TreeMap<>();

		for(final Map.Entry<Integer, byte[]> entry : model.getRevisionLogs().entrySet())
		{
			deferOrStopIfRequested(ctx);

			final RevisionInfo info = RevisionInfo.read(entry.getValue());
			if(info instanceof RevisionInfoRevise)
				revisions.put(entry.getKey(), (RevisionInfoRevise)info);
		}

		deferOrStopIfRequested(ctx);

		for(final Map.Entry<Integer, RevisionInfoRevise> entry : revisions.entrySet())
			Revstat.write(model, entry.getKey(), entry.getValue(), ctx);
	}

	/**
	 * @return the result of {@link TypeSet#getExplicitTypes()} for {@link #types}.
	 * @throws IllegalArgumentException if model does not {@link #isContainedIn(Model) contain} RevisionStatistics.
	 */
	public static List<Type<?>> getExplicitTypes(final Model model)
	{
		if(!isContainedIn(model))
			throw new IllegalArgumentException("model does not contain RevisionStatistics");

		return types.getExplicitTypes();
	}

	public static boolean isContainedIn(final Model model)
	{
		return model.contains(RevisionStatistics.types);
	}

	public static final TypeSet types = new TypeSet(Revstat.TYPE, RevstatBody.TYPE);

	private RevisionStatistics()
	{
		// prevent instantiation
	}
}
