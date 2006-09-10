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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import bak.pcj.list.IntArrayList;

final class Statement
{
	private final Database database;
	final StringBuffer text = new StringBuffer();
	final ArrayList<Object> parameters;
	private final HashMap<JoinTable, JoinTable> joinTables;
	private final HashSet<Table> ambiguousTables;
	private final boolean qualifyTable;
	final IntArrayList columnTypes;
	
	Statement(final Database database, final boolean prepare, final boolean qualifyTable, final boolean defineColumnTypes)
	{
		if(database==null)
			throw new NullPointerException();

		this.database = database;
		this.parameters = prepare ? new ArrayList<Object>() : null;
		this.joinTables = null;
		this.ambiguousTables = null;
		this.qualifyTable = qualifyTable;
		this.columnTypes = defineColumnTypes ? new IntArrayList() : null;
	}

	Statement(final Database database, final boolean prepare, final Query<? extends Object> query, final boolean defineColumnTypes)
	{
		if(database==null)
			throw new NullPointerException();

		this.database = database;
		this.parameters = prepare ? new ArrayList<Object>() : null;
		
		// TODO: implementation is far from optimal
		// TODO: all tables for each type are joined, also tables with no columns used
		
		final ArrayList<JoinType> types = new ArrayList<JoinType>();
		
		types.add(new JoinType(null, query.type));
		for(final Join join : query.getJoins())
			types.add(new JoinType(join, join.type));

		final HashMap<Table, Object> joinTypeTableByTable = new HashMap<Table, Object>();
		this.joinTables = new HashMap<JoinTable, JoinTable>();
		for(Iterator i = types.iterator(); i.hasNext(); )
		{
			final JoinType joinType = (JoinType)i.next();
			for(Type type = joinType.type; type!=null; type=type.supertype)
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

					final ArrayList<JoinTable> list = new ArrayList<JoinTable>(2);
					list.add((JoinTable)previous);
					list.add(current);
					joinTypeTableByTable.put(table, list);
				}
				else
				{
					castJoinTable(previous).add(current);
				}
			}
		}
		
		HashSet<Table> ambiguousTables = null;
		
		for(final Map.Entry<Table, Object> entry : joinTypeTableByTable.entrySet())
		{
			final Table table = entry.getKey();
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
				if(ambiguousTables==null)
					ambiguousTables = new HashSet<Table>();
				ambiguousTables.add(table);
			}
		}
		//System.out.println("-------"+joinTables.keySet().toString());
		
		this.qualifyTable = joinTables.size()>1;
		this.columnTypes = defineColumnTypes ? new IntArrayList() : null;
		this.ambiguousTables = ambiguousTables;
	}
	
	@SuppressWarnings("unchecked") // OK: joinTypeTableByTable contains both JoinTable and List<JoinTable>
	private static final ArrayList<JoinTable> castJoinTable(final Object o)
	{
		return (ArrayList<JoinTable>)o;
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
	
	Statement append(final Selectable function, final Join join)
	{
		function.append(this, join);
		return this;
	}

	Statement appendPK(final Type type, final Join join)
	{
		return append(type.getTable().primaryKey, join);
	}
		
	Statement append(final Table table)
	{
		this.text.append(table.protectedID);
			
		return this;
	}
		
	Statement append(final Column column)
	{
		return append(column, (Join)null);
	}
	
	Statement append(final Column column, final Join join)
	{
		if(qualifyTable)
		{
			this.text.
				append(getName(join, column.table, column)).
				append('.');
		}
		this.text.
			append(column.protectedID);
			
		return this;
	}
		
	<E> Statement appendParameter(final Function<E> function, final E value)
	{
		function.appendParameter(this, value);
		return this;
	}
	
	private static final byte[] toArray(final InputStream source, final DataField attribute, final Item item) throws IOException
	{
		final ByteArrayOutputStream target = new ByteArrayOutputStream();
		attribute.copy(source, target, item);
		source.close();
		final byte[] result = target.toByteArray();
		target.close();
		return result;
	}
	
	Statement appendParameterBlob(final BlobColumn function, final InputStream data, final DataField attribute, final Item item) throws IOException
	{
		this.text.append(QUESTION_MARK);
		this.parameters.add(toArray(data, attribute, item)); // TODO
		return this;
	}
	
	Statement appendParameterBlob(final BlobColumn function, final byte[] data)
	{
		this.text.append(QUESTION_MARK);
		this.parameters.add(data);
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
			this.parameters.add(Integer.valueOf(value));
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
	
	Statement defineColumn(final Selectable selectable)
	{
		if(columnTypes!=null)
			columnTypes.add(selectable.getTypeForDefiningColumn());
		return this;
	}
		
	Statement defineColumn(final Column column)
	{
		if(columnTypes!=null)
			columnTypes.add(column.typeForDefiningColumn);
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

	@Override
	public String toString()
	{
		if(parameters==null)
			return text.toString();
		else
		{
			final String text = this.text.toString();
			final StringBuffer result = new StringBuffer();
			
			int lastPos = 0;
			final Iterator pi = parameters.iterator();
			for(int pos = text.indexOf(QUESTION_MARK); pos>=0&&pi.hasNext(); pos = text.indexOf(QUESTION_MARK, lastPos))
			{
				result.append(text.substring(lastPos, pos));
				result.append(QUESTION_MARK);
				result.append(pi.next());
				result.append(QUESTION_MARK);
				lastPos = pos+1;
			}
			result.append(text.substring(lastPos));
			
			return result.toString();
		}
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
		
		@Override
		public int hashCode()
		{
			return (join==null ? 1982763 : System.identityHashCode(join)) ^ System.identityHashCode(table);
		}
		
		@Override
		public boolean equals(final Object other)
		{
			final JoinTable o = (JoinTable)other;
			return join==o.join && table==o.table;
		}
		
		@Override
		public String toString()
		{
			return (join==null?"-":join.type.id) + '/' + table.id + '>' + (alias==null?"-":alias);
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
	
	Statement appendTableDefinition(final Join join, final Table table)
	{
		append(table.protectedID);
		final String alias = getAlias(join, table);
		if(alias!=null)
		{
			append(' ').
			append(alias);
		}
		return this;
	}
	
	void appendTypeDefinition(final Join join, final Type type)
	{
		for(Type currentType = type.supertype; currentType!=null; currentType=currentType.supertype)
		{
			final Table table = currentType.getTable();
			append(" inner join ");
			appendTableDefinition(join, table);
			append(" on ");
			append(table.primaryKey, join);
			append('=');
			append(type.getTable().primaryKey, join);
		}
	}
	
	private JoinTable getJoinTable(final Join join, final Table table)
	{
		return joinTables!=null ? joinTables.get(new JoinTable(join, table)) : null;
	}

	private String getAlias(final Join join, final Table table)
	{
		final JoinTable jt = getJoinTable(join, table);
		return (jt!=null) ? jt.alias : null;
	}

	private String getName(final Join join, final Table table, final Column exceptionColumn)
	{
		final JoinTable jt = getJoinTable(join, table);
		if(jt!=null && jt.alias!=null)
			return jt.alias;
		else
		{
			if(ambiguousTables!=null && ambiguousTables.contains(table))
				throw new RuntimeException("feature " + exceptionColumn + " is ambiguous, use JoinedFunction");
			return table.protectedID;
		}
	}
	
	static final StringColumn assertTypeColumn(final StringColumn tc, final Type t)
	{
		if(tc==null)
			throw new RuntimeException("type " + t + " has no subtypes, therefore a TypeInCondition makes no sense");
		else
			return tc;
	}

}
