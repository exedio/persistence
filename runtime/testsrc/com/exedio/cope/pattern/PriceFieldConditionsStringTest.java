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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.Price.storeOf;
import static com.exedio.cope.pattern.PriceFieldItem.optionalPrice;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import org.junit.jupiter.api.Test;

public class PriceFieldConditionsStringTest
{
	private static final Price
				pN = null,
				p1 = storeOf(111),
				p2 = storeOf(222),
				p3 = storeOf(333);

	private static final PriceField f = optionalPrice;
	private static final String s = f.getInt().getID();

	@Test void testCondition()
	{
		final Query<?> q = PriceFieldItem.TYPE.newQuery();
		final Join j = q.join(PriceFieldItem.TYPE);
		final PriceFunction b = f.bind(j);

		assertEquals("p1."+s, b.toString());

		assertEquals(      s+" is null"    , f.isNull()    .toString());
		assertEquals("p1."+s+" is null"    , b.isNull()    .toString());
		assertEquals(      s+" is not null", f.isNotNull() .toString());
		assertEquals("p1."+s+" is not null", b.isNotNull() .toString());
		assertEquals(      s+" is null"    , f.equal(pN)   .toString());
		assertEquals("p1."+s+" is null"    , b.equal(pN)   .toString());
		assertEquals(      s+" is not null", f.notEqual(pN).toString());
		assertEquals("p1."+s+" is not null", b.notEqual(pN).toString());
		assertEquals(      s+"='111'"      , f.equal(p1)   .toString());
		assertEquals("p1."+s+"='111'"      , b.equal(p1)   .toString());
		assertEquals(      s+"<>'111'"     , f.notEqual(p1).toString());
		assertEquals("p1."+s+"<>'111'"     , b.notEqual(p1).toString());

		assertEquals(      s+"<'222'" , f.less          (p2).toString());
		assertEquals("p1."+s+"<'222'" , b.less          (p2).toString());
		assertEquals(      s+"<='222'", f.lessOrEqual   (p2).toString());
		assertEquals("p1."+s+"<='222'", b.lessOrEqual   (p2).toString());
		assertEquals(      s+">='222'", f.greaterOrEqual(p2).toString());
		assertEquals("p1."+s+">='222'", b.greaterOrEqual(p2).toString());
		assertEquals(      s+">'222'" , f.greater       (p2).toString());
		assertEquals("p1."+s+">'222'" , b.greater       (p2).toString());
		assertEquals("("   +s+">='222' AND "   +s+"<='333')", f.between(p2, p3).toString());
		assertEquals("(p1."+s+">='222' AND p1."+s+"<='333')", b.between(p2, p3).toString());
	}

	@Test void testJoinNull()
	{
		try
		{
			f.bind(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("join", e.getMessage());
		}
	}

	@SuppressWarnings("unused") // OK: initializes types
	private static final Model MODEL = PriceFieldModelTest.MODEL;
}
