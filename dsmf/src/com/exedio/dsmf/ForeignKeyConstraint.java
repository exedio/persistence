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

public final class ForeignKeyConstraint extends Constraint
{
	final String foreignKeyColumn;
	final String targetTable;
	final String targetColumn;

	public ForeignKeyConstraint(
			final Table table,
			final String name,
			final String foreignKeyColumn,
			final String targetTable,
			final String targetColumn)
	{
		this(table, name, true, foreignKeyColumn, targetTable, targetColumn);
	}

	ForeignKeyConstraint(
			final Table table,
			final String name,
			final boolean required,
			final String foreignKeyColumn,
			final String targetTable,
			final String targetColumn)
	{
		super(table, name, Type.ForeignKey, required, makeClause(foreignKeyColumn, targetTable, targetColumn));

		if(foreignKeyColumn==null)
			throw new NullPointerException(name);
		if(targetTable==null)
			throw new NullPointerException(name);
		if(targetColumn==null)
			throw new NullPointerException(name);

		this.foreignKeyColumn = foreignKeyColumn;
		this.targetTable = targetTable;
		this.targetColumn = targetColumn;
		//System.out.println("-------------"+name+"-"+foreignKeyColumn+"-"+targetTable+"-"+targetColumn);
	}

	private static String makeClause(
			final String foreignKeyColumn,
			final String targetTable,
			final String targetColumn)
	{
		return foreignKeyColumn + "->" + targetTable + '.' + targetColumn;
	}

	public String getForeignKeyColumn()
	{
		return foreignKeyColumn;
	}

	public String getTargetTable()
	{
		return targetTable;
	}

	public String getTargetColumn()
	{
		return targetColumn;
	}

	void notifyExists(
			final String foreignKeyColumn,
			final String targetTable,
			final String targetColumn)
	{
		notifyExistsCondition(makeClause(foreignKeyColumn, targetTable, targetColumn));
	}

	@Override
	void create(final StringBuilder bf)
	{
		bf.append("alter table ").
			append(quoteName(table.name)).
			append(" add constraint ").
			append(quoteName(name)).
			append(" foreign key (").
			append(quoteName(foreignKeyColumn)).
			append(") references ").
			append(quoteName(targetTable));

		if(dialect.needsTargetColumnName())
		{
			bf.append('(').
				append(quoteName(targetColumn)).
				append(')');
		}

		dialect.appendForeignKeyCreateStatement(bf);
	}

	@Override
	void drop(final StringBuilder bf)
	{
		dialect.dropForeignKeyConstraint(bf, quoteName(table.name), quoteName(name));
	}

	@Override
	void createInTable(final StringBuilder bf)
	{
		bf.append(",constraint ").
			append(quoteName(name)).
			append(" foreign key (").
			append(quoteName(foreignKeyColumn)).
			append(") references ").
			append(quoteName(targetTable));

		if(dialect.needsTargetColumnName())
		{
			bf.append('(').
				append(quoteName(targetColumn)).
				append(')');
		}

		dialect.appendForeignKeyCreateStatement(bf);
	}
}
