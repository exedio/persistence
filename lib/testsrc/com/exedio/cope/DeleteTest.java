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
		assertEquals(Item.FORBID, item.selfForbid.getDeletePolicy());
		assertEquals(Item.FORBID, item.otherForbid.getDeletePolicy());

		assertEqualsUnmodifiable(list(item.selfForbid, item.selfNullify), item.TYPE.getReferences());
		assertEqualsUnmodifiable(list(item.otherForbid, item.otherNullify), other.TYPE.getReferences());
		
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
		assertEquals(Item.NULLIFY, item.selfNullify.getDeletePolicy());
		assertEquals(Item.NULLIFY, item.otherNullify.getDeletePolicy());

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
		
		assertDelete(item);
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

}
