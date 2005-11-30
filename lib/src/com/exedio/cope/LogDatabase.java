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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;

class LogDatabase extends WrappingDatabase
{
	final PrintWriter writer;
	final boolean disable;
	
	LogDatabase( Database wrapped, String target, String disableString )
	{
		super( wrapped );
		try
		{
			if ( target==null || "".equals(target) )
			{
				throw new RuntimeException( "database.log.target not set" );
			}
			if ( target.equals("out") )
			{
				writer = new PrintWriter( System.out, true );
			}
			else if ( target.equals("err") )
			{
				writer = new PrintWriter( System.err, true );
			}
			else
			{
				writer = new PrintWriter( new FileWriter(target), true );
			}
			disable = disableString!=null && disableString.equalsIgnoreCase("true");
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public LogDatabase( Properties properties )
	{
		this( getWrappedDatabase(properties), properties.getDatabaseCustomProperty("target"), properties.getDatabaseCustomProperty("disable") );
	}
	
	private static Database getWrappedDatabase( Properties properties )
	{
		String dbCode = properties.getDatabaseCustomProperty("wrapped");
		return properties.createDatabase( dbCode );
	}
	
	public void load( Connection connection, PersistentState state )
	{
		if ( ! disable )
		{
			writer.println( "load: "+state.toString() );
		}
		super.load( connection, state );
	}
	
	public void store( Connection connection, State state, boolean present ) throws UniqueViolationException
	{
		if ( ! disable )
		{
			writer.println( "store("+(present?"update":"insert")+"): "+state.toStringWithValues() );
		}
		super.store( connection, state, present );
	}
	
	public ArrayList search( Connection connection, Query query, boolean doCountOnly )
	{
		if ( ! disable )
		{
			writer.println( "search(countOnly="+doCountOnly+"): "+query.getType() );
		}
		// TODO: should be as follows, but that causes tests to fail with a FeatureNotInitializedException
		// writer.println( "search(countOnly="+doCountOnly+"): "+query.toString() );
		return super.search( connection, query, doCountOnly );
	}
	
}
