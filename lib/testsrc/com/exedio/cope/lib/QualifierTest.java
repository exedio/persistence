
package com.exedio.cope.lib;

public class QualifierTest extends DatabaseLibTest
{
	QualifiedItem item;
	ItemWithoutAttributes key1;
	ItemWithoutAttributes key2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		item = new QualifiedItem();
		key1 = new ItemWithoutAttributes();
		key2 = new ItemWithoutAttributes();
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
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key1));
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key2));

		QualifierItem qitem1 = new QualifierItem(item, key1);
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key2));

		qitem1.setQualifiedA("value1A");
		assertEquals("value1A", QualifierItem.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key2));
		
		qitem1.setQualifiedB("value1B");
		assertEquals("value1A", QualifierItem.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals("value1B", QualifierItem.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key2));
		
		QualifierItem qitem2 = new QualifierItem(item, key2);
		assertEquals("value1A", QualifierItem.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals("value1B", QualifierItem.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key2).getQualifiedA());
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key2).getQualifiedB());

		qitem2.setQualifiedB("value2B");
		assertEquals("value1A", QualifierItem.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals("value1B", QualifierItem.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key2).getQualifiedA());
		assertEquals("value2B", QualifierItem.findByQualifyUnique(item, key2).getQualifiedB());

		qitem1.setQualifiedB(null);
		assertEquals("value1A", QualifierItem.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifierItem.findByQualifyUnique(item, key2).getQualifiedA());
		assertEquals("value2B", QualifierItem.findByQualifyUnique(item, key2).getQualifiedB());

		qitem2.delete();
		qitem1.delete();
	}
	
}
