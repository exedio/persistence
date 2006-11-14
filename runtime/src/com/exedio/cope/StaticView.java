/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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


abstract class StaticView<E> extends View<E>
{
	private final Function[] sources;
	private final String[] sqlFragments;
	
	StaticView(
			final Function<?>[] sources,
			final String name,
			final Class<E> valueClass,
			final int typeForDefiningColumn,
			final String[] sqlFragments)
	{
		super(sources, name, valueClass, typeForDefiningColumn);
		this.sources = sources;
		this.sqlFragments = sqlFragments;
		if(sources.length+1!=sqlFragments.length)
			throw new RuntimeException("length "+sources.length+" "+sqlFragments.length);
	}
						
	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated
	public final void check(final TC tc, final Join join)
	{
		for(int i = 0; i<sources.length; i++)
			sources[i].check(tc, join);
	}
	
	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated
	public final void append(final Statement bf, final Join join)
	{
		for(int i = 0; i<sources.length; i++)
		{
			bf.append(sqlFragments[i]).
				append(sources[i], join);
		}
		bf.append(sqlFragments[sqlFragments.length-1]);
	}
}
