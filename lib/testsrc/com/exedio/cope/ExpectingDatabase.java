package com.exedio.cope;

import java.sql.Connection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ExpectingDatabase implements Database
{
	final Database nested;
	
	private List expectedCalls = null;
	
	public ExpectingDatabase( Database nested )
	{
		this.nested = nested;
	}
	
	Database getNestedDatabase()
	{
		return nested;
	}

	public void addIntegrityConstraint(ItemColumn itemColumn)
	{
		nested.addIntegrityConstraint( itemColumn );
	}

	public void addTable(Table table)
	{
		nested.addTable( table );
	}

	public void addUniqueConstraint(String str, UniqueConstraint uniqueConstraint)
	{
		nested.addUniqueConstraint( str, uniqueConstraint );
	}

	public void appendMatchClause(Statement statement, StringFunction stringFunction, String str)
	{
		nested.appendMatchClause( statement, stringFunction, str );
	}

	public void checkDatabase(Connection connection)
	{
		nested.checkDatabase( connection );
	}

	public void checkEmptyDatabase(Connection connection)
	{
		nested.checkEmptyDatabase( connection );
	}

	public void createDatabase()
	{
		nested.createDatabase();
	}

	public void createDatabaseConstraints()
	{
		nested.createDatabaseConstraints();
	}

	public void delete(Connection connection, Item item)
	{
		nested.delete(connection, item);
	}

	public void dropDatabase()
	{
		nested.dropDatabase();
	}

	public void dropDatabaseConstraints()
	{
		nested.dropDatabaseConstraints();
	}

	public ConnectionPool getConnectionPool()
	{
		return nested.getConnectionPool();
	}

	public String getDayType()
	{
		return nested.getDayType();
	}

	public String getDoubleType(int precision)
	{
		return nested.getDoubleType( precision );
	}

	public com.exedio.dsmf.Driver getDriver()
	{
		return nested.getDriver();
	}

	public String getIntegerType(int precision)
	{
		return nested.getIntegerType( precision );
	}

	public int[] getMinMaxPK(Connection connection, Table table)
	{
		return nested.getMinMaxPK( connection, table );
	}

	public String getStringType(int param)
	{
		return nested.getStringType( param );
	}

	public java.util.Properties getTableOptions()
	{
		return nested.getTableOptions();
	}

	public void load(Connection connection, PersistentState state)
	{
		if ( expectedCalls!=null )
		{
			nextExpectedCall().checkLoad( connection, state );
		}
		nested.load( connection, state );
	}

	public String makeName(String str)
	{
		return nested.makeName( str );
	}

	public String makeName(String str, String str1)
	{
		return nested.makeName( str, str1 );
	}

	public PkSource makePkSource(Table table)
	{
		return nested.makePkSource( table );
	}

	public com.exedio.dsmf.Schema makeSchema()
	{
		return nested.makeSchema();
	}

	public com.exedio.dsmf.Schema makeVerifiedSchema()
	{
		return nested.makeVerifiedSchema();
	}

	public java.util.ArrayList search(Connection connection, Query query, boolean doCountOnly)
	{
		nextExpectedCall().checkSearch( connection, query );
		return nested.search( connection, query, doCountOnly );
	}

	public void store(Connection connection, State state, boolean param) throws UniqueViolationException
	{
		nested.store( connection, state, param );
	}

	public boolean supportsCheckConstraints()
	{
		return nested.supportsCheckConstraints();
	}

	public boolean supportsEmptyStrings()
	{
		return nested.supportsEmptyStrings();
	}

	public boolean supportsRightOuterJoins()
	{
		return nested.supportsRightOuterJoins();
	}

	public void tearDownDatabase()
	{
		nested.tearDownDatabase();
	}

	public void tearDownDatabaseConstraints()
	{
		nested.tearDownDatabaseConstraints();
	}

	private List getExpectedCalls()
	{
		if ( expectedCalls==null ) 
		{
			expectedCalls = new LinkedList();
		}
		return expectedCalls;
	}
	
	public void expectNoCall()
	{
		if ( expectedCalls!=null ) throw new RuntimeException( expectedCalls.toString() );
		expectedCalls = Collections.EMPTY_LIST;
	}
	
	public void expectLoad( Transaction tx, Item item )
	{
		getExpectedCalls().add( new LoadCall(tx, item) );
	}
	
	public void expectSearch( Transaction tx, Type type )
	{
		getExpectedCalls().add( new SearchCall(tx, type) );
	}
	
	public void verifyExpectations()
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
	public boolean clearExpectedCalls()
	{
		boolean result = expectedCalls!=null;
		expectedCalls = null;
		return result;
	}

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
				throw new RuntimeException( "connection mismatch in "+toString()+": expected <"+connection+"> but was <"+tx.getConnection()+">" );
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
