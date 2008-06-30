/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

public class PrimaryKeyConstraint extends Constraint
{
	final String primaryKeyColumn;
	
	public PrimaryKeyConstraint(
			final Table table,
			final String name,
			final String primaryKeyColumn)
	{
		this(table, name, true, primaryKeyColumn);
	}
	
	PrimaryKeyConstraint(
			final Table table,
			final String name,
			final boolean required,
			final String primaryKeyColumn)
	{
		super(table, name, Type.PrimaryKey, false, required, null);

		if(required && primaryKeyColumn==null)
			throw new RuntimeException(name);
		
		this.primaryKeyColumn = primaryKeyColumn;
		//System.out.println("-------------"+name+"-"+primaryKeyColumn);
	}
	
	public final String getPrimaryKeyColumn()
	{
		return primaryKeyColumn;
	}

	@Override
	final void createInTable(final StringBuilder bf)
	{
		bf.append(",constraint ").
			append(protectName(name)).
			append(" primary key(").
			append(protectName(primaryKeyColumn)).
			append(')');
	}
	
	@Override
	public final void create(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(protectName(table.name)).
			append(" add constraint ").
			append(protectName(name)).
			append(" primary key(").
			append(protectName(primaryKeyColumn)).
			append(')');

		executeSQL(bf.toString(), listener);
	}
	
	@Override
	public final void drop(final StatementListener listener)
	{
		executeSQL(driver.dropPrimaryKeyConstraint(protectName(table.name), protectName(name)), listener);
	}
}
