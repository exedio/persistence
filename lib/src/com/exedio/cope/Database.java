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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

abstract class Database
{
	private final List tables = new ArrayList();
	private final HashMap uniqueConstraintsByID = new HashMap();
	private final HashMap itemColumnsByIntegrityConstraintName = new HashMap();
	private boolean buildStage = true;
	private final String schema;
	private final boolean useDefineColumnTypes;
	final ConnectionPool connectionPool;
	final boolean hsqldb; // TODO remove hsqldb-specific stuff
	final boolean mysql; // TODO remove mysql-specific stuff
	
	protected Database(final Properties properties, final String schema)
	{
		this.schema = schema;
		this.useDefineColumnTypes = this instanceof DatabaseColumnTypesDefinable;
		this.connectionPool = new ConnectionPool(properties);
		this.hsqldb = "com.exedio.cope.HsqldbDatabase".equals(getClass().getName()); 
		this.mysql = "com.exedio.cope.MysqlDatabase".equals(getClass().getName()); 
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
			throw new RuntimeException("ambiguous unique constraint "+constraint+" trimmed to >"+constraintID+"< colliding with "+collision);
	}
	
	final void addIntegrityConstraint(final ItemColumn column)
	{
		if(!buildStage)
			throw new RuntimeException();
		if(itemColumnsByIntegrityConstraintName.put(column.integrityConstraintName, column)!=null)
			throw new RuntimeException("there is more than one integrity constraint with name "+column.integrityConstraintName);
	}
	
	protected final List getTables()
	{
		return tables;
	}
	
	protected final Statement createStatement()
	{
		return new Statement(useDefineColumnTypes);
	}
	
	void createDatabase()
	{
		buildStage = false;
		
		final ReportSchema report = requiredReport();
		report.create();
	}

	//private static int checkTableTime = 0;

	void checkDatabase()
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
		
