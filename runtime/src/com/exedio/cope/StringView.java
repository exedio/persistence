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

import java.sql.ResultSet;
import java.sql.SQLException;

import com.exedio.cope.function.LengthView;

public abstract class StringView
	extends StaticView
	implements StringFunction
{
	public StringView(
			final Function[] sources,
			final String[] sqlFragments,
			final String functionName)
	{
		super(sources, sqlFragments, functionName, StringColumn.JDBC_TYPE);
	}

	final Object load(final ResultSet resultSet, final int columnIndex)
	throws SQLException
	{
		return resultSet.getString(columnIndex);
	}

	final String surface2Database(final Object value)
	{
		return StringColumn.cacheToDatabaseStatic(value);
	}
	
	final void surface2DatabasePrepared(final Statement bf, final Object value)
	{
		bf.appendParameter((String)value);
	}
	
	public final String get(final Item item)
	{
		return (String)getObject(item);
	}
	
	public final EqualCondition equal(final String value)
	{
		return new EqualCondition(this, value);
	}
	
	public final EqualCondition equal(final Join join, final String value)
	{
		return new EqualCondition(new JoinedFunction(this, join), value);
	}
	
	public final NotEqualCondition notEqual(final String value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final LikeCondition like(final String value)
	{
		return new LikeCondition(this, value);
	}
	
	public final LengthView length()
	{
		return new LengthView(this);
	}
	
}
