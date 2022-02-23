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
	final String requiredCondition;
	private String existingCondition;

	Constraint(
			final Table table,
			final Column column,
			final String name,
			final Type type,
			final boolean required,
			final String condition)
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
		if(required)
			this.requiredCondition = condition;
		else
		{
			this.requiredCondition = null;
			this.existingCondition = condition;
		}
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

	public final String getRequiredCondition()
	{
		return requiredCondition;
	}

	final void notifyExists()
	{
		notifyExistsNode();
	}

	final void notifyExistsCondition(final String condition)
	{
		if(condition==null)
			throw new NullPointerException();

		assert existingCondition==null;

		notifyExistsNode();
		this.existingCondition = condition;
	}

	@Override
	final Result computeResult()
	{
		// TODO: make this dependent on type of constraint:
		// check/not null constraint are yellow only if missing
		// foreign key/unique constraint are red when missing or unused
		if(!exists())
			return isSupported()
				? Result.missing
				: Result.unsupported;

		if(!required())
			return table.required()
				? Result.unusedError
				: Result.unusedWarning;

		final String existingConditionAdjusted =
				existingCondition!=null
				? adjustExistingCondition(existingCondition)
				: null;
		if(requiredCondition!=null && existingCondition!=null &&
			!requiredCondition.equals(existingConditionAdjusted))
		{
			final StringBuilder bf = new StringBuilder();
			bf.append(
					"unexpected condition >>>").append(existingConditionAdjusted).append("<<<");

			if(!existingCondition.equals(existingConditionAdjusted))
				bf.append(" (originally >>>").
					append(existingCondition).
					append("<<<)");

			return Result.error(bf.toString());
		}

		if(requiredCondition==null && existingCondition!=null)
			return Result.error(
					"surplus condition >>>" + existingCondition + "<<<");

		if(requiredCondition!=null && existingCondition==null)
			return Result.error(
					"missing condition >>>" + requiredCondition + "<<<");

		return Result.ok;
	}

	String adjustExistingCondition(final String s)
	{
		return s;
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
		final StringBuilder bf = new StringBuilder();
		bf.append("ALTER TABLE ").
			append(quoteName(table.name)).
			append(" ADD ");
		appendCreateClause(bf);
		executeSQL(bf.toString(), listener);
	}

	public final void drop(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		drop(bf);
		executeSQL(bf.toString(), listener);
	}

	abstract void appendCreateClause(StringBuilder bf);
	abstract void drop(StringBuilder bf);
}
