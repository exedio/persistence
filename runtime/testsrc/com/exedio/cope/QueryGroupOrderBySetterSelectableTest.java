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

import org.junit.jupiter.api.Test;

/**
 * @see QueryGroupOrderBySetterTest
 */
public class QueryGroupOrderBySetterSelectableTest
{
	private static final Selectable<?> alpha = QueryGroupOrderBySetterItem.alpha;
	private static final Selectable<?> beta  = QueryGroupOrderBySetterItem.beta;
	private static final Selectable<?> gamma = QueryGroupOrderBySetterItem.gamma;

	@Test void testSetOrderByAscending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderBy(alpha, true);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(true), q.getOrderByAscending());
	}
	@Test void testSetOrderByDescending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderBy(alpha, false);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(false), q.getOrderByAscending());
	}
	@Test void testSetOrderByNull()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy((Selectable<?>)null, false),
				NullPointerException.class,
				"orderBy");
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}

	@Test void testSetOrderByAndThisAscending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderByAndThis(alpha, true);
		assertEqualsUnmodifiable(asList(alpha, TYPE.getThis()), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(true, true), q.getOrderByAscending());
	}
	@Test void testSetOrderByAndThisDescending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderByAndThis(alpha, false);
		assertEqualsUnmodifiable(asList(alpha, TYPE.getThis()), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(false, true), q.getOrderByAscending());
	}
	@Test void testSetOrderByAndThisNull()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderByAndThis((Selectable<?>)null, false),
				NullPointerException.class,
				"orderBy");
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}

	@Test void testSetOrderBy()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderBy(new Selectable<?>[]{alpha, beta}, new boolean[]{false, true});
		assertEqualsUnmodifiable(asList(alpha, beta), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(false, true), q.getOrderByAscending());

		q.resetOrderBy();
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@Test void testSetOrderByEmpty()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderBy(new Selectable<?>[]{}, new boolean[]{});
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());

		q.addOrderBy(alpha);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(true), q.getOrderByAscending());

		q.setOrderBy(new Selectable<?>[]{}, new boolean[]{});
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@Test void testSetOrderByLengthMismatch()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(new Selectable<?>[]{alpha, beta}, new boolean[]{true}),
				IllegalArgumentException.class,
				"orderBy and ascending must have same length, " +
				"but was 2 and 1");
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@Test void testSetOrderByNullOrderBy()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy((Selectable<?>[])null, new boolean[]{true, true}),
				NullPointerException.class,
				"orderBy");
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@Test void testSetOrderByNullAscending()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(new Selectable<?>[]{alpha, null}, null),
				NullPointerException.class,
				"ascending");
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@Test void testSetOrderByNullElement()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(new Selectable<?>[]{alpha, null}, new boolean[]{true, true}),
				NullPointerException.class,
				"orderBy[1]");
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}

	@Test void testAddOrderByAscending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.addOrderBy(alpha);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(true), q.getOrderByAscending());
	}
	@Test void testAddOrderByDescending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.addOrderByDescending(alpha);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(false), q.getOrderByAscending());
	}

	@Test void testAddOrderBy()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.addOrderBy(alpha, false);
		assertEqualsUnmodifiable(asList(alpha), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(false), q.getOrderByAscending());

		q.addOrderBy(beta, true);
		assertEqualsUnmodifiable(asList(alpha, beta), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(false, true), q.getOrderByAscending());

		q.addOrderBy(gamma, false);
		assertEqualsUnmodifiable(asList(alpha, beta, gamma), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(false, true, false), q.getOrderByAscending());
	}
	@Test void testAddOrderByNull()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.addOrderBy((Selectable<?>)null, false),
				NullPointerException.class,
				"orderBy");
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}

	@SuppressWarnings("unused") // OK: Model that is never connected
	private static final Model MODEL = QueryGroupOrderBySetterTest.MODEL;
}