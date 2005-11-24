package com.exedio.cope;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;

public class LogDatabase extends WrappingDatabase
{
	final PrintWriter writer;
	
	public LogDatabase( Properties properties )
	{
		super( getWrappedDatabase(properties) );
		try
		{
			final String target = properties.getDatabaseCustomProperty("target");
			if ( target==null || target.equals("") )
			{
				throw new RuntimeException( "database.log.target not set" );
			}
			if ( target.equals("out") )
			{
				writer = new PrintWriter( System.out );
			}
			else if ( target.equals("err") )
			{
				writer = new PrintWriter( System.err, true );
			}
			else
			{
				writer = new PrintWriter( new FileWriter(target), true );
			}
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	private static Database getWrappedDatabase( Properties properties )
	{
		String dbCode = properties.getDatabaseCustomProperty("wrapped");
		return properties.createDatabase( dbCode );
	}
	
	public void load( Connection connection, PersistentState state )
	{
		writer.println( "load: "+state.toString() );
		super.load( connection, state );
	}
	
	public void store( Connection connection, State state, boolean present ) throws UniqueViolationException
	{
		writer.println( "store("+(present?"update":"insert")+"): "+state.toString() );
		super.store( connection, state, present );
	}
	
	public ArrayList search( Connection connection, Query query, boolean doCountOnly )
	{
		writer.println( "search(countOnly="+doCountOnly+"): "+query.getType() );
		// TODO: should be as follows, but that causes tests to fail with a FeatureNotInitializedException
		// writer.println( "search(countOnly="+doCountOnly+"): "+query.toString() );
		return super.search( connection, query, doCountOnly );
	}
	
}
