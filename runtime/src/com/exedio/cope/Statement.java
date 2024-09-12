/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.misc.Arrays;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

final class Statement
{
	final Dialect dialect;
	final Marshallers marshallers;

	private final boolean fulltextIndex;
	final StringBuilder text = new StringBuilder();
	private final ArrayList<Object> parameters;
	boolean typeColumnsRequired = false;
	final TC tc;
	private final HashMap<JoinTable, JoinTable> joinTables;
	private final HashSet<Table> ambiguousTables;
	private final boolean qualifyTable;

	Statement(final Executor executor, final boolean qualifyTable, final Mode mode)
	{
		if(executor==null)
			throw new NullPointerException();

		this.dialect = executor.dialect;
		this.marshallers = executor.marshallers;
		this.fulltextIndex = executor.fulltextIndex;
		this.parameters = (mode==Mode.NORMAL && executor.prepare) ? new ArrayList<>() : null;
		this.tc = null;
		this.joinTables = null;
		this.ambiguousTables = null;
		this.qualifyTable = qualifyTable;
	}

	enum Mode {NORMAL, SQL_ONLY}

	Statement(final Dialect dialect, final Marshallers marshallers)
	{
		this.dialect = dialect;
		this.marshallers = marshallers;
		this.fulltextIndex = false;
		this.parameters = null;
		this.tc = null;
		this.joinTables = null;
		this.ambiguousTables = null;
		this.qualifyTable = false;
	}

	Statement(final Executor executor, final Query<?> query, final boolean sqlOnly)
	{
		if(executor==null)
			throw new NullPointerException();

		this.dialect = executor.dialect;
		this.marshallers = executor.marshallers;
		this.fulltextIndex = executor.fulltextIndex;
		this.parameters = (!sqlOnly && executor.prepare) ? new ArrayList<>() : null;

		this.tc = query.check();

		// TODO: implementation is far from optimal
		// TODO: do all the rest in this constructor with TC

		final ArrayList<JoinType> joinTypes = new ArrayList<>();

		joinTypes.add(new JoinType(null, query.type));
		for(final Join join : query.getJoins())
			joinTypes.add(new JoinType(join, join.type));

		final HashMap<Table, Object> tableToJoinTables = new HashMap<>();
		this.joinTables = new HashMap<>();
		for(final JoinType joinType : joinTypes)
		{
			for(Type<?> type = joinType.type; type!=null; type=type.supertype)
			{
				final Table table = type.getTable();
				final Object previous = tableToJoinTables.get(table);
				final JoinTable current = new JoinTable(joinType.join, table);
				//noinspection RedundantIfStatement
				if(joinTables.putIfAbsent(current, current)!=null)
					assert false;
				if(previous==null)
					tableToJoinTables.put(table, current);
				else if(previous instanceof JoinTable)
				{
					assert table==((JoinTable)previous).table;

					final ArrayList<JoinTable> list = new ArrayList<>(2);
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
			if(value instanceof final ArrayList<?> list)
			{
				int aliasNumber = 0;
				for(final Object o : list)
				{
					final JoinTable joinType = (JoinTable)o;
					joinType.alias = table.id + (aliasNumber++);
				}
				if(ambiguousTables==null)
					ambiguousTables = new HashSet<>();
				ambiguousTables.add(table);
			}
		}
		//System.out.println("-------"+joinTables.keySet().toString());

		this.qualifyTable = joinTables.size()>1;
		this.ambiguousTables = ambiguousTables;
	}

	@SuppressWarnings("unchecked") // OK: tableToJoinTables contains both JoinTable and List<JoinTable>
	private static ArrayList<JoinTable> castJoinTable(final Object o)
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

	Statement append(final Selectable<?> select)
	{
		return append(select, null);
	}

	@SuppressWarnings("deprecation") // OK: Selectable.append is for internal use within COPE only
	Statement append(final Selectable<?> select, final Join join)
	{
		select.append(this, join);
		return this;
	}

	Statement appendPK(final Type<?> type)
	{
		return appendPK(type, null);
	}

	Statement appendPK(final Type<?> type, final Join join)
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
	@SuppressWarnings("UnusedReturnValue") // OK: is always the last in chain
	Statement appendTypeCheck(final Table table, final Type<?> type)
	{
		final StringColumn column = table.typeColumn;
		if(column!=null)
		{
			append(" AND ").
			append(column).
			append('=').
			appendParameter(type.schemaId);
		}

		return this;
	}

	Statement append(final Column column)
	{
		return append(column, null);
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

	void appendTypeColumnIfRequired(final StringColumn typeColumn, final Join join)
	{
		if(typeColumnsRequired && typeColumn!=null)
			append(',').append(typeColumn, join);
	}

	<E> Statement appendParameterAny(final E value)
	{
		@SuppressWarnings({"unchecked","rawtypes"})
		final Marshaller<E> marshaller = (Marshaller)marshallers.getByValue(value);

		final boolean isString = value instanceof String;
		if(isString)
			dialect.appendStringParameterPrefix(text);

		if(parameters==null)
			text.append(marshaller.marshalLiteral(value));
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(marshaller.marshalPrepared(value));
		}
		if(isString)
			dialect.appendStringParameterPostfix(text);

		return this;
	}

	Statement appendParameterBlob(final byte[] data)
	{
		if(data!=null)
		{
			if(parameters==null)
			{
				this.dialect.addBlobInStatementText(this.text, data);
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

	@SuppressWarnings("UnusedReturnValue") // OK: is always the last in chain
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
			this.text.append(value);
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(value);
		}
		return this;
	}

	Statement appendParameter(final Number value)
	{
		if(parameters==null)
			this.text.append(value);
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(value);
		}
		return this;
	}

	@SuppressWarnings("UnusedReturnValue") // OK: is always the last in chain
	Statement appendParameter(final String value)
	{
		if(parameters==null)
			this.text.append(StringColumn.cacheToDatabaseStatic(value));
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(value);
		}
		return this;
	}

	/**
	 * <b>BEWARE:</b>
	 * Does not consider {@link ConnectProperties#isSupportDisabledForNativeDate()}.
	 */
	Statement appendParameterDateNativelyEvenIfSupportDisabled(final Date value)
	{
		if(parameters==null)
			text.append(dialect.toLiteral(value));
		else
		{
			this.text.append(QUESTION_MARK);
			this.parameters.add(new Timestamp(value.getTime()));
		}

		return this;
	}

	@SuppressWarnings("UnusedReturnValue") // OK: is always the last in chain
	Statement appendParameters(final Statement other)
	{
		assert (parameters==null) == (other.parameters==null);

		if(parameters!=null) // otherwise no prepared statements are used
			parameters.addAll(other.parameters);

		return this;
	}

	void appendMatch(final StringFunction function, final String value)
	{
		if(fulltextIndex)
			dialect.appendMatchClauseFullTextIndex(this, function, value);
		else
			Dialect.appendMatchClauseByLike(this, function, value);
	}

	String getText()
	{
		return text.toString();
	}

	void setParameters(final PreparedStatement prepared) throws SQLException
	{
		int parameterIndex = 1;
		for(final Object p : parameters)
		{
			if(p instanceof Timestamp)
				prepared.setTimestamp(parameterIndex, (Timestamp)p, TimestampColumn.newGMTCalendar());
			else
				prepared.setObject(parameterIndex, p);

			parameterIndex++;
		}
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
			final Iterator<?> pi = parameters.iterator();
			for(int pos = text.indexOf(QUESTION_MARK); pos>=0&&pi.hasNext(); pos = text.indexOf(QUESTION_MARK, lastPos))
			{
				result.append(text, lastPos, pos);
				result.append(QUESTION_MARK);
				appendValue(result, pi.next());
				result.append(QUESTION_MARK);
				lastPos = pos+1;
			}
			result.append(text.substring(lastPos));

			return result.toString();
		}
	}

