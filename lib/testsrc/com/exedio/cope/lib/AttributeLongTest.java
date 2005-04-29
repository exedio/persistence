/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
package com.exedio.cope.lib;


public class AttributeLongTest extends AttributeTest
{
	public void testSomeLong()
	{
		assertEquals(item.TYPE, item.someLong.getType());
		assertEquals(null, item.getSomeLong());
		assertContains(item, item2, item.TYPE.search(Cope.equal(item.someLong, null)));
		assertContains(item, item2, item.TYPE.search(Cope.isNull(item.someLong)));
		assertContains(item.TYPE.search(Cope.notEqual(item.someLong, null)));
		assertContains(item.TYPE.search(Cope.isNotNull(item.someLong)));

		item.setSomeLong(new Long(11));
		assertEquals(new Long(11), item.getSomeLong());

		item.passivateItem();
		assertEquals(new Long(11), item.getSomeLong());
		assertEquals(
			list(item),
			item.TYPE.search(Cope.equal(item.someLong, 11)));
		assertEquals(
			list(item2),
			item.TYPE.search(Cope.notEqual(item.someLong, 11)));

		assertEquals(list(item2), item.TYPE.search(Cope.equal(item.someLong, null)));
		assertEquals(list(item2), item.TYPE.search(Cope.isNull(item.someLong)));
		assertEquals(list(item), item.TYPE.search(Cope.notEqual(item.someLong, null)));
		assertEquals(list(item), item.TYPE.search(Cope.isNotNull(item.someLong)));

		assertContains(new Long(11), null, search(item.someLong));
		assertContains(new Long(11), search(item.someLong, Cope.equal(item.someLong, new Long(11))));

		item.setSomeLong(null);
		assertEquals(null, item.getSomeLong());
		
		item.passivateItem();
		assertEquals(null, item.getSomeLong());
	}

	public void testSomeNotNullLong()
	{
		assertEquals(item.TYPE, item.someNotNullLong.getType());
		assertEquals(6l, item.getSomeNotNullLong());
		item.setSomeNotNullLong(21l);
		assertEquals(21l, item.getSomeNotNullLong());

		item.setSomeNotNullLong(0l);
		assertEquals(0l, item.getSomeNotNullLong());

		item.passivateItem();
		assertEquals(0l, item.getSomeNotNullLong());
		assertContains(item,
			item.TYPE.search(Cope.equal(item.someNotNullLong, 0l)));

		item.setSomeNotNullLong(Long.MIN_VALUE);
		assertEquals(Long.MIN_VALUE, item.getSomeNotNullLong());

		item.passivateItem();
		assertEquals(Long.MIN_VALUE, item.getSomeNotNullLong());
		assertContains(item,
			item.TYPE.search(Cope.equal(item.someNotNullLong, Long.MIN_VALUE)));

		item.setSomeNotNullLong(Long.MAX_VALUE);
		assertEquals(Long.MAX_VALUE, item.getSomeNotNullLong());

		item.passivateItem();
		assertEquals(Long.MAX_VALUE, item.getSomeNotNullLong());
		assertContains(item,
			item.TYPE.search(Cope.equal(item.someNotNullLong, Long.MAX_VALUE)));
	}
}
