/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
	final String name;
	final Type type;
	private final boolean required;
	final String requiredCondition;
	private boolean exists = false;
	private String existingCondition;

	Constraint(
			final Table table,
			final String name,
			final Type type,
			final boolean required,
			final String condition)
	{
		super(table.dialect, table.connectionProvider);

		if(name==null)
			throw new RuntimeException(table.name);
		if(type==null)
			throw new RuntimeException(table.name);

		this.table = table;
		this.name = name;
		this.type = type;
		this.required = required;
		if(required)
			this.requiredCondition = condition;
		else
		{
			this.requiredCondition = null;
			this.existingCondition = condition;
			this.exists = true;
		}
		table.register(this);
	}

	public final Table getTable()
	{
		return table;
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
		exists = true;
	}

	final void notifyExistsCondition(final String condition)
	{
		if(condition==null)
			throw new NullPointerException();

		assert !exists;
		assert existingCondition==null;

		exists = true;
		this.existingCondition = condition;
	}

	@Override
	final void finish()
	{
		assert particularColor==null;
		assert cumulativeColor==null;

		// TODO: make this dependend on type of constraint:
		// check/not null constraint are yellow only if missing
		// foreign key/unique constraint are red when missing or unused
		final String error;
		final Color particularColor;
		if(!exists)
		{
			if(isSupported())
			{
				error = "missing";
				particularColor = Color.ERROR;
			}
			else
			{
				error = "not supported";
				particularColor = Color.OK;
			}
		}
		else if(!required)
		{
			error = "not used";
			if(!table.required())
				particularColor = Color.WARNING;
			else
				particularColor = Color.ERROR;
		}
		else
		{
			if(requiredCondition!=null && existingCondition!=null &&
				!requiredCondition.equals(existingCondition))
			{
				error = "different condition in database: expected ---" + requiredCondition + "---, but was ---" + existingCondition + "---";
				particularColor = Color.ERROR;
			}
			else if(requiredCondition==null && existingCondition!=null)
			{
				error = "surplus condition in database: ---" + existingCondition + "---";
				particularColor = Color.ERROR;
			}
			else if(requiredCondition!=null && existingCondition==null)
			{
				error = "missing condition in database: ---" + requiredCondition + "---";
				particularColor = Color.ERROR;
			}
			else
			{
				error = null;
				particularColor = Color.OK;
			}
		}

		this.error = error;
		this.particularColor = particularColor;
		cumulativeColor = particularColor;
	}

	public final boolean required()
	{
		return required;
	}

	public final boolean exists()
	{
		return exists;
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

	public int check()
	{
		throw new RuntimeException("no yet implemented"); // TODO
	}

	public final void create()
	{
		create((StatementListener)null);
	}

	public final void drop()
	{
		drop((StatementListener)null);
	}

	public final void create(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		create(bf);
		executeSQL(bf.toString(), listener);
	}

	public final void drop(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		drop(bf);
		executeSQL(bf.toString(), listener);
	}

	abstract void createInTable(StringBuilder bf);
	abstract void create(StringBuilder bf);
	abstract void drop(StringBuilder bf);
}
