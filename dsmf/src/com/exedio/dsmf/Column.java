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

import java.util.ArrayList;
import java.util.Collection;

public final class Column extends Node
{
	final Table table;
	final String name;
	private final Field<String> type;
	private final ArrayList<Constraint> constraints = new ArrayList<>();

	public Column(final Table table, final String name, final String type)
	{
		this(table, name, type, true);
	}

	Column(final Table table, final String name, final String type, final boolean required)
	{
		super(table.dialect, table.connectionProvider, required);

		if(name==null)
			throw new RuntimeException(type);
		if(type==null)
			throw new RuntimeException(name);

		this.table = table;
		this.name = name;
		this.type = new Field<>(type, required);
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

	void register(final Constraint constraint)
	{
		if(table!=constraint.table)
			throw new RuntimeException(table.name + '#' + constraint.name + '>' + name);

		constraints.add(constraint);
	}

	void notifyExists(final String existingType)
	{
		notifyExistsNode();
		type.notifyExists(existingType);
	}

	@Override
	Result computeResult()
	{
		Result cumulativeResult = computeParticularResult();
		for(final Constraint constraint : constraints)
			cumulativeResult = cumulativeResult.cumulate(constraint.finish());
		return cumulativeResult;
	}

	private Result computeParticularResult()
	{
		if(!exists())
			return Result.missing;

		if(!required())
			return Result.notUsedWarning;

		if(type.mismatches())
			return Result.error(
					"different type in database: >"+type.getExisting()+"<");

		return Result.ok;
	}

	/**
	 * @see Table#getConstraints()
	 * @see Table#getTableConstraints()
	 */
	public Collection<Constraint> getConstraints()
	{
		return unmodifiableList(constraints);
	}

	public String getType()
	{
		return type.get();
	}

	public boolean mismatchesType()
	{
		return type.mismatches();
	}

	public String getRequiredType()
	{
		return type.getRequired();
	}

	public String getExistingType()
	{
		return type.getExisting();
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
				type.getExisting()), listener);
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

