package com.exedio.cope.lib;


public class AttributeBooleanTest extends AttributeTest 
{
	public void testSomeBoolean()
	{
		assertEquals(item.TYPE, item.someBoolean.getType());
		assertEquals(null, item.getSomeBoolean());
		assertEquals(set(item, item2), toSet(item.TYPE.search(Search.equal(item.someBoolean, null))));
		assertEquals(set(item, item2), toSet(item.TYPE.search(Search.isNull(item.someBoolean))));
		assertEquals(set(), toSet(item.TYPE.search(Search.notEqual(item.someBoolean, null))));
		assertEquals(set(), toSet(item.TYPE.search(Search.isNotNull(item.someBoolean))));

		item.setSomeBoolean(Boolean.TRUE);
		assertEquals(Boolean.TRUE, item.getSomeBoolean());
		assertEquals(set(item), toSet(item.TYPE.search(Search.equal(item.someBoolean, true))));
		assertEquals(set(item2), toSet(item.TYPE.search(Search.isNull(item.someBoolean))));
		assertEquals(set(item2), toSet(item.TYPE.search(Search.notEqual(item.someBoolean, true))));
		assertEquals(set(item), toSet(item.TYPE.search(Search.isNotNull(item.someBoolean))));

		item.setSomeBoolean(Boolean.FALSE);
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertEquals(set(item), toSet(item.TYPE.search(Search.equal(item.someBoolean, false))));
		assertEquals(set(item2), toSet(item.TYPE.search(Search.isNull(item.someBoolean))));
		assertEquals(set(item2), toSet(item.TYPE.search(Search.notEqual(item.someBoolean, false))));
		assertEquals(set(item), toSet(item.TYPE.search(Search.isNotNull(item.someBoolean))));
		
		item.passivate();
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertEquals(set(item), toSet(item.TYPE.search(Search.equal(item.someBoolean, false))));
		assertEquals(set(item2), toSet(item.TYPE.search(Search.isNull(item.someBoolean))));
		assertEquals(set(item2), toSet(item.TYPE.search(Search.notEqual(item.someBoolean, false))));
		assertEquals(set(item), toSet(item.TYPE.search(Search.isNotNull(item.someBoolean))));

		item.setSomeBoolean(null);
		assertEquals(null, item.getSomeBoolean());
		assertEquals(set(item, item2), toSet(item.TYPE.search(Search.equal(item.someBoolean, null))));
		assertEquals(set(item, item2), toSet(item.TYPE.search(Search.isNull(item.someBoolean))));
		assertEquals(set(), toSet(item.TYPE.search(Search.notEqual(item.someBoolean, null))));
		assertEquals(set(), toSet(item.TYPE.search(Search.isNotNull(item.someBoolean))));
	}

	public void testSomeNotNullBoolean()
	{
		assertEquals(item.TYPE, item.someNotNullBoolean.getType());
		assertEquals(true, item.getSomeNotNullBoolean());
		assertEquals(set(item), toSet(item.TYPE.search(Search.equal(item.someNotNullBoolean, true))));
		assertEquals(set(), toSet(item.TYPE.search(Search.isNull(item.someNotNullBoolean))));
		assertEquals(set(item), toSet(item.TYPE.search(Search.notEqual(item.someNotNullBoolean, false))));
		assertEquals(set(item, item2), toSet(item.TYPE.search(Search.isNotNull(item.someNotNullBoolean))));
		
		item.setSomeNotNullBoolean(false);
		assertEquals(false, item.getSomeNotNullBoolean());
		assertEquals(set(), toSet(item.TYPE.search(Search.equal(item.someNotNullBoolean, true))));
		assertEquals(set(), toSet(item.TYPE.search(Search.isNull(item.someNotNullBoolean))));
		assertEquals(set(), toSet(item.TYPE.search(Search.notEqual(item.someNotNullBoolean, false))));
		assertEquals(set(item, item2), toSet(item.TYPE.search(Search.isNotNull(item.someNotNullBoolean))));
	}
}
