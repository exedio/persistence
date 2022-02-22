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

import static java.util.Collections.unmodifiableList;

import com.exedio.dsmf.Constraint.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;
import javax.annotation.Nonnull;

public final class Table extends Node
{
	final Schema schema;
	final String name;

	private final HashMap<String, Column> columnMap = new HashMap<>();
	private final ArrayList<Column> columnList = new ArrayList<>();

	private final HashMap<String, Constraint> constraintMap = new HashMap<>();
	private final ArrayList<Constraint> constraintList = new ArrayList<>();
	private final ArrayList<Constraint> tableConstraints = new ArrayList<>();

	/**
	 * @deprecated Use {@link Schema#newTable(String)} instead
	 */
	@Deprecated
	public Table(final Schema schema, final String name)
	{
		this(schema, name, true);
	}

	Table(final Schema schema, final String name, final boolean required)
	{
		super(schema.dialect, schema.connectionProvider, required);

		if(name==null)
			throw new RuntimeException();

		this.schema = schema;
		this.name = name;

		//noinspection ThisEscapedInObjectConstruction
		schema.register(this);
	}

	public String getName()
	{
		return name;
	}

	@SuppressWarnings("deprecation") // OK: moved api
	public Column newColumn(
			@Nonnull final String name,
			@Nonnull final String type)
	{
		return new Column(this,
				requireNonEmptyTrimmed(name, "name"),
				requireNonEmptyTrimmed(type, "type"));
	}

	@SuppressWarnings("deprecation") // OK: moved api
	public UniqueConstraint newUnique(
			final Column column,
			@Nonnull final String name,
			@Nonnull final String clause)
	{
		return new UniqueConstraint(this,
				column,
				requireNonEmptyTrimmed(name, "name"),
				requireNonEmptyTrimmed(clause, "clause"));
	}

	@SuppressWarnings("deprecation") // OK: moved api
	public CheckConstraint newCheck(
			@Nonnull final String name,
			@Nonnull final String condition)
	{
		return new CheckConstraint(this,
				requireNonEmptyTrimmed(name, "name"),
				requireNonEmptyTrimmed(condition, "condition"));
	}

	void register(final Column column)
	{
		if(columnMap.putIfAbsent(column.name, column)!=null)
			throw new RuntimeException("duplicate column name in table " + name + ": " + column.name);
		columnList.add(column);
	}

	void register(final Constraint constraint)
	{
		if(constraintMap.putIfAbsent(constraint.name, constraint)!=null)
			throw new RuntimeException("duplicate constraint name in table " + name + ": " + constraint.name);
		constraintList.add(constraint);
		if(constraint.column==null)
			tableConstraints.add(constraint);
		schema.register(constraint);
	}

	void notifyExists()
	{
		notifyExistsNode();
	}

	public Collection<Column> getColumns()
	{
		return unmodifiableList(columnList);
	}

	public Column getColumn(final String columnName)
	{
		return columnMap.get(columnName);
	}

	/**
	 * Returns all constraints of this table, including constraints returned
	 * by {@link Column#getConstraints()}.
	 * @see #getTableConstraints()
	 */
	public Collection<Constraint> getConstraints()
	{
		return unmodifiableList(constraintList);
	}

	/**
	 * Returns constraints of this table, that are not already returned
	 * by {@link Column#getConstraints()}.
	 * @see #getConstraints()
	 */
	public Collection<Constraint> getTableConstraints()
	{
		return unmodifiableList(tableConstraints);
	}

	public Constraint getConstraint(final String constraintName)
	{
		return constraintMap.get(constraintName);
	}

	private Result computeParticularResult()
	{
		if(!exists())
			return Result.missing;

		if(!required())
			return Result.unusedWarning;

		return Result.ok;
	}

	@Override
	Result computeResult()
	{
		Result cumulativeResult = computeParticularResult();
		for(final Column column : columnList)
			cumulativeResult = cumulativeResult.cumulate(column.finish());
		for(final Constraint constraint : constraintList)
			cumulativeResult = cumulativeResult.cumulate(constraint.finish());
		return cumulativeResult;
	}

	@SuppressWarnings("RedundantCast")
	public void create()
	{
		create((StatementListener)null);
	}

	void create(final StringBuilder bf, final Set<ForeignKeyConstraint> constraintsBroken)
	{
		bf.append("CREATE TABLE ").
			append(quoteName(name)).
			append('(');

		boolean first = true;
		for(final Column column : columnList)
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(quoteName(column.name)).
				append(' ').
				append(column.getType());
		}

		for(final Constraint c : constraintList)
		{
			//noinspection SuspiciousMethodCalls
			if(c.isSupported() && (constraintsBroken!=null ? !constraintsBroken.contains(c) : !c.type.secondPhase))
			{
				bf.append(',');
				c.appendCreateClause(bf);
			}
		}

		bf.append(')');

		dialect.appendTableCreateStatement(bf);
	}

	public void create(final StatementListener listener)
	{
		create(listener, null);
	}

	void create(final StatementListener listener, final Set<ForeignKeyConstraint> constraintsBroken)
	{
		final StringBuilder bf = new StringBuilder();
		create(bf, constraintsBroken);
		executeSQL(bf.toString(), listener);

	}

	public void drop()
	{
		drop((StatementListener)null);
	}

	void drop(final StringBuilder bf)
	{
		bf.append("DROP TABLE ").
			append(quoteName(name));
	}

	public void drop(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		drop(bf);
		executeSQL(bf.toString(), listener);

	}

	void createConstraints(final EnumSet<Type> types, final boolean secondPhase, final StatementListener listener)
	{
		for(final Constraint constraint : constraintList)
		{
			if(constraint.isSupported() && types.contains(constraint.type) && constraint.type.secondPhase==secondPhase)
				constraint.create(listener);
		}
	}

	void dropConstraints(final EnumSet<Type> types, final boolean secondPhase, final StatementListener listener)
	{
		for(final Constraint constraint : constraintList)
		{
			if(constraint.isSupported() && types.contains(constraint.type) && constraint.type.secondPhase==secondPhase)
				constraint.drop(listener);
		}
	}

	void tearDownConstraints(final EnumSet<Type> types, final boolean secondPhase, final StatementListener listener)
	{
		for(final Constraint constraint : constraintList)
		{
			if(types.contains(constraint.type) && constraint.type.secondPhase==secondPhase)
			{
				try
				{
					constraint.drop(listener);
				}
				catch(final SQLRuntimeException ignored)
				{
					// ignored in teardown
					//System.err.println("failed:"+e2.getMessage());
				}
			}
		}
	}

	public void renameTo(final String newName)
	{
		renameTo(newName, null);
	}

	public void renameTo(final String newName, final StatementListener listener)
	{
		executeSQL(dialect.renameTable(quoteName(name), quoteName(newName)), listener);
	}

	public void checkUnsupportedConstraints()
	{
		for(final Constraint c : getConstraints())
			if(!c.isSupported())
			{
				final long count = c.checkL();
				if(count!=0)
					throw new RuntimeException("constraint violated for " + c + " on " + count + " tuples.");
			}
	}

	@Override
	public String toString()
	{
		return name;
	}
}
