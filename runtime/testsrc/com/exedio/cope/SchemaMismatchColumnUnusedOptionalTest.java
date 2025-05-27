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

import static com.exedio.cope.SchemaInfo.supportsCheckConstraint;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.dsmf.Constraint.Type.Check;
import static com.exedio.dsmf.Constraint.Type.PrimaryKey;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static com.exedio.dsmf.Node.Color.WARNING;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

/**
 * @see SchemaMismatchColumnUnusedTest
 */
public class SchemaMismatchColumnUnusedOptionalTest extends SchemaMismatchTest
{
	public SchemaMismatchColumnUnusedOptionalTest()
	{
		super(modelA, modelB);
	}

	@Test void testIt()
	{
		assertIt(null, OK, OK, modelA.getVerifiedSchema());

		assertEquals(name(ItemA.TYPE), name(ItemB.TYPE));

		final Schema schema = modelB.getVerifiedSchema();
		assertIt(null, OK, supportsCheckConstraint(model)?ERROR:WARNING, schema);

		final Table table = schema.getTable(name(ItemA.TYPE));
		assertIt(null, OK, supportsCheckConstraint(model)?ERROR:WARNING, table);

		final Column pk, field;
		{
			assertIt(null, OK, OK, pk = table.getColumn(name(ItemA.TYPE.getThis())));
			assertIt("unused", WARNING, WARNING, field = table.getColumn(name(ItemA.field)));
			assertExistance(false, true, field);
			assertEquals(type(ItemA.field), field.getType());
			assertFails(field::getRequiredType, IllegalStateException.class, "not required");
			assertEquals(type(ItemA.field), field.getExistingType());
			assertEquals(false, field.mismatchesType());

			assertEqualsUnmodifiable(asList(pk, field), table.getColumns());
		}

		// test check constraints as well
		{
			final boolean supported = supportsCheckConstraint(model);
			final Constraint pkPk, checkPkMin, checkPkMax;
			assertIt(null, OK, OK, PrimaryKey, pkPk = table.getConstraint("ItemAB_PK"));
			assertIt(
					supported ? null : "unsupported",
					OK, OK, Check, checkPkMin = table.getConstraint("ItemAB_this_MN"));
			assertIt(
					supported ? null : "unsupported",
					OK, OK, Check, checkPkMax = table.getConstraint("ItemAB_this_MX"));

			final Constraint checkA = table.getConstraint(nameCkEnum(ItemA.field));
			if(supported)
			{
				assertIt("unused", ERROR, ERROR, Check, checkA);
				assertFails(checkA::isSupported, IllegalStateException.class, "not required");
				assertExistance(false, true, checkA);
				assertTrue(checkA  instanceof com.exedio.dsmf.CheckConstraint);
			}
			else
			{
				assertNull(checkA);
			}

			assertTrue(pkPk       instanceof com.exedio.dsmf.PrimaryKeyConstraint);
			assertTrue(checkPkMin instanceof com.exedio.dsmf.CheckConstraint);
			assertTrue(checkPkMax instanceof com.exedio.dsmf.CheckConstraint);

			assertEqualsUnmodifiable(
					supported
					? asList(pkPk, checkPkMin, checkPkMax, checkA)
					: asList(pkPk, checkPkMin, checkPkMax),
					table.getConstraints());
			assertSame(pk, pkPk.getColumn());
			assertSame(pk, checkPkMin.getColumn());
			assertSame(pk, checkPkMax.getColumn());
			if(supported)
				assertSame(null, checkA.getColumn()); // TODO should be field
		}

		assertEqualsUnmodifiable(withTrail(schema, table), schema.getTables());
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class ItemA extends Item
	{
		static final BooleanField field = new BooleanField().optional().toFinal(); // avoid update counter

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Boolean getField()
		{
			return ItemA.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<ItemA> TYPE = com.exedio.cope.TypesBound.newType(ItemA.class,ItemA::new);

		@com.exedio.cope.instrument.Generated
		private ItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class ItemB extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<ItemB> TYPE = com.exedio.cope.TypesBound.newType(ItemB.class,ItemB::new);

		@com.exedio.cope.instrument.Generated
		private ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model modelA = new Model(ItemA.TYPE);
	static final Model modelB = new Model(ItemB.TYPE);
}
