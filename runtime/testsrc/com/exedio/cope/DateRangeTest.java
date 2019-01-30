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
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Table;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DateRangeTest extends TestWithEnvironment
{
	@Test void testMinimum()
	{
		final MyItem item = new MyItem();
		final Date value = toDate(LocalDateTime.of(1600, 1, 1, 0, 0));
		assertEquals(value, DateField.getDefaultMinimum());
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
		final Date value = toDate(LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999000000));
		assertEquals(value, DateField.getDefaultMaximum());
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
		final Date value = toDate(LocalDateTime.of(1599, 12, 31, 23, 59, 59, 999000000));
		assertEquals(new Date(value.getTime()+1), MyItem.field.getMinimum());

		final DateRangeViolationException e = assertFails(
				() -> item.setField(value),
				DateRangeViolationException.class,
				"range violation on " + item + ", " +
				"1599-12-31 23:59:59.999 GMT is too small for MyItem.field, must be at least " +
				"1600-01-01 00:00:00.000.");
		assertEquals(item, e.getItem());
		assertEquals(MyItem.field, e.getFeature());
		assertEquals(value, e.getValue());
		assertEquals(true, e.isTooSmall());
		assertEquals(null, item.getField());
	}

	@Test void testMaximumExceeded()
	{
		final MyItem item = new MyItem();
		final Date value = toDate(LocalDateTime.of(10000, 1, 1, 0, 0, 0));
		assertEquals(new Date(value.getTime()-1), MyItem.field.getMaximum());

		final DateRangeViolationException e = assertFails(
				() -> item.setField(value),
				DateRangeViolationException.class,
				"range violation on " + item + ", " +
				"10000-01-01 00:00:00.000 GMT is too big for MyItem.field, must be at most" +
				" 9999-12-31 23:59:59.999.");
		assertEquals(item, e.getItem());
		assertEquals(MyItem.field, e.getFeature());
		assertEquals(value, e.getValue());
		assertEquals(false, e.isTooSmall());
		assertEquals(null, item.getField());
	}

	@Test void testSchema()
	{
		assertSchema();
	}


	@WrapperType(indent=2)
	static final class MyItem extends Item
	{
		static final DateField field = new DateField().optional();

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
		java.util.Date getField()
		{
			return MyItem.field.get(this);
		}

		/**
		 * Sets a new value for {@link #field}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		void setField(@javax.annotation.Nullable final java.util.Date field)
		{
			MyItem.field.set(this,field);
		}

		/**
		 * Sets the current date for the date field {@link #field}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
		void touchField()
		{
			MyItem.field.touch(this);
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

	DateRangeTest()
	{
		super(MODEL);
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	private static Date toDate(final LocalDateTime ldt)
	{
		return Date.from(ldt.toInstant(UTC));
	}
}
