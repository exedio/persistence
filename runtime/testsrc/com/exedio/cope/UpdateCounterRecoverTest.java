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
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.SI;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
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

	private void execute(final String sql) throws SQLException
	{
		assertEquals(1, connection.executeUpdate(sql));
	}
}
