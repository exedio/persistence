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

import static com.exedio.cope.CacheIsolationItem.TYPE;
import static java.lang.Integer.MAX_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.SI;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public final class UpdateCounterRecoverTest extends TestWithEnvironment
{
	public UpdateCounterRecoverTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	CacheIsolationItem item = null;

	@BeforeEach void setUp()
	{
		item = new CacheIsolationItem("name0");
	}

	@Test void testSameTransaction() throws SQLException
	{
		assertEquals("name0", item.getName());
		commit();

		// This transaction fills the global cache
		startTransaction();
		assertEquals("name0", item.getName());
		commit();

		execute(
				"update " + SI.tab(TYPE) +
				" set " + SI.update(TYPE) + "=99");

		startTransaction();
		assertEquals("name0", item.getName());
		if(cache)
		{
			try
			{
				item.setName("name1");
				fail();
			}
			catch(final TemporaryTransactionException e)
			{
				assertTrue(e.getMessage().startsWith("expected one row, but got 0 on statement: "), e.getMessage());
			}
			assertEquals("name0", item.getName());
		}
		else
		{
			item.setName("name1");
			assertEquals("name1", item.getName());
		}

		item.setName("name2");
		assertEquals("name2", item.getName());
	}

	@Test void testSameTransactionDelete() throws SQLException
	{
		assertEquals(true, item.existsCopeItem());
		commit();

		// This transaction fills the global cache
		startTransaction();
		assertEquals(true, item.existsCopeItem());
		commit();

		execute(
				"update " + SI.tab(TYPE) +
				" set " + SI.update(TYPE) + "=99");

		startTransaction();
		assertEquals(true, item.existsCopeItem());
		if(cache)
		{
			try
			{
				item.deleteCopeItem();
				fail();
			}
			catch(final TemporaryTransactionException e)
			{
				assertTrue(e.getMessage().startsWith("expected one row, but got 0 on statement: "), e.getMessage());
			}
			assertEquals(true, item.existsCopeItem());
		}

		item.deleteCopeItem();
		assertEquals(false, item.existsCopeItem());
	}

	@Test void testCommit() throws SQLException
	{
		assertEquals("name0", item.getName());
		commit();

		// This transaction fills the global cache
		startTransaction();
		assertEquals("name0", item.getName());
		commit();

		execute(
				"update " + SI.tab(TYPE) +
				" set " + SI.update(TYPE) + "=99");

		startTransaction();
		assertEquals("name0", item.getName());
		if(cache)
		{
			try
			{
				item.setName("name1");
				fail();
			}
			catch(final TemporaryTransactionException e)
			{
				assertTrue(e.getMessage().startsWith("expected one row, but got 0 on statement: "), e.getMessage());
			}
			assertEquals("name0", item.getName());
		}
		else
		{
			item.setName("name1");
			assertEquals("name1", item.getName());
		}

		commit();
		startTransaction();

		item.setName("name2");
		assertEquals("name2", item.getName());
	}

	@Test void testCommitDelete() throws SQLException
	{
		assertEquals(true, item.existsCopeItem());
		commit();

		// This transaction fills the global cache
		startTransaction();
		assertEquals(true, item.existsCopeItem());
		commit();

		execute(
				"update " + SI.tab(TYPE) +
				" set " + SI.update(TYPE) + "=99");

		startTransaction();
		assertEquals(true, item.existsCopeItem());
		if(cache)
		{
			try
			{
				item.deleteCopeItem();
				fail();
			}
			catch(final TemporaryTransactionException e)
			{
				assertTrue(e.getMessage().startsWith("expected one row, but got 0 on statement: "), e.getMessage());
			}
			assertEquals(true, item.existsCopeItem());
		}

		commit();
		startTransaction();

		item.deleteCopeItem();
		assertEquals(false, item.existsCopeItem());
	}

	@Test void testRollback() throws SQLException
	{
		assertEquals("name0", item.getName());
		commit();

		// This transaction fills the global cache
		startTransaction();
		assertEquals("name0", item.getName());
		commit();

		execute(
				"update " + SI.tab(TYPE) +
				" set " + SI.update(TYPE) + "=99");

		startTransaction();
		assertEquals("name0", item.getName());
		if(cache)
		{
			try
			{
				item.setName("name1");
				fail();
			}
			catch(final TemporaryTransactionException e)
			{
				assertTrue(e.getMessage().startsWith("expected one row, but got 0 on statement: "), e.getMessage());
			}
			assertEquals("name0", item.getName());
		}
		else
		{
			item.setName("name1");
			assertEquals("name1", item.getName());
		}

		model.rollback();
		startTransaction();

		item.setName("name2");
		assertEquals("name2", item.getName());
	}

	@Test void testRollbackDelete() throws SQLException
	{
		assertEquals(true, item.existsCopeItem());
		commit();

		// This transaction fills the global cache
		startTransaction();
		assertEquals(true, item.existsCopeItem());
		commit();

		execute(
				"update " + SI.tab(TYPE) +
				" set " + SI.update(TYPE) + "=99");

		startTransaction();
		assertEquals(true, item.existsCopeItem());
		if(cache)
		{
			try
			{
				item.deleteCopeItem();
				fail();
			}
			catch(final TemporaryTransactionException e)
			{
				assertTrue(e.getMessage().startsWith("expected one row, but got 0 on statement: "), e.getMessage());
			}
			assertEquals(true, item.existsCopeItem());
		}

		model.rollback();
		startTransaction();

		item.deleteCopeItem();
		assertEquals(false, item.existsCopeItem());
	}

	@Test void testOverflow() throws SQLException
	{
		final String count = "select " + SI.update(TYPE) + " from " + SI.tab(TYPE);

		assertEquals("name0", item.getName());
		commit();
		assertEquals(0, queryLong(count));

		startTransaction();
		assertEquals("name0", item.getName());
		item.setName("name1");
		assertEquals("name1", item.getName());
		commit();
		assertEquals(1, queryLong(count));

		startTransaction();
		assertEquals("name1", item.getName());
		item.setName("name2");
		assertEquals("name2", item.getName());
		commit();
		assertEquals(2, queryLong(count));

		execute(
				"update " + SI.tab(TYPE) +
				" set " + SI.update(TYPE) + "=" + (MAX_VALUE-1));
		assertEquals(MAX_VALUE-1, queryLong(count));
		model.clearCache();

		startTransaction();
		assertEquals("name2", item.getName());
		item.setName("name3");
		assertEquals("name3", item.getName());
		commit();
		assertEquals(MAX_VALUE, queryLong(count));

		startTransaction();
		assertEquals("name3", item.getName());
		item.setName("name4");
		assertEquals("name4", item.getName());
		commit();
		assertEquals(0, queryLong(count));

		startTransaction();
		assertEquals("name4", item.getName());
		item.setName("name5");
		assertEquals("name5", item.getName());
		commit();
		assertEquals(1, queryLong(count));
	}

	/**
	 * Needed additionally to {@link #testOverflow()} to test
	 * counter incrementation in {@link WrittenState#WrittenState(State)}.
	 */
	@Test void testOverflowWrittenState() throws SQLException
	{
		final String count = "select " + SI.update(TYPE) + " from " + SI.tab(TYPE);

		assertEquals("name0", item.getName());
		commit();
		assertEquals(0, queryLong(count));

		startTransaction();
		assertEquals("name0", item.getName());
		item.setName("name1");
		assertEquals("name1", item.getName());
		item.setName("name2");
		assertEquals("name2", item.getName());
		commit();
		assertEquals(2, queryLong(count));

		startTransaction();
		assertEquals("name2", item.getName());
		item.setName("name3");
		assertEquals("name3", item.getName());
		item.setName("name4");
		assertEquals("name4", item.getName());
		commit();
		assertEquals(4, queryLong(count));

		execute(
				"update " + SI.tab(TYPE) +
				" set " + SI.update(TYPE) + "=" + (MAX_VALUE-2));
		assertEquals(MAX_VALUE-2, queryLong(count));
		model.clearCache();

		startTransaction();
		assertEquals("name4", item.getName());
		item.setName("name5");
		assertEquals("name5", item.getName());
		item.setName("name6");
		assertEquals("name6", item.getName());
		commit();
		assertEquals(MAX_VALUE, queryLong(count));

		startTransaction();
		assertEquals("name6", item.getName());
		item.setName("name7");
		assertEquals("name7", item.getName());
		item.setName("name8");
		assertEquals("name8", item.getName());
		commit();
		assertEquals(1, queryLong(count));

		startTransaction();
		assertEquals("name8", item.getName());
		item.setName("name9");
		assertEquals("name9", item.getName());
		item.setName("name10");
		assertEquals("name10", item.getName());
		commit();
		assertEquals(3, queryLong(count));
	}

	private void execute(final String sql) throws SQLException
	{
		assertEquals(1, connection.executeUpdate(sql));
	}

	private long queryLong(final String sql) throws SQLException
	{
		try(ResultSet rs = connection.executeQuery(sql))
		{
			assertTrue(rs.next());
			final long result = rs.getLong(1);
			assertFalse(rs.next());
			return result;
		}
	}
}
