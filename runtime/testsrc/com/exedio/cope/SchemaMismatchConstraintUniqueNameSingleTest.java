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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.dsmf.Constraint.Type.Unique;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

public class SchemaMismatchConstraintUniqueNameSingleTest extends SchemaMismatchTest
{
	public SchemaMismatchConstraintUniqueNameSingleTest()
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

		final Constraint uniqueA, uniqueB;
		assertIt("unused",   ERROR, ERROR, Unique, uniqueA = table.getConstraint(nameUnq(ItemA.fieldA)));
		assertIt("missing",  ERROR, ERROR, Unique, uniqueB = table.getConstraint(nameUnq(ItemB.fieldB)));

		assertTrue(uniqueA instanceof com.exedio.dsmf.UniqueConstraint);
		assertTrue(uniqueB instanceof com.exedio.dsmf.UniqueConstraint);

		assertSame(null, uniqueA.getColumn()); // TODO should be ItemA.fieldA
		assertSame(table.getColumn(getColumnName(ItemB.fieldB)), uniqueB.getColumn());
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ItemA extends Item
	{
		static final IntegerField fieldA = new IntegerField().unique().toFinal(); // avoid update counter

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getFieldA()
		{
			return ItemA.fieldA.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static ItemA forFieldA(final int fieldA)
		{
			return ItemA.fieldA.searchUnique(ItemA.class,fieldA);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static ItemA forFieldAStrict(final int fieldA)
				throws
					java.lang.IllegalArgumentException
		{
			return ItemA.fieldA.searchUniqueStrict(ItemA.class,fieldA);
		}

		@com.exedio.cope.instrument.Generated
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
		static final IntegerField fieldB = new IntegerField().unique().toFinal(); // avoid update counter

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getFieldB()
		{
			return ItemB.fieldB.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static ItemB forFieldB(final int fieldB)
		{
			return ItemB.fieldB.searchUnique(ItemB.class,fieldB);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static ItemB forFieldBStrict(final int fieldB)
				throws
					java.lang.IllegalArgumentException
		{
			return ItemB.fieldB.searchUniqueStrict(ItemB.class,fieldB);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemB> TYPE = com.exedio.cope.TypesBound.newType(ItemB.class,ItemB::new);

		@com.exedio.cope.instrument.Generated
		private ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model modelA = new Model(ItemA.TYPE);
	static final Model modelB = new Model(ItemB.TYPE);
}
