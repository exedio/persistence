/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.dsmf.ForeignKeyConstraint;

final class ItemColumn extends IntegerColumn
{
	final Class<? extends Item> targetTypeClass;
	final String integrityConstraintName;

	ItemColumn(final Table table, final String id,
					  final boolean optional,
					  final Class<? extends Item> targetTypeClass)
	{
		super(table, id, optional, Type.MIN_PK, Type.MAX_PK, false);
		if(targetTypeClass==null)
			throw new RuntimeException();
		this.targetTypeClass = targetTypeClass;
		this.integrityConstraintName = table.database.makeName( table.id + '_' + this.id/* not equal to "id"! */ + "_Fk" ).intern();
	}

	/**
	 * Creates a primary key column with a foreign key contraint.
	 */
	ItemColumn(final Table table, final Class<? extends Item> targetTypeClass)
	{
		super(table);
		if(targetTypeClass==null)
			throw new RuntimeException();
		this.targetTypeClass = targetTypeClass;
		this.integrityConstraintName = table.id+"_Sup";
	}

	@Override
	void makeSchema(final com.exedio.dsmf.Table dsmfTable)
	{
		super.makeSchema(dsmfTable);
		final Table targetTable = Type.findByJavaClass(targetTypeClass).getTable();
		new ForeignKeyConstraint(dsmfTable, integrityConstraintName, id, targetTable.id, targetTable.primaryKey.id);
	}
		
}
