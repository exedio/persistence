package com.exedio.cope;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class WrappingDatabase implements Database
{
	private final Database nested;
	
	public WrappingDatabase( Database nested )
	{
		this.nested = nested;
	}
	
	Database getWrappedDatabase()
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

	public ArrayList search(Connection connection, Query query, boolean doCountOnly)
	{
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
}
