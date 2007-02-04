/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.Date;


public class FieldDoubleTest extends FieldTest
{
	static final Double CONST = 1.1;

	public void testSomeDouble()
	{
		assertEquals(item.TYPE, item.someDouble.getType());
		assertEquals(Date.class, item.someDate.getValueClass());

		assertEquals(null, item.getSomeDouble());
		assertContains(item, item2, item.TYPE.search(item.someDouble.equal((Double)null)));
		assertContains(item, item2, item.TYPE.search(item.someDouble.isNull()));
		assertContains(item.TYPE.search(item.someDouble.notEqual((Double)null)));
		assertContains(item.TYPE.search(item.someDouble.isNotNull()));

		item.someDouble.set(item, new Double(44.44));
		assertEquals(new Double(44.44), item.getSomeDouble());

		item.someDouble.set(item, 33.33);
		assertEquals(new Double(33.33), item.getSomeDouble());

		item.setSomeDouble(new Double(22.22));
		assertEquals(new Double(22.22), item.getSomeDouble());
		assertEquals(new Double(22.22), item.someDouble.get(item));
		try
		{
			item.someDouble.getMandatory(item);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("field "+item.someDouble+" is not mandatory", e.getMessage());
		}

		restartTransaction();
		assertEquals(new Double(22.22), item.getSomeDouble());
		assertEquals(
			list(item),
			item.TYPE.search(item.someDouble.equal(22.22)));
		assertEquals(
			list(),
			item.TYPE.search(item.someDouble.notEqual(22.22)));
		assertEquals(list(item2), item.TYPE.search(item.someDouble.equal((Double)null)));
		assertEquals(list(item2), item.TYPE.search(item.someDouble.isNull()));
		assertEquals(list(item), item.TYPE.search(item.someDouble.notEqual((Double)null)));
		assertEquals(list(item), item.TYPE.search(item.someDouble.isNotNull()));

		assertContains(new Double(22.22), null, search(item.someDouble));
		assertContains(new Double(22.22), search(item.someDouble, item.someDouble.equal(new Double(22.22))));

		item.setSomeDouble(null);
		assertEquals(null, item.getSomeDouble());

		restartTransaction();
		assertEquals(null, item.getSomeDouble());
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)item.someDouble, Integer.valueOf(10));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + Double.class.getName() + ", but was a " + Integer.class.getName() + " for " + item.someDouble + '.', e.getMessage());
		}
	}

	public void testSomeNotNullDouble()
	{
		assertEquals(item.TYPE, item.someNotNullDouble.getType());
		assertEquals(2.2, item.getSomeNotNullDouble(), 0.0);

		item.setSomeNotNullDouble(2.5);
		assertEquals(2.5, item.getSomeNotNullDouble(), 0.0);
		assertEquals(new Double(2.5), item.someNotNullDouble.get(item));
		assertEquals(2.5, item.someNotNullDouble.getMandatory(item), 0.0);
		
		item.someNotNullDouble.set(item, 2.9);
		assertEquals(2.9, item.getSomeNotNullDouble(), 0.0);

		item.someNotNullDouble.set(item, new Double(3.1));
		assertEquals(3.1, item.getSomeNotNullDouble(), 0.0);

		item.setSomeNotNullDouble(0.0);
		assertEquals(0.0, item.getSomeNotNullDouble(), 0.0);

		restartTransaction();
		assertEquals(0.0, item.getSomeNotNullDouble(), 0.0);
		assertContains(item,
			item.TYPE.search(item.someNotNullDouble.equal(0.0)));

		// TODO: test with extreme values
		/*item.setSomeNotNullDouble(Double.MIN_VALUE);
		// TODO: passivate
		assertEquals(Double.MIN_VALUE, item.getSomeNotNullDouble(), 0.0);
		assertContains(
			item,
			Search.search(
				item.TYPE,
				Search.equal(item.someNotNullDouble, Double.MIN_VALUE))));

		item.setSomeNotNullDouble(Double.MAX_VALUE);
		// TODO: passivate
		assertEquals(Double.MAX_VALUE, item.getSomeNotNullDouble(), 0.0);
		assertEquals(
			item,
			Search.search(
				item.TYPE,
				Search.equal(item.someNotNullDouble, Double.MAX_VALUE))));*/
	}
}
