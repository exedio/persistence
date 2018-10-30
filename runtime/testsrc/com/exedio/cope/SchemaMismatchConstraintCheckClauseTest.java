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
import static com.exedio.dsmf.Constraint.Type.Check;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
			final String error;

			if(postgresql)
			{
				error =
						"different condition in database: " +
						"expected "  + "---" + q("field") + "<=88---, " +
						"but was "   + "---" + q("field") + "<=66--- " +
						"(originally "+"---" + q("field") + " <= 66---)";
			}
			else
			{
				error =
						"different condition in database: " +
						"expected "  + "---" + q("field") + "<=88---, " +
						"but was "   + "---" + q("field") + "<=66---";
			}
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
	static final class ItemA extends Item
	{
		static final IntegerField field = new IntegerField().range(0, 66).toFinal(); // avoid update counter

	/**
	 * Creates a new ItemA with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @throws com.exedio.cope.IntegerRangeViolationException if field violates its range constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemA(
				final int field)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemA.field.map(field),
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
	 * Returns the value of {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getField()
	{
		return ItemA.field.getMandatory(this);
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
		static final IntegerField field = new IntegerField().range(0, 88).toFinal(); // avoid update counter

	/**
	 * Creates a new ItemB with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @throws com.exedio.cope.IntegerRangeViolationException if field violates its range constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemB(
				final int field)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemB.field.map(field),
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
	 * Returns the value of {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getField()
	{
		return ItemB.field.getMandatory(this);
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
