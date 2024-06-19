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

package com.exedio.cope;

import java.util.function.Consumer;

/**
 * Use only as select in query using groupBy
 * <p>
 * Grouping functionality is 'beta' - API may change
 *
 * @see Function#count()
 */
public final class Count implements NumberFunction<Integer>
{
	private static final long serialVersionUID = 1l;

	private final Function<?> source;

	public Count()
	{
		this(null);
	}

	Count(final Function<?> source)
	{
		this.source = source;
	}


	@Override
	public Class<Integer> getValueClass()
	{
		return Integer.class;
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<Integer> getValueType()
	{
		return SimpleSelectType.INTEGER;
	}

	@Override
	public Count bind(final Join join)
	{
		return new Count(source.bind(join));
	}

	@Override
	public Type<?> getType()
	{
		if (source==null)
			throw new UnsupportedOperationException( "Not supported yet." );
		else
			return source.getType();
	}

	@Override
	public void toString( final StringBuilder bf, final Type<?> defaultType )
	{
		bf.append("count(");
		if (source==null)
			bf.append('*');
		else
			source.toString(bf, defaultType);
		bf.append(')');
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void check(@SuppressWarnings("ClassEscapesDefinedScope") final TC tc, final Join join)
	{
		// TODO
		// probably: nothing to do here, since there are no sources
	}

	@Override
	public void forEachFieldCovered(final Consumer<Field<?>> action)
	{
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void append(@SuppressWarnings("ClassEscapesDefinedScope") final Statement bf, final Join join)
	{
		bf.append("COUNT(");
		if (source==null)
			bf.append('*');
		else
			source.append(bf, join);
		bf.append(')');
	}


	@Override
	public void requireSupportForGet() throws UnsupportedGetException
	{
		throw new UnsupportedGetException(this);
	}

	@Override
	public Integer get(final Item item) throws UnsupportedGetException
	{
		throw new UnsupportedGetException(this);
	}
}
