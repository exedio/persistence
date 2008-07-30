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

package com.exedio.cope;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.exedio.cope.search.SumAggregate;

public abstract class IntegerView<E extends Number> extends View<E> implements IntegerFunction<E> // TODO rename to NumberView
{
	public IntegerView(final Function<?>[] sources, final String name, final Class<E> valueClass)
	{
		super(sources, name, valueClass);
	}

	@Override
	final Object load(final ResultSet resultSet, final int columnIndex)
	throws SQLException
	{
		final Object loadedInteger = resultSet.getObject(columnIndex);
		//System.out.println("IntegerView.load "+functionName+" "+loadedInteger+" "+(loadedInteger==null?"null":loadedInteger.getClass().getName()));
		if(loadedInteger!=null)
		{
			if(loadedInteger instanceof BigDecimal)
				return Integer.valueOf(((BigDecimal)loadedInteger).intValue());
			else if(loadedInteger instanceof Long)
				return Integer.valueOf(((Long)loadedInteger).intValue());
			else
			{
				assert loadedInteger==null || loadedInteger instanceof Integer;
				return loadedInteger;
			}
		}
		else
			return null;
	}

	@Override
	final String surface2Database(final Object value)
	{
		if(value==null)
			return "NULL";
		else
			return ((Number)value).toString();
	}
	
	@Override
	final void surface2DatabasePrepared(final Statement bf, final Object value)
	{
		bf.appendParameter((Number)value);
	}
	
	// convenience methods for conditions and views ---------------------------------

	@Override
	public final BindIntegerFunction<E> bind(final Join join)
	{
		return new BindIntegerFunction<E>(this, join);
	}
	
	public final PlusView<E> plus(final IntegerFunction<E> other)
	{
		return new PlusView<E>(new IntegerFunction[]{this, other});
	}

	public final SumAggregate<E> sum()
	{
		return new SumAggregate<E>(this);
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated renamed to {@link #plus(IntegerFunction)}.
	 */
	@Deprecated
	public final PlusView<E> sum(final IntegerFunction<E> other)
	{
		return plus(other);
	}
}
