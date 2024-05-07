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

import com.exedio.cope.DateField.Precision;
import java.sql.Connection;
import java.util.List;
import java.util.function.Consumer;

class AssertionFailedDialect extends Dialect
{
	AssertionFailedDialect(final com.exedio.dsmf.Dialect dsmfDialect)
	{
		super(dsmfDialect);
	}
	@Override
	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		throw new AssertionError();
	}
	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		throw new AssertionError();
	}
	@Override
	String getDoubleType()
	{
		throw new AssertionError();
	}
	@Override
	String getStringType(final int maxChars, final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		throw new AssertionError();
	}
	@Override
	String getDayType()
	{
		throw new AssertionError();
	}
	@Override
	String getDateTimestampType()
	{
		throw new AssertionError();
	}
	@Override
	String getDateIntegerPrecision(final String quotedName, final Precision precision)
	{
		throw new AssertionError();
	}
	@Override
	String getBlobType(final long maximumLength)
	{
		throw new AssertionError();
	}
	@Override
	void appendPageClauseAfter(final Statement bf, final int offset, final int limit)
	{
		throw new AssertionError();
	}
	@Override
	void appendAsString(final Statement bf, final NumberFunction<?> source, final Join join)
	{
		throw new AssertionError();
	}
	@Override
	void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		throw new AssertionError();
	}
	@Override
	void appendStartsWith(final Statement bf, final Consumer<Statement> column, final int offset, final byte[] value)
	{
		throw new AssertionError();
	}
	@Override
	void appendRegexpLike(final Statement bf, final StringFunction function, final String regexp)
	{
		throw new AssertionError();
	}
	@Override
	String getClause(final String column, final String regexp)
	{
		throw new AssertionError();
	}
	@Override
	void deleteSchema(final List<Table> tables, final List<SequenceX> sequences, final boolean forTest, final ConnectionPool connectionPool)
	{
		throw new AssertionError();
	}
	@Override
	void deleteSequence(
			final StringBuilder bf, final String quotedName,
			final long start)
	{
		throw new AssertionError();
	}
	@Override
	Long nextSequence(final Executor executor, final Connection connection, final String quotedName)
	{
		throw new AssertionError();
	}
	@Override
	Long getNextSequence(final Executor executor, final Connection connection, final String name)
	{
		throw new AssertionError();
	}
	@Override
	void append(
			final VaultTrail trail,
			final Statement bf,
			final String hashValue,
			final DataConsumer consumer,
			final boolean markPutEnabled,
			final DataField fieldValue)
	{
		throw new AssertionError();
	}
}
