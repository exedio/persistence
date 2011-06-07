/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.misc;

import com.exedio.cope.EnvironmentInfo;
import com.exedio.cope.RevisionSource;
import com.exedio.cope.Revisions;

public final class ImmediateRevisionSource implements RevisionSource
{
	public static ImmediateRevisionSource wrap(final Revisions revisions)
	{
		return
			revisions!=null
			? new ImmediateRevisionSource(revisions)
			: null;
	}


	private final Revisions revisions;

	private ImmediateRevisionSource(final Revisions revisions)
	{
		this.revisions = revisions;
	}

	public Revisions get(final EnvironmentInfo environment)
	{
		return revisions;
	}
}
