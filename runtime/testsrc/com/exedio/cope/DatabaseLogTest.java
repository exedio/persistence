/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.List;

import com.exedio.cope.misc.DatabaseListener;

public class DatabaseLogTest extends AbstractRuntimeTest
{
	public DatabaseLogTest()
	{
		super(MatchTest.MODEL);
	}
	
	MatchItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MatchItem());
	}

	@Override
	public void tearDown() throws Exception
	{
		model.setDatabaseListener(null);
		super.tearDown();
	}
	
	public void testDatabaseLog()
	{
		final ExpectingDatabaseListener l = new ExpectingDatabaseListener();
		model.setTestDatabaseListener(l);
		
		assertEquals(null, model.getDatabaseListener());
		
		final DBL dbl = new DBL();
		model.setDatabaseListener(dbl);
		assertSame(dbl, model.getDatabaseListener());
		l.expectSearch(model.getCurrentTransaction(), item.TYPE);
		item.TYPE.search(item.text.equal("string1"));
		l.verifyExpectations();
		dbl.assertSql("select");
		item.setText("string1");
		dbl.assertSql("update");
		
		final DBL dbl2 = new DBL();
		model.setDatabaseListener(dbl2);
		assertSame(dbl2, model.getDatabaseListener());
		l.expectSearch(model.getCurrentTransaction(), item.TYPE);
		item.TYPE.search(item.text.equal("string2"));
		l.verifyExpectations();
		dbl2.assertSql("select");
		item.setText("string2");
		dbl2.assertSql("update");
		dbl.assertSqlEmpty();
		
		final DBL dbl3 = new DBL();
		model.setDatabaseListener(dbl3);
		assertSame(dbl3, model.getDatabaseListener());
		l.expectSearch(model.getCurrentTransaction(), item.TYPE);
		item.TYPE.search(item.text.equal("string2"));
		l.verifyExpectations();
		dbl3.assertSql("select");
		item.setText("string2");
		dbl3.assertSql("update");
		dbl2.assertSqlEmpty();
		dbl.assertSqlEmpty();
		
		model.setDatabaseListener(null);
		assertNull(model.getDatabaseListener());
		l.expectSearch(model.getCurrentTransaction(), item.TYPE);
		item.TYPE.search(item.text.equal("string3"));
		l.verifyExpectations();
		item.setText("string3");
		dbl2.assertSqlEmpty();
		dbl.assertSqlEmpty();
		
		model.setTestDatabaseListener(null);
	}
	
	static final class DBL implements DatabaseListener
	{
		private String sql = null;
		
		public void onStatement(
				String sql,
				List<Object> parameters,
				long durationPrepare,
				long durationExecute,
				long durationRead,
				long durationClose)
		{
			assertNull(this.sql);
			assertNotNull(sql);
			this.sql = sql;
		}
		
		void assertSql(final String sql)
		{
			assertNotNull(this.sql);
			assertTrue(this.sql, this.sql.startsWith(sql));
			this.sql = null;
		}
		
		void assertSqlEmpty()
		{
			assertNull(this.sql);
		}
	}
}
