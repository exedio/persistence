
package com.exedio.cope.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class Database
{
	public static final Database theInstance = createInstance();
	
	private static final Database createInstance()
	{
		final String databaseName = Properties.getInstance().getDatabase();

		final Class databaseClass;
		try
		{
			databaseClass = Class.forName(databaseName);
		}
		catch(ClassNotFoundException e)
		{
			final String m = "ERROR: class "+databaseName+" from cope.properties not found.";
			System.err.println(m);
			throw new RuntimeException(m);
		}
		
		if(!Database.class.isAssignableFrom(databaseClass))
		{
			final String m = "ERROR: class "+databaseName+" from cope.properties not a subclass of "+Database.class.getName()+".";
			System.err.println(m);
			throw new RuntimeException(m);
		}

		final Constructor constructor;
		try
		{
			constructor = databaseClass.getDeclaredConstructor(new Class[]{});
		}
		catch(NoSuchMethodException e)
		{
			final String m = "ERROR: class "+databaseName+" from cope.properties has no default constructor.";
			System.err.println(m);
			throw new RuntimeException(m);
		}

		try
		{
			return (Database)constructor.newInstance(new Object[]{});
		}
		catch(InstantiationException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
			throw new SystemException(e);
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
			throw new SystemException(e);
		}
		catch(InvocationTargetException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
			throw new SystemException(e);
		}
	}
	
	private final boolean useDefineColumnTypes;
	
	protected Database(final boolean useDefineColumnTypes)
	{
		this.useDefineColumnTypes = useDefineColumnTypes;
		//System.out.println("using database "+getClass());
	}
	
	private final Statement createStatement()
	{
		return new Statement(useDefineColumnTypes);
	}
	
	//private static int createTableTime = 0, dropTableTime = 0, checkEmptyTableTime = 0;
	
	public void createDatabase()
	{
		//final long time = System.currentTimeMillis();
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			createTable((Type)i.next());
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			createForeignKeyConstraints((Type)i.next());
		//final long amount = (System.currentTimeMillis()-time);
		//createTableTime += amount;
		//System.out.println("CREATE TABLES "+amount+"ms  accumulated "+createTableTime);
	}

	public void dropDatabase()
	{
		//final long time = System.currentTimeMillis();
		final List types = Type.getTypes();
		// must delete in reverse order, to obey integrity constraints
		for(ListIterator i = types.listIterator(types.size()); i.hasPrevious(); )
			dropForeignKeyConstraints((Type)i.previous());
		for(ListIterator i = types.listIterator(types.size()); i.hasPrevious(); )
			dropTable((Type)i.previous());
		//final long amount = (System.currentTimeMillis()-time);
		//dropTableTime += amount;
		//System.out.println("DROP TABLES "+amount+"ms  accumulated "+dropTableTime);
	}
	
	public void tearDownDatabase()
	{
		System.err.println("TEAR DOWN ALL DATABASE");
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

	public void checkEmptyTables()
	{
		//final long time = System.currentTimeMillis();
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
		{
			final Type type = (Type)i.next();
			final int count = countTable(type);
			if(count>0)
				throw new RuntimeException("there are "+count+" items left for type "+type); 
		}
		//final long amount = (System.currentTimeMillis()-time);
		//checkEmptyTableTime += amount;
		//System.out.println("CHECK EMPTY TABLES "+amount+"ms  accumulated "+checkEmptyTableTime);
	}
	
	Collection search(final Query query)
	{
		final Type selectType = query.selectType;
		final Statement bf = createStatement();

		bf.append("select ").
			append(selectType.protectedName).
			append('.').
			append(selectType.primaryKey.protectedName).defineColumnInteger().
			append(" from ");

		boolean first = true;
		for(Iterator i = query.fromTypes.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(((Type)i.next()).protectedName);
		}

		bf.append(" where ");
		query.condition.appendStatement(bf);
		
		//System.out.println("searching "+bf.toString());
		try
		{
			final QueryResultSetHandler handler = new QueryResultSetHandler();
			executeSQL(bf, handler);
			return handler.result;
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}

	void load(final Row row)
	{
		final Statement bf = createStatement();
		bf.append("select ");

		boolean first = true;
		for(Type type = row.type; type!=null; type = type.getSupertype())
		{
			final List columns = type.getColumns();
			for(Iterator i = columns.iterator(); i.hasNext(); )
			{
				if(first)
					first = false;
				else
					bf.append(',');

				final Column column = (Column)i.next();
				bf.append(type.protectedName).
					append('.').
					append(column.protectedName).defineColumn(column);
			}
		}

		bf.append(" from ");
		first = true;
		for(Type type = row.type; type!=null; type = type.getSupertype())
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(type.protectedName);
		}
			
		bf.append(" where ");
		first = true;
		for(Type type = row.type; type!=null; type = type.getSupertype())
		{
			if(first)
				first = false;
			else
				bf.append(" and ");

			bf.append(type.protectedName).
				append('.').
				append(type.primaryKey.protectedName).
				append('=').
				append(row.pk);
		}

		//System.out.println("loading "+bf.toString());
		try
		{
			executeSQL(bf, new LoadResultSetHandler(row));
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}

	void store(final Row row)
			throws UniqueViolationException
	{
		store(row, row.type);
	}

	private void store(final Row row, final Type type)
			throws UniqueViolationException
	{
		final Type supertype = type.getSupertype();
		if(supertype!=null)
			store(row, supertype);

		final List columns = type.getColumns();

		final Statement bf = createStatement();
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

		try
		{
			executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
		}
		catch(UniqueViolationException e)
		{
			throw e;
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}

	void delete(final Type type, final int pk)
			throws IntegrityViolationException
	{
		for(Type currentType = type; currentType!=null; currentType = currentType.getSupertype())
		{
			final Statement bf = createStatement();
			bf.append("delete from ").
				append(currentType.protectedName).
				append(" where ").
				append(currentType.primaryKey.protectedName).
				append('=').
				append(pk);

			//System.out.println("deleting "+bf.toString());

			try
			{
				executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
			}
			catch(IntegrityViolationException e)
			{
				throw e;
			}
			catch(ConstraintViolationException e)
			{
				throw new SystemException(e);
			}
		}
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
			if(!resultSet.next())
				throw new RuntimeException("no such pk"); // TODO use some better exception
			int columnIndex = 1;
			for(Type type = row.type; type!=null; type = type.getSupertype())
				for(Iterator i = type.getColumns().iterator(); i.hasNext(); )
					((Column)i.next()).load(resultSet, columnIndex++, row);
			return;
		}
	}

	private static class IntegerResultSetHandler implements ResultSetHandler
	{
		int result;

		public void run(ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();
			result = IntegerColumn.convertSQLResult(resultSet.getObject(1));
		}
	}

	private static class NextPKResultSetHandler implements ResultSetHandler
	{
		int resultLo;
		int resultHi;

		public void run(ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();
			final Object oLo = resultSet.getObject(1);
			if(oLo==null)
			{
				resultLo = -1;
				resultHi = 0;
			}
			else
			{
				resultLo = IntegerColumn.convertSQLResult(oLo)-1;
				final Object oHi = resultSet.getObject(2);
				resultHi = IntegerColumn.convertSQLResult(oHi)+1;
			}
		}
	}
	
	protected abstract void defineColumnTypes(List columnTypes, java.sql.Statement sqlStatement)
			throws SQLException;

	//private static int timeExecuteQuery = 0;

	private void executeSQL(final Statement statement, final ResultSetHandler resultSetHandler)
			throws ConstraintViolationException
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
			if(resultSetHandler==EMPTY_RESULT_SET_HANDLER)
			{
				final int rows = sqlStatement.executeUpdate(statement.getText());
				//System.out.println("("+rows+"): "+statement.getText());
			}
			else
			{
				//long time = System.currentTimeMillis();
				if(useDefineColumnTypes)
					defineColumnTypes(statement.columnTypes, sqlStatement);
				resultSet = sqlStatement.executeQuery(statement.getText());
				//long interval = System.currentTimeMillis() - time;
				//timeExecuteQuery += interval;
				//System.out.println("executeQuery: "+interval+"ms sum "+timeExecuteQuery+"ms");
				resultSetHandler.run(resultSet);
			}
		}
		catch(SQLException e)
		{
			final ConstraintViolationException wrappedException = wrapException(e);
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
	
	protected abstract String extractUniqueConstraintName(SQLException e);
	protected abstract String extractIntegrityConstraintName(SQLException e);

	private final ConstraintViolationException wrapException(final SQLException e)
	{
		{		
			final String uniqueConstraintName = extractUniqueConstraintName(e);
			if(uniqueConstraintName!=null)
			{
				final UniqueConstraint constraint = UniqueConstraint.getUniqueConstraint(uniqueConstraintName);
				if(constraint==null)
					throw new SystemException(e, "no unique constraint found for >"+uniqueConstraintName+"<");
				return new UniqueViolationException(e, null, constraint);
			}
		}
		{		
			final String integrityConstraintName = extractIntegrityConstraintName(e);
			if(integrityConstraintName!=null)
			{
				final ItemAttribute attribute = ItemAttribute.getItemAttributeByIntegrityConstraintName(integrityConstraintName);
				if(attribute==null)
					throw new SystemException(e, "no item attribute found for >"+integrityConstraintName+"<");
				return new IntegrityViolationException(e, null, attribute);
			}
		}
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
	protected abstract String protectName(final String name);

	abstract String getIntegerType(int precision);
	abstract String getStringType(int maxLength);
	
	private void createTable(final Type type)
	{
		final Statement bf = createStatement();
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
			bf.append(',').
				append(column.protectedName).
				append(' ').
				append(column.databaseType);
			
			if(column.notNull)
				bf.append(" not null");
		}
		
		final Type supertype = type.getSupertype();
		if(supertype!=null)
		{
			bf.append(",constraint ").
				append(protectName(type.trimmedName+"SUP")).
				append(" foreign key(").
				append(type.primaryKey.protectedName).
				append(")references ").
				append(supertype.protectedName).
				append('(').
				append(supertype.primaryKey.protectedName).
				append(')');
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
			//System.out.println("createTable:"+bf.toString());
			executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
		}
		catch(ConstraintViolationException e)
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
			if(column instanceof ItemColumn)
			{
				final ItemColumn itemColumn = (ItemColumn)column;
				final Statement bf = createStatement();
				bf.append("alter table ").
					append(type.protectedName).
					append(" add constraint ").
					append(Database.theInstance.protectName(itemColumn.integrityConstraintName)).
					append(" foreign key (").
					append(column.protectedName).
					append(") references ").
					append(itemColumn.getForeignTableNameProtected());

				//System.out.println("createForeignKeyConstraints:"+bf);
				try
				{
					executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
				}
				catch(ConstraintViolationException e)
				{
					throw new SystemException(e);
				}
			}
		}
	}
	
	private void dropTable(final Type type)
	{
		type.onDropTable();

		final Statement bf = createStatement();
		bf.append("drop table ").
			append(type.protectedName);

		try
		{
			executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}
	
	private int countTable(final Type type)
	{
		final Statement bf = createStatement();
		bf.append("select count(*) from ").defineColumnInteger().
			append(type.protectedName);

		try
		{
			final IntegerResultSetHandler handler = new IntegerResultSetHandler();
			executeSQL(bf, handler);
			return handler.result;
		}
		catch(ConstraintViolationException e)
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
			if(column instanceof ItemColumn)
			{
				final Statement bf = createStatement();
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
				catch(ConstraintViolationException e)
				{
					throw new SystemException(e);
				}
			}
		}
	}
	
	int[] getNextPK(final Type type)
	{
		final Statement bf = createStatement();
		final String primaryKeyProtectedName = type.primaryKey.protectedName;
		bf.append("select min(").
			append(primaryKeyProtectedName).defineColumnInteger().
			append("),max(").
			append(primaryKeyProtectedName).defineColumnInteger().
			append(") from ").
			append(type.protectedName);
			
		try
		{
			final NextPKResultSetHandler handler = new NextPKResultSetHandler();
			executeSQL(bf, handler);
			//System.err.println("select max("+type.primaryKey.trimmedName+") from "+type.trimmedName+" : "+handler.result);
			return new int[] {handler.resultLo, handler.resultHi};
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}
	
}
