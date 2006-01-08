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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bak.pcj.list.IntArrayList;

final class Statement
{
	private final Database database;
	final StringBuffer text = new StringBuffer();
	final ArrayList parameters;
	private final HashMap joinTables;
	private final boolean qualifyTable;
	final IntArrayList columnTypes;
	
	Statement(final Database database, final boolean prepare, final boolean qualifyTable, final boolean defineColumnTypes)
	{
		if(database==null)
			throw new NullPointerException();

		this.database = database;
		this.parameters = prepare ? new ArrayList() : null;
		this.joinTables = null;
		this.qualifyTable = qualifyTable;
		this.columnTypes = defineColumnTypes ? new IntArrayList() : null;
	}

	Statement(final Database database, final boolean prepare, final Query query, final boolean defineColumnTypes)
	{
		if(database==null)
			throw new NullPointerException();

		this.database = database;
		this.parameters = prepare ? new ArrayList() : null;
		
		// TODO: implementation is far from optimal
		// TODO: all tables for each type are joined, also tables with no columns used
		
		final ArrayList types = new ArrayList();
		
		types.add(new JoinType(null, query.type));
		for(Iterator i = query.getJoins().iterator(); i.hasNext(); )
		{
			final Join join = (Join)i.next();
			types.add(new JoinType(join, join.type));
		}

		final HashMap joinTypeTableByTable = new HashMap();
		this.joinTables = new HashMap();
		for(Iterator i = types.iterator(); i.hasNext(); )
		{
			final JoinType joinType = (JoinType)i.next();
			for(Type type = joinType.type; type!=null; type=type.getSupertype())
			{
				final Table table = type.getTable();
				final Object previous = joinTypeTableByTable.get(table);
				final JoinTable current = new JoinTable(joinType.join, table);
				if(joinTables.put(current, current)!=null)
					throw new RuntimeException();
				if(previous==null)
					joinTypeTableByTable.put(table, current);
				else if(previous instanceof JoinTable)
				{
					if(table!=((JoinTable)previous).table)
						throw new RuntimeException();

					final ArrayList list = new ArrayList(2);
					list.add(previous);
					list.add(current);
					joinTypeTableByTable.put(table, list);
				}
				else
				{
					((ArrayList)previous).add(current);
				}
			}
		}
		
		for(Iterator i = joinTypeTableByTable.entrySet().iterator(); i.hasNext(); )
		{
			final Map.Entry entry = (Map.Entry)i.next();
			final Table table = (Table)entry.getKey();
			final Object value = entry.getValue();
			if(value instanceof ArrayList)
			{
				final ArrayList list = (ArrayList)value;
				int aliasNumber = 0;
				for(Iterator j = list.iterator(); j.hasNext(); )
				{
					final JoinTable joinType = (JoinTable)j.next();
					joinType.alias = table.id + (aliasNumber++);
				}
			}
		}
		//System.out.println("-------"+joinTables.keySet().toString());
		
		this.qualifyTable = joinTables.size()>1;
		this.columnTypes = defineColumnTypes ? new IntArrayList() : null;
	}

	Statement append(final String text)
	{
		this.text.append(text);
		return this;
	}
		
	Statement append(final char text)
	{
		this.text.append(text);
		return this;
	}
	
	Statement append(final Function function, final Join join)
	{
		function.append(this, join);
		return this;
	}

	Statement appendPK(final Type type, final Join join)
	{
		return append(type.getTable().primaryKey, join);
	}
		
	Statement append(final Column column, final Join join)
	{
		if(qualifyTable)
		{
			this.text.
				append(join!=null ? getName(join, column.table) : column.table.protectedID).
				append('.');
		}
		this.text.
			append(column.protectedID);
			
		return this;
	}
		
	Statement appendParameter(final Function function, final Object value)
	{
		function.appendParameter(this, value);
		return this;
	}
	
	private static final byte[] toArray(final InputStream source) throws IOException
	{
		final ByteArrayOutputStream target = new ByteArrayOutputStream();
		DataAttribute.copy(source, target);
		source.close();
		final byte[] result = target.toByteArray();
		target.close();
		return result;
	}
	
