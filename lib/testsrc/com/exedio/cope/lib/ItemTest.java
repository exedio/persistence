
package com.exedio.cope.lib;

import java.util.Arrays;

public class ItemTest extends AbstractLibTest
{
	
	public ItemTest()
	{}
	
	/**
	 * Test getType, ID, equals, hashCode etc.
	 */
	public void testItemMethods()
	{
		assertEquals(ItemWithoutAttributes.TYPE, Type.getType(ItemWithoutAttributes.class.getName()));
		assertEquals(ItemWithoutAttributes2.TYPE, Type.getType(ItemWithoutAttributes2.class.getName()));
		assertEquals(toSet(Arrays.asList(types)), toSet(Type.getTypes()));

		final ItemWithoutAttributes item1 = new ItemWithoutAttributes();
		final ItemWithoutAttributes item2 = new ItemWithoutAttributes();
		final ItemWithoutAttributes2 item3 = new ItemWithoutAttributes2();

		assertEquals(ItemWithoutAttributes.TYPE, item1.getType());
		assertEquals(ItemWithoutAttributes.TYPE, item2.getType());
		assertEquals(ItemWithoutAttributes2.TYPE, item3.getType());

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

		final ItemWithoutAttributes item4 = new ItemWithoutAttributes();
		item4.TYPE.flushPK();
		final ItemWithoutAttributes item5 = new ItemWithoutAttributes();
		assertNotEquals(item4, item5);
		
		assertDelete(item1);
		assertDelete(item2);
		assertDelete(item3);
		assertDelete(item4);
		assertDelete(item5);
	}

}
