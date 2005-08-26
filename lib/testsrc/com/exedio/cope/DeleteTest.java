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
package com.exedio.cope;



public class DeleteTest extends AbstractLibTest
{
	public DeleteTest()
	{
		super(Main.deleteModel);
	}
	
	private DeleteItem item;
	private DeleteOtherItem other;

	public void setUp() throws Exception
	{
		super.setUp();
	}
	
	public void testForbid() throws ConstraintViolationException
	{
		assertTrue(Item.FORBID.forbid);
		assertTrue(!Item.FORBID.nullify);
		assertTrue(!Item.FORBID.cascade);
		assertTrue(!Item.NULLIFY.forbid);
		assertTrue(Item.NULLIFY.nullify);
		assertTrue(!Item.NULLIFY.cascade);
		assertTrue(!Item.CASCADE.forbid);
		assertTrue(!Item.CASCADE.nullify);
		assertTrue(Item.CASCADE.cascade);
		assertEquals("FORBID",  Item.FORBID.toString());
		assertEquals("NULLIFY", Item.NULLIFY.toString());
		assertEquals("CASCADE", Item.CASCADE.toString());
		
		assertEqualsUnmodifiable(list(item.selfForbid, item.selfNullify, item.selfCascade, item.selfCascade2), item.TYPE.getReferences());
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
		if(hsqldb||mysql)
		{
			assertDeleteFails(item, item.selfForbid);
			item.setSelfForbid(null);
		}
		assertDelete(item);

		assertDelete(other);
		assertDelete(item2);
	}
	
	public void testNullify() throws ConstraintViolationException
	{
		assertSame(Item.NULLIFY, item.selfNullify.getDeletePolicy());
		assertSame(Item.NULLIFY, item.otherNullify.getDeletePolicy());

		try
		{
			Item.itemAttribute(Item.MANDATORY, DeleteItem.class, Item.NULLIFY);
		}
		catch(RuntimeException e)
		{
			assertTrue(e.getMessage(), e.getMessage().startsWith("mandatory attribute "+ItemAttribute.class.getName()+'@'));
			assertTrue(e.getMessage(), e.getMessage().endsWith(" cannot have delete policy nullify"));
		}
		try
		{
			Item.itemAttribute(Item.READ_ONLY_OPTIONAL, DeleteItem.class, Item.NULLIFY);
		}
		catch(RuntimeException e)
		{
			assertTrue(e.getMessage(), e.getMessage().startsWith("read-only attribute "+ItemAttribute.class.getName()+'@'));
			assertTrue(e.getMessage(), e.getMessage().endsWith(" cannot have delete policy nullify"));
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
	}
	
	public void testCascade() throws ConstraintViolationException
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
		assertTrue(item6.existsCopeItem()); // TODO!!!!!!!!!!!
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
	
	void assertDeleteFails(final Item item, final ItemAttribute attribute)
	{
		try
		{
			item.deleteCopeItem();
			fail("should have thrown IntegrityViolationException");
		}
		catch(IntegrityViolationException e)
		{
			assertEquals(mysql ? null : attribute, e.getAttribute());
			assertEquals(null/*TODO*/, e.getItem());
		}
		assertTrue(item.existsCopeItem());
	}
	
	public void testItemObjectPool()
	{
		item = new DeleteItem("item1");
		DeleteItem item2 = new DeleteItem("item2");
		DeleteItem item3 = new DeleteItem("item3");
		DeleteItem item4 = new DeleteItem("item4");
		DeleteItem item5 = new DeleteItem("item5");
		DeleteItem item6 = new DeleteItem("item6");
		deleteOnTearDown(item);
		deleteOnTearDown(item2);
		deleteOnTearDown(item3);
		deleteOnTearDown(item4);
		deleteOnTearDown(item5);
		deleteOnTearDown(item6);
		
		// test Item.get(ItemAttribute)
		item.setSelfNullify(item2);
		assertSame(item2, item.getSelfNullify());
		
		
	}

}
