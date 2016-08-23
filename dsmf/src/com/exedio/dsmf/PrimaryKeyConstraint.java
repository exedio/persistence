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

public final class PrimaryKeyConstraint extends Constraint
{
	final String primaryKeyColumn;

	public PrimaryKeyConstraint(
			final Column column,
			final String name)
	{
		this(column.table, column, name, true, column.name);
	}

	PrimaryKeyConstraint(
			final Table table,
			final Column column,
			final String name,
			final boolean required,
			final String primaryKeyColumn)
	{
		super(table, column, name, Type.PrimaryKey, required, null);

		if(required && primaryKeyColumn==null)
			throw new RuntimeException(name);

		this.primaryKeyColumn = primaryKeyColumn;
		//System.out.println("-------------"+name+"-"+primaryKeyColumn);
	}

	public String getPrimaryKeyColumn()
	{
		return primaryKeyColumn;
	}

	@Override
	void createInTable(final StringBuilder bf)
	{
		bf.append(",CONSTRAINT ").
			append(quoteName(name)).
			append(" PRIMARY KEY(").
			append(quoteName(primaryKeyColumn)).
			append(')');
	}

	@Override
	void create(final StringBuilder bf)
	{
		bf.append("ALTER TABLE ").
			append(quoteName(table.name)).
			append(" ADD CONSTRAINT ").
			append(quoteName(name)).
			append(" PRIMARY KEY(").
			append(quoteName(primaryKeyColumn)).
			append(')');
	}

	@Override
	void drop(final StringBuilder bf)
	{
		dialect.dropPrimaryKeyConstraint(bf, quoteName(table.name), quoteName(name));
	}
}
