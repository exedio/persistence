/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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
 *	An implementation of {@code DatabaseListener} that can be used to expect load and search calls
 * in unit tests, and to verify that all these and no other calls have been made
 * to the wrapped database.
 */
public class ExpectingDatabaseListener implements TestDatabaseListener
{
	private List<Call> expectedCalls = null;

	@Override
	public void load(final Connection connection, final Item item)
	{
		if ( expectedCalls!=null )
		{
			nextExpectedCall().checkLoad(connection, item);
		}
	}

	@Override
	public void search(final Connection connection, final Query<?> query, final Query.Mode mode)
	{
		nextExpectedCall().checkSearch( connection, query );
	}

	private List<Call> getExpectedCalls()
	{
		if ( expectedCalls==null )
		{
			expectedCalls = new LinkedList<>();
		}
		return expectedCalls;
	}

	public void expectNoCall()
	{
		if ( expectedCalls!=null ) throw new RuntimeException( expectedCalls.toString() );
		expectedCalls = Collections.emptyList();
	}

	public void expectLoad( final Transaction tx, final Item item )
	{
		getExpectedCalls().add( new LoadCall(tx, item) );
	}

	public void expectSearch( final Transaction tx, final Type<?> type )
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

	abstract static class Call
	{
		final Transaction tx;

		Call( final Transaction tx )
		{
			this.tx = tx;
		}

		/**
		 * @param connection used in subclasses
		 * @param item used in subclasses
		 */
		void checkLoad(final Connection connection, final Item item)
		{
			throw new RuntimeException( "load in " + this );
		}

		/**
		 * @param connection used in subclasses
		 * @param query used in subclasses
		 */
		void checkSearch( final Connection connection, final Query<?> query )
		{
			throw new RuntimeException( "search in " + this );
		}

		@SuppressWarnings("resource")
		void checkConnection( final Connection connection )
		{
			if ( ! tx.getConnection().equals(connection) )
			{
				throw new RuntimeException( "connection mismatch in "+this+": expected <"+connection+"> but was <"+tx.getConnection()+">" );
			}
		}
	}

	static class LoadCall extends Call
	{
		final Item item;

		LoadCall( final Transaction tx, final Item item )
		{
			super( tx );
			this.item = item;
		}

		@Override
		void checkLoad(final Connection connection, final Item item)
		{
			checkConnection( connection );
			if ( !this.item.equals(item) )
			{
				throw new RuntimeException( "item mismatch in "+this+" (got "+item.getCopeID()+")" );
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
		final Type<?> type;

		SearchCall( final Transaction tx, final Type<?> type )
		{
			super( tx );
			this.type = type;
		}

		@Override
		void checkSearch( final Connection connection, final Query<?> query )
		{
			checkConnection( connection );
			if ( !type.equals(query.getType()) )
			{
				throw new RuntimeException( "search type mismatch in "+this+" (got "+query.getType()+")" );
			}
		}

		@Override
		public String toString()
		{
			return "Search("+tx.getName()+"/"+type+")";
		}
	}
}
