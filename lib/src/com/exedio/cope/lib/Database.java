
package com.exedio.cope.lib;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import bak.pcj.list.IntArrayList;

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
	
	private final List tables = new ArrayList();
	private final HashMap uniqueConstraintsByID = new HashMap();
	private final HashMap itemColumnsByIntegrityConstraintName = new HashMap();
	private boolean buildStage = true;
	private final boolean useDefineColumnTypes;
	
	
	protected Database()
	{
		this.useDefineColumnTypes = this instanceof DatabaseColumnTypesDefinable;
		//System.out.println("using database "+getClass());
	}
	
	final void addTable(final Table table)
	{
		if(!buildStage)
			throw new RuntimeException();
		tables.add(table);
	}
	
	final void addUniqueConstraint(final String constraintID, final UniqueConstraint constraint)
	{
		if(!buildStage)
			throw new RuntimeException();

		final Object collision = uniqueConstraintsByID.put(constraintID, constraint);
		if(collision!=null)
			throw new InitializerRuntimeException(null, "ambiguous unique constraint "+constraint+" trimmed to >"+constraintID+"< colliding with "+collision);
	}
	
	final void addIntegrityConstraint(final ItemColumn column)
	{
		if(!buildStage)
			throw new RuntimeException();
		if(itemColumnsByIntegrityConstraintName.put(column.integrityConstraintName, column)!=null)
			throw new InitializerRuntimeException("there is more than one integrity constraint with name "+column.integrityConstraintName);
	}
	
	private final Statement createStatement()
	{
		return new Statement(useDefineColumnTypes);
	}
	
	//private static int createTableTime = 0, dropTableTime = 0, checkEmptyTableTime = 0;
	
	public void createDatabase()
	{
		buildStage = false;

		//final long time = System.currentTimeMillis();
		for(Iterator i = tables.iterator(); i.hasNext(); )
			createTable((Table)i.next());

		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			createMediaDirectories((Type)i.next());

		for(Iterator i = tables.iterator(); i.hasNext(); )
			createForeignKeyConstraints((Table)i.next());

		//final long amount = (System.currentTimeMillis()-time);
		//createTableTime += amount;
		//System.out.println("CREATE TABLES "+amount+"ms  accumulated "+createTableTime);
	}

	//private static int checkTableTime = 0;

	/**
	 * Checks the database,
	 * whether the database tables representing the types do exist.
	 * Issues a single database statement,
	 * that touches all tables and columns,
	 * that would have been created by
	 * {@link #createDatabase()}.
	 * @throws SystemException
	 * 	if something is wrong with the database.
	 * 	TODO: use a more specific exception.
	 */
	public void checkDatabase()
	{
		buildStage = false;

		//final long time = System.currentTimeMillis();
		final Statement bf = createStatement();
		bf.append("select count(*) from ").defineColumnInteger();
		boolean first = true;

		for(Iterator i = tables.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				bf.append(',');

			final Table table = (Table)i.next();
			bf.append(table.protectedID);
		}
		
		final Long testDate = new Long(System.currentTimeMillis());
		
		bf.append(" where ");
		first = true;
		for(Iterator i = tables.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				bf.append(" and ");

			final Table table = (Table)i.next();

			final Column primaryKey = table.getPrimaryKey();
			bf.append(table.protectedID).
				append('.').
				append(primaryKey.protectedID).
				append('=').
				append(Type.NOT_A_PK);
			
			for(Iterator j = table.getColumns().iterator(); j.hasNext(); )
			{
				final Column column = (Column)j.next();
				bf.append(" and ").
					append(table.protectedID).
					append('.').
					append(column.protectedID).
					append('=');

				if(column instanceof IntegerColumn)
					bf.appendValue(column, ((IntegerColumn)column).longInsteadOfInt ? (Number)new Long(1) : new Integer(1));
				else if(column instanceof DoubleColumn)
					bf.appendValue(column, new Double(2.2));
				else if(column instanceof StringColumn)
					bf.appendValue(column, "z");
				else if(column instanceof TimestampColumn)
					bf.appendValue(column, testDate);
				else
					throw new RuntimeException(column.toString());
			}
		}
		
		try
		{
			//System.out.println("checkDatabase:"+bf.toString());
			executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}

		//final long amount = (System.currentTimeMillis()-time);
		//checkTableTime += amount;
		//System.out.println("CHECK TABLES "+amount+"ms  accumulated "+checkTableTime);
	}
	
	public Report reportDatabase()
	{
		buildStage = false;
		
		final Report report = new Report();

		final Statement bf = createStatement();
		bf.append("select TABLE_NAME, CONSTRAINT_NAME, CONSTRAINT_TYPE  from user_constraints order by table_name").
			defineColumnString().
			defineColumnString().
			defineColumnString();
		
		try
		{
			executeSQL(bf, new ReportConstraintHandler(report));
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}

		for(Iterator i = tables.iterator(); i.hasNext(); )
		{
			final Table table = (Table)i.next();
			final Report.Table reportTable = report.notifyRequiredTable(table.id);
			//System.out.println("REQUIRED:"+table.id);
		}

		return report;
	}

	private static class ReportConstraintHandler implements ResultSetHandler
	{
		private final Report report;

		ReportConstraintHandler(final Report report)
		{
			this.report = report;
		}

		public void run(ResultSet resultSet) throws SQLException
		{
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(1);
				final String constraintName = resultSet.getString(2);
				final String constraintType = resultSet.getString(3);
				final Report.Table table = report.notifyExistentTable(tableName);
				final Report.Constraint constraint = table.notifyExistentConstraint(constraintName);
				//System.out.println("EXISTS:"+tableName);
			}
		}
	}


	public void dropDatabase()
	{
		buildStage = false;

		//final long time = System.currentTimeMillis();
		{
			final List types = Type.getTypes();
			for(ListIterator i = types.listIterator(types.size()); i.hasPrevious(); )
				((Type)i.previous()).onDropTable();
		}
		{
			// must delete in reverse order, to obey integrity constraints
			for(ListIterator i = tables.listIterator(tables.size()); i.hasPrevious(); )
				dropForeignKeyConstraints((Table)i.previous());
			for(ListIterator i = tables.listIterator(tables.size()); i.hasPrevious(); )
				dropTable((Table)i.previous());
		}
		//final long amount = (System.currentTimeMillis()-time);
		//dropTableTime += amount;
		//System.out.println("DROP TABLES "+amount+"ms  accumulated "+dropTableTime);
	}
	
	public void tearDownDatabase()
	{
		buildStage = false;

		System.err.println("TEAR DOWN ALL DATABASE");
		for(Iterator i = tables.iterator(); i.hasNext(); )
		{
			try
			{
				final Table table = (Table)i.next();
				System.err.print("DROPPING FOREIGN KEY CONSTRAINTS "+table+"... ");
				dropForeignKeyConstraints(table);
				System.err.println("done.");
			}
			catch(SystemException e2)
			{
				System.err.println("failed:"+e2.getMessage());
			}
		}
		for(Iterator i = tables.iterator(); i.hasNext(); )
		{
			try
			{
				final Table table = (Table)i.next();
				System.err.print("DROPPING TABLE "+table+" ... ");
				dropTable(table);
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
		buildStage = false;

		//final long time = System.currentTimeMillis();
		for(Iterator i = tables.iterator(); i.hasNext(); )
		{
			final Table table = (Table)i.next();
			final int count = countTable(table);
			if(count>0)
				throw new RuntimeException("there are "+count+" items left for table "+table.id);
		}
		//final long amount = (System.currentTimeMillis()-time);
		//checkEmptyTableTime += amount;
		//System.out.println("CHECK EMPTY TABLES "+amount+"ms  accumulated "+checkEmptyTableTime);
	}
	
	final IntArrayList search(final Query query)
	{
		buildStage = false;

		final Table selectTable = query.selectType.table;
		final Statement bf = createStatement();

		bf.append("select ").
			append(selectTable.protectedID).
			append('.').
			append(selectTable.getPrimaryKey().protectedID).defineColumnInteger().
			append(" from ");

		boolean first = true;
		for(Iterator i = query.fromTypes.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(((Type)i.next()).table.protectedID);
		}

		if(query.condition!=null)
		{
			bf.append(" where ");
			query.condition.appendStatement(bf);
		}
		
		if(query.orderBy!=null)
		{
			bf.append(" order by ").
				append(query.orderBy);
			if(!query.orderAscending)
				bf.append(" desc");
		}
		
		//System.out.println("searching "+bf.toString());
		try
		{
			final SearchResultSetHandler handler = new SearchResultSetHandler(query.start, query.count);
			executeSQL(bf, handler);
			return handler.result;
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}

	private static class SearchResultSetHandler implements ResultSetHandler
	{
		private final int start;
		private final int count;
		private final IntArrayList result = new IntArrayList();
		
		SearchResultSetHandler(final int start, final int count)
		{
			this.start = start;
			this.count = count;
			if(start<0)
				throw new RuntimeException();
		}

		public void run(ResultSet resultSet) throws SQLException
		{
			if(start>0)
			{
				// TODO: ResultSet.relative
				// Would like to use
				//    resultSet.relative(start+1);
				// but this throws a java.sql.SQLException:
				// Invalid operation for forward only resultset : relative
				for(int i = start; i>0; i--)
					resultSet.next();
			}
				
			int i = (count>=0 ? count : Integer.MAX_VALUE);

			while(resultSet.next() && (--i)>=0)
			{
				final int pk = resultSet.getInt(1);
				//System.out.println("pk:"+pk);
				result.add(pk);
			}
		}
	}
	

	void load(final Row row)
	{
		buildStage = false;

		final Statement bf = createStatement();
		bf.append("select ");

		boolean first = true;
		for(Type type = row.type; type!=null; type = type.getSupertype())
		{
			final Table table = type.table;
			final List columns = table.getColumns();
			for(Iterator i = columns.iterator(); i.hasNext(); )
			{
				if(first)
					first = false;
				else
					bf.append(',');

				final Column column = (Column)i.next();
				bf.append(table.protectedID).
					append('.').
					append(column.protectedID).defineColumn(column);
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

			bf.append(type.table.protectedID);
		}
			
		bf.append(" where ");
		first = true;
		for(Type type = row.type; type!=null; type = type.getSupertype())
		{
			if(first)
				first = false;
			else
				bf.append(" and ");

			final Table table = type.table;
			bf.append(table.protectedID).
				append('.').
				append(table.getPrimaryKey().protectedID).
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
				throw new RuntimeException("no such pk"); // TODO use a dedicated runtime exception
			int columnIndex = 1;
			for(Type type = row.type; type!=null; type = type.getSupertype())
			{
				for(Iterator i = type.table.getColumns().iterator(); i.hasNext(); )
					((Column)i.next()).load(resultSet, columnIndex++, row);
			}
			return;
		}
	}
	

	boolean check(final Type type, final int pk)
	{
		buildStage = false;

		final Statement bf = createStatement();
		final Table table = type.table;
		final Column primaryKey = table.getPrimaryKey();
		final String tableProtectedID = table.protectedID;
		final String primaryKeyProtectedID = primaryKey.protectedID;

		bf.append("select ").
			append(tableProtectedID).
			append('.').
			append(primaryKeyProtectedID).defineColumnInteger().
			append(" from ").
			append(tableProtectedID).
			append(" where ").
			append(tableProtectedID).
			append('.').
			append(primaryKeyProtectedID).
			append('=').
			append(pk);

		//System.out.println("loading "+bf.toString());
		try
		{
			final CheckResultSetHandler handler = new CheckResultSetHandler();
			executeSQL(bf, handler);
			return handler.result;
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}

	private static class CheckResultSetHandler implements ResultSetHandler
	{
		private boolean result = false;

		public void run(ResultSet resultSet) throws SQLException
		{
			result = resultSet.next();
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
		buildStage = false;

		final Type supertype = type.getSupertype();
		if(supertype!=null)
			store(row, supertype);
			
		final Table table = type.table;

		final List columns = table.getColumns();

		final Statement bf = createStatement();
		if(row.present)
		{
			bf.append("update ").
				append(table.protectedID).
				append(" set ");

			boolean first = true;
			for(Iterator i = columns.iterator(); i.hasNext(); )
			{
				if(first)
					first = false;
				else
					bf.append(',');

				final Column column = (Column)i.next();
				bf.append(column.protectedID).
					append('=');

				final Object value = row.store(column);
				bf.append(column.cacheToDatabase(value));
			}
			bf.append(" where ").
				append(table.getPrimaryKey().protectedID).
				append('=').
				append(row.pk);
		}
		else
		{
			bf.append("insert into ").
				append(table.protectedID).
				append("(").
				append(table.getPrimaryKey().protectedID);

			boolean first = true;
			for(Iterator i = columns.iterator(); i.hasNext(); )
			{
				bf.append(',');
				final Column column = (Column)i.next();
				bf.append(column.protectedID);
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

		try
		{
			//System.out.println("storing "+bf.toString());
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
		buildStage = false;

		for(Type currentType = type; currentType!=null; currentType = currentType.getSupertype())
		{
			final Table currentTable = currentType.table;
			final Statement bf = createStatement();
			bf.append("delete from ").
				append(currentTable.protectedID).
				append(" where ").
				append(currentTable.getPrimaryKey().protectedID).
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
		
	private final static int convertSQLResult(final Object sqlInteger)
	{
		// IMPLEMENTATION NOTE for Oracle
		// Whether the returned object is an Integer or a BigDecimal,
		// depends on whether OracleStatement.defineColumnType is used or not,
		// so we support both here.
		if(sqlInteger instanceof BigDecimal)
			return ((BigDecimal)sqlInteger).intValue();
		else
			return ((Integer)sqlInteger).intValue();
	}

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
			final String sqlText = statement.getText();
			if(!sqlText.startsWith("select "))
			{
				final int rows = sqlStatement.executeUpdate(sqlText);
				//System.out.println("("+rows+"): "+statement.getText());
			}
			else
			{
				//long time = System.currentTimeMillis();
				if(useDefineColumnTypes)
					((DatabaseColumnTypesDefinable)this).defineColumnTypes(statement.columnTypes, sqlStatement);
				resultSet = sqlStatement.executeQuery(sqlText);
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
			final String uniqueConstraintID = extractUniqueConstraintName(e);
			if(uniqueConstraintID!=null)
			{
				final UniqueConstraint constraint =
					(UniqueConstraint)uniqueConstraintsByID.get(uniqueConstraintID);
				if(constraint==null)
					throw new SystemException(e, "no unique constraint found for >"+uniqueConstraintID
																			+"<, has only "+uniqueConstraintsByID.keySet());

				return new UniqueViolationException(e, null, constraint);
			}
		}
		{		
			final String integrityConstraintName = extractIntegrityConstraintName(e);
			if(integrityConstraintName!=null)
			{
				final ItemColumn column =
					(ItemColumn)itemColumnsByIntegrityConstraintName.get(integrityConstraintName);
				if(column==null)
					throw new SystemException(e, "no column attribute found for >"+integrityConstraintName
																			+"<, has only "+itemColumnsByIntegrityConstraintName.keySet());

				final ItemAttribute attribute = column.attribute;
				if(attribute==null)
					throw new SystemException(e, "no item attribute for column "+column);

				return new IntegrityViolationException(e, null, attribute);
			}
		}
		return null;
	}
	
	protected static final String trimString(final String longString, final int maxLength)
	{
		if(maxLength<=0)
			throw new RuntimeException("maxLength must be greater zero");
		if(longString.length()==0)
			throw new RuntimeException("longString must not be empty");

		if(longString.length()<=maxLength)
			return longString;

		int longStringLength = longString.length();
		final int[] trimPotential = new int[maxLength];
		final ArrayList words = new ArrayList();
		{
			final StringBuffer buf = new StringBuffer();
			for(int i=0; i<longString.length(); i++)
			{
				final char c = longString.charAt(i);
				if((c=='_' || Character.isUpperCase(c) || Character.isDigit(c)) && buf.length()>0)
				{
					words.add(buf.toString());
					int potential = 1;
					for(int j = buf.length()-1; j>=0; j--, potential++)
						trimPotential[j] += potential; 
					buf.setLength(0);
				}
				if(buf.length()<maxLength)
					buf.append(c);
				else
					longStringLength--;
			}
			if(buf.length()>0)
			{
				words.add(buf.toString());
				int potential = 1;
				for(int j = buf.length()-1; j>=0; j--, potential++)
					trimPotential[j] += potential; 
				buf.setLength(0);
			}
		}
		
		final int expectedTrimPotential = longStringLength - maxLength;
		//System.out.println("expected trim potential = "+expectedTrimPotential);

		int wordLength;
		int remainder = 0;
		for(wordLength = trimPotential.length-1; wordLength>=0; wordLength--)
		{
			//System.out.println("trim potential ["+wordLength+"] = "+trimPotential[wordLength]);
			remainder = trimPotential[wordLength] - expectedTrimPotential;
			if(remainder>=0)
				break;
		}
		
		final StringBuffer result = new StringBuffer(longStringLength);
		for(Iterator i = words.iterator(); i.hasNext(); )
		{
			final String word = (String)i.next();
			//System.out.println("word "+word+" remainder:"+remainder);
			if((word.length()>wordLength) && remainder>0)
			{
				result.append(word.substring(0, wordLength+1));
				remainder--;
			}
			else if(word.length()>wordLength)
				result.append(word.substring(0, wordLength));
			else
				result.append(word);
		}
		//System.out.println("---- trimName("+longString+","+maxLength+") == "+result+"     --- "+words);

		if(result.length()!=maxLength)
			throw new RuntimeException(result.toString()+maxLength);

		return result.toString();
	}
	
	String trimName(final Type type)
	{
		final String className = type.getJavaClass().getName();
		final int pos = className.lastIndexOf('.');
		return trimString(className.substring(pos+1), 25);
	}
	
	/**
	 * Trims a name to length for being be a suitable qualifier for database entities,
	 * such as tables, columns, indexes, constraints, partitions etc.
	 */
	String trimName(final String longName)
	{
		return trimString(longName, 25);
	}

	/**
	 * Protects a database name from being interpreted as a SQL keyword.
	 * This is usually done by enclosing the name with some (database specific) delimiters.
	 * The default implementation uses double quotes as delimiter.
	 */
	protected String protectName(String name)
	{
		return '"' + name + '"';
	}

	abstract String getIntegerType(int precision);
	abstract String getDoubleType(int precision);
	abstract String getStringType(int maxLength);
	
	private void createTable(final Table table)
	{
		final Statement bf = createStatement();
		bf.append("create table ").
			append(table.protectedID).
			append('(');

		boolean firstColumn = true;
		for(Iterator i = table.getAllColumns().iterator(); i.hasNext(); )
		{
			if(firstColumn)
				firstColumn = false;
			else
				bf.append(',');
			
			final Column column = (Column)i.next();
			bf.append(column.protectedID).
				append(' ').
				append(column.databaseType);
		}
		
		// attribute constraints		
		for(Iterator i = table.getAllColumns().iterator(); i.hasNext(); )
		{
			final Column column = (Column)i.next();

			if(column.primaryKey)
			{
				bf.append(",constraint ").
					append(protectName(column.getPrimaryKeyConstraintID())).
					append(" primary key(").
					append(column.protectedID).
					append(')');
			}
			else if(column.notNull)
			{
				bf.append(",constraint ").
					append(protectName(column.getNotNullConstraintID())).
					append(" check(").
					append(column.protectedID).
					append(" is not null)");
			}

			if(column instanceof StringColumn)
			{
				final StringColumn stringColumn = (StringColumn)column;
				if(stringColumn.minimumLength>0)
				{
					bf.append(",constraint ").
						append(protectName(stringColumn.getMinimumLengthConstraintID())).
						append(" check(length(").
						append(column.protectedID).
						append(")>=").
						append(stringColumn.minimumLength);

					if(!column.notNull)
					{
						bf.append(" or ").
							append(column.protectedID).
							append(" is null");
					}
					bf.append(')');
				}
				if(stringColumn.maximumLength!=Integer.MAX_VALUE)
				{
					bf.append(",constraint ").
						append(protectName(stringColumn.getMaximumLengthConstraintID())).
						append(" check(length(").
						append(column.protectedID).
						append(")<=").
						append(stringColumn.maximumLength);

					if(!column.notNull)
					{
						bf.append(" or ").
							append(column.protectedID).
							append(" is null");
					}
					bf.append(')');
				}
			}
			else if(column instanceof IntegerColumn)
			{
				final IntegerColumn intColumn = (IntegerColumn)column;
				final int[] allowedValues = intColumn.allowedValues;
				if(allowedValues!=null)
				{
					bf.append(",constraint ").
						append(protectName(intColumn.getAllowedValuesConstraintID())).
						append(" check(").
						append(column.protectedID).
						append(" in (");

					for(int j = 0; j<allowedValues.length; j++)
					{
						if(j>0)
							bf.append(',');
						bf.append(allowedValues[j]);
					}
					bf.append(')');

					if(!column.notNull)
					{
						bf.append(" or ").
							append(column.protectedID).
							append(" is null");
					}
					bf.append(')');
				}
			}
		}

		for(Iterator i = table.getUniqueConstraints().iterator(); i.hasNext(); )
		{
			final UniqueConstraint uniqueConstraint = (UniqueConstraint)i.next();
			bf.append(",constraint ").
				append(uniqueConstraint.getProtectedID()).
				append(" unique(");
			boolean first = true;
			for(Iterator j = uniqueConstraint.getUniqueAttributes().iterator(); j.hasNext(); )
			{
				if(first)
					first = false;
				else
					bf.append(',');
				final Attribute uniqueAttribute = (Attribute)j.next();
				bf.append(uniqueAttribute.getMainColumn().protectedID);
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
	
	private void createForeignKeyConstraints(final Table table)
	{
		//System.out.println("createForeignKeyConstraints:"+bf);

		for(Iterator i = table.getAllColumns().iterator(); i.hasNext(); )
		{
			final Column column = (Column)i.next();
			//System.out.println("createForeignKeyConstraints("+column+"):"+bf);
			if(column instanceof ItemColumn)
			{
				final ItemColumn itemColumn = (ItemColumn)column;
				final Statement bf = createStatement();
				bf.append("alter table ").
					append(table.protectedID).
					append(" add constraint ").
					append(Database.theInstance.protectName(itemColumn.integrityConstraintName)).
					append(" foreign key (").
					append(column.protectedID).
					append(") references ").
					append(itemColumn.getForeignTableNameProtected());

				try
				{
					//System.out.println("createForeignKeyConstraints:"+bf);
					executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
				}
				catch(ConstraintViolationException e)
				{
					throw new SystemException(e);
				}
			}
		}
	}
	
	private void createMediaDirectories(final Type type)
	{
		File typeDirectory = null;

		for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
		{
			final Attribute attribute = (Attribute)i.next();
			if(attribute instanceof MediaAttribute)
			{
				if(typeDirectory==null)
				{
					final File directory = Properties.getInstance().getMediaDirectory();
					typeDirectory = new File(directory, type.id);
					typeDirectory.mkdir();
				}
				final File attributeDirectory = new File(typeDirectory, attribute.getName());
				attributeDirectory.mkdir();
			}
		}
	}

	private void dropTable(final Table table) 
	{
		final Statement bf = createStatement();
		bf.append("drop table ").
			append(table.protectedID);

		try
		{
			executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}
	
	private int countTable(final Table table)
	{
		final Statement bf = createStatement();
		bf.append("select count(*) from ").defineColumnInteger().
			append(table.protectedID);

		try
		{
			final CountResultSetHandler handler = new CountResultSetHandler();
			executeSQL(bf, handler);
			return handler.result;
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}
	
	private static class CountResultSetHandler implements ResultSetHandler
	{
		int result;

		public void run(ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();

			result = convertSQLResult(resultSet.getObject(1));
		}
	}


	private void dropForeignKeyConstraints(final Table table) 
	{
		for(Iterator i = table.getColumns().iterator(); i.hasNext(); )
		{
			final Column column = (Column)i.next();
			//System.out.println("dropForeignKeyConstraints("+column+")");
			if(column instanceof ItemColumn)
			{
				final ItemColumn itemColumn = (ItemColumn)column;
				final Statement bf = createStatement();
				boolean hasOne = false;

				bf.append("alter table ").
					append(table.protectedID).
					append(" drop constraint ").
					append(Database.theInstance.protectName(itemColumn.integrityConstraintName));

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
	

	int[] getNextPK(final Table table)
	{
		buildStage = false;

		final Statement bf = createStatement();
		final String primaryKeyProtectedID = table.getPrimaryKey().protectedID;
		bf.append("select min(").
			append(primaryKeyProtectedID).defineColumnInteger().
			append("),max(").
			append(primaryKeyProtectedID).defineColumnInteger().
			append(") from ").
			append(table.protectedID);
			
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
				resultLo = convertSQLResult(oLo)-1;
				final Object oHi = resultSet.getObject(2);
				resultHi = convertSQLResult(oHi)+1;
			}
		}
	}
	
}
