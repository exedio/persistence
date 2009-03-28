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

import java.util.Collections;
import java.util.List;

import com.exedio.cope.junit.CopeAssert;

public class CompositeConditionTest extends CopeAssert
{
	private static final Condition TRUE  = Condition.TRUE;
	private static final Condition FALSE = Condition.FALSE;
	
	private static final CompositeCondition.Operator AND = CompositeCondition.Operator.AND;
	private static final CompositeCondition.Operator OR  = CompositeCondition.Operator.OR;
	
	public CompositeConditionTest()
	{
		super();
	}
	
	public void testIt()
	{
		final Condition c1 = CompareConditionItem.doublex.equal(1d);
		final Condition c2 = CompareConditionItem.doublex.equal(2d);
		final Condition c3 = CompareConditionItem.doublex.equal(3d);
		
		assertEquals(TRUE, TRUE);
		assertEquals(FALSE, FALSE);
		assertFalse(TRUE.equals(FALSE));
		assertFalse(FALSE.equals(TRUE));
		
		assertFalse(TRUE.equals(c1));
		assertFalse(c1.equals(TRUE));
		assertFalse(FALSE.equals(c1));
		assertFalse(c1.equals(FALSE));
		
		try
		{
			new CompositeCondition(null, (Condition[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("operator", e.getMessage());
		}
		try
		{
			Cope.and((Condition[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		try
		{
			Cope.and((List<Condition>)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		try
		{
			Cope.or((Condition[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		try
		{
			Cope.or((List<Condition>)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		assertSame(TRUE, Cope.and(new Condition[0]));
		assertSame(TRUE, Cope.and(Collections.<Condition>emptyList()));
		assertSame(FALSE, Cope.or(new Condition[0]));
		assertSame(FALSE, Cope.or(Collections.<Condition>emptyList()));
		try
		{
			new CompositeCondition(AND);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("conditions must not be empty", e.getMessage());
		}
		try
		{
			new CompositeCondition(AND, Collections.<Condition>emptyList());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("conditions must not be empty", e.getMessage());
		}
		try
		{
			new CompositeCondition(OR);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("conditions must not be empty", e.getMessage());
		}
		try
		{
			new CompositeCondition(OR, Collections.<Condition>emptyList());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("conditions must not be empty", e.getMessage());
		}
		try
		{
			Cope.and(new Condition[]{null});
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions[0]", e.getMessage());
		}
		try
		{
			new CompositeCondition(AND, listg((Condition)null));
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions[0]", e.getMessage());
		}
		try
		{
			new CompositeCondition(OR, (Condition)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions[0]", e.getMessage());
		}
		try
		{
			new CompositeCondition(OR, listg((Condition)null));
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions[0]", e.getMessage());
		}
		try
		{
			new CompositeCondition(AND, Condition.TRUE);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("conditions[0] must not be a literal", e.getMessage());
		}
		try
		{
			new CompositeCondition(AND, listg(Condition.TRUE));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("conditions[0] must not be a literal", e.getMessage());
		}
		try
		{
			new CompositeCondition(OR, Condition.TRUE);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("conditions[0] must not be a literal", e.getMessage());
		}
		try
		{
			new CompositeCondition(OR, listg(Condition.TRUE));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("conditions[0] must not be a literal", e.getMessage());
		}

		// test composites with a single subcondition
		assertEquals(c1, Cope.and(new Condition[]{c1}));
		assertEquals(c1, Cope.and(listg(c1)));
		assertEquals(c1, Cope.or(new Condition[]{c1}));
		assertEquals(c1, Cope.or(listg(c1)));
		
		// test flattening of CompositeCondition
		assertEquals(new CompositeCondition(AND, c1, c2, c3), c1.and(c2).and(c3));
		assertEquals(new CompositeCondition(AND, c1, c2, c3), c1.and(c2.and(c3)));
		assertEquals(new CompositeCondition(OR,  c1, c2, c3), c1.or(c2).or(c3));
		assertEquals(new CompositeCondition(OR,  c1, c2, c3), c1.or(c2.or(c3)));

		assertEquals(new CompositeCondition(AND, new CompositeCondition(OR,  c1, c2), c3), c1.or(c2).and(c3));
		assertEquals(new CompositeCondition(AND, c1, new CompositeCondition(OR,  c2, c3)), c1.and(c2.or(c3)));
		assertEquals(new CompositeCondition(OR,  new CompositeCondition(AND, c1, c2), c3), c1.and(c2).or(c3));
		assertEquals(new CompositeCondition(OR,  c1, new CompositeCondition(AND, c2, c3)), c1.or(c2.and(c3)));
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
			assertEquals("argument", e.getMessage());
		}
		try
		{
			new NotCondition(Condition.TRUE);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("argument must not be a literal", e.getMessage());
		}
		assertSame(TRUE, FALSE.not());
		assertSame(FALSE, TRUE.not());
		assertEquals(new NotCondition(c1), c1.not());
		assertSame(c1, c1.not().not());
	}
	
	public void testNeutrumAbsolutum()
	{
		final Condition c1 = CompareConditionItem.doublex.equal(1d);
		final Condition c2 = CompareConditionItem.doublex.equal(2d);

		// Condition.and/or
		assertSame(c1, c1.and(TRUE));
		assertSame(c1, TRUE.and(c1));
		assertSame(c1, c1.or(FALSE));
		assertSame(c1, FALSE.or(c1));
		
		assertSame(FALSE, c1.and(FALSE));
		assertSame(FALSE, FALSE.and(c1));
		assertSame(TRUE,  c1.or(TRUE));
		assertSame(TRUE,  TRUE.or(c1));
		
		assertSame(FALSE, TRUE.and(FALSE));
		assertSame(FALSE, FALSE.and(TRUE));
		assertSame(TRUE,  TRUE.or(FALSE));
		assertSame(TRUE,  FALSE.or(TRUE));

		// Cope.and/or
		assertSame(c1, Cope.and(c1, TRUE));
		assertSame(c1, Cope.and(TRUE, c1));
		assertEquals(new CompositeCondition(AND, c1, c2), Cope.and(TRUE, c1, c2));
		assertEquals(new CompositeCondition(AND, c1, c2), Cope.and(c1, TRUE, c2));
		assertEquals(new CompositeCondition(AND, c1, c2), Cope.and(c1, c2, TRUE));
		assertSame(c1, Cope.and(TRUE, TRUE, c1));
		assertSame(c1, Cope.and(TRUE, c1, TRUE));
		assertSame(c1, Cope.and(c1, TRUE, TRUE));
		assertSame(TRUE, Cope.and(TRUE, TRUE, TRUE));

		assertSame(c1, Cope.or(c1, FALSE));
		assertSame(c1, Cope.or(FALSE, c1));
		assertEquals(new CompositeCondition(OR, c1, c2), Cope.or(FALSE, c1, c2));
		assertEquals(new CompositeCondition(OR, c1, c2), Cope.or(c1, FALSE, c2));
		assertEquals(new CompositeCondition(OR, c1, c2), Cope.or(c1, c2, FALSE));
		assertSame(c1, Cope.or(FALSE, FALSE, c1));
		assertSame(c1, Cope.or(FALSE, c1, FALSE));
		assertSame(c1, Cope.or(c1, FALSE, FALSE));
		assertSame(FALSE, Cope.or(FALSE, FALSE, FALSE));

		assertSame(FALSE, Cope.and(c1, FALSE));
		assertSame(FALSE, Cope.and(FALSE, c1));
		assertSame(FALSE, Cope.and(FALSE, c1, c2));
		assertSame(FALSE, Cope.and(c1, FALSE, c2));
		assertSame(FALSE, Cope.and(c1, c2, FALSE));
		assertSame(FALSE, Cope.and(FALSE, FALSE, c1));
		assertSame(FALSE, Cope.and(FALSE, c1, FALSE));
		assertSame(FALSE, Cope.and(c1, FALSE, FALSE));
		assertSame(FALSE, Cope.and(FALSE, FALSE, FALSE));

		assertSame(TRUE, Cope.or(c1, TRUE));
		assertSame(TRUE, Cope.or(TRUE, c1));
		assertSame(TRUE, Cope.or(TRUE, c1, c2));
		assertSame(TRUE, Cope.or(c1, TRUE, c2));
		assertSame(TRUE, Cope.or(c1, c2, TRUE));
		assertSame(TRUE, Cope.or(TRUE, TRUE, c1));
		assertSame(TRUE, Cope.or(TRUE, c1, TRUE));
		assertSame(TRUE, Cope.or(c1, TRUE, TRUE));
		assertSame(TRUE, Cope.or(TRUE, TRUE, TRUE));

		assertSame(FALSE, Cope.and(TRUE, FALSE, TRUE));
		assertSame(TRUE,  Cope.or(FALSE, TRUE, FALSE));
		
		// Function.in
		assertEquals(new CompositeCondition(OR, c1, c2), CompareConditionItem.doublex.in(1.0, 2.0));
		assertEquals(new CompositeCondition(OR, c1, c2), CompareConditionItem.doublex.in(listg(1.0, 2.0)));
		assertEquals(c1, CompareConditionItem.doublex.in(1.0));
		assertEquals(c1, CompareConditionItem.doublex.in(listg(1.0)));
		assertEquals(c2, CompareConditionItem.doublex.in(2.0));
		assertEquals(c2, CompareConditionItem.doublex.in(listg(2.0)));
		assertSame(FALSE, CompareConditionItem.doublex.in());
		assertSame(FALSE, CompareConditionItem.doublex.in(CopeAssert.<Double>listg()));
		
		// Condition.valueOf
		assertSame(Condition.TRUE,  Condition.valueOf(true));
		assertSame(Condition.FALSE, Condition.valueOf(false));
	}
}
