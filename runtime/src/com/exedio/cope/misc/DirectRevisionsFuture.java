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

package com.exedio.cope.misc;

import com.exedio.cope.EnvironmentInfo;
import com.exedio.cope.Revisions;

/**
 * @deprecated since {@link com.exedio.cope.RevisionsFuture} has been deprecated.
 */
@Deprecated
public final class DirectRevisionsFuture implements com.exedio.cope.RevisionsFuture
{
	public static DirectRevisionsFuture make(final Revisions revisions)
	{
		return
			revisions!=null
			? new DirectRevisionsFuture(revisions)
			: null;
	}


	private final Revisions revisions;

	private DirectRevisionsFuture(final Revisions revisions)
	{
		this.revisions = revisions;
	}

	public Revisions get(final EnvironmentInfo environment)
	{
		if(environment==null)
			throw new NullPointerException();

		return revisions;
	}
}
