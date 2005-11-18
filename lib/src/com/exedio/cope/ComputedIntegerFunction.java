/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.function.SumFunction;
import com.exedio.cope.search.EqualCondition;
import com.exedio.cope.search.NotEqualCondition;

public abstract class ComputedIntegerFunction
	extends ComputedFunction
	implements IntegerFunction
{
	public ComputedIntegerFunction(
			final Function[] sources,
			final String[] sqlFragments,
			final String functionName)
	{
		super(sources, sqlFragments, functionName, IntegerColumn.JDBC_TYPE_INT);
	}

	final Object load(final ResultSet resultSet, final int columnIndex)
	throws SQLException
	{
		final Object loadedInteger = resultSet.getObject(columnIndex);
		//System.out.println("ComputedIntegerFunction.load "+functionName+" "+loadedInteger+" "+(loadedInteger==null?"null":loadedInteger.getClass().getName()));
		if(loadedInteger!=null)
		{
			if(loadedInteger instanceof BigDecimal)
				return new Integer(((BigDecimal)loadedInteger).intValue());
			else if(loadedInteger instanceof Long)
				return new Integer(((Long)loadedInteger).intValue());
			else
				return (Integer)loadedInteger;
		}
		else
			return null;
	}

	final String surface2Database(final Object value)
	{
		if(value==null)
			return "NULL";
		else
			return ((Integer)value).toString();
	}
	
	final void surface2DatabasePrepared(final Statement bf, final Object value)
	{
		bf.appendParameter(((Integer)value).intValue());
	}
	
	public final Integer get(final Item item)
	{
		return (Integer)item.get(this);
	}
	
	public final EqualCondition equal(final Integer value)
	{
		return new EqualCondition(null, this, value);
	}
	
	public final EqualCondition equal(final int value)
	{
		return new EqualCondition(null, this, new Integer(value));
	}
	
	public final NotEqualCondition notEqual(final Integer value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final NotEqualCondition notEqual(final int value)
	{
		return new NotEqualCondition(this, new Integer(value));
	}
	
	public final SumFunction sum(final IntegerFunction other)
	{
		return new SumFunction(new IntegerFunction[]{this, other});
	}

}
