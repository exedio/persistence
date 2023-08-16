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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import org.junit.jupiter.api.Test;

public class PriceFieldConditionsStringTest
{
	private static final Price
			vN = null,
			v1 = storeOf(111),
			v2 = storeOf(222),
			v3 = storeOf(333);

	private static final PriceField f = optionalPrice;
	private static final String s = f.getInt().getID();

	@Test void testCondition()
	{
		final Query<?> q = PriceFieldItem.TYPE.newQuery();
		final Join j = q.join(PriceFieldItem.TYPE);
		@SuppressWarnings("deprecation") // OK testing deprecated api
		final PriceFunction b = f.bind(j);

		assertEquals("p1."+s, b.toString());

		assertEquals(      s+" is null"    , f.isNull()    .toString());
		assertEquals("p1."+s+" is null"    , b.isNull()    .toString());
		assertEquals("p1."+s+" is null"    , f.isNull().bind(j).toString());
		assertEquals(      s+" is not null", f.isNotNull() .toString());
		assertEquals("p1."+s+" is not null", b.isNotNull() .toString());
		assertEquals("p1."+s+" is not null", f.isNotNull().bind(j).toString());
		assertEquals(      s+" is null"    , f.equal(vN)   .toString());
		assertEquals("p1."+s+" is null"    , b.equal(vN)   .toString());
		assertEquals("p1."+s+" is null"    , f.equal(vN).bind(j).toString());
		assertEquals(      s+" is not null", f.notEqual(vN).toString());
		assertEquals("p1."+s+" is not null", b.notEqual(vN).toString());
		assertEquals("p1."+s+" is not null", f.notEqual(vN).bind(j).toString());
		assertEquals(      s+"='111'"      , f.equal(v1)   .toString());
		assertEquals("p1."+s+"='111'"      , b.equal(v1)   .toString());
		assertEquals("p1."+s+"='111'"      , f.equal(v1).bind(j).toString());
		assertEquals(      s+"<>'111'"     , f.notEqual(v1).toString());
		assertEquals("p1."+s+"<>'111'"     , b.notEqual(v1).toString());
		assertEquals("p1."+s+"<>'111'"     , f.notEqual(v1).bind(j).toString());

		assertEquals(      s+"<'222'" , f.less          (v2).toString());
		assertEquals("p1."+s+"<'222'" , b.less          (v2).toString());
		assertEquals("p1."+s+"<'222'" , f.less          (v2).bind(j).toString());
		assertEquals(      s+"<='222'", f.lessOrEqual   (v2).toString());
		assertEquals("p1."+s+"<='222'", b.lessOrEqual   (v2).toString());
		assertEquals("p1."+s+"<='222'", f.lessOrEqual   (v2).bind(j).toString());
		assertEquals(      s+">='222'", f.greaterOrEqual(v2).toString());
		assertEquals("p1."+s+">='222'", b.greaterOrEqual(v2).toString());
		assertEquals("p1."+s+">='222'", f.greaterOrEqual(v2).bind(j).toString());
		assertEquals(      s+">'222'" , f.greater       (v2).toString());
		assertEquals("p1."+s+">'222'" , b.greater       (v2).toString());
		assertEquals("p1."+s+">'222'" , f.greater       (v2).bind(j).toString());
		assertEquals("("   +s+">='222' and "   +s+"<='333')", f.between(v2, v3).toString());
		assertEquals("(p1."+s+">='222' and p1."+s+"<='333')", b.between(v2, v3).toString());
		assertEquals("(p1."+s+">='222' and p1."+s+"<='333')", f.between(v2, v3).bind(j).toString());
	}

	@SuppressWarnings("deprecation") // OK testing deprecated api
	@Test void testJoinNull()
	{
		assertFails(
				() -> f.bind(null),
				NullPointerException.class, "join");
	}

	@SuppressWarnings("unused") // OK: initializes types
	private static final Model MODEL = PriceFieldModelTest.MODEL;
}
