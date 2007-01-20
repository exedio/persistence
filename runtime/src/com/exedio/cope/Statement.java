/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import gnu.trove.TIntArrayList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public final class Statement
{
	private final Database database;
	final StringBuffer text = new StringBuffer();
	final ArrayList<Object> parameters;
	final TC tc;
	private final HashMap<JoinTable, JoinTable> joinTables;
	private final HashSet<Table> ambiguousTables;
	private final boolean qualifyTable;
	final TIntArrayList columnTypes;
	
	Statement(final Database database, final boolean qualifyTable)
	{
		if(database==null)
			throw new NullPointerException();

		this.database = database;
		this.parameters = database.prepare ? new ArrayList<Object>() : null;
		this.tc = null;
		this.joinTables = null;
		this.ambiguousTables = null;
		this.qualifyTable = qualifyTable;
		this.columnTypes = database.dialect.isDefiningColumnTypes() ? new TIntArrayList() : null;
	}

	Statement(final Database database, final Query<? extends Object> query)
	{
		if(database==null)
			throw new NullPointerException();

		this.database = database;
		this.parameters = database.prepare ? new ArrayList<Object>() : null;
		
		this.tc = query.check();
		
		// TODO: implementation is far from optimal
		// TODO: do all the rest in this constructor with TC
		
		final ArrayList<JoinType> joinTypes = new ArrayList<JoinType>();
		
		joinTypes.add(new JoinType(null, query.type));
		for(final Join join : query.getJoins())
			joinTypes.add(new JoinType(join, join.type));

		final HashMap<Table, Object> tableToJoinTables = new HashMap<Table, Object>();
		this.joinTables = new HashMap<JoinTable, JoinTable>();
		for(final JoinType joinType : joinTypes)
		{
			for(Type type = joinType.type; type!=null; type=type.supertype)
			{
				final Table table = type.getTable();
				final Object previous = tableToJoinTables.get(table);
				final JoinTable current = new JoinTable(joinType.join, table);
				if(joinTables.put(current, current)!=null)
					assert false;
				if(previous==null)
					tableToJoinTables.put(table, current);
				else if(previous instanceof JoinTable)
				{
					assert table==((JoinTable)previous).table;

					final ArrayList<JoinTable> list = new ArrayList<JoinTable>(2);
					list.add((JoinTable)previous);
					list.add(current);
					tableToJoinTables.put(table, list);
				}
				else
				{
					castJoinTable(previous).add(current);
				}
			}
		}
		
		HashSet<Table> ambiguousTables = null;
		
		for(final Map.Entry<Table, Object> entry : tableToJoinTables.entrySet())
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
		this.columnTypes = database.dialect.isDefiningColumnTypes() ? new TIntArrayList() : null;
		this.ambiguousTables = ambiguousTables;
	}
	
	@SuppressWarnings("unchecked") // OK: tableToJoinTables contains both JoinTable and List<JoinTable>
	private static final ArrayList<JoinTable> castJoinTable(final Object o)
	{
		return (ArrayList<JoinTable>)o;
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
	
	@SuppressWarnings("deprecation") // OK: Selectable.append is for internal use within COPE only
	public Statement append(final Selectable select, final Join join)
	{
		select.append(this, join);
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
		
	/**
	 * Check correctness of type column
	 * If type column is inconsistent,
	 * the database will query will affect no rows.
	 * Within a SELECT query the result set handler will fail,
	 * because the result is empty.
	 * Within a UPDATE, INSERT or DELETE
	 * the command will return "0 rows affected"
	 * and executeSQLUpdate will fail.
	 */
	Statement appendTypeCheck(final Table table, final Type type)
	{
		final StringColumn column = table.typeColumn;
		if(column!=null)
		{
			append(" and ").
			append(column).
			append('=').
			appendParameter(type.id);
		}
			
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
		
	@SuppressWarnings("deprecation") // OK: Function.appendParameter is for internal use within COPE only
	<E> Statement appendParameter(final Function<E> function, final E value)
	{
		function.appendParameter(this, value);
		return this;
	}
	
	private static final byte[] toArray(final InputStream source, final DataField field, final Item exceptionItem) throws IOException
	{
		final ByteArrayOutputStream target = new ByteArrayOutputStream();
		field.copy(source, target, exceptionItem);
		source.close();
		final byte[] result = target.toByteArray();
		target.close();
		return result;
	}
	
	Statement appendParameterBlob(final InputStream data, final DataField field, final Item exceptionItem) throws IOException
	{
		if(parameters==null)
		{
			this.text.append('\'');
			field.copyAsHex(data, this.text, exceptionItem);
			this.text.append('\'');
		}
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(toArray(data, field, exceptionItem)); // TODO
		}
		return this;
	}
	
	Statement appendParameterBlob(final byte[] data)
	{
		if(parameters==null)
		{
			this.text.append('\'');
			DataField.appendAsHex(data, data.length, this.text);
			this.text.append('\'');
		}
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(data);
		}
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

	@SuppressWarnings("deprecation") // OK: Selectable.getTypeForDefiningColumn is for internal use within COPE only
	Statement defineColumn(final Selectable select)
	{
		if(columnTypes!=null)
			columnTypes.add(select.getTypeForDefiningColumn());
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
		
	Statement defineColumnString()
	{
		if(columnTypes!=null)
			columnTypes.add(StringColumn.JDBC_TYPE);
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
		final Type supertype = type.supertype;
		final Table table = type.getTable();

		ArrayList<Table> superTables = null;
		if(supertype!=null)
		{
			for(Type iType = supertype; iType!=null; iType=iType.supertype)
			{
				final Table iTable = iType.getTable();
				if(tc.containsTable(join, iTable))
				{
					if(superTables==null)
						superTables = new ArrayList<Table>();
					
					superTables.add(iTable);
				}
			}
		}

		if(superTables!=null)
			append('(');

		appendTableDefinition(join, table);

		if(superTables!=null)
		{
			for(final Table iTable : superTables)
			{
				append(" join ");
				appendTableDefinition(join, iTable);
				append(" on ");
				append(iTable.primaryKey, join);
				append('=');
				append(table.primaryKey, join);
			}
			append(')');
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
				throw new IllegalArgumentException("feature " + exceptionColumn + " is ambiguous, use Function#bind (deprecated)"); // TODO replace by assertion, once BadQueryTest works
			return table.protectedID;
		}
	}
	
	static final StringColumn assertTypeColumn(final StringColumn tc, final Type t)
	{
		if(tc==null)
			throw new IllegalArgumentException("type " + t + " has no subtypes, therefore a TypeInCondition makes no sense");
		else
			return tc;
	}
}
