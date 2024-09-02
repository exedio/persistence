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

import java.io.Serial;
import java.sql.SQLException;

/**
 * A wrapper for an unexpected SQLException.
 *
 * @author Ralf Wiebicke
 */
public final class SQLRuntimeException extends RuntimeException
{
	@Serial
	private static final long serialVersionUID = 1l;

	public SQLRuntimeException(final SQLException cause, final String statement)
	{
		this(cause, statement, 10000);
	}

	public SQLRuntimeException(final SQLException cause, final String statement, final int statementLimit)
	{
		super(truncateStatement(statement, statementLimit), cause);
	}

	private static String truncateStatement(final String value, int limit)
	{
		if(limit<200)
			limit = 200;

		if(value==null)
			return null;

		final int length = value.length();
		if(length<=limit)
			return value;

		return
			value.substring(0, limit-100) + " ... " + value.substring(length-20, length) +
			" (truncated, was " + length + " characters)";
	}
}
