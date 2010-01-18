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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class Statement
{
	private final Executor database;
	final StringBuilder text = new StringBuilder();
	final ArrayList<Object> parameters;
	final TC tc;
	private final HashMap<JoinTable, JoinTable> joinTables;
	private final HashSet<Table> ambiguousTables;
	private final boolean qualifyTable;
	
	Statement(final Executor database, final boolean qualifyTable)
	{
		if(database==null)
			throw new NullPointerException();

		this.database = database;
		this.parameters = database.prepare ? new ArrayList<Object>() : null;
		this.tc = null;
		this.joinTables = null;
		this.ambiguousTables = null;
		this.qualifyTable = qualifyTable;
	}

	Statement(final Executor database, final Query<? extends Object> query)
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
		this.ambiguousTables = ambiguousTables;
	}
	
	@SuppressWarnings("unchecked") // OK: tableToJoinTables contains both JoinTable and List<JoinTable>
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
	
	@SuppressWarnings("deprecation") // OK: Selectable.append is for internal use within COPE only
	Statement append(final Selectable select, final Join join)
	{
		select.append(this, join);
		return this;
	}
	
	@SuppressWarnings("deprecation") // OK: Selectable.appendSelect is for internal use within COPE only
	Statement appendSelect(final Selectable<?> select, final Join join, final Holder<Column> columnHolder, final Holder<Type> typeHolder)
	{
		select.appendSelect(this, join, columnHolder, typeHolder);
		return this;
	}

	Statement appendPK(final Type type, final Join join)
	{
		return append(type.getTable().primaryKey, join);
	}
		
	Statement append(final Table table)
	{
		this.text.append(table.quotedID);
			
		return this;
	}
		
	/**
	 * Check correctness of type column.
	 * If type column is inconsistent,
	 * the database statement won't match any row.
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
			append(column.quotedID);
			
		return this;
	}
		
	@SuppressWarnings("deprecation") // OK: Function.appendParameter is for internal use within COPE only
	<E> Statement appendParameter(final Function<E> function, final E value)
	{
		function.appendParameter(this, value);
		return this;
	}
	
	Statement appendParameterBlob(final byte[] data)
	{
		if(data!=null)
		{
			if(parameters==null)
			{
				this.database.dialect.addBlobInStatementText(this.text, data);
			}
			else
			{
				this.text.append(QUESTION_MARK);
				this.parameters.add(data);
			}
		}
		else
		{
			this.text.append("NULL");
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
	
	Statement appendParameter(final Number value)
	{
		if(parameters==null)
			this.text.append(value.toString());
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(value);
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
		assert (parameters==null) == (other.parameters==null);

		if(parameters!=null) // otherwise no prepared statements are used
			parameters.addAll(other.parameters);
		
		return this;
	}
	
	Statement appendLength()
	{
		this.text.append(database.dialect.stringLength);
		
		return this;
	}

	void appendMatch(final StringFunction function, final String value)
	{
		database.appendMatchClause(this, function, value);
	}

	void appendStartsWith(final DataField field, final byte[] value)
	{
		database.dialect.appendStartsWith(this, (BlobColumn)field.getColumn(), value);
	}

	<E extends Number >void appendIntegerDivisionOperator(
			final NumberFunction<E> dividend,
			final NumberFunction<E> divisor,
			final Join join)
	{
		database.dialect.appendIntegerDivision(this, dividend, divisor, join);
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
			final StringBuilder result = new StringBuilder();
			
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
		append(table.quotedID);
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
			return table.quotedID;
		}
	}
	
	static final StringColumn assertTypeColumn(final StringColumn tc, final Type t)
	{
		if(tc==null)
			throw new IllegalArgumentException("type " + t + " has no subtypes, therefore a TypeInCondition makes no sense");
		else
			return tc;
	}
	
	List<Object> getParameters()
	{
		if(parameters==null)
			return null;
		
		return Collections.unmodifiableList(parameters);
	}
}
