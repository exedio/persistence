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

import static com.exedio.cope.SchemaSavepointTest.getSchemaSavepoint;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.DirectRevisionsFactory;
import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;

/**
 * @author baumgaertel
 */
public class ModelTest extends TestWithEnvironment
{
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ModelTestItem extends Item
	{
		@WrapperIgnore
		static final IntegerField next = new IntegerField().defaultToNext(5);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ModelTestItem> TYPE = com.exedio.cope.TypesBound.newType(ModelTestItem.class);

		@com.exedio.cope.instrument.Generated
		private ModelTestItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
 	}

	private static final Model model = Model.builder().
			add(DirectRevisionsFactory.make(new Revisions(0))). // needed for testing schema verification of table "while" via assertSchema
			add(ModelTestItem.TYPE).
			build();

	public ModelTest()
	{
		super(model);
	}

	@Test void testIt()
	{
		final String expectedText = "must not be called within a transaction: tx:com.exedio.cope.ModelTest for model " + model;
		assertFails(
				model::getVerifiedSchema,
				IllegalStateException.class,
				expectedText);

		final Schema schema = model.getSchema();
		assertFails(
				schema::drop,
				IllegalStateException.class,
				expectedText);
	assertFails(
			schema::create,
			IllegalStateException.class,
			expectedText);
		final com.exedio.dsmf.Table table = schema.getTables().iterator().next();
		assertFails(
				table::drop,
				IllegalStateException.class,
				expectedText);
		assertFails(
				table::create,
				IllegalStateException.class,
				expectedText);

		assertFails(
				model::deleteSchema,
				IllegalStateException.class,
				expectedText);

		assertFails(
				model::deleteSchemaForTest,
				IllegalStateException.class,
				expectedText);

		assertFails(
				model::createSchema,
				IllegalStateException.class,
				expectedText);

		assertFails(
				model::dropSchema,
				IllegalStateException.class,
				expectedText);

		assertFails(
				model::tearDownSchema,
				IllegalStateException.class,
				expectedText);

		assertFails(
				() -> model.createSchemaConstraints(EnumSet.allOf(Constraint.Type.class)),
				IllegalStateException.class,
				expectedText);

		assertFails(
				() -> model.dropSchemaConstraints(EnumSet.allOf(Constraint.Type.class)),
				IllegalStateException.class,
				expectedText);

		assertFails(
				() -> model.tearDownSchemaConstraints(EnumSet.allOf(Constraint.Type.class)),
				IllegalStateException.class,
				expectedText);

		assertFails(
				() -> model.purgeSchema(new AssertionErrorJobContext()),
				IllegalStateException.class,
				expectedText);

		assertFails(
				model::checkUnsupportedConstraints,
				IllegalStateException.class,
				expectedText);

		assertFails(
				model::revise,
				IllegalStateException.class,
				expectedText);
		assertFails(
				model::reviseIfSupportedAndAutoEnabled,
				IllegalStateException.class,
				expectedText);
		assertFails(
				model::getRevisionLogs,
				IllegalStateException.class,
				expectedText);
		assertFails(
				model::getRevisionLogsAndMutex,
				IllegalStateException.class,
				expectedText);

		//noinspection resource
		assertFails(
				() -> SchemaInfo.newConnection(model),
				IllegalStateException.class,
				expectedText);

		assertFails(
				ModelTestItem.TYPE::checkSequenceBehindPrimaryKey,
				IllegalStateException.class,
				expectedText);

		assertFails(
				ModelTestItem.next::checkSequenceBehindDefaultToNextX,
				IllegalStateException.class,
				expectedText);

		assertFails(
				() -> getSchemaSavepoint(model),
				IllegalStateException.class,
				expectedText);
		assertFails(
				model::getSchemaSavepointNew,
				IllegalStateException.class,
				expectedText);

		model.rollback();
		assertNotNull(model.getVerifiedSchema());
		model.startTransaction(ModelTest.class.getName());
	}

	@Test void testSchema()
	{
		assertSchema();
	}
}
