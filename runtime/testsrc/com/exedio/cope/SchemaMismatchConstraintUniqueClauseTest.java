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

import static com.exedio.dsmf.Constraint.Type.Unique;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	static final class ItemA extends Item
	{
		static final IntegerField field1 = new IntegerField().toFinal(); // avoid update counter
		static final IntegerField field2 = new IntegerField().toFinal(); // avoid update counter

		static final UniqueConstraint unique = UniqueConstraint.create(field1, field2);

	/**
	 * Creates a new ItemA with all the fields initially needed.
	 * @param field1 the initial value for field {@link #field1}.
	 * @param field2 the initial value for field {@link #field2}.
	 * @throws com.exedio.cope.UniqueViolationException if field1, field2 is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemA(
				final int field1,
				final int field2)
			throws
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemA.field1.map(field1),
			ItemA.field2.map(field2),
		});
	}

	/**
	 * Creates a new ItemA and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ItemA(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getField1()
	{
		return ItemA.field1.getMandatory(this);
	}

	/**
	 * Returns the value of {@link #field2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getField2()
	{
		return ItemA.field2.getMandatory(this);
	}

	/**
	 * Finds a itemA by it's unique fields.
	 * @param field1 shall be equal to field {@link #field1}.
	 * @param field2 shall be equal to field {@link #field2}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
	@javax.annotation.Nullable
	static ItemA forUnique(final int field1,final int field2)
	{
		return ItemA.unique.search(ItemA.class,field1,field2);
	}

	/**
	 * Finds a itemA by its unique fields.
	 * @param field1 shall be equal to field {@link #field1}.
	 * @param field2 shall be equal to field {@link #field2}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finderStrict")
	@javax.annotation.Nonnull
	static ItemA forUniqueStrict(final int field1,final int field2)
			throws
				java.lang.IllegalArgumentException
	{
		return ItemA.unique.searchStrict(ItemA.class,field1,field2);
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
		static final IntegerField field1 = new IntegerField().toFinal(); // avoid update counter
		static final IntegerField field2 = new IntegerField().toFinal(); // avoid update counter

		static final UniqueConstraint unique = UniqueConstraint.create(field2, field1);

	/**
	 * Creates a new ItemB with all the fields initially needed.
	 * @param field1 the initial value for field {@link #field1}.
	 * @param field2 the initial value for field {@link #field2}.
	 * @throws com.exedio.cope.UniqueViolationException if field1, field2 is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemB(
				final int field1,
				final int field2)
			throws
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ItemB.field1.map(field1),
			ItemB.field2.map(field2),
		});
	}

	/**
	 * Creates a new ItemB and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ItemB(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getField1()
	{
		return ItemB.field1.getMandatory(this);
	}

	/**
	 * Returns the value of {@link #field2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	int getField2()
	{
		return ItemB.field2.getMandatory(this);
	}

	/**
	 * Finds a itemB by it's unique fields.
	 * @param field2 shall be equal to field {@link #field2}.
	 * @param field1 shall be equal to field {@link #field1}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
	@javax.annotation.Nullable
	static ItemB forUnique(final int field2,final int field1)
	{
		return ItemB.unique.search(ItemB.class,field2,field1);
	}

	/**
	 * Finds a itemB by its unique fields.
	 * @param field2 shall be equal to field {@link #field2}.
	 * @param field1 shall be equal to field {@link #field1}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finderStrict")
	@javax.annotation.Nonnull
	static ItemB forUniqueStrict(final int field2,final int field1)
			throws
				java.lang.IllegalArgumentException
	{
		return ItemB.unique.searchStrict(ItemB.class,field2,field1);
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
