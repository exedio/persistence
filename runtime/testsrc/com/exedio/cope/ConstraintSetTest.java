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

import static com.exedio.cope.ConstraintSetTest.AnItem.TYPE;
import static com.exedio.cope.ConstraintSetTest.AnItem.field;
import static com.exedio.cope.ConstraintSetTest.AnItem.item;
import static com.exedio.cope.ConstraintSetTest.AnItem.uniqueA;
import static com.exedio.cope.ConstraintSetTest.AnItem.uniqueB;
import static com.exedio.cope.ConstraintSetTest.AnItem.uniqueSingle;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.getUpdateCounterColumnName;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.Column;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ConstraintSetTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE, ASubItem.TYPE);

	public ConstraintSetTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test void test()
	{
		final Table table = model.getSchema().getTable(getTableName(TYPE));

		assertEquals(asList(
				"AnItem_PK", "AnItem_this_MN", "AnItem_this_MX",
				"AnItem_class_EN",
				"AnItem_catch_MN", "AnItem_catch_MX",
				"AnItem_field_EN",
				"AnItem_item_MN", "AnItem_item_MX", "AnItem_item_Fk",
				"AnItem_itemType_EN", "AnItem_itemType_NS",
				"AnItem_uniqueSingle_EN",
				"AnItem_uniqueA_EN", "AnItem_uniqueB_EN",
				"AnItem_uniqueSingle_Unq",
				"AnItem_uniqueDouble_Unq"),
				names(table.getConstraints()));

		assertColumn(table, getPrimaryKeyColumnName(TYPE), "AnItem_PK", "AnItem_this_MN", "AnItem_this_MX");
		assertColumn(table, getTypeColumnName(TYPE), "AnItem_class_EN");
		assertColumn(table, getUpdateCounterColumnName(TYPE), "AnItem_catch_MN", "AnItem_catch_MX");
		assertColumn(table, getColumnName(field), "AnItem_field_EN");
		assertColumn(table, getColumnName(item), "AnItem_item_MN", "AnItem_item_MX", "AnItem_item_Fk");
		assertColumn(table, getTypeColumnName(item), "AnItem_itemType_EN", "AnItem_itemType_NS");
		assertColumn(table, getColumnName(uniqueSingle), "AnItem_uniqueSingle_EN", "AnItem_uniqueSingle_Unq");
		assertColumn(table, getColumnName(uniqueA), "AnItem_uniqueA_EN");
		assertColumn(table, getColumnName(uniqueB), "AnItem_uniqueB_EN");
		assertColumn(table, null, "AnItem_uniqueDouble_Unq");

		assertEquals(asList(
				"AnItem_PK", "AnItem_this_MN", "AnItem_this_MX"),
				names(table.getColumn(getPrimaryKeyColumnName(TYPE)).getConstraints()));
		assertEquals(asList(
				"AnItem_class_EN"),
				names(table.getColumn(getTypeColumnName(TYPE)).getConstraints()));
		assertEquals(asList(
				"AnItem_catch_MN", "AnItem_catch_MX"),
				names(table.getColumn(getUpdateCounterColumnName(TYPE)).getConstraints()));
		assertEquals(asList(
				"AnItem_field_EN"),
				names(table.getColumn(getColumnName(field)).getConstraints()));
		assertEquals(asList(
				"AnItem_item_MN", "AnItem_item_MX", "AnItem_item_Fk"),
				names(table.getColumn(getColumnName(item)).getConstraints()));
		assertEquals(asList(
				"AnItem_itemType_EN", "AnItem_itemType_NS"),
				names(table.getColumn(getTypeColumnName(item)).getConstraints()));
		assertEquals(asList(
				"AnItem_uniqueSingle_EN", "AnItem_uniqueSingle_Unq"),
				names(table.getColumn(getColumnName(uniqueSingle)).getConstraints()));
		assertEquals(asList(
				"AnItem_uniqueA_EN"),
				names(table.getColumn(getColumnName(uniqueA)).getConstraints()));
		assertEquals(asList(
				"AnItem_uniqueB_EN"),
				names(table.getColumn(getColumnName(uniqueB)).getConstraints()));
		assertEquals(asList(
				"AnItem_uniqueDouble_Unq"),
				names(table.getTableConstraints()));
	}

	private static List<String> names(final Collection<Constraint> constraints)
	{
		final ArrayList<String> result = new ArrayList<>(constraints.size());
		for(final Constraint c : constraints)
			result.add(c.getName());
		return result;
	}

	private static void assertColumn(
			final Table table,
			final String columnName,
			final String... constraintNames)
	{
		final Column column;
		if(columnName!=null)
		{
			column = table.getColumn(columnName);
			assertNotNull(column);
		}
		else
		{
			column = null;
		}
		for(final String constraintName : constraintNames)
		{
			assertNotNull(constraintName);
			final Constraint constraint = table.getConstraint(constraintName);
			assertNotNull(constraint);
			assertSame(column, constraint.getColumn());
		}
	}

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, indent=2) // TODO use import, but this is not accepted by javac
	static class AnItem extends Item
	{
		static final BooleanField field = new BooleanField().optional();

		static final ItemField<AnItem> item = ItemField.create(AnItem.class).optional();

		static final BooleanField uniqueSingle = new BooleanField().unique().optional();

		static final BooleanField uniqueA = new BooleanField().optional();
		static final BooleanField uniqueB = new BooleanField().optional();
		static final UniqueConstraint uniqueDouble = new UniqueConstraint(uniqueA, uniqueB);

		/**
		 * Creates a new AnItem and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		protected AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #field}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final java.lang.Boolean getField()
		{
			return AnItem.field.get(this);
		}

		/**
		 * Sets a new value for {@link #field}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setField(@javax.annotation.Nullable final java.lang.Boolean field)
		{
			AnItem.field.set(this,field);
		}

		/**
		 * Returns the value of {@link #item}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final AnItem getItem()
		{
			return AnItem.item.get(this);
		}

		/**
		 * Sets a new value for {@link #item}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setItem(@javax.annotation.Nullable final AnItem item)
		{
			AnItem.item.set(this,item);
		}

		/**
		 * Returns the value of {@link #uniqueSingle}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final java.lang.Boolean getUniqueSingle()
		{
			return AnItem.uniqueSingle.get(this);
		}

		/**
		 * Sets a new value for {@link #uniqueSingle}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setUniqueSingle(@javax.annotation.Nullable final java.lang.Boolean uniqueSingle)
				throws
					com.exedio.cope.UniqueViolationException
		{
			AnItem.uniqueSingle.set(this,uniqueSingle);
		}

		/**
		 * Finds a anItem by it's {@link #uniqueSingle}.
		 * @param uniqueSingle shall be equal to field {@link #uniqueSingle}.
		 * @return null if there is no matching item.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
		@javax.annotation.Nullable
		static final AnItem forUniqueSingle(@javax.annotation.Nonnull final java.lang.Boolean uniqueSingle)
		{
			return AnItem.uniqueSingle.searchUnique(AnItem.class,uniqueSingle);
		}

		/**
		 * Finds a anItem by its {@link #uniqueSingle}.
		 * @param uniqueSingle shall be equal to field {@link #uniqueSingle}.
		 * @throws java.lang.IllegalArgumentException if there is no matching item.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forStrict")
		@javax.annotation.Nonnull
		static final AnItem forUniqueSingleStrict(@javax.annotation.Nonnull final java.lang.Boolean uniqueSingle)
				throws
					java.lang.IllegalArgumentException
		{
			return AnItem.uniqueSingle.searchUniqueStrict(AnItem.class,uniqueSingle);
		}

		/**
		 * Returns the value of {@link #uniqueA}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final java.lang.Boolean getUniqueA()
		{
			return AnItem.uniqueA.get(this);
		}

		/**
		 * Sets a new value for {@link #uniqueA}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setUniqueA(@javax.annotation.Nullable final java.lang.Boolean uniqueA)
				throws
					com.exedio.cope.UniqueViolationException
		{
			AnItem.uniqueA.set(this,uniqueA);
		}

		/**
		 * Returns the value of {@link #uniqueB}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final java.lang.Boolean getUniqueB()
		{
			return AnItem.uniqueB.get(this);
		}

		/**
		 * Sets a new value for {@link #uniqueB}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setUniqueB(@javax.annotation.Nullable final java.lang.Boolean uniqueB)
				throws
					com.exedio.cope.UniqueViolationException
		{
			AnItem.uniqueB.set(this,uniqueB);
		}

		/**
		 * Finds a anItem by it's unique fields.
		 * @param uniqueA shall be equal to field {@link #uniqueA}.
		 * @param uniqueB shall be equal to field {@link #uniqueB}.
		 * @return null if there is no matching item.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
		@javax.annotation.Nullable
		static final AnItem forUniqueDouble(@javax.annotation.Nonnull final java.lang.Boolean uniqueA,@javax.annotation.Nonnull final java.lang.Boolean uniqueB)
		{
			return AnItem.uniqueDouble.search(AnItem.class,uniqueA,uniqueB);
		}

		/**
		 * Finds a anItem by its unique fields.
		 * @param uniqueA shall be equal to field {@link #uniqueA}.
		 * @param uniqueB shall be equal to field {@link #uniqueB}.
		 * @throws java.lang.IllegalArgumentException if there is no matching item.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finderStrict")
		@javax.annotation.Nonnull
		static final AnItem forUniqueDoubleStrict(@javax.annotation.Nonnull final java.lang.Boolean uniqueA,@javax.annotation.Nonnull final java.lang.Boolean uniqueB)
				throws
					java.lang.IllegalArgumentException
		{
			return AnItem.uniqueDouble.searchStrict(AnItem.class,uniqueA,uniqueB);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for anItem.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	@WrapperType(constructor=NONE, indent=2)
	static final class ASubItem extends AnItem
	{


		/**
		 * Creates a new ASubItem and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private ASubItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for aSubItem.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<ASubItem> TYPE = com.exedio.cope.TypesBound.newType(ASubItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private ASubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}
