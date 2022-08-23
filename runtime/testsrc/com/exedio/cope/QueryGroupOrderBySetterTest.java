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
import static com.exedio.cope.QueryGroupOrderBySetterItem.alpha;
import static com.exedio.cope.QueryGroupOrderBySetterItem.beta;
import static com.exedio.cope.QueryGroupOrderBySetterItem.gamma;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;

import org.junit.jupiter.api.Test;

/**
 * @see QueryGroupOrderBySetterSelectableTest
 */
public class QueryGroupOrderBySetterTest
{
	@Test void testEmpty()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertEqualsUnmodifiable(asList(), q.getGroupBy());
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}

	@Test void testSetGroupBy()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setGroupBy(alpha);
		assertEqualsUnmodifiable(asList(alpha), q.getGroupBy());
	}
	@Test void testSetGroupByEmpty()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setGroupBy();
		assertEqualsUnmodifiable(asList(), q.getGroupBy());
	}
	@Test void testSetGroupByNull()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setGroupBy((Function<?>[])null),
				NullPointerException.class,
				null);
		assertEqualsUnmodifiable(asList(), q.getGroupBy());
	}
	@Test void testSetGroupByNullElement()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setGroupBy(new Function<?>[]{alpha, null});
		assertEqualsUnmodifiable(asList(alpha, null), q.getGroupBy()); // TODO this is a bug
	}

	@Test void testSetOrderByThisAscending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderByThis(true);
		assertEqualsUnmodifiable(asList(TYPE.getThis()), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(true), q.getOrderByAscending());
	}
	@Test void testSetOrderByThisDescending()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderByThis(false);
		assertEqualsUnmodifiable(asList(TYPE.getThis()), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(false), q.getOrderByAscending());
	}

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
				() -> q.setOrderBy(null, false),
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
				() -> q.setOrderByAndThis(null, false),
				NullPointerException.class,
				"orderBy");
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}

	@Test void testSetOrderBy()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderBy(new Function<?>[]{alpha, beta}, new boolean[]{false, true});
		assertEqualsUnmodifiable(asList(alpha, beta), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(false, true), q.getOrderByAscending());

		q.resetOrderBy();
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@Test void testSetOrderByEmpty()
	{
		final Query<?> q = TYPE.newQuery(null);
		q.setOrderBy(new Function<?>[]{}, new boolean[]{});
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@Test void testSetOrderByLengthMismatch()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(new Function<?>[]{alpha, beta}, new boolean[]{true}),
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
				() -> q.setOrderBy(null, new boolean[]{true, true}),
				NullPointerException.class,
				null);
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@Test void testSetOrderByNullAscending()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(new Function<?>[]{alpha, null}, null),
				NullPointerException.class,
				null);
		assertEqualsUnmodifiable(asList(), q.getOrderByFunctions());
		assertEqualsUnmodifiable(asList(), q.getOrderByAscending());
	}
	@Test void testSetOrderByNullElement()
	{
		final Query<?> q = TYPE.newQuery(null);
		assertFails(
				() -> q.setOrderBy(new Function<?>[]{alpha, null}, new boolean[]{true, true}),
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
		q.addOrderBy(null, false);
		assertEqualsUnmodifiable(asList(new Object[]{null}), q.getOrderByFunctions()); // TODO this is a bug
		assertEqualsUnmodifiable(asList(false), q.getOrderByAscending()); // TODO this is a bug
	}

	@SuppressWarnings("unused") // OK: Model that is never connected
	static final Model MODEL = new Model(TYPE);
}
