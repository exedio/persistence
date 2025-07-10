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
	final Field<String> condition;
	private String existingConditionRaw;

	Constraint(
			final Table table,
			final Column column,
			final String name,
			final Type type,
			final boolean required,
			final String condition)
	{
		this(table, column, name, type, required, condition, condition);
	}

	Constraint(
			final Table table,
			final Column column,
			final String name,
			final Type type,
			final boolean required,
			final String condition,
			final String conditionRaw)
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
		this.condition = (type==Type.PrimaryKey) ? null : new Field<>(condition, required);
		this.existingConditionRaw = required ? null : conditionRaw;
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

	public final String getCondition()
	{
		return condition!=null ? condition.get() : null;
	}

	public final String getRequiredCondition()
	{
		return condition!=null ? condition.getRequiredOrNull() : null;
	}

	public final String getMismatchingCondition()
	{
		return condition!=null ? condition.getMismatching(this) : null;
	}

	public final String getMismatchingConditionRaw()
	{
		return getMismatchingCondition()!=null ? existingConditionRaw : null;
	}

	final void notifyExists()
	{
		notifyExistsNode();
	}

	final void notifyExistsCondition(final String condition)
	{
		notifyExistsCondition(condition, condition);
	}

	final void notifyExistsCondition(final String condition, final String conditionRaw)
	{
		if(condition==null)
			throw new NullPointerException();
		if(conditionRaw==null)
			throw new NullPointerException();

		assert existingConditionRaw==null;

		notifyExistsNode();
		this.condition.notifyExists(condition);
		this.existingConditionRaw = conditionRaw;
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

		if(condition!=null && condition.mismatches())
		{
			final String mismatching = condition.getMismatching(this);
			final StringBuilder sb = new StringBuilder();
			sb.append(
					"unexpected condition >>>").append(mismatching).append("<<<"); // The value of this string literal must not be changed, otherwise cope console breaks

			if(!Objects.equals(mismatching, existingConditionRaw))
				sb.append(" (originally >>>"). // The value of this string literal must not be changed, otherwise cope console breaks
					append(existingConditionRaw).
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
	abstract void drop(StringBuilder sb);
}
