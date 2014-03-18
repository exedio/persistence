package com.exedio.cope;

enum MysqlRowFormat
{
	/**
	 * This value means, that there is no ROW_FORMAT
	 * clause at all.
	 */
	NONE(null),

	DEFAULT, DYNAMIC, FIXED, COMPRESSED, REDUNDANT, COMPACT;

	final String sql;

	MysqlRowFormat(final String sql)
	{
		this.sql = sql;
	}

	MysqlRowFormat()
	{
		this.sql = name();
	}
}
