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
import static com.exedio.dsmf.Constraint.Type.ForeignKey;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

public class SchemaMismatchConstraintForeignClauseTest extends SchemaMismatchTest
{
	public SchemaMismatchConstraintForeignClauseTest()
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

		final Constraint fk;
		assertIt(
				"different condition in database: " +
				"expected "  + "---field->" + name(TargetB.TYPE) + "." + name(TargetB.TYPE.getThis()) + "---, " +
				"but was "   + "---field->" + name(TargetA.TYPE) + "." + name(TargetA.TYPE.getThis()) + "---",
				ERROR, ERROR, ForeignKey, fk = table.getConstraint(nameFk(ItemA.field)));

		assertTrue(fk instanceof com.exedio.dsmf.ForeignKeyConstraint);

		// test propagation to cumulativeColor
		assertIt(null, OK, ERROR, table.getColumn(name(ItemA.field)));
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class ItemA extends Item
	{
		static final ItemField<TargetA> field = ItemField.create(TargetA.class).toFinal(); // avoid update counter

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		TargetA getField()
		{
			return ItemA.field.get(this);
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
		static final ItemField<TargetB> field = ItemField.create(TargetB.class).toFinal(); // avoid update counter

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		TargetB getField()
		{
			return ItemB.field.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemB> TYPE = com.exedio.cope.TypesBound.newType(ItemB.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class TargetA extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<TargetA> TYPE = com.exedio.cope.TypesBound.newType(TargetA.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private TargetA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class TargetB extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<TargetB> TYPE = com.exedio.cope.TypesBound.newType(TargetB.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private TargetB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model modelA = new Model(ItemA.TYPE, TargetA.TYPE);
	static final Model modelB = new Model(ItemB.TYPE, TargetB.TYPE);
}
