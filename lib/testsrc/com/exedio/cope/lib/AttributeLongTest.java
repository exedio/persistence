package com.exedio.cope.lib;


public class AttributeLongTest extends AttributeTest
{
	public void testSomeLong()
	{
		assertEquals(item.TYPE, item.someLong.getType());
		assertEquals(null, item.getSomeLong());
		assertContains(item, item2, item.TYPE.search(Cope.equal(item.someLong, null)));
		assertContains(item, item2, item.TYPE.search(Cope.isNull(item.someLong)));
		assertContains(item.TYPE.search(Cope.notEqual(item.someLong, null)));
		assertContains(item.TYPE.search(Cope.isNotNull(item.someLong)));

		item.setSomeLong(new Long(11));
		assertEquals(new Long(11), item.getSomeLong());

		item.passivate();
		assertEquals(new Long(11), item.getSomeLong());
		assertEquals(
			list(item),
			item.TYPE.search(Cope.equal(item.someLong, 11)));
		assertEquals(
			list(item2),
			item.TYPE.search(Cope.notEqual(item.someLong, 11)));

		assertEquals(list(item2), item.TYPE.search(Cope.equal(item.someLong, null)));
		assertEquals(list(item2), item.TYPE.search(Cope.isNull(item.someLong)));
		assertEquals(list(item), item.TYPE.search(Cope.notEqual(item.someLong, null)));
		assertEquals(list(item), item.TYPE.search(Cope.isNotNull(item.someLong)));

		assertContains(new Long(11), null, search(item.someLong));
		assertContains(new Long(11), search(item.someLong, Cope.equal(item.someLong, new Long(11))));

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
		assertContains(item,
			item.TYPE.search(Cope.equal(item.someNotNullLong, 0l)));

		item.setSomeNotNullLong(Long.MIN_VALUE);
		assertEquals(Long.MIN_VALUE, item.getSomeNotNullLong());

		item.passivate();
		assertEquals(Long.MIN_VALUE, item.getSomeNotNullLong());
		assertContains(item,
			item.TYPE.search(Cope.equal(item.someNotNullLong, Long.MIN_VALUE)));

		item.setSomeNotNullLong(Long.MAX_VALUE);
		assertEquals(Long.MAX_VALUE, item.getSomeNotNullLong());

		item.passivate();
		assertEquals(Long.MAX_VALUE, item.getSomeNotNullLong());
		assertContains(item,
			item.TYPE.search(Cope.equal(item.someNotNullLong, Long.MAX_VALUE)));
	}
}
