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
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

public class SchemaMismatchTableNameTest extends SchemaMismatchTest
{
	public SchemaMismatchTableNameTest()
	{
		super(modelA, modelB);
	}

	@Test void testIt()
	{
		assertIt(null, OK, OK, modelA.getVerifiedSchema());

		final Schema schema = modelB.getVerifiedSchema();
		assertIt(null, OK, ERROR, schema);

		final Table tableA = schema.getTable(name(ItemA.TYPE));
		{
			final Column pkA, fdA;
			assertIt("not used", WARNING, WARNING, tableA);
			assertIt("not used", WARNING, WARNING, pkA = tableA.getColumn(name(ItemA.TYPE.getThis())));
			assertIt("not used", WARNING, WARNING, fdA = tableA.getColumn(name(ItemA.field)));
			assertEqualsUnmodifiable(asList(pkA, fdA), tableA.getColumns());
		}
		final Table tableB = schema.getTable(name(ItemB.TYPE));
		final Column pkBcolumn;
		{
			final Column pkB, fdB;
			assertIt("missing", ERROR, ERROR, tableB);
			assertIt("missing", ERROR, ERROR, pkB = tableB.getColumn(name(ItemB.TYPE.getThis())));
			assertIt("missing", ERROR, ERROR, fdB = tableB.getColumn(name(ItemB.field)));
			assertEqualsUnmodifiable(asList(pkB, fdB), tableB.getColumns());
			pkBcolumn = pkB;
		}
		{
			final Constraint pkA = tableA.getConstraint(namePk(ItemA.TYPE.getThis()));
			final Constraint pkB = tableB.getConstraint(namePk(ItemB.TYPE.getThis()));
			if(mysql) // TODO
			{
				assertNull(pkA);
			}
			else
			{
				assertIt("not used", WARNING, WARNING, PrimaryKey, pkA);
				assertTrue(pkA instanceof com.exedio.dsmf.PrimaryKeyConstraint);
				assertSame(null, pkA.getColumn()); // TODO should be pkAcolumn
			}
			assertIt("missing", ERROR, ERROR, PrimaryKey, pkB);
			assertTrue(pkB instanceof com.exedio.dsmf.PrimaryKeyConstraint);
			assertSame(pkBcolumn, pkB.getColumn());
		}

		if(model.getConnectProperties().primaryKeyGenerator.persistent)
		{
			if(mysql) // TODO fix
			{
				final Table seqA;
				final Sequence seqB;
				assertEquals(null, schema.getSequence(nameSeq(ItemA.TYPE.getThis())));
				assertIt("not used", WARNING, WARNING, seqA = schema.getTable   (nameSeq(ItemA.TYPE.getThis())));
				assertIt("missing",  ERROR,   ERROR,   seqB = schema.getSequence(nameSeq(ItemB.TYPE.getThis())));

				assertEqualsUnmodifiable(asList(tableB, tableA, seqA), schema.getTables());
				assertEqualsUnmodifiable(asList(seqB), schema.getSequences());
			}
			else
			{
				final Sequence seqA, seqB;
				assertIt("not used", WARNING, WARNING, seqA = schema.getSequence(nameSeq(ItemA.TYPE.getThis())));
				assertIt("missing",  ERROR,   ERROR,   seqB = schema.getSequence(nameSeq(ItemB.TYPE.getThis())));

				assertEqualsUnmodifiable(asList(tableB, tableA), schema.getTables());
				assertEqualsUnmodifiable(asList(seqB, seqA), schema.getSequences());
			}
		}
		else
		{
			assertEqualsUnmodifiable(asList(tableB, tableA), schema.getTables());
			assertEqualsUnmodifiable(asList(), schema.getSequences());
		}
	}

	static final class ItemA extends Item
	{
		static final IntegerField field = new IntegerField().toFinal(); // avoid update counter

	/**
	 * Creates a new ItemA with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemA(
				final int field)
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
	private ItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

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
