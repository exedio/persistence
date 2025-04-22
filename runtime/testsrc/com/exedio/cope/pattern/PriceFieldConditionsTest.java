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

import com.exedio.cope.Condition;
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

	@BeforeEach final void setUp()
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
		assertIt(f.is   (pN),      iN             );
		assertIt(f.isNot(pN),          i1, i2, i3 );
		assertIt(f.is   (p1),          i1         );
		assertIt(f.isNot(p1),              i2, i3 );

		assertIt(f.less          (p2), i1         );
		assertIt(f.lessOrEqual   (p2), i1, i2     );
		assertIt(f.greaterOrEqual(p2),     i2, i3 );
		assertIt(f.greater       (p2),         i3 );
		assertIt(f.between   (p2, p3),     i2, i3 );
		assertIt(f.between   (p1, p2), i1, i2     );

		assertIt(f.is            (f2),      i2    );
		assertIt(f.isNot         (f2), i1,     i3 );
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
}
