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

package com.exedio.cope.pattern;

public final class Range<E>
{
	final E from;
	final E to;

	public Range(E from, E to)
	{
		if(from==null)
			throw new NullPointerException("optional from not yet implemented");
		if(to==null)
			throw new NullPointerException("optional to not yet implemented");
		
		this.from = from;
		this.to = to;
	}
	
	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof Range))
			return false;
		
		final Range o = (Range)other;
		return from.equals(o.from) && to.equals(o.to);
	}
	
	@Override
	public int hashCode()
	{
		return from.hashCode() ^ (to.hashCode() << 2);
	}
}
