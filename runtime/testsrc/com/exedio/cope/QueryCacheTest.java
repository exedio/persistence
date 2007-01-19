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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.exedio.cope.util.CacheQueryInfo;

public class QueryCacheTest extends AbstractLibTest
{
	public QueryCacheTest()
	{
		super(MatchTest.MODEL);
	}
	
	MatchItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new MatchItem());
	}
	
	private static final String Q1 = "select MatchItem.this from MatchItem where MatchItem.text='someString'";
	
	public void testQueryCache()
	{
		// start new transaction, otherwise query cache will not work,
		// because type is invalidated. 
		model.commit();
		model.startTransaction("QueryCacheTest");
		final boolean enabled = model.getProperties().getCacheQueryLimit()>0;
		assertEquals(list(), cqi());
		
		final DBL l = new DBL();
		model.setDatabaseListener(l);
		final Query q = item.TYPE.newQuery(item.text.equal("someString"));
		q.enableMakeStatementInfo(); // otherwise hits are not counted
		
		q.search();
		assertEquals(list(sc(q, false)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(Q1, 0)) : list(), cqi());
		
		q.search();
		assertEquals(enabled ? list() : list(sc(q, false)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(Q1, 1)) : list(), cqi());
		
		q.countWithoutLimit();
		assertEquals(list(sc(q, true)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(Q1, 1)) : list(), cqi());
		
		q.countWithoutLimit();
		assertEquals(list(sc(q, true)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(Q1, 1)) : list(), cqi());
		
		model.setDatabaseListener(null);
	}
	
	private CacheQueryInfo cqi(final String query, final int hits)
	{
		return new CacheQueryInfo(query, hits);
	}

	private List<CacheQueryInfo> cqi()
	{
		return Arrays.asList(model.getCacheQueryHistogram());
	}

	private SC sc(final Query query, final boolean doCountOnly)
	{
		return new SC(query, doCountOnly);
	}

	private class SC
	{
		final Query query;
		final boolean doCountOnly;
		
		SC(final Query query, final boolean doCountOnly)
		{
			this.query = query;
			this.doCountOnly = doCountOnly;
		}
		
		@Override
		public boolean equals(final Object other)
		{
			final SC o = (SC)other;
			return
				query == o.query && // do not use equals !!!
				doCountOnly == o.doCountOnly;
		}

		@Override
		public String toString()
		{
			return (doCountOnly ? "COUNT " : "SEARCH ") + query.toString();
		}
	}
	
	private class DBL implements DatabaseListener
	{
		final ArrayList<SC> scs = new ArrayList<SC>();
		
		public void load(Connection connection, PersistentState state)
		{
			throw new RuntimeException();
		}

		public void search(final Connection connection, final Query query, final boolean doCountOnly)
		{
			scs.add(new SC(query, doCountOnly));
		}
		
		void clear()
		{
			scs.clear();
		}
	}
}
