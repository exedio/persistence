
package com.exedio.cope.lib;

public class QualifierTest extends DatabaseLibTest
{
	QualifiedItem item;
	EmptyItem key1;
	EmptyItem key2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		item = new QualifiedItem();
		key1 = new EmptyItem();
		key2 = new EmptyItem();
	}
	
	public void tearDown() throws Exception
	{
		item.delete();
		key2.delete();
		key1.delete();
		super.tearDown();
	}
	
	public void testQualified()
		throws UniqueViolationException, NotNullViolationException, IntegrityViolationException
	{
		assertEquals(QualifiedItem.qualifier.getParent(), QualifiedEmptyQualifier.parent);
		assertEquals(QualifiedItem.qualifier.getKey(), QualifiedEmptyQualifier.key);
		assertEquals(QualifiedItem.qualifier.getQualifyUnique(), QualifiedEmptyQualifier.qualifyUnique);

		assertEquals(null, item.getQualifier(key1));
		assertEquals(null, item.getQualifiedA(key1));
		assertEquals(null, item.getQualifiedB(key1));
		assertEquals(null, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals(null, item.getQualifiedB(key2));

		final QualifiedEmptyQualifier qitem1 = new QualifiedEmptyQualifier(item, key1);
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals(null, item.getQualifiedA(key1));
		assertEquals(null, item.getQualifiedB(key1));
		assertEquals(null, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals(null, item.getQualifiedB(key2));

		qitem1.setQualifiedA("value1A");
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals("value1A", item.getQualifiedA(key1));
		assertEquals(null, item.getQualifiedB(key1));
		assertEquals(null, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals(null, item.getQualifiedB(key2));
		
		qitem1.setQualifiedB("value1B");
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals("value1A", item.getQualifiedA(key1));
		assertEquals("value1B", item.getQualifiedB(key1));
		assertEquals(null, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals(null, item.getQualifiedB(key2));
		
		final QualifiedEmptyQualifier qitem2 = new QualifiedEmptyQualifier(item, key2);
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals("value1A", item.getQualifiedA(key1));
		assertEquals("value1B", item.getQualifiedB(key1));
		assertEquals(qitem2, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals(null, item.getQualifiedB(key2));

		qitem2.setQualifiedB("value2B");
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals("value1A", item.getQualifiedA(key1));
		assertEquals("value1B", item.getQualifiedB(key1));
		assertEquals(qitem2, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals("value2B", item.getQualifiedB(key2));

		qitem1.setQualifiedB(null);
		assertEquals(qitem1, item.getQualifier(key1));
		assertEquals("value1A", item.getQualifiedA(key1));
		assertEquals(null, item.getQualifiedB(key1));
		assertEquals(qitem2, item.getQualifier(key2));
		assertEquals(null, item.getQualifiedA(key2));
		assertEquals("value2B", item.getQualifiedB(key2));

		qitem2.delete();
		qitem1.delete();
	}
	
}
