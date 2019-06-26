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
import static com.exedio.dsmf.Constraint.Type.Unique;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

public class SchemaMismatchConstraintUniqueClauseTest extends SchemaMismatchTest
{
	public SchemaMismatchConstraintUniqueClauseTest()
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

		final Constraint unique;
		assertIt(
				"different condition in database: " +
				"expected "  + "---(" + q("field2") + "," + q("field1") + ")---, " +
				"but was "   + "---(" + q("field1") + "," + q("field2") + ")---",
				ERROR, ERROR, Unique, unique = table.getConstraint(name(ItemA.unique)));

		assertTrue(unique instanceof com.exedio.dsmf.UniqueConstraint);

		// test propagation to cumulativeColor
		assertIt(null, OK, OK, table.getColumn(name(ItemA.field1)));
		assertIt(null, OK, OK, table.getColumn(name(ItemA.field1)));
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class ItemA extends Item
	{
		static final IntegerField field1 = new IntegerField().toFinal(); // avoid update counter
		static final IntegerField field2 = new IntegerField().toFinal(); // avoid update counter

		static final UniqueConstraint unique = UniqueConstraint.create(field1, field2);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		int getField1()
		{
			return ItemA.field1.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		int getField2()
		{
			return ItemA.field2.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static ItemA forUnique(final int field1,final int field2)
		{
			return ItemA.unique.search(ItemA.class,field1,field2);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static ItemA forUniqueStrict(final int field1,final int field2)
				throws
					java.lang.IllegalArgumentException
		{
			return ItemA.unique.searchStrict(ItemA.class,field1,field2);
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
		static final IntegerField field1 = new IntegerField().toFinal(); // avoid update counter
		static final IntegerField field2 = new IntegerField().toFinal(); // avoid update counter

		static final UniqueConstraint unique = UniqueConstraint.create(field2, field1);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		int getField1()
		{
			return ItemB.field1.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		int getField2()
		{
			return ItemB.field2.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static ItemB forUnique(final int field2,final int field1)
		{
			return ItemB.unique.search(ItemB.class,field2,field1);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static ItemB forUniqueStrict(final int field2,final int field1)
				throws
					java.lang.IllegalArgumentException
		{
			return ItemB.unique.searchStrict(ItemB.class,field2,field1);
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
