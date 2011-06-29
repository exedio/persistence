/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.sql.SQLException;

import com.exedio.dsmf.SQLRuntimeException;

public class DeleteAfterUniqueViolationTest extends AbstractRuntimeTest
{
	public DeleteAfterUniqueViolationTest()
	{
		super(CacheIsolationTest.MODEL);
		skipTransactionManagement();
	}

	boolean unq;
	boolean cluster;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		unq = model.connect().executor.supportsUniqueViolation;
		cluster = model.connect().properties.cluster.booleanValue();
	}

	public void testCommit()
	{
		model.startTransaction(getClass().getName());

		deleteOnTearDown(new CacheIsolationItem("commit", 1.0));

		try
		{
			new CacheIsolationItem("commit", 1.0);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertSame(CacheIsolationItem.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			if(unq)
			{
				assertEquals(
						"Duplicate entry 'commit' for key 'CacheIsolItem_uniStri_Unq'", // TODO MySQL specific
						e.getCause().getMessage());
				assertTrue(e.getCause() instanceof SQLException);
			}
			else
			{
				assertNull(e.getCause());
			}
		}

		model.commit();

		try
		{
			model.deleteSchema();
			assertTrue(!unq || cluster);
		}
		catch(final SQLRuntimeException e)
		{
			assertEquals(
					"set FOREIGN_KEY_CHECKS=0;truncate `CacheIsolationItem`;set FOREIGN_KEY_CHECKS=1;", // TODO MySQL specific
					e.getMessage());
			assertEquals(
					"Can't execute the given command because you have active locked tables or an active transaction", // TODO MySQL specific
					e.getCause().getMessage());
			assertTrue(e.getCause() instanceof SQLException);
			assertTrue(unq);
		}
	}

	public void testRollback()
	{
		model.startTransaction(getClass().getName());

		deleteOnTearDown(new CacheIsolationItem("rollback", 1.0));

		try
		{
			new CacheIsolationItem("rollback", 1.0);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertSame(CacheIsolationItem.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			if(unq)
			{
				assertEquals(
						"Duplicate entry 'rollback' for key 'CacheIsolItem_uniStri_Unq'", // TODO MySQL specific
						e.getCause().getMessage());
				assertTrue(e.getCause() instanceof SQLException);
			}
			else
			{
				assertNull(e.getCause());
			}
		}

		model.rollback();

		try
		{
			model.deleteSchema();
			assertFalse(unq);
		}
		catch(final SQLRuntimeException e)
		{
			assertEquals(
					cluster
					? "set FOREIGN_KEY_CHECKS=0;truncate `CacheIsolationItem`;set FOREIGN_KEY_CHECKS=1;truncate `CacheIsolatiItem_this_Seq`;" // TODO MySQL specific
					: "set FOREIGN_KEY_CHECKS=0;truncate `CacheIsolationItem`;set FOREIGN_KEY_CHECKS=1;", // TODO MySQL specific
					e.getMessage());
			assertEquals(
					"Can't execute the given command because you have active locked tables or an active transaction", // TODO MySQL specific
					e.getCause().getMessage());
			assertTrue(e.getCause() instanceof SQLException);
			assertTrue(unq);
		}
	}
}
