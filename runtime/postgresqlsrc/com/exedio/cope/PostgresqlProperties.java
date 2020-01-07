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

import com.exedio.cope.util.Properties;

final class PostgresqlProperties extends Properties
{
	private static final String searchPathDEFAULT = "\"$user\"";
	final String searchPath = valueX("search_path", searchPathDEFAULT, ',');

	String schema(final ConnectProperties connect)
	{
		return
				searchPathDEFAULT.equals(searchPath)
				? connect.connection.username
				: searchPath;
	}


	/**
	 * Requires pgcrypto extension in the schema set in this property.
	 * Set to {@code <disabled>}, if pgcrypto extension should not be required.
	 * Then cope will just support MD5.
	 *
	 * To enable pgcrypto do
	 * <pre>
	 * sudo su - postgres
	 * psql
	 * \connect database_name
	 * CREATE EXTENSION pgcrypto WITH SCHEMA "public";
	 * </pre>
	 * https://www.postgresql.org/docs/9.6/sql-createextension.html
	 */
	final String pgcryptoSchema = valueX("pgcryptoSchema", "public", '"');


	private String valueX(final String key, final String defaultValue, final char forbidden)
	{
		final String result = value(key, defaultValue);

		final int position = result.indexOf(forbidden);
		if(position>=0)
			throw newException(key,
				"must not contain '" + forbidden + "', " +
				"but did at position " + position + " and was '" + result + '\'');

		return result;
	}

	PostgresqlProperties(final Source source) { super(source); }
}
