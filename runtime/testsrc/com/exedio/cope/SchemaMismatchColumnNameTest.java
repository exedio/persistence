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
import static com.exedio.cope.junit.CopeAssert.assertEqualsUnmodifiable;
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

import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

public class SchemaMismatchColumnNameTest extends SchemaMismatchTest
{
	public SchemaMismatchColumnNameTest()
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

		final Column pk, fieldA, fieldB;
		{
			assertIt(null, OK, OK, pk = table.getColumn(name(ItemA.TYPE.getThis())));
			assertIt("not used", WARNING, WARNING, fieldA = table.getColumn(name(ItemA.fieldA)));
			assertIt("missing",  ERROR,   ERROR,   fieldB = table.getColumn(name(ItemB.fieldB)));

			assertEqualsUnmodifiable(asList(pk, fieldB, fieldA), table.getColumns());
		}

		// test check constraints as well
		{
			final boolean supported = supportsCheckConstraints(model);
			final Constraint pkPk, checkPkMin, checkPkMax;
			assertIt(null, OK, OK, PrimaryKey, pkPk = table.getConstraint("ItemAB_PK"));
			assertIt(
					supported ? null : "not supported",
					OK, OK, Check, checkPkMin = table.getConstraint("ItemAB_this_MN"));
			assertIt(
					supported ? null : "not supported",
					OK, OK, Check, checkPkMax = table.getConstraint("ItemAB_this_MX"));

			final Constraint checkA = table.getConstraint(nameCkEnum(ItemA.fieldA));
			final Constraint checkB = table.getConstraint(nameCkEnum(ItemB.fieldB));
			if(supported)
			{
				assertIt("not used", ERROR, ERROR, Check, checkA);
				assertIt("missing",  ERROR, ERROR, Check, checkB);
				assertTrue(checkA  instanceof com.exedio.dsmf.CheckConstraint);
			}
			else
			{
				assertNull(checkA);
				assertIt("not supported",  OK, OK, Check, checkB);
			}

			assertTrue(pkPk    instanceof com.exedio.dsmf.PrimaryKeyConstraint);
			assertTrue(checkPkMin instanceof com.exedio.dsmf.CheckConstraint);
			assertTrue(checkPkMax instanceof com.exedio.dsmf.CheckConstraint);
			assertTrue(checkB  instanceof com.exedio.dsmf.CheckConstraint);

			assertEqualsUnmodifiable(
					supported
					? asList(pkPk, checkPkMin, checkPkMax, checkB, checkA)
					: asList(pkPk, checkPkMin, checkPkMax, checkB),
					table.getConstraints());
			assertSame(pk, pkPk.getColumn());
			assertSame(pk, checkPkMin.getColumn());
			assertSame(pk, checkPkMax.getColumn());
			if(supported)
				assertSame(null, checkA.getColumn()); // TODO should be fieldA
			assertSame(fieldB, checkB.getColumn());
		}

		assertEqualsUnmodifiable(asList(table), schema.getTables());
	}

	@CopeName("ItemAB")
	static final class ItemA extends Item
	{
		static final BooleanField fieldA = new BooleanField().toFinal(); // avoid update counter

	/**
	 * Creates a new ItemA with all the fields initially needed.
	 * @param fieldA the initial value for field {@link #fieldA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemA(
				final boolean fieldA)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemA.fieldA.map(fieldA),
		});
	}

	/**
	 * Creates a new ItemA and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ItemA(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #fieldA}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean getFieldA()
	{
		return ItemA.fieldA.getMandatory(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for itemA.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ItemA> TYPE = com.exedio.cope.TypesBound.newType(ItemA.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	@CopeName("ItemAB")
	static final class ItemB extends Item
	{
		static final BooleanField fieldB = new BooleanField().toFinal(); // avoid update counter

	/**
	 * Creates a new ItemB with all the fields initially needed.
	 * @param fieldB the initial value for field {@link #fieldB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemB(
				final boolean fieldB)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemB.fieldB.map(fieldB),
		});
	}

	/**
	 * Creates a new ItemB and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ItemB(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #fieldB}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	boolean getFieldB()
	{
		return ItemB.fieldB.getMandatory(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for itemB.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ItemB> TYPE = com.exedio.cope.TypesBound.newType(ItemB.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	static final Model modelA = new Model(ItemA.TYPE);
	static final Model modelB = new Model(ItemB.TYPE);

}
