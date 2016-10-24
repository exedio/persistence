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

package com.exedio.cope.hookaudit;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.Test;

public class HookAuditTest extends TestWithEnvironment
{
	@Test void testCreateSpecified()
	{
		newTransaction("create");
		final HookAuditItem item = HookAuditItem.newWithField("createValue");
		assertEquals("createValue", item.getField());
		assertEquals(asList(), HookAudit.search());

		newTransaction("check");
		assertEquals("createValue", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=createValue,2=null"), HookAudit.search());
	}

	@Test void testCreateUnspecified()
	{
		newTransaction("create");
		final HookAuditItem item = new HookAuditItem();
		assertEquals(null, item.getField());
		assertEquals(asList(), HookAudit.search());

		newTransaction("check");
		assertEquals(null, item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());
	}

	@Test void testSet()
	{
		newTransaction("create");
		final HookAuditItem item = HookAuditItem.newWithField(null);
		assertEquals(null, item.getField());
		assertEquals(asList(), HookAudit.search());

		newTransaction("set");
		assertEquals(null, item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());

		item.setField("setValue");
		assertEquals("setValue", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());

		newTransaction("check");
		assertEquals("setValue", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null", "set:" + item + ":1=setValue"), HookAudit.search());
	}

	@Test void testSetAfterCreate()
	{
		newTransaction("create");
		final HookAuditItem item = HookAuditItem.newWithField("createValue");
		assertEquals("createValue", item.getField());
		assertEquals(asList(), HookAudit.search());

		item.setField("setValue");
		assertEquals("setValue", item.getField());
		assertEquals(asList(), HookAudit.search());

		newTransaction("check");
		assertEquals("setValue", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=setValue,2=null"), HookAudit.search());
	}

	@Test void testSetAfterSet()
	{
		newTransaction("create");
		final HookAuditItem item = new HookAuditItem();
		assertEquals(null, item.getField());
		assertEquals(asList(), HookAudit.search());

		newTransaction("set");
		assertEquals(null, item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());

		item.setField("setValue1");
		assertEquals("setValue1", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());

		item.setField("setValue2");
		assertEquals("setValue2", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());

		newTransaction("check");
		assertEquals("setValue2", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null", "set:" + item + ":1=setValue2"), HookAudit.search());
	}

	@Test void testDelete()
	{
		newTransaction("create");
		final HookAuditItem item = HookAuditItem.newWithField("createValue");
		assertEquals("createValue", item.getField());
		assertEquals(asList(), HookAudit.search());
		assertEquals(true, item.existsCopeItem());

		newTransaction("check");
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=createValue,2=null"), HookAudit.search());
		assertEquals(true, item.existsCopeItem());

		newTransaction("delete");
		item.deleteCopeItem();
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=createValue,2=null"), HookAudit.search());
		assertEquals(false, item.existsCopeItem());

		newTransaction("check");
		assertEquals(asList(
				"create:" + item + ":NEW:P=PV,1=createValue,2=null",
				"delete:" + item + ":DEL:P=PV,1=createValue,2=null"), HookAudit.search());
		assertEquals(false, item.existsCopeItem());
	}

	@Test void testDeleteAfterCreate()
	{
		newTransaction("create");
		final HookAuditItem item = HookAuditItem.newWithField("createValue");
		assertEquals("createValue", item.getField());
		assertEquals(asList(), HookAudit.search());
		assertEquals(true, item.existsCopeItem());

		item.deleteCopeItem();
		assertEquals(asList(), HookAudit.search());
		assertEquals(false, item.existsCopeItem());

		newTransaction("check");
		assertEquals(asList(), HookAudit.search());
		assertEquals(false, item.existsCopeItem());
	}

	@Test void testDeleteAfterSet()
	{
		newTransaction("create");
		final HookAuditItem item = new HookAuditItem();
		assertEquals(null, item.getField());
		assertEquals(asList(), HookAudit.search());
		assertEquals(true, item.existsCopeItem());

		newTransaction("set");
		assertEquals(null, item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());
		assertEquals(true, item.existsCopeItem());

		item.setField("setValue");
		assertEquals("setValue", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());
		assertEquals(true, item.existsCopeItem());

		item.deleteCopeItem();
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());
		assertEquals(false, item.existsCopeItem());

		newTransaction("check");
		assertEquals(asList(
				"create:" + item + ":NEW:P=PV,1=null,2=null",
				"set:"    + item + ":DEL:P=PV,1=null,2=null"), HookAudit.search());
		assertEquals(false, item.existsCopeItem());
	}

	@Test void testSubItem()
	{
		newTransaction("create");
		final HookAuditItemSub item = HookAuditItemSub.newWithField("createValue");
		assertEquals("createValue", item.getField());
		assertEquals(asList(), HookAudit.search());

		newTransaction("check");
		assertEquals("createValue", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=createValue,2=null,S=null"), HookAudit.search());

		newTransaction("set");
		assertEquals("createValue", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=createValue,2=null,S=null"), HookAudit.search());

		item.setField("setValue");
		assertEquals("setValue", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=createValue,2=null,S=null"), HookAudit.search());

		newTransaction("check");
		assertEquals("setValue", item.getField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=createValue,2=null,S=null", "set:" + item + ":1=setValue"), HookAudit.search());
	}

	@Test void testSubField()
	{
		newTransaction("create");
		final HookAuditItemSub item = HookAuditItemSub.newWithSubField("createValue");
		assertEquals("createValue", item.getSubField());
		assertEquals(asList(), HookAudit.search());

		newTransaction("check");
		assertEquals("createValue", item.getSubField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null,S=createValue"), HookAudit.search());

		newTransaction("set");
		assertEquals("createValue", item.getSubField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null,S=createValue"), HookAudit.search());

		item.setSubField("setValue");
		assertEquals("setValue", item.getSubField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null,S=createValue"), HookAudit.search());

		newTransaction("check");
		assertEquals("setValue", item.getSubField());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null,S=createValue", "set:" + item + ":S=setValue"), HookAudit.search());
	}

	@Test void testSuperItem()
	{
		newTransaction("create");
		final HookAuditItemSuper item = HookAuditItemSuper.newWithSuperField("createValue");
		assertEquals("createValue", item.getSuperField());
		assertEquals(asList(), HookAudit.search());

		newTransaction("check");
		assertEquals("createValue", item.getSuperField());
		assertEquals(asList("create:" + item + ":NEW:P=createValue"), HookAudit.search());

		newTransaction("set");
		assertEquals("createValue", item.getSuperField());
		assertEquals(asList("create:" + item + ":NEW:P=createValue"), HookAudit.search());

		item.setSuperField("setValue");
		assertEquals("setValue", item.getSuperField());
		assertEquals(asList("create:" + item + ":NEW:P=createValue"), HookAudit.search());

		newTransaction("check");
		assertEquals("setValue", item.getSuperField());
		assertEquals(asList("create:" + item + ":NEW:P=createValue", "set:" + item + ":P=setValue"), HookAudit.search());
	}

	@Test void testNotCovered()
	{
		newTransaction("create");
		final HookAuditItem item = HookAuditItem.newWithNotCovered("createValue");
		assertEquals("createValue", item.getNotCovered());
		assertEquals(asList(), HookAudit.search());

		newTransaction("check");
		assertEquals("createValue", item.getNotCovered());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());

		newTransaction("set");
		assertEquals("createValue", item.getNotCovered());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());

		item.setNotCovered("setValue");
		assertEquals("setValue", item.getNotCovered());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());

		newTransaction("check");
		assertEquals("setValue", item.getNotCovered());
		assertEquals(asList("create:" + item + ":NEW:P=PV,1=null,2=null"), HookAudit.search());
	}


	static final String TX_PREFIX = HookAuditTest.class.getName() + ":";

	static final Model MODEL = Model.builder().
			add(HookAuditItem.TYPE, HookAuditItemSub.TYPE, HookAuditItemSuper.TYPE, HookAudit.TYPE).
			changeHooks(model ->
				new HookAuditHook(model, () ->
				{
					final String txName = HookAuditTest.MODEL.currentTransaction().getName();
					assertTrue(txName.startsWith(TX_PREFIX), txName);
					return txName.substring(TX_PREFIX.length());
				}
			)).
			build();

	public HookAuditTest()
	{
		super(MODEL);
	}

	private static void newTransaction(final String name)
	{
		MODEL.commit();
		MODEL.startTransaction(TX_PREFIX + name);
	}
}
