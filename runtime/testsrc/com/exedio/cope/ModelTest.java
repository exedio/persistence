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

import java.util.EnumSet;

import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;

/**
 * @author baumgaertel
 */
public class ModelTest extends com.exedio.cope.junit.CopeTest
{
	static class ModelTestItem extends Item
	{
		private static final long serialVersionUID = 1l;

		private ModelTestItem(final ActivationParameters ap)
		{
			super(ap);
		}

		// no fields required

		public static final com.exedio.cope.Type<ModelTestItem> TYPE = com.exedio.cope.TypesBound.newType(ModelTestItem.class);
 	}

	static Model model = new Model(ModelTestItem.TYPE);

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
			model.dropSchema();
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
			model.checkUnsupportedConstraints();
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
