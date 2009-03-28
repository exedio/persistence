/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

public abstract class Aggregate<E> implements Selectable<E>
{
	/**
	 * I'm not yet sure, whether the type of the aggregate
	 * always equals the type of the source, but it does for
	 * SUM, MIN, MAX, AVG.
	 */
	final Function<E> source;
	private final String name;
	private final String sqlPrefix;

	public Aggregate(final Function<E> source, final String name, final String sqlName)
	{
		if(source==null)
			throw new NullPointerException("source");
		if(name==null)
			throw new NullPointerException("name");
		if(sqlName==null)
			throw new NullPointerException("sqlName");
		
		this.source = source;
		this.name = name;
		this.sqlPrefix = sqlName + '(';
	}
	
	public final Function<E> getSource()
	{
		return source;
	}

	public final String getName()
	{
		return name;
	}

	public final Type<? extends Item> getType()
	{
		return source.getType();
	}
	
	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	public final void check(final TC tc, final Join join)
	{
		source.check(tc, join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		bf.append(sqlPrefix).
			append(source, join).
			append(')');
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	public final void appendSelect(final Statement bf, final Join join, final Holder<Column> columnHolder, final Holder<Type> typeHolder)
	{
		bf.append(sqlPrefix).
			appendSelect(source, join, columnHolder, typeHolder).
			append(')');
	}
	
	@Override
	public final boolean equals(final Object other)
	{
		if(!(other instanceof Aggregate))
			return false;
		
		final Aggregate a = (Aggregate)other;
		
		return name.equals(a.name) && source.equals(a.source);
	}
	
	@Override
	public final int hashCode()
	{
		return name.hashCode() ^ source.hashCode();
	}
	
	@Override
	public final String toString()
	{
		return name + '(' + source + ')';
	}

	public final void toString(final StringBuilder bf, final Type defaultType)
	{
		bf.append(name).
			append('(');
		source.toString(bf, defaultType);
		bf.append(')');
	}
}
