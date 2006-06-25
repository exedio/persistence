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


public class AttributeBooleanTest extends AttributeTest
{
	public void testSomeBoolean()
	{
		assertEquals(item.TYPE, item.someBoolean.getType());
		assertSame(null, item.someBoolean.cast(null));
		assertSame(Boolean.TRUE, item.someBoolean.cast(Boolean.TRUE));
		try
		{
			item.someBoolean.cast(1);
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + Boolean.class.getName() + ", but was a " + Integer.class.getName(), e.getMessage());
		}
		assertEquals(Boolean.class, item.someBoolean.getValueClass());

		assertEquals(null, item.getSomeBoolean());
		assertContains(item, item2, item.TYPE.search(item.someBoolean.equal((Boolean)null)));
		assertContains(item, item2, item.TYPE.search(item.someBoolean.isNull()));
		assertContains(item.TYPE.search(item.someBoolean.notEqual(null)));
		assertContains(item.TYPE.search(item.someBoolean.isNotNull()));

		item.someBoolean.set(item, Boolean.TRUE);
		assertEquals(Boolean.TRUE, item.getSomeBoolean());
		
		item.someBoolean.set(item, false);
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		
		item.setSomeBoolean(Boolean.TRUE);
		assertEquals(Boolean.TRUE, item.getSomeBoolean());
		assertEquals(Boolean.TRUE, item.someBoolean.get(item));
		try
		{
			item.someBoolean.getMandatory(item);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("attribute "+item.someBoolean+" is not mandatory", e.getMessage());
		}
		assertContains(item, item.TYPE.search(item.someBoolean.equal(true)));
		assertContains(item2, item.TYPE.search(item.someBoolean.isNull()));
		assertContains(item2, item.TYPE.search(item.someBoolean.notEqual(true)));
		assertContains(item, item.TYPE.search(item.someBoolean.isNotNull()));

		item.setSomeBoolean(Boolean.FALSE);
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertContains(item, item.TYPE.search(item.someBoolean.equal(false)));
		assertContains(item2, item.TYPE.search(item.someBoolean.isNull()));
		assertContains(item2, item.TYPE.search(item.someBoolean.notEqual(false)));
		assertContains(item, item.TYPE.search(item.someBoolean.isNotNull()));
		
		assertContains(Boolean.FALSE, null, search(item.someBoolean));
		assertContains(Boolean.FALSE, search(item.someBoolean, item.someBoolean.equal(false)));

		restartTransaction();
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertContains(item, item.TYPE.search(item.someBoolean.equal(false)));
		assertContains(item2, item.TYPE.search(item.someBoolean.isNull()));
		assertContains(item2, item.TYPE.search(item.someBoolean.notEqual(false)));
		assertContains(item, item.TYPE.search(item.someBoolean.isNotNull()));

		item.setSomeBoolean(null);
		assertEquals(null, item.getSomeBoolean());
		assertContains(item, item2, item.TYPE.search(item.someBoolean.equal((Boolean)null)));
		assertContains(item, item2, item.TYPE.search(item.someBoolean.isNull()));
		assertContains(item.TYPE.search(item.someBoolean.notEqual(null)));
		assertContains(item.TYPE.search(item.someBoolean.isNotNull()));
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionAttribute)item.someBoolean, Integer.valueOf(10));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + Boolean.class.getName() + ", but was a " + Integer.class.getName(), e.getMessage());
		}
	}
	
	public void testSomeNotNullBoolean()
	{
		assertEquals(item.TYPE, item.someNotNullBoolean.getType());
		assertEquals(true, item.getSomeNotNullBoolean());
		assertContains(item, item.TYPE.search(item.someNotNullBoolean.equal(true)));
		assertContains(item.TYPE.search(item.someNotNullBoolean.isNull()));
		assertContains(item, item.TYPE.search(item.someNotNullBoolean.notEqual(false)));
		assertContains(item, item2, item.TYPE.search(item.someNotNullBoolean.isNotNull()));
		
		item.someNotNullBoolean.set(item, Boolean.FALSE);
		assertEquals(false, item.getSomeNotNullBoolean());
		
		item.someNotNullBoolean.set(item, true);
		assertEquals(true, item.getSomeNotNullBoolean());
		
		item.setSomeNotNullBoolean(false);
		assertEquals(false, item.getSomeNotNullBoolean());
		assertEquals(Boolean.FALSE, item.someNotNullBoolean.get(item));
		assertEquals(false, item.someNotNullBoolean.getMandatory(item));
		assertContains(item.TYPE.search(item.someNotNullBoolean.equal(true)));
		assertContains(item.TYPE.search(item.someNotNullBoolean.isNull()));
		assertContains(item.TYPE.search(item.someNotNullBoolean.notEqual(false)));
		assertContains(item, item2, item.TYPE.search(item.someNotNullBoolean.isNotNull()));
	}
}
