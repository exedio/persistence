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
import static com.exedio.dsmf.Constraint.Type.Check;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

/**
 * SchemaMismatchConstraintCheckNameTest is in {@link SchemaMismatchColumnNameTest}.
 */
public class SchemaMismatchConstraintCheckClauseTest extends SchemaMismatchTest
{
	public SchemaMismatchConstraintCheckClauseTest()
	{
		super(modelA, modelB);
	}

	@Test void testIt()
	{
		final boolean supported = supportsCheckConstraint(model);
		final String sao = switch(dialect) // space around operator
		{
			case hsqldb-> "";
			case mysql, postgresql-> " ";
		};
		{
			final Schema schema = modelA.getVerifiedSchema();
			assertIt(null, OK, OK, schema);

			final Constraint check = schema.getTable(name(ItemA.TYPE)).getConstraint(nameCkMax(ItemA.field));
			assertIt(supported ? null : "unsupported", OK, OK, Check, check);
			assertEquals(supported, check.isSupported());
			assertExistance(true, supported, check);
			assertEquals(q("field") + "<=66", check.getCondition());
			assertEquals(q("field") + "<=66", check.getRequiredCondition());
			assertEquals(null, check.getMismatchingCondition());
			assertEquals(null, check.getMismatchingConditionRaw());
		}

		assertEquals(name(ItemA.TYPE), name(ItemB.TYPE));

		final Schema schema = modelB.getVerifiedSchema();
		assertIt(null, OK, supported ? ERROR : OK, schema);

		final Table table = schema.getTable(name(ItemA.TYPE));
		assertIt(null, OK, supported ? ERROR : OK, table);

		final Constraint check = table.getConstraint(nameCkMax(ItemA.field));
		if(supported)
		{
			String error =
					"unexpected condition " +
					">>>" + q("field") + "<=66<<<";

			if(mysql || postgresql)
				error +=
						" (originally >>>"+ q("field") + " <= 66<<<)";

			assertIt(error, ERROR, ERROR, Check, check);
			assertEquals(true, check.isSupported());
			assertExistance(true, true, check);
			assertEquals(q("field") + "<=88", check.getCondition());
			assertEquals(q("field") + "<=88", check.getRequiredCondition());
			assertEquals(q("field") + "<=66", check.getMismatchingCondition());
			assertEquals(q("field") + sao+"<="+sao + "66", check.getMismatchingConditionRaw());

			// test propagation to cumulativeColor
			assertIt(null, OK, ERROR, table.getColumn(name(ItemA.field)));
		}
		else
		{
			assertIt("unsupported", OK, OK, Check, check);
			assertEquals(false, check.isSupported());
			assertExistance(true, false, check);
			assertEquals(q("field") + "<=88", check.getCondition());
			assertEquals(q("field") + "<=88", check.getRequiredCondition());
			assertEquals(null, check.getMismatchingCondition());
			assertEquals(null, check.getMismatchingConditionRaw());

			// test propagation to cumulativeColor
			assertIt(null, OK, OK, table.getColumn(name(ItemA.field)));
		}

		assertTrue(check instanceof com.exedio.dsmf.CheckConstraint);
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ItemA extends Item
	{
		static final IntegerField field = new IntegerField().range(0, 66).toFinal(); // avoid update counter

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getField()
		{
			return ItemA.field.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemA> TYPE = com.exedio.cope.TypesBound.newType(ItemA.class,ItemA::new);

		@com.exedio.cope.instrument.Generated
		private ItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ItemB extends Item
	{
		static final IntegerField field = new IntegerField().range(0, 88).toFinal(); // avoid update counter

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getField()
		{
			return ItemB.field.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemB> TYPE = com.exedio.cope.TypesBound.newType(ItemB.class,ItemB::new);

		@com.exedio.cope.instrument.Generated
		private ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model modelA = new Model(ItemA.TYPE);
	static final Model modelB = new Model(ItemB.TYPE);
}
