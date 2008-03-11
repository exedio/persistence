/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.util.Collection;
import java.util.List;

public class DeleteTest extends AbstractLibTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(DeleteItem.TYPE, DeleteOtherItem.TYPE);

	public DeleteTest()
	{
		super(MODEL);
	}
	
	private DeleteItem item;
	private DeleteOtherItem other;

	public void testForbid()
	{
		assertEqualsUnmodifiable(list(item.selfForbid, item.selfNullify, item.selfCascade, item.selfCascade2), item.TYPE.getDeclaredReferences());
		assertEqualsUnmodifiable(list(item.selfForbid, item.selfNullify, item.selfCascade, item.selfCascade2), item.TYPE.getReferences());
		assertEqualsUnmodifiable(list(item.otherForbid, item.otherNullify, item.otherCascade), other.TYPE.getDeclaredReferences());
		assertEqualsUnmodifiable(list(item.otherForbid, item.otherNullify, item.otherCascade), other.TYPE.getReferences());
		
		assertSame(Item.FORBID, item.selfForbid.getDeletePolicy());
		assertSame(Item.FORBID, item.otherForbid.getDeletePolicy());

		// other type
		other = new DeleteOtherItem("other");
		item = new DeleteItem("item");
		item.setOtherForbid(other);
		assertDeleteFails(other, item.otherForbid);
		
		// other item
		DeleteItem item2 = new DeleteItem("item2");
		item.setOtherForbid(null);
		item.setSelfForbid(item2);
		assertDeleteFails(item2, item.selfForbid);

		// same item
		item.setSelfForbid(item);
		if(true) // TODO allow self references
		{
			assertDeleteFails(item, item.selfForbid);
			item.setSelfForbid(null);
		}
		assertDelete(item);
		
		// indirect forbid
		item = new DeleteItem("itemb");
		item2.setSelfCascade(item);
		DeleteItem item3 = new DeleteItem("item3");
		item3.setSelfForbid(item2);
		assertDeleteFails(item, item.selfForbid, item2);

		assertDelete(other);
		assertDelete(item3);
		assertDelete(item2);
		assertDelete(item);
	}
	
	@Deprecated
	public void testNullifyDeprecated()
	{
		try
		{
			Item.newItemField(Item.MANDATORY, DeleteItem.class, Item.NULLIFY);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("mandatory item field cannot have delete policy nullify", e.getMessage());
		}
	}
	
	public void testNullify()
	{
		assertSame(Item.NULLIFY, item.selfNullify.getDeletePolicy());
		assertSame(Item.NULLIFY, item.otherNullify.getDeletePolicy());
		assertFalse(item.selfNullify.isMandatory());
		assertFalse(item.otherNullify.isMandatory());

		try
		{
			Item.newItemField(Item.class, null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("is not a subclass of " + Item.class.getName() + " but Item itself", e.getMessage());
		}
		try
		{
			Item.newItemField(DeleteItem.class, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("delete policy for item field must not be null", e.getMessage());
		}
		assertEquals(false, Item.newItemField(DeleteItem.class, Item.NULLIFY).isMandatory());
		try
		{
			Item.newItemField(DeleteItem.class, Item.NULLIFY).toFinal();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("final item field cannot have delete policy nullify", e.getMessage());
		}

		// other type
		item = new DeleteItem("itema");
		other = new DeleteOtherItem("other");
		item.setOtherNullify(other);
		assertEquals(other, item.getOtherNullify());
		assertDelete(other);
		assertEquals(null, item.getOtherNullify());
		
		// other item
		DeleteItem item2 = new DeleteItem("item");
		item.setSelfNullify(item2);
		assertEquals(item2, item.getSelfNullify());
		assertDelete(item2);
		assertEquals(null, item.getSelfNullify());
		
		// same item
		item.setSelfNullify(item);
		assertDelete(item);

		// indirect nullify
		item = new DeleteItem("itemb");
		item2 = new DeleteItem("item2b");
		item2.setSelfCascade(item);
		DeleteItem item3 = new DeleteItem("item3b");
		item3.setSelfNullify(item2);
		assertEquals(item2, item3.getSelfNullify());
		assertDelete(item);
		assertTrue(!item2.existsCopeItem());
		assertEquals(null, item3.getSelfNullify());

		assertDelete(item3);
	}
	
	public void testCascade()
	{
		assertSame(Item.CASCADE, item.selfCascade.getDeletePolicy());
		assertSame(Item.CASCADE, item.otherCascade.getDeletePolicy());

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
		assertDelete(item3);
		
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
		assertDelete(item);
		assertTrue(!item2.existsCopeItem());
		
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
		assertDelete(item);
		assertTrue(!item2.existsCopeItem());
	}
	
	public void testAtomicity()
	{
		final DeleteItem todelete = deleteOnTearDown(new DeleteItem("todelete"));

		final DeleteItem middle1 = deleteOnTearDown(new DeleteItem("middle1"));
		middle1.setSelfCascade(todelete);
		middle1.setSelfNullify(todelete);
		
		final DeleteItem middle2 = deleteOnTearDown(new DeleteItem("middle2"));
		middle2.setSelfCascade(todelete);
		middle2.setSelfNullify(todelete);
		
		final DeleteItem middle3 = deleteOnTearDown(new DeleteItem("middle3"));
		middle3.setSelfCascade(todelete);
		middle3.setSelfNullify(todelete);
		
		final DeleteItem item = deleteOnTearDown(new DeleteItem("forbid"));
		item.setSelfForbid(middle2);

		assertDeleteFails(todelete, item.selfForbid, middle2);
		assertTrue(todelete.existsCopeItem());
		assertTrue(middle1.existsCopeItem());
		assertTrue(middle2.existsCopeItem());
		assertTrue(middle3.existsCopeItem());
		assertEquals(todelete, middle1.getSelfNullify());
		assertEquals(todelete, middle2.getSelfNullify());
		assertEquals(todelete, middle3.getSelfNullify());
		assertTrue(item.existsCopeItem());
	}
	
	public void testItemObjectPool() throws NoSuchIDException
	{
		item = deleteOnTearDown(new DeleteItem("item1"));
		DeleteItem item2 = deleteOnTearDown(new DeleteItem("item2"));
		
		// test Model.getItem
		assertSame(item, item.TYPE.getModel().getItem(item.getCopeID()));
		
		// test Item.get(ItemAttribute)
		item.setSelfNullify(item2);
		assertSame(item2, item.getSelfNullify());
		
		// test Query.search
		final Query query1 = item.TYPE.newQuery(null);
		query1.setOrderByThis(true);
		final Collection searchResult1 = query1.search();
		assertEquals(list(item, item2), searchResult1);
		assertSame(item, searchResult1.iterator().next());

		// test Query.search with selects
		final Query<DeleteItem> query2 = new Query<DeleteItem>(item.selfNullify);
		query2.setOrderByThis(true);
		final List<? extends DeleteItem> searchResult2 = query2.search();
		assertEquals(list(item2, null), searchResult2);
		assertSame(item2, searchResult2.iterator().next());
	}

	/**
	 * Tests migration on a model not supporting migrations.
	 */
	public void testMigrate()
	{
		assertEquals(ConnectProperties.getDefaultPropertyFile().getAbsolutePath(), model.getProperties().getSource());
		
		assertFalse(model.isMigrationSupported());
		try
		{
			model.getMigrationRevision();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("not in migration mode", e.getMessage());
		}
		try
		{
			model.getMigrations();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("not in migration mode", e.getMessage());
		}
		try
		{
			model.migrate();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("not in migration mode", e.getMessage());
		}
		try
		{
			model.getMigrationLogs();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("not in migration mode", e.getMessage());
		}
		model.migrateIfSupported();
	}
}
