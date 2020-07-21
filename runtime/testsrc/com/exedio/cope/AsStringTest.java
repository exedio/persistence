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

import static com.exedio.cope.AsStringItem.TYPE;
import static com.exedio.cope.AsStringItem.doublex;
import static com.exedio.cope.AsStringItem.intx;
import static com.exedio.cope.AsStringItem.longx;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AsStringTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public AsStringTest()
	{
		super(MODEL);
	}

	AsStringItem item1, item10, item2, itemN, item0, itemX;

	@BeforeEach final void setUp()
	{
		item1 = new AsStringItem( 13,  15l,  1.9);
		item10= new AsStringItem( 13,  15l,  0.9);
		item2 = new AsStringItem( 23,  25l, 29.0);
		itemN = new AsStringItem(-33, -35l, -3.9);
		item0 = new AsStringItem(  0,   0l,  0.0);
		itemX = new AsStringItem(null, null, null);
	}

	@Test void testIt()
	{
		assertEquals("13",   intx   .asString().get(item1));
		assertEquals("15",   longx  .asString().get(item1));
		assertEquals( "1.9", doublex.asString().get(item1));
		assertEquals( "0.9", doublex.asString().get(item10));

		assertEquals("23",   intx   .asString().get(item2));
		assertEquals("25",   longx  .asString().get(item2));
		assertEquals("29.0", doublex.asString().get(item2)); // TODO 29

		assertEquals("-33",   intx   .asString().get(itemN));
		assertEquals("-35",   longx  .asString().get(itemN));
		assertEquals( "-3.9", doublex.asString().get(itemN));

		assertEquals("0",   intx   .asString().get(item0));
		assertEquals("0",   longx  .asString().get(item0));
		assertEquals("0.0", doublex.asString().get(item0)); // TODO 0

		assertEquals(null, intx   .asString().get(itemX));
		assertEquals(null, longx  .asString().get(itemX));
		assertEquals(null, doublex.asString().get(itemX));

		{
			final Query<List<Object>> q = Query.newQuery(
					new Selectable<?>[]{intx.asString(), longx.asString(), doublex.asString()},
					TYPE, null);
			q.setOrderBy(TYPE.getThis(), true);
			final String p = hsqldb ? "E0" : "";
			final Iterator<List<Object>> i = q.search().iterator();
			assertEquals(list( "13",  "15", postgresql?"2":("1.9"+p)), i.next());
			assertEquals(list( "13",  "15", postgresql?"1":("0.9"+p)), i.next());
			assertEquals(list( "23",  "25", hsqldb?"29.0E0":"29"), i.next());
			assertEquals(list("-33", "-35", postgresql?"-4":("-3.9"+p)), i.next());
			assertEquals(list(  "0",   "0", hsqldb?"0.0E0":"0"), i.next());
			assertEquals(list( null,  null,   null), i.next());
			assertFalse(i.hasNext());
		}

		assertEquals(item2, TYPE.searchSingleton(intx   .asString().equal("23")));
		assertEquals(item2, TYPE.searchSingleton(longx  .asString().equal("25")));
		assertEquals(item2, TYPE.searchSingleton(doublex.asString().equal(hsqldb?"29.0E0":"29")));

		assertEquals(item2, TYPE.searchSingleton(intx   .asString().like("2%")));
		assertEquals(item2, TYPE.searchSingleton(longx  .asString().like("2%")));
		assertEquals(item2, TYPE.searchSingleton(doublex.asString().like(postgresql?"29%":"2%")));

		assertEquals(itemX, TYPE.searchSingleton(intx   .asString().isNull()));
		assertEquals(itemX, TYPE.searchSingleton(longx  .asString().isNull()));
		assertEquals(itemX, TYPE.searchSingleton(doublex.asString().isNull()));
	}
}
