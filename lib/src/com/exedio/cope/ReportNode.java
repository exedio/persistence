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

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ReportNode
{
	static final int COLOR_NOT_YET_CALC = 0;
	public static final int COLOR_OK = 1;
	public static final int COLOR_WARNING = 2;
	public static final int COLOR_ERROR = 3;
	
	final Database database;

	protected String error = null;
	protected int particularColor = ReportSchema.COLOR_NOT_YET_CALC;
	protected int cumulativeColor = ReportSchema.COLOR_NOT_YET_CALC;
	
	ReportNode(final Database database)
	{
		this.database = database;
	}

	protected final String protectName(final String name)
	{
		return database.protectName(name);
	}
	
	protected final void executeSQL(final Statement statement)
	{
		Connection connection = null;
		java.sql.Statement sqlStatement = null;
		try
		{
			connection = database.connectionPool.getConnection();
			final String sqlText = statement.getText();
			//System.err.println(statement.getText());
			sqlStatement = connection.createStatement();
			final int rows = sqlStatement.executeUpdate(sqlText);

			//System.out.println("("+rows+"): "+statement.getText());
			if(rows!=0)
				throw new RuntimeException("expected no rows, but got "+rows+" on statement "+sqlText);
		}
		catch(SQLException e)
		{
			throw new NestingRuntimeException(e, statement.toString());
		}
		finally
		{
			if(sqlStatement!=null)
			{
				try
				{
					sqlStatement.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
			if(connection!=null)
			{
				try
				{
					database.connectionPool.putConnection(connection);
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}
	
	abstract void finish();

	public final String getError()
	{
		if(particularColor==ReportSchema.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return error;
	}

	public final int getParticularColor()
	{
		if(particularColor==ReportSchema.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return particularColor;
	}

	public final int getCumulativeColor()
	{
		if(cumulativeColor==ReportSchema.COLOR_NOT_YET_CALC)
			throw new RuntimeException();

		return cumulativeColor;
	}
}

