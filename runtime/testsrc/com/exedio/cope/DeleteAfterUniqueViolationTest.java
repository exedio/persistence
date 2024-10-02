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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class DeleteAfterUniqueViolationTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(DeleteAfterUniqueViolationItem.TYPE);

	public DeleteAfterUniqueViolationTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private boolean unq;
	private String unqPrefix;

	@BeforeEach final void setUp()
	{
		unq = model.connect().supportsUniqueViolation;
		unqPrefix = (unq && mysql && atLeastMysql8()) ? "Main." : "";
	}

	@Test void testCommit()
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
			assertSame(DeleteAfterUniqueViolationItem.uniqueString, e.getFeatureForDescription());
			if(unq)
			{
				switch(dialect)
				{
					case hsqldb ->
						assertEquals(
								"integrity constraint violation: unique constraint or index violation ; " +
								"\"Main_uniqueString_Unq\" table: \"Main\"",
								e.getCause().getMessage());
					case mysql ->
						assertEquals(
								"Duplicate entry 'commit' for key '" + unqPrefix + "Main_uniqueString_Unq'",
								dropMariaConnectionId(e.getCause().getMessage()));
					case postgresql ->
						throw new AssertionFailedError(dialect.name(), e);
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

	@Test void testRollback()
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
			assertSame(DeleteAfterUniqueViolationItem.uniqueString, e.getFeatureForDescription());
			if(unq)
			{
				switch(dialect)
				{
					case hsqldb ->
						assertEquals(
								"integrity constraint violation: unique constraint or index violation ; " +
								"\"Main_uniqueString_Unq\" table: \"Main\"",
								e.getCause().getMessage());
					case mysql ->
						assertEquals(
								"Duplicate entry 'rollback' for key '" + unqPrefix + "Main_uniqueString_Unq'",
								dropMariaConnectionId(e.getCause().getMessage()));
					case postgresql ->
						throw new AssertionFailedError(dialect.name(), e);
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
