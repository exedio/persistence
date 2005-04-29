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


public class AttributeBooleanTest extends AttributeTest 
{
	public void testSomeBoolean()
	{
		assertEquals(item.TYPE, item.someBoolean.getType());
		assertEquals(null, item.getSomeBoolean());
		assertContains(item, item2, item.TYPE.search(Cope.equal(item.someBoolean, null)));
		assertContains(item, item2, item.TYPE.search(Cope.isNull(item.someBoolean)));
		assertContains(item.TYPE.search(Cope.notEqual(item.someBoolean, null)));
		assertContains(item.TYPE.search(Cope.isNotNull(item.someBoolean)));

		item.setSomeBoolean(Boolean.TRUE);
		assertEquals(Boolean.TRUE, item.getSomeBoolean());
		assertContains(item, item.TYPE.search(Cope.equal(item.someBoolean, true)));
		assertContains(item2, item.TYPE.search(Cope.isNull(item.someBoolean)));
		assertContains(item2, item.TYPE.search(Cope.notEqual(item.someBoolean, true)));
		assertContains(item, item.TYPE.search(Cope.isNotNull(item.someBoolean)));

		item.setSomeBoolean(Boolean.FALSE);
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertContains(item, item.TYPE.search(Cope.equal(item.someBoolean, false)));
		assertContains(item2, item.TYPE.search(Cope.isNull(item.someBoolean)));
		assertContains(item2, item.TYPE.search(Cope.notEqual(item.someBoolean, false)));
		assertContains(item, item.TYPE.search(Cope.isNotNull(item.someBoolean)));
		
		assertContains(Boolean.FALSE, null, search(item.someBoolean));
		assertContains(Boolean.FALSE, search(item.someBoolean, Cope.equal(item.someBoolean, false)));

		item.passivateCopeItem();
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertContains(item, item.TYPE.search(Cope.equal(item.someBoolean, false)));
		assertContains(item2, item.TYPE.search(Cope.isNull(item.someBoolean)));
		assertContains(item2, item.TYPE.search(Cope.notEqual(item.someBoolean, false)));
		assertContains(item, item.TYPE.search(Cope.isNotNull(item.someBoolean)));

		item.setSomeBoolean(null);
		assertEquals(null, item.getSomeBoolean());
		assertContains(item, item2, item.TYPE.search(Cope.equal(item.someBoolean, null)));
		assertContains(item, item2, item.TYPE.search(Cope.isNull(item.someBoolean)));
		assertContains(item.TYPE.search(Cope.notEqual(item.someBoolean, null)));
		assertContains(item.TYPE.search(Cope.isNotNull(item.someBoolean)));
	}

	public void testSomeNotNullBoolean()
	{
		assertEquals(item.TYPE, item.someNotNullBoolean.getType());
		assertEquals(true, item.getSomeNotNullBoolean());
		assertContains(item, item.TYPE.search(Cope.equal(item.someNotNullBoolean, true)));
		assertContains(item.TYPE.search(Cope.isNull(item.someNotNullBoolean)));
		assertContains(item, item.TYPE.search(Cope.notEqual(item.someNotNullBoolean, false)));
		assertContains(item, item2, item.TYPE.search(Cope.isNotNull(item.someNotNullBoolean)));
		
		item.setSomeNotNullBoolean(false);
		assertEquals(false, item.getSomeNotNullBoolean());
		assertContains(item.TYPE.search(Cope.equal(item.someNotNullBoolean, true)));
		assertContains(item.TYPE.search(Cope.isNull(item.someNotNullBoolean)));
		assertContains(item.TYPE.search(Cope.notEqual(item.someNotNullBoolean, false)));
		assertContains(item, item2, item.TYPE.search(Cope.isNotNull(item.someNotNullBoolean)));
	}
}
