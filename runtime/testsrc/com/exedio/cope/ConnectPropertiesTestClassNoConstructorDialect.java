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
import com.exedio.dsmf.Sequence;
import java.sql.Connection;
import java.util.List;

class ConnectPropertiesTestClassNoConstructorDialect extends Dialect
{
	ConnectPropertiesTestClassNoConstructorDialect(final com.exedio.dsmf.Dialect dsmfDialect)
	{
		super(dsmfDialect);
	}
	@Override
	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		throw new RuntimeException();
	}
	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		throw new RuntimeException();
	}
	@Override
	String getDoubleType()
	{
		throw new RuntimeException();
	}
	@Override
	String getStringType(final int maxChars, final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		throw new RuntimeException();
	}
	@Override
	String getDayType()
	{
		throw new RuntimeException();
	}
	@Override
	String getDateTimestampType()
	{
		throw new RuntimeException();
	}
	@Override
	String getDateIntegerPrecision(final String quotedName, final Precision precision)
	{
		throw new RuntimeException();
	}
	@Override
	String getBlobType(final long maximumLength)
	{
		throw new RuntimeException();
	}
	@Override
	PageSupport getPageSupport()
	{
		throw new RuntimeException();
	}
	@Override
	void appendPageClause(final Statement bf, final int offset, final int limit)
	{
		throw new RuntimeException();
	}
	@Override
	void appendPageClause2(final Statement bf, final int offset, final int limit)
	{
		throw new RuntimeException();
	}
	@Override
	void appendAsString(final Statement bf, final NumberFunction<?> source, final Join join)
	{
		throw new RuntimeException();
	}
	@Override
	void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		throw new RuntimeException();
	}
	@Override
	void appendStartsWith(final Statement bf, final BlobColumn column, final byte[] value)
	{
		throw new RuntimeException();
	}
	@Override
	void deleteSchema(final List<Table> tables, final List<SequenceX> sequences, final ConnectionPool connectionPool)
	{
		throw new RuntimeException();
	}
	@Override
	void deleteSequence(
			final StringBuilder bf, final String quotedName,
			final Sequence.Type type, final long start)
	{
		throw new RuntimeException();
	}
	@Override
	Long nextSequence(final Executor executor, final Connection connection, final String quotedName)
	{
		throw new RuntimeException();
	}
	@Override
	Long getNextSequence(final Executor executor, final Connection connection, final String name)
	{
		throw new RuntimeException();
	}
}