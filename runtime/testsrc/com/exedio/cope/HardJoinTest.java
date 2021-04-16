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

import static com.exedio.cope.HardJoinA1Item.a1;
import static com.exedio.cope.HardJoinA2Item.a2;
import static com.exedio.cope.HardJoinA3Item.a3;
import static com.exedio.cope.HardJoinB1Item.b1;
import static com.exedio.cope.HardJoinB2Item.b2;
import static com.exedio.cope.HardJoinB3Item.b3;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HardJoinTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(
			HardJoinA1Item.TYPE,
			HardJoinA2Item.TYPE,
			HardJoinA3Item.TYPE,
			HardJoinB1Item.TYPE,
			HardJoinB2Item.TYPE,
			HardJoinB3Item.TYPE);

	public HardJoinTest()
	{
		super(MODEL);
	}

	private static final Type<HardJoinA3Item> aTYPE = HardJoinA3Item.TYPE;
	private static final Type<HardJoinB3Item> bTYPE = HardJoinB3Item.TYPE;

	private HardJoinA3Item a;
	private HardJoinB3Item b;

	@BeforeEach final void setUp()
	{
		a = new HardJoinA3Item("a", 10, 11, 12);
		b = new HardJoinB3Item("b", 20, 21, 22);
	}

	private void reset()
	{
		a.setA1(10);
		a.setA2(11);
		a.setA3(12);
		b.setB1(20);
		b.setB2(21);
		b.setB3(22);
	}

	private void assert1x1(final IntegerField ax, final IntegerField bx, final int bv)
	{
		reset();
		final Query<HardJoinA3Item> q = aTYPE.newQuery();
		q.join(bTYPE, ax.equal(bx));
		assertEquals(list(), q.search());
		bx.set(b, bv);
		assertEquals(list(a), q.search());
	}

	@Test void test11()
	{
		assert1x1(a1, b1, 10);
		assert1x1(a1, b2, 10);
		assert1x1(a1, b3, 10);
		assert1x1(a2, b1, 11);
		assert1x1(a2, b2, 11);
		assert1x1(a2, b3, 11);
		assert1x1(a3, b1, 12);
		assert1x1(a3, b2, 12);
		assert1x1(a3, b3, 12);
	}

	private void assert2x1(final IntegerField a1, final IntegerField a2, final IntegerField bx, final int bv)
	{
		reset();
		final Query<HardJoinA3Item> q = aTYPE.newQuery();
		q.join(bTYPE, a1.equal(bx).and(a2.equal(bx)));
		assertEquals(list(), q.search());
		a1.set(a, bv);
		assertEquals(list(), q.search());
		a2.set(a, bv);
		assertEquals(list(a), q.search());
	}

	@Test void test2x1()
	{
		assert2x1(a1, a2, b1, 20);
		assert2x1(a1, a3, b1, 20);
		assert2x1(a2, a3, b1, 20);
		assert2x1(a1, a2, b2, 21);
		assert2x1(a1, a3, b2, 21);
		assert2x1(a2, a3, b2, 21);
		assert2x1(a1, a2, b3, 22);
		assert2x1(a1, a3, b3, 22);
		assert2x1(a2, a3, b3, 22);
	}

	private void assert1x2(final IntegerField ax, final IntegerField b1, final IntegerField b2, final int av)
	{
		reset();
		final Query<HardJoinA3Item> q = aTYPE.newQuery();
		q.join(bTYPE, ax.equal(b1).and(ax.equal(b2)));
		assertEquals(list(), q.search());
		b1.set(b, av);
		assertEquals(list(), q.search());
		b2.set(b, av);
		assertEquals(list(a), q.search());
	}

	@Test void test1x2()
	{
		assert1x2(a1, b1, b2, 10);
		assert1x2(a1, b1, b3, 10);
		assert1x2(a1, b2, b3, 10);
		assert1x2(a2, b1, b2, 11);
		assert1x2(a2, b1, b3, 11);
		assert1x2(a2, b2, b3, 11);
		assert1x2(a3, b1, b2, 12);
		assert1x2(a3, b1, b3, 12);
		assert1x2(a3, b2, b3, 12);
	}

	@Test void testOuter()
	{
		{
			final Query<HardJoinA3Item> q = aTYPE.newQuery();
			q.joinOuterLeft(bTYPE, a1.equal(b3));
			assertEquals(list(a), q.search());
		}
		{
			final Query<HardJoinA3Item> q = aTYPE.newQuery();
			q.joinOuterLeft(bTYPE, a1.equal(b1));
			assertEquals(list(a), q.search());
		}
		{
			final Query<HardJoinA3Item> q = aTYPE.newQuery();
			q.joinOuterLeft(bTYPE, a2.equal(b2));
			assertEquals(list(a), q.search());
		}
		{
			final Query<HardJoinA3Item> q = aTYPE.newQuery();
			q.joinOuterLeft(bTYPE, a3.equal(b3));
			assertEquals(list(a), q.search());
		}
	}

	@Test void testValid()
	{
		{
			final Query<HardJoinA3Item> q = aTYPE.newQuery();
			final Join j1 = q.join(bTYPE, b3.equal(a3));
			final Join j2 = q.join(bTYPE, b3.equal(a3));
			try
			{
				q.search();
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals(b3 + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}

			j1.setCondition(b3.bind(j1).equal(a3));
			try
			{
				q.search();
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals(b3 + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}

			j2.setCondition(b3.bind(j2).equal(a3));
			assertEquals(list(), q.search());

			// test with super fields
			j1.setCondition(b1.equal(a3));
			j2.setCondition(b1.equal(a3));
			try
			{
				q.search();
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals(b1 + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}

			j1.setCondition(b1.bind(j1).equal(a3));
			try
			{
				q.search();
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals(b1 + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}

			j2.setCondition(b1.bind(j2).equal(a3));
			assertEquals(list(), q.search());
		}
		// test with typeIn
		{
			final Query<HardJoinA3Item> q = aTYPE.newQuery();
			final Join j1 = q.join(HardJoinB2Item.TYPE, HardJoinB2Item.TYPE.getThis().notInstanceOf(bTYPE));
			final Join j2 = q.join(HardJoinB2Item.TYPE, HardJoinB2Item.TYPE.getThis().notInstanceOf(bTYPE));

			try
			{
				q.search();
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals(HardJoinB2Item.TYPE.getThis() + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}

			j1.setCondition(HardJoinB2Item.TYPE.getThis().bind(j1).notInstanceOf(bTYPE));
			try
			{
				q.search();
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals(HardJoinB2Item.TYPE.getThis() + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}

			j2.setCondition(HardJoinB2Item.TYPE.getThis().bind(j2).notInstanceOf(bTYPE));
			assertEquals(list(), q.search());
		}
	}
}
