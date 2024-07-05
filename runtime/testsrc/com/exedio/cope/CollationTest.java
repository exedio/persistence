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
import static com.exedio.cope.instrument.Wrapper.ALL_WRAPS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CollationTest extends TestWithEnvironment
{
	@Test void test()
	{
		assertIt(
				"vAw",
				"vBw",
				"vCw");
	}
	@Test void testCase()
	{
		assertIt(
				"vAw",
				"vBw",
				"vaw");
	}
	@Test void testUmlautUpper()
	{
		assertIt(
				"vAw",
				"vBw",
				"v\u00c4w"); // Auml - A Umlaut
	}
	@Test void testUmlautLower()
	{
		assertIt(
				"vow",
				"vpw",
				"v\u00f6w"); // ouml - o Umlaut
	}

	private static void assertIt(final String... values)
	{
		final List<String> valueList = List.of(values);
		{
			final List<String> l = new ArrayList<>(valueList);
			Collections.sort(l);
			assertEquals(valueList, l, "not already sorted");
		}
		final List<String> valueListReverse;
		{
			final List<String> l = new ArrayList<>(valueList);
			Collections.reverse(l);
			valueListReverse = List.copyOf(l);
		}

		assertEquals(3, values.length);
		final String value0 = values[0];
		final String value1 = values[1];
		final String value2 = values[2];

		new AnItem(value0, value0, value1, value2);
		new AnItem(value1, value0, value1, value2);
		new AnItem(value2, value0, value1, value2);

		{
			final Query<String> q = new Query<>(AnItem.field);
			q.setOrderBy(AnItem.field, true);
			assertEquals(valueList, q.search());
			q.setOrderBy(AnItem.field, false);
			assertEquals(valueListReverse, q.search());
		}

		assertEquals(List.of(              ), search(AnItem.field.less(value0)));
		assertEquals(List.of(              ), search(AnItem.field.less(AnItem.compare0)));
		assertEquals(List.of(value0        ), search(AnItem.field.less(value1)));
		assertEquals(List.of(value0        ), search(AnItem.field.less(AnItem.compare1)));
		assertEquals(List.of(value0, value1), search(AnItem.field.less(value2)));
		assertEquals(List.of(value0, value1), search(AnItem.field.less(AnItem.compare2)));

		assertEquals(List.of(value1, value2), search(AnItem.field.greater(value0)));
		assertEquals(List.of(value1, value2), search(AnItem.field.greater(AnItem.compare0)));
		assertEquals(List.of(        value2), search(AnItem.field.greater(value1)));
		assertEquals(List.of(        value2), search(AnItem.field.greater(AnItem.compare1)));
		assertEquals(List.of(              ), search(AnItem.field.greater(value2)));
		assertEquals(List.of(              ), search(AnItem.field.greater(AnItem.compare2)));

		assertEquals(List.of(value0                ), search(AnItem.field.lessOrEqual(value0)));
		assertEquals(List.of(value0                ), search(AnItem.field.lessOrEqual(AnItem.compare0)));
		assertEquals(List.of(value0, value1        ), search(AnItem.field.lessOrEqual(value1)));
		assertEquals(List.of(value0, value1        ), search(AnItem.field.lessOrEqual(AnItem.compare1)));
		assertEquals(List.of(value0, value1, value2), search(AnItem.field.lessOrEqual(value2)));
		assertEquals(List.of(value0, value1, value2), search(AnItem.field.lessOrEqual(AnItem.compare2)));

		assertEquals(List.of(value0, value1, value2), search(AnItem.field.greaterOrEqual(value0)));
		assertEquals(List.of(value0, value1, value2), search(AnItem.field.greaterOrEqual(AnItem.compare0)));
		assertEquals(List.of(        value1, value2), search(AnItem.field.greaterOrEqual(value1)));
		assertEquals(List.of(        value1, value2), search(AnItem.field.greaterOrEqual(AnItem.compare1)));
		assertEquals(List.of(                value2), search(AnItem.field.greaterOrEqual(value2)));
		assertEquals(List.of(                value2), search(AnItem.field.greaterOrEqual(AnItem.compare2)));
	}

	private static List<String> search(final Condition condition)
	{
		final Query<String> q = new Query<>(AnItem.field, condition);
		q.setOrderBy(AnItem.TYPE.getThis(), true);
		return q.search();
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		static final StringField field = new StringField().toFinal().unique();
		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		static final StringField compare0 = new StringField().toFinal();
		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		static final StringField compare1 = new StringField().toFinal();
		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		static final StringField compare2 = new StringField().toFinal();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem(
					@javax.annotation.Nonnull final java.lang.String field,
					@javax.annotation.Nonnull final java.lang.String compare0,
					@javax.annotation.Nonnull final java.lang.String compare1,
					@javax.annotation.Nonnull final java.lang.String compare2)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItem.field,field),
				com.exedio.cope.SetValue.map(AnItem.compare0,compare0),
				com.exedio.cope.SetValue.map(AnItem.compare1,compare1),
				com.exedio.cope.SetValue.map(AnItem.compare2,compare2),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);

	public CollationTest()
	{
		super(MODEL);
	}
}
