package com.exedio.cope.lib;


public class SumTest extends DatabaseLibTest
{
	SumItem item;
	SumItem item2;
	
	final static Integer i1 = new Integer(1);
	final static Integer i2 = new Integer(2);
	final static Integer i3 = new Integer(3);
	final static Integer i4 = new Integer(4);
	final static Integer i5 = new Integer(5);
	final static Integer i6 = new Integer(6);
	final static Integer i7 = new Integer(7);
	final static Integer i8 = new Integer(8);
	
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
		// test model
		assertEquals(item.TYPE, item.sum12.getType());
		assertEquals(item.TYPE, item.sum13.getType());
		assertEquals(item.TYPE, item.sum23.getType());
		assertEquals(item.TYPE, item.sum123.getType());
		assertEquals(item.TYPE, item.sum12a3.getType());
		assertEquals("sum12", item.sum12.getName());
		assertEquals("sum13", item.sum13.getName());
		assertEquals("sum23", item.sum23.getName());
		assertEquals("sum123", item.sum123.getName());
		assertEquals("sum12a3", item.sum12a3.getName());
		assertEquals(list(item.num1, item.num2), item.sum12.getSources());
		assertEquals(list(item.num1, item.num3), item.sum13.getSources());
		assertEquals(list(item.num2, item.num3), item.sum23.getSources());
		assertEquals(list(item.num1, item.num2, item.num3), item.sum123.getSources());
		assertEquals(list(item.sum12, item.num3), item.sum12a3.getSources());
		assertUnmodifiable(item.sum12.getSources());
		assertUnmodifiable(item.sum13.getSources());
		assertUnmodifiable(item.sum23.getSources());
		assertUnmodifiable(item.sum123.getSources());
		assertUnmodifiable(item.sum12a3.getSources());

		// test normal operation
		assertEquals(i1, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i3, item.getNum3());
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.num1, 1))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.num2, 2))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.num3, 3))));

		assertEquals(i3, item.getSum12());
		assertEquals(i4, item.getSum13());
		assertEquals(i5, item.getSum23());
		assertEquals(i6, item.getSum123());
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum12, 3))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum13, 4))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum23, 5))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum123, 6))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum12a3, 6))));
		
		// test null propagation
		item.setNum1(null);

		assertEquals(null, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i3, item.getNum3());
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.num1, null))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.num2, 2))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.num3, 3))));

		assertEquals(null, item.getSum12());
		assertEquals(null, item.getSum13());
		assertEquals(i5, item.getSum23());
		assertEquals(null, item.getSum123());
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum12, null))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum13, null))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum23, 5))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum123, null))));
		assertEquals(set(item), toSet(Search.search(item.TYPE, Search.equal(item.sum12a3, null))));
	}

}
