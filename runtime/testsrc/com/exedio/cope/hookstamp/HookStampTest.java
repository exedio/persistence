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

package com.exedio.cope.hookstamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.Test;

public class HookStampTest extends TestWithEnvironment
{
	@Test void testCreateSpecified()
	{
		newTransaction("create");
		final HookStampItem item = HookStampItem.newWithField("createValue");
		assertEquals("createValue", item.getField());
		assertEquals("{S}", item.getHistory());

		newTransaction("check");
		assertEquals("createValue", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=createValue,2=null}", item.getHistory());
	}

	@Test void testCreateUnspecified()
	{
		newTransaction("create");
		final HookStampItem item = new HookStampItem();
		assertEquals(null, item.getField());
		assertEquals("{S}", item.getHistory());

		newTransaction("check");
		assertEquals(null, item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());
	}

	@Test void testSet()
	{
		newTransaction("create");
		final HookStampItem item = HookStampItem.newWithField(null);
		assertEquals(null, item.getField());
		assertEquals("{S}", item.getHistory());

		newTransaction("set");
		assertEquals(null, item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());

		item.setField("setValue");
		assertEquals("setValue", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());

		newTransaction("check");
		assertEquals("setValue", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}{set:1=setValue}", item.getHistory());
	}

	@Test void testSetAfterCreate()
	{
		newTransaction("create");
		final HookStampItem item = HookStampItem.newWithField("createValue");
		assertEquals("createValue", item.getField());
		assertEquals("{S}", item.getHistory());

		item.setField("setValue");
		assertEquals("setValue", item.getField());
		assertEquals("{S}", item.getHistory());

		newTransaction("check");
		assertEquals("setValue", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=setValue,2=null}", item.getHistory());
	}

	@Test void testSetAfterSet()
	{
		newTransaction("create");
		final HookStampItem item = new HookStampItem();
		assertEquals(null, item.getField());
		assertEquals("{S}", item.getHistory());

		newTransaction("set");
		assertEquals(null, item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());

		item.setField("setValue1");
		assertEquals("setValue1", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());

		item.setField("setValue2");
		assertEquals("setValue2", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());

		newTransaction("check");
		assertEquals("setValue2", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}{set:1=setValue2}", item.getHistory());
	}

	@Test void testDeleteAfterCreate()
	{
		newTransaction("create");
		final HookStampItem item = HookStampItem.newWithField("createValue");
		assertEquals("createValue", item.getField());
		assertEquals("{S}", item.getHistory());
		assertEquals(true, item.existsCopeItem());

		item.deleteCopeItem();
		assertEquals(false, item.existsCopeItem());

		newTransaction("check");
		assertEquals(false, item.existsCopeItem());
	}

	@Test void testDeleteAfterSet()
	{
		newTransaction("create");
		final HookStampItem item = new HookStampItem();
		assertEquals(null, item.getField());
		assertEquals("{S}", item.getHistory());
		assertEquals(true, item.existsCopeItem());

		newTransaction("set");
		assertEquals(null, item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());
		assertEquals(true, item.existsCopeItem());

		item.setField("setValue");
		assertEquals("setValue", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());
		assertEquals(true, item.existsCopeItem());

		item.deleteCopeItem();
		assertEquals(false, item.existsCopeItem());

		newTransaction("check");
		assertEquals(false, item.existsCopeItem());
	}

	@Test void testSubItem()
	{
		newTransaction("create");
		final HookStampItemSub item = HookStampItemSub.newWithField("createValue");
		assertEquals("createValue", item.getField());
		assertEquals("{S}", item.getHistory());
		assertEquals("{S}", item.getHistorySub());

		newTransaction("check");
		assertEquals("createValue", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=createValue,2=null,S=null}", item.getHistory());
		assertEquals("{S}{NEW:create:P=PV,1=createValue,2=null,S=null}", item.getHistorySub());

		newTransaction("set");
		assertEquals("createValue", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=createValue,2=null,S=null}", item.getHistory());
		assertEquals("{S}{NEW:create:P=PV,1=createValue,2=null,S=null}", item.getHistorySub());

		item.setField("setValue");
		assertEquals("setValue", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=createValue,2=null,S=null}", item.getHistory());
		assertEquals("{S}{NEW:create:P=PV,1=createValue,2=null,S=null}", item.getHistorySub());

		newTransaction("check");
		assertEquals("setValue", item.getField());
		assertEquals("{S}{NEW:create:P=PV,1=createValue,2=null,S=null}{set:1=setValue}", item.getHistory());
		assertEquals("{S}{NEW:create:P=PV,1=createValue,2=null,S=null}{set:1=setValue}", item.getHistorySub());
	}

	@Test void testSubField()
	{
		newTransaction("create");
		final HookStampItemSub item = HookStampItemSub.newWithSubField("createValue");
		assertEquals("createValue", item.getSubField());
		assertEquals("{S}", item.getHistory());
		assertEquals("{S}", item.getHistorySub());

		newTransaction("check");
		assertEquals("createValue", item.getSubField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null,S=createValue}", item.getHistory());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null,S=createValue}", item.getHistorySub());

		newTransaction("set");
		assertEquals("createValue", item.getSubField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null,S=createValue}", item.getHistory());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null,S=createValue}", item.getHistorySub());

		item.setSubField("setValue");
		assertEquals("setValue", item.getSubField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null,S=createValue}", item.getHistory());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null,S=createValue}", item.getHistorySub());

		newTransaction("check");
		assertEquals("setValue", item.getSubField());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null,S=createValue}{set:S=setValue}", item.getHistory());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null,S=createValue}{set:S=setValue}", item.getHistorySub());
	}

	@Test void testSuperItem()
	{
		newTransaction("create");
		final HookStampItemSuper item = HookStampItemSuper.newWithSuperField("createValue");
		assertEquals("createValue", item.getSuperField());

		newTransaction("check");
		assertEquals("createValue", item.getSuperField());

		newTransaction("set");
		assertEquals("createValue", item.getSuperField());

		item.setSuperField("setValue");
		assertEquals("setValue", item.getSuperField());

		newTransaction("check");
		assertEquals("setValue", item.getSuperField());
	}

	@Test void testNotCovered()
	{
		newTransaction("create");
		final HookStampItem item = HookStampItem.newWithNotCovered("createValue");
		assertEquals("createValue", item.getNotCovered());
		assertEquals("{S}", item.getHistory());

		newTransaction("check");
		assertEquals("createValue", item.getNotCovered());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());

		newTransaction("set");
		assertEquals("createValue", item.getNotCovered());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());

		item.setNotCovered("setValue");
		assertEquals("setValue", item.getNotCovered());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());

		newTransaction("check");
		assertEquals("setValue", item.getNotCovered());
		assertEquals("{S}{NEW:create:P=PV,1=null,2=null}", item.getHistory());
	}


	private static final String TX_PREFIX = HookStampTest.class.getName() + ":";

	static final Model MODEL = Model.builder().
			add(HookStampItem.TYPE, HookStampItemSub.TYPE, HookStampItemSuper.TYPE).
			changeHooks(model ->
				new HookStampHook(model, () ->
				{
					final String txName = HookStampTest.MODEL.currentTransaction().getName();
					assertTrue(txName.startsWith(TX_PREFIX), txName);
					return txName.substring(TX_PREFIX.length());
				}
			)).
			build();

	public HookStampTest()
	{
		super(MODEL);
	}

	private static void newTransaction(final String name)
	{
		MODEL.commit();
		MODEL.startTransaction(TX_PREFIX + name);
	}
}
