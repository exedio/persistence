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

import java.util.HashMap;
import java.util.Iterator;

import bak.pcj.list.IntArrayList;

public final class Statement
{
	final StringBuffer text = new StringBuffer();
	final IntArrayList columnTypes;
		
	Statement(final boolean useDefineColumnTypes)
	{
		columnTypes = useDefineColumnTypes ? new IntArrayList() : null;
	}

	public Statement append(final String text)
	{
		this.text.append(text);
		return this;
	}
		
	public Statement append(final char text)
	{
		this.text.append(text);
		return this;
	}
		
	public Statement append(final int text)
	{
		this.text.append(text);
		return this;
	}
		
	public Statement append(final Object text)
	{
		this.text.append(text);
		return this;
	}
	
	public Statement append(final Function function, final Join join)
	{
		function.append(this, join);
		return this;
	}

	public Statement appendPK(final Type type, final Join join)
	{
		final Table table = type.getTable();
		this.text.
			append(join!=null ? getName(join) : table.protectedID).
			append('.').
			append(table.getPrimaryKey().protectedID);
			
		return this;
	}
		
	public Statement appendValue(final Function function, final Object value)
	{
		if(function instanceof ComputedFunction)
		{
			this.text.append(((ComputedFunction)function).surface2Database(value));
		}
		else
		{
			final ObjectAttribute attribute = (ObjectAttribute)function;
			appendValue(attribute.getColumn(), attribute.surfaceToCache(value));
		}
		return this;
	}
	
	public Statement appendValue(final Column column, final Object value)
	{
		this.text.append(column.cacheToDatabase(value));
		return this;
	}
	
	public Statement defineColumn(final ComputedFunction function)
	{
		if(columnTypes!=null)
			columnTypes.add(function.jdbcType);
		return this;
	}
		
	public Statement defineColumn(final Column column)
	{
		if(columnTypes!=null)
			columnTypes.add(column.jdbcType);
		return this;
	}
		
	public Statement defineColumnInteger()
	{
		if(columnTypes!=null)
			columnTypes.add(IntegerColumn.JDBC_TYPE_INT);
		return this;
	}
		
	public Statement defineColumnString()
	{
		if(columnTypes!=null)
			columnTypes.add(StringColumn.JDBC_TYPE);
		return this;
	}
		
	public Statement defineColumnTimestamp()
	{
		if(columnTypes!=null)
			columnTypes.add(TimestampColumn.JDBC_TYPE);
		return this;
	}
		
	public String getText()
	{
		return text.toString();
	}

	public String toString()
	{
		return text.toString();
	}
	
	// join aliases
	
	private HashMap joinsToAliases;
	private String fromAlias;
	private String fromName;
	
	void setJoinsToAliases(final Query query)
	{
		if(joinsToAliases!=null)
			throw new RuntimeException();
		
		joinsToAliases = new HashMap();
		fromName = query.type.getTable().protectedID;
		if(query.joins==null)
			return;
		
		final HashMap tablesToJoins = new HashMap();
		int aliasNumber = 0;
		for(Iterator i = query.joins.iterator(); i.hasNext(); )
		{
			final Join join = (Join)i.next();
			final Table table = join.type.getTable();
			if(table==null)
				throw new RuntimeException();
			
			//System.out.println("----------------X"+join);
			final Join oldJoin = (Join)tablesToJoins.put(table, join);
			if(oldJoin!=null)
			{
				final String oldAlias = (String)joinsToAliases.get(oldJoin);
				if(oldAlias==null)
					joinsToAliases.put(oldJoin, "alias"+(aliasNumber++));
				joinsToAliases.put(join, "alias"+(aliasNumber++));
			}
		}
		{
			final Table table = query.type.getTable();
			if(table==null)
				throw new RuntimeException();
			
			final Join oldJoin = (Join)tablesToJoins.get(table);
			if(oldJoin!=null)
			{
				final String oldAlias = (String)joinsToAliases.get(oldJoin);
				if(oldAlias==null)
					joinsToAliases.put(oldJoin, "alias"+(aliasNumber++));
				fromName = fromAlias = "alias"+(aliasNumber++);
			}
		}
		//System.out.println("----------------"+joinsToAliases);
	}
	
	String getAlias(final Join join)
	{
		if(join!=null)
			return (String)joinsToAliases.get(join);
		else
			return fromAlias;
	}

	String getName(final Join join)
	{
		if(join!=null)
		{
			final String alias = (String)joinsToAliases.get(join);
			if(alias!=null)
				return alias;
			else
				return join.type.getTable().protectedID;
		}
		else
			return fromName;
	}

}
