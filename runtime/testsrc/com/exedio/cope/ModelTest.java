package com.exedio.cope;

import java.util.EnumSet;
import java.util.Iterator;

import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.SQLRuntimeException;

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

	public void testGetVerifiedSchema() throws Exception
	{
		try
		{
			model.getVerifiedSchema();
		}
		catch (final IllegalStateException e)
		{
			assertEquals("must not be called within a transaction: CopeTest", e.getMessage());
		}
		model.rollback();
		assertNotNull(model.getVerifiedSchema());
		model.startTransaction();
	}

	public void testDeleteSchema() throws Exception
	{
		try
		{
			model.deleteSchema();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals("must not be called within a transaction: CopeTest", e.getMessage());
		}

		// close the transaction
		model.commit();

		// test for success
		model.deleteSchema();

		// repeat with success
		model.deleteSchema();

		// prepare CopeTest.tearDown
		model.startTransaction();
	}

	public void testDropSchema() throws Exception
	{
		// test with open transaction: fails
		try
		{
			model.dropSchema();
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals("must not be called within a transaction: CopeTest", e.getMessage());
		}

		model.commit();
		model.dropSchema();

		// test whether dropping did drop the tables
		try
		{
			model.deleteSchema();
		}
		catch (final SQLRuntimeException e)
		{
			assertEquals("Table 'gmvtest.ModelTestItem' doesn't exist", e.getCause().getMessage());
		}

		// try a second time
		try
		{
			model.dropSchema();
		}
		catch (final SQLRuntimeException e)
		{
			assertEquals("Unknown table 'ModelTestItem'", e.getCause().getMessage());
		}

		// prepare tearDown
		model.createSchema();
		model.startTransaction();
	}

	public void testDropSchemaConstraints() throws Exception
	{
		try
		{
			model.dropSchemaConstraints(null);
			fail();
		}
		catch (final IllegalStateException e)
		{
			assertEquals("must not be called within a transaction: CopeTest", e.getMessage());
		}

		model.rollback();
		model.dropSchemaConstraints(EnumSet.allOf(Constraint.Type.class));

		// prepare tearDown
		model.startTransaction();
	}
}
