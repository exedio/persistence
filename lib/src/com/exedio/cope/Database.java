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
import java.util.LinkedList;
import java.util.List;

import com.exedio.cope.search.Condition;
import com.exedio.dsmf.Driver;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;

abstract class Database
{
	private final List tables = new ArrayList();
	private final HashMap uniqueConstraintsByID = new HashMap();
	private final HashMap itemColumnsByIntegrityConstraintName = new HashMap();
	private boolean buildStage = true;
	final Driver driver;
	private final boolean useDefineColumnTypes;
	final ConnectionPool connectionPool;
	private List expectedCalls = null;
	
	protected Database(final Driver driver, final Properties properties)
	{
		this.driver = driver;
		this.useDefineColumnTypes = this instanceof DatabaseColumnTypesDefinable;
		this.connectionPool = new ConnectionPool(properties);
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
	
	protected final Statement createStatement()
	{
		return new Statement(useDefineColumnTypes);
	}
	
	void createDatabase()
	{
		buildStage = false;
		
		makeSchema().create();
	}

	//private static int checkTableTime = 0;

	void checkDatabase(final Connection connection)
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
		executeSQLQuery(connection, bf,
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

		makeSchema().drop();
	}
	
	void tearDownDatabase()
	{
		buildStage = false;

		makeSchema().tearDown();
	}

	void checkEmptyDatabase(final Connection connection)
	{
		buildStage = false;

		//final long time = System.currentTimeMillis();
		for(Iterator i = tables.iterator(); i.hasNext(); )
		{
			final Table table = (Table)i.next();
			final int count = countTable(connection, table);
			if(count>0)
				throw new RuntimeException("there are "+count+" items left for table "+table.id);
		}
		//final long amount = (System.currentTimeMillis()-time);
		//checkEmptyTableTime += amount;
		//System.out.println("CHECK EMPTY TABLES "+amount+"ms  accumulated "+checkEmptyTableTime);
	}
	
	final ArrayList search(final Connection connection, final Query query, final boolean doCountOnly)
	{
		if ( expectedCalls!=null )
		{
			nextExpectedCall().checkSearch( connection, query );
		}
		
		buildStage = false;

		final Statement bf = createStatement();
		bf.setJoinsToAliases(query);
		bf.append("select ");

		final Selectable[] selectables = query.selectables;
		final Column[] selectColumns = new Column[selectables.length];
		final Type[] selectTypes = new Type[selectables.length];

		if(doCountOnly)
		{
			bf.append("count(*)");
		}
		else
		{
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
						selectColumn = ((ObjectAttribute)selectAttribute).getColumn();
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
						bf.append(computedFunction, (Join)null).defineColumn(computedFunction);
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
		}

		bf.append(" from ").
			append(query.type.getTable().protectedID);
		final String fromAlias = bf.getAlias(null);
		if(fromAlias!=null)
		{
			bf.append(' ').
				append(fromAlias);
		}

		final ArrayList queryJoins = query.joins;
		if(queryJoins!=null)
		{
			for(Iterator i = queryJoins.iterator(); i.hasNext(); )
			{
				final Join join = (Join)i.next();
				
				bf.append(' ').
					append(join.getKindString()).
					append(" join ").
					append(join.type.getTable().protectedID);
				final String joinAlias = bf.getAlias(join);
				if(joinAlias!=null)
				{
					bf.append(' ').
						append(joinAlias);
				}

				final Condition joinCondition = join.condition;
				if(joinCondition!=null)
				{
					bf.append(" on ");
					joinCondition.appendStatement(bf);
				}
			}
		}

		if(query.condition!=null)
		{
			bf.append(" where ");
			query.condition.appendStatement(bf);
		}

		if(!doCountOnly)
		{
			boolean firstOrderBy = true;		
			if(query.orderBy!=null || query.deterministicOrder)
				bf.append(" order by ");
	
			if(query.orderBy!=null)
			{
				firstOrderBy = false;
	
				bf.append(query.orderBy, (Join)null);
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

		query.statementInfo = executeSQLQuery(connection, bf, new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					if(doCountOnly)
					{
						resultSet.next();
						result.add(new Integer(resultSet.getInt(1)));
						if(resultSet.next())
							throw new RuntimeException();
						return;
					}
					
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
						
					int i = (count==Query.UNLIMITED_COUNT ? Integer.MAX_VALUE : count );
					if(i<=0)
						throw new RuntimeException(String.valueOf(count));

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

									resultCell = currentType.getItemObject(pk.intValue());
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
	
	private List getExpectedCalls()
	{
		if ( expectedCalls==null ) 
		{
			expectedCalls = new LinkedList();
		}
		return expectedCalls;
	}
	
	void expectNoCall()
	{
		if ( expectedCalls!=null ) throw new RuntimeException( expectedCalls.toString() );
		expectedCalls = Collections.EMPTY_LIST;
	}
	
	void expectLoad( Transaction tx, Item item )
	{
		getExpectedCalls().add( new LoadCall(tx, item) );
	}
	
	void expectSearch( Transaction tx, Type type )
	{
		getExpectedCalls().add( new SearchCall(tx, type) );
	}
	
	void verifyExpectations()
	{
		if ( expectedCalls==null ) throw new RuntimeException("no expectations set");
		if ( !expectedCalls.isEmpty() )
		{
			throw new RuntimeException( "missing calls: "+expectedCalls );
		}
		expectedCalls = null;
	}
	
	Call nextExpectedCall()
	{
		if ( expectedCalls.isEmpty() )
		{
			throw new RuntimeException( "no more calls expected" );
		}
		return (Call)expectedCalls.remove(0);
	}
	
	/**
	 *	return true if the database was in expectation checking mode
	 */
	boolean clearExpectedCalls()
	{
		boolean result = expectedCalls!=null;
		expectedCalls = null;
		return result;
	}

	void load(final Connection connection, final PersistentState state)
	{
		if ( expectedCalls!=null )
		{
			nextExpectedCall().checkLoad( connection, state );
		}
		
		buildStage = false;

		final Statement bf = createStatement();
		bf.append("select ");

		boolean first = true;
		for(Type type = state.type; type!=null; type = type.getSupertype())
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
		
		if(first)
		{
			// no columns in type
			bf.append(state.type.getTable().getPrimaryKey().protectedID);
		}

		bf.append(" from ");
		first = true;
		for(Type type = state.type; type!=null; type = type.getSupertype())
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(type.getTable().protectedID);
		}
			
		bf.append(" where ");
		first = true;
		for(Type type = state.type; type!=null; type = type.getSupertype())
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
				append(state.pk);
		}

		//System.out.println("loading "+bf.toString());
		// TODO: let PersistentState be its own ResultSetHandler
		executeSQLQuery(connection, bf, new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					if(!resultSet.next())
						throw new NoSuchItemException( state.item );
					else
					{
						int columnIndex = 1;
						for(Type type = state.type; type!=null; type = type.getSupertype())
						{
							for(Iterator i = type.getTable().getColumns().iterator(); i.hasNext(); )
								((Column)i.next()).load(resultSet, columnIndex++, state);
						}
					}
				}
			}, false);
	}

	void store(final Connection connection, final State state, final boolean present)
			throws UniqueViolationException
	{
		store(connection, state, state.type, present);
	}

	private void store(final Connection connection, final State state, final Type type, final boolean present)
			throws UniqueViolationException
	{
		buildStage = false;

		final Type supertype = type.getSupertype();
		if(supertype!=null)
			store(connection, state, supertype, present);
			
		final Table table = type.getTable();

		final List columns = table.getColumns();

		final Statement bf = createStatement();
		if(present)
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

				final Object value = state.store(column);
				bf.append(column.cacheToDatabase(value));
			}
			bf.append(" where ").
				append(table.getPrimaryKey().protectedID).
				append('=').
				append(state.pk);
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
				append(state.pk);
			
			if(typeColumn!=null)
			{
				bf.append(",'").
					append(state.type.getID()).
					append('\'');
			}

			for(Iterator i = columns.iterator(); i.hasNext(); )
			{
				bf.append(',');
				final Column column = (Column)i.next();
				final Object value = state.store(column);
				bf.append(column.cacheToDatabase(value));
			}
			bf.append(')');
		}

