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
import static com.exedio.cope.MatchModel.MODEL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.misc.DatabaseListener;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatabaseLogTest extends TestWithEnvironment
{
	public DatabaseLogTest()
	{
		super(MODEL);
	}

	MatchItem item;

	@BeforeEach final void setUp()
	{
		item = new MatchItem();
	}

	@AfterEach final void tearDown()
	{
		model.setDatabaseListener(null);
	}

	@Test void testDatabaseLog()
	{
		final ExpectingDatabaseListener l = new ExpectingDatabaseListener();
		model.setTestDatabaseListener(l);

		assertEquals(null, model.getDatabaseListener());

		final DBL dbl1 = new DBL();
		model.setDatabaseListener(dbl1);
		assertSame(dbl1, model.getDatabaseListener());
		l.expectSearch(model.currentTransaction(), TYPE);
		TYPE.search(text.is("string1"));
		l.verifyExpectations();
		dbl1.assertSql("SELECT");
		item.setText("string1");
		dbl1.assertSql("UPDATE");

		final DBL dbl2 = new DBL();
		model.setDatabaseListener(dbl2);
		assertSame(dbl2, model.getDatabaseListener());
		l.expectSearch(model.currentTransaction(), TYPE);
		TYPE.search(text.is("string2"));
		l.verifyExpectations();
		dbl2.assertSql("SELECT");
		item.setText("string2");
		dbl2.assertSql("UPDATE");
		dbl1.assertSqlEmpty();

		final DBL dbl3 = new DBL();
		model.setDatabaseListener(dbl3);
		assertSame(dbl3, model.getDatabaseListener());
		l.expectSearch(model.currentTransaction(), TYPE);
		TYPE.search(text.is("string2"));
		l.verifyExpectations();
		dbl3.assertSql("SELECT");
		item.setText("string2b");
		dbl3.assertSql("UPDATE");
		dbl2.assertSqlEmpty();
		dbl1.assertSqlEmpty();

		model.setDatabaseListener(null);
		assertNull(model.getDatabaseListener());
		l.expectSearch(model.currentTransaction(), TYPE);
		TYPE.search(text.is("string3"));
		l.verifyExpectations();
		item.setText("string3");
		dbl2.assertSqlEmpty();
		dbl1.assertSqlEmpty();

		model.setTestDatabaseListener(null);
	}

	static final class DBL implements DatabaseListener
	{
		private String sql = null;

		@Override
		public void onStatement(
				final String sql,
				final List<Object> parameters,
				final long durationPrepare,
				final long durationExecute,
				final long durationRead,
				final long durationClose)
		{
			assertNull(this.sql);
			assertNotNull(sql);
			this.sql = sql;
		}

		void assertSql(final String sql)
		{
			assertNotNull(this.sql);
			assertTrue(this.sql.startsWith(sql), this.sql);
			this.sql = null;
		}

		void assertSqlEmpty()
		{
			assertNull(sql);
		}
	}
}