		//System.out.println("checkDatabase:"+bf.toString());
		executeSQLQuery(bf,
			new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					if(!resultSet.next())
						throw new RuntimeException();
				}
			},
			false
		);

		//final long amount = (System.currentTimeMillis()-time);
		//checkTableTime += amount;
		//System.out.println("CHECK TABLES "+amount+"ms  accumulated "+checkTableTime);
	}	

	void dropDatabase()
	{
		buildStage = false;

		final ReportSchema report = requiredReport();
		final List tables = report.getTables();

		//final long time = System.currentTimeMillis();
		// must delete in reverse order, to obey integrity constraints
		for(ListIterator i = tables.listIterator(tables.size()); i.hasPrevious(); )
			dropForeignKeyConstraints((ReportTable)i.previous(), false);
		for(ListIterator i = tables.listIterator(tables.size()); i.hasPrevious(); )
			dropTable((ReportTable)i.previous());
		//final long amount = (System.currentTimeMillis()-time);
		//dropTableTime += amount;
		//System.out.println("DROP TABLES "+amount+"ms  accumulated "+dropTableTime);
	}
	
	void tearDownDatabase()
	{
		buildStage = false;

		final ReportSchema report = requiredReport();
		final List tables = report.getTables();

		System.err.println("TEAR DOWN ALL DATABASE");
		for(Iterator i = tables.iterator(); i.hasNext(); )
		{
			try
			{
				final ReportTable table = (ReportTable)i.next();
				dropForeignKeyConstraints(table, true);
			}
			catch(NestingRuntimeException e2)
			{
				System.err.println("failed:"+e2.getMessage());
			}
		}
		
		final ArrayList tablesToDelete = new ArrayList(tables);

		boolean deleted;
		int run = 1;
		do
		{
			deleted = false;
			
			for(Iterator i = tablesToDelete.iterator(); i.hasNext(); )
			{
				try
				{
					final ReportTable table = (ReportTable)i.next();
					System.err.print("DROPPING TABLE "+table+" ... ");
					dropTable(table);
					System.err.println("done.");
					// remove the table, so it's not tried again
					i.remove();
					// remember there was at least one table deleted
					deleted = true;
				}
				catch(NestingRuntimeException e2)
				{
					System.err.println("failed:"+e2.getMessage());
				}
			}
			System.err.println("FINISH STAGE "+(run++));
		}
		while(deleted);
	}

	void checkEmptyDatabase()
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
	
	final ArrayList search(final Query query)
	{
		buildStage = false;

		final Statement bf = createStatement();
		bf.append("select ");

		final Selectable[] selectables = query.selectables;
		final Column[] selectColumns = new Column[selectables.length];
		final Type[] selectTypes = new Type[selectables.length];

		for(int selectableIndex = 0; selectableIndex<selectables.length; selectableIndex++)
		{
			final Selectable selectable = selectables[selectableIndex];
			final Column selectColumn;
			final Type selectType;
			final Table selectTable;
			final Column selectPrimaryKey;
			if(selectable instanceof Function)
			{
				final Function selectAttribute = (Function)selectable;
				selectType = selectAttribute.getType();

				if(selectableIndex>0)
					bf.append(',');
				
				if(selectable instanceof ObjectAttribute)
				{
					selectColumn = ((ObjectAttribute)selectAttribute).getMainColumn();
					selectTable = selectColumn.table;
					selectPrimaryKey = selectTable.getPrimaryKey();
					bf.append(selectColumn.table.protectedID).
						append('.').
						append(selectColumn.protectedID).defineColumn(selectColumn);
				}
				else
				{
					selectColumn = null;
					final ComputedFunction computedFunction = (ComputedFunction)selectable;
					bf.append(computedFunction).defineColumn(computedFunction);
				}
			}
			else
			{
				selectType = (Type)selectable;
				selectTable = selectType.getTable();
				selectPrimaryKey = selectTable.getPrimaryKey();
				selectColumn = selectPrimaryKey;

				if(selectableIndex>0)
					bf.append(',');
				
				bf.append(selectColumn.table.protectedID).
					append('.').
					append(selectColumn.protectedID).defineColumn(selectColumn);

				if(selectColumn.primaryKey)
				{
					final StringColumn selectTypeColumn = selectColumn.getTypeColumn();
					if(selectTypeColumn!=null)
					{
						bf.append(',').
							append(selectTable.protectedID).
							append('.').
							append(selectTypeColumn.protectedID).defineColumn(selectTypeColumn);
					}
					else
						selectTypes[selectableIndex] = selectType;
				}
				else
					selectTypes[selectableIndex] = selectType;
			}

			selectColumns[selectableIndex] = selectColumn;
		}

		bf.append(" from ").
			append(query.type.getTable().protectedID);

		final ArrayList queryJoins = query.joins;
		if(queryJoins!=null)
		{
			for(Iterator i = queryJoins.iterator(); i.hasNext(); )
			{
				final Join join = (Join)i.next();
				
				if(hsqldb && join.getKind()==Join.KIND_OUTER_RIGHT)
					throw new RuntimeException("hsqldb not support right outer joins");
	
				bf.append(' ').
					append(join.getKindString()).
					append(" join ").
					append(join.type.getTable().protectedID).
					append(" on ");
				join.condition.appendStatement(bf);
			}
		}

		if(query.condition!=null)
		{
			bf.append(" where ");
			query.condition.appendStatement(bf);
		}

		boolean firstOrderBy = true;		
		if(query.orderBy!=null || query.deterministicOrder)
			bf.append(" order by ");

		if(query.orderBy!=null)
		{
			firstOrderBy = false;

			bf.append(query.orderBy);
			if(!query.orderAscending)
				bf.append(" desc");
		}
		
		if(query.deterministicOrder)
		{
			if(!firstOrderBy)
				bf.append(',');
			
			final Table deterministicOrderTable = query.type.getTable();
			bf.append("abs(").
				append(deterministicOrderTable.protectedID).
				append('.').
				append(deterministicOrderTable.getPrimaryKey().protectedID).
				append("*4+1)");
		}

		//System.out.println("searching "+bf.toString());
		final int start = query.start;
		final int count = query.count;
		final Type[] types = selectTypes;
		final Model model = query.model;
		final ArrayList result = new ArrayList();

		if(start<0)
			throw new RuntimeException();
		if(selectables.length!=selectColumns.length)
			throw new RuntimeException();
		if(selectables.length!=types.length)
			throw new RuntimeException();

		query.statementInfo = executeSQLQuery(bf, new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
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
						int columnIndex = 1;
						final Object[] resultRow = (selectables.length > 1) ? new Object[selectables.length] : null;
							
						for(int selectableIndex = 0; selectableIndex<selectables.length; selectableIndex++)
						{
							final Selectable selectable = selectables[selectableIndex];
							final Object resultCell;
							if(selectable instanceof ObjectAttribute)
							{
								final Object selectValue = selectColumns[selectableIndex].load(resultSet, columnIndex++);
								final ObjectAttribute selectAttribute = (ObjectAttribute)selectable;
								resultCell = selectAttribute.cacheToSurface(selectValue);
							}
							else if(selectable instanceof ComputedFunction)
							{
								final ComputedFunction selectFunction = (ComputedFunction)selectable;
								resultCell = selectFunction.load(resultSet, columnIndex++);
							}
							else
							{
								final Number pk = (Number)resultSet.getObject(columnIndex++);
								//System.out.println("pk:"+pk);
								if(pk==null)
								{
									// can happen when using right outer joins
									resultCell = null;
								}
								else
								{
									final Type type = types[selectableIndex];
									final Type currentType;
									if(type==null)
									{
										final String typeID = resultSet.getString(columnIndex++);
										currentType = model.findTypeByID(typeID);
										if(currentType==null)
											throw new RuntimeException("no type with type id "+typeID);
									}
									else
										currentType = type;
					
									resultCell = currentType.getItem(pk.intValue());
								}
							}
							if(resultRow!=null)
								resultRow[selectableIndex] = resultCell;
							else
								result.add(resultCell);
						}
						if(resultRow!=null)
							result.add(Collections.unmodifiableList(Arrays.asList(resultRow)));
					}
				}
			}, query.makeStatementInfo);
		return result;
	}

	void load(final Row row)
	{
		buildStage = false;

		final Statement bf = createStatement();
		bf.append("select ");

		boolean first = true;
		for(Type type = row.type; type!=null; type = type.getSupertype())
		{
			final Table table = type.getTable();
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

			bf.append(type.getTable().protectedID);
		}
			
		bf.append(" where ");
		first = true;
		for(Type type = row.type; type!=null; type = type.getSupertype())
		{
			if(first)
				first = false;
			else
				bf.append(" and ");

			final Table table = type.getTable();
			bf.append(table.protectedID).
				append('.').
				append(table.getPrimaryKey().protectedID).
				append('=').
				append(row.pk);
		}

		//System.out.println("loading "+bf.toString());
		executeSQLQuery(bf, new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					if(!resultSet.next())
						throw new RuntimeException("no such pk"); // TODO use a dedicated runtime exception
					int columnIndex = 1;
					for(Type type = row.type; type!=null; type = type.getSupertype())
					{
						for(Iterator i = type.getTable().getColumns().iterator(); i.hasNext(); )
							((Column)i.next()).load(resultSet, columnIndex++, row);
					}
					return;
				}
			}, false);
	}

	boolean check(final Type type, final int pk)
	{
		buildStage = false;

		final Statement bf = createStatement();
		final Table table = type.getTable();
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
		final CheckResultSetHandler handler = new CheckResultSetHandler();
		executeSQLQuery(bf, handler, false);
		return handler.result;
	}

	private static class CheckResultSetHandler implements ResultSetHandler
	{
		private boolean result = false;

		public void run(final ResultSet resultSet) throws SQLException
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
			
		final Table table = type.getTable();

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
			
			final StringColumn typeColumn = table.getTypeColumn();
			if(typeColumn!=null)
			{
				bf.append(',').
					append(typeColumn.protectedID);
			}

			for(Iterator i = columns.iterator(); i.hasNext(); )
			{
				bf.append(',');
				final Column column = (Column)i.next();
				bf.append(column.protectedID);
			}

			bf.append(")values(").
				append(row.pk);
			
			if(typeColumn!=null)
			{
				bf.append(",'").
					append(row.type.getID()).
					append('\'');
			}

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
			final UniqueConstraint[] uqs = type.uniqueConstraints;
			executeSQLUpdate(bf, 1, uqs.length==1?uqs[0]:null, null);
		}
		catch(UniqueViolationException e)
		{
			throw e;
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	void delete(final Type type, final int pk)
			throws IntegrityViolationException
	{
		buildStage = false;

		for(Type currentType = type; currentType!=null; currentType = currentType.getSupertype())
		{
			final Table currentTable = currentType.getTable();
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
				executeSQLUpdate(bf, 1, null, type.onlyReference);
			}
			catch(IntegrityViolationException e)
			{
				throw e;
			}
			catch(ConstraintViolationException e)
			{
				throw new NestingRuntimeException(e);
			}
		}
	}

	static interface ResultSetHandler
	{
		public void run(ResultSet resultSet) throws SQLException;
	}

	private final static int convertSQLResult(final Object sqlInteger)
	{
		// IMPLEMENTATION NOTE
		// Whether the returned object is an Integer, a Long or a BigDecimal,
		// depends on the database used and for oracle on whether
		// OracleStatement.defineColumnType is used or not, so we support all
		// here.
		return ((Number)sqlInteger).intValue();
	}

	static final String GET_TABLES = "getTables";
	static final String GET_COLUMNS = "getColumns";

	//private static int timeExecuteQuery = 0;

	protected final StatementInfo executeSQLQuery(
		final Statement statement,
		final ResultSetHandler resultSetHandler,
		final boolean makeStatementInfo)
	{
		Connection connection = null;
		java.sql.Statement sqlStatement = null;
		ResultSet resultSet = null;
		try
		{
			connection = connectionPool.getConnection();

			final String sqlText = statement.getText();
			if(GET_TABLES.equals(sqlText))
			{
				resultSet = connection.getMetaData().getTables(null, schema, null, new String[]{"TABLE"});
			}
			else if(GET_COLUMNS.equals(sqlText))
			{
				resultSet = connection.getMetaData().getColumns(null, schema, null, null);
			}
			else
			{
				// TODO: use prepared statements and reuse the statement.
				sqlStatement = connection.createStatement();
				
				if(useDefineColumnTypes)
					((DatabaseColumnTypesDefinable)this).defineColumnTypes(statement.columnTypes, sqlStatement);

				//long time = System.currentTimeMillis();
				resultSet = sqlStatement.executeQuery(sqlText);
				//long interval = System.currentTimeMillis() - time;
				//timeExecuteQuery += interval;
				//System.out.println("executeQuery: "+interval+"ms sum "+timeExecuteQuery+"ms");
			}

			resultSetHandler.run(resultSet);
			
			if(resultSet!=null)
			{
				resultSet.close();
				resultSet = null;
			}
			if(sqlStatement!=null)
			{
				sqlStatement.close();
				sqlStatement = null;
			}
			
			if(makeStatementInfo)
				return makeStatementInfo(statement, connection);
			else
				return null;
		}
		catch(SQLException e)
		{
			throw new NestingRuntimeException(e, statement.toString());
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
	
	protected final void executeSQLUpdate(final Statement statement, final int expectedRows)
			throws ConstraintViolationException
	{
		executeSQLUpdate(statement, expectedRows, null, null);
	}

	protected final void executeSQLUpdate(
			final Statement statement, final int expectedRows,
			final UniqueConstraint onlyThreatenedUniqueConstraint,
			final ItemAttribute onlyThreatenedIntegrityConstraint)
		throws ConstraintViolationException
	{
		Connection connection = null;
		java.sql.Statement sqlStatement = null;
		try
		{
			connection = connectionPool.getConnection();
			// TODO: use prepared statements and reuse the statement.
			final String sqlText = statement.getText();
			//System.err.println(statement.getText());
			sqlStatement = connection.createStatement();
			final int rows = sqlStatement.executeUpdate(sqlText);

			//System.out.println("("+rows+"): "+statement.getText());
			if(rows!=expectedRows)
				throw new RuntimeException("expected "+expectedRows+" rows, but got "+rows+" on statement "+sqlText);
		}
		catch(SQLException e)
		{
			final ConstraintViolationException wrappedException = wrapException(e, onlyThreatenedUniqueConstraint, onlyThreatenedIntegrityConstraint);
			if(wrappedException!=null)
				throw wrappedException;
			else
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
					connectionPool.putConnection(connection);
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}
	
	protected StatementInfo makeStatementInfo(final Statement statement, final Connection connection)
	{
		return new StatementInfo(statement.getText());
	}
	
	protected abstract String extractUniqueConstraintName(SQLException e);
	protected abstract String extractIntegrityConstraintName(SQLException e);
	
	protected final static String ANY_CONSTRAINT = "--ANY--";

	private final ConstraintViolationException wrapException(
			final SQLException e,
			final UniqueConstraint onlyThreatenedUniqueConstraint,
			final ItemAttribute onlyThreatenedIntegrityConstraint)
	{
		{		
			final String uniqueConstraintID = extractUniqueConstraintName(e);
			if(uniqueConstraintID!=null)
			{
				final UniqueConstraint constraint;
				if(ANY_CONSTRAINT.equals(uniqueConstraintID))
					constraint = onlyThreatenedUniqueConstraint;
				else
				{
					constraint = (UniqueConstraint)uniqueConstraintsByID.get(uniqueConstraintID);
					if(constraint==null)
						throw new NestingRuntimeException(e, "no unique constraint found for >"+uniqueConstraintID
																				+"<, has only "+uniqueConstraintsByID.keySet());
				}
				return new UniqueViolationException(e, null, constraint);
			}
		}
		{		
			final String integrityConstraintName = extractIntegrityConstraintName(e);
			if(integrityConstraintName!=null)
			{
				final ItemAttribute attribute;
				if(ANY_CONSTRAINT.equals(integrityConstraintName))
					attribute = onlyThreatenedIntegrityConstraint;
				else
				{
					final ItemColumn column =
						(ItemColumn)itemColumnsByIntegrityConstraintName.get(integrityConstraintName);
					if(column==null)
						throw new NestingRuntimeException(e, "no column attribute found for >"+integrityConstraintName
																				+"<, has only "+itemColumnsByIntegrityConstraintName.keySet());
					attribute = column.attribute;
					if(attribute==null)
						throw new NestingRuntimeException(e, "no item attribute for column "+column);
				}

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
	
	/**
	 * Trims a name to length for being a suitable qualifier for database entities,
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
	protected String protectName(final String name)
	{
		return '"' + name + '"';
	}

	protected boolean supportsCheckConstraints()
	{
		return true;
	}

	abstract String getIntegerType(int precision);
	abstract String getDoubleType(int precision);
	abstract String getStringType(int maxLength);
	
	private void dropTable(final ReportTable table) 
	{
		final Statement bf = createStatement();
		bf.append("drop table ").
			append(protectName(table.name));

		try
		{
			executeSQLUpdate(bf, 0);
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	private int countTable(final Table table)
	{
		final Statement bf = createStatement();
		bf.append("select count(*) from ").defineColumnInteger().
			append(table.protectedID);

		final CountResultSetHandler handler = new CountResultSetHandler();
		executeSQLQuery(bf, handler, false);
		return handler.result;
	}
	
	private static class CountResultSetHandler implements ResultSetHandler
	{
		int result;

		public void run(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();

			result = convertSQLResult(resultSet.getObject(1));
		}
	}


	protected Statement getDropForeignKeyConstraintStatement(final ReportTable table, final ReportForeignKeyConstraint fk)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(protectName(table.name)).
			append(" drop constraint ").
			append(protectName(fk.name));
		return bf;
	}
	
	private final void dropForeignKeyConstraints(final ReportTable table, final boolean log) 
	{
		for(Iterator i = table.getConstraints().iterator(); i.hasNext(); )
		{
			final ReportConstraint constraint = (ReportConstraint)i.next();
			//System.out.println("dropForeignKeyConstraints("+column+")");
			if(constraint instanceof ReportForeignKeyConstraint)
			{
				final ReportForeignKeyConstraint fk = (ReportForeignKeyConstraint)constraint;
				final Statement bf = getDropForeignKeyConstraintStatement(table, fk);

				if(log)
					System.err.println("DROPPING FOREIGN KEY CONSTRAINTS "+table.name+" "+fk.name+"... ");
				try
				{
					executeSQLUpdate(bf, 0);
				}
				catch(ConstraintViolationException e)
				{
					throw new NestingRuntimeException(e);
				}
			}
		}
	}
	
	final int[] getMinMaxPK(final Table table)
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
			
		final NextPKResultSetHandler handler = new NextPKResultSetHandler();
		executeSQLQuery(bf, handler, false);
		//System.err.println("select max("+type.primaryKey.trimmedName+") from "+type.trimmedName+" : "+handler.result);
		return handler.result;
	}
	
	private static class NextPKResultSetHandler implements ResultSetHandler
	{
		int[] result;

		public void run(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();
			final Object oLo = resultSet.getObject(1);
			if(oLo!=null)
			{
				result = new int[2];
				result[0] = convertSQLResult(oLo);
				final Object oHi = resultSet.getObject(2);
				result[1] = convertSQLResult(oHi);
			}
		}
	}
	
	protected abstract String getColumnType(final int dataType, final ResultSet resultSet)
			throws SQLException;
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	protected static final void printMeta(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();;
		final int columnCount = metaData.getColumnCount();
		for(int i = 1; i<=columnCount; i++)
			System.out.println("------"+i+":"+metaData.getColumnName(i)+":"+metaData.getColumnType(i));
	}
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	protected static final void printRow(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();;
		final int columnCount = metaData.getColumnCount();
		for(int i = 1; i<=columnCount; i++)
			System.out.println("----------"+i+":"+resultSet.getObject(i));
	}
	
	final ReportSchema requiredReport()
	{
		final ReportSchema report = new ReportSchema(this);
		for(Iterator i = getTables().iterator(); i.hasNext(); )
			((Table)i.next()).report(report);
		appendReport(report);
		return report;
	}
	
	protected void appendReport(final ReportSchema report)
	{
	}
	
	final ReportSchema report()
	{
		final ReportSchema report = requiredReport();
		fillReport(report);
		report.finish();
		return report;
	}

	void fillReport(final ReportSchema report)
	{
		{
			final com.exedio.cope.Statement bf = createStatement();
			bf.append(GET_TABLES);
			executeSQLQuery(bf, new ResultSetHandler()
				{
					public void run(final ResultSet resultSet) throws SQLException
					{
						while(resultSet.next())
						{
							final String tableName = resultSet.getString("TABLE_NAME");
							final ReportTable table = report.notifyExistentTable(tableName);
							//System.out.println("EXISTS:"+tableName);
						}
					}
				}, false);
		}
		{
			final com.exedio.cope.Statement bf = createStatement();
			bf.append(GET_COLUMNS);
			executeSQLQuery(bf, new ResultSetHandler()
				{
					public void run(final ResultSet resultSet) throws SQLException
					{
						while(resultSet.next())
						{
							final String tableName = resultSet.getString("TABLE_NAME");
							final String columnName = resultSet.getString("COLUMN_NAME");
							final int dataType = resultSet.getInt("DATA_TYPE");
							
							final ReportTable table = report.getTable(tableName);
							if(table!=null)
							{
								String columnType = getColumnType(dataType, resultSet);
								if(columnType==null)
									columnType = String.valueOf(dataType);

								table.notifyExistentColumn(columnName, columnType);
							}
							//System.out.println("EXISTS:"+tableName);
						}
					}
				}, false);
		}
	}

	final void renameTable(final String oldTableName, final String newTableName)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(protectName(oldTableName)).
			append(" rename to ").
			append(protectName(newTableName));

		try
		{
			//System.out.println("renameTable:"+bf);
			executeSQLUpdate(bf, 0);
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	final void dropTable(final String tableName)
	{
		final Statement bf = createStatement();
		bf.append("drop table ").
			append(protectName(tableName));

		try
		{
			//System.out.println("dropTable:"+bf);
			executeSQLUpdate(bf, 0);
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	final void analyzeTable(final String tableName)
	{
		final Statement bf = createStatement();
		bf.append("analyze table ").
			append(protectName(tableName)).
			append(" compute statistics");

		try
		{
			//System.out.println("analyzeTable:"+bf);
			executeSQLUpdate(bf, 0);
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	final void dropColumn(final String tableName, final String columnName)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(protectName(tableName)).
			append(" drop column ").
			append(protectName(columnName));

		try
		{
			//System.out.println("dropColumn:"+bf);
			executeSQLUpdate(bf, 0);
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	abstract Statement getRenameColumnStatement(String tableName, String oldColumnName, String newColumnName, String columnType);

	final void renameColumn(final String tableName,
			final String oldColumnName, final String newColumnName, final String columnType)
	{
		final Statement bf =
			getRenameColumnStatement(
				protectName(tableName),
				protectName(oldColumnName),
				protectName(newColumnName),
				columnType);

		try
		{
			//System.err.println("renameColumn:"+bf);
			executeSQLUpdate(bf, 0);
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	abstract Statement getCreateColumnStatement(final String tableName, final String columnName, final String columnType);

	final void createColumn(final ReportColumn column)
	{
		final Statement bf =
			getCreateColumnStatement(
				protectName(column.table.name),
				protectName(column.name),
				column.getType());

		try
		{
			//System.out.println("createColumn:"+bf);
			executeSQLUpdate(bf, 0);
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	abstract Statement getModifyColumnStatement(final String tableName, final String columnName, final String newColumnType);

	final void modifyColumn(final String tableName, final String columnName, final String newColumnType)
	{
		final Statement bf =
			getModifyColumnStatement(
				protectName(tableName),
				protectName(columnName),
				newColumnType);

		try
		{
			//System.out.println("modifyColumn:"+bf);
			executeSQLUpdate(bf, 0);
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	static final ResultSetHandler logHandler = new ResultSetHandler()
	{
		public void run(final ResultSet resultSet) throws SQLException
		{
			final int columnCount = resultSet.getMetaData().getColumnCount();
			System.out.println("columnCount:"+columnCount);
			final ResultSetMetaData meta = resultSet.getMetaData();
			for(int i = 1; i<=columnCount; i++)
			{
				System.out.println(meta.getColumnName(i)+"|");
			}
			while(resultSet.next())
			{
				for(int i = 1; i<=columnCount; i++)
				{
					System.out.println(resultSet.getObject(i)+"|");
				}
			}
		}
	};

}
