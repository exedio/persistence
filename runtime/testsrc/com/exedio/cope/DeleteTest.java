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

import static com.exedio.cope.AbstractRuntimeTest.assertDelete;
import static com.exedio.cope.AbstractRuntimeTest.assertDeleteFails;
import static com.exedio.cope.DeleteItem.otherCascade;
import static com.exedio.cope.DeleteItem.otherForbid;
import static com.exedio.cope.DeleteItem.otherNullify;
import static com.exedio.cope.DeleteItem.selfCascade;
import static com.exedio.cope.DeleteItem.selfCascade2;
import static com.exedio.cope.DeleteItem.selfForbid;
import static com.exedio.cope.DeleteItem.selfNullify;
import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.ItemField.DeletePolicy.FORBID;
import static com.exedio.cope.ItemField.DeletePolicy.NULLIFY;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeleteTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(DeleteItem.TYPE, DeleteOtherItem.TYPE);

	public DeleteTest()
	{
		super(MODEL);
	}

	private DeleteItem item;
	private DeleteOtherItem other;

	@BeforeEach final void setUpDeleteTest()
	{
		DeleteItem.BEFORE_DELETE_COPE_ITEM_CALLS.set(new LinkedList<>());
	}

	@AfterEach final void tearDownDeleteTest()
	{
		DeleteItem.BEFORE_DELETE_COPE_ITEM_CALLS.remove();
	}

	@Test void testForbid()
	{
		assertEqualsUnmodifiable(list(selfForbid, selfNullify, selfCascade, selfCascade2), DeleteItem.TYPE.getDeclaredReferences());
		assertEqualsUnmodifiable(list(selfForbid, selfNullify, selfCascade, selfCascade2), DeleteItem.TYPE.getReferences());
		assertEqualsUnmodifiable(list(otherForbid, otherNullify, otherCascade), DeleteOtherItem.TYPE.getDeclaredReferences());
		assertEqualsUnmodifiable(list(otherForbid, otherNullify, otherCascade), DeleteOtherItem.TYPE.getReferences());

		assertSame(FORBID, selfForbid.getDeletePolicy());
		assertSame(FORBID, otherForbid.getDeletePolicy());

		// other type
		other = new DeleteOtherItem("other");
		item = new DeleteItem("item");
		item.setOtherForbid(other);
		assertDeleteFails(other, otherForbid);
		assertAndResetBeforeDeleteCopeItemCalls();

		// other item
		final DeleteItem item2 = new DeleteItem("item2");
		item.setOtherForbid(null);
		item.setSelfForbid(item2);
		assertDeleteFails(item2, selfForbid);
		assertAndResetBeforeDeleteCopeItemCalls();

		// same item
		item.setSelfForbid(item);
		// TODO allow self references
		assertDeleteFails(item, selfForbid);
		assertAndResetBeforeDeleteCopeItemCalls();
		item.setSelfForbid(null);
		assertDelete(item);
		assertAndResetBeforeDeleteCopeItemCalls("item");

		// indirect forbid
		item = new DeleteItem("itemb");
		item2.setSelfCascade(item);
		final DeleteItem item3 = new DeleteItem("item3");
		item3.setSelfForbid(item2);
		assertDeleteFails(item, selfForbid, item2);
		assertAndResetBeforeDeleteCopeItemCalls();

		assertDelete(other);
		assertDelete(item3);
		assertDelete(item2);
		assertDelete(item);
		assertAndResetBeforeDeleteCopeItemCalls("other", "item3", "item2", "itemb");
	}

	@Test void testNullify()
	{
		assertSame(NULLIFY, selfNullify.getDeletePolicy());
		assertSame(NULLIFY, otherNullify.getDeletePolicy());
		assertFalse(selfNullify.isMandatory());
		assertFalse(otherNullify.isMandatory());

		try
		{
			ItemField.create(Item.class, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("is not a subclass of " + Item.class.getName() + " but Item itself", e.getMessage());
		}
		try
		{
			ItemField.create(DeleteItem.class, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("policy", e.getMessage());
		}
		assertEquals(false, ItemField.create(DeleteItem.class).nullify().isMandatory());
		try
		{
			ItemField.create(DeleteItem.class).nullify().toFinal();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("final item field cannot have delete policy nullify", e.getMessage());
		}

		// other type
		item = new DeleteItem("itema");
		other = new DeleteOtherItem("other");
		item.setOtherNullify(other);
		assertEquals(other, item.getOtherNullify());
		assertDelete(other);
		assertAndResetBeforeDeleteCopeItemCalls("other");
		assertEquals(null, item.getOtherNullify());

		// other item
		DeleteItem item2 = new DeleteItem("item");
		item.setSelfNullify(item2);
		assertEquals(item2, item.getSelfNullify());
		assertDelete(item2);
		assertAndResetBeforeDeleteCopeItemCalls("item");
		assertEquals(null, item.getSelfNullify());

		// same item
		item.setSelfNullify(item);
		assertDelete(item);
		assertAndResetBeforeDeleteCopeItemCalls("itema");

		// indirect nullify
		item = new DeleteItem("itemb");
		item2 = new DeleteItem("item2b");
		item2.setSelfCascade(item);
		final DeleteItem item3 = new DeleteItem("item3b");
		item3.setSelfNullify(item2);
		assertEquals(item2, item3.getSelfNullify());
		assertDelete(item);
		assertTrue(!item2.existsCopeItem());
		assertAndResetBeforeDeleteCopeItemCalls("itemb", "item2b");
		assertEquals(null, item3.getSelfNullify());

		assertDelete(item3);
		assertAndResetBeforeDeleteCopeItemCalls("item3b");
	}

	@Test void testCascade()
	{
		assertSame(CASCADE, selfCascade.getDeletePolicy());
		assertSame(CASCADE, otherCascade.getDeletePolicy());

		DeleteItem item2;
		DeleteItem item3;
		DeleteItem item4;
		DeleteItem item5;
		DeleteItem item6;

		// other type
		item = new DeleteItem("itema");
		other = new DeleteOtherItem("other");
		item.setOtherCascade(other);
		assertEquals(other, item.getOtherCascade());
		assertDelete(other);
		assertTrue(!item.existsCopeItem());
		assertAndResetBeforeDeleteCopeItemCalls("other", "itema");

		// other type with multiple sources
		item = new DeleteItem("item");
		item2 = new DeleteItem("item2");
		item3 = new DeleteItem("item3");
		item4 = new DeleteItem("item4");
		other = new DeleteOtherItem("other");
		item.setOtherCascade(other);
		item2.setOtherCascade(other);
		item4.setOtherCascade(other);
		assertEquals(other, item.getOtherCascade());
		assertDelete(other);
		assertTrue(!item.existsCopeItem());
		assertTrue(!item2.existsCopeItem());
		assertTrue(item3.existsCopeItem());
		assertTrue(!item4.existsCopeItem());
		assertAndResetBeforeDeleteCopeItemCalls("other", "item", "item2", "item4");
		assertDelete(item3);
		assertAndResetBeforeDeleteCopeItemCalls("item3");

		// other item
		item = new DeleteItem("item");
		item2 = new DeleteItem("item2");
		item3 = new DeleteItem("item3");
		item4 = new DeleteItem("item4");
		item5 = new DeleteItem("item5");
		item6 = new DeleteItem("item6");
		item2.setSelfCascade(item);
		item3.setSelfCascade(item);
		item4.setSelfCascade(item3);
		item5.setSelfCascade(item3);
		item6.setSelfCascade(item5);
		assertDelete(item3);
		assertTrue(item.existsCopeItem());
		assertTrue(item2.existsCopeItem());
		assertTrue(!item3.existsCopeItem());
		assertTrue(!item4.existsCopeItem());
		assertTrue(!item5.existsCopeItem());
		assertTrue(!item6.existsCopeItem());
		assertAndResetBeforeDeleteCopeItemCalls("item3", "item4", "item5", "item6");
		assertDelete(item);
		assertTrue(!item2.existsCopeItem());
		assertAndResetBeforeDeleteCopeItemCalls("item", "item2");

		// other item with diamond
		item = new DeleteItem("item");
		item2 = new DeleteItem("item2");
		item3 = new DeleteItem("item3");
		item4 = new DeleteItem("item4");
		item5 = new DeleteItem("item5");
		item6 = new DeleteItem("item6");
		item2.setSelfCascade(item);
		item3.setSelfCascade(item);
		item4.setSelfCascade(item3);
		item5.setSelfCascade(item3);
		item6.setSelfCascade(item5);
		item5.setSelfCascade2(item4); // closes diamond
		assertDelete(item3);
		assertTrue(item.existsCopeItem());
		assertTrue(item2.existsCopeItem());
		assertTrue(!item3.existsCopeItem());
		assertTrue(!item4.existsCopeItem());
		assertTrue(!item5.existsCopeItem());
		assertTrue(!item6.existsCopeItem());
		assertAndResetBeforeDeleteCopeItemCalls("item3", "item4", "item5", "item6");
		assertDelete(item);
		assertTrue(!item2.existsCopeItem());
		assertAndResetBeforeDeleteCopeItemCalls("item", "item2");
	}

	@Test void testAtomicity()
	{
		final DeleteItem todelete = new DeleteItem("todelete");

		final DeleteItem middle1 = new DeleteItem("middle1");
		middle1.setSelfCascade(todelete);
		middle1.setSelfNullify(todelete);

		final DeleteItem middle2 = new DeleteItem("middle2");
		middle2.setSelfCascade(todelete);
		middle2.setSelfNullify(todelete);

		final DeleteItem middle3 = new DeleteItem("middle3");
		middle3.setSelfCascade(todelete);
		middle3.setSelfNullify(todelete);

		final DeleteItem item = new DeleteItem("forbid");
		item.setSelfForbid(middle2);

		assertDeleteFails(todelete, selfForbid, middle2);
		assertTrue(todelete.existsCopeItem());
		assertTrue(middle1.existsCopeItem());
		assertTrue(middle2.existsCopeItem());
		assertTrue(middle3.existsCopeItem());
		assertEquals(todelete, middle1.getSelfNullify());
		assertEquals(todelete, middle2.getSelfNullify());
		assertEquals(todelete, middle3.getSelfNullify());
		assertTrue(item.existsCopeItem());
		assertAndResetBeforeDeleteCopeItemCalls();
	}

	@Test void testItemObjectPool() throws NoSuchIDException
	{
		item = new DeleteItem("item1");
		final DeleteItem item2 = new DeleteItem("item2");

		// test Model.getItem
		assertSame(item, DeleteItem.TYPE.getModel().getItem(item.getCopeID()));

		// test Item.get(ItemAttribute)
		item.setSelfNullify(item2);
		assertSame(item2, item.getSelfNullify());

		// test Query.search
		final Query<?> query1 = DeleteItem.TYPE.newQuery(null);
		query1.setOrderByThis(true);
		final Collection<?> searchResult1 = query1.search();
		assertEquals(list(item, item2), searchResult1);
		assertSame(item, searchResult1.iterator().next());

		// test Query.search with selects
		final Query<DeleteItem> query2 = new Query<>(selfNullify);
		query2.setOrderByThis(true);
		final List<DeleteItem> searchResult2 = query2.search();
		assertEquals(list(item2, null), searchResult2);
		assertSame(item2, searchResult2.iterator().next());
	}

	/**
	 * Tests revision on a model without revisions enabled.
	 */
	@Test void testRevise()
	{
		assertEquals(ConnectProperties.getDefaultPropertyFile().getAbsolutePath(), model.getConnectProperties().getSource());

		assertNull(model.getRevisions());
		try
		{
			model.revise();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("revisions are not enabled", e.getMessage());
		}
		try
		{
			model.getRevisionLogs();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("revisions are not enabled", e.getMessage());
		}
		try
		{
			model.getRevisionLogsAndMutex();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("revisions are not enabled", e.getMessage());
		}
		model.reviseIfSupportedAndAutoEnabled();
	}

	private static void assertAndResetBeforeDeleteCopeItemCalls(final String... itemNames)
	{
		final List<String> calls = DeleteItem.BEFORE_DELETE_COPE_ITEM_CALLS.get();
		final String[] callArray = calls.toArray(new String[calls.size()]);
		calls.clear();
		assertArrayEquals(itemNames, callArray, "Item#beforeDeleteCopeItem() method is not called as expected (expected: "+Arrays.toString(itemNames)+" | result: "+Arrays.toString(callArray)+").");
	}
}
