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

import java.sql.ResultSet;
import java.sql.SQLException;

public final class CheckConstraint extends Constraint
{
	/**
	 * @deprecated Use {@link Column#newCheck(String,String)} instead
	 */
	@Deprecated
	public CheckConstraint(
			final Column column,
			final String name,
			final String condition)
	{
		this(column.table, column, name, true, condition);
	}

	/**
	 * @deprecated Use {@link Table#newCheck(String,String)} instead
	 */
	@Deprecated
	public CheckConstraint(
			final Table table,
			final String name,
			final String condition)
	{
		this(table, null, name, true, condition);
	}

	CheckConstraint(
			final Table table,
			final Column column,
			final String name,
			final boolean required,
			final String condition)
	{
		super(table, column, name, Type.Check, required, condition);

		if(condition==null)
			throw new RuntimeException(name);
	}

	@Override
	public boolean isSupported()
	{
		return dialect.supportsCheckConstraints();
	}

	@Override
	String adjustExistingCondition(final String s)
	{
		return dialect.adjustExistingCheckConstraintCondition(s);
	}

	@Override
	public long checkL()
	{
		final CheckResultSetHandler handler = new CheckResultSetHandler();
		querySQL(
				"SELECT COUNT(*) FROM " + quoteName(table.name) +
				" WHERE NOT(" + requiredCondition + ')',
				handler);
		return handler.result;
	}

	private static class CheckResultSetHandler implements ResultSetHandler
	{
		long result = Long.MIN_VALUE;

		CheckResultSetHandler()
		{
			// make constructor non-private
		}

		@Override
		public void run(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();

			result = resultSet.getLong(1);
		}
	}

	@Override
	void createInTable(final StringBuilder bf)
	{
		assert isSupported() : getRequiredCondition();
		bf.append(",CONSTRAINT ").
			append(quoteName(name)).
			append(" CHECK(").
			append(requiredCondition).
			append(')');
	}

	@Override
	void create(final StringBuilder bf)
	{
		bf.append("ALTER TABLE ").
			append(quoteName(table.name)).
			append(" ADD CONSTRAINT ").
			append(quoteName(name)).
			append(" CHECK(").
			append(requiredCondition).
			append(')');
	}

	@Override
	void drop(final StringBuilder bf)
	{
		bf.append("ALTER TABLE ").
			append(quoteName(table.name)).
			append(" DROP CONSTRAINT ").
			append(quoteName(name));
	}
}
