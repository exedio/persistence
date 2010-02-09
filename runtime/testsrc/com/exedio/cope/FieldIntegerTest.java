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


public class FieldIntegerTest extends FieldTest
{
	static final Integer CONST = 1;
	
	public void testSomeInteger()
	{
		assertEquals(item.TYPE, item.someInteger.getType());
		assertEquals(Integer.class, item.someInteger.getValueClass());
		assertSerializedSame(item.someInteger, 380);

		assertEquals(null, item.getSomeInteger());
		assertContains(item, item2, item.TYPE.search(item.someInteger.equal((Integer)null)));
		assertContains(item, item2, item.TYPE.search(item.someInteger.isNull()));
		assertContains(item.TYPE.search(item.someInteger.notEqual((Integer)null)));
		assertContains(item.TYPE.search(item.someInteger.isNotNull()));

		item.someInteger.set(item, Integer.valueOf(14));
		assertEquals(Integer.valueOf(14), item.getSomeInteger());

		item.someInteger.set(item, 12);
		assertEquals(Integer.valueOf(12), item.getSomeInteger());

		item.setSomeInteger(Integer.valueOf(10));
		assertEquals(Integer.valueOf(10), item.getSomeInteger());
		assertEquals(Integer.valueOf(10), item.someInteger.get(item));
		try
		{
			item.someInteger.getMandatory(item);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("field "+item.someInteger+" is not mandatory", e.getMessage());
		}

		restartTransaction();
		assertEquals(Integer.valueOf(10), item.getSomeInteger());
		assertEquals(
			list(item),
			item.TYPE.search(item.someInteger.equal(10)));
		assertEquals(
			list(),
			item.TYPE.search(item.someInteger.notEqual(10)));
		assertEquals(list(item2), item.TYPE.search(item.someInteger.equal((Integer)null)));
		assertEquals(list(item2), item.TYPE.search(item.someInteger.isNull()));
		assertEquals(list(item), item.TYPE.search(item.someInteger.notEqual((Integer)null)));
		assertEquals(list(item), item.TYPE.search(item.someInteger.isNotNull()));

		assertContains(Integer.valueOf(10), null, search(item.someInteger));
		assertContains(Integer.valueOf(10), search(item.someInteger, item.someInteger.equal(Integer.valueOf(10))));

		item.setSomeInteger(null);
		assertEquals(null, item.getSomeInteger());
		
		restartTransaction();
		assertEquals(null, item.getSomeInteger());
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)item.someInteger, Long.valueOf(10l));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + Integer.class.getName() + ", but was a " + Long.class.getName() + " for " + item.someInteger + '.', e.getMessage());
		}
	}
	
	public void testSomeNotNullInteger()
	{
		assertEquals(item.TYPE, item.someNotNullInteger.getType());
		assertEquals(5, item.getSomeNotNullInteger());
		
		item.someNotNullInteger.set(item, Integer.valueOf(24));
		assertEquals(24, item.getSomeNotNullInteger());
		
		item.someNotNullInteger.set(item, 22);
		assertEquals(22, item.getSomeNotNullInteger());

		item.setSomeNotNullInteger(20);
		assertEquals(20, item.getSomeNotNullInteger());
		assertEquals(Integer.valueOf(20), item.someNotNullInteger.get(item));
		assertEquals(20, item.someNotNullInteger.getMandatory(item));

		item.setSomeNotNullInteger(0);
		assertEquals(0, item.getSomeNotNullInteger());

		restartTransaction();
		assertEquals(0, item.getSomeNotNullInteger());
		assertContains(item,
			item.TYPE.search(item.someNotNullInteger.equal(0)));

		item.setSomeNotNullInteger(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, item.getSomeNotNullInteger());

		restartTransaction();
		assertEquals(Integer.MIN_VALUE, item.getSomeNotNullInteger());
		assertContains(item,
			item.TYPE.search(item.someNotNullInteger.equal(Integer.MIN_VALUE)));

		item.setSomeNotNullInteger(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, item.getSomeNotNullInteger());

		restartTransaction();
		assertEquals(Integer.MAX_VALUE, item.getSomeNotNullInteger());
		assertContains(item,
			item.TYPE.search(item.someNotNullInteger.equal(Integer.MAX_VALUE)));
	}
}
