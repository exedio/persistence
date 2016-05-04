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

import com.exedio.dsmf.Constraint.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public final class Table extends Node
{
	final Schema schema;
	final String name;
	private final boolean required;
	private boolean exists;

	private final HashMap<String, Column> columnMap = new HashMap<>();
	private final ArrayList<Column> columnList = new ArrayList<>();

	private final HashMap<String, Constraint> constraintMap = new HashMap<>();
	private final ArrayList<Constraint> constraintList = new ArrayList<>();

	public Table(final Schema schema, final String name)
	{
		this(schema, name, true);
	}

	Table(final Schema schema, final String name, final boolean required)
	{
		super(schema.dialect, schema.connectionProvider);

		if(name==null)
			throw new RuntimeException();

		this.schema = schema;
		this.name = name;
		this.required = required;
		this.exists = !required;

		schema.register(this);
	}

	public String getName()
	{
		return name;
	}

	void register(final Column column)
	{
		if(columnMap.put(column.name, column)!=null)
			throw new RuntimeException("duplicate column name in table " + name + ": " + column.name);
		columnList.add(column);
	}

	void register(final Constraint constraint)
	{
		if(constraintMap.put(constraint.name, constraint)!=null)
			throw new RuntimeException("duplicate constraint name in table " + name + ": " + constraint.name);
		constraintList.add(constraint);
		schema.register(constraint);
	}

	void notifyExists()
	{
		exists = true;
	}

	Column notifyExistentColumn(final String columnName, final String existingType)
	{
		Column result = columnMap.get(columnName);
		if(result==null)
			result = new Column(this, columnName, existingType, false);
		else
			result.notifyExists(existingType);

		return result;
	}

	Constraint notifyExistentCheckConstraint(final String constraintName, final String condition)
	{
		Constraint result = constraintMap.get(constraintName);

		if(result==null)
			result = new CheckConstraint(this, constraintName, false, condition);
		else
			result.notifyExistsCondition(condition);

		return result;
	}

	Constraint notifyExistentPrimaryKeyConstraint(final String constraintName)
	{
		Constraint result = constraintMap.get(constraintName);

		if(result==null)
			result = new PrimaryKeyConstraint(this, constraintName, false, null);
		else
			result.notifyExists();

		return result;
	}

	Constraint notifyExistentForeignKeyConstraint(
			final String constraintName,
			final String foreignKeyColumn,
			final String targetTable,
			final String targetColumn)
	{
		ForeignKeyConstraint result = (ForeignKeyConstraint)constraintMap.get(constraintName);

		if(result==null)
			result = new ForeignKeyConstraint(this, constraintName, false, foreignKeyColumn, targetTable, targetColumn);
		else
			result.notifyExists(foreignKeyColumn, targetTable, targetColumn);

		return result;
	}

	Constraint notifyExistentUniqueConstraint(final String constraintName, final List<String> condition)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append('(');
		boolean first = true;
		for(final String i : condition)
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(quoteName(i));
		}
		bf.append(')');

		return notifyExistentUniqueConstraint(constraintName, bf.toString());
	}

	Constraint notifyExistentUniqueConstraint(final String constraintName, final String condition)
	{
		Constraint result = constraintMap.get(constraintName);

		if(result==null)
			result = new UniqueConstraint(this, constraintName, false, condition);
		else
			result.notifyExistsCondition(condition);

		return result;
	}

	public boolean required()
	{
		return required;
	}

	public boolean exists()
	{
		return exists;
	}

	public Collection<Column> getColumns()
	{
		return columnList;
	}

	public Column getColumn(final String columnName)
	{
		return columnMap.get(columnName);
	}

	public Collection<Constraint> getConstraints()
	{
		return constraintList;
	}

	public Constraint getConstraint(final String constraintName)
	{
		return constraintMap.get(constraintName);
	}

	@Override
	Result computeResult()
	{
		final String error;
		final Color particularColor;
		if(!exists)
		{
			error = "missing";
			particularColor = Color.ERROR;
		}
		else if(!required)
		{
			error = "not used";
			particularColor = Color.WARNING;
		}
		else
		{
			error = null;
			particularColor = Color.OK;
		}

		Color cumulativeColor = particularColor;

		for(final Column column : columnList)
		{
			column.finish();
			cumulativeColor = cumulativeColor.max(column.getCumulativeColor());
		}

		for(final Constraint constraint : constraintList)
		{
			constraint.finish();
			cumulativeColor = cumulativeColor.max(constraint.getCumulativeColor());
		}

		return new Result(error, particularColor, cumulativeColor);
	}

	public void create()
	{
		create((StatementListener)null);
	}

	void create(final StringBuilder bf)
	{
		create(bf, null);
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
			if(c.isSupported() && (constraintsBroken!=null ? !constraintsBroken.contains(c) : !c.type.secondPhase))
				c.createInTable(bf);
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
				catch(final SQLRuntimeException e2)
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
				final int count = c.check();
				if(count!=0)
					throw new RuntimeException("constraint violated for " + c + " on " + count + " tuples.");
			}
	}

	@Override
	public String toString()
	{
		return name;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #Table(Schema, String)} instead
	 * @param options is ignored
	 */
	@Deprecated
	public Table(final Schema schema, final String name, final String options)
	{
		this(schema, name, true);
	}

	/**
	 * @deprecated Is not supported anymore, does nothing.
	 */
	@Deprecated
	public void makeDefensive()
	{
		// do nothing
	}

	/**
	 * @deprecated Not supported anymore, always returns null.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public String getOptions()
	{
		return null;
	}
}
