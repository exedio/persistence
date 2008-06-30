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

import java.sql.ResultSet;
import java.sql.SQLException;


public class CheckConstraint extends Constraint
{
	public CheckConstraint(
			final Table table,
			final String name,
			final String condition)
	{
		this(table, name, true, condition);
	}

	CheckConstraint(
			final Table table,
			final String name,
			final boolean required,
			final String condition)
	{
		super(table, name, Type.Check, required, condition);
		
		if(condition==null)
			throw new RuntimeException(name);
	}

	@Override
	public boolean isSupported()
	{
		return driver.supportsCheckConstraints();
	}

	@Override
	public int check()
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("select count(*) from ").
			append(protectName(table.name)).
			append(" where not(").
			append(requiredCondition).
			append(')');

		//System.out.println("CHECKC:"+bf.toString());
		
		final CheckResultSetHandler handler = new CheckResultSetHandler();
		querySQL(bf.toString(), handler);
		return handler.result;
	}
	
	private static class CheckResultSetHandler implements ResultSetHandler
	{
		int result = Integer.MIN_VALUE;
		
		CheckResultSetHandler()
		{
			// make constructor non-private
		}

		public void run(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();
			
			result = resultSet.getInt(1);
		}
	}

	@Override
	final void createInTable(final StringBuilder bf)
	{
		bf.append(",constraint ").
			append(protectName(name)).
			append(" check(").
			append(requiredCondition).
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
			append(" check(").
			append(requiredCondition).
			append(')');

		executeSQL(bf.toString(), listener);
	}
	
	@Override
	public final void drop(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(protectName(table.name)).
			append(" drop constraint ").
			append(protectName(name));

		executeSQL(bf.toString(), listener);
	}
	
}
