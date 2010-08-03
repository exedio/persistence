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
			Cope.and((List<Condition>)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		try
		{
			Cope.or((Condition[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		try
		{
			Cope.or((List<Condition>)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions", e.getMessage());
		}
		assertSame(TRUE, Cope.and(new Condition[0]));
		assertSame(TRUE, Cope.and(Collections.<Condition>emptyList()));
		assertSame(FALSE, Cope.or(new Condition[0]));
		assertSame(FALSE, Cope.or(Collections.<Condition>emptyList()));

		try
		{
			Cope.and(new Condition[]{null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions[0]", e.getMessage());
		}

		// test composites with a single subcondition
		assertEquals(c1, Cope.and(new Condition[]{c1}));
		assertEquals(c1, Cope.and(listg(c1)));
		assertEquals(c1, Cope.or(new Condition[]{c1}));
		assertEquals(c1, Cope.or(listg(c1)));
	}

	public void testNeutrumAbsolutum()
	{
		final Condition c1 = field.equal(1d);
		final Condition c2 = field.equal(2d);

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
	}
}
