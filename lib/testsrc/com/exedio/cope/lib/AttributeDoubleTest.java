package com.exedio.cope.lib;


public class AttributeDoubleTest extends AttributeTest
{
	public void testSomeDouble()
	{
		assertEquals(item.TYPE, item.someDouble.getType());
		assertEquals(null, item.getSomeDouble());
		assertContains(item, item2, item.TYPE.search(Search.equal(item.someDouble, null)));
		assertContains(item, item2, item.TYPE.search(Search.isNull(item.someDouble)));
		assertContains(item.TYPE.search(Search.notEqual(item.someDouble, null)));
		assertContains(item.TYPE.search(Search.isNotNull(item.someDouble)));

		item.setSomeDouble(new Double(22.22));
		assertEquals(new Double(22.22), item.getSomeDouble());

		item.passivate();
		assertEquals(new Double(22.22), item.getSomeDouble());
		assertEquals(
			list(item),
			item.TYPE.search(Search.equal(item.someDouble, 22.22)));
		assertEquals(
			list(item2),
			item.TYPE.search(Search.notEqual(item.someDouble, 22.22)));
		assertEquals(list(item2), item.TYPE.search(Search.equal(item.someDouble, null)));
		assertEquals(list(item2), item.TYPE.search(Search.isNull(item.someDouble)));
		assertEquals(list(item), item.TYPE.search(Search.notEqual(item.someDouble, null)));
		assertEquals(list(item), item.TYPE.search(Search.isNotNull(item.someDouble)));

		item.setSomeDouble(null);
		assertEquals(null, item.getSomeDouble());

		item.passivate();
		assertEquals(null, item.getSomeDouble());
	}

	public void testSomeNotNullDouble()
	{
		assertEquals(item.TYPE, item.someNotNullDouble.getType());
		assertEquals(2.2, item.getSomeNotNullDouble(), 0.0);
		item.setSomeNotNullDouble(2.5);
		assertEquals(2.5, item.getSomeNotNullDouble(), 0.0);

		item.setSomeNotNullDouble(0.0);
		assertEquals(0.0, item.getSomeNotNullDouble(), 0.0);

		item.passivate();
		assertEquals(0.0, item.getSomeNotNullDouble(), 0.0);
		assertContains(
			item,
			
				item.TYPE.search(
					Search.equal(item.someNotNullDouble, 0.0)));

		// TODO: test with extreme values
		/*item.setSomeNotNullDouble(Double.MIN_VALUE);
		// TODO: passivate
		assertEquals(Double.MIN_VALUE, item.getSomeNotNullDouble(), 0.0);
		assertContains(
			item,
			Search.search(
				item.TYPE,
				Search.equal(item.someNotNullDouble, Double.MIN_VALUE))));

		item.setSomeNotNullDouble(Double.MAX_VALUE);
		// TODO: passivate
		assertEquals(Double.MAX_VALUE, item.getSomeNotNullDouble(), 0.0);
		assertEquals(
			item,
			Search.search(
				item.TYPE,
				Search.equal(item.someNotNullDouble, Double.MAX_VALUE))));*/
	}
}
