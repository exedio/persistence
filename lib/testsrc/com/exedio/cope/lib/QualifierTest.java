
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

		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key1));
		assertEquals(null, item.getQualifiedEmptyQualifier(key1));
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key2));

		final QualifiedEmptyQualifier qitem1 = new QualifiedEmptyQualifier(item, key1);
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key2));

		qitem1.setQualifiedA("value1A");
		assertEquals("value1A", QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key2));
		
		qitem1.setQualifiedB("value1B");
		assertEquals("value1A", QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals("value1B", QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key2));
		
		final QualifiedEmptyQualifier qitem2 = new QualifiedEmptyQualifier(item, key2);
		assertEquals("value1A", QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals("value1B", QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key2).getQualifiedA());
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key2).getQualifiedB());

		qitem2.setQualifiedB("value2B");
		assertEquals("value1A", QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals("value1B", QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key2).getQualifiedA());
		assertEquals("value2B", QualifiedEmptyQualifier.findByQualifyUnique(item, key2).getQualifiedB());

		qitem1.setQualifiedB(null);
		assertEquals("value1A", QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedA());
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key1).getQualifiedB());
		assertEquals(null, QualifiedEmptyQualifier.findByQualifyUnique(item, key2).getQualifiedA());
		assertEquals("value2B", QualifiedEmptyQualifier.findByQualifyUnique(item, key2).getQualifiedB());

		qitem2.delete();
		qitem1.delete();
	}
	
}
