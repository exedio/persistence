
package com.exedio.cope.lib;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.lib.database.OracleDatabase;

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
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			createForeignKeyConstraints((Type)i.next());
	}

	public void dropTables()
	{
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			dropForeignKeyConstraints((Type)i.next());
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			dropTable((Type)i.next());
	}
	
	public void tearDownTables()
	{
		System.err.println("TEAR DOWN ALL TABLES");
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
		{
			try
			{
				final Type type = (Type)i.next();
				System.err.print("DROPPING FOREIGN KEY CONSTRAINTS "+type+"... ");
				dropForeignKeyConstraints(type);
				System.err.println("done.");
			}
			catch(SystemException e2)
			{
				System.err.println("failed:"+e2.getMessage());
			}
		}
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
		{
			try
			{
				final Type type = (Type)i.next();
				System.err.print("DROPPING TABLE "+type+" ... ");
				dropTable(type);
				System.err.println("done.");
			}
			catch(SystemException e2)
			{
				System.err.println("failed:"+e2.getMessage());
			}
		}
	}

	Collection search(final Query query)
	{
		final Type type = query.type;
		final Statement bf = new Statement();
		bf.append("select ").
			append(type.primaryKey.protectedName).
			append(" from ").
			append(type.protectedName).
			append(" where ");
		query.condition.appendStatement(bf);
		
		//System.out.println("searching "+bf.toString());
		try
		{
			final QueryResultSetHandler handler = new QueryResultSetHandler();
			executeSQL(bf, handler);
			return handler.result;
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
	}

	void load(final Row row)
	{
		final Type type = row.type;
		final List columns = type.getColumns();

		final Statement bf = new Statement();
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
			append(row.pk);

		//System.out.println("loading "+bf.toString());

		try
		{
			executeSQL(bf, new LoadResultSetHandler(row));
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
	}

	void store(final Row row)
			throws UniqueViolationException
	{
		final Type type = row.type;
		final List columns = type.getColumns();

		final Statement bf = new Statement();
		if(row.present)
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

				final Object value = row.store(column);
				bf.append(column.cacheToDatabase(value));
			}
			bf.append(" where ").
				append(type.primaryKey.protectedName).
				append('=').
				append(row.pk);
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
				append(row.pk);
			for(Iterator i = columns.iterator(); i.hasNext(); )
			{
				bf.append(',');
				final Column column = (Column)i.next();
				final Object value = row.store(column);
				bf.append(column.cacheToDatabase(value));
			}
			bf.append(')');
		}

		//System.out.println("storing "+bf.toString());

		executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
	}

	private static interface ResultSetHandler
	{
		public void run(ResultSet resultSet) throws SQLException;
	}

	private static final ResultSetHandler EMPTY_RESULT_SET_HANDLER = new ResultSetHandler()
	{
		public void run(ResultSet resultSet)
		{
		}
	};
		
	private static class QueryResultSetHandler implements ResultSetHandler
	{
		private final ArrayList result = new ArrayList();

		public void run(ResultSet resultSet) throws SQLException
		{
			// TODO: use special list for integers
			while(resultSet.next())
			{
				final Integer pk = new Integer(resultSet.getInt(1));
				//System.out.println("pk:"+pk);
				result.add(pk);
			}
		}
	}
	
	private static class LoadResultSetHandler implements ResultSetHandler
	{
		private final Row row;

		LoadResultSetHandler(final Row row)
		{
			this.row = row;
		}

		public void run(ResultSet resultSet) throws SQLException
		{
			final Type type = row.type;
			if(!resultSet.next())
				return;
			int columnIndex = 1;
			for(Iterator i = type.getColumns().iterator(); i.hasNext(); )
				((Column)i.next()).load(resultSet, columnIndex++, row);
			return;
		}
	}

	private static class MaxPKResultSetHandler implements ResultSetHandler
	{
		int result;

		public void run(ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();
			final BigDecimal o = (BigDecimal)resultSet.getObject(1);
			result =	(o==null) ? 0 : o.intValue()+1;
		}
	}

	private void executeSQL(final Statement statement, final ResultSetHandler resultSetHandler)
			throws UniqueViolationException
	{
		final ConnectionPool connectionPool = ConnectionPool.getInstance();
		
		Connection connection = null;
		java.sql.Statement sqlStatement = null;
		ResultSet resultSet = null;
		try
		{
			connection = connectionPool.getConnection();
			// TODO: use prepared statements and reuse the statement.
			sqlStatement = connection.createStatement();
			resultSet = sqlStatement.executeQuery(statement.getText());
			resultSetHandler.run(resultSet);
		}
		catch(SQLException e)
		{
			final UniqueViolationException wrappedException = wrapException(e);
			if(wrappedException!=null)
				throw wrappedException;
			else
				throw new SystemException(e, statement.toString());
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
					connectionPool.putConnection(connection);
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
			final UniqueConstraint constraint = UniqueConstraint.getUniqueConstraint(name);
			if(constraint==null)
				throw new SystemException(e, "no unique constraint found for >"+name+"<");
			return new UniqueViolationException(e, null, constraint);
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

	/**
	 * Protects a database name from being interpreted as a SQL keyword.
	 * This is usually done by using some (database specific) delimiters.
	 */
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
			executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
	}
	
	private void createForeignKeyConstraints(final Type type)
	{
		//System.out.println("createForeignKeyConstraints:"+bf);

		for(Iterator i = type.getColumns().iterator(); i.hasNext(); )
		{
			final Column column = (Column)i.next();
			//System.out.println("createForeignKeyConstraints("+column+"):"+bf);
			if(column instanceof IntegerColumn)
			{
				final IntegerColumn integerColumn = (IntegerColumn)column;
				if(integerColumn.foreignTable!=null)
				{
					final Statement bf = new Statement();
					bf.append("alter table ").
						append(type.protectedName).
						append(" add constraint ").
						append(Database.theInstance.protectName(column.trimmedName+"FK")).
						append(" foreign key (").
						append(column.protectedName).
						append(") references ").
						append(Database.theInstance.protectName(integerColumn.foreignTable));

					//System.out.println("createForeignKeyConstraints:"+bf);
					try
					{
						executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
					}
					catch(UniqueViolationException e)
					{
						throw new SystemException(e);
					}
				}
			}
		}
	}
	
	private void dropTable(final Type type)
	{
		type.onDropTable();

		final Statement bf = new Statement();
		bf.append("drop table ").
			append(type.protectedName);

		try
		{
			executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
	}
	
	private void dropForeignKeyConstraints(final Type type)
	{
		for(Iterator i = type.getColumns().iterator(); i.hasNext(); )
		{
			final Column column = (Column)i.next();
			//System.out.println("dropForeignKeyConstraints("+column+")");
			if(column instanceof IntegerColumn)
			{
				final IntegerColumn integerColumn = (IntegerColumn)column;
				if(integerColumn.foreignTable!=null)
				{
					final Statement bf = new Statement();
					boolean hasOne = false;

					bf.append("alter table ").
						append(type.protectedName).
						append(" drop constraint ").
						append(Database.theInstance.protectName(column.trimmedName+"FK"));

					//System.out.println("dropForeignKeyConstraints:"+bf);
					try
					{
						executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
					}
					catch(UniqueViolationException e)
					{
						throw new SystemException(e);
					}
				}
			}
		}
	}
	
	int getNextPK(final Type type)
	{
		final Statement bf = new Statement();
		bf.append("select max(").
			append(type.primaryKey.protectedName).
			append(") from ").
			append(type.protectedName);
			
		try
		{
			final MaxPKResultSetHandler handler = new MaxPKResultSetHandler();
			executeSQL(bf, handler);
			//System.err.println("select max("+type.primaryKey.trimmedName+") from "+type.trimmedName+" : "+handler.result);
			return handler.result;
		}
		catch(UniqueViolationException e)
		{
			throw new SystemException(e);
		}
	}
	
}
