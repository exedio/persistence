
package com.exedio.cope.lib;

import java.util.Arrays;

public class ItemTest extends DatabaseLibTest
{
	
	public ItemTest()
	{}
	
	/**
	 * Test getType, ID, equals, hashCode etc.
	 */
	public void testItemMethods()
			throws IntegrityViolationException, NoSuchIDException
	{
		assertEquals(EmptyItem.TYPE, Type.findByJavaClass(EmptyItem.class));
		assertEquals(EmptyItem2.TYPE, Type.findByJavaClass(EmptyItem2.class));
		assertEquals(toSet(Arrays.asList(types)), toSet(Type.getTypes()));

		final EmptyItem item1 = new EmptyItem();
		final EmptyItem item2 = new EmptyItem();
		final EmptyItem2 item3 = new EmptyItem2();

		assertEquals(EmptyItem.TYPE, item1.getType());
		assertEquals(EmptyItem.TYPE, item2.getType());
		assertEquals(EmptyItem2.TYPE, item3.getType());

		assertID(0, item1);
		assertID(1, item2);
		assertID(0, item3);

		assertEquals(item1, Search.findByID(item1.getID()));
		assertEquals(item2, Search.findByID(item2.getID()));
		assertEquals(item3, Search.findByID(item3.getID()));

		assertEquals(item1, item1);
		assertEquals(item2, item2);
		assertEquals(item3, item3);

		assertFalse(item1.equals(null));
		assertFalse(item2.equals(null));
		assertFalse(item3.equals(null));

		assertNotEquals(item1, item2);
		assertNotEquals(item1, item3);
		assertNotEquals(item2, item3);

		assertFalse(item1.equals("hello"));
		assertFalse(item1.equals(new Integer(1)));
		assertFalse(item1.equals(Boolean.TRUE));

		final EmptyItem item4 = new EmptyItem();
		assertID(2, item4);
		item4.TYPE.flushPK();
		final EmptyItem item5 = new EmptyItem();
		assertID(3, item5);
		assertNotEquals(item4, item5);
		item4.TYPE.flushPK();
		final EmptyItem item6 = new EmptyItem();
		assertID(4, item6);
		assertNotEquals(item4, item5);
		assertNotEquals(item4, item6);
		assertNotEquals(item5, item6);
		
		assertDelete(item1);
		assertDelete(item2);
		assertDelete(item3);
		assertDelete(item4);
		assertDelete(item5);
		assertDelete(item6);
	}
	
	public void testCheckDatabase()
	{
		Database.theInstance.checkDatabase();
	}

}
