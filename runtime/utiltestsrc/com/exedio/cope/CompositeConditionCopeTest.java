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

import static com.exedio.cope.CompositeCondition.Operator.AND;
import static com.exedio.cope.CompositeCondition.Operator.OR;
import static com.exedio.cope.CompositeConditionItem.field;
import static com.exedio.cope.Condition.FALSE;
import static com.exedio.cope.Condition.TRUE;
import static com.exedio.cope.Cope.and;
import static com.exedio.cope.Cope.or;

import java.util.Collections;
import java.util.List;

import com.exedio.cope.junit.CopeAssert;

public class CompositeConditionCopeTest extends CopeAssert
{
	public CompositeConditionCopeTest()
	{
		super();
	}

	public void testIt()
	{
		final Condition c1 = field.equal(1d);

		try
		{
			Cope.and((Condition[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		try
		{
			and((List<Condition>)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		try
		{
			or((Condition[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		try
		{
			or((List<Condition>)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		assertSame(TRUE, and(new Condition[0]));
		assertSame(TRUE, and(Collections.<Condition>emptyList()));
		assertSame(FALSE, or(new Condition[0]));
		assertSame(FALSE, or(Collections.<Condition>emptyList()));

		try
		{
			and(new Condition[]{null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions[0]", e.getMessage());
		}

		// test composites with a single subcondition
		assertEquals(c1, and(new Condition[]{c1}));
		assertEquals(c1, and(listg(c1)));
		assertEquals(c1, or(new Condition[]{c1}));
		assertEquals(c1, or(listg(c1)));
	}

	public void testNeutrumAbsolutum()
	{
		final Condition c1 = field.equal(1d);
		final Condition c2 = field.equal(2d);

		// and/or
		assertSame(c1, and(c1, TRUE));
		assertSame(c1, and(TRUE, c1));
		assertEquals(new CompositeCondition(AND, c1, c2), and(TRUE, c1, c2));
		assertEquals(new CompositeCondition(AND, c1, c2), and(c1, TRUE, c2));
		assertEquals(new CompositeCondition(AND, c1, c2), and(c1, c2, TRUE));
		assertSame(c1, and(TRUE, TRUE, c1));
		assertSame(c1, and(TRUE, c1, TRUE));
		assertSame(c1, and(c1, TRUE, TRUE));
		assertSame(TRUE, and(TRUE, TRUE, TRUE));

		assertSame(c1, or(c1, FALSE));
		assertSame(c1, or(FALSE, c1));
		assertEquals(new CompositeCondition(OR, c1, c2), or(FALSE, c1, c2));
		assertEquals(new CompositeCondition(OR, c1, c2), or(c1, FALSE, c2));
		assertEquals(new CompositeCondition(OR, c1, c2), or(c1, c2, FALSE));
		assertSame(c1, or(FALSE, FALSE, c1));
		assertSame(c1, or(FALSE, c1, FALSE));
		assertSame(c1, or(c1, FALSE, FALSE));
		assertSame(FALSE, or(FALSE, FALSE, FALSE));

		assertSame(FALSE, and(c1, FALSE));
		assertSame(FALSE, and(FALSE, c1));
		assertSame(FALSE, and(FALSE, c1, c2));
		assertSame(FALSE, and(c1, FALSE, c2));
		assertSame(FALSE, and(c1, c2, FALSE));
		assertSame(FALSE, and(FALSE, FALSE, c1));
		assertSame(FALSE, and(FALSE, c1, FALSE));
		assertSame(FALSE, and(c1, FALSE, FALSE));
		assertSame(FALSE, and(FALSE, FALSE, FALSE));

		assertSame(TRUE, or(c1, TRUE));
		assertSame(TRUE, or(TRUE, c1));
		assertSame(TRUE, or(TRUE, c1, c2));
		assertSame(TRUE, or(c1, TRUE, c2));
		assertSame(TRUE, or(c1, c2, TRUE));
		assertSame(TRUE, or(TRUE, TRUE, c1));
		assertSame(TRUE, or(TRUE, c1, TRUE));
		assertSame(TRUE, or(c1, TRUE, TRUE));
		assertSame(TRUE, or(TRUE, TRUE, TRUE));

		assertSame(FALSE, and(TRUE, FALSE, TRUE));
		assertSame(TRUE,  or(FALSE, TRUE, FALSE));
	}
}
