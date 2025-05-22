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

import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.SchemaMismatchConstraintTypeCheckUniqueTest.ItemA;
import com.exedio.cope.SchemaMismatchConstraintTypeCheckUniqueTest.ItemB;
import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import com.exedio.dsmf.UniqueConstraint;
import org.junit.jupiter.api.Test;

/**
 * @see SchemaMismatchConstraintTypeCheckUniqueTest
 */
public class SchemaMismatchConstraintTypeUniqueCheckTest extends SchemaMismatchTest
{
	public SchemaMismatchConstraintTypeUniqueCheckTest()
	{
		super(modelA, modelB);
	}

	@Test void testIt()
	{
		{
			final Schema schema = modelA.getVerifiedSchema();
			assertIt(null, OK, OK, schema);
			final Table table = schema.getTable(name(ItemA.TYPE));
			assertIt(null, OK, OK, table);
			final Constraint constraint = table.getConstraint("ItemAB_constraint_Unq");
			assertExistance(true, true, constraint);
			assertEquals(Constraint.Type.Unique, constraint.getType());
			assertEquals("("+q("fieldA")+","+q("fieldB")+")", constraint.getCondition());
			assertEquals("("+q("fieldA")+","+q("fieldB")+")", constraint.getRequiredCondition());
			assertEquals(null, constraint.getMismatchingCondition());
			assertEquals(null, constraint.getMismatchingConditionRaw());
			assertEquals(UniqueConstraint.class, constraint.getClass());
			assertIt(null, OK, OK, constraint);
		}

		assertEquals(name(ItemA.TYPE), name(ItemB.TYPE));

		final Schema schema = modelB.getVerifiedSchema();
		assertIt(null, OK, ERROR, schema);

		final Table table = schema.getTable(name(ItemA.TYPE));
		assertIt(null, OK, ERROR, table);

		final Constraint constraint = table.getConstraint("ItemAB_constraint_Unq");
		assertExistance(true, true, constraint);
		assertEquals(Constraint.Type.Check, constraint.getType());
		assertEquals(q("fieldA")+"<"+q("fieldB"), constraint.getCondition());
		assertEquals(q("fieldA")+"<"+q("fieldB"), constraint.getRequiredCondition());
		assertEquals(
				"("+q("fieldA")+","+q("fieldB")+")",
				constraint.getMismatchingCondition());
		assertEquals(
				"("+q("fieldA")+","+q("fieldB")+")",
				constraint.getMismatchingConditionRaw());
		assertEquals(CheckConstraint.class, constraint.getClass());
		assertIt(
				"unexpected condition >>>("+q("fieldA")+","+q("fieldB")+")<<<",
				ERROR, ERROR, constraint);

		// test propagation to cumulativeColor
		assertIt(null, OK, ERROR, table);
	}

	private static final Model modelA = SchemaMismatchConstraintTypeCheckUniqueTest.modelB;
	private static final Model modelB = SchemaMismatchConstraintTypeCheckUniqueTest.modelA;
}
