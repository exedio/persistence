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
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

public class SchemaMismatchColumnTypeTest extends SchemaMismatchTest
{
	public SchemaMismatchColumnTypeTest()
	{
		super(modelA, modelB);
	}

	@Test void testIt()
	{
		{
			final Schema schema = modelA.getVerifiedSchema();
			assertIt(null, OK, OK, schema);
			final Column field = schema.getTable(name(ItemA.TYPE)).getColumn(name(ItemA.field));
			assertIt(null, OK, OK, field);
			assertExistance(true, true, field);
			assertEquals(type(ItemA.field), field.getType());
			assertEquals(type(ItemA.field), field.getRequiredType());
			assertEquals(type(ItemA.field), field.getExistingType());
			assertEquals(null, field.getMismatchingType());
			assertEquals(false, field.mismatchesType());
			assertFails(field::toleratesInsertIfUnused, IllegalStateException.class, "supported for unused columns only");
		}

		assertEquals(name(ItemA.TYPE) , name(ItemB.TYPE ));
		assertEquals(name(ItemA.field), name(ItemB.field));

		final Schema schema = modelB.getVerifiedSchema();
		assertIt(null, OK, ERROR, schema);

		final Table table = schema.getTable(name(ItemA.TYPE));
		assertIt(null, OK, ERROR, table);

		final Column pk, field;
		assertIt(null, OK, OK, pk = table.getColumn(name(ItemA.TYPE.getThis())));
		assertIt(
				"unexpected type >" + type(ItemA.field) + "<",
				ERROR, ERROR, field = table.getColumn(name(ItemA.field)));
		assertEquals(type(ItemB.field), field.getType());
		assertEquals(type(ItemB.field), field.getRequiredType());
		assertEquals(type(ItemA.field), field.getExistingType());
		assertEquals(type(ItemA.field), field.getMismatchingType());
		assertEquals(true, field.mismatchesType());
		assertFails(field::toleratesInsertIfUnused, IllegalStateException.class, "supported for unused columns only");

		assertEqualsUnmodifiable(asList(pk, field), table.getColumns());
		assertEqualsUnmodifiable(withTrail(schema, table), schema.getTables());
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ItemA extends Item
	{
		static final StringField field = new StringField().toFinal(); // avoid update counter

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getField()
		{
			return ItemA.field.get(this);
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
		static final IntegerField field = new IntegerField().toFinal(); // avoid update counter

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
