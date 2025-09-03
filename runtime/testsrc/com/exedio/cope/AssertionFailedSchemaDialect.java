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

import com.exedio.dsmf.Dialect;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.opentest4j.AssertionFailedError;

@SuppressWarnings("RedundantThrows") // RedundantThrows: allow subclasses to throw exceptions
public class AssertionFailedSchemaDialect extends Dialect
{
	public AssertionFailedSchemaDialect()
	{
		super(false);
	}

	@Override
	public String quoteName(final String name)
	{
		throw new AssertionFailedError();
	}

	@Override
	public boolean supportsCheckConstraint()
	{
		throw new AssertionFailedError();
	}

	@Override
	public boolean supportsCheckConstraint(final String condition)
	{
		throw new AssertionFailedError();
	}

	@Override
	protected String adjustExistingCheckConstraintCondition(final String s)
	{
		throw new AssertionFailedError();
	}

	@Override
	protected String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
	{
		throw new AssertionFailedError();
	}

	@Override
	protected void verify(final Schema schema)
	{
		throw new AssertionFailedError();
	}

	@Override
	protected void appendTableCreateStatement(final StringBuilder sb)
	{
		throw new AssertionFailedError();
	}

	@Override
	public String renameColumn(final String tableName, final String name, final String newName, final String type)
	{
		throw new AssertionFailedError();
	}

	@Override
	public String modifyColumn(final String tableName, final String name, final String newType)
	{
		throw new AssertionFailedError();
	}

	@Override
	protected void dropPrimaryKeyConstraint(final StringBuilder sb, final String tableName, final String name)
	{
		throw new AssertionFailedError();
	}

	@Override
	protected void dropForeignKeyConstraint(final StringBuilder sb, final String tableName, final String name)
	{
		throw new AssertionFailedError();
	}

	@Override
	protected void dropUniqueConstraint(final StringBuilder sb, final String tableName, final String name)
	{
		throw new AssertionFailedError();
	}

	@Override
	protected void createSequence(
			final StringBuilder sb,
			final String name,
			final Sequence.Type type,
			final long start)
	{
		throw new AssertionFailedError();
	}

	@Override
	protected void dropSequence(final StringBuilder sb, final String name)
	{
		throw new AssertionFailedError();
	}

	@Override
	protected void renameSequence(
			final StringBuilder sb,
			final String name,
			final String newName)
	{
		throw new AssertionFailedError();
	}
}
