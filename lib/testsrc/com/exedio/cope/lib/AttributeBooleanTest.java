package com.exedio.cope.lib;


public class AttributeBooleanTest extends AttributeTest 
{
	public void testSomeBoolean()
	{
		assertEquals(item.TYPE, item.someBoolean.getType());
		assertEquals(null, item.getSomeBoolean());
		assertContains(item, item2, item.TYPE.search(Search.equal(item.someBoolean, null)));
		assertContains(item, item2, item.TYPE.search(Search.isNull(item.someBoolean)));
		assertContains(item.TYPE.search(Search.notEqual(item.someBoolean, null)));
		assertContains(item.TYPE.search(Search.isNotNull(item.someBoolean)));

		item.setSomeBoolean(Boolean.TRUE);
		assertEquals(Boolean.TRUE, item.getSomeBoolean());
		assertContains(item, item.TYPE.search(Search.equal(item.someBoolean, true)));
		assertContains(item2, item.TYPE.search(Search.isNull(item.someBoolean)));
		assertContains(item2, item.TYPE.search(Search.notEqual(item.someBoolean, true)));
		assertContains(item, item.TYPE.search(Search.isNotNull(item.someBoolean)));

		item.setSomeBoolean(Boolean.FALSE);
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertContains(item, item.TYPE.search(Search.equal(item.someBoolean, false)));
		assertContains(item2, item.TYPE.search(Search.isNull(item.someBoolean)));
		assertContains(item2, item.TYPE.search(Search.notEqual(item.someBoolean, false)));
		assertContains(item, item.TYPE.search(Search.isNotNull(item.someBoolean)));
		
		item.passivate();
		assertEquals(Boolean.FALSE, item.getSomeBoolean());
		assertContains(item, item.TYPE.search(Search.equal(item.someBoolean, false)));
		assertContains(item2, item.TYPE.search(Search.isNull(item.someBoolean)));
		assertContains(item2, item.TYPE.search(Search.notEqual(item.someBoolean, false)));
		assertContains(item, item.TYPE.search(Search.isNotNull(item.someBoolean)));

		item.setSomeBoolean(null);
		assertEquals(null, item.getSomeBoolean());
		assertContains(item, item2, item.TYPE.search(Search.equal(item.someBoolean, null)));
		assertContains(item, item2, item.TYPE.search(Search.isNull(item.someBoolean)));
		assertContains(item.TYPE.search(Search.notEqual(item.someBoolean, null)));
		assertContains(item.TYPE.search(Search.isNotNull(item.someBoolean)));
	}

	public void testSomeNotNullBoolean()
	{
		assertEquals(item.TYPE, item.someNotNullBoolean.getType());
		assertEquals(true, item.getSomeNotNullBoolean());
		assertContains(item, item.TYPE.search(Search.equal(item.someNotNullBoolean, true)));
		assertContains(item.TYPE.search(Search.isNull(item.someNotNullBoolean)));
		assertContains(item, item.TYPE.search(Search.notEqual(item.someNotNullBoolean, false)));
		assertContains(item, item2, item.TYPE.search(Search.isNotNull(item.someNotNullBoolean)));
		
		item.setSomeNotNullBoolean(false);
		assertEquals(false, item.getSomeNotNullBoolean());
		assertContains(item.TYPE.search(Search.equal(item.someNotNullBoolean, true)));
		assertContains(item.TYPE.search(Search.isNull(item.someNotNullBoolean)));
		assertContains(item.TYPE.search(Search.notEqual(item.someNotNullBoolean, false)));
		assertContains(item, item2, item.TYPE.search(Search.isNotNull(item.someNotNullBoolean)));
	}
}
