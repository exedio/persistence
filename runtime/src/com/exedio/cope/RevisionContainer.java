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

package com.exedio.cope;

final class RevisionContainer
{
	static RevisionContainer wrap(final Model model, final RevisionSource source)
	{
		return source!=null ? new RevisionContainer(model, source) : null;
	}


	private final Model model;
	private final RevisionSource source;

	private Revisions target = null;
	private final Object targetLock = new Object();

	private RevisionContainer(final Model model, final RevisionSource source)
	{
		this.model = model;
		this.source = source;

		assert model!=null;
		assert source!=null;
	}

	Revisions get()
	{
		synchronized(targetLock)
		{
			if(target==null)
				target = source.get(model);

			return target;
		}
	}
}
