
package com.exedio.cope.lib;

import java.sql.SQLException;

class HsqldbDatabase extends Database
{

	String getIntegerType(final int precision)
	{
		// TODO: use precision to select between TINYINT, SMALLINT, INTEGER, BIGINT, NUMBER
		return "integer";
	}

	String getStringType(final int maxLength)
	{
		return "varchar("+maxLength+")";
	}
	
	private final String extractConstraintName(final SQLException e, final String start, final char end)
	{
		final String m = e.getMessage();
		if(m.startsWith(start))
		{
			final int pos = m.indexOf(end, start.length());
			if(pos<0)
				return null;
			return m.substring(start.length(), pos);
		}
		else
			return null;
	}
	
	protected String extractUniqueConstraintName(final SQLException e)
	{
		return extractConstraintName(e, "Violation of unique index: SYS_IDX_", '_');
	}

	protected String extractIntegrityConstraintName(final SQLException e)
	{
		return extractConstraintName(e, "Integrity constraint violation ", ' ');
	}

}
