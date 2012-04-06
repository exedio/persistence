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

/**
 * Use only as select in query using groupBy
 * <p>
 * Grouping functionality is 'beta' - API may change
 */
public class CountSelectable implements Selectable<Integer>
{
	private static final long serialVersionUID = 1l;

	public Class<Integer> getValueClass()
	{
		return Integer.class;
	}

	@Override
	public SelectType<Integer> getValueType()
	{
		return SimpleSelectType.INTEGER;
	}

	@Override
	public Type<? extends Item> getType()
	{
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public void toString( final StringBuilder bf, final Type defaultType )
	{
		bf.append( "count(*)" );
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void check( final TC tc, final Join join )
	{
		// TODO
		// probably: nothing to do here, since there are no sources
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void append( final Statement bf, final Join join )
	{
		bf.append( "count(*)" );
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void appendSelect( final Statement bf, final Join join )
	{
		bf.append( "count(*)" );
	}

}
