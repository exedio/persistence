/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.misc.DirectRevisionsFactory;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import java.util.EnumSet;

/**
 * @author baumgaertel
 */
public class ModelTest extends com.exedio.cope.junit.CopeTest
{
	static class ModelTestItem extends Item
	{
		private static final long serialVersionUID = 1l;

		private ModelTestItem(final ActivationParameters ap) { super(ap); }

		static final IntegerField next = new IntegerField().defaultToNext(5);

		static final Type<ModelTestItem> TYPE = TypesBound.newType(ModelTestItem.class);
 	}

	private static Model model = new Model(DirectRevisionsFactory.make(new Revisions(0)), ModelTestItem.TYPE);

	public ModelTest()
	{
		super(model);
	}

	public void testIt() throws Exception
	{
		final String expectedText = "must not be called within a transaction: CopeTest";
		try
		{
			model.getVerifiedSchema();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		final Schema schema = model.getSchema();
		try
		{
			schema.drop();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}
		try
		{
			schema.create();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}
		final com.exedio.dsmf.Table table = schema.getTables().iterator().next();
		try
		{
			table.drop();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}
		try
		{
			table.create();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.deleteSchema();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.deleteSchemaForTest();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.createSchema();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.dropSchema();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.tearDownSchema();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.createSchemaConstraints(EnumSet.allOf(Constraint.Type.class));
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.dropSchemaConstraints(EnumSet.allOf(Constraint.Type.class));
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.tearDownSchemaConstraints(EnumSet.allOf(Constraint.Type.class));
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.checkUnsupportedConstraints();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.revise();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}
		try
		{
			model.reviseIfSupportedAndAutoEnabled();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}
		try
		{
			model.getRevisionLogs();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}
		try
		{
			model.getRevisionLogsAndMutex();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			SchemaInfo.newConnection(model);
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			ModelTestItem.TYPE.checkPrimaryKey();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			ModelTestItem.next.checkDefaultToNext();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		try
		{
			model.getSchemaSavepoint();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals(expectedText, e.getMessage());
		}

		model.rollback();
		assertNotNull(model.getVerifiedSchema());
		model.startTransaction();
	}
}
