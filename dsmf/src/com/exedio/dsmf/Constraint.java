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

package com.exedio.dsmf;

import java.util.Objects;

public abstract class Constraint extends Node
{
	public enum Type
	{
		PrimaryKey(false), ForeignKey(true), Unique(false), Check(false);

		final boolean secondPhase;

		Type(final boolean secondPhase)
		{
			this.secondPhase = secondPhase;
		}
	}

	final Table table;
	final Column column;
	final String name;
	final Type type;
	final Field<String> clause;
	private String existingClauseRaw;

	Constraint(
			final Table table,
			final Column column,
			final String name,
			final Type type,
			final boolean required,
			final String clause)
	{
		this(table, column, name, type, required, clause, clause);
	}

	Constraint(
			final Table table,
			final Column column,
			final String name,
			final Type type,
			final boolean required,
			final String clause,
			final String clauseRaw)
	{
		super(table.dialect, table.connectionProvider, required);

		if(name==null)
			throw new RuntimeException(table.name);
		if(type==null)
			throw new RuntimeException(table.name);

		this.table = table;
		this.column = column;
		this.name = name;
		this.type = type;
		this.clause = (type==Type.PrimaryKey) ? null : new Field<>(clause, required);
		this.existingClauseRaw = required ? null : clauseRaw;
		//noinspection ThisEscapedInObjectConstruction
		table.register(this);
		if(column!=null)
			//noinspection ThisEscapedInObjectConstruction
			column.register(this);
	}

	public final Table getTable()
	{
		return table;
	}

	public final Column getColumn()
	{
		return column;
	}

	public final String getName()
	{
		return name;
	}

	public final Type getType()
	{
		return type;
	}

	/**
	 * @deprecated Use {@link #getClause()} instead.
	 */
	@Deprecated
	public final String getCondition()
	{
		return getClause();
	}

	public final String getClause()
	{
		return clause!=null ? clause.get() : null;
	}

	/**
	 * @deprecated Use {@link #getRequiredClause()} instead.
	 */
	@Deprecated
	public final String getRequiredCondition()
	{
		return getRequiredClause();
	}

	public final String getRequiredClause()
	{
		return clause!=null ? clause.getRequiredOrNull() : null;
	}

	/**
	 * @deprecated Use {@link #getMismatchingClause()} instead.
	 */
	@Deprecated
	public final String getMismatchingCondition()
	{
		return getMismatchingClause();
	}

	public final String getMismatchingClause()
	{
		return clause!=null ? clause.getMismatching(this) : null;
	}

	/**
	 * @deprecated Use {@link #getMismatchingClauseRaw()} instead.
	 */
	@Deprecated
	public final String getMismatchingConditionRaw()
	{
		return getMismatchingClauseRaw();
	}

	public final String getMismatchingClauseRaw()
	{
		return getMismatchingClause()!=null ? existingClauseRaw : null;
	}

	final void notifyExists()
	{
		notifyExistsNode();
	}

	final void notifyExistsCondition(final String condition)
	{
		notifyExistsCondition(condition, condition);
	}

	final void notifyExistsCondition(final String clause, final String clauseRaw)
	{
		if(clause==null)
			throw new NullPointerException();
		if(clauseRaw==null)
			throw new NullPointerException();

		assert existingClauseRaw==null;

		notifyExistsNode();
		this.clause.notifyExists(clause);
		this.existingClauseRaw = clauseRaw;
	}

	@Override
	final Result computeResult()
	{
		if(!exists())
			return isSupported()
				? Result.missing(type!=Type.Check)
				: Result.unsupported;

		if(!required())
			return Result.unused(table.required());

		if(clause!=null && clause.mismatches())
		{
			final String mismatching = clause.getMismatching(this);
			final StringBuilder sb = new StringBuilder();
			sb.append(
					"unexpected condition >>>").append(mismatching).append("<<<"); // The value of this string literal must not be changed, otherwise cope console breaks

			if(!Objects.equals(mismatching, existingClauseRaw))
				sb.append(" (originally >>>"). // The value of this string literal must not be changed, otherwise cope console breaks
					append(existingClauseRaw).
					append("<<<)"); // The value of this string literal must not be changed, otherwise cope console breaks

			return Result.error(sb.toString());
		}

		return Result.ok;
	}

	@Override
	public final String toString()
	{
		return name;
	}

	public boolean isSupported()
	{
		return true;
	}

	/**
	 * @deprecated Use {@link #checkL()} instead
	 */
	@Deprecated
	public final int check()
	{
		return toIntCapped(checkL());
	}

	// copied from CastUtils
	@Deprecated
	private static int toIntCapped(final long longValue)
	{
		if(longValue>Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		if(longValue<Integer.MIN_VALUE)
			return Integer.MIN_VALUE;
		return (int)longValue;
	}

	public long checkL()
	{
		throw new RuntimeException("no yet implemented"); // TODO
	}

	public final void create()
	{
		create(null);
	}

	public final void drop()
	{
		drop((StatementListener)null);
	}

	public final void create(final StatementListener listener)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").
			append(quoteName(table.name)).
			append(" ADD ");
		appendCreateClause(sb);
		executeSQL(sb.toString(), listener);
	}

	public final void drop(final StatementListener listener)
	{
		final StringBuilder sb = new StringBuilder();
		drop(sb);
		executeSQL(sb.toString(), listener);
	}

	abstract void appendCreateClause(StringBuilder sb);

	void drop(final StringBuilder sb)
	{
		Dialect.dropConstraint(sb, quoteName(table.name), quoteName(name));
	}
}
