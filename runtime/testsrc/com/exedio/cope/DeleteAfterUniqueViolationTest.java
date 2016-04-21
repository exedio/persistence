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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;

public class DeleteAfterUniqueViolationTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(DeleteAfterUniqueViolationItem.TYPE);

	public DeleteAfterUniqueViolationTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private boolean unq;

	@Before public final void setUp()
	{
		unq = model.connect().executor.supportsUniqueViolation;
	}

	@Test public void testCommit()
	{
		model.startTransaction(getClass().getName());

		new DeleteAfterUniqueViolationItem("commit", 1.0);

		try
		{
			new DeleteAfterUniqueViolationItem("commit", 1.0);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertSame(DeleteAfterUniqueViolationItem.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			if(unq)
			{
				final String driver = model.getEnvironmentInfo().getDriverName();
				if(driver.startsWith("MySQL"))
				{
					assertEquals(
							"Duplicate entry 'commit' for key 'DelAftUniVioIte_unStr_Unq'",
							e.getCause().getMessage());
				}
				else if(driver.startsWith("MariaDB"))
				{
					assertEquals(
							"Duplicate entry 'commit' for key 'DelAftUniVioIte_unStr_Unq'\n" +
							"Query is : INSERT INTO `DeleteAfterUniquViolaItem`(`this`,`catch`,`uniqueString`,`name`)VALUES(1,0,'commit','commit')",
							e.getCause().getMessage());
				}
				else
				{
					fail(driver);
				}
				assertTrue(e.getCause() instanceof SQLException);
			}
			else
			{
				assertNull(e.getCause());
			}
		}

		model.commit();

		model.deleteSchema();

		model.startTransaction(getClass().getName()+"#checkEmptySchema");
		model.checkEmptySchema();
		model.commit();
	}

	@Test public void testRollback()
	{
		model.startTransaction(getClass().getName());

		new DeleteAfterUniqueViolationItem("rollback", 1.0);

		try
		{
			new DeleteAfterUniqueViolationItem("rollback", 1.0);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertSame(DeleteAfterUniqueViolationItem.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			if(unq)
			{
				final String driver = model.getEnvironmentInfo().getDriverName();
				if(driver.startsWith("MySQL"))
				{
					assertEquals(
							driver,
							"Duplicate entry 'rollback' for key 'DelAftUniVioIte_unStr_Unq'",
							e.getCause().getMessage());
				}
				else if(driver.startsWith("MariaDB"))
				{
					assertEquals(
							driver,
							"Duplicate entry 'rollback' for key 'DelAftUniVioIte_unStr_Unq'\n" +
							"Query is : INSERT INTO `DeleteAfterUniquViolaItem`(`this`,`catch`,`uniqueString`,`name`)VALUES(1,0,'rollback','rollback')",
							e.getCause().getMessage());
				}
				else
				{
					fail(driver);
				}
				assertTrue(e.getCause() instanceof SQLException);
			}
			else
			{
				assertNull(e.getCause());
			}
		}

		model.rollback();

		model.deleteSchema();

		model.startTransaction(getClass().getName()+"#checkEmptySchema");
		model.checkEmptySchema();
		model.commit();
	}
}
