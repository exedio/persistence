/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import org.hsqldb.jdbcDriver;

import com.exedio.dsmf.HsqldbDriver;

final class HsqldbDialect extends Dialect
{
	static
	{
		try
		{
			Class.forName(jdbcDriver.class.getName());
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	protected HsqldbDialect(final DialectParameters parameters)
	{
		super(
				new HsqldbDriver(),
				"LENGTH");
	}

	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		// TODO: select between TINYINT, SMALLINT, INTEGER, BIGINT, NUMBER
		return (minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? "integer" : "bigint";
	}

	@Override
	String getDoubleType()
	{
		return "double";
	}

	@Override
	String getStringType(final int maxLength)
	{
		return "varchar("+maxLength+")";
	}
	
	@Override
	String getDayType()
	{
		return "date";
	}
	
	@Override
	String getDateTimestampType()
	{
		return "timestamp";
	}

	@Override
	String getBlobType(final long maximumLength)
	{
		return "binary";
	}
	
	@Override
	int getBlobLengthFactor()
	{
		return 2;
	}

	@Override
	LimitSupport getLimitSupport()
	{
		return LimitSupport.CLAUSE_AFTER_SELECT;
	}

	@Override
	void appendLimitClause(final Statement bf, final int offset, final int limit)
	{
		assert offset>=0;
		assert limit>0 || limit==Query.UNLIMITED;
		assert offset>0 || limit>0;

		bf.append(" limit ").
			appendParameter(offset).
			append(' ').
			appendParameter(limit!=Query.UNLIMITED ? limit : 0);
	}
	
	@Override
	void appendLimitClause2(final Statement bf, final int offset, final int limit)
	{
		throw new RuntimeException(bf.toString());
	}
	
	@Override
	protected void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		appendMatchClauseByLike(bf, function, value);
	}
	
	@Override
	boolean fakesSupportReadCommitted()
	{
		return true;
	}
}
