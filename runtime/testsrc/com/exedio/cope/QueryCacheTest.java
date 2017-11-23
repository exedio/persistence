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

import static com.exedio.cope.MatchItem.TYPE;
import static com.exedio.cope.MatchItem.text;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public class QueryCacheTest extends TestWithEnvironment
{
	public QueryCacheTest()
	{
		super(MatchTest.MODEL);
	}

	@SuppressWarnings("static-method")
	@Before public final void setUp()
	{
		new MatchItem();
	}

	private static final String Q1 = "select this from MatchItem where text='someString'";
	private static final String C1 = "select count(*) from MatchItem where text='someString'";
	private static final String Q2 = "select this from MatchItem where text='someString2'";
	private static final String C2 = "select count(*) from MatchItem where text='someString2'";

	@Test public void testQueryCache()
	{
		// start new transaction, otherwise query cache will not work,
		// because type is invalidated.
		restartTransaction();

		final boolean enabled = model.getConnectProperties().getQueryCacheLimit()>0;
		assertEquals(list(), qch());

		final DBL l = new DBL();
		model.setTestDatabaseListener(l);
		final Query<?> q1 = TYPE.newQuery(text.equal("someString"));
		final Query<?> q2 = TYPE.newQuery(text.equal("someString2"));

		q1.search();
		assertEquals(list(sc(q1, false)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(Q1, 0, 0)) : list(), qch());

		q1.search();
		assertEquals(enabled ? list() : list(sc(q1, false)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(Q1, 0, 1)) : list(), qch());

		q2.search();
		assertEquals(list(sc(q2, false)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(Q2, 0, 0), cqi(Q1, 0, 1)) : list(), qch());

		q1.total();
		assertEquals(list(sc(q1, true)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(C1, 1, 0), cqi(Q2, 0, 0), cqi(Q1, 0, 1)) : list(), qch());

		q1.total();
		assertEquals(enabled ? list() : list(sc(q1, true)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(C1, 1, 1), cqi(Q2, 0, 0), cqi(Q1, 0, 1)) : list(), qch());

		q2.total();
		assertEquals(list(sc(q2, true)), l.scs);
		l.clear();
		assertEquals(enabled ? list(cqi(C2, 1, 0), cqi(C1, 1, 1), cqi(Q2, 0, 0), cqi(Q1, 0, 1)) : list(), qch());

		model.clearCache();
		assertEquals(list(), qch());

		model.setTestDatabaseListener(null);
	}

	private static QueryCacheHistogram cqi(final String query, final int resultSize, final int hits)
	{
		return new QueryCacheHistogram(query, resultSize, hits);
	}

	private List<QueryCacheHistogram> qch()
	{
		return Arrays.asList(model.getQueryCacheHistogram());
	}

	private static SC sc(final Query<?> query, final boolean totalOnly)
	{
		return new SC(query, totalOnly);
	}

	private static final class SC
	{
		final Query<?> query;
		final boolean totalOnly;

		SC(final Query<?> query, final boolean totalOnly)
		{
			this.query = query;
			this.totalOnly = totalOnly;
		}

		@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
		@SuppressFBWarnings({"NP_EQUALS_SHOULD_HANDLE_NULL_ARGUMENT", "BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS"})
		@Override
		public boolean equals(final Object other)
		{
			final SC o = (SC)other;
			return
				query == o.query && // do not use equals !!!
				totalOnly == o.totalOnly;
		}

		@Override
		public int hashCode()
		{
			// avoid warning:
			// class should implement hashCode() since it overrides equals()
			// but hashCode is not used here
			throw new RuntimeException();
		}

		@Override
		public String toString()
		{
			return (totalOnly ? "TOTAL " : "SEARCH ") + query;
		}
	}

	private static final class DBL implements TestDatabaseListener
	{
		final ArrayList<SC> scs = new ArrayList<>();

		DBL()
		{
			// make constructor non-private
		}

		@Override
		public void load(final Connection connection, final Item item)
		{
			throw new RuntimeException();
		}

		@Override
		public void search(final Connection connection, final Query<?> query, final boolean totalOnly)
		{
			scs.add(new SC(query, totalOnly));
		}

		void clear()
		{
			scs.clear();
		}
	}
}
