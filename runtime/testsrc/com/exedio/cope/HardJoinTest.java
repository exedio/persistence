/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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


public class HardJoinTest extends AbstractRuntimeTest
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
	
	private HardJoinA3Item a;
	private HardJoinB3Item b;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		a = deleteOnTearDown(new HardJoinA3Item("a", 10, 11, 12));
		b = deleteOnTearDown(new HardJoinB3Item("b", 20, 21, 22));
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
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, ax.equal(bx));
		assertEquals(list(), q.search());
		bx.set(b, bv);
		assertEquals(list(a), q.search());
	}
	
	public void test11()
	{
		if(!noJoinParentheses)
		{
			assert1x1(a.a1, b.b1, 10);
			assert1x1(a.a1, b.b2, 10);
			assert1x1(a.a1, b.b3, 10);
			assert1x1(a.a2, b.b1, 11);
			assert1x1(a.a2, b.b2, 11);
			assert1x1(a.a2, b.b3, 11);
			assert1x1(a.a3, b.b1, 12);
			assert1x1(a.a3, b.b2, 12);
		}
		assert1x1(a.a3, b.b3, 12);
	}
	
	private void assert2x1(final IntegerField a1, final IntegerField a2, final IntegerField bx, final int bv)
	{
		reset();
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a1.equal(bx).and(a2.equal(bx)));
		assertEquals(list(), q.search());
		a1.set(a, bv);
		assertEquals(list(), q.search());
		a2.set(a, bv);
		assertEquals(list(a), q.search());
	}
	
	public void test2x1()
	{
		if(noJoinParentheses) return;
		
		assert2x1(a.a1, a.a2, b.b1, 20);
		assert2x1(a.a1, a.a3, b.b1, 20);
		assert2x1(a.a2, a.a3, b.b1, 20);
		assert2x1(a.a1, a.a2, b.b2, 21);
		assert2x1(a.a1, a.a3, b.b2, 21);
		assert2x1(a.a2, a.a3, b.b2, 21);
		assert2x1(a.a1, a.a2, b.b3, 22);
		assert2x1(a.a1, a.a3, b.b3, 22);
		assert2x1(a.a2, a.a3, b.b3, 22);
	}
	
	private void assert1x2(final IntegerField ax, final IntegerField b1, final IntegerField b2, final int av)
	{
		reset();
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, ax.equal(b1).and(ax.equal(b2)));
		assertEquals(list(), q.search());
		b1.set(b, av);
		assertEquals(list(), q.search());
		b2.set(b, av);
		assertEquals(list(a), q.search());
	}
	
	public void test1x2()
	{
		if(noJoinParentheses) return;
		
		assert1x2(a.a1, b.b1, b.b2, 10);
		assert1x2(a.a1, b.b1, b.b3, 10);
		assert1x2(a.a1, b.b2, b.b3, 10);
		assert1x2(a.a2, b.b1, b.b2, 11);
		assert1x2(a.a2, b.b1, b.b3, 11);
		assert1x2(a.a2, b.b2, b.b3, 11);
		assert1x2(a.a3, b.b1, b.b2, 12);
		assert1x2(a.a3, b.b1, b.b3, 12);
		assert1x2(a.a3, b.b2, b.b3, 12);
	}
	
	public void testOuter()
	{
		if(!noJoinParentheses)
		{
			final Query<HardJoinA3Item> q = a.TYPE.newQuery();
			q.joinOuterLeft(b.TYPE, a.a1.equal(b.b3));
			assertEquals(list(a), q.search());
		}
		if(!noJoinParentheses)
		{
			final Query<HardJoinA3Item> q = a.TYPE.newQuery();
			q.joinOuterLeft(b.TYPE, a.a1.equal(b.b1));
			assertEquals(list(a), q.search());
		}
		if(!noJoinParentheses)
		{
			final Query<HardJoinA3Item> q = a.TYPE.newQuery();
			q.joinOuterLeft(b.TYPE, a.a2.equal(b.b2));
			assertEquals(list(a), q.search());
		}
		{
			final Query<HardJoinA3Item> q = a.TYPE.newQuery();
			q.joinOuterLeft(b.TYPE, a.a3.equal(b.b3));
			assertEquals(list(a), q.search());
		}
	}
	
	public void testValid()
	{
		{
			final Query<HardJoinA3Item> q = a.TYPE.newQuery();
			final Join j1 = q.join(b.TYPE, b.b3.equal(a.a3));
			final Join j2 = q.join(b.TYPE, b.b3.equal(a.a3));
			try
			{
				q.search();
				fail();
			}
			catch(IllegalArgumentException e)
			{
				assertEquals(b.b3 + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}
			
			j1.setCondition(b.b3.bind(j1).equal(a.a3));
			try
			{
				q.search();
				fail();
			}
			catch(IllegalArgumentException e)
			{
				assertEquals(b.b3 + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}
			
			j2.setCondition(b.b3.bind(j2).equal(a.a3));
			assertEquals(list(), q.search());

			// test with super fields
			j1.setCondition(b.b1.equal(a.a3));
			j2.setCondition(b.b1.equal(a.a3));
			try
			{
				q.search();
				fail();
			}
			catch(IllegalArgumentException e)
			{
				assertEquals(b.b1 + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}

			j1.setCondition(b.b1.bind(j1).equal(a.a3));
			try
			{
				q.search();
				fail();
			}
			catch(IllegalArgumentException e)
			{
				assertEquals(b.b1 + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}

			j2.setCondition(b.b1.bind(j2).equal(a.a3));
			if(!noJoinParentheses) assertEquals(list(), q.search());
		}
		// test with typeIn
		{
			final Query<HardJoinA3Item> q = a.TYPE.newQuery();
			final Join j1 = q.join(HardJoinB2Item.TYPE, HardJoinB2Item.TYPE.getThis().notInstanceOf(b.TYPE));
			final Join j2 = q.join(HardJoinB2Item.TYPE, HardJoinB2Item.TYPE.getThis().notInstanceOf(b.TYPE));

			try
			{
				q.search();
				fail();
			}
			catch(IllegalArgumentException e)
			{
				assertEquals(HardJoinB2Item.TYPE.getThis() + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}

			j1.setCondition(HardJoinB2Item.TYPE.getThis().bind(j1).notInstanceOf(b.TYPE));
			try
			{
				q.search();
				fail();
			}
			catch(IllegalArgumentException e)
			{
				assertEquals(HardJoinB2Item.TYPE.getThis() + " is ambiguous, use Function#bind in query: " + q, e.getMessage());
			}

			j2.setCondition(HardJoinB2Item.TYPE.getThis().bind(j2).notInstanceOf(b.TYPE));
			assertEquals(list(), q.search());
		}
	}
}
