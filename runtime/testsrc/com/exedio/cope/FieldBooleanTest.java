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
import static com.exedio.cope.testmodel.AttributeItem.someBoolean;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullBoolean;

public class FieldBooleanTest extends FieldTest
{
	public void testSomeBoolean()
	{
		assertEquals(TYPE, someBoolean.getType());
		assertEquals(Boolean.class, someBoolean.getValueClass());
		assertSerializedSame(someBoolean, 380);

		assertEquals(null, item.getSomeBoolean());
		assertContains(item, item2, TYPE.search(someBoolean.equal((Boolean)null)));
		assertContains(item, item2, TYPE.search(someBoolean.isNull()));
		assertContains(TYPE.search(someBoolean.notEqual((Boolean)null)));
		assertContains(TYPE.search(someBoolean.isNotNull()));

		someBoolean.set(item, Boolean.TRUE);
		assertEquals(Boolean.TRUE, item.getSomeBoolean());

		someBoolean.set(item, false);
		assertEquals(Boolean.FALSE, item.getSomeBoolean());

		item.setSomeBoolean(Boolean.TRUE);
		assertEquals(Boolean.TRUE, item.getSomeBoolean());
		assertEquals(Boolean.TRUE, someBoolean.get(item));
		try
		{
			someBoolean.getMandatory(item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field "+someBoolean+" is not mandatory", e.getMessage());
		}
		assertContains(item, TYPE.search(someBoolean.equal(true)));
		assertContains(item2, TYPE.search(someBoolean.isNull()));
		assertContains(TYPE.search(someBoolean.notEqual(true)));
		assertContains(item, TYPE.search(someBoolean.isNotNull()));

		item.setSomeBoolean(Boolean.FALSE);
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertContains(item, TYPE.search(someBoolean.equal(false)));
		assertContains(item2, TYPE.search(someBoolean.isNull()));
		assertContains(TYPE.search(someBoolean.notEqual(false)));
		assertContains(item, TYPE.search(someBoolean.isNotNull()));

		assertContains(Boolean.FALSE, null, search(someBoolean));
		assertContains(Boolean.FALSE, search(someBoolean, someBoolean.equal(false)));

		restartTransaction();
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertContains(item, TYPE.search(someBoolean.equal(false)));
		assertContains(item2, TYPE.search(someBoolean.isNull()));
		assertContains(TYPE.search(someBoolean.notEqual(false)));
		assertContains(item, TYPE.search(someBoolean.isNotNull()));

		item.setSomeBoolean(null);
		assertEquals(null, item.getSomeBoolean());
		assertContains(item, item2, TYPE.search(someBoolean.equal((Boolean)null)));
		assertContains(item, item2, TYPE.search(someBoolean.isNull()));
		assertContains(TYPE.search(someBoolean.notEqual((Boolean)null)));
		assertContains(TYPE.search(someBoolean.isNotNull()));
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)someBoolean, Integer.valueOf(10));
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + Boolean.class.getName() + ", but was a " + Integer.class.getName() + " for " + someBoolean + '.', e.getMessage());
		}
	}

	public void testSomeNotNullBoolean()
	{
		assertEquals(TYPE, someNotNullBoolean.getType());
		assertEquals(true, item.getSomeNotNullBoolean());
		assertContains(item, TYPE.search(someNotNullBoolean.equal(true)));
		assertContains(TYPE.search(someNotNullBoolean.isNull()));
		assertContains(item, TYPE.search(someNotNullBoolean.notEqual(false)));
		assertContains(item, item2, TYPE.search(someNotNullBoolean.isNotNull()));

		someNotNullBoolean.set(item, Boolean.FALSE);
		assertEquals(false, item.getSomeNotNullBoolean());

		someNotNullBoolean.set(item, true);
		assertEquals(true, item.getSomeNotNullBoolean());

		item.setSomeNotNullBoolean(false);
		assertEquals(false, item.getSomeNotNullBoolean());
		assertEquals(Boolean.FALSE, someNotNullBoolean.get(item));
		assertEquals(false, someNotNullBoolean.getMandatory(item));
		assertContains(TYPE.search(someNotNullBoolean.equal(true)));
		assertContains(TYPE.search(someNotNullBoolean.isNull()));
		assertContains(TYPE.search(someNotNullBoolean.notEqual(false)));
		assertContains(item, item2, TYPE.search(someNotNullBoolean.isNotNull()));
	}
}
