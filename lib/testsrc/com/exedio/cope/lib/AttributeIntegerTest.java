package com.exedio.cope.lib;


public class AttributeIntegerTest extends AttributeTest
{
	public void testSomeInteger()
	{
		assertEquals(item.TYPE, item.someInteger.getType());
		assertEquals(null, item.getSomeInteger());
		assertContains(item, item2, toSet(item.TYPE.search(Search.equal(item.someInteger, null))));
		assertContains(item, item2, toSet(item.TYPE.search(Search.isNull(item.someInteger))));
		assertContains(toSet(item.TYPE.search(Search.notEqual(item.someInteger, null))));
		assertContains(toSet(item.TYPE.search(Search.isNotNull(item.someInteger))));

		item.setSomeInteger(new Integer(10));
		assertEquals(new Integer(10), item.getSomeInteger());

		item.passivate();
		assertEquals(new Integer(10), item.getSomeInteger());
		assertEquals(
			list(item),
			item.TYPE.search(Search.equal(item.someInteger, 10)));
		assertEquals(
			list(item2),
			item.TYPE.search(Search.notEqual(item.someInteger, 10)));
		assertEquals(list(item2), item.TYPE.search(Search.equal(item.someInteger, null)));
		assertEquals(list(item2), item.TYPE.search(Search.isNull(item.someInteger)));
		assertEquals(list(item), item.TYPE.search(Search.notEqual(item.someInteger, null)));
		assertEquals(list(item), item.TYPE.search(Search.isNotNull(item.someInteger)));

		item.setSomeInteger(null);
		assertEquals(null, item.getSomeInteger());
		
		item.passivate();
		assertEquals(null, item.getSomeInteger());
	}

	public void testSomeNotNullInteger()
	{
		assertEquals(item.TYPE, item.someNotNullInteger.getType());
		assertEquals(5, item.getSomeNotNullInteger());
		item.setSomeNotNullInteger(20);
		assertEquals(20, item.getSomeNotNullInteger());

		item.setSomeNotNullInteger(0);
		assertEquals(0, item.getSomeNotNullInteger());

		item.passivate();
		assertEquals(0, item.getSomeNotNullInteger());
		assertContains(
			item,
			toSet(
				item.TYPE.search(
					Search.equal(item.someNotNullInteger, 0))));

		item.setSomeNotNullInteger(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, item.getSomeNotNullInteger());

		item.passivate();
		assertEquals(Integer.MIN_VALUE, item.getSomeNotNullInteger());
		assertContains(
			item,
			toSet(
				item.TYPE.search(
					Search.equal(item.someNotNullInteger, Integer.MIN_VALUE))));

		item.setSomeNotNullInteger(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, item.getSomeNotNullInteger());

		item.passivate();
		assertEquals(Integer.MAX_VALUE, item.getSomeNotNullInteger());
		assertContains(
			item,
			toSet(
				item.TYPE.search(
					Search.equal(item.someNotNullInteger, Integer.MAX_VALUE))));
	}
}
