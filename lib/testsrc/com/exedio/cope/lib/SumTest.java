package com.exedio.cope.lib;


public class SumTest extends DatabaseLibTest
{
	SumItem item;
	SumItem item2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		item = new SumItem(1, 2, 3);
		item2 = new SumItem(3, 4, 5);
	}
	
	public void tearDown() throws Exception
	{
		item2.delete();
		item.delete();
		super.tearDown();
	}
	
	public void testSum()
	{
		assertEquals(1, item.getNum1());
		assertEquals(2, item.getNum2());
		assertEquals(3, item.getNum3());
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.num1, 1))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.num2, 2))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.num3, 3))));

		assertEquals(new Integer(3), item.getSum12());
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum12, 3))));
	}

}
