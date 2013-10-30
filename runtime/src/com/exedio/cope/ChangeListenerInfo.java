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

package com.exedio.cope;

public final class ChangeListenerInfo
{
	private final int size;
	private final int cleared;
	private final int removed;
	private final int failed;

	ChangeListenerInfo(
			final int size,
			final int cleared,
			final int removed,
			final int failed)
	{
		this.size = size;
		this.cleared = cleared;
		this.removed = removed;
		this.failed  = failed;
	}

	public int getSize()
	{
		return size;
	}

	public int getCleared()
	{
		return cleared;
	}

	public int getRemoved()
	{
		return removed;
	}

	public int getFailed()
	{
		return failed;
	}
}
