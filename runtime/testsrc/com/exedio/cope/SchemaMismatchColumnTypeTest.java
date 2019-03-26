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

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
		assertIt(null, OK, OK, modelA.getVerifiedSchema());

		assertEquals(name(ItemA.TYPE) , name(ItemB.TYPE ));
		assertEquals(name(ItemA.field), name(ItemB.field));

		final Schema schema = modelB.getVerifiedSchema();
		assertIt(null, OK, ERROR, schema);

		final Table table = schema.getTable(name(ItemA.TYPE));
		assertIt(null, OK, ERROR, table);

		final Column pk, field;
		assertIt(null, OK, OK, pk = table.getColumn(name(ItemA.TYPE.getThis())));
		assertIt(
				"different type in database: >" + type(ItemA.field) + "<",
				ERROR, ERROR, field = table.getColumn(name(ItemA.field)));

		assertEqualsUnmodifiable(asList(pk, field), table.getColumns());
		assertEqualsUnmodifiable(asList(table), schema.getTables());
	}

	@CopeName("ItemAB")
	static final class ItemA extends Item
	{
		static final StringField field = new StringField().toFinal(); // avoid update counter

	/**
	 * Creates a new ItemA with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @throws com.exedio.cope.MandatoryViolationException if field is null.
	 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemA(
				@javax.annotation.Nonnull final java.lang.String field)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemA.field.map(field),
		});
	}

	/**
	 * Creates a new ItemA and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ItemA(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getField()
	{
		return ItemA.field.get(this);
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
	private ItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	@CopeName("ItemAB")
	static final class ItemB extends Item
	{
		static final IntegerField field = new IntegerField().toFinal(); // avoid update counter

	/**
	 * Creates a new ItemB with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemB(
				final int field)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemB.field.map(field),
		});
	}

	/**
	 * Creates a new ItemB and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ItemB(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

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
	private ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	static final Model modelA = new Model(ItemA.TYPE);
	static final Model modelB = new Model(ItemB.TYPE);

}
