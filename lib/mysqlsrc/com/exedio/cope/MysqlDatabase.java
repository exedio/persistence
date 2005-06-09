/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.exedio.dsmf.MysqlDriver;
import com.mysql.jdbc.Driver;

/**
 * This MySQL driver requires the InnoDB engine.
 * It makes no sense supporting older engines,
 * since cope heavily depends on foreign key constraints.
 * @author Ralf Wiebicke
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
		super(new MysqlDriver(), properties, null);
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
	
	protected String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
	{
		switch(dataType)
		{
			case Types.INTEGER:
				return "integer";
			case Types.BIGINT:
				return "bigint";
			case Types.DOUBLE:
				return "double";
			case Types.TIMESTAMP:
				return "timestamp";
			case Types.VARCHAR:
				final int columnSize = resultSet.getInt("COLUMN_SIZE");
				return "varchar("+columnSize+") binary";
			default:
				return null;
		}
	}
	
	private static final char PROTECTOR = '`';

	protected String protectName(final String name)
	{
		return PROTECTOR + name + PROTECTOR;
	}
	
	private final String unprotectName(final String protectedName)
	{
		final int length = protectedName.length();
		if(length<3)
			throw new RuntimeException(protectedName);
		if(protectedName.charAt(0)!=PROTECTOR)
			throw new RuntimeException(protectedName);
		if(protectedName.charAt(length-1)!=PROTECTOR)
			throw new RuntimeException(protectedName);

		return protectedName.substring(1, protectedName.length()-1);
	}

	protected boolean supportsCheckConstraints()
	{
		return false;
	}

	private final String extractConstraintName(final SQLException e, final int vendorCode, final String start)
	{
		// TODO: MySQL does not deliver constraint name in exception
		//System.out.println("-u-"+e.getClass()+" "+e.getCause()+" "+e.getErrorCode()+" "+e.getLocalizedMessage()+" "+e.getSQLState()+" "+e.getNextException());

		if(e.getErrorCode()==vendorCode &&
				e.getMessage().startsWith(start))
			return ANY_CONSTRAINT;
		else
			return null;
	}

	protected String extractUniqueConstraintName(final SQLException e)
	{
		return extractConstraintName(e, 1062, "Duplicate entry ");
	}

	protected String extractIntegrityConstraintName(final SQLException e)
	{
		return extractConstraintName(e, 1217, "Cannot delete or update a parent row: a foreign key constraint fails");
	}

	void fillReport(final ReportSchema report)
	{
		super.fillReport(report);
		{
			for(Iterator i = report.getTables().iterator(); i.hasNext(); )
			{
				final ReportTable reportTable = (ReportTable)i.next();
				if(!reportTable.exists())
					continue;
				
				{
					final Statement bf = createStatement();
					bf.append("show columns from ").
						append(protectName(reportTable.name));
					executeSQLQuery(bf, new ResultSetHandler()
						{
							public void run(final ResultSet resultSet) throws SQLException
							{
								//printMeta(resultSet);
								while(resultSet.next())
								{
									//printRow(resultSet);
									final String key = resultSet.getString("Key");
									if("PRI".equals(key))
									{
										final String field = resultSet.getString("Field");
										if(Table.PK_COLUMN_NAME.equals(field) && reportTable.required())
										{
											for(Iterator j = reportTable.getConstraints().iterator(); j.hasNext(); )
											{
												final ReportConstraint c = (ReportConstraint)j.next();
												if(c instanceof ReportPrimaryKeyConstraint)
												{
													reportTable.notifyExistentPrimaryKeyConstraint(c.name);
													break;
												}
											}
										}
										else
											reportTable.notifyExistentPrimaryKeyConstraint(field+"_Pk");
									}
								}
							}
						}, false);
				}
				{
					final Statement bf = createStatement();
					bf.append("show create table ").
						append(protectName(reportTable.name));
					executeSQLQuery(bf, new ResultSetHandler()
						{
							public void run(final ResultSet resultSet) throws SQLException
							{
								while(resultSet.next())
								{
									final String tableName = resultSet.getString("Table");
									final String createTable = resultSet.getString("Create Table");
									final ReportTable table = report.notifyExistentTable(tableName);
									//System.out.println("----------"+tableName+"----"+createTable);
									final StringTokenizer t = new StringTokenizer(createTable);
									for(String s = t.nextToken(); t.hasMoreTokens(); s = t.nextToken())
									{
										//System.out.println("----------"+tableName+"---------------"+s);
										if("CONSTRAINT".equals(s))
										{
											if(!t.hasMoreTokens())
												continue;
											final String protectedName = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------protectedName:"+protectedName);
											final String name = unprotectName(protectedName);
											//System.out.println("----------"+tableName+"--------------------name:"+name);
											if(!t.hasMoreTokens() || !"FOREIGN".equals(t.nextToken()) ||
												!t.hasMoreTokens() || !"KEY".equals(t.nextToken()) ||
												!t.hasMoreTokens())
												continue;
											final String source = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------source:"+source);
											if(!t.hasMoreTokens() || !"REFERENCES".equals(t.nextToken()) ||
												!t.hasMoreTokens())
												continue;
											final String targetTable = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------targetTable:"+targetTable);
											if(!t.hasMoreTokens())
												continue;
											final String targetAttribute = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------targetAttribute:"+targetAttribute);
											
											table.notifyExistentForeignKeyConstraint(name);
										}
										//UNIQUE KEY `AttriEmptyItem_parKey_Unq` (`parent`,`key`)
										if("UNIQUE".equals(s))
										{
											if(!t.hasMoreTokens() || !"KEY".equals(t.nextToken()) ||
												!t.hasMoreTokens())
												continue;
											final String protectedName = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------protectedName:"+protectedName);
											final String name = unprotectName(protectedName);
											//System.out.println("----------"+tableName+"--------------------name:"+name);
											if(!t.hasMoreTokens())
												continue;
											final String clause = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------clause:"+clause);

											final int clauseLengthM1 = clause.length()-1;
											table.notifyExistentUniqueConstraint(name, clause.charAt(clauseLengthM1)==',' ? clause.substring(0, clauseLengthM1) : clause);
										}
									}
								}
							}
						}, false);
				}
			}
		}
	}

}
