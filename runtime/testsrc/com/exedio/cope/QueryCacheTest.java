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
	
	private static final String Q1 = "select this from MatchItem where text='someString'";
	private static final String C1 = "select count(*) from MatchItem where text='someString'";
	private static final String Q2 = "select this from MatchItem where text='someString2'";
	private static final String C2 = "select count(*) from MatchItem where text='someString2'";
	
	public void testQueryCache()
	{
		// start new transaction, otherwise query cache will not work,
		// because type is invalidated.
		restartTransaction();
		
		final boolean enabled = model.getProperties().getQueryCacheLimit()>0;
		assertEquals(list(), cqi());
		
		final DBL l = new DBL();
		model.setDatabaseListener(l);
		final Query q1 = item.TYPE.newQuery(item.text.equal("someString"));
		final Query q2 = item.TYPE.newQuery(item.text.equal("someString2"));
		
		q1.search();
		assertEquals(list(sc(q1, false)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(Q1, 0, 0)) : list(), cqi());
		
		q1.search();
		assertEquals(enabled ? list() : list(sc(q1, false)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(Q1, 0, 1)) : list(), cqi());
		
		q2.search();
		assertEquals(list(sc(q2, false)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(Q2, 0, 0), cqi(Q1, 0, 1)) : list(), cqi());
		
		q1.total();
		assertEquals(list(sc(q1, true)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(C1, 1, 0), cqi(Q2, 0, 0), cqi(Q1, 0, 1)) : list(), cqi());
		
		q1.total();
		assertEquals(enabled ? list() : list(sc(q1, true)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(C1, 1, 1), cqi(Q2, 0, 0), cqi(Q1, 0, 1)) : list(), cqi());
		
		q2.total();
		assertEquals(list(sc(q2, true)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(C2, 1, 0), cqi(C1, 1, 1), cqi(Q2, 0, 0), cqi(Q1, 0, 1)) : list(), cqi());
		
		model.clearCache();
		assertEquals(list(), cqi());
		
		model.setDatabaseListener(null);
	}
	
	private CacheQueryInfo cqi(final String query, final int resultSize, final int hits)
	{
		return new CacheQueryInfo(query, resultSize, hits);
	}

	private List<CacheQueryInfo> cqi()
	{
		return Arrays.asList(model.getQueryCacheHistogram());
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
		
		public void load(Connection connection, WrittenState state)
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
