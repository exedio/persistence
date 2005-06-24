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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class ComputedFunction extends TypeComponent implements Function
{
	private final Function[] sources;
	private final List sourceList;
	private final String[] sqlFragments;
	private final String functionName;
	final int jdbcType;
	final Type sourceType;

	public ComputedFunction(final Function[] sources,
									final String[] sqlFragments,
									final String functionName,
									final int jdbcType)
	{
		this.sources = sources;
		this.sourceList = Collections.unmodifiableList(Arrays.asList(sources));
		this.sqlFragments = sqlFragments;
		if(sources.length+1!=sqlFragments.length)
			throw new RuntimeException("length "+sources.length+" "+sqlFragments.length);
		this.functionName = functionName;
		this.jdbcType = jdbcType;
		this.sourceType = sources[0].getTypeIfInitialized();
	}
	
	public final List getSources()
	{
		return sourceList;
	}

	public abstract Object mapJava(Object[] sourceValues);

	abstract Object load(ResultSet resultSet, int columnIndex) throws SQLException;

	abstract Object surface2Database(Object value);
	
	public final void append(final Statement bf)
	{
		for(int i = 0; i<sources.length; i++)
		{
			bf.append(sqlFragments[i]).
				append(sources[i]);
		}
		bf.append(sqlFragments[sqlFragments.length-1]);
	}

	public final String toString()
	{
		final StringBuffer buf = new StringBuffer(functionName);
		buf.append('(');
		for(int i = 0; i<sources.length; i++)
		{
			if(i>0)
				buf.append(',');
			buf.append(sources[i].getName());
		}
		buf.append(')');
		
		return buf.toString();
	}
	
	// second initialization phase ---------------------------------------------------

	public final void initialize(final Type type, final String name)
	{
		if(sourceType!=null && type!=sourceType)
			throw new RuntimeException();
			
		super.initialize(type, name);

		type.register(this);
	}
	
	public final Type getType()
	{
		return (sourceType!=null) ? sourceType : super.getType();
	}
	
	public final Type getTypeIfInitialized()
	{
		return (sourceType!=null) ? sourceType : super.getTypeIfInitialized();
	}
	
}
