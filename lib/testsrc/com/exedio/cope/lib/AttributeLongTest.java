package com.exedio.cope.lib;


public class AttributeLongTest extends AttributeTest
{
	public void testSomeLong()
	{
		assertEquals(item.TYPE, item.someLong.getType());
		assertEquals(null, item.getSomeLong());
		assertEquals(set(item, item2), toSet(Search.search(item.TYPE, Search.equal(item.someLong, null))));
		assertEquals(set(item, item2), toSet(Search.search(item.TYPE, Search.isNull(item.someLong))));
		assertEquals(set(), toSet(Search.search(item.TYPE, Search.notEqual(item.someLong, null))));
		assertEquals(set(), toSet(Search.search(item.TYPE, Search.isNotNull(item.someLong))));

		item.setSomeLong(new Long(11));
		assertEquals(new Long(11), item.getSomeLong());

		item.passivate();
		assertEquals(new Long(11), item.getSomeLong());
		assertEquals(
			list(item),
			Search.search(item.TYPE, Search.equal(item.someLong, 11)));
		assertEquals(
			list(item2),
			Search.search(item.TYPE, Search.notEqual(item.someLong, 11)));

		assertEquals(list(item2), Search.search(item.TYPE, Search.equal(item.someLong, null)));
		assertEquals(list(item2), Search.search(item.TYPE, Search.isNull(item.someLong)));
		assertEquals(list(item), Search.search(item.TYPE, Search.notEqual(item.someLong, null)));
		assertEquals(list(item), Search.search(item.TYPE, Search.isNotNull(item.someLong)));

		item.setSomeLong(null);
		assertEquals(null, item.getSomeLong());
		
		item.passivate();
		assertEquals(null, item.getSomeLong());
	}

	public void testSomeNotNullLong()
	{
		assertEquals(item.TYPE, item.someNotNullLong.getType());
		assertEquals(6l, item.getSomeNotNullLong());
		item.setSomeNotNullLong(21l);
		assertEquals(21l, item.getSomeNotNullLong());

		item.setSomeNotNullLong(0l);
		assertEquals(0l, item.getSomeNotNullLong());

		item.passivate();
		assertEquals(0l, item.getSomeNotNullLong());
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someNotNullLong, 0l))));

		item.setSomeNotNullLong(Long.MIN_VALUE);
		assertEquals(Long.MIN_VALUE, item.getSomeNotNullLong());

		item.passivate();
		assertEquals(Long.MIN_VALUE, item.getSomeNotNullLong());
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someNotNullLong, Long.MIN_VALUE))));

		item.setSomeNotNullLong(Long.MAX_VALUE);
		assertEquals(Long.MAX_VALUE, item.getSomeNotNullLong());

		item.passivate();
		assertEquals(Long.MAX_VALUE, item.getSomeNotNullLong());
		assertEquals(
			set(item),
			toSet(
				Search.search(
					item.TYPE,
					Search.equal(item.someNotNullLong, Long.MAX_VALUE))));
	}
}
