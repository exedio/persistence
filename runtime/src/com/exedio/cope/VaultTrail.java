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

import static com.exedio.cope.DataFieldVaultStore.mysqlExtendedVarchar;
import static com.exedio.cope.Executor.longResultSetHandler;
import static com.exedio.dsmf.Dialect.NOT_NULL;

import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vault.VaultPutInfo;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import java.sql.Connection;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class VaultTrail
{
	private final ConnectionPool connectionPool;
	private final Executor executor;

	private final VaultProperties props;
	private final int startLimit;
	private final int fieldLimit;
	private final int originLimit;

	private final String table;
	private final String hash;
	private final String hashPK;
	private final String length;
	private final String start;
	private final String date;
	private final String field;
	private final String origin;

	private final String tableQuoted;
	private final String hashQuoted;
	private final String lengthQuoted;
	private final String startQuoted;
	private final String dateQuoted;
	private final String fieldQuoted;
	private final String originQuoted;

	VaultTrail(
			final String serviceKey,
			final ConnectionPool connectionPool,
			final Executor executor,
			final Trimmer trimmer,
			final VaultProperties props)
	{
		this.connectionPool = connectionPool;
		this.executor = executor;

		this.props = props;
		this.startLimit = props.getTrailStartLimit();
		this.fieldLimit = props.getTrailFieldLimit();
		this.originLimit = props.getTrailOriginLimit();

		table   = trimmer.trimString("VaultTrail_" + serviceKey);
		hash    = trimmer.trimString("hash");
		hashPK  = trimmer.trimString(table + "_PK");
		length  = trimmer.trimString("length");
		start   = trimmer.trimString("start" + startLimit); // TODO use for StartsWithCondition
		date    = trimmer.trimString("date");
		field   = trimmer.trimString("field");
		origin  = trimmer.trimString("origin");

		final com.exedio.dsmf.Dialect d = executor.dialect.dsmfDialect;
		tableQuoted   = d.quoteName(table);
		hashQuoted    = d.quoteName(hash);
		lengthQuoted  = d.quoteName(length);
		startQuoted   = d.quoteName(start);
		dateQuoted    = d.quoteName(date);
		fieldQuoted   = d.quoteName(field);
		originQuoted  = d.quoteName(origin);
	}

	void makeSchema(
			final Schema schema,
			final Dialect dialect)
	{
		final Table tab = schema.newTable(table);
		tab.newColumn(hash, dialect.getStringType(
				props.getAlgorithmLength(), mysqlExtendedVarchar) + NOT_NULL).
				newPrimaryKey(hashPK);
		tab.newColumn(length,  dialect.getIntegerType(0, Long.MAX_VALUE) + NOT_NULL);
		tab.newColumn(start,   dialect.getBlobType(startLimit) + NOT_NULL);
		tab.newColumn(date,    dialect.getDateTimestampType()); // always use dateTimestamp, ignore supportsNativeDate
		tab.newColumn(field,   dialect.getStringType(fieldLimit, null));
		tab.newColumn(origin,  dialect.getStringType(originLimit, null));
	}

	LengthConsumer newDataConsumer()
	{
		return new LengthConsumer(startLimit);
	}

	void put(
			final String hashValue,
			final LengthConsumer lengthValue,
			final VaultPutInfo putInfo,
			final boolean result)
	{
		if(!result)
			return;

		final Statement bf = executor.newStatement();
		bf.append("INSERT INTO ").append(tableQuoted).
				append('(').append(hashQuoted).
				append(',').append(lengthQuoted).
				append(',').append(startQuoted);
		bf.
				append(',').append(dateQuoted).
				append(',').append(fieldQuoted).
				append(',').append(originQuoted).
				append(")VALUES(").
				appendParameter(hashValue).
				append(',').
				appendParameter(lengthValue.value()).
				append(',').
				appendParameterBlob(lengthValue.start());
		bf.
				append(',').
				appendParameterAny(new Date()).
				append(',').
				appendParameter(truncate(putInfo.getFieldString(), fieldLimit)).
				append(',').
				appendParameter(truncate(putInfo.getOrigin(), originLimit)).
				append(')');

		final Connection connection = connectionPool.get(true);
		try
		{
			final int rows = executor.update(connection, null, bf);
			if(rows>1)
				logger.error("{} rows {}", hashValue, rows);
		}
		catch(final SQLRuntimeException e)
		{
			if(logger.isErrorEnabled())
				logger.error(hashValue, e);
		}
		finally
		{
			connectionPool.put(connection);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(VaultTrail.class);

	static String truncate(final String s, final int limit)
	{
		if(s==null || s.isEmpty())
			return null;

		if(s.length()>limit)
			return s.substring(0, limit-POSTFIX.length()) + POSTFIX;

		return s;
	}

	private static final String POSTFIX = " ...";


	long check(final DataField field)
	{
		final Type<?> type = field.getType();
		final Transaction tx = type.getModel().currentTransaction();
		final Connection connection = tx.getConnection();
		final Executor executor = tx.connect.executor;
		final String alias1 = executor.dialect.dsmfDialect.quoteName(com.exedio.cope.Table.SQL_ALIAS_1);
		final String alias2 = executor.dialect.dsmfDialect.quoteName(com.exedio.cope.Table.SQL_ALIAS_2);

		final Statement bf = executor.newStatement(false);
		//language=SQL
		bf.append("SELECT COUNT(*) FROM ").
				append(type.getTable()).append(' ').append(alias1).
				append(" LEFT JOIN ").
				append(tableQuoted).append(' ').append(alias2).
				append(" ON ").
				append(alias1).append('.').append(field.getColumn()).
				append('=').
				append(alias2).append('.').append(hashQuoted).
				append(" WHERE ").
				append(alias1).append('.').append(field.getColumn()).append(" IS NOT NULL").
				append(" AND ").
				append(alias2).append('.').append(hashQuoted).append(" IS NULL");

		return executor.query(connection, bf, null, false, longResultSetHandler);
	}
}
