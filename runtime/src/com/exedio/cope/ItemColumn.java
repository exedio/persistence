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

import static com.exedio.cope.Intern.intern;

import com.exedio.cope.ConnectProperties.TrimClass;

final class ItemColumn extends IntegerColumn
{
	private final Type<?> targetType;
	final String foreignKeyConstraintName;

	ItemColumn(
			final Table table,
			final String id,
			final boolean optional,
			final Type<?> targetType)
	{
		super(table, id, false, optional, PK.MIN_VALUE, targetType.createLimit, true);
		this.targetType = targetType;
		this.foreignKeyConstraintName = intern(makeGlobalID(TrimClass.standard, "Fk"));
	}

	/**
	 * Creates a primary key column with a foreign key contraint.
	 */
	ItemColumn(final Table table, final Type<?> targetType, final long maximum)
	{
		super(table, maximum);
		assert targetType!=null;
		this.targetType = targetType;
		this.foreignKeyConstraintName = table.id + "_Sup";
	}

	@Override
	void makeSchema(final com.exedio.dsmf.Column dsmf)
	{
		super.makeSchema(dsmf);
		final Table targetTable = targetType.getTable();
		dsmf.newForeignKey(foreignKeyConstraintName, targetTable.id, targetTable.primaryKey.id);
	}
}
