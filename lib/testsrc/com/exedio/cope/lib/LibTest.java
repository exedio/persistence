
package com.exedio.cope.lib;


public class LibTest extends AbstractLibTest
{
	
	public LibTest()
	{}
	
	public void testLib()
	{
		final ItemWithoutAttributes item1 = new ItemWithoutAttributes();
		item1.TYPE.flushPK();
		final ItemWithoutAttributes item2 = new ItemWithoutAttributes();
		assertNotEquals(item1, item2);
	}

}
