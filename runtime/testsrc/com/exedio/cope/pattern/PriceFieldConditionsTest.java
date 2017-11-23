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
import static com.exedio.cope.pattern.PriceFieldItem.finalPrice;
import static com.exedio.cope.pattern.PriceFieldItem.optionalPrice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.Condition;
import com.exedio.cope.Join;
import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PriceFieldConditionsTest extends TestWithEnvironment
{
	public PriceFieldConditionsTest()
	{
		super(PriceFieldModelTest.MODEL);
	}

	private static final Price
				pN = null,
				p1 = storeOf(111),
				p2 = storeOf(222),
				p3 = storeOf(333);
	private PriceFieldItem iN, i1, i2, i3;

	@BeforeEach public final void setUp()
	{
		iN  = PriceFieldItem.n(pN, p2);
		i1  = PriceFieldItem.n(p1, p2);
		i2  = PriceFieldItem.n(p2, p2);
		i3  = PriceFieldItem.n(p3, p2);
	}

	@Test void testSearch()
	{
		final PriceField f = optionalPrice;
		final PriceField f2 = finalPrice;

		assertIt(f.isNull(),       iN             );
		assertIt(f.isNotNull(),        i1, i2, i3 );
		assertIt(f.equal(pN),      iN             );
		assertIt(f.notEqual(pN),       i1, i2, i3 );
		assertIt(f.equal(p1),          i1         );
		assertIt(f.notEqual(p1),           i2, i3 );

		assertIt(f.less          (p2), i1         );
		assertIt(f.lessOrEqual   (p2), i1, i2     );
		assertIt(f.greaterOrEqual(p2),     i2, i3 );
		assertIt(f.greater       (p2),         i3 );
		assertIt(f.between   (p2, p3),     i2, i3 );
		assertIt(f.between   (p1, p2), i1, i2     );

		assertIt(f.equal         (f2),      i2    );
		assertIt(f.notEqual      (f2), i1,     i3 );
		assertIt(f.less          (f2), i1         );
		assertIt(f.lessOrEqual   (f2), i1, i2     );
		assertIt(f.greaterOrEqual(f2),     i2, i3 );
		assertIt(f.greater       (f2),         i3 );
	}

	private static void assertIt(final Condition condition, final PriceFieldItem... expected)
	{
		final Query<PriceFieldItem> query = PriceFieldItem.TYPE.newQuery(condition);
		query.setOrderBy(PriceFieldItem.TYPE.getThis(), true);
		assertEquals(Arrays.asList(expected), query.search());
	}

	@Test void testCondition()
	{
		final PriceField f = optionalPrice;
		final String s = f.getInt().getID();

		assertEquals(s+" is null"    , f.isNull()    .toString());
		assertEquals(s+" is not null", f.isNotNull() .toString());
		assertEquals(s+" is null"    , f.equal(pN)   .toString());
		assertEquals(s+" is not null", f.notEqual(pN).toString());
		assertEquals(s+"='111'"      , f.equal(p1)   .toString());
		assertEquals(s+"<>'111'"     , f.notEqual(p1).toString());

		assertEquals(s+"<'222'" , f.less          (p2).toString());
		assertEquals(s+"<='222'", f.lessOrEqual   (p2).toString());
		assertEquals(s+">='222'", f.greaterOrEqual(p2).toString());
		assertEquals(s+">'222'" , f.greater       (p2).toString());
		assertEquals("("+s+">='222' AND "+s+"<='333')", f.between(p2, p3).toString());

		final Query<?> q = PriceFieldItem.TYPE.newQuery();
		final Join j = q.join(PriceFieldItem.TYPE);
		assertEquals("p1."+s, f.bind(j).toString());

		assertEquals("p1."+s+" is null"    , f.bind(j).isNull()    .toString());
		assertEquals("p1."+s+" is not null", f.bind(j).isNotNull() .toString());
		assertEquals("p1."+s+" is null"    , f.bind(j).equal(pN)   .toString());
		assertEquals("p1."+s+" is not null", f.bind(j).notEqual(pN).toString());
		assertEquals("p1."+s+"='111'"      , f.bind(j).equal(p1)   .toString());
		assertEquals("p1."+s+"<>'111'"     , f.bind(j).notEqual(p1).toString());

		assertEquals("p1."+s+"<'222'" , f.bind(j).less          (p2).toString());
		assertEquals("p1."+s+"<='222'", f.bind(j).lessOrEqual   (p2).toString());
		assertEquals("p1."+s+">='222'", f.bind(j).greaterOrEqual(p2).toString());
		assertEquals("p1."+s+">'222'" , f.bind(j).greater       (p2).toString());
		assertEquals("(p1."+s+">='222' AND p1."+s+"<='333')", f.bind(j).between(p2, p3).toString());

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
}