	Statement appendParameterBlob(final BlobColumn function, final InputStream data) throws IOException
	{
		this.text.append(QUESTION_MARK);
		this.parameters.add(toArray(data)); // TODO
		return this;
	}
	
	private static final char QUESTION_MARK = '?';
	
	Statement appendParameter(final Column column, final Object value)
	{
		if(parameters==null)
			this.text.append(column.cacheToDatabase(value));
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(column.cacheToDatabasePrepared(value));
		}
		return this;
	}
	
	Statement appendParameter(final int value)
	{
		if(parameters==null)
			this.text.append(Integer.toString(value));
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(new Integer(value));
		}
		return this;
	}
	
	Statement appendParameter(final String value)
	{
		if(parameters==null)
			this.text.append('\'').append(value).append('\'');
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(value);
		}
		return this;
	}
	
	Statement appendParameters(final Statement other)
	{
		if(parameters==null)
			throw new RuntimeException();
		if(other.parameters==null)
			throw new RuntimeException();

		parameters.addAll(other.parameters);
		
		return this;
	}
	
	void appendMatch(final StringFunction function, final String value)
	{
		database.appendMatchClause(this, function, value);
	}
	
	Statement defineColumn(final ComputedFunction function)
	{
		if(columnTypes!=null)
			columnTypes.add(function.jdbcType);
		return this;
	}
		
	Statement defineColumn(final Column column)
	{
		if(columnTypes!=null)
			columnTypes.add(column.jdbcType);
		return this;
	}
		
	Statement defineColumnInteger()
	{
		if(columnTypes!=null)
			columnTypes.add(IntegerColumn.JDBC_TYPE_INT);
		return this;
	}
		
	String getText()
	{
		return text.toString();
	}

	public String toString()
	{
		return text.toString();
	}
	
	// join aliases
	
	private static class JoinTable
	{
		final Join join;
		final Table table;

		String alias = null;
		
		JoinTable(final Join join, final Table table)
		{
			if(table==null)
				throw new NullPointerException();
			
			this.join = join;
			this.table = table;
		}
		
		public int hashCode()
		{
			return (join==null ? 1982763 : System.identityHashCode(join)) ^ System.identityHashCode(table);
		}
		
		public boolean equals(final Object other)
		{
			final JoinTable o = (JoinTable)other;
			return join==o.join && table==o.table;
		}
		
		public String toString()
		{
			return (join==null?"-":join.type.id) + '/' + table.id;
		}
	}
	
	private static class JoinType
	{
		final Join join;
		final Type type;
		
		JoinType(final Join join, final Type type)
		{
			this.join = join;
			this.type = type;
		}
	}
	
	void appendTypeDefinition(final Join join, final Type type)
	{
		boolean first = true;
		for(Type currentType = type; currentType!=null; currentType=currentType.getSupertype())
		{
			if(first)
				first = false;
			else
				append(',');
			
			final Table table = currentType.getTable();
			append(table.protectedID);
			final String alias = getAlias(join, table);
			if(alias!=null)
			{
				append(' ').
				append(alias);
			}
		}
	}
	
	void appendTypeJoinCondition(final String prefix, final Join join, final Type type)
	{
		boolean first = true;
		for(Type currentType = type.getSupertype(); currentType!=null; currentType=currentType.getSupertype())
		{
			if(first)
			{
				append(prefix);
				first = false;
			}
			else
				append(" and ");
			
			final Table table = currentType.getTable();

			append(table.primaryKey, join);
			append('=');
			append(type.getTable().primaryKey, join);
		}
	}
	
	private String getAlias(final Join join, final Table table)
	{
		return ((JoinTable)joinTables.get(new JoinTable(join, table))).alias;
	}

	private String getName(final Join join, final Table table)
	{
		final String alias = getAlias(join, table);
		if(alias!=null)
			return alias;
		else
			return table.protectedID;
	}

}