		//System.out.println("storing "+bf.toString());
		final UniqueConstraint[] uqs = type.uniqueConstraints;
		executeSQLUpdate(connection, bf, 1, uqs.length==1?uqs[0]:null);
	}

	void delete(final Connection connection, final Item item)
	{
		buildStage = false;
		final Type type = item.type;
		final int pk = item.pk;

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
				executeSQLUpdate(connection, bf, 1);
			}
			catch(UniqueViolationException e)
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

	//private static int timeExecuteQuery = 0;

	private final StatementInfo executeSQLQuery(
		final Connection connection,
		final Statement statement,
		final ResultSetHandler resultSetHandler,
		final boolean makeStatementInfo)
	{
		java.sql.Statement sqlStatement = null;
		ResultSet resultSet = null;
		try
		{
			final String sqlText = statement.getText();
			// TODO: use prepared statements and reuse the statement.
			sqlStatement = connection.createStatement();
			
			if(useDefineColumnTypes)
				((DatabaseColumnTypesDefinable)this).defineColumnTypes(statement.columnTypes, sqlStatement);

			//System.out.println(Transaction.get().toString()+": "+sqlText);
			//long time = System.currentTimeMillis();
			resultSet = sqlStatement.executeQuery(sqlText);
			//long interval = System.currentTimeMillis() - time;
			//timeExecuteQuery += interval;
			//System.out.println("executeQuery: "+interval+"ms sum "+timeExecuteQuery+"ms");

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
			throw new SQLRuntimeException(e, statement.toString());
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
		}
	}
	
	private final void executeSQLUpdate(final Connection connection, final Statement statement, final int expectedRows)
			throws UniqueViolationException
	{
		executeSQLUpdate(connection, statement, expectedRows, null);
	}

	private final void executeSQLUpdate(
			final Connection connection,
			final Statement statement, final int expectedRows,
			final UniqueConstraint onlyThreatenedUniqueConstraint)
		throws UniqueViolationException
	{
		java.sql.Statement sqlStatement = null;
		try
		{
			// TODO: use prepared statements and reuse the statement.
			final String sqlText = statement.getText();
			//System.err.println(Transaction.get().toString()+": "+statement.getText());
			sqlStatement = connection.createStatement();
			final int rows = sqlStatement.executeUpdate(sqlText);

			//System.out.println("("+rows+"): "+statement.getText());
			if(rows!=expectedRows)
				throw new RuntimeException("expected "+expectedRows+" rows, but got "+rows+" on statement "+sqlText);
		}
		catch(SQLException e)
		{
			final UniqueViolationException wrappedException = wrapException(e, onlyThreatenedUniqueConstraint);
			if(wrappedException!=null)
				throw wrappedException;
			else
				throw new SQLRuntimeException(e, statement.toString());
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
		}
	}
	
	protected StatementInfo makeStatementInfo(final Statement statement, final Connection connection)
	{
		return new StatementInfo(statement.getText());
	}
	
	protected abstract String extractUniqueConstraintName(SQLException e);
	
	protected final static String ANY_CONSTRAINT = "--ANY--";

	private final UniqueViolationException wrapException(
			final SQLException e,
			final UniqueConstraint onlyThreatenedUniqueConstraint)
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
					throw new SQLRuntimeException(e, "no unique constraint found for >"+uniqueConstraintID
																			+"<, has only "+uniqueConstraintsByID.keySet());
			}
			return new UniqueViolationException(e, null, constraint);
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

	protected boolean supportsCheckConstraints()
	{
		return true;
	}

	protected boolean supportsEmptyStrings()
	{
		return true;
	}

	protected boolean supportsRightOuterJoins()
	{
		return true;
	}

	abstract String getIntegerType(int precision);
	abstract String getDoubleType(int precision);
	abstract String getStringType(int maxLength);
	
	private int countTable(final Connection connection, final Table table)
	{
		final Statement bf = createStatement();
		bf.append("select count(*) from ").defineColumnInteger().
			append(table.protectedID);

		final CountResultSetHandler handler = new CountResultSetHandler();
		executeSQLQuery(connection, bf, handler, false);
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


	final int[] getMinMaxPK(final Connection connection, final Table table)
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
		executeSQLQuery(connection, bf, handler, false);
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
	
	final Schema makeSchema()
	{
		final Schema result = new Schema(driver, connectionPool);
		for(Iterator i = tables.iterator(); i.hasNext(); )
			((Table)i.next()).makeSchema(result);
		completeSchema(result);
		return result;
	}
	
	protected void completeSchema(final Schema schema)
	{
	}
	
	final Schema makeVerifiedSchema()
	{
		final Schema result = makeSchema();
		result.verify();
		return result;
	}

	
	
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
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
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
	
	static abstract class Call 
	{
		final Transaction tx;

		Call( Transaction tx )
		{
			this.tx = tx;
		}
		
		void checkLoad( Connection connection, PersistentState state )
		{
			throw new RuntimeException( "load in "+toString() );
		}
		
		void checkSearch( Connection connection, Query query )
		{
			throw new RuntimeException( "search in "+toString() );
		}
		
		void checkConnection( Connection connection )
		{
			if ( ! tx.getConnection().equals(connection) )
			{
				throw new RuntimeException( "connection mismatch in "+toString() );
			}
		}
	}

	static class LoadCall extends Call
	{
		final Item item;
		
		LoadCall( Transaction tx, Item item )
		{
			super( tx );
			this.item = item;
		}
		
		void checkLoad( Connection connection, PersistentState state )
		{
			checkConnection( connection );
			if ( !item.equals(state.item) )
			{
				throw new RuntimeException( "item mismatch in "+toString()+" (got "+state.item.getCopeID()+")" );
			}
		}
		
		public String toString()
		{
			return "Load("+tx.getName()+"/"+item.getCopeID()+")";
		}
	}
	
	static class SearchCall extends Call
	{
		final Type type;
		
		SearchCall( Transaction tx, Type type )
		{
			super( tx );
			this.type = type;
		}
		
		void checkSearch( Connection connection, Query query )
		{
			checkConnection( connection );
			if ( !type.equals(query.getType()) )
			{
				throw new RuntimeException( "search type mismatch in "+toString()+" (got "+query.getType()+")" );
			}
		}
		
		public String toString()
		{
			return "Search("+tx.getName()+"/"+type+")";
		}
	}
}
