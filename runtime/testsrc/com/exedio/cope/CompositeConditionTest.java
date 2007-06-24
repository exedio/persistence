/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.Collections;
import java.util.List;

import com.exedio.cope.junit.CopeAssert;

public class CompositeConditionTest extends CopeAssert
{
	public CompositeConditionTest()
	{
		super();
	}
	
	public void testIt()
	{
		final Condition c1 = CompareConditionItem.doublex.equal(1d);
		final Condition c2 = CompareConditionItem.doublex.equal(2d);
		final Condition c3 = CompareConditionItem.doublex.equal(3d);
		
		try
		{
			new CompositeCondition(null, (Condition[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("operator must not be null", e.getMessage());
		}
		try
		{
			Cope.and((Condition[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions must not be null", e.getMessage());
		}
		try
		{
			Cope.and((List<Condition>)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null/*TODO*/, e.getMessage());
		}
		try
		{
			Cope.or((Condition[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions must not be null", e.getMessage());
		}
		try
		{
			Cope.or((List<Condition>)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null/*TODO*/, e.getMessage());
		}
		try
		{
			Cope.and(new Condition[0]);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("composite condition must have at least one subcondition", e.getMessage());
		}
		try
		{
			Cope.and(Collections.<Condition>emptyList());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("composite condition must have at least one subcondition", e.getMessage());
		}
		try
		{
			Cope.or(new Condition[0]);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("composite condition must have at least one subcondition", e.getMessage());
		}
		try
		{
			Cope.or(Collections.<Condition>emptyList());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("composite condition must have at least one subcondition", e.getMessage());
		}
		try
		{
			Cope.and(new Condition[]{null});
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("condition 0 must not be null", e.getMessage());
		}
		try
		{
			Cope.and(listg((Condition)null));
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("condition 0 must not be null", e.getMessage());
		}
		try
		{
			Cope.or(new Condition[]{null});
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("condition 0 must not be null", e.getMessage());
		}
		try
		{
			Cope.or(listg((Condition)null));
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("condition 0 must not be null", e.getMessage());
		}

		// test composites with a single subcondition
		assertEquals(new CompositeCondition(CompositeCondition.Operator.AND, new Condition[]{c1}), Cope.and(new Condition[]{c1}));
		assertEquals(new CompositeCondition(CompositeCondition.Operator.OR,  new Condition[]{c1}), Cope.or(new Condition[]{c1}));
		
		// test flattening of CompositeCondition
		assertEquals(
				new CompositeCondition(CompositeCondition.Operator.AND, new Condition[]{c1, c2, c3}),
				c1.and(c2).and(c3));
		assertEquals(
				new CompositeCondition(CompositeCondition.Operator.AND, new Condition[]{c1, c2, c3}),
				c1.and(c2.and(c3)));
		assertEquals(
				new CompositeCondition(CompositeCondition.Operator.OR, new Condition[]{c1, c2, c3}),
				c1.or(c2).or(c3));
		assertEquals(
				new CompositeCondition(CompositeCondition.Operator.OR, new Condition[]{c1, c2, c3}),
				c1.or(c2.or(c3)));

		assertEquals(
				new CompositeCondition(CompositeCondition.Operator.AND, new Condition[]{new CompositeCondition(CompositeCondition.Operator.OR, new Condition[]{c1, c2}), c3}),
				c1.or(c2).and(c3));
		assertEquals(
				new CompositeCondition(CompositeCondition.Operator.AND, new Condition[]{c1, new CompositeCondition(CompositeCondition.Operator.OR, new Condition[]{c2, c3})}),
				c1.and(c2.or(c3)));
		assertEquals(
				new CompositeCondition(CompositeCondition.Operator.OR, new Condition[]{new CompositeCondition(CompositeCondition.Operator.AND, new Condition[]{c1, c2}), c3}),
				c1.and(c2).or(c3));
		assertEquals(
				new CompositeCondition(CompositeCondition.Operator.OR, new Condition[]{c1, new CompositeCondition(CompositeCondition.Operator.AND, new Condition[]{c2, c3})}),
				c1.or(c2.and(c3)));
	}
	
	public void testNot()
	{
		final Condition c1 = CompareConditionItem.doublex.equal(1d);
		try
		{
			new NotCondition(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertNull(e.getMessage());
		}
		assertEquals(new NotCondition(c1), c1.not());
		assertEquals(new NotCondition(new NotCondition(c1)), c1.not().not());
	}
}
