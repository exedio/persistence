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

package com.exedio.cope;

import static java.util.Objects.requireNonNull;

import com.exedio.dsmf.Table;

final class ForeignKeyConstraintCollector
{
	final Table table;
	final String referencedTableName;
	final String deleteRule;
	final String updateRule;

	private String columnName;
	private String referencedColumnName;

	ForeignKeyConstraintCollector(
			final Table table,
			final String referencedTableName,
			final String deleteRule,
			final String updateRule)
	{
		this.table = requireNonNull(table);
		this.referencedTableName = requireNonNull(referencedTableName);
		this.deleteRule = requireNonNull(deleteRule);
		this.updateRule = requireNonNull(updateRule);
	}

	void setKeyColumnUsage(
			final String columnName,
			final String referencedColumnName)
	{
		if(this.columnName!=null)
			throw new RuntimeException(this.toString() + '|' + columnName + '/' + referencedColumnName);

		this.columnName = requireNonNull(columnName);
		this.referencedColumnName = requireNonNull(referencedColumnName);
	}

	String columnName()
	{
		return requireNonNull(columnName);
	}

	String referencedColumnName()
	{
		return requireNonNull(referencedColumnName);
	}

	@Override
	public String toString()
	{
		return
				table.toString() + '/' +
				referencedTableName + '/' +
				deleteRule + '/' +
				updateRule + '/' +
				columnName + '/' +
				referencedColumnName;
	}
}
