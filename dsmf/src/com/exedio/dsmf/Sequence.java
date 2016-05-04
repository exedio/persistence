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

public final class Sequence extends Node
{
	final String name;
	final int start;
	private final boolean required;
	private boolean exists;

	public Sequence(final Schema schema, final String name, final int start)
	{
		this(schema, name, start, true);
	}

	Sequence(final Schema schema, final String name, final int start, final boolean required)
	{
		super(schema.dialect, schema.connectionProvider);

		if(name==null)
			throw new RuntimeException();

		this.name = name;
		this.start = start;
		this.required = required;
		this.exists = !required;

		schema.register(this);
	}

	public String getName()
	{
		return name;
	}

	public int getStart()
	{
		return start;
	}

	void notifyExists()
	{
		exists = true;
	}

	public boolean required()
	{
		return required;
	}

	public boolean exists()
	{
		return exists;
	}

	@Override
	Result computeResult()
	{
		if(!exists)
		{
			return Result.missing;
		}
		else if(!required)
		{
			return Result.notUsedWarning;
		}
		else
		{
			return Result.ok;
		}
	}

	public void create()
	{
		create((StatementListener)null);
	}

	public void create(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		create(bf);
		executeSQL(bf.toString(), listener);
	}

	void create(final StringBuilder bf)
	{
		dialect.createSequence(bf, quoteName(name), start);
	}

	public void drop()
	{
		drop((StatementListener)null);
	}

	public void drop(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		drop(bf);
		executeSQL(bf.toString(), listener);
	}

	void drop(final StringBuilder bf)
	{
		dialect.dropSequence(bf, quoteName(name));
	}

	@Override
	public String toString()
	{
		return name;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getStart()} instead
	 */
	@Deprecated
	public int getStartWith()
	{
		return getStart();
	}
}