	private static void appendValue(final StringBuilder bf, final Object o)
	{
		if(o==null)
			bf.append("NULL");
		else if(o instanceof Item)
			((Item)o).appendCopeID(bf);
		else if(o instanceof Date)
			bf.append(DateField.format().format((Date)o));
		else if(o instanceof String)
			bf.append(StringField.truncateValue((String)o));
		else if(o instanceof byte[])
			Arrays.append(bf, (byte[])o, 30);
		else
			bf.append(o);
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

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
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

	private record JoinType(Join join, Type<?> type)
	{
	}

	private void appendTableDefinition(final Join join, final Table table)
	{
		append(table.quotedID);
		final String alias = getAlias(join, table);
		if(alias!=null)
		{
			append(' ').
			append(alias);
		}
	}

	void appendTypeDefinition(final Join join, final Type<?> type, final boolean hasJoins)
	{
		final Type<?> supertype = type.supertype;
		final Table table = type.getTable();

		ArrayList<Table> superTables = null;
		if(supertype!=null)
		{
			for(Type<?> iType = supertype; iType!=null; iType=iType.supertype)
			{
				final Table iTable = iType.getTable();
				if(tc.containsTable(join, iTable))
				{
					if(superTables==null)
						superTables = new ArrayList<>();

					superTables.add(iTable);
				}
			}
		}
		// TODO join tables of subtypes as well if needed

		if(superTables!=null && hasJoins)
			append('(');

		appendTableDefinition(join, table);

		if(superTables!=null)
		{
			for(final Table iTable : superTables)
			{
				append(" JOIN ");
				appendTableDefinition(join, iTable);
				append(" ON ");
				append(iTable.primaryKey, join);
				append('=');
				append(table.primaryKey, join);
			}
			if(hasJoins)
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
			// TODO replace by assertion, once BadQueryTest works
			if(ambiguousTables!=null && ambiguousTables.contains(table))
				throw new IllegalArgumentException(
						"feature " + exceptionColumn + " is ambiguous, " +
						"use Function#bind (deprecated): " + tc.queryToString());
			return table.quotedID;
		}
	}

	static StringColumn assertTypeColumn(final StringColumn tc, final Type<?> t)
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

	QueryInfo getQueryInfo()
	{
		final QueryInfo result = new QueryInfo(text.toString());
		if(parameters!=null)
			for(final Object p : parameters)
				result.addChild(new QueryInfo(p.toString()));
		return result;
	}


	// Vault Trail

	private HashMap<DataField, String> vaultTrailToAlias = null;

	void joinVaultTrailIfAbsent(
			@Nonnull final DataField field,
			@Nonnull final VaultTrail trail)
	{
		if(vaultTrailToAlias!=null &&
			vaultTrailToAlias.containsKey(field))
			return;

		final String alias = dialect.dsmfDialect.quoteName(field.getID());
		if(vaultTrailToAlias==null)
			vaultTrailToAlias = new HashMap<>();
		vaultTrailToAlias.put(field, alias);

		append(" JOIN ");
		append(trail.tableQuoted);
		append(' ');
		append(alias);
		append(" ON ");
		append(field.getColumn());
		append('=');
		append(alias);
		append('.');
		append(trail.hashQuoted);
	}

	String getJoinVaultTrailAlias(
			@Nonnull final DataField field)
	{
		final String alias = vaultTrailToAlias.get(field);
		if(alias==null)
			throw new RuntimeException(field.getID());
		return alias;
	}
}
