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

public final class Column extends Node
{
	final Table table;
	final String name;
	private final String requiredType;
	private String existingType;

	public Column(final Table table, final String name, final String type)
	{
		this(table, name, type, true);
	}

	Column(final Table table, final String name, final String type, final boolean required)
	{
		super(table.dialect, table.connectionProvider);

		if(name==null)
			throw new RuntimeException(type);
		if(type==null)
			throw new RuntimeException(name);

		this.table = table;
		this.name = name;
		if(required)
		{
			this.requiredType = type;
			this.existingType = null;
		}
		else
		{
			this.requiredType = null;
			this.existingType = type;
		}
		table.register(this);
	}

	public Table getTable()
	{
		return table;
	}

	public String getName()
	{
		return name;
	}

	void notifyExists(final String existingType)
	{
		if(existingType==null)
			throw new RuntimeException(name);
		if(this.existingType!=null && !this.existingType.equals(existingType))
			throw new RuntimeException(name);

		this.existingType = existingType;
	}

	@Override
	Result computeResult()
	{
		if(existingType==null)
		{
			return Result.missing;
		}
		else if(requiredType==null)
		{
			return Result.notUsedWarning;
		}
		else
		{
			if(!requiredType.equals(existingType))
			{
				return Result.error("different type in database: >"+existingType+"<");
			}
			else
			{
				return Result.ok;
			}
		}
	}

	public boolean required()
	{
		return requiredType!=null;
	}

	public boolean exists()
	{
		return existingType!=null;
	}

	public String getType()
	{
		if(requiredType!=null)
			return requiredType;
		else
			return existingType;
	}

	public boolean mismatchesType()
	{
		return
			requiredType!=null &&
			existingType!=null &&
			!requiredType.equals(existingType);
	}

	public String getRequiredType()
	{
		if(requiredType==null)
			throw new IllegalStateException("not required");

		return requiredType;
	}

	public void create()
	{
		create(null);
	}

	public void create(final StatementListener listener)
	{
		//System.out.println("createColumn:"+bf);
		executeSQL(
			dialect.createColumn(
				quoteName(table.name),
				quoteName(name),
				getType()), listener);
	}

	public void renameTo(final String newName)
	{
		renameTo(newName, null);
	}

	public void renameTo(final String newName, final StatementListener listener)
	{
		//System.err.println("renameColumn:"+bf);
		executeSQL(
			dialect.renameColumn(
				quoteName(table.name),
				quoteName(name),
				quoteName(newName),
				existingType), listener);
	}

	public void modify(final String newType)
	{
		modify(newType, null);
	}

	public void modify(final String newType, final StatementListener listener)
	{
		executeSQL(
			dialect.modifyColumn(
				quoteName(table.name),
				quoteName(name),
				newType), listener);
	}

	public void drop()
	{
		drop(null);
	}

	public void drop(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("ALTER TABLE ").
			append(quoteName(table.name)).
			append(" DROP COLUMN ").
			append(quoteName(name));

		//System.out.println("dropColumn:"+bf);
		executeSQL(bf.toString(), listener);
	}

	public void update(final String value, final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("UPDATE ").
			append(quoteName(table.name)).
			append(" SET ").
			append(quoteName(name)).
			append('=').
			append(value);

		executeSQL(bf.toString(), listener);
	}

	@Override
	public String toString()
	{
		return name;
	}
}

