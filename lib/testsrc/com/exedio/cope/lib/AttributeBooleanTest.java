package com.exedio.cope.lib;


public class AttributeBooleanTest extends AttributeTest 
{
	public void testSomeBoolean()
	{
		assertEquals(item.TYPE, item.someBoolean.getType());
		assertEquals(null, item.getSomeBoolean());
		assertContains(item, item2, toSet(item.TYPE.search(Search.equal(item.someBoolean, null))));
		assertContains(item, item2, toSet(item.TYPE.search(Search.isNull(item.someBoolean))));
		assertContains(toSet(item.TYPE.search(Search.notEqual(item.someBoolean, null))));
		assertContains(toSet(item.TYPE.search(Search.isNotNull(item.someBoolean))));

		item.setSomeBoolean(Boolean.TRUE);
		assertEquals(Boolean.TRUE, item.getSomeBoolean());
		assertContains(item, toSet(item.TYPE.search(Search.equal(item.someBoolean, true))));
		assertContains(item2, toSet(item.TYPE.search(Search.isNull(item.someBoolean))));
		assertContains(item2, toSet(item.TYPE.search(Search.notEqual(item.someBoolean, true))));
		assertContains(item, toSet(item.TYPE.search(Search.isNotNull(item.someBoolean))));

		item.setSomeBoolean(Boolean.FALSE);
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertContains(item, toSet(item.TYPE.search(Search.equal(item.someBoolean, false))));
		assertContains(item2, toSet(item.TYPE.search(Search.isNull(item.someBoolean))));
		assertContains(item2, toSet(item.TYPE.search(Search.notEqual(item.someBoolean, false))));
		assertContains(item, toSet(item.TYPE.search(Search.isNotNull(item.someBoolean))));
		
		item.passivate();
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertContains(item, toSet(item.TYPE.search(Search.equal(item.someBoolean, false))));
		assertContains(item2, toSet(item.TYPE.search(Search.isNull(item.someBoolean))));
		assertContains(item2, toSet(item.TYPE.search(Search.notEqual(item.someBoolean, false))));
		assertContains(item, toSet(item.TYPE.search(Search.isNotNull(item.someBoolean))));

		item.setSomeBoolean(null);
		assertEquals(null, item.getSomeBoolean());
		assertContains(item, item2, toSet(item.TYPE.search(Search.equal(item.someBoolean, null))));
		assertContains(item, item2, toSet(item.TYPE.search(Search.isNull(item.someBoolean))));
		assertContains(toSet(item.TYPE.search(Search.notEqual(item.someBoolean, null))));
		assertContains(toSet(item.TYPE.search(Search.isNotNull(item.someBoolean))));
	}

	public void testSomeNotNullBoolean()
	{
		assertEquals(item.TYPE, item.someNotNullBoolean.getType());
		assertEquals(true, item.getSomeNotNullBoolean());
		assertContains(item, toSet(item.TYPE.search(Search.equal(item.someNotNullBoolean, true))));
		assertContains(toSet(item.TYPE.search(Search.isNull(item.someNotNullBoolean))));
		assertContains(item, toSet(item.TYPE.search(Search.notEqual(item.someNotNullBoolean, false))));
		assertContains(item, item2, toSet(item.TYPE.search(Search.isNotNull(item.someNotNullBoolean))));
		
		item.setSomeNotNullBoolean(false);
		assertEquals(false, item.getSomeNotNullBoolean());
		assertContains(toSet(item.TYPE.search(Search.equal(item.someNotNullBoolean, true))));
		assertContains(toSet(item.TYPE.search(Search.isNull(item.someNotNullBoolean))));
		assertContains(toSet(item.TYPE.search(Search.notEqual(item.someNotNullBoolean, false))));
		assertContains(item, item2, toSet(item.TYPE.search(Search.isNotNull(item.someNotNullBoolean))));
	}
}
