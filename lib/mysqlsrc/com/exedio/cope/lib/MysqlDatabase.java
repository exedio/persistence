
package com.exedio.cope.lib;

import java.sql.SQLException;

import com.mysql.jdbc.Driver;

/**
 * This MySQL driver requires the InnoDB engine.
 * It makes no sense supporting older engines,
 * since cope heavily depends on foreign key constraints.
 */
public final class MysqlDatabase extends Database

// TODO
//	implements DatabaseTimestampCapable
// would require type "timestamp(14,3) null default null"
// but (14,3) is not yet supported
// "null default null" is needed to allow null and
// make null the default value
// This works with 4.1.6 and higher only

{
	static
	{
		try
		{
			Class.forName(Driver.class.getName());
		}
		catch(ClassNotFoundException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	protected MysqlDatabase(final Properties properties)
	{
		super(properties);
	}

	String getIntegerType(final int precision)
	{
		// TODO: use precision to select between TINYINT, SMALLINT, INTEGER, BIGINT, NUMBER
		return (precision <= 10) ? "integer" : "bigint";
	}

	String getDoubleType(final int precision)
	{
		return "double";
	}

	String getStringType(final int maxLength)
	{
		// TODO:
		// 255 is needed for unique columns only,
		// non-unique can have more,
		// and for longer unique columns you may specify a shorter key length
		
		// IMPLEMENTATION NOTE: "binary" is needed to make string comparisions case sensitive
		return "varchar("+(maxLength!=Integer.MAX_VALUE ? maxLength : 255)+") binary";
	}
	
	protected String protectName(final String name)
	{
		return '`' + name + '`';
	}

	protected boolean supportsForeignKeyConstraints()
	{
		return false;
	}

	protected String extractUniqueConstraintName(final SQLException e)
	{
		// TODO: MySQL does not deliver constraint name in exception
		//System.out.println("-u-"+e.getClass()+" "+e.getCause()+" "+e.getErrorCode()+" "+e.getLocalizedMessage()+" "+e.getSQLState()+" "+e.getNextException());
		return null;
	}

	protected String extractIntegrityConstraintName(final SQLException e)
	{
		// TODO: MySQL does not deliver constraint name in exception
		//System.out.println("-i-"+e.getClass()+" "+e.getCause()+" "+e.getErrorCode()+" "+e.getLocalizedMessage()+" "+e.getSQLState()+" "+e.getNextException());
		return null;
	}

	Statement getRenameColumnStatement(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(tableName).
			append(" change ").
			append(oldColumnName).
			append(' ').
			append(newColumnName).
			append(' ').
			append(columnType);
		return bf;
	}

	// TODO is same as hsqldb
	Statement getCreateColumnStatement(final String tableName, final String columnName, final String columnType)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(tableName).
			append(" add column ").
			append(columnName).
			append(' ').
			append(columnType);
		return bf;
	}

	Statement getModifyColumnStatement(final String tableName, final String columnName, final String newColumnType)
	{
		throw new RuntimeException("not implemented");
	}

}
