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

import java.sql.SQLException;
import org.junit.Test;

public class DeleteAfterUniqueViolationTest extends AbstractRuntimeModelTest
{
	private static final Model MODEL = new Model(DeleteAfterUniqueViolationItem.TYPE);

	public DeleteAfterUniqueViolationTest()
	{
		super(MODEL);
	}

	@Override
	protected boolean doesManageTransactions()
	{
		return false;
	}

	private boolean unq;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
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
				assertEquals(
						"Duplicate entry 'commit' for key 'DelAftUniVioIte_unStr_Unq'", // TODO MySQL specific
						e.getCause().getMessage());
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
				assertEquals(
						"Duplicate entry 'rollback' for key 'DelAftUniVioIte_unStr_Unq'", // TODO MySQL specific
						e.getCause().getMessage());
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
