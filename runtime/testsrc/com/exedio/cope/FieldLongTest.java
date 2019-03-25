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
import static com.exedio.cope.testmodel.AttributeItem.someLong;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullLong;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FieldLongTest extends FieldTest
{
	@Test void testSomeLong()
	{
		assertEquals(TYPE, someLong.getType());
		assertEquals(Long.class, someLong.getValueClass());
		assertSerializedSame(someLong, 377);

		assertEquals(null, item.getSomeLong());
		assertContains(item, item2, TYPE.search(someLong.equal((Long)null)));
		assertContains(item, item2, TYPE.search(someLong.isNull()));
		assertContains(TYPE.search(someLong.notEqual((Long)null)));
		assertContains(TYPE.search(someLong.isNotNull()));

		someLong.set(item, Long.valueOf(22));
		assertEquals(Long.valueOf(22), item.getSomeLong());

		someLong.set(item, 22l);
		assertEquals(Long.valueOf(22), item.getSomeLong());

		item.setSomeLong(Long.valueOf(11));
		assertEquals(Long.valueOf(11), item.getSomeLong());
		assertEquals(Long.valueOf(11), someLong.get(item));
		try
		{
			someLong.getMandatory(item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field "+someLong+" is not mandatory", e.getMessage());
		}

		restartTransaction();
		assertEquals(Long.valueOf(11), item.getSomeLong());
		assertEquals(
			list(item),
			TYPE.search(someLong.equal(11l)));
		assertEquals(
			list(),
			TYPE.search(someLong.notEqual(11l)));

		assertEquals(list(item2), TYPE.search(someLong.equal((Long)null)));
		assertEquals(list(item2), TYPE.search(someLong.isNull()));
		assertEquals(list(item), TYPE.search(someLong.notEqual((Long)null)));
		assertEquals(list(item), TYPE.search(someLong.isNotNull()));

		assertContains(Long.valueOf(11), null, search(someLong));
		assertContains(Long.valueOf(11), search(someLong, someLong.equal(Long.valueOf(11))));

		item.setSomeLong(null);
		assertEquals(null, item.getSomeLong());

		restartTransaction();
		assertEquals(null, item.getSomeLong());
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	@Test void testUnchecked()
	{
		try
		{
			item.set((FunctionField)someLong, Integer.valueOf(10));
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + Long.class.getName() + ", but was a " + Integer.class.getName() + " for " + someLong + '.', e.getMessage());
		}
	}

	@Test void testSomeNotNullLong()
	{
		assertEquals(TYPE, someNotNullLong.getType());
		assertEquals(6l, item.getSomeNotNullLong());

		someNotNullLong.set(item, 27l);
		assertEquals(27l, item.getSomeNotNullLong());

		someNotNullLong.set(item, Long.valueOf(24));
		assertEquals(24l, item.getSomeNotNullLong());

		item.setSomeNotNullLong(21l);
		assertEquals(21l, item.getSomeNotNullLong());
		assertEquals(Long.valueOf(21), someNotNullLong.get(item));
		assertEquals(21l, someNotNullLong.getMandatory(item));

		item.setSomeNotNullLong(0l);
		assertEquals(0l, item.getSomeNotNullLong());

		restartTransaction();
		assertEquals(0l, item.getSomeNotNullLong());
		assertContains(item,
			TYPE.search(someNotNullLong.equal(0l)));

		item.setSomeNotNullLong(Long.MIN_VALUE);
		assertEquals(Long.MIN_VALUE, item.getSomeNotNullLong());

		restartTransaction();
		assertEquals(Long.MIN_VALUE, item.getSomeNotNullLong());
		assertContains(item,
			TYPE.search(someNotNullLong.equal(Long.MIN_VALUE)));

		item.setSomeNotNullLong(Long.MAX_VALUE);
		assertEquals(Long.MAX_VALUE, item.getSomeNotNullLong());

		restartTransaction();
		assertEquals(Long.MAX_VALUE, item.getSomeNotNullLong());
		assertContains(item,
			TYPE.search(someNotNullLong.equal(Long.MAX_VALUE)));
	}
}
