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

import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Day;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DayRangeTest extends TestWithEnvironment
{
	@Test void testMinimum()
	{
		final MyItem item = new MyItem();
		final Day value = new Day(1600, 1, 1);
		assertEquals(value, DayField.getDefaultMinimum());
		assertEquals(value, MyItem.field.getMinimum());

		item.setField(value);
		assertEquals(value, item.getField());
		assertEquals(asList(value), new Query<>(MyItem.field).search());

		restartTransaction();
		assertEquals(value, item.getField());
		assertEquals(asList(value), new Query<>(MyItem.field).search());

		model.commit();
		assertConstraints();
	}

	@Test void testMaximum()
	{
		final MyItem item = new MyItem();
		final Day value = new Day(9999, 12, 31);
		assertEquals(value, DayField.getDefaultMaximum());
		assertEquals(value, MyItem.field.getMaximum());

		item.setField(value);
		assertEquals(value, item.getField());
		assertEquals(asList(value), new Query<>(MyItem.field).search());

		restartTransaction();
		assertEquals(value, item.getField());
		assertEquals(asList(value), new Query<>(MyItem.field).search());

		model.commit();
		assertConstraints();
	}

	private void assertConstraints()
	{
		final Table tab = model.getSchema().getTable(getTableName(MyItem.TYPE));
		final Constraint min = tab.getConstraint("MyItem_field_MN");
		final Constraint max = tab.getConstraint("MyItem_field_MX");
		assertNotNull(min);
		assertNotNull(max);
		assertEquals(0, min.checkL());
		assertEquals(0, max.checkL());
	}

	@Test void testMinimumExceeded()
	{
		final MyItem item = new MyItem();
		final Day value = new Day(1599, 12, 31);
		assertEquals(value.plusDays(1), MyItem.field.getMinimum());

		final DayRangeViolationException e = assertFails(
				() -> item.setField(value),
				DayRangeViolationException.class,
				"range violation on " + item + ", " +
				"1599/12/31 is too small for MyItem.field, must be at least " +
				"1600/1/1.");
		assertEquals(item, e.getItem());
		assertEquals(MyItem.field, e.getFeature());
		assertEquals(value, e.getValue());
		assertEquals(true, e.isTooSmall());
		assertEquals(null, item.getField());
	}

	@Test void testSchema()
	{
		assertSchema();
	}


	@WrapperType(indent=2)
	static final class MyItem extends Item
	{
		static final DayField field = new DayField().optional();

		/**
		 * Creates a new MyItem with all the fields initially needed.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		MyItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		/**
		 * Creates a new MyItem and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #field}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		com.exedio.cope.util.Day getField()
		{
			return MyItem.field.get(this);
		}

		/**
		 * Sets a new value for {@link #field}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		void setField(@javax.annotation.Nullable final com.exedio.cope.util.Day field)
		{
			MyItem.field.set(this,field);
		}

		/**
		 * Sets today for the date field {@link #field}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
		void touchField(@javax.annotation.Nonnull final java.util.TimeZone zone)
		{
			MyItem.field.touch(this,zone);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for myItem.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	DayRangeTest()
	{
		super(MODEL);
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}
