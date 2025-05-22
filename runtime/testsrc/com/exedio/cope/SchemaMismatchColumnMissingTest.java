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
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.dsmf.Constraint.Type.Check;
import static com.exedio.dsmf.Constraint.Type.PrimaryKey;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static com.exedio.dsmf.Node.Color.WARNING;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.SchemaMismatchColumnUnusedTest.ItemA;
import com.exedio.cope.SchemaMismatchColumnUnusedTest.ItemB;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

public class SchemaMismatchColumnMissingTest extends SchemaMismatchTest
{
	public SchemaMismatchColumnMissingTest()
	{
		super(modelA, modelB);
	}

	@Test void testIt()
	{
		assertIt(null, OK, OK, modelA.getVerifiedSchema());

		assertEquals(name(ItemA.TYPE), name(ItemB.TYPE));

		final Schema schema = modelB.getVerifiedSchema();
		assertIt(null, OK, ERROR, schema);

		final Table table = schema.getTable(name(ItemA.TYPE));
		assertIt(null, OK, ERROR, table);

		final Column pk, field;
		{
			assertIt(null, OK, OK, pk = table.getColumn(name(ItemA.TYPE.getThis())));
			assertIt("missing", ERROR, ERROR, field = table.getColumn(name(ItemA.field)));
			assertExistance(true, false, field);
			assertEquals(type(ItemA.field), field.getType());
			assertEquals(type(ItemA.field), field.getRequiredType());
			assertFails(field::getExistingType, IllegalStateException.class, "not existing");
			assertEquals(null, field.getMismatchingType());
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
			assertIt(
					supported ? "missing" : "unsupported",
					supported ? WARNING : OK,
					supported ? WARNING : OK, Check,
					checkA);
			assertEquals(supported, checkA.isSupported());
			assertExistance(true, false, checkA);
			assertTrue(checkA  instanceof com.exedio.dsmf.CheckConstraint);

			assertTrue(pkPk       instanceof com.exedio.dsmf.PrimaryKeyConstraint);
			assertTrue(checkPkMin instanceof com.exedio.dsmf.CheckConstraint);
			assertTrue(checkPkMax instanceof com.exedio.dsmf.CheckConstraint);

			assertEqualsUnmodifiable(
					asList(pkPk, checkPkMin, checkPkMax, checkA),
					table.getConstraints());
			assertSame(pk, pkPk.getColumn());
			assertSame(pk, checkPkMin.getColumn());
			assertSame(pk, checkPkMax.getColumn());
			assertSame(field, checkA.getColumn());
		}

		assertEqualsUnmodifiable(withTrail(schema, table), schema.getTables());
	}

	static final Model modelA = SchemaMismatchColumnUnusedTest.modelB;
	static final Model modelB = SchemaMismatchColumnUnusedTest.modelA;
}
