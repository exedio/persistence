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

package com.exedio.cope;


public class AttributeLongTest extends AttributeTest
{
	public void testSomeLong() throws ConstraintViolationException
	{
		assertEquals(item.TYPE, item.someLong.getType());
		assertEquals(null, item.getSomeLong());
		assertContains(item, item2, item.TYPE.search(item.someLong.equal(null)));
		assertContains(item, item2, item.TYPE.search(item.someLong.isNull()));
		assertContains(item.TYPE.search(item.someLong.notEqual(null)));
		assertContains(item.TYPE.search(item.someLong.isNotNull()));

		item.someLong.set(item, new Long(22));
		assertEquals(new Long(22), item.getSomeLong());
		
		item.someLong.set(item, 22l);
		assertEquals(new Long(22), item.getSomeLong());
		
		item.setSomeLong(new Long(11));
		assertEquals(new Long(11), item.getSomeLong());
		assertEquals(new Long(11), item.someLong.get(item));
		try
		{
			item.someLong.getMandatory(item);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("attribute "+item.someLong+" is not mandatory", e.getMessage());
		}

		restartTransaction();
		assertEquals(new Long(11), item.getSomeLong());
		assertEquals(
			list(item),
			item.TYPE.search(item.someLong.equal(11)));
		assertEquals(
			list(item2),
			item.TYPE.search(item.someLong.notEqual(11)));

		assertEquals(list(item2), item.TYPE.search(item.someLong.equal(null)));
		assertEquals(list(item2), item.TYPE.search(item.someLong.isNull()));
		assertEquals(list(item), item.TYPE.search(item.someLong.notEqual(null)));
		assertEquals(list(item), item.TYPE.search(item.someLong.isNotNull()));

		assertContains(new Long(11), null, search(item.someLong));
		assertContains(new Long(11), search(item.someLong, item.someLong.equal(new Long(11))));

		item.setSomeLong(null);
		assertEquals(null, item.getSomeLong());
		
		restartTransaction();
		assertEquals(null, item.getSomeLong());

		try
		{
			item.set(item.someLong, new Integer(10));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected " + Long.class.getName() + ", got " + Integer.class.getName() + " for someLong", e.getMessage());
		}
	}

	public void testSomeNotNullLong() throws ConstraintViolationException
	{
		assertEquals(item.TYPE, item.someNotNullLong.getType());
		assertEquals(6l, item.getSomeNotNullLong());

		item.someNotNullLong.set(item, 27l);
		assertEquals(27l, item.getSomeNotNullLong());

		item.someNotNullLong.set(item, new Long(24));
		assertEquals(24l, item.getSomeNotNullLong());

		item.setSomeNotNullLong(21l);
		assertEquals(21l, item.getSomeNotNullLong());
		assertEquals(new Long(21), item.someNotNullLong.get(item));
		assertEquals(21l, item.someNotNullLong.getMandatory(item));

		item.setSomeNotNullLong(0l);
		assertEquals(0l, item.getSomeNotNullLong());

		restartTransaction();
		assertEquals(0l, item.getSomeNotNullLong());
		assertContains(item,
			item.TYPE.search(item.someNotNullLong.equal(0l)));

		item.setSomeNotNullLong(Long.MIN_VALUE);
		assertEquals(Long.MIN_VALUE, item.getSomeNotNullLong());

		restartTransaction();
		assertEquals(Long.MIN_VALUE, item.getSomeNotNullLong());
		assertContains(item,
			item.TYPE.search(item.someNotNullLong.equal(Long.MIN_VALUE)));

		item.setSomeNotNullLong(Long.MAX_VALUE);
		assertEquals(Long.MAX_VALUE, item.getSomeNotNullLong());

		restartTransaction();
		assertEquals(Long.MAX_VALUE, item.getSomeNotNullLong());
		assertContains(item,
			item.TYPE.search(item.someNotNullLong.equal(Long.MAX_VALUE)));
	}
}
