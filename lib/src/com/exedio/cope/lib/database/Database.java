
package com.exedio.cope.lib.database;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.BooleanAttribute;
import com.exedio.cope.lib.EnumerationAttribute;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.SystemException;
import com.exedio.cope.lib.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class Database
{
	
	public Database()
	{
	}
	
	private char getNameDelimiterStart()
	{
		return '"';
	}
	
	private char getNameDelimiterEnd()
	{
		return '"';
	}
	
	private String getSyntheticPrimaryKeyType()
	{
		return "number(10,0)";
	}
	
	public void createTable(final Type type)
	{
		executeSQL(getCreateTableStatement(type));
	}
	
	public void dropTable(final Type type)
	{
		executeSQL(getDropTableStatement(type));
	}
	
	private void executeSQL(final String sql)
	{
		final String driver = "oracle.jdbc.driver.OracleDriver";
		final String url = "jdbc:oracle:thin:@database3.exedio.com:1521:DB3";
		final String user = "wiebicke";
		final String password = "wiebicke1234";
		
		try
		{
			Class.forName(driver);
		}
		catch(ClassNotFoundException e)
		{
			throw new SystemException(e);
		}
		
		Connection connection = null;
		Statement statement = null;
		try
		{
			connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();
			statement.execute(sql);
		}
		catch(SQLException e)
		{
			throw new SystemException(e, sql);
		}
		finally
		{
			if(connection!=null)
			{
				try
				{
					connection.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
			if(statement!=null)
			{
				try
				{
					statement.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}
	
	private String getPersistentQualifier(final Type type)
	{
		final String className = type.getJavaClass().getName();
		final int pos = className.lastIndexOf('.');
		return className.substring(pos+1);
	}
	
	private String getPersistentQualifier(final Attribute attribute)
	{
		return attribute.getName();
	}
	
	private String getPersistentType(final Attribute attribute)
	{
		if(attribute instanceof StringAttribute)
			return "varchar2(4000)";
		else if(attribute instanceof BooleanAttribute)
			return "number(1,0)";
		else if(attribute instanceof IntegerAttribute)
			return "number(20,0)";
		else if(attribute instanceof EnumerationAttribute)
			return "number(10,0)";
		else if(attribute instanceof ItemAttribute)
			return getSyntheticPrimaryKeyType();
		else
			throw new RuntimeException(attribute.toString());
	}
	
	private String getCreateTableStatement(final Type type)
	{
		final char delimiterStart = getNameDelimiterStart();
		final char delimiterEnd = getNameDelimiterEnd();

		final StringBuffer bf = new StringBuffer();
		bf.append("create table ").
			append(delimiterStart).
			append(getPersistentQualifier(type)).
			append(delimiterEnd).
			append('(');

		bf.append("\"PK\" ").
			append(getSyntheticPrimaryKeyType());
		
		for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
		{
			bf.append(',');
			final Attribute attribute = (Attribute)i.next();
			bf.append(delimiterStart).
				append(getPersistentQualifier(attribute)).
				append(delimiterEnd).
				append(" ").
				append(getPersistentType(attribute));
		}
		bf.append(')');
		return bf.toString();
	}
	
	private String getDropTableStatement(final Type type)
	{
		final char delimiterStart = getNameDelimiterStart();
		final char delimiterEnd = getNameDelimiterEnd();

		final StringBuffer bf = new StringBuffer();
		bf.append("drop table ").
			append(delimiterStart).
			append(getPersistentQualifier(type)).
			append(delimiterEnd);
		return bf.toString();
	}
	
}
