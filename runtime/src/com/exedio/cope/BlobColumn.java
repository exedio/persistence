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

import static com.exedio.cope.Executor.NO_SUCH_ROW;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

final class BlobColumn extends Column
{
	private final long maximumLength;

	BlobColumn(
			final Table table,
			final String id,
			final boolean optional,
			final long maximumLength)
	{
		super(table, id, false, Kind.of(false, optional));
		this.maximumLength = maximumLength;
	}

	@Override
	String getDatabaseType()
	{
		return table.database.dialect.getBlobType(maximumLength);
	}

	@Override
	void makeSchema(final com.exedio.dsmf.Column dsmf)
	{
		newCheck(dsmf, "MX",
				table.database.dialect.getBlobLength() + '(' + quotedID + ")<=" + maximumLength);
	}

	@Override
	void load(final ResultSet resultSet, final int columnIndex, final Row row)
	{
		throw new RuntimeException(id);
	}

	@Override
	String cacheToDatabase(final Object cache)
	{
		throw new RuntimeException(id);
	}

	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		throw new RuntimeException(id);
	}


	private <R> R select(
			final Transaction tx, final Item item,
			final Executor.ResultSetHandler<R> resultSetHandler)
	{
		final Table table = this.table;
		final Executor executor = tx.connect.executor;
		final Statement bf = executor.newStatement();
		bf.append("SELECT ").
			append(quotedID).
			append(" FROM ").
			append(table.quotedID).
			append(" WHERE ").
			append(table.primaryKey.quotedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);

		return executor.query(tx.getConnection(), bf, null, false, resultSetHandler);
	}

	byte[] load(final Transaction tx, final Item item)
	{
		return select(tx, item, resultSet ->
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);

				return resultSet.getBytes(1);
			}
		);
	}

	void load(final Transaction tx, final Item item, final OutputStream sink, final DataField field)
	{
		select(tx, item, resultSet ->
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);

				tx.connect.executor.dialect.fetchBlob(resultSet, 1, item, sink, field);

				return null;
			}
		);
	}

	long loadLength(final Transaction tx, final Item item)
	{
		final Table table = this.table;
		final Executor executor = tx.connect.executor;
		final Statement bf = executor.newStatement();
		bf.append("SELECT ").append(table.database.dialect.getBlobLength()).append('(').
			append(quotedID).
			append(") FROM ").
			append(table.quotedID).
			append(" WHERE ").
			append(table.primaryKey.quotedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);

		return executor.query(tx.getConnection(), bf, null, false, resultSet ->
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);

				final long result = resultSet.getLong(1);
				if(resultSet.wasNull())
					return -1l;

				return result;
			}
		);
	}

	boolean isNull(final Transaction tx, final Item item)
	{
		final Table table = this.table;
		final Executor executor = tx.connect.executor;
		final Statement bf = executor.newStatement();
		bf.append("SELECT ");
		bf.append(quotedID).
			append(" IS NULL FROM ").
			append(table.quotedID).
			append(" WHERE ").
			append(table.primaryKey.quotedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);

		return executor.query(tx.getConnection(), bf, null, false, resultSet ->
		{
			if(!resultSet.next())
				throw new SQLException(NO_SUCH_ROW);

			return resultSet.getBoolean(1);
		});
	}
}
