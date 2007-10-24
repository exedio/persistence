/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *	An implementation of <tt>DatabaseListener</tt> that can be used to expect load and search calls
 * in unit tests, and to verify that all these and no other calls have been made
 * to the wrapped database.
 */
public class ExpectingDatabaseListener implements DatabaseListener
{
	private List<Call> expectedCalls = null;
	
	public void load(Connection connection, WrittenState state)
	{
		if ( expectedCalls!=null )
		{
			nextExpectedCall().checkLoad( connection, state );
		}
	}

	public void search(Connection connection, Query query, boolean totalOnly)
	{
		nextExpectedCall().checkSearch( connection, query );
	}

	private List<Call> getExpectedCalls()
	{
		if ( expectedCalls==null )
		{
			expectedCalls = new LinkedList<Call>();
		}
		return expectedCalls;
	}
	
	public void expectNoCall()
	{
		if ( expectedCalls!=null ) throw new RuntimeException( expectedCalls.toString() );
		expectedCalls = Collections.<Call>emptyList();
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
		return expectedCalls.remove(0);
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
		
		/**
		 * @param connection used in subclasses
		 * @param state used in subclasses
		 */
		void checkLoad(Connection connection, WrittenState state)
		{
			throw new RuntimeException( "load in "+toString() );
		}
		
		/**
		 * @param connection used in subclasses
		 * @param query used in subclasses
		 */
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
		
		@Override
		public/* TODO SOON workaround instrumentor bug with annotations */ void checkLoad(final Connection connection, final WrittenState state)
		{
			checkConnection( connection );
			if ( !item.equals(state.item) )
			{
				throw new RuntimeException( "item mismatch in "+toString()+" (got "+state.item.getCopeID()+")" );
			}
		}
		
		@Override
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
		
		@Override
		public/* TODO SOON workaround instrumentor bug with annotations */ void checkSearch( Connection connection, Query query )
		{
			checkConnection( connection );
			if ( !type.equals(query.getType()) )
			{
				throw new RuntimeException( "search type mismatch in "+toString()+" (got "+query.getType()+")" );
			}
		}
		
		@Override
		public String toString()
		{
			return "Search("+tx.getName()+"/"+type+")";
		}
	}
}
