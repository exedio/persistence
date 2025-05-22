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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.UsageEntryPoint;
import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import com.exedio.dsmf.UniqueConstraint;
import org.junit.jupiter.api.Test;

/**
 * @see SchemaMismatchConstraintTypeUniqueCheckTest
 */
public class SchemaMismatchConstraintTypeCheckUniqueTest extends SchemaMismatchTest
{
	public SchemaMismatchConstraintTypeCheckUniqueTest()
	{
		super(modelA, modelB);
	}

	@Test void testIt()
	{
		final boolean supportsCheck = SchemaInfo.supportsCheckConstraint(modelA);
		final String sao = switch(dialect) // space around operator
		{
			case hsqldb-> "";
			case mysql, postgresql-> " ";
		};
		{
			final Schema schema = modelA.getVerifiedSchema();
			assertIt(null, OK, OK, schema);
			final Table table = schema.getTable(name(ItemA.TYPE));
			assertIt(null, OK, OK, table);
			final Constraint constraint = table.getConstraint("ItemAB_constraint_Unq");
			assertExistance(true, supportsCheck, constraint);
			assertEquals(Constraint.Type.Check, constraint.getType());
			assertEquals(q("fieldA")+"<"+q("fieldB"), constraint.getCondition());
			assertEquals(q("fieldA")+"<"+q("fieldB"), constraint.getRequiredCondition());
			assertEquals(null, constraint.getMismatchingCondition());
			assertEquals(null, constraint.getMismatchingConditionRaw());
			assertEquals(CheckConstraint.class, constraint.getClass());
			assertIt(supportsCheck ? null : "unsupported", OK, OK, constraint);
		}

		assertEquals(name(ItemA.TYPE), name(ItemB.TYPE));

		final Schema schema = modelB.getVerifiedSchema();
		assertIt(null, OK, ERROR, schema);

		final Table table = schema.getTable(name(ItemA.TYPE));
		assertIt(null, OK, ERROR, table);

		final Constraint constraint = table.getConstraint("ItemAB_constraint_Unq");
		assertExistance(true, supportsCheck, constraint);
		assertEquals(Constraint.Type.Unique, constraint.getType());
		assertEquals("("+q("fieldA")+","+q("fieldB")+")", constraint.getCondition());
		assertEquals("("+q("fieldA")+","+q("fieldB")+")", constraint.getRequiredCondition());
		assertEquals(supportsCheck
				? q("fieldA")+"<"+q("fieldB")
				: null,
				constraint.getMismatchingCondition());
		assertEquals(supportsCheck
				? q("fieldA")+sao+"<"+sao+q("fieldB")
				: null,
				constraint.getMismatchingConditionRaw());
		assertEquals(UniqueConstraint.class, constraint.getClass());
		assertIt(
				supportsCheck
				? "unexpected condition >>>"+q("fieldA")+"<"+q("fieldB")+"<<<" + (!sao.isEmpty() ? " (originally >>>"+q("fieldA")+sao+"<"+sao+q("fieldB")+"<<<)" : "")
				: "missing",
				ERROR, ERROR, constraint);

		// test propagation to cumulativeColor
		assertIt(null, OK, ERROR, table);
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class ItemA extends Item
	{
		@WrapperIgnore static final IntegerField fieldA = new IntegerField().toFinal(); // avoid update counter
		@WrapperIgnore static final IntegerField fieldB = new IntegerField().toFinal(); // avoid update counter
		@CopeSchemaName("constraint_Unq") // needed because unique constraint always end with "_Unq"
		@UsageEntryPoint
		static final com.exedio.cope.CheckConstraint constraint = new com.exedio.cope.CheckConstraint(fieldA.less(fieldB));

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
		@WrapperIgnore static final IntegerField fieldA = new IntegerField().toFinal(); // avoid update counter
		@WrapperIgnore static final IntegerField fieldB = new IntegerField().toFinal(); // avoid update counter
		@UsageEntryPoint
		@WrapperIgnore static final com.exedio.cope.UniqueConstraint constraint = com.exedio.cope.UniqueConstraint.create(fieldA, fieldB);

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
