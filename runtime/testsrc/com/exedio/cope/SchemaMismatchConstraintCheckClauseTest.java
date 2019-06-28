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

import static com.exedio.cope.SchemaInfo.supportsCheckConstraints;
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
		assertIt(null, OK, OK, modelA.getVerifiedSchema());

		assertEquals(name(ItemA.TYPE), name(ItemB.TYPE));

		final boolean supported = supportsCheckConstraints(model);
		final Schema schema = modelB.getVerifiedSchema();
		assertIt(null, OK, supported ? ERROR : OK, schema);

		final Table table = schema.getTable(name(ItemA.TYPE));
		assertIt(null, OK, supported ? ERROR : OK, table);

		final Constraint check = table.getConstraint(nameCkMax(ItemA.field));
		if(supported)
		{
			String error =
					"different condition in database: " +
					"expected "  + "---" + q("field") + "<=88---, " +
					"but was "   + "---" + q("field") + "<=66---";

			if(postgresql)
				error +=
						" (originally ---"+ q("field") + " <= 66---)";

			assertIt(error, ERROR, ERROR, Check, check);

			// test propagation to cumulativeColor
			assertIt(null, OK, ERROR, table.getColumn(name(ItemA.field)));
		}
		else
		{
			assertIt("not supported", OK, OK, Check, check);
			// test propagation to cumulativeColor
			assertIt(null, OK, OK, table.getColumn(name(ItemA.field)));
		}

		assertTrue(check instanceof com.exedio.dsmf.CheckConstraint);
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class ItemA extends Item
	{
		static final IntegerField field = new IntegerField().range(0, 66).toFinal(); // avoid update counter

		@javax.annotation.Generated("com.exedio.cope.instrument")
		int getField()
		{
			return ItemA.field.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemA> TYPE = com.exedio.cope.TypesBound.newType(ItemA.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private ItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class ItemB extends Item
	{
		static final IntegerField field = new IntegerField().range(0, 88).toFinal(); // avoid update counter

		@javax.annotation.Generated("com.exedio.cope.instrument")
		int getField()
		{
			return ItemB.field.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemB> TYPE = com.exedio.cope.TypesBound.newType(ItemB.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model modelA = new Model(ItemA.TYPE);
	static final Model modelB = new Model(ItemB.TYPE);
}
