/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
	static final Long CONST = 1l;
	
	public void testSomeLong()
	{
		assertEquals(item.TYPE, item.someLong.getType());
		assertEquals(Long.class, item.someLong.getValueClass());

		assertEquals(null, item.getSomeLong());
		assertContains(item, item2, item.TYPE.search(item.someLong.equal((Long)null)));
		assertContains(item, item2, item.TYPE.search(item.someLong.isNull()));
		assertContains(item.TYPE.search(item.someLong.notEqual(null)));
		assertContains(item.TYPE.search(item.someLong.isNotNull()));

		item.someLong.set(item, Long.valueOf(22));
		assertEquals(Long.valueOf(22), item.getSomeLong());
		
		item.someLong.set(item, 22l);
		assertEquals(Long.valueOf(22), item.getSomeLong());
		
		item.setSomeLong(Long.valueOf(11));
		assertEquals(Long.valueOf(11), item.getSomeLong());
		assertEquals(Long.valueOf(11), item.someLong.get(item));
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
		assertEquals(Long.valueOf(11), item.getSomeLong());
		assertEquals(
			list(item),
			item.TYPE.search(item.someLong.equal(11l)));
		assertEquals(
			list(item2),
			item.TYPE.search(item.someLong.notEqual(11l)));

		assertEquals(list(item2), item.TYPE.search(item.someLong.equal((Long)null)));
		assertEquals(list(item2), item.TYPE.search(item.someLong.isNull()));
		assertEquals(list(item), item.TYPE.search(item.someLong.notEqual(null)));
		assertEquals(list(item), item.TYPE.search(item.someLong.isNotNull()));

		assertContains(Long.valueOf(11), null, search(item.someLong));
		assertContains(Long.valueOf(11), search(item.someLong, item.someLong.equal(Long.valueOf(11))));

		item.setSomeLong(null);
		assertEquals(null, item.getSomeLong());
		
		restartTransaction();
		assertEquals(null, item.getSomeLong());
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)item.someLong, Integer.valueOf(10));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + Long.class.getName() + ", but was a " + Integer.class.getName(), e.getMessage());
		}
	}
	
	public void testSomeNotNullLong()
	{
		assertEquals(item.TYPE, item.someNotNullLong.getType());
		assertEquals(6l, item.getSomeNotNullLong());

		item.someNotNullLong.set(item, 27l);
		assertEquals(27l, item.getSomeNotNullLong());

		item.someNotNullLong.set(item, Long.valueOf(24));
		assertEquals(24l, item.getSomeNotNullLong());

		item.setSomeNotNullLong(21l);
		assertEquals(21l, item.getSomeNotNullLong());
		assertEquals(Long.valueOf(21), item.someNotNullLong.get(item));
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
