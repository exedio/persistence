
package com.exedio.cope.lib;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.AttributeMapping;
import com.exedio.cope.lib.BooleanAttribute;
import com.exedio.cope.lib.EnumerationAttribute;
import com.exedio.cope.lib.EnumerationValue;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.MediaAttribute;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.SystemException;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.database.OracleDatabase;
import com.exedio.cope.lib.search.Condition;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public abstract class Database
{
	public static final Database theInstance = new OracleDatabase();
	
	protected Database()
	{
	}
	
	public void createTables()
	{
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			createTable((Type)i.next());
	}

	public void createTablesDesperatly()
	{
		try
		{
			for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			{
				final Type type = (Type)i.next();
				createTable(type);
			}
		}
		catch(SystemException e)
		{
			e.printStackTrace();
			System.err.println("DROPPING ALL TABLES");
			for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			{
				try
				{
					final Type type = (Type)i.next();
					System.err.print("DROPPING "+type+" ... ");
					dropTable(type);
					System.err.println("done.");
				}
				catch(SystemException e2)
				{
					System.err.println("failed:"+e2.getMessage());
				}
			}
			throw e;
		}
	}

	public void dropTables()
	{
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			dropTable((Type)i.next());
	}

	public static final class Statement
	{
		final StringBuffer text = new StringBuffer();
		
		private Statement()
		{
		}

		public Statement append(final String text)
		{
			this.text.append(text);
			return this;
		}
		
		public Statement append(final char text)
		{
			this.text.append(text);
			return this;
		}
		
		public Statement append(final Attribute attribute)
		{
			final AttributeMapping mapping = attribute.mapping;
			if(attribute.mapping!=null)
			{
				this.text.append(mapping.sqlMappingStart);
				append(mapping.sourceAttribute);
				this.text.append(mapping.sqlMappingEnd);
			}
			else
				this.text.append(attribute.getMainColumn().protectedName);
			
			return this;
		}
		
		public Statement appendValue(Attribute attribute, final Object value)
		{
			while(attribute.mapping!=null)
				attribute = attribute.mapping.sourceAttribute;

			this.text.append(attribute.getMainColumn().cacheToDatabase(attribute.surfaceToCache(value)));
			return this;
		}
		
		public String getText()
		{
			return text.toString();
		}

		public String toString()
		{
			return text.toString();
		}
	}

	Collection search(final Type type, final Condition condition)
	{
		final Statement bf = new Statement();
		bf.append("select ").
			append(type.primaryKey.protectedName).
			append(" from ").
			append(type.protectedName).
			append(" where ");
		condition.appendStatement(bf);
		
		//System.out.println("searching "+bf.toString());
		return executeSQLQuery(bf.getText());
	}

	void load(final Type type, final int pk, final HashMap itemCache)
	{
		final List columns = type.getColumns();

		// TODO: use prepared statements and reuse the statement.
		// TODO: use Statement class
		final StringBuffer bf = new StringBuffer();
		bf.append("select ");

		boolean first = true;
		for(Iterator i = columns.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				bf.append(',');
			final Column column = (Column)i.next();
			bf.append(column.protectedName);
		}
		bf.append(" from ").
			append(type.protectedName).
			append(" where ").
			append(type.primaryKey.protectedName).
			append('=').
			append(pk);

		//System.out.println("loading "+bf.toString());

		executeSQLLoad(bf.toString(), columns, itemCache);
	}

	void store(final Type type, final int pk, final HashMap itemCache, final boolean present)
			throws UniqueViolationException
	{
		final List columns = type.getColumns();

		// TODO: use prepared statements and reuse the statement.
		// TODO: use Statement class
		final StringBuffer bf = new StringBuffer();
		if(present)
		{
			bf.append("update ").
				append(type.protectedName).
				append(" set ");

			boolean first = true;
			for(Iterator i = columns.iterator(); i.hasNext(); )
			{
				if(first)
					first = false;
				else
					bf.append(',');

				final Column column = (Column)i.next();
				bf.append(column.protectedName).
					append('=');

				final Object value = itemCache.get(column);
				bf.append(column.cacheToDatabase(value));
			}
			bf.append(" where ").
				append(type.primaryKey.protectedName).
				append('=').
				append(pk);
		}
		else
		{
			bf.append("insert into ").
				append(type.protectedName).
				append("(").
				append(type.primaryKey.protectedName);

			boolean first = true;
			for(Iterator i = columns.iterator(); i.hasNext(); )
			{
				bf.append(',');
				final Column column = (Column)i.next();
				bf.append(column.protectedName);
			}

			bf.append(")values(").
				append(pk);
			for(Iterator i = columns.iterator(); i.hasNext(); )
			{
				bf.append(',');
				final Column column = (Column)i.next();
				final Object value = itemCache.get(column);
				bf.append(column.cacheToDatabase(value));
			}
			bf.append(')');
		}

		//System.out.println("storing "+bf.toString());

		executeSQL(bf.toString());
	}

	private void executeSQL(final String sql)
			throws UniqueViolationException
	{
		executeSQLInternal(sql, 0, null, null);
	}
	
	private ArrayList executeSQLQuery(final String sql)
	{
		try
		{
			return executeSQLInternal(sql, 1, null, null);
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
	}
		
	private void executeSQLLoad(final String sql, final List columns, final HashMap itemCache)
	{
		try
		{
			executeSQLInternal(sql, 2, columns, itemCache);
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
	}
		
	private ArrayList executeSQLInternal(final String sql, final int type, final List columns, final HashMap itemCache)
			throws UniqueViolationException
	{
		final String driver = "oracle.jdbc.driver.OracleDriver";
		final String url = "jdbc:oracle:thin:@database3.exedio.com:1521:DB3";
		final String user = "wiebicke2";
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
		java.sql.Statement statement = null;
		ResultSet resultSet = null;
		try
		{
			connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();
			switch(type)
			{
				case 0:
				{
					statement.execute(sql);
					return null;
				}
				case 1:
				{
					resultSet = statement.executeQuery(sql);
					// TODO: use special list for integers
					final ArrayList result = new ArrayList();
					while(resultSet.next())
					{
						final Integer pk = new Integer(resultSet.getInt(1));
						//System.out.println("pk:"+pk);
						result.add(pk);
					}
					return result;
				}
				case 2:
				{
					resultSet = statement.executeQuery(sql);
					if(!resultSet.next())
						return null;
					int columnIndex = 1;
					for(Iterator i = columns.iterator(); i.hasNext(); )
						((Column)i.next()).load(resultSet, columnIndex++, itemCache);
					return null;
				}
				default:
					throw new RuntimeException("type"+type);
			}
		}
		catch(SQLException e)
		{
			final UniqueViolationException wrappedException = wrapException(e);
			if(wrappedException!=null)
				throw wrappedException;
			else
				throw new SystemException(e, sql);
		}
		finally
		{
			if(resultSet!=null)
			{
				try
				{
					resultSet.close();
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
		}
	}
	
	private static final String UNIQUE_VIOLATION_START = "ORA-00001: unique constraint (";
	private static final String UNIQUE_VIOLATION_END   = ") violated\n";

	private UniqueViolationException wrapException(final SQLException e)
	{
		final String m = e.getMessage();
		if(m.startsWith(UNIQUE_VIOLATION_START) && m.endsWith(UNIQUE_VIOLATION_END))
		{
			final int pos = m.indexOf('.', UNIQUE_VIOLATION_START.length());
			final String name = m.substring(pos+1, m.length()-UNIQUE_VIOLATION_END.length());
			return new UniqueViolationException(e, null, UniqueConstraint.getUniqueConstraint(name));
		}
		else
			return null;
	}
	
	String trimName(final Type type)
	{
		final String className = type.getJavaClass().getName();
		final int pos = className.lastIndexOf('.');
		return className.substring(pos+1);
	}
	
	/**
	 * Trims a name to length for being be a suitable qualifier for database entities,
	 * such as tables, columns, indexes, constraints, partitions etc.
	 */
	String trimName(final String longName)
	{
		return longName; // TODO: we should actually do some shortening
	}

	public abstract String protectName(final String name);

	private void createTable(final Type type)
	{
		final Statement bf = new Statement();
		bf.append("create table ").
			append(type.protectedName).
			append('(');

		final Column primaryKey = type.primaryKey;
		bf.append(primaryKey.protectedName).
			append(' ').
			append(primaryKey.databaseType).
			append(" primary key");
		
		for(Iterator i = type.getColumns().iterator(); i.hasNext(); )
		{
			final Column column = (Column)i.next();
			//System.out.println("getCreateTableStatement:"+column);
			bf.append(',').
				append(column.protectedName).
				append(' ').
				append(column.databaseType);
			
			if(column.notNull)
				bf.append(" not null");
		}
		
		for(Iterator i = type.getUniqueConstraints().iterator(); i.hasNext(); )
		{
			final UniqueConstraint uniqueConstraint = (UniqueConstraint)i.next();
			bf.append(",constraint ").
				append(uniqueConstraint.protectedName).
				append(" unique(");
			boolean first = true;
			for(Iterator j = uniqueConstraint.getUniqueAttributes().iterator(); j.hasNext(); )
			{
				if(first)
					first = false;
				else
					bf.append(',');
				final Attribute uniqueAttribute = (Attribute)j.next();
				bf.append(uniqueAttribute.getMainColumn().protectedName);
			}
			bf.append(')');
		}
		
		bf.append(')');

		try
		{
			executeSQL(bf.toString());
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
	}
	
	private void dropTable(final Type type)
	{
		// TODO: use Statement class
		final StringBuffer bf = new StringBuffer();
		bf.append("drop table ").
			append(type.protectedName);

		try
		{
			executeSQL(bf.toString());
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
	}
	
}
