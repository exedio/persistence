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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.testmodel.AttributeItem.TYPE;
import static com.exedio.cope.testmodel.AttributeItem.someDouble;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullDouble;
import static java.lang.Double.valueOf;

public class FieldDoubleTest extends FieldTest
{
	static final Double CONST = 1.1;

	@Test public void testSomeDouble()
	{
		assertEquals(TYPE, someDouble.getType());
		assertEquals(Double.class, someDouble.getValueClass());
		assertSerializedSame(someDouble, 379);

		assertEquals(null, item.getSomeDouble());
		assertContains(item, item2, TYPE.search(someDouble.equal((Double)null)));
		assertContains(item, item2, TYPE.search(someDouble.isNull()));
		assertContains(TYPE.search(someDouble.notEqual((Double)null)));
		assertContains(TYPE.search(someDouble.isNotNull()));

		someDouble.set(item, valueOf(44.44));
		assertEquals(valueOf(44.44), item.getSomeDouble());

		someDouble.set(item, 33.33);
		assertEquals(valueOf(33.33), item.getSomeDouble());

		item.setSomeDouble(valueOf(22.22));
		assertEquals(valueOf(22.22), item.getSomeDouble());
		assertEquals(valueOf(22.22), someDouble.get(item));
		try
		{
			someDouble.getMandatory(item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field "+someDouble+" is not mandatory", e.getMessage());
		}

		restartTransaction();
		assertEquals(valueOf(22.22), item.getSomeDouble());
		assertEquals(
			list(item),
			TYPE.search(someDouble.equal(22.22)));
		assertEquals(
			list(),
			TYPE.search(someDouble.notEqual(22.22)));
		assertEquals(list(item2), TYPE.search(someDouble.equal((Double)null)));
		assertEquals(list(item2), TYPE.search(someDouble.isNull()));
		assertEquals(list(item), TYPE.search(someDouble.notEqual((Double)null)));
		assertEquals(list(item), TYPE.search(someDouble.isNotNull()));

		assertContains(valueOf(22.22), null, search(someDouble));
		assertContains(valueOf(22.22), search(someDouble, someDouble.equal(valueOf(22.22))));

		item.setSomeDouble(null);
		assertEquals(null, item.getSomeDouble());

		restartTransaction();
		assertEquals(null, item.getSomeDouble());
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	@Test public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)someDouble, Integer.valueOf(10));
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + Double.class.getName() + ", but was a " + Integer.class.getName() + " for " + someDouble + '.', e.getMessage());
		}
	}

	@Test public void testSomeNotNullDouble()
	{
		assertEquals(TYPE, someNotNullDouble.getType());
		assertEquals(2.2, item.getSomeNotNullDouble(), 0.0);

		item.setSomeNotNullDouble(2.5);
		assertEquals(2.5, item.getSomeNotNullDouble(), 0.0);
		assertEquals(valueOf(2.5), someNotNullDouble.get(item));
		assertEquals(2.5, someNotNullDouble.getMandatory(item), 0.0);

		someNotNullDouble.set(item, 2.9);
		assertEquals(2.9, item.getSomeNotNullDouble(), 0.0);

		someNotNullDouble.set(item, valueOf(3.1));
		assertEquals(3.1, item.getSomeNotNullDouble(), 0.0);

		item.setSomeNotNullDouble(0.0);
		assertEquals(0.0, item.getSomeNotNullDouble(), 0.0);

		restartTransaction();
		assertEquals(0.0, item.getSomeNotNullDouble(), 0.0);
		assertContains(item,
			TYPE.search(someNotNullDouble.equal(0.0)));

		// TODO: test with extreme values
		/*item.setSomeNotNullDouble(Double.MIN_VALUE);
		// TODO: passivate
		assertEquals(Double.MIN_VALUE, item.getSomeNotNullDouble(), 0.0);
		assertContains(
			item,
			Search.search(
				TYPE,
				Search.equal(someNotNullDouble, Double.MIN_VALUE))));

		item.setSomeNotNullDouble(Double.MAX_VALUE);
		// TODO: passivate
		assertEquals(Double.MAX_VALUE, item.getSomeNotNullDouble(), 0.0);
		assertEquals(
			item,
			Search.search(
				TYPE,
				Search.equal(someNotNullDouble, Double.MAX_VALUE))));*/
	}
}
