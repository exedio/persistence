package com.exedio.cope.lib;


public class AttributeIntegerTest extends AttributeTest
{
	public void testSomeInteger()
	{
		assertEquals(item.TYPE, item.someInteger.getType());
		assertEquals(null, item.getSomeInteger());
		assertEquals(set(item, item2), toSet(Search.search(item.TYPE, Search.equal(item.someInteger, null))));
		assertEquals(set(item, item2), toSet(Search.search(item.TYPE, Search.isNull(item.someInteger))));
		assertEquals(set(), toSet(Search.search(item.TYPE, Search.notEqual(item.someInteger, null))));
		assertEquals(set(), toSet(Search.search(item.TYPE, Search.isNotNull(item.someInteger))));

		item.setSomeInteger(new Integer(10));
		assertEquals(new Integer(10), item.getSomeInteger());

		item.passivate();
		assertEquals(new Integer(10), item.getSomeInteger());
		assertEquals(
			list(item),
			Search.search(item.TYPE, Search.equal(item.someInteger, 10)));
		assertEquals(
			list(item2),
			Search.search(item.TYPE, Search.notEqual(item.someInteger, 10)));
		assertEquals(list(item2), Search.search(item.TYPE, Search.equal(item.someInteger, null)));
		assertEquals(list(item2), Search.search(item.TYPE, Search.isNull(item.someInteger)));
		assertEquals(list(item), Search.search(item.TYPE, Search.notEqual(item.someInteger, null)));
		assertEquals(list(item), Search.search(item.TYPE, Search.isNotNull(item.someInteger)));

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
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someNotNullInteger, 0))));

		item.setSomeNotNullInteger(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, item.getSomeNotNullInteger());

		item.passivate();
		assertEquals(Integer.MIN_VALUE, item.getSomeNotNullInteger());
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someNotNullInteger, Integer.MIN_VALUE))));

		item.setSomeNotNullInteger(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, item.getSomeNotNullInteger());

		item.passivate();
		assertEquals(Integer.MAX_VALUE, item.getSomeNotNullInteger());
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someNotNullInteger, Integer.MAX_VALUE))));
	}
}
