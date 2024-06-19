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

import static com.exedio.cope.QueryGroupOrderBySetterItem.TYPE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

/**
 * @see QueryGroupOrderBySetterTest
 */
public class QueryGroupOrderBySetterSelectableTest
{
	private static final Selectable<?> alpha = QueryGroupOrderBySetterItem.alpha;
	private static final Selectable<?> beta  = QueryGroupOrderBySetterItem.beta;
	private static final Selectable<?> gamma = QueryGroupOrderBySetterItem.gamma;

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetGroupBy()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setGroupBy(alpha);
		assertEqualsUnmodifiable(asList(alpha), q.getGroupBys());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetGroupByEmpty()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setGroupBy(new Selectable<?>[]{});
		assertEqualsUnmodifiable(asList(), q.getGroupBys());

		q.setGroupBy(alpha);
		assertEqualsUnmodifiable(asList(alpha), q.getGroupBys());

		q.setGroupBy(new Selectable<?>[]{});
		assertEqualsUnmodifiable(asList(), q.getGroupBys());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetGroupByNull()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setGroupBy((Selectable<?>[])null),
				NullPointerException.class,
				"groupBy");
		assertEqualsUnmodifiable(asList(), q.getGroupBys());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetGroupByNullElement()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setGroupBy(new Selectable<?>[]{alpha, null}),
				NullPointerException.class,
				"groupBy[1]");
		assertEqualsUnmodifiable(asList(), q.getGroupBys());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetGroupByNonFunction()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setGroupBy(new Selectable<?>[]{null, nonFunction}),
				IllegalArgumentException.class,
				"groupBy[1] is no Function but " + nonFunction);
		assertEqualsUnmodifiable(asList(), q.getGroupBys());
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByAscending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderBy(alpha, true);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderBys());
		assertEqualsUnmodifiable(asList(true), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByDescending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderBy(alpha, false);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderBys());
		assertEqualsUnmodifiable(asList(false), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByNull()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy((Selectable<?>)null, false),
				NullPointerException.class,
				"orderBy");
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByNonFunction()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(nonFunction, false),
				IllegalArgumentException.class,
				"orderBy is no Function but " + nonFunction);
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByAndThisAscending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderByAndThis(alpha, true);
		assertEqualsUnmodifiable(asList(alpha, TYPE.getThis()), q.getOrderBys());
		assertEqualsUnmodifiable(asList(true, true), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByAndThisDescending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderByAndThis(alpha, false);
		assertEqualsUnmodifiable(asList(alpha, TYPE.getThis()), q.getOrderBys());
		assertEqualsUnmodifiable(asList(false, true), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByAndThisNull()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderByAndThis((Selectable<?>)null, false),
				NullPointerException.class,
				"orderBy");
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByAndThisNonFunction()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderByAndThis(nonFunction, false),
				IllegalArgumentException.class,
				"orderBy is no Function but " + nonFunction);
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderBy()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderBy(new Selectable<?>[]{alpha, beta}, new boolean[]{false, true});
		assertEqualsUnmodifiable(asList(alpha, beta), q.getOrderBys());
		assertEqualsUnmodifiable(asList(false, true), q.getOrderByAscending());

		q.resetOrderBy();
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByEmpty()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderBy(new Selectable<?>[]{}, new boolean[]{});
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());

		q.addOrderBy(alpha);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderBys());
		assertEqualsUnmodifiable(asList(true), q.getOrderByAscending());

		q.setOrderBy(new Selectable<?>[]{}, new boolean[]{});
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByLengthMismatch()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(new Selectable<?>[]{alpha, beta}, new boolean[]{true}),
				IllegalArgumentException.class,
				"orderBy and ascending must have same length, " +
				"but was 2 and 1");
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByNullOrderBy()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy((Selectable<?>[])null, new boolean[]{true, true}),
				NullPointerException.class,
				"orderBy");
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByNullAscending()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(new Selectable<?>[]{alpha, null}, null),
				NullPointerException.class,
				"ascending");
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByNullElement()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(new Selectable<?>[]{alpha, null}, new boolean[]{true, true}),
				NullPointerException.class,
				"orderBy[1]");
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testSetOrderByArrayNonFunction()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(new Selectable<?>[]{alpha, nonFunction}, new boolean[]{true, true}),
				IllegalArgumentException.class,
				"orderBy[1] is no Function but " + nonFunction);
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testAddOrderByAscending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.addOrderBy(alpha);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderBys());
		assertEqualsUnmodifiable(asList(true), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testAddOrderByDescending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.addOrderByDescending(alpha);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderBys());
		assertEqualsUnmodifiable(asList(false), q.getOrderByAscending());
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testAddOrderBy()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.addOrderBy(alpha, false);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderBys());
		assertEqualsUnmodifiable(asList(false), q.getOrderByAscending());

		q.addOrderBy(beta, true);
		assertEqualsUnmodifiable(asList(alpha, beta), q.getOrderBys());
		assertEqualsUnmodifiable(asList(false, true), q.getOrderByAscending());

		q.addOrderBy(gamma, false);
		assertEqualsUnmodifiable(asList(alpha, beta, gamma), q.getOrderBys());
		assertEqualsUnmodifiable(asList(false, true, false), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testAddOrderByNull()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.addOrderBy((Selectable<?>)null, false),
				NullPointerException.class,
				"orderBy");
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testAddOrderByNonFunction()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.addOrderBy(nonFunction, false),
				IllegalArgumentException.class,
				"orderBy is no Function but " + nonFunction);
		assertEqualsUnmodifiable(asList(), q.getOrderBys());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}

	private static final Selectable<?> nonFunction = new Selectable<>()
	{
		@Override
		public Class<Object> getValueClass()
		{
			throw new AssertionFailedError();
		}
		@Override
		public SelectType<Object> getValueType()
		{
			throw new AssertionFailedError();
		}
		@Override
		public Type<?> getType()
		{
			throw new AssertionFailedError();
		}
		@Override
		public void toString(final StringBuilder bf, final Type<?> defaultType)
		{
			throw new AssertionFailedError();
		}
		@Override
		@Deprecated
		public void check(final TC tc, final Join join)
		{
			throw new AssertionFailedError();
		}
		@Override
		public void forEachFieldCovered(final Consumer<Field<?>> action)
		{
			throw new AssertionFailedError();
		}
		@Override
		@Deprecated
		public void append(final Statement bf, final Join join)
		{
			throw new AssertionFailedError();
		}
		private static final long serialVersionUID = -1l;
	};

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testGetterDeprecated()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertEqualsUnmodifiable(asList(), q.getGroupBy());
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		q.setGroupBy(alpha);
		q.setOrderBy(beta, true);
		assertEqualsUnmodifiable(asList(alpha), q.getGroupBy());
		assertEqualsUnmodifiable(asList(beta), q.getOrderByFunctions());
	}

	@SuppressWarnings("unused") // OK: Model that is never connected
	private static final Model MODEL = QueryGroupOrderBySetterTest.MODEL;
}
