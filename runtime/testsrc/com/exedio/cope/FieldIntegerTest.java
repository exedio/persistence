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
import static com.exedio.cope.testmodel.AttributeItem.someInteger;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullInteger;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

public class FieldIntegerTest extends FieldTest
{
	static final Integer CONST = 1;

	@Test public void testSomeInteger()
	{
		assertEquals(TYPE, someInteger.getType());
		assertEquals(Integer.class, someInteger.getValueClass());
		assertSerializedSame(someInteger, 380);

		assertEquals(null, item.getSomeInteger());
		assertContains(item, item2, TYPE.search(someInteger.equal((Integer)null)));
		assertContains(item, item2, TYPE.search(someInteger.isNull()));
		assertContains(TYPE.search(someInteger.notEqual((Integer)null)));
		assertContains(TYPE.search(someInteger.isNotNull()));

		someInteger.set(item, Integer.valueOf(14));
		assertEquals(Integer.valueOf(14), item.getSomeInteger());

		someInteger.set(item, 12);
		assertEquals(Integer.valueOf(12), item.getSomeInteger());

		item.setSomeInteger(Integer.valueOf(10));
		assertEquals(Integer.valueOf(10), item.getSomeInteger());
		assertEquals(Integer.valueOf(10), someInteger.get(item));
		try
		{
			someInteger.getMandatory(item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field "+someInteger+" is not mandatory", e.getMessage());
		}

		restartTransaction();
		assertEquals(Integer.valueOf(10), item.getSomeInteger());
		assertEquals(
			list(item),
			TYPE.search(someInteger.equal(10)));
		assertEquals(
			list(),
			TYPE.search(someInteger.notEqual(10)));
		assertEquals(list(item2), TYPE.search(someInteger.equal((Integer)null)));
		assertEquals(list(item2), TYPE.search(someInteger.isNull()));
		assertEquals(list(item), TYPE.search(someInteger.notEqual((Integer)null)));
		assertEquals(list(item), TYPE.search(someInteger.isNotNull()));

		assertContains(Integer.valueOf(10), null, search(someInteger));
		assertContains(Integer.valueOf(10), search(someInteger, someInteger.equal(Integer.valueOf(10))));

		item.setSomeInteger(null);
		assertEquals(null, item.getSomeInteger());

		restartTransaction();
		assertEquals(null, item.getSomeInteger());
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	@Test public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)someInteger, Long.valueOf(10l));
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + Integer.class.getName() + ", but was a " + Long.class.getName() + " for " + someInteger + '.', e.getMessage());
		}
	}

	@Test public void testSomeNotNullInteger()
	{
		assertEquals(TYPE, someNotNullInteger.getType());
		assertEquals(5, item.getSomeNotNullInteger());

		someNotNullInteger.set(item, Integer.valueOf(24));
		assertEquals(24, item.getSomeNotNullInteger());

		someNotNullInteger.set(item, 22);
		assertEquals(22, item.getSomeNotNullInteger());

		item.setSomeNotNullInteger(20);
		assertEquals(20, item.getSomeNotNullInteger());
		assertEquals(Integer.valueOf(20), someNotNullInteger.get(item));
		assertEquals(20, someNotNullInteger.getMandatory(item));

		item.setSomeNotNullInteger(0);
		assertEquals(0, item.getSomeNotNullInteger());

		restartTransaction();
		assertEquals(0, item.getSomeNotNullInteger());
		assertContains(item,
			TYPE.search(someNotNullInteger.equal(0)));

		item.setSomeNotNullInteger(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, item.getSomeNotNullInteger());

		restartTransaction();
		assertEquals(Integer.MIN_VALUE, item.getSomeNotNullInteger());
		assertContains(item,
			TYPE.search(someNotNullInteger.equal(Integer.MIN_VALUE)));

		item.setSomeNotNullInteger(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, item.getSomeNotNullInteger());

		restartTransaction();
		assertEquals(Integer.MAX_VALUE, item.getSomeNotNullInteger());
		assertContains(item,
			TYPE.search(someNotNullInteger.equal(Integer.MAX_VALUE)));
	}
}
